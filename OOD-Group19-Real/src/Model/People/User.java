package Model.People;

/**
 * Demonstrates a user (lecturer, student, admin) and
 * works as an abstract superclass for each user type and
 * identifies the key attributes of a user in UL and what
 * is shared among a lecturer, admin and student.
 */
public abstract class User {

    /** UL user ID. */
    protected String id;

    /** Full name of the user. */
    protected String name;

    /** UL email address of the user. */
    protected String email;

    /** User's password. */
    protected String password;

    /** The type of user: "Student", "Lecturer", or "Admin". */
    protected String userType;

    /**
     * A constructor (User) with unique attributes of a user in UL
     * @param id   user's specific UL id
     * @param name  user';s name
     * @param email  user's UL email
     * @param password user's password in which they selected
     * @param userType (Lecturer, student, admin)
     */
    public User(String id, String name, String email, String password,
                String userType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    /**
     *
     * @return users specific UL id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @return users UL email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @return users name
     */
    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    /**
     *
     * @return users type (lecturer, student, admin)
     */
    public String getUserType() {
        return userType;
    }
    // password doesn't get a getter because of security and encapsulation
    // no other class or user should be able to read it directly once its stored

    /**
     * Represents a login system where the user is prompted to
     * enter id and password and compares the input to a stored
     * users info (id, password)
     * @param idEntered users id entered
     * @param passwordEntered users password entered
     * @return true if login details match, false if no match
     */
    public boolean idLogin(String idEntered, String passwordEntered) {
        if (id == null || password == null) {
            return false;
        }
        return this.id.equals(idEntered) && password.equals(passwordEntered); //Checks if inputted password matches user password;
    }

    /**
     * Each user type must define how they view a timetable.
     */
    public abstract void viewTimetable();
}

