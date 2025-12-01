package controllers;

import Model.People.Student;
import Model.People.Lecturer;
import Model.People.Admin;
import Model.Academic.Module;
import Model.Room.Room;
import Model.Timetable.ScheduledSession;
import Model.Timetable.Timeslot;
import Model.Academic.Programme;

import java.util.*;

/**
 * The DataManager class is responsible for loading data from CSV files
 * and converting it into the model objects used throughout the system.
 * It also provides simple lookup utilities for modules, rooms and lecturers.
 */
public final class DataManager {

    public List<Student> students = new ArrayList<>();
    public List<Lecturer> lecturers = new ArrayList<>();
    public List<Room> rooms = new ArrayList<>();
    public List<Module> modules = new ArrayList<>();
    public List<Programme> programmes = new ArrayList<>();
    public List<ScheduledSession> sessions = new ArrayList<>();
    public List<Admin> admins = new ArrayList<>();

    /**
     * Loads student data from a CSV file and creates Student objects.
     *
     * @param file the path to the students CSV file
     */
    public void loadStudents(String file) {
        List<String[]> data = CSVReader.readCSV(file);

        for (String[] row : data) {
            if (row[0].equalsIgnoreCase("studentId")) continue;

            String id = row[0];
            String name = row[1];
            String email = row[2];
            String password = row[3];
            String programme = row[4];
            int year = Integer.parseInt(row[5]);
            String groupId = row[6];

            students.add(new Student(id, name, email, password, programme, year, groupId));
        }
    }

    /**
     * Loads lecturer data from a CSV file and creates Lecturer objects.
     *
     * @param file the path to the lecturers CSV file
     */
    public void loadLecturers(String file) {
        List<String[]> data = CSVReader.readCSV(file);

        for (String[] row : data) {
            if (row[0].equalsIgnoreCase("lecturerId")) continue;

            lecturers.add(new Lecturer(row[0], row[1], row[2], row[3], row[4]));
        }
    }

    /**
     * Loads room data from a CSV file (classrooms and labs).
     *
     * @param file the path to the rooms CSV file
     */
    public void loadRooms(String file) {
        List<String[]> data = CSVReader.readCSV(file);

        for (String[] row : data) {
            if (row[0].equalsIgnoreCase("roomId")) continue;

            String id = row[0];
            String type = row[1];
            int capacity = Integer.parseInt(row[2]);
            String building = row[3];

            rooms.add(new Room(id, type, capacity, building));
        }
    }

    /**
     * Loads module data (codes, names, programme ID, hours, etc.).
     *
     * @param file the path to the modules CSV file
     */
    public void loadModules(String file) {
        List<String[]> data = CSVReader.readCSV(file);

        for (String[] row : data) {
            if (row[0].equalsIgnoreCase("moduleCode")) continue;

            String code = row[0];
            String name = row[1];
            int year = Integer.parseInt(row[2]);
            int semester = Integer.parseInt(row[3]);
            String programmeId = row[4];
            int lec = Integer.parseInt(row[5]);
            int lab = Integer.parseInt(row[6]);
            int tut = Integer.parseInt(row[7]);

            modules.add(new Module(name, code, programmeId, year, semester, lec, lab, tut));
        }
    }

    /**
     * Loads programme IDs and names.
     *
     * @param file the path to the programmes CSV file
     */
    public void loadProgrammes(String file) {
        List<String[]> data = CSVReader.readCSV(file);

        for (String[] row : data) {
            if (row[0].equalsIgnoreCase("programmeId")) continue;

            programmes.add(new Programme(row[0], row[1]));
        }
    }

    /**
     * Loads scheduled sessions from a CSV file and reconstructs
     * module, lecturer, room and timeslot links.
     *
     * @param file the path to the sessions CSV file
     * @return a list of ScheduledSession objects
     */
    public List<ScheduledSession> loadSessions(String file) {
        List<ScheduledSession> loaded = new ArrayList<>();
        List<String[]> data = CSVReader.readCSV(file);

        for (String[] row : data) {
            if (row[0].equalsIgnoreCase("sessionId")) continue;

            Module module = findModule(row[1]);
            int start = Integer.parseInt(row[3]);
            int end = Integer.parseInt(row[4]);
            int duration = end - start;
            Timeslot timeslot = new Timeslot(row[2], start, duration);
            Room room = findRoom(row[5]);
            Lecturer lecturer = findLecturer(row[6]);
            String groupId = "ALL";

            loaded.add(new ScheduledSession(module, lecturer, room, timeslot, groupId));
        }
        return loaded;
    }

    /**
     * Loads admin users from a CSV file.
     *
     * @param file the path to the admins CSV file
     */
    public void loadAdmins(String file) {
        List<String[]> data = CSVReader.readCSV(file);

        for (String[] row : data) {
            if (row[0].equalsIgnoreCase("adminId")) continue;

            admins.add(new Admin(row[0], row[1], row[2], row[3]));
        }
    }

    /**
     * Finds a module by its module code.
     *
     * @param code the module code
     * @return the matching Module or null if not found
     */
    public Module findModule(String code) {
        for (Module m : modules)
            if (m.getModuleCode().equals(code))
                return m;
        return null;
    }

    /**
     * Finds a room by its ID.
     *
     * @param id the room ID
     * @return the matching Room or null if not found
     */
    public Room findRoom(String id) {
        for (Room r : rooms)
            if (r.getRoomId().equals(id))
                return r;
        return null;
    }

    /**
     * Finds a lecturer by their lecturer ID.
     *
     * @param id the lecturer ID
     * @return the matching Lecturer or null if not found
     */
    public Lecturer findLecturer(String id) {
        for (Lecturer l : lecturers)
            if (l.getLecturerId().equals(id))
                return l;
        return null;
    }

    public void saveSessions(String file) {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"sessionId", "moduleCode", "day", "start", "end", "roomId", "lecturerId", "groupId"});

        int id = 1;
        for (ScheduledSession s : sessions) {
            String moduleCode = (s.getModule() != null) ? s.getModule().getModuleCode() : "";
            String day = (s.getTimeslot() != null) ? s.getTimeslot().getDay() : "";
            int start = (s.getTimeslot() != null) ? s.getTimeslot().getStartHour() : 0;
            int end = (s.getTimeslot() != null) ? s.getTimeslot().getStartHour() + s.getTimeslot().getDuration() : start;
            String roomId = (s.getRoom() != null) ? s.getRoom().getRoomId() : "";
            String lecturerId = (s.getLecturer() != null) ? s.getLecturer().getLecturerId() : "";
            String groupId = (s.getGroupId() != null) ? s.getGroupId() : "";

            rows.add(new String[]{
                    String.valueOf(id++),
                    moduleCode,
                    day,
                    String.valueOf(start),
                    String.valueOf(end),
                    roomId,
                    lecturerId,
                    groupId
            });
        }
        CSVWriter.writeCSV(file, rows);
    }

    public void saveStudents(String file) {
    List<String[]> rows = new ArrayList<>();
    rows.add(new String[]{"studentId","name","email","password","programme","year","groupId"});
    for (Student s : students) {
        rows.add(new String[]{
            s.getId(), s.getName(), s.getEmail(), s.getPassword(),
            s.getProgrammeID(), String.valueOf(s.getYear()), s.getGroupId()
        });
    }
        CSVWriter.writeCSV(file, rows);
    }

    public void saveLecturers(String file) {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"lecturerId","name","email","password","department"});
        for (Lecturer l : lecturers) {
            rows.add(new String[]{l.getLecturerId(), l.getName(), l.getEmail(), l.getPassword(), l.getDepartment()});
        }
        CSVWriter.writeCSV(file, rows);
    }

    public void saveAdmins(String file) {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"adminId","name","email","password"});
        for (Admin a : admins) {
            rows.add(new String[]{a.getAdminId(), a.getName(), a.getEmail(), a.getPassword()});
        }
        CSVWriter.writeCSV(file, rows);
    }

}
