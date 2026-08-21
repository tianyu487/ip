import java.util.Scanner;

/**
 * A small command line chatbot called littleDaisy.
 *
 * <p>Level-5 makes littleDaisy hard to crash: input the chatbot cannot act
 * on -- an unknown command, an empty description, a task number that does not
 * exist -- is answered with a friendly complaint instead of a stack trace,
 * and the conversation carries on. Deleting tasks comes in Level-6.
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

    /** Word the user types to add a task with no date attached. */
    private static final String COMMAND_TODO = "todo";

    /** Word the user types to add a task due by some time. */
    private static final String COMMAND_DEADLINE = "deadline";

    /** Word the user types to add a task spanning two times. */
    private static final String COMMAND_EVENT = "event";

    /** Separator introducing the due time of a {@code deadline}. */
    private static final String OPTION_BY = " /by ";

    /** Separator introducing the start time of an {@code event}. */
    private static final String OPTION_FROM = " /from ";

    /** Separator introducing the end time of an {@code event}. */
    private static final String OPTION_TO = " /to ";

    /** Largest number of tasks one conversation can hold. */
    private static final int MAX_TASKS = 100;

    /** Line printed above the task list. */
    private static final String LIST_HEADER = "Here are the tasks in your list:";

    /** Line confirming a {@code mark}. */
    private static final String MESSAGE_MARKED = "Nice! I've marked this task as done:";

    /** Line confirming an {@code unmark}. */
    private static final String MESSAGE_UNMARKED = "OK, I've marked this task as not done yet:";

    /** Line confirming that a task was added. */
    private static final String MESSAGE_ADDED = "Got it. I've added this task:";

    /** Prefix put in front of every complaint about bad input. */
    private static final String MESSAGE_OOPS = "OOPS!!! ";

    /** Complaint for a line whose first word is no known command. */
    private static final String ERROR_UNKNOWN_COMMAND =
            "I'm sorry, but I don't know what that means :-(";

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
     * one of them is done, and {@code todo}, {@code deadline} and {@code
     * event} store a new task. Anything else is an error, as is a command
     * whose arguments cannot be understood; errors are announced and the
     * conversation continues. Tasks live only for the length of one
     * conversation, since nothing is written to disk yet.
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
            }

            // Everything below can go wrong, so it runs under one try. The
            // moment any step throws, the rest of the command is skipped,
            // the complaint is shown, and the loop moves to the next line.
            try {
                if (command.equals(COMMAND_LIST)) {
                    showList(tasks, taskCount);
                } else if (command.equals(COMMAND_MARK)) {
                    int index = parseTaskNumber(parts, taskCount) - 1;
                    tasks[index].markAsDone();
                    say(MESSAGE_MARKED, "  " + tasks[index]);
                } else if (command.equals(COMMAND_UNMARK)) {
                    int index = parseTaskNumber(parts, taskCount) - 1;
                    tasks[index].markAsNotDone();
                    say(MESSAGE_UNMARKED, "  " + tasks[index]);
                } else if (command.equals(COMMAND_TODO)) {
                    tasks[taskCount] = new Todo(requireDescription(input, command));
                    taskCount++;
                    sayAdded(tasks[taskCount - 1], taskCount);
                } else if (command.equals(COMMAND_DEADLINE)) {
                    String[] pieces = requireDescription(input, command).split(OPTION_BY);
                    if (pieces.length < 2) {
                        throw new LittleDaisyException(
                                "A deadline needs \"" + OPTION_BY.trim() + " <time>\" after the description.");
                    }
                    tasks[taskCount] = new Deadline(pieces[0], pieces[1]);
                    taskCount++;
                    sayAdded(tasks[taskCount - 1], taskCount);
                } else if (command.equals(COMMAND_EVENT)) {
                    String[] pieces = requireDescription(input, command).split(OPTION_FROM);
                    if (pieces.length < 2) {
                        throw new LittleDaisyException(
                                "An event needs \"" + OPTION_FROM.trim() + " <start>\" after the description.");
                    }
                    String[] times = pieces[1].split(OPTION_TO);
                    if (times.length < 2) {
                        throw new LittleDaisyException(
                                "An event needs \"" + OPTION_TO.trim() + " <end>\" after the start time.");
                    }
                    tasks[taskCount] = new Event(pieces[0], times[0], times[1]);
                    taskCount++;
                    sayAdded(tasks[taskCount - 1], taskCount);
                } else {
                    throw new LittleDaisyException(ERROR_UNKNOWN_COMMAND);
                }
            } catch (LittleDaisyException e) {
                say(MESSAGE_OOPS + e.getMessage());
            }
        }
        scanner.close();
    }

    /**
     * Returns the description part of an add-task command, refusing to accept
     * an empty one.
     *
     * <p>Worked example of throwing: the caller does not need to know that an
     * empty description is reported via an exception -- it just calls this and
     * carries on with a guaranteed-usable description.
     *
     * @param input the whole line the user typed
     * @param command the command word, used to phrase the complaint
     * @return everything after the command word
     * @throws LittleDaisyException if there is nothing after the command word
     */
    private static String requireDescription(String input, String command)
            throws LittleDaisyException {
        if (input.equals(command)) {
            // The line IS the bare command word: nothing follows it.
            throw new LittleDaisyException(
                    "The description of a " + command + " cannot be empty.");
        }
        return argumentsOf(input);
    }

    /**
     * Reads the task number named in a mark or unmark command, refusing
     * numbers that do not point at a stored task.
     *
     * @param parts the user's line, already split on spaces
     * @param taskCount number of tasks stored so far
     * @return the number the user typed, counted from 1
     * @throws LittleDaisyException if the number is missing, is not a number,
     *     or does not point at a stored task
     */
    private static int parseTaskNumber(String[] parts, int taskCount)
            throws LittleDaisyException {
        if (parts.length < 2) {
            throw new LittleDaisyException("Please tell me which task number.");
        }
        int number;
        try {
            number = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new LittleDaisyException("A task number should be a number.");
        }
        if (number < 1 || number > taskCount) {
            throw new LittleDaisyException("Task " + number + " does not exist.");
        }
        return number;
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

    /**
     * Returns everything on a line after its first word.
     *
     * <p>{@code split(" ")} is no good here, because a description may itself
     * contain spaces: {@code todo borrow book} has to yield {@code borrow
     * book}, not just {@code borrow}.
     *
     * @param input the whole line the user typed
     * @return the line with its first word and the space after it removed
     */
    private static String argumentsOf(String input) {
        return input.substring(input.indexOf(' ') + 1);
    }

    /**
     * Confirms that a task was added, and says how long the list now is.
     *
     * @param task the task that was just added
     * @param taskCount number of tasks stored after the addition
     */
    private static void sayAdded(Task task, int taskCount) {
        say(MESSAGE_ADDED,
                "  " + task,
                "Now you have " + taskCount + " tasks in the list.");
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
