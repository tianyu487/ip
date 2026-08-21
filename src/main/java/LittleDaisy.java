import java.util.Scanner;

/**
 * A small command line chatbot called littleDaisy.
 *
 * <p>Level-2 gives littleDaisy a memory: any line that is not a command is
 * stored as a task, {@code list} shows the tasks stored so far, and
 * {@code bye} ends the conversation. Marking a task as done comes in Level-3.
 */
public class LittleDaisy {
    /** Name the chatbot introduces itself with. */
    private static final String BOT_NAME = "littleDaisy";

    /** Word the user types to end the conversation. */
    private static final String COMMAND_BYE = "bye";

    private static final int MAX_TASKS = 100;
    private static final String COMMAND_LIST = "list";

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
     * <p>A line of {@code list} shows the tasks stored so far; any other line
     * is stored as a new task. Tasks live only for the length of one
     * conversation, since nothing is written to disk yet.
     *
     * <p>The loop is guarded by {@code hasNextLine()} rather than looping
     * forever, so that input which ends without a "bye" -- a piped file, or
     * Ctrl-D -- stops the loop instead of throwing NoSuchElementException.
     */
    private static void chat() {
        String[] tasks = new String[MAX_TASKS];
        int taskCount = 0;
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (input.equals(COMMAND_BYE)) {
                break;
            } else if (input.equals(COMMAND_LIST)) {
                showList(tasks, taskCount);
            } else {
                tasks[taskCount] = input;
                taskCount++;
                say("added: " + input);
            }
        }
        scanner.close();
    }

    /**
     * Shows the stored tasks as a numbered list, oldest first.
     *
     * <p>The numbering shown to the user starts at 1 while the array index
     * starts at 0, hence the {@code i + 1}.
     *
     * @param tasks array holding the tasks, of which only the first
     *     {@code taskCount} slots are filled
     * @param taskCount number of tasks stored so far
     */
    private static void showList(String[] tasks, int taskCount) {
        String[] lines = new String[taskCount];
        for (int i = 0; i < taskCount; i++) {
            lines[i] = (i + 1) + ". " + tasks[i];
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
