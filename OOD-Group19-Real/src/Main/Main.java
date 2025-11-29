package Main;

import controllers.DataManager;
import controllers.TimetableController;
import Model.Timetable.TimetableService;
import View.UserInterface;
import Model.Timetable.ScheduledSession;

import java.util.*;

public class Main {
    public static void main(String[] args) {

        DataManager datamanager = new DataManager();
        TimetableService service = new TimetableService();

        datamanager.loadStudents("data/students.csv");
        datamanager.loadLecturers("data/lecturers.csv");
        datamanager.loadRooms("data/rooms.csv");
        datamanager.loadModules("data/modules.csv");
        datamanager.loadProgrammes("data/programmes.csv");
        datamanager.loadAdmins("data/admins.csv");

        List<ScheduledSession> loadedSessions = datamanager.loadSessions("data/sessions.csv");
        datamanager.sessions.addAll(loadedSessions);
        service.loadSessions(loadedSessions);

        TimetableController controller = new TimetableController(service, datamanager);

        var conflicts = controller.findRoomConflicts();
        if (conflicts.isEmpty()) {
            System.out.println("No room conflicts found ");
        } else {
            System.out.println("Room conflicts detected:");
            for (String c : conflicts) {
                System.out.println(c);
            }
        }

        UserInterface ui = new UserInterface(controller, datamanager);
        ui.start();
    }
}

// 29/11/25