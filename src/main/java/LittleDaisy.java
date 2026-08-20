import java.util.Scanner;

/**
 * A small command line chatbot called littleDaisy.
 *
 * <p>Level-1 adds a conversation loop: littleDaisy repeats back whatever the
 * user types, and stops when the user types {@code bye}. Remembering what was
 * typed comes in Level-2.
 */
public class LittleDaisy {
    /** Name the chatbot introduces itself with. */
    private static final String BOT_NAME = "littleDaisy";

    /** Word the user types to end the conversation. */
    private static final String COMMAND_BYE = "bye";

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
     * Reads one line at a time and echoes it back, until the user types
     * {@code bye}.
     *
     * <p>The loop is guarded by {@code hasNextLine()} rather than looping
     * forever, so that input which ends without a "bye" -- a piped file, or
     * Ctrl-D -- stops the loop instead of throwing NoSuchElementException.
     */
    private static void chat() {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (input.equals(COMMAND_BYE)) {
                break;
            }
            say(input);
        }
        scanner.close();
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
