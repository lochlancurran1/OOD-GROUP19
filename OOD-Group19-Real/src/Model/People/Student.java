package Model.People;
/**
 *  Represents a student in UL and their key details such as
 * student ID, name, email, password, course programme ID
 * and year of study.
 */

public class Student extends User {
    private String programmeID;
    private int year;
    private String groupId;

    public Student(String id, String name, String email, String password,
                   String programmeID, int year, String groupId) {
        super(id, name, email, password, "Student");
        this.programmeID = programmeID;
        this.year = year;
        this.groupId = groupId;
    }

    public String getProgrammeID() {
        return programmeID;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getGroupId() {
        return groupId;
    }

    @Override
    public void viewTimetable() {
        System.out.println("Timetable for " + name + " (" + programmeID + " Year: " + year + ")");
    }


}

