package littledaisy.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/** Owns the application's ordered collection of tasks. */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /** Creates a task list containing the supplied tasks. */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /** Returns the task at the given zero-based index. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Removes and returns the task at the given zero-based index. */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /** Returns the number of stored tasks. */
    public int size() {
        return tasks.size();
    }

    /** Returns tasks whose descriptions contain the keyword, ignoring case. */
    public TaskList find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        ArrayList<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            String description = task.getDescription().toLowerCase(Locale.ROOT);
            if (description.contains(normalizedKeyword)) {
                matches.add(task);
            }
        }
        return new TaskList(matches);
    }

    /** Returns a read-only view for display and persistence. */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }
}
