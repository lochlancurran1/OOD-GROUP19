package Model.Timetable;

import java.util.ArrayList;
import java.util.List;

public class TimetableService {

    private List<ScheduledSession> sessions;

    public TimetableService() {
        this.sessions = new ArrayList<>();
    }

    public void loadSessions(List<ScheduledSession> loaded) {
        sessions.addAll(loaded);
    }

    public List<String> addSession(ScheduledSession newSession) {
        List<String> conflicts = new ArrayList<>();

        for (ScheduledSession existing : sessions) {
            if (existing.sameTimeWith(newSession)) {

                // Check room conflict
                if (existing.getRoom().equals(newSession.getRoom())) {
                    conflicts.add("ROOM conflict with " + existing);
                }

                // Check lecturer conflict
                if (existing.getLecturer().equals(newSession.getLecturer())) {
                    conflicts.add("LECTURER conflict with " + existing);
                }
            }
        }

        if (conflicts.isEmpty()) {
            sessions.add(newSession);
        }

        return conflicts;
    }

    /**
     * Returns all scheduled sessions.
     */
    public List<ScheduledSession> getAllSessions() {
        return sessions;
    }

    /**
     * Gets all sessions for a specific lecturer.
     */
    public List<ScheduledSession> getSessionsForLecturer(String lecturerName) {
        List<ScheduledSession> result = new ArrayList<>();
        for (ScheduledSession s : sessions) {
            if (s.getLecturer().getName().equalsIgnoreCase(lecturerName)) {
                result.add(s);
            }
        }
        return result;
    }

    /**
     * Gets all sessions for a specific room.
     */
    public List<ScheduledSession> getSessionsForRoom(String roomId) {
        List<ScheduledSession> result = new ArrayList<>();
        for (ScheduledSession s : sessions) {
            if (s.getRoom().getRoomId().equalsIgnoreCase(roomId)) {
                result.add(s);
            }
        }
        return result;
    }

    /**
     * Gets all sessions for a specific module.
     */
    public List<ScheduledSession> getSessionsForModule(String moduleCode) {
        List<ScheduledSession> result = new ArrayList<>();
        for (ScheduledSession s : sessions) {
            if (s.getModule().getModuleCode().equalsIgnoreCase(moduleCode)) {
                result.add(s);
            }
        }
        return result;
    }
}

