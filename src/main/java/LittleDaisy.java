import java.util.ArrayList;
import java.util.Scanner;

/**
 * A small command line chatbot called littleDaisy.
 *
 * <p>The command words now live in the {@link Command} enum, and the command
 * loop dispatches with a switch over its constants: the compiler can then
 * see, in a way it never could with a chain of string comparisons, that every
 * command is handled somewhere.
 */
public class LittleDaisy {
    /** Name the chatbot introduces itself with. */
    private static final String BOT_NAME = "littleDaisy";

    /** Separator introducing the due time of a {@code deadline}. */
    private static final String OPTION_BY = " /by ";

    /** Separator introducing the start time of an {@code event}. */
    private static final String OPTION_FROM = " /from ";

    /** Separator introducing the end time of an {@code event}. */
    private static final String OPTION_TO = " /to ";

    /** Line printed above the task list. */
    private static final String LIST_HEADER = "Here are the tasks in your list:";

    /** Line confirming a {@code mark}. */
    private static final String MESSAGE_MARKED = "Nice! I've marked this task as done:";

    /** Line confirming an {@code unmark}. */
    private static final String MESSAGE_UNMARKED = "OK, I've marked this task as not done yet:";

    /** Line confirming that a task was added. */
    private static final String MESSAGE_ADDED = "Got it. I've added this task:";

    /** Line confirming that a task was removed. */
    private static final String MESSAGE_DELETED = "Noted. I've removed this task:";

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
        ArrayList<Task> tasks = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();

            // Commands now carry arguments ("mark 2"), so the whole line no
            // longer matches a command word. Split it and look at word one.
            String[] parts = input.split(" ");
            Command command = Command.of(parts[0]);

            if (command == Command.BYE) {
                break;
            }

            // Everything below can go wrong, so it runs under one try. The
            // moment any step throws, the rest of the command is skipped,
            // the complaint is shown, and the loop moves to the next line.
            try {
                switch (command) {
                case LIST:
                    showList(tasks);
                    break;
                case MARK: {
                    int index = parseTaskNumber(parts, tasks.size()) - 1;
                    tasks.get(index).markAsDone();
                    say(MESSAGE_MARKED, "  " + tasks.get(index));
                    break;
                }
                case UNMARK: {
                    int index = parseTaskNumber(parts, tasks.size()) - 1;
                    tasks.get(index).markAsNotDone();
                    say(MESSAGE_UNMARKED, "  " + tasks.get(index));
                    break;
                }
                case DELETE: {
                    int index = parseTaskNumber(parts, tasks.size()) - 1;
                    Task removed = tasks.remove(index);
                    sayTaskChange(MESSAGE_DELETED, removed, tasks.size());
                    break;
                }
                case TODO:
                    tasks.add(new Todo(requireDescription(input, parts[0])));
                    sayTaskChange(MESSAGE_ADDED, tasks.get(tasks.size() - 1), tasks.size());
                    break;
                case DEADLINE: {
                    String[] pieces = requireDescription(input, parts[0]).split(OPTION_BY);
                    if (pieces.length < 2) {
                        throw new LittleDaisyException(
                                "A deadline needs \"" + OPTION_BY.trim() + " <time>\" after the description.");
                    }
                    tasks.add(new Deadline(pieces[0], pieces[1]));
                    sayTaskChange(MESSAGE_ADDED, tasks.get(tasks.size() - 1), tasks.size());
                    break;
                }
                case EVENT: {
                    String[] pieces = requireDescription(input, parts[0]).split(OPTION_FROM);
                    if (pieces.length < 2) {
                        throw new LittleDaisyException(
                                "An event needs \"" + OPTION_FROM.trim() + " <start>\" after the description.");
                    }
                    String[] times = pieces[1].split(OPTION_TO);
                    if (times.length < 2) {
                        throw new LittleDaisyException(
                                "An event needs \"" + OPTION_TO.trim() + " <end>\" after the start time.");
                    }
                    tasks.add(new Event(pieces[0], times[0], times[1]));
                    sayTaskChange(MESSAGE_ADDED, tasks.get(tasks.size() - 1), tasks.size());
                    break;
                }
                default:
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
     * <p>The numbering shown to the user starts at 1 while the list index
     * starts at 0. The header takes up {@code lines[0]}, so task {@code i}
     * lands one slot further along again.
     *
     * @param tasks the tasks stored so far
     */
    private static void showList(ArrayList<Task> tasks) {
        String[] lines = new String[tasks.size() + 1];
        lines[0] = LIST_HEADER;
        for (int i = 0; i < tasks.size(); i++) {
            lines[i + 1] = (i + 1) + "." + tasks.get(i);
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
     * Confirms that a task was added or removed, and says how long the list
     * now is.
     *
     * @param message line announcing what happened, e.g. {@link #MESSAGE_ADDED}
     * @param task the task that was added or removed
     * @param taskCount number of tasks stored after the change
     */
    private static void sayTaskChange(String message, Task task, int taskCount) {
        say(message,
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
