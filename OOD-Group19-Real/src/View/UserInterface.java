package View;

import controllers.DataManager;
import controllers.TimetableController;
import Model.People.Student;
import Model.People.Lecturer;
import Model.People.Admin;

import java.util.Scanner;

/**
 * This class handles all the user interaction through the command line.
 * It only shows menus, gets input and prints messages.
 * The controller will deal with the logic, not this class.
 */
public class UserInterface {

    private final Scanner scanner = new Scanner(System.in);
    private final TimetableController controller;
    private final DataManager datamanager;

    public UserInterface(TimetableController controller, DataManager datamanager) {
        this.controller = controller;
        this.datamanager = datamanager;
    }

    public void start() {
        boolean running = true;

        while (running) {
            showMainMenu();
            String choice = getInput();

            switch (choice) {
                case "1" -> loginFlow();
                case "2" -> {
                    System.out.println("Exiting system");
                    running = false;
                }
                default -> showMessage("Invalid option. Try again");
            }
        }
    }

    private void loginFlow() {
        String email = prompt("Enter email");
        String password = prompt("Enter password");

        Object user = controller.login(email, password);


        if (user instanceof Student s) studentMenu(s);
        else if (user instanceof Lecturer l) lecturerMenu(l);
        else if (user instanceof Admin a) adminMenu(a);

    }

    private void studentMenu(Student s) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("    Student Menu    ");
            System.out.println("1. View my timetable");
            System.out.println("2. View course/year timetable");
            System.out.println("3. View module timetable");
            System.out.println("4. View room timetable");
            System.out.println("5. Logout");
            System.out.print("Choose an option: ");
            String choice = getInput();

            switch (choice) {
                case "1" -> {
                    System.out.println("1. Autumn (Semester 1)");
                    System.out.println("2. Spring (Semester 2)");
                    System.out.println("Choose an option: ");

                    String semChoice = getInput();
                    int semester = semChoice.equals("2") ? 2 : 1;

                    showTimetable(controller.getTimetableForStudent(s, semester));
                }


                case "2" -> {
                    String programmeId = prompt("Enter programme ID (e.g. LM174)");
                    int year = Integer.parseInt(prompt("Enter year (e.g. 1–4)"));
                    int semester = Integer.parseInt(prompt("Enter semester (1=Autumn, 2=Spring)"));
                    showTimetable(controller.getTimetableForCourseYear(programmeId, year, semester));
                }
                case "3" -> {
                    String moduleCode = prompt("Enter module code (e.g. CS4013)");
                    showTimetable(controller.getTimetableForModule(moduleCode));
                }
                case "4" -> {
                    String roomId = prompt("Enter room ID (e.g. CSG001)");
                    showTimetable(controller.getTimetableForRoom(roomId));
                }

                case "5" -> loggedIn = false;
                default -> showMessage("Invalid choice.");
            }
        }
     }
        
    

 private void lecturerMenu(Lecturer l) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("   Lecturer Menu   ");
            System.out.println("1. View my timetable");
            System.out.println("2. View course/year timetable");
            System.out.println("3. View module timetable");
            System.out.println("4. View room timetable");
            System.out.println("5. Logout");
            System.out.print("Choose an option: ");
            String choice = getInput();

            switch (choice) {
                case "1" -> {
                    showTimetable(controller.getTimetableForLecturer(l));
                }
                 case "2" -> {
                    String programmeId = prompt("Enter programme ID (e.g. LM174)");
                    int year = Integer.parseInt(prompt("Enter year (e.g. 1–4)"));
                    int semester = Integer.parseInt(prompt("Enter semester (1=Autumn, 2=Spring)"));
                    showTimetable(controller.getTimetableForCourseYear(programmeId, year, semester));
                }

                case "3" -> {
                    String moduleCode = prompt("Enter module code (e.g. CS4013)");
                    showTimetable(controller.getTimetableForModule(moduleCode));
                }

                case "4" -> {
                    String roomId = prompt("Enter room ID (e.g. CSG001)");
                    showTimetable(controller.getTimetableForRoom(roomId));
                }
                case "5" -> loggedIn = false;
                default -> showMessage("Invalid choice.");
            }
        }
 }

 private void adminMenu(Admin a) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("     Admin Menu    ");
            System.out.println("1. View full timetable");
            System.out.println("2. View course/year timetable");
            System.out.println("3. View module timetable");
            System.out.println("4. View room timetable");
            System.out.println("5. List sessions (with index)");
            System.out.println("6. Add session");
            System.out.println("7. Remove session");
            System.out.println("8. Update session");
            System.out.println("9. Add User");
            System.out.println("10. Remove User");
            System.out.println("11. Logout");
            System.out.print("Choose an option: ");
            String choice = getInput();

            switch (choice) {
                case "1" -> showTimetable(controller.getFullTimetable());
                case "2" -> {
                    String programmeId = prompt("Enter programme ID");
                    int year = Integer.parseInt(prompt("Enter year (1-4)"));
                    int semester = Integer.parseInt(prompt("Enter semester (1-Autumn, 2-Spring)"));
                    showTimetable(controller.getTimetableForCourseYear(programmeId, year, semester));
                }
                case "3" -> {
                    String moduleCode = prompt("Enter module code");
                    showTimetable(controller.getTimetableForModule(moduleCode));
                }
                case "4" -> {
                    String roomId = prompt("Enter room ID");
                    showTimetable(controller.getTimetableForRoom(roomId));
                }
                case "5" -> showTimetable(controller.listSessionsWithIndex());
                case "6" -> addSession();
                case "7" -> removeSession();
                case "8" -> updateSession();
                case "9" -> addUser();
                case "10" -> removeUser();
                case "11" -> loggedIn = false;
                default -> showMessage("Invalid choice.");
            }
        }
 }

 private void addUser() {
        showMessage("Add user - choose type (student/lecturer/admin)");
        String type = getInput().trim().toLowerCase();

        if (type.equals("student")) {
            String id = prompt("Student ID");
            String name = prompt("Name");
            String email = prompt("Email");
            String password = prompt("Password");
            String programme = prompt("Programme");
            int year = Integer.parseInt(prompt("Year"));
            String groupId = prompt("Group");
            datamanager.students.add(new Model.People.Student(id, name, email, password, programme, year, groupId));
            showMessage("Student added.");
            datamanager.saveStudents("OOD-Group19-Real/data/students.csv");
        } else if (type.equals("lecturer")) {
            String id = prompt("Lecturer ID");
            String name = prompt("Name");
            String email = prompt("Email");
            String password = prompt("Password");
            String dept = prompt("Department");
            datamanager.lecturers.add(new Model.People.Lecturer(id, name, email, password, dept));
            showMessage("Lecturer added.");
            datamanager.saveLecturers("OOD-Group19-Real/data/lecturers.csv");
        } else if (type.equals("admin")) {
            String id = prompt("Admin ID");
            String name = prompt("Name");
            String email = prompt("Email");
            String password = prompt("Password");
            datamanager.admins.add(new Model.People.Admin(id, name, email, password));
            showMessage("Admin added.");
            datamanager.saveAdmins("OOD-Group19-Real/data/admins.csv");
        } else {
            showMessage("Unknown type.");
        }

 }
 private void removeUser() {
        showMessage("Remove user - enter email");
        String email = getInput().trim();

        boolean removed = datamanager.students.removeIf(s -> s.getEmail().equalsIgnoreCase(email));
        removed = datamanager.lecturers.removeIf(l -> l.getEmail().equalsIgnoreCase(email)) || removed;
        removed = datamanager.admins.removeIf(a -> a.getEmail().equalsIgnoreCase(email)) || removed;

        if (removed){
            datamanager.saveStudents("OOD-Group19-Real/data/students.csv");
            datamanager.saveLecturers("OOD-Group19-Real/data/lecturers.csv");
            datamanager.saveAdmins("OOD-Group19-Real/data/admins.csv");
             showMessage("User removed.");
        }
        else showMessage("No user found with that email.");
 }

 public void showMainMenu() {
        System.out.println("     UL Timetable System     ");
        System.out.println(" 1. Login");
        System.out.println(" 2. Exit");
        System.out.println("Choose an option: ");
 }

 public String getInput() {
        return scanner.nextLine();
 }
 public String prompt(String message) {
        System.out.print(message + ": ");
        return scanner.nextLine();
 }

 public void showMessage(String message) {
        System.out.println(message);
 }

 public void showTimetable(String timetable) {
        System.out.println("     Timetable     ");
        System.out.println(timetable);
 }
 private void addSession() {
        showMessage("Add new session:");
        String moduleCode = prompt("Module code");
        String day = prompt("Day");
        int start = Integer.parseInt(prompt("Start hour"));
        int end = Integer.parseInt(prompt("End hour"));
        String roomId = prompt("Room ID");
        String lecturerId = prompt("Lecturer ID");
        String groupId = prompt("Group ID");

        boolean success = controller.addSessionAdmin(moduleCode, day, start, end, roomId, lecturerId, groupId);
        if (success) {
            showMessage("Session added successfully.");
            datamanager.saveSessions("OOD-Group19-Real/data/sessions.csv");
        } else {
            showMessage("Session could not be added.");
        }
 }

 private void removeSession() {
        showMessage("Remove session - enter index (see list sessions)");
        try {
            int idx = Integer.parseInt(getInput().trim());
            boolean ok = controller.removeSessionByIndex(idx);
            if (ok) {
                datamanager.saveSessions("OOD-Group19-Real/data/sessions.csv");
                showMessage("Session removed.");
            } else {
                showMessage("Invalid index.");
            }
        } catch (NumberFormatException e) {
            showMessage("Invalid number.");
        }
 }

 private void updateSession() {
        showMessage("Update session - enter index (see list sessions)");
        try {
            int idx = Integer.parseInt(getInput().trim());
            String moduleCode = prompt("Module code");
            String day = prompt("Day");
            int start = Integer.parseInt(prompt("Start hour"));
            int end = Integer.parseInt(prompt("End hour"));
            String roomId = prompt("Room ID");
            String lecturerId = prompt("Lecturer ID");
            String groupId = prompt("Group ID");

            boolean ok = controller.updateSessionByIndex(idx, moduleCode, day, start, end, roomId, lecturerId, groupId);
            if (ok) {
                datamanager.saveSessions("OOD-Group19-Real/data/sessions.csv");
                showMessage("Session updated.");
            } else {
                showMessage("Could not update session (conflict or bad data).");
            }
        } catch (NumberFormatException e) {
            showMessage("Invalid number.");
        }
 }
}
