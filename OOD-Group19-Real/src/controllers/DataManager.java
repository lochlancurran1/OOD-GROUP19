package controllers;

import Model.People.Student;
import Model.People.Lecturer;
import Model.People.Admin;
import Model.Academic.Module;
import Model.Room.Room;
import Model.Timetable.ScheduledSession;
import Model.Timetable.Timeslot;
import Model.Academic.Programme;

import java.util.*;


/**
 * The controllers.DataManager class is responsible for loading data from CSV files
 * and converting it into the objects used by the system.
 */
public final class DataManager {

    public List<Student> students = new ArrayList<>();
    public List<Lecturer> lecturers = new ArrayList<>();
    public List<Room> rooms = new ArrayList<>();
    public List<Module> modules = new ArrayList<>();
    public List<Programme> programmes = new ArrayList<>();
    public List<ScheduledSession> sessions = new ArrayList<>();
    public List<Admin> admins = new ArrayList<>();


    public void loadStudents(String file) {
        List<String[]> data = CSVReader.readCSV(file);

        for (String[] row : data) {
            if (row[0].equalsIgnoreCase("studentId")) continue;


            String id = row[0];
            String name = row[1];
            String email = row[2];
            String password = row[3];
            String programme = row[4];
            int year = Integer.parseInt(row[5]);
            String groupId = row[6];

            students.add(new Student(id, name, email, password, programme, year, groupId));

        }
    }


    /*
    for (String[] row : data) {
        if (row[0].equalsIgnoreCase("lecturerId")) continue;
    
        String id = row[0];
        String name = row[1];
        String email = row[2];
        String password = row[3];
        String department = row[4];
        String role = row[5];
        
        lecturer.add(new Lecturer(id, name, email, password, department, role));
    }
    */

    public void loadLecturers(String file) {
        List<String[]> data = CSVReader.readCSV(file);

        for (String[] row : data) {
            if (row[0].equalsIgnoreCase("lecturerId")) continue;

            lecturers.add(new Lecturer(row[0], row[1], row[2], row[3], row[4]));

        }
    }

    public void loadRooms(String file) {
        List<String[]> data = CSVReader.readCSV(file);

        for (String[] row : data) {
            if (row[0].equalsIgnoreCase("roomId")) continue;

            String id = row[0];
            String type = row[1];
            int capacity = Integer.parseInt(row[2]);
            String building = row[3];

            rooms.add(new Room(id, type, capacity, building));
        }
    }


    public void loadModules(String file) {
        List<String[]> data = CSVReader.readCSV(file);

        for (String[] row : data) {
            if (row[0].equalsIgnoreCase("moduleCode")) continue;


            String code = row[0];
            String name = row[1];
            int year = Integer.parseInt(row[2]);
            int semester = Integer.parseInt(row[3]);
            String programmeId = row[4];
            int lec = Integer.parseInt(row[5]);
            int lab = Integer.parseInt(row[6]);
            int tut = Integer.parseInt(row[7]);

            modules.add(new Module(name, code, year, semester, lec, lab, tut));
        }
    }

    public void loadProgrammes(String file) {
        List<String[]> data = CSVReader.readCSV(file);

        for (String[] row : data) {
            if (row[0].equalsIgnoreCase("programmeId")) continue;

            programmes.add(new Programme(row[0], row[1]));
        }
    }


    public List<ScheduledSession> loadSessions(String file) {
        List<ScheduledSession> loaded = new ArrayList<>();
        List<String[]> data = CSVReader.readCSV(file);

        for (String[] row : data) {
            if (row[0].equalsIgnoreCase("sessionId")) {
                continue;
            }
            Module module = findModule(row[1]);
            int start = Integer.parseInt(row[3]);
            int end = Integer.parseInt(row[4]);
            int duration = end - start;
            Timeslot timeslot = new Timeslot(row[2], start, duration);
            Room room = findRoom(row[5]);
            Lecturer lecturer = findLecturer(row[6]);
            String groupId = "ALL";
            loaded.add(new ScheduledSession(module, lecturer, room, timeslot, groupId));


        }
        return loaded;
    }

    public void loadAdmins(String file) {
        List<String[]> data = CSVReader.readCSV(file);

        for (String[] row : data) {
            if (row[0].equalsIgnoreCase("adminId")) continue;

            String id = row[0];
            String name = row[1];
            String email = row[2];
            String password = row[3];
            admins.add(new Admin(id, name, email, password));
        }
    }

    public Module findModule(String code) {
        for (Module m : modules) if (m.getModuleCode().equals(code)) return m;
        return null;
    }

    public Room findRoom(String id) {
        for (Room r : rooms) if (r.getRoomId().equals(id)) return r;
        return null;
    }

    public Lecturer findLecturer(String id) {
        for (Lecturer l : lecturers) if (l.getLecturerId().equals(id)) return l;
        return null;
    }
}

