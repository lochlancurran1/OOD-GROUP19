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
 * The TimetableGenerator builds a complete timetable by assigning sessions to free
 * rooms and timeslots while checking for conflicts, and then outputs all generated 
 * sessions to a CSV file for use by the system.
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
     * Creates a TimetableGenerator with access to data and the timetable service.
     *
     * @param data     the DataManager containing modules, rooms and lecturers
     * @param service  the timetable service used for conflict checking
     */

    public TimetableGenerator(DataManager data, TimetableService service) {
        this.data = data;
        this.service = service;
    }

   /**
     * Generates a full timetable automatically and writes the result to a CSV file.
     * The generated sessions are also loaded into the DataManager and TimetableService.
     *
     * @param outputCsvPath the file path where the generated timetable should be saved
     */
   
    public void generateAndLog(String outputCsvPath) {
        generated.clear();

        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{
                "sessionId", "moduleCode", "day", "start", "end", "roomId", "lecturerId", "groupId"
        });

        int nextSessionId = 1;

        
        List<Module> moduleOrder = new ArrayList<>(data.modules);
        Collections.shuffle(moduleOrder, random);

        for (Module module : moduleOrder) {
            Lecturer lecturer = pickLecturerForModule(module);
            if (lecturer == null) {
                System.out.println("No lecturer for " + module.getModuleCode());
                continue;
            }

            
            nextSessionId = scheduleHoursForModule(
                    rows, nextSessionId, module, lecturer,
                    module.getLecHours(), false, "ALL"
            );

            
            if (module.getLabHours() > 0) {
                nextSessionId = scheduleHoursForModule(
                        rows, nextSessionId, module, lecturer,
                        module.getLabHours(), true, "G1"
                );
                nextSessionId = scheduleHoursForModule(
                        rows, nextSessionId, module, lecturer,
                        module.getLabHours(), true, "G2"
                );
            }

            
            if (module.getTutHours() > 0) {
                nextSessionId = scheduleHoursForModule(
                        rows, nextSessionId, module, lecturer,
                        module.getTutHours(), false, "G1"
                );
                nextSessionId = scheduleHoursForModule(
                        rows, nextSessionId, module, lecturer,
                        module.getTutHours(), false, "G2"
                );
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

    /**
     * Schedule a given number of hours for one (module, lecturer, group, lab/non-lab).
     *
     * @param rows           CSV rows to log into
     * @param nextSessionId  current session ID counter
     * @param module         module being scheduled
     * @param lecturer       lecturer teaching it
     * @param hoursNeeded    number of contact hours to place
     * @param lab            true if lab (needs lab rooms), false otherwise
     * @param groupId        "ALL", "G1", or "G2"
     * @return updated nextSessionId after placing what we can
     */
    private int scheduleHoursForModule(List<String[]> rows,
                                       int nextSessionId,
                                       Module module,
                                       Lecturer lecturer,
                                       int hoursNeeded,
                                       boolean lab,
                                       String groupId) {
        int remaining = hoursNeeded;

        while (remaining > 0) {
            ScheduledSession session = findFreeSession(module, lecturer, lab, groupId);
            if (session == null) {
                System.out.println("Could not place " + module.getModuleCode() +
                        " (" + (lab ? "lab" : "class") + ", group " + groupId + ")");
                break;
            }

            generated.add(session);

            Timeslot t = session.getTimeslot();
            int endHour = t.getStartHour() + t.getDuration();

            rows.add(new String[]{
                    String.valueOf(nextSessionId++),
                    module.getModuleCode(),
                    t.getDay(),
                    String.valueOf(t.getStartHour()),
                    String.valueOf(endHour),
                    session.getRoom().getRoomId(),
                    lecturer.getLecturerId(),
                    session.getGroupId()
            });

            remaining -= t.getDuration(); 
        }
        return nextSessionId;
    }

    /**
     * Find a free (day, hour, room) for this module/lecturer/group
     * that doesn't violate our conflict rules.
     */
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
        for (Room room : data.rooms) {
            if (lab && !room.isLab()) continue;
            if (!lab && room.isLab()) continue;
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

    /**
     * Returns true if placing 'candidate' would cause a clash.
     *
     * - Room / lecturer double-booking is always a clash.
     * - For same programme+year+semester, overlapping lectures are clashes,
     *   except when it's the same module in different groups (e.g. CS4006 G1 vs G2).
     */
    private boolean hasConflict(ScheduledSession candidate) {
        for (ScheduledSession existing : generated) {

            
            if (existing.sameTimeWith(candidate)) {
                return true;
            }

            Module m1 = existing.getModule();
            Module m2 = candidate.getModule();
            if (m1 == null || m2 == null) {
                continue;
            }

            
            boolean sameProgramme =
                    m1.getProgrammeId().equalsIgnoreCase(m2.getProgrammeId());
            boolean sameYear = m1.getYear() == m2.getYear();
            boolean sameSemester = m1.getSemester() == m2.getSemester();

            if (!sameProgramme || !sameYear || !sameSemester) {
                continue;
            }

            
            if (!existing.getTimeslot().overlaps(candidate.getTimeslot())) {
                continue;
            }

            
            boolean sameModuleCode =
                    m1.getModuleCode().equalsIgnoreCase(m2.getModuleCode());
            String g1 = existing.getGroupId();
            String g2 = candidate.getGroupId();
            boolean sameGroup =
                    g1 != null && g2 != null && g1.equalsIgnoreCase(g2);

            if (sameModuleCode && !sameGroup) {
                
                continue;
            }

            return true;
        }

        return false;
    }
}


