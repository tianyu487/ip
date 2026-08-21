/**
 * A task that has to be finished before a stated point in time, e.g.
 * {@code return book (by: Sunday)}.
 */
public class Deadline extends Task {
    /** When the task has to be done by, as the user typed it. */
    protected String by;

    /**
     * Creates a deadline that is not done yet.
     *
     * @param description what the deadline is about
     * @param by when it has to be done by, as the user typed it
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns this deadline with its type icon and due time, e.g.
     * {@code [D][ ] return book (by: Sunday)}.
     *
     * @return the type icon, the inherited rendering, then the due time
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
