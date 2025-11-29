package Model.Timetable;

import Model.Academic.Module;
import Model.People.Lecturer;
import Model.Room.Room;

public class ScheduledSession {


    private Module module;
    private Lecturer lecturer;
    private Room room;
    private Timeslot timeslot;
    private String groupId;


    public ScheduledSession(){
    }


    public ScheduledSession(Module module, Lecturer lecturer, Room room, Timeslot timeslot, String groupId) {
        this.module = module;
        this.lecturer = lecturer;
        this.room = room;
        this.timeslot = timeslot;
        this.groupId = groupId;
    }

    public ScheduledSession(Module module, Lecturer lecturer, Room room, Timeslot timeslot) {
        this(module, lecturer, room, timeslot, "ALL");
    }

    /**
     * @return the module code for this class
     */
    public Module getModule() {
        return module;
    }

    /**
     * @return the name of the lecturer
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
     * @return the time of the class
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
                " | " + lecturer.getName() +
                " | " + room.getRoomId() +
                " | " + timeslot.toString() +
                " | Group: " + groupId;
    }
    public boolean sameTimeWith(ScheduledSession other) {
        return (this.room.equals(other.room) || this.lecturer.equals(other.lecturer))
                && this.timeslot.overlaps(other.getTimeslot());
    }
}
