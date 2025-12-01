package controllers;

import Model.People.Admin;
import Model.People.Lecturer;
import Model.People.Student;
import Model.Timetable.ScheduledSession;
import Model.Timetable.TimetableService;
import Model.Room.Room;
import Model.Academic.Module;
import Model.Timetable.Timeslot;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

/**
 * The TimetableController acts as the main controller for user actions.
 * It handles login, timetable queries, conflict checks and admin scheduling,
 */
public class TimetableController {

    private final TimetableService service;
    private final DataManager datamanager;

    /**
     * Creates a TimetableController with a timetable service and a data manager.
     *
     * @param service      the timetable service handling conflict logic
     * @param datamanager  the data manager storing loaded system data
     */
    public TimetableController(TimetableService service, DataManager datamanager) {
        this.service = service;
        this.datamanager = datamanager;
    }

    /**
     * Attempts to log a user in by checking their email and password
     * against the stored student, lecturer and admin accounts.
     *
     * @param email     the email entered
     * @param password  the password entered
     * @return the matching user object, or null if the login fails
     */
    public Object login(String email, String password) {
        for (Student s : datamanager.students) {
            if (s.getEmail().equals(email) && s.getPassword().equals(password)) {
                return s;
            }
        }

        for (Lecturer l : datamanager.lecturers) {
            if (l.getEmail().equals(email) && l.getPassword().equals(password)) {
                return l;
            }
        }

        for (Admin a : datamanager.admins) {
            if (a.getEmail().equals(email) && a.getPassword().equals(password)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Gets the timetable for a specific student for a given semester.
     * Timetable entries are filtered by year, semester and group.
     *
     * @param s              the student requesting the timetable
     * @param targetSemester the semester to search
     * @return a formatted timetable string or a 'not found' message
     */
    public String getTimetableForStudent(Student s, int targetSemester) {
        StringBuilder sb = new StringBuilder();

        int studentYear = s.getYear();
        String studentGroup = s.getGroupId();

        List<ScheduledSession> matches = new ArrayList<>();

        for (ScheduledSession session : datamanager.sessions) {
            Module m = session.getModule();
            if (m == null) continue;

            boolean sameYear = (m.getYear() == studentYear);
            boolean sameSemester = (m.getSemester() == targetSemester);
            boolean groupMatches =
                    session.getGroupId().equalsIgnoreCase("ALL") ||
                            session.getGroupId().equalsIgnoreCase(studentGroup);

            if (sameYear && sameSemester && groupMatches) {
                matches.add(session);
            }
        }

        if (matches.isEmpty()) {
            return "No sessions found.";
        }

        matches.sort(Comparator
                .comparingInt((ScheduledSession sess) -> dayOrder(sess.getTimeslot().getDay()))
                .thenComparingInt(sess -> sess.getTimeslot().getStartHour()));

        for (ScheduledSession sess : matches) {
            sb.append(sess).append("\n");
        }
        return sb.toString();
    }

    /**
     * Retrieves all sessions taught by a specific lecturer.
     *
     * @param l the lecturer
     * @return the lecturer's timetable or a 'not found' message
     */
    public String getTimetableForLecturer(Lecturer l) {
        StringBuilder sb = new StringBuilder();

        for (ScheduledSession session : datamanager.sessions) {
            if (session.getLecturer().equals(l)) {
                sb.append(session).append("\n");
            }
        }
        return sb.length() == 0 ? "No sessions found." : sb.toString();
    }

    /**
     * Retrieves and prints every scheduled session in the system.
     *
     * @return a full timetable or a 'not found' message
     */
    public String getFullTimetable() {
        StringBuilder sb = new StringBuilder();

        for (ScheduledSession s : datamanager.sessions) {
            sb.append(s).append("\n");
        }
        return sb.length() == 0 ? "No sessions found." : sb.toString();
    }

    /**
     * Adds a new session if it does not conflict with existing sessions.
     *
     * @param newSession the session to add
     * @return true if added successfully, false if a conflict exists
     */
    public boolean addSession(ScheduledSession newSession) {
        for (ScheduledSession existing : datamanager.sessions) {
            if (existing.sameTimeWith(newSession)) {
                System.out.println("Unable to add session : Interferes with " + existing);
                return false;
            }
        }
        datamanager.sessions.add(newSession);
        System.out.println("Session added: " + newSession);
        return true;
    }

    /**
     * Finds and returns all room booking conflicts across the timetable.
     *
     * @return a list of room conflict descriptions
     */
    public List<String> findRoomConflicts() {
        List<String> conflicts = new ArrayList<>();
        List<ScheduledSession> sessions = datamanager.sessions;

        for (int i = 0; i < sessions.size(); i++) {
            for (int j = i + 1; j < sessions.size(); j++) {
                ScheduledSession a = sessions.get(i);
                ScheduledSession b = sessions.get(j);

                if (a.getRoom() == null || b.getRoom() == null) continue;

                boolean sameRoom = a.getRoom().getRoomId()
                        .equalsIgnoreCase(b.getRoom().getRoomId());
                boolean overlap = a.getTimeslot().overlaps(b.getTimeslot());

                if (sameRoom && overlap) {
                    conflicts.add("ROOM CONFLICT: " + a + " <--> " + b);
                }
            }
        }
        return conflicts;
    }

    /**
     * Returns the timetable for a programme year and semester.
     *
     * @param programmeId the programme ID or "ALL"
     * @param year        the academic year
     * @param semester    the semester number
     * @return the matching sessions 
     */
    public String getTimetableForCourseYear(String programmeId, int year, int semester) {
        StringBuilder sb = new StringBuilder();

        for (ScheduledSession session : datamanager.sessions) {
            Module m = session.getModule();
            if (m == null) continue;

            boolean sameProgramme = programmeId.equalsIgnoreCase("ALL")
                    || m.getProgrammeId().equalsIgnoreCase(programmeId);
            boolean sameYear = m.getYear() == year;
            boolean sameSemester = m.getSemester() == semester;

            if (sameProgramme && sameYear && sameSemester) {
                sb.append(session).append("\n");
            }
        }
        return sb.length() == 0 ? "No sessions found." : sb.toString();
    }

    /**
     * Returns all scheduled sessions for a specific module code.
     *
     * @param moduleCode the module code
     * @return the timetable for the module
     */
    public String getTimetableForModule(String moduleCode) {
        StringBuilder sb = new StringBuilder();

        for (ScheduledSession session : datamanager.sessions) {
            Module m = session.getModule();
            if (m != null && m.getModuleCode().equalsIgnoreCase(moduleCode)) {
                sb.append(session).append("\n");
            }
        }
        return sb.length() == 0 ?
                "No sessions found for module " + moduleCode : sb.toString();
    }

    /**
     * Returns the sessions scheduled in a specific room.
     *
     * @param roomId the room ID
     * @return the timetable for the room
     */
    public String getTimetableForRoom(String roomId) {
        StringBuilder sb = new StringBuilder();

        for (ScheduledSession session : datamanager.sessions) {
            Room room = session.getRoom();
            if (room != null && room.getRoomId().equalsIgnoreCase(roomId)) {
                sb.append(session).append("\n");
            }
        }
        return sb.length() == 0 ?
                "No sessions found for room " + roomId : sb.toString();
    }

    /**
     * Method for admins only for adding a session manually.
     * Confirms module, room, lecturer and checks for timetable conflicts.
     *
     * @param moduleCode   the module code
     * @param day          the day of the week
     * @param startHour    session start hour
     * @param endHour      session end hour
     * @param roomId       room ID
     * @param lecturerId   lecturer ID
     * @param groupId      student group
     * @return true if the session is added, false otherwise
     */
    public boolean addSessionAdmin(String moduleCode, String day, int startHour,
                                   int endHour, String roomId, String lecturerId,
                                   String groupId) {

        Module module = datamanager.findModule(moduleCode);
        Room room = datamanager.findRoom(roomId);
        Lecturer lecturer = datamanager.findLecturer(lecturerId);

        if (module == null || room == null || lecturer == null) {
            System.out.println("Invalid module or room or lecturer");
            return false;
        }

        int duration = endHour - startHour;
        if (duration <= 0) {
            System.out.println("Invalid duration");
            return false;
        }

        Timeslot slot = new Timeslot(day.toUpperCase(), startHour, duration);
        ScheduledSession newSession = new ScheduledSession(module, lecturer, room, slot, groupId);

        List<String> conflicts = service.addSession(newSession);
        if (!conflicts.isEmpty()) {
            System.out.println("Unable to add session.");
            for (String conflict : conflicts) {
                System.out.println(" - " + conflict);
            }
            return false;
        }

        datamanager.sessions.add(newSession);
        System.out.println("Session added: " + newSession);
        return true;
    }

    /** Converts day names into numbers. */
    private int dayOrder(String day) {
        if (day == null) return 99;
        return switch (day.toUpperCase()) {
            case "MON" -> 1;
            case "TUE" -> 2;
            case "WED" -> 3;
            case "THU" -> 4;
            case "FRI" -> 5;
            default -> 99;
        };
    }
}
