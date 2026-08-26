/** Converts raw command lines into commands and domain objects. */
public final class Parser {
    private static final String OPTION_BY = " /by ";
    private static final String OPTION_FROM = " /from ";
    private static final String OPTION_TO = " /to ";

    private Parser() {
    }

    /** Separates a command word from the rest of its input line. */
    public static ParsedCommand parse(String input) {
        String trimmed = input.trim();
        int firstSpace = trimmed.indexOf(' ');
        String word = firstSpace < 0 ? trimmed : trimmed.substring(0, firstSpace);
        String arguments = firstSpace < 0 ? "" : trimmed.substring(firstSpace + 1).trim();
        return new ParsedCommand(Command.of(word), arguments);
    }

    /** Parses and validates a user-facing task number as a zero-based index. */
    public static int parseTaskIndex(String arguments, int taskCount)
            throws LittleDaisyException {
        if (arguments.isBlank()) {
            throw new LittleDaisyException("Please tell me which task number.");
        }

        String numberText = arguments.split("\\s+", 2)[0];
        int number;
        try {
            number = Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            throw new LittleDaisyException("A task number should be a number.");
        }
        if (number < 1 || number > taskCount) {
            throw new LittleDaisyException("Task " + number + " does not exist.");
        }
        return number - 1;
    }

    /** Creates a todo from its command arguments. */
    public static Todo parseTodo(String arguments) throws LittleDaisyException {
        return new Todo(requireDescription(arguments, "todo"));
    }

    /** Creates a deadline from its description and {@code /by} date. */
    public static Deadline parseDeadline(String arguments) throws LittleDaisyException {
        String[] pieces = splitRequired(arguments, OPTION_BY,
                "A deadline needs \"/by <date>\" after the description.");
        return Deadline.fromInput(pieces[0], pieces[1]);
    }

    /** Creates an event from its description, start, and end. */
    public static Event parseEvent(String arguments) throws LittleDaisyException {
        String[] descriptionAndTimes = splitRequired(arguments, OPTION_FROM,
                "An event needs \"/from <start>\" after the description.");
        String[] times = splitRequired(descriptionAndTimes[1], OPTION_TO,
                "An event needs \"/to <end>\" after the start time.");
        return new Event(descriptionAndTimes[0], times[0], times[1]);
    }

    /** Rejects an add-task command with no description. */
    private static String requireDescription(String arguments, String command)
            throws LittleDaisyException {
        if (arguments.isBlank()) {
            throw new LittleDaisyException(
                    "The description of a " + command + " cannot be empty.");
        }
        return arguments;
    }

    /** Splits around one required option and rejects missing values. */
    private static String[] splitRequired(String input, String separator, String errorMessage)
            throws LittleDaisyException {
        int separatorIndex = input.indexOf(separator);
        if (separatorIndex <= 0) {
            throw new LittleDaisyException(errorMessage);
        }
        String before = input.substring(0, separatorIndex).trim();
        String after = input.substring(separatorIndex + separator.length()).trim();
        if (before.isEmpty() || after.isEmpty()) {
            throw new LittleDaisyException(errorMessage);
        }
        return new String[]{before, after};
    }
}
