package Model.Academic;

import java.util.ArrayList;
import java.util.List;

public class Programme {

    private String id;
    private String name;
    private List<ProgrammeSemester> semesters = new ArrayList<>();


    public Programme() {
    }


    public Programme(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<ProgrammeSemester> getSemesters() {
        return semesters;
    }


    public void addSemester(ProgrammeSemester semester) {
        semesters.add(semester);
    }

}

