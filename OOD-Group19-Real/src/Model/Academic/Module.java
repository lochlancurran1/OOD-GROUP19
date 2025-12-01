package Model.Academic;


/**
 * Represents a module, each module has a name, code, and
 * the number of lecture, lab and tutorial hours required weekly.
 */
public class Module {


    private String moduleName;
    private String moduleCode;
    private String programmeId;
    private int year;
    private int semester;
    private int LecHours;
    private int LabHours;
    private int tutHours;

    
    /** Empty constructor for cases where fields are set later. */
    public Module() { }

    public Module(String moduleName, String moduleCode, String programmeId, int year, int semester,
                  int LecHours, int LabHours, int tutHours) {
        this.moduleName = moduleName;
        this.moduleCode = moduleCode;
        this.programmeId = programmeId;
        this.year = year;
        this.semester = semester;
        this.LecHours = LecHours;
        this.LabHours = LabHours;
        this.tutHours = tutHours;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }
    public void setProgrammeId(String programmeId) {
        this.programmeId = programmeId;
    }

    public void setLecHour(int LecHours) {
        this.LecHours = LecHours;
    }


    public void setLabHour(int LabHours) {
        this.LabHours = LabHours;
    }

    public void setTutHours(int tutHours) {
        this.tutHours = tutHours;
    }

    public String getModuleName() {
        return moduleName;
    }

    public int getYear() {
        return year;
    }

    public int getSemester() {
        return semester;
    }


    public String getModuleCode() {
        return moduleCode;
    }
    public String getProgrammeId() {
        return programmeId;
    }


    public int getLabHours() {
        return LabHours;
    }


    public int getLecHours() {
        return LecHours;
    }

    public int getTutHours() {
        return tutHours;
    }

    @Override
    public String toString() {
        return moduleCode + " - " + moduleName + " (Prog " + programmeId + ") (Year " + year + ", Semester " + semester + ")" +
                " (Lec: " + LecHours + ", Lab: " + LabHours + ", Tut: " + tutHours + ")";
    }
}
