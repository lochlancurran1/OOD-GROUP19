package controllers;

import Model.Academic.Module;
import Model.People.Lecturer;
import Model.Room.Room;
import Model.Timetable.ScheduledSession;
import Model.Timetable.Timeslot;
import Model.Timetable.TimetableService;

import java.util.ArrayList;
import java.util.List;

public class TimetableGenerator {

    private static final String[] DAYS = {"MON", "TUE", "WED", "THU", "FRI"};
    private static final int START_HOUR = 9;
    private static final int END_HOUR = 18; // last start is 17

    private final DataManager data;
    private final TimetableService service;
    private final List<ScheduledSession> generated = new ArrayList<>();

    public TimetableGenerator(DataManager data, TimetableService service) {
        this.data = data;
        this.service = service;
    }

    public void generateAndLog(String outputCsvPath) {
        generated.clear();
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"sessionId", "moduleCode", "day", "start", "end", "roomId", "lecturerId", "groupId"});

        int nextId = 1;

        for (Module m : data.modules) {
            Lecturer lec = pickLecturerForModule(m);
            if (lec == null) {
                System.out.println("No lecturer for " + m.getModuleCode());
                continue;
            }

            nextId = scheduleHours(m, lec, m.getLecHours(), false, rows, nextId);
            nextId = scheduleHours(m, lec, m.getLabHours(), true, rows, nextId);
            nextId = scheduleHours(m, lec, m.getTutHours(), false, rows, nextId);
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

    private int scheduleHours(Module module,
                              Lecturer lecturer,
                              int hoursNeeded,
                              boolean lab,
                              List<String[]> rows,
                              int nextId) {
        int remaining = hoursNeeded;

        while (remaining > 0) {
            ScheduledSession session = findFreeSession(module, lecturer, lab);
            if (session == null) {
                System.out.println("Could not place " + module.getModuleCode() + " (" + (lab ? "lab" : "class") + ")");
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

    private ScheduledSession findFreeSession(Module module, Lecturer lecturer, boolean lab) {
        for (String day : DAYS) {
            for (int hour = START_HOUR; hour < END_HOUR; hour++) {
                Timeslot slot = new Timeslot(day, hour, 1);

                for (Room room : data.rooms) {
                    if (lab && !room.isLab()) continue;
                    if (!lab && room.isLab()) continue;

                    ScheduledSession candidate = new ScheduledSession(module, lecturer, room, slot, "ALL");

                    if (!hasConflict(candidate)) {
                        return candidate;
                    }
                }
            }
        }
        return null;
    }

    private boolean hasConflict(ScheduledSession candidate) {
        for (ScheduledSession existing : generated) {
            if (existing.sameTimeWith(candidate)) {
                return true;
            }
        }
        return false;
    }
}
