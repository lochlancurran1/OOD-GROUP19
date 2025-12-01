package Model.Timetable;

import Model.Academic.Module;
import Model.People.Lecturer;
import Model.Room.Room;

/**
 * Represents a single scheduled session in the timetable,
 * containing a module, lecturer, room, timeslot and student group.
 */
public class ScheduledSession {


    private Module module;
    private Lecturer lecturer;
    private Room room;
    private Timeslot timeslot;
    private String groupId;

    /** Default constructor. */
    public ScheduledSession(){
    }

    /**
     * Creates a scheduled session with the given details.
     * 
     * @param module the module being taught
     * @param lecturer  the lecturer assigned
     * @param room the room used
     * @param timeslot the timeslot of the session
     * @param groupId the student group attending
     */
    public ScheduledSession(Module module, Lecturer lecturer, Room room, Timeslot timeslot, String groupId) {
        this.module = module;
        this.lecturer = lecturer;
        this.room = room;
        this.timeslot = timeslot;
        this.groupId = groupId;
    }

    /**
     * Creates a scheduled session for all students
     * 
     * @param module the module being taught
     * @param lecturer the lecturer assigned
     * @param room the room used
     * @param timeslot the timeslot of the session
     */
    public ScheduledSession(Module module, Lecturer lecturer, Room room, Timeslot timeslot) {
        this(module, lecturer, room, timeslot, "ALL");
    }

    /**
     * @return the module for this session
     */
    public Module getModule() {
        return module;
    }

    /**
     * @return the lecturer teaching the session
     */
    public Lecturer getLecturer() {
        return lecturer;
    }

    /**
     * @return the room
     */
    public Room getRoom() {
        return room;
    }

    /**
     * @return the timeslot of the session
     */
    public Timeslot getTimeslot() {
        return timeslot;
    }

    public String getGroupId() {
        return groupId;
    }

    /**
     * Returns a readable formatted version of the timetable entry.
     *
     * @return a string containing module, lecturer, room, day and time
     */
    @Override
    public String toString() {
        return module.getModuleCode() +
                "   " + lecturer.getName() +
                "   " + room.getRoomId() +
                "   " + timeslot.toString() +
                "   Group: " + groupId;
    }

    public boolean sameTimeWith(ScheduledSession other) {
    if (this.timeslot == null || other.timeslot == null) return false;

    boolean sameRoom = (this.room != null && this.room.equals(other.room));
    boolean sameLecturer = (this.lecturer != null && this.lecturer.equals(other.lecturer));

    
    boolean sameGroup = false;
    if (this.groupId != null && other.groupId != null) {
        if (!this.groupId.equalsIgnoreCase("ALL")
                && this.groupId.equalsIgnoreCase(other.groupId)) {
            sameGroup = true;
        }
    }

    return (sameRoom || sameLecturer || sameGroup)
            && this.timeslot.overlaps(other.getTimeslot());
    }
}
