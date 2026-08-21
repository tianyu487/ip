import java.util.Scanner;

/**
 * A small command line chatbot called littleDaisy.
 *
 * <p>Level-3 lets the user tick tasks off: each task is now a {@link Task}
 * object that remembers whether it is done, and {@code mark} / {@code unmark}
 * flip that flag. Telling different kinds of task apart comes in Level-4.
 */
public class LittleDaisy {
    /** Name the chatbot introduces itself with. */
    private static final String BOT_NAME = "littleDaisy";

    /** Word the user types to end the conversation. */
    private static final String COMMAND_BYE = "bye";

    /** Word the user types to see every stored task. */
    private static final String COMMAND_LIST = "list";

    /** Word the user types to tick a task off. */
    private static final String COMMAND_MARK = "mark";

    /** Word the user types to undo a {@code mark}. */
    private static final String COMMAND_UNMARK = "unmark";

    /** Largest number of tasks one conversation can hold. */
    private static final int MAX_TASKS = 100;

    /** Line printed above the task list. */
    private static final String LIST_HEADER = "Here are the tasks in your list:";

    /** Line confirming a {@code mark}. */
    private static final String MESSAGE_MARKED = "Nice! I've marked this task as done:";

    /** Line confirming an {@code unmark}. */
    private static final String MESSAGE_UNMARKED = "OK, I've marked this task as not done yet:";

    /** Ruled line that opens and closes every block of output. */
    private static final String DIVIDER =
            "    ____________________________________________________________";

    /**
     * Banner shown on startup, one array element per printed row.
     *
     * <p>Generated with the figlet "small" font. The doubled backslashes are
     * only Java escaping -- each pair prints as a single backslash.
     */
    private static final String[] BANNER = {
        " _ _ _   _   _     ___       _         ",
        "| (_) |_| |_| |___|   \\ __ _(_)____  _ ",
        "| | |  _|  _| / -_) |) / _` | (_-< || |",
        "|_|_|\\__|\\__|_\\___|___/\\__,_|_/__/\\_, |",
        "                                  |__/ "
    };

    /**
     * Starts littleDaisy.
     *
     * @param args command line arguments, unused
     */
    public static void main(String[] args) {
        greet();
        chat();
        sayBye();
    }

    /** Shows the banner followed by the welcome message. */
    private static void greet() {
        for (String row : BANNER) {
            System.out.println(row);
        }
        System.out.println();
        say("Hello! I'm " + BOT_NAME + ".", "What can I do for you?");
    }

    /**
     * Reads one line at a time and acts on it, until the user types
     * {@code bye}.
     *
     * <p>The first word of the line decides what happens: {@code list} shows
     * the tasks stored so far, {@code mark} and {@code unmark} change whether
     * one of them is done, and any other line is stored as a new task. Tasks
     * live only for the length of one conversation, since nothing is written
     * to disk yet.
     *
     * <p>The loop is guarded by {@code hasNextLine()} rather than looping
     * forever, so that input which ends without a "bye" -- a piped file, or
     * Ctrl-D -- stops the loop instead of throwing NoSuchElementException.
     */
    private static void chat() {
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();

            // Commands now carry arguments ("mark 2"), so the whole line no
            // longer matches a command word. Split it and look at word one.
            String[] parts = input.split(" ");
            String command = parts[0];

            if (command.equals(COMMAND_BYE)) {
                break;
            } else if (command.equals(COMMAND_LIST)) {
                showList(tasks, taskCount);
            } else if (command.equals(COMMAND_MARK)) {
                int index = Integer.parseInt(parts[1]) - 1;
                tasks[index].markAsDone();
                say(MESSAGE_MARKED, "  " + tasks[index]);
            } else if (command.equals(COMMAND_UNMARK)) {
                int index = Integer.parseInt(parts[1]) - 1;
                tasks[index].markAsNotDone();
                say(MESSAGE_UNMARKED, "  " + tasks[index]);
            } else {
                tasks[taskCount] = new Task(input);
                taskCount++;
                say("added: " + input);
            }
        }
        scanner.close();
    }

    /**
     * Shows the stored tasks as a numbered list, oldest first, under a header.
     *
     * <p>The numbering shown to the user starts at 1 while the array index
     * starts at 0. The header takes up {@code lines[0]}, so task {@code i}
     * lands one slot further along again.
     *
     * @param tasks array holding the tasks, of which only the first
     *     {@code taskCount} slots are filled
     * @param taskCount number of tasks stored so far
     */
    private static void showList(Task[] tasks, int taskCount) {
        String[] lines = new String[taskCount + 1];
        lines[0] = LIST_HEADER;
        for (int i = 0; i < taskCount; i++) {
            lines[i + 1] = (i + 1) + "." + tasks[i];
        }
        say(lines);
    }

    /** Shows the sign-off message printed just before the program ends. */
    private static void sayBye() {
        say("Bye. Hope to see you again soon!");
    }

    /**
     * Prints a reply: the given lines, indented, between two dividers.
     *
     * @param lines lines of the reply, printed in the order given
     */
    private static void say(String... lines) {
        System.out.println(DIVIDER);
        for (String line : lines) {
            System.out.println("     " + line);
        }
        System.out.println(DIVIDER);
        System.out.println();
    }
}
