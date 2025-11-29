package Model.Room;

/**
 * Represents a teaching room in the university and
 * each room has a room ID, seating capacity and indicates whether the room is a lab or a classroom.
 */
public class Room {


    private String roomId;
    private String type;
    private int capacity;
    private String building;



    public Room(String roomId, String type, int capacity, String buidling) {
        this.roomId = roomId;
        this.type = type;
        this.capacity = capacity;
        this.building = building;
    }


    public String getRoomId() {
        return roomId;
    }


    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }


    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    public String getType() {
        return type;
    }

    public String getBuilding() {
        return building;
    }

    public boolean isLab() {
        return type.equalsIgnoreCase("Laboratory");
    }





    @Override
    public String toString() {
        return roomId +  "Type: " + type + ", Capacity: " + capacity + "Building: " + building;
    }

}
