/**
 * A single item on littleDaisy's task list.
 *
 * <p>A task is just a line of text plus a flag saying whether it has been
 * done. Level-4 will add subclasses for the different kinds of task, which is
 * why the fields are {@code protected} rather than {@code private}: the
 * subclasses need to reach them directly.
 */
public class Task {
    /** What the user typed when adding this task. */
    protected String description;

    /** Whether the user has marked this task as done. */
    protected boolean isDone;

    /**
     * Creates a task that is not done yet.
     *
     * @param description what the task is about
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Records that this task has been done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Records that this task is not done after all. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the one-character mark shown inside the status box.
     *
     * @return {@code "X"} if this task is done, a single space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns this task as it should appear to the user, e.g. {@code [X] read
     * book}.
     *
     * <p>Level-4 subclasses override this to put a type icon in front, reusing
     * this result via {@code super.toString()}.
     *
     * @return the status box followed by the description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
