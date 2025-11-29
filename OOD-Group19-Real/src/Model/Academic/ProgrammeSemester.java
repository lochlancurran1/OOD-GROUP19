package Model.Academic;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a semester within a programme,
 * each semester has a number and a list of modules taught during that semester.
 */
public class ProgrammeSemester {

    /** The semester number of the programme. */
    private int semesterNumber;

    /** The list of modules assigned to this semester. */
    private List<Module> modules = new ArrayList<>();

    /**
     * Creates an empty ProgrammeSemester object.
     * Allows the object to be created before all data is available.
     */
    public ProgrammeSemester() {}

    /**
     * Creates ProgrammeSemester along with the semester number.
     *
     * @param semesterNumber the number of the semester
     */
    public ProgrammeSemester(int semesterNumber) {
        this.semesterNumber = semesterNumber;
    }

    /**
     * Sets the semester number.
     *
     * @param semesterNumber the new semester number
     */
    public void setSemesterNumber(int semesterNumber) {
        this.semesterNumber = semesterNumber;
    }

    /**
     * Returns the semester number.
     *
     * @return the semester number
     */
    public int getSemesterNumber() {
        return semesterNumber;
    }

    /**
     * Returns the list of modules in the semester.
     *
     * @return the list of Module objects in this semester
     */
    public List<Module> getModules() {
        return modules;
    }

    /**
     * Adds a module to this semester.
     *
     * @param module the module to add
     */
    public void addModule(Module module) {
        modules.add(module);
    }
}
