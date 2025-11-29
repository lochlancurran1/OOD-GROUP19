package Model.People;


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
    @Override
    public void viewTimetable() {
        System.out.println("Admin " + name + " can view or modify any timetable.");

    }
}





