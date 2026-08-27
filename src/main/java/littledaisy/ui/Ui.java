package littledaisy.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import littledaisy.task.Task;
import littledaisy.task.TaskList;

/** Handles all console input and output for littleDaisy. */
public class Ui {
    private static final String BOT_NAME = "littleDaisy";
    private static final String DIVIDER =
            "    ____________________________________________________________";
    private static final String[] BANNER = {
        " _ _ _   _   _     ___       _         ",
        "| (_) |_| |_| |___|   \\ __ _(_)____  _ ",
        "| | |  _|  _| / -_) |) / _` | (_-< || |",
        "|_|_|\\__|\\__|_\\___|___/\\__,_|_/__/\\_, |",
        "                                  |__/ "
    };

    private final PrintStream output;
    private final Scanner scanner;

    /** Creates a UI using the process's standard input and output. */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a UI using the supplied streams.
     *
     * @param input source of user commands
     * @param output destination for chatbot responses
     */
    public Ui(InputStream input, PrintStream output) {
        scanner = new Scanner(input);
        this.output = output;
    }

    /** Shows the application banner and greeting. */
    public void showWelcome() {
        for (String row : BANNER) {
            output.println(row);
        }
        output.println();
        showMessage("Hello! I'm " + BOT_NAME + ".", "What can I do for you?");
    }

    /**
     * Returns whether another command can be read.
     *
     * @return {@code true} if another input line is available
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command line.
     *
     * @return raw user command
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Shows all tasks as a numbered list.
     *
     * @param tasks tasks to display
     */
    public void showList(TaskList tasks) {
        String[] lines = new String[tasks.size() + 1];
        lines[0] = "Here are the tasks in your list:";
        for (int i = 0; i < tasks.size(); i++) {
            lines[i + 1] = (i + 1) + "." + tasks.get(i);
        }
        showMessage(lines);
    }

    /**
     * Shows confirmation that a task was marked.
     *
     * @param task task whose status changed
     */
    public void showMarked(Task task) {
        showMessage("Nice! I've marked this task as done:", "  " + task);
    }

    /**
     * Shows confirmation that a task was unmarked.
     *
     * @param task task whose status changed
     */
    public void showUnmarked(Task task) {
        showMessage("OK, I've marked this task as not done yet:", "  " + task);
    }

    /**
     * Shows confirmation that a task was added.
     *
     * @param task added task
     * @param taskCount resulting task count
     */
    public void showAdded(Task task, int taskCount) {
        showTaskChange("Got it. I've added this task:", task, taskCount);
    }

    /**
     * Shows confirmation that a task was deleted.
     *
     * @param task deleted task
     * @param taskCount resulting task count
     */
    public void showDeleted(Task task, int taskCount) {
        showTaskChange("Noted. I've removed this task:", task, taskCount);
    }

    /**
     * Shows a recoverable user-facing error.
     *
     * @param message error explanation
     */
    public void showError(String message) {
        showMessage("OOPS!!! " + message);
    }

    /** Shows the sign-off message. */
    public void showGoodbye() {
        showMessage("Bye. Hope to see you again soon!");
    }

    /** Closes the input scanner. */
    public void close() {
        scanner.close();
    }

    /** Shows a task change and the resulting list size. */
    private void showTaskChange(String message, Task task, int taskCount) {
        showMessage(message,
                "  " + task,
                "Now you have " + taskCount + " tasks in the list.");
    }

    /** Prints the supplied lines, indented, between dividers. */
    private void showMessage(String... lines) {
        output.println(DIVIDER);
        for (String line : lines) {
            output.println("     " + line);
        }
        output.println(DIVIDER);
        output.println();
    }
}
