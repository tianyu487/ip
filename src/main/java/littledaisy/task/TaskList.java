package littledaisy.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Owns the application's ordered collection of tasks. */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks initial tasks in list order
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index zero-based task position
     * @return task at the specified position
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param index zero-based task position
     * @return removed task
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the number of stored tasks.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a read-only view for display and persistence.
     *
     * @return unmodifiable task view
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }
}
