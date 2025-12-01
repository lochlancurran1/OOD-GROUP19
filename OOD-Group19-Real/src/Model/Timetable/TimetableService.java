package Model.Timetable;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles timetable-related logic such as storing sessions and
 * checking for clashes when new sessions are added.
 */
public class TimetableService {

    private List<ScheduledSession> sessions;

    public TimetableService() {
        this.sessions = new ArrayList<>();
    }

    /**
     * Loads a new list of sessions into the system and replaces any existing ones.
     *
     * @param loaded the list of sessions to load
     */
    public void loadSessions(List<ScheduledSession> loaded) {
        sessions = new ArrayList<>();
        sessions.addAll(loaded);
    }

    /**
     * Attempts to add a new session to the timetable.
     * If there are clashes then the session is not added.
     *
     * @param newSession the session to add
     * @return a list of conflict messages, empty if the session is valid
     */
    public List<String> addSession(ScheduledSession newSession) {
        List<String> conflicts = new ArrayList<>();

        for (ScheduledSession existing : sessions) {
            if (existing.sameTimeWith(newSession)) {

                // Room conflict
                if (existing.getRoom().equals(newSession.getRoom())) {
                    conflicts.add("ROOM conflict with " + existing);
                }

                // Lecturer conflict
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
     * @return all sessions stored in the timetable
     */
    public List<ScheduledSession> getAllSessions() {
        return sessions;
    }

    /**
     * Gets all sessions taught by a lecturer.
     *
     * @param lecturerName the lecturer's name
     * @return matching sessions
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
     * Gets all sessions held in a particular room.
     *
     * @param roomId the room ID
     * @return matching sessions
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
     *
     * @param moduleCode the module code
     * @return matching sessions
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
