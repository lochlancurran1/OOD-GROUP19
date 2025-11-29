package Model.People;

import java.util.ArrayList;
import java.util.List;

public class Lecturer extends User {
    private final String department;
    private String role;
    private List<String> modules;

    /**
     * Constructor for Lecturer.
     * @param id the lecturer's ID
     * @param name the lecturer's name
     * @param email the lecturer's email
     * @param password the lecturer's password
     * @param department the lecturer's department
     */

    /**
     * public Lecturer(String id, String name, String email, String password, String department) {
     *      super(id, name, email, password, String role);
     *      this.department = department;
     *      this.role = role;
     *      this.modules = new ArrayList<>();
     * }
     */
    public Lecturer(String id, String name, String email, String password, String department) {
        super(id, name, email, password, "Lecturer"); 
        this.department = department;
        this.modules = new ArrayList<>();
    }

    /**
     * public String getRole() {
     * return role;
     * }
     */
    
    /**
     * Gets the lecturer's department.
     * @return the department
     */
    public String getDepartment() {
        return department;
    }
    /**
     * Gets the list of modules taught by the lecturer.
     * @return the list of modules
     */
    public List<String> getModules() {
        return modules;
    }

    public String getLecturerId() {
        return this.id;
    }

    /**
     * Prints the lecturer's timetable details.
     */
    @Override
    public void viewTimetable() {
        System.out.println("Timetable for Lecturer: " + name);

    /**
     * @Override
     * public void viewTimetable() {
     * System.out.println("Timetable for " + role + ": " + name)
     * }
     */
    }
}
