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

        datamanager.loadStudents("OOD-Group19-Real/data/students.csv");
        datamanager.loadLecturers("OOD-Group19-Real/data/lecturers.csv");
        datamanager.loadRooms("OOD-Group19-Real/data/rooms.csv");
        datamanager.loadModules("OOD-Group19-Real/data/modules.csv");
        datamanager.loadProgrammes("OOD-Group19-Real/data/programmes.csv");
        datamanager.loadAdmins("OOD-Group19-Real/data/admins.csv");

        List<ScheduledSession> loadedSessions = datamanager.loadSessions("OOD-Group19-Real/data/sessions.csv");
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
