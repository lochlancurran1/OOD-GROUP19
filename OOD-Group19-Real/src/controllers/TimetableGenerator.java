package controllers;

import Model.Academic.Module;
import Model.People.Lecturer;
import Model.Room.Room;
import Model.Timetable.ScheduledSession;
import Model.Timetable.Timeslot;
import Model.Timetable.TimetableService;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * Builds an automatic timetable by placing module lecture, lab and tutorial hours into 
 * available room/time slots and then writes the result to a CSV fil.
 */
public class TimetableGenerator {

    private static final String[] DAYS = {"MON", "TUE", "WED", "THU", "FRI"};
    private static final int START_HOUR = 9;
    private static final int END_HOUR = 18; // last start is 17

    private final DataManager data;
    private final TimetableService service;
    private final List<ScheduledSession> generated = new ArrayList<>();
    private final Random random = new Random();
    
    /**
     * Creates a timetable generator using the data and timetable service
     * 
     * @param data the data storage for modules, lecturers, rooms etc
     * @param service the timetable service which checks for conflicts
     */
    public TimetableGenerator(DataManager data, TimetableService service) {
        this.data = data;
        this.service = service;
    }

    public void generateAndLog(String outputCsvPath) {
        generated.clear();
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"sessionId", "moduleCode", "day", "start", "end", "roomId", "lecturerId", "groupId"});

        int nextId = 1;

        for (Module module : data.modules) {
            Lecturer lecturer = pickLecturerForModule(module);
            if (lecturer == null) {
                System.out.println("No lecturer for " + module.getModuleCode());
                continue;
            }

            nextId = scheduleHours(rows, nextId, module, lecturer, module.getLecHours(), false, "ALL");

            if (module.getLabHours() > 0) {
                nextId = scheduleHours(rows, nextId, module, lecturer, module.getLabHours(), true, "G1");
                nextId = scheduleHours(rows, nextId, module, lecturer, module.getLabHours(), true, "G2");
            }

            if (module.getTutHours() > 0) {
                nextId = scheduleHours(rows, nextId, module, lecturer, module.getTutHours(), false, "G1");
                nextId = scheduleHours(rows, nextId, module, lecturer, module.getTutHours(), false, "G2");
            }
        }

        service.loadSessions(generated);
        data.sessions.clear();
        data.sessions.addAll(generated);

        CSVWriter.writeCSV(outputCsvPath, rows);
        System.out.println("Generated " + generated.size() + " sessions to " + outputCsvPath);
    }

    private Lecturer pickLecturerForModule(Module m) {
        if (data.lecturers.isEmpty()) return null;
        int idx = Math.abs(m.getModuleCode().hashCode()) % data.lecturers.size();
        return data.lecturers.get(idx);
    }

    private int scheduleHours(List<String[]> rows,
                              int nextId,
                              Module module,
                              Lecturer lecturer,
                              int hoursNeeded,
                              boolean lab,
                              String groupId) {

        int remaining = hoursNeeded;

        while (remaining > 0) {
            ScheduledSession session = findFreeSession(module, lecturer, lab, groupId);
            if (session == null) {
                System.out.println("Could not place " + module.getModuleCode()
                        + " (" + (lab ? "lab" : "class") + ", group " + groupId + ")");
                break;
            }

            generated.add(session);
            Timeslot t = session.getTimeslot();
            int endHour = t.getStartHour() + t.getDuration();

            rows.add(new String[]{
                    String.valueOf(nextId++),
                    module.getModuleCode(),
                    t.getDay(),
                    String.valueOf(t.getStartHour()),
                    String.valueOf(endHour),
                    session.getRoom().getRoomId(),
                    lecturer.getLecturerId(),
                    session.getGroupId()
            });

            remaining -= t.getDuration(); // duration is 1
        }

        return nextId;
    }

    private ScheduledSession findFreeSession(Module module,
                                             Lecturer lecturer,
                                             boolean lab,
                                             String groupId) {

        List<String> days = new ArrayList<>(Arrays.asList(DAYS));
        Collections.shuffle(days, random);

        List<Integer> hours = new ArrayList<>();
        for (int h = START_HOUR; h < END_HOUR; h++) {
            hours.add(h);
        }
        Collections.shuffle(hours, random);

        List<Room> roomCandidates = new ArrayList<>();
        int neededCapacity = requiredCapacity(groupId);
        for (Room room : data.rooms) {
            if (lab && !room.isLab()) continue;
            if (!lab && room.isLab()) continue;
            if (room.getCapacity() < neededCapacity) continue;
            roomCandidates.add(room);
        }
        Collections.shuffle(roomCandidates, random);
        
        
        
         for (String day : days) {
            for (int hour : hours) {
                Timeslot slot = new Timeslot(day, hour, 1);

                for (Room room : roomCandidates) {
                    ScheduledSession candidate =
                            new ScheduledSession(module, lecturer, room, slot, groupId);
                    
                            if (!hasConflict(candidate)) {
                        return candidate;
                    }
                }
            }
        }
        return null;
    }

    private int requiredCapacity(String groupId) {
        if (groupId == null || groupId.equalsIgnoreCase("ALL")) return 60;
        return 30;
    }

    private boolean hasConflict(ScheduledSession candidate) {
        for (ScheduledSession existing : generated) {
            // room/lecturer/group conflict
            if (existing.sameTimeWith(candidate)) return true;

            Module m1 = existing.getModule();
            Module m2 = candidate.getModule();
            if (m1 == null || m2 == null) continue;

            boolean sameProgramme = m1.getProgrammeId().equalsIgnoreCase(m2.getProgrammeId());
            boolean sameYear = m1.getYear() == m2.getYear();
            boolean sameSemester = m1.getSemester() == m2.getSemester();
            if (!sameProgramme || !sameYear || !sameSemester) continue;

            boolean overlaps = existing.getTimeslot().overlaps(candidate.getTimeslot());
            if (!overlaps) continue;

            String g1 = existing.getGroupId();
            String g2 = candidate.getGroupId();
            boolean bothAll = (g1 == null || g1.equalsIgnoreCase("ALL")) &&
                              (g2 == null || g2.equalsIgnoreCase("ALL"));
            boolean sameGroup = g1 != null && g2 != null && g1.equalsIgnoreCase(g2);

            if (bothAll || sameGroup) return true;
        }
        return false;
    }
}
