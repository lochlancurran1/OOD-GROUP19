package Model.Timetable;

public class Timeslot {


    private String day;
    private int startHour;
    private int duration;


    public Timeslot(String day, int startHour, int duration) {
        this.day = day;
        this.startHour = startHour;
        this.duration = duration;
    }

    /**
     * Gets the day of the timeslot.
     *
     * @return day the day of the week
     */
    public String getDay() {
        return day;
    }

    /**
     * Gets the starting hour of this timeslot.
     *
     * @return startHour the hour the class starts
     */
    public int getStartHour() {
        return startHour;
    }

    /**
     * Gets how long this timeslot lasts.
     *
     * @return duration in hours
     */
    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        int endHour = startHour + duration;
        return day + " " + startHour + ":00 to " + endHour + ":00";
    }

    /**
     * Checks if this timeslot overlaps another one.
     * Two timeslots overlap if:
     * - they are on the same day, and
     * - their time ranges cross over.
     *
     * @param other the other timeslot to compare with
     * @return true if both timeslots overlap, false otherwise
     */
    public boolean overlaps(Timeslot other) {
        if (!this.day.equals(other.day)) {
            return false;
        }

        int thisEnd = this.startHour + this.duration;
        int otherEnd = other.startHour + other.duration;

        return this.startHour < otherEnd && other.startHour < thisEnd;
    }
}


