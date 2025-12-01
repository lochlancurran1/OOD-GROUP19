package Model.People;

/**
 * Represents an admin user in the system.
 * Admins inherit from User and can manage other users.
 */
public class Admin extends User {

    private String adminId;
    
    public Admin(String adminId, String name, String email, String password) {
        super(adminId, name, email, password, "Admin");
        this.adminId = adminId;
    }

    public void addUser(User newUser) {
        System.out.println("Added user: " + newUser.getName());

    }


    public void removeUser(User user) {
        System.out.println("Removed user: " + user.getName());
    }

    public String getAdminId() {
        return adminId;
    }

    /** Displays timetable access information. */
    @Override
    public void viewTimetable() {
        System.out.println("Admin " + name + " can view or modify any timetable.");

    }
}





