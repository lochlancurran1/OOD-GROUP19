package Model.Academic;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an academic programme in UL
 * A programme has an ID, name and a list of semesters.
 */
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

