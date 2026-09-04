package littledaisy;

import java.nio.file.Path;

import littledaisy.command.ParsedCommand;
import littledaisy.command.Parser;
import littledaisy.exception.LittleDaisyException;
import littledaisy.storage.Storage;
import littledaisy.task.Task;
import littledaisy.task.TaskList;
import littledaisy.ui.Ui;

/**
 * Coordinates littleDaisy's user interface, command parser, task list, and
 * persistent storage.
 */
public class LittleDaisy {
    /** Relative, platform-independent location of the saved task list. */
    private static final Path DATA_FILE = Path.of("data", "littleDaisy.txt");

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final String loadingError;

    /** Creates an application backed by the normal data-file location. */
    public LittleDaisy() {
        this(DATA_FILE);
    }

    /**
     * Creates an application backed by the specified data file.
     *
     * @param filePath location used to load and save tasks
     */
    public LittleDaisy(Path filePath) {
        ui = new Ui();
        storage = new Storage(filePath);

        TaskList loadedTasks;
        String loadFailure = null;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (LittleDaisyException e) {
            loadedTasks = new TaskList();
            loadFailure = e.getMessage();
        }
        tasks = loadedTasks;
        loadingError = loadFailure;
    }

    /**
     * Starts littleDaisy using its normal data-file location.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        new LittleDaisy().run();
    }

    /** Reads and executes commands until the user exits or input ends. */
    public void run() {
        ui.showWelcome();
        if (loadingError != null) {
            ui.showError(loadingError);
        }

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String input = ui.readCommand();
            ParsedCommand parsed = Parser.parse(input);
            isExit = parsed.command() == littledaisy.command.Command.BYE;
            ui.showResponse(getResponse(parsed));
        }
        if (!isExit) {
            ui.showGoodbye();
        }
        ui.close();
    }

    /**
     * Returns the greeting to show when either interface starts.
     *
     * @return greeting, including any recoverable loading error
     */
    public String getWelcomeMessage() {
        String greeting = ui.getWelcomeMessage();
        if (loadingError == null) {
            return greeting;
        }
        return greeting + System.lineSeparator() + ui.getErrorMessage(loadingError);
    }

    /**
     * Executes one user command and returns the response for display.
     *
     * @param input raw command entered by the user
     * @return chatbot response, including validation errors
     */
    public String getResponse(String input) {
        return getResponse(Parser.parse(input));
    }

    /** Executes one parsed command and converts recoverable errors to responses. */
    private String getResponse(ParsedCommand parsed) {
        try {
            return execute(parsed);
        } catch (LittleDaisyException e) {
            return ui.getErrorMessage(e.getMessage());
        }
    }

    /** Executes one parsed command and returns its successful response. */
    private String execute(ParsedCommand parsed) throws LittleDaisyException {
        switch (parsed.command()) {
            case BYE:
                return ui.getGoodbyeMessage();
            case LIST:
                return ui.getListMessage(tasks);
            case MARK: {
                Task task = tasks.get(Parser.parseTaskIndex(parsed.arguments(), tasks.size()));
                task.markAsDone();
                storage.save(tasks);
                return ui.getMarkedMessage(task);
            }
            case UNMARK: {
                Task task = tasks.get(Parser.parseTaskIndex(parsed.arguments(), tasks.size()));
                task.markAsNotDone();
                storage.save(tasks);
                return ui.getUnmarkedMessage(task);
            }
            case DELETE: {
                int index = Parser.parseTaskIndex(parsed.arguments(), tasks.size());
                Task removed = tasks.delete(index);
                storage.save(tasks);
                return ui.getDeletedMessage(removed, tasks.size());
            }
            case FIND:
                return ui.getMatchesMessage(
                        tasks.find(Parser.parseFindKeyword(parsed.arguments())));
            case TODO:
                return addAndSave(Parser.parseTodo(parsed.arguments()));
            case DEADLINE:
                return addAndSave(Parser.parseDeadline(parsed.arguments()));
            case EVENT:
                return addAndSave(Parser.parseEvent(parsed.arguments()));
            default:
                throw new LittleDaisyException(
                        "I'm sorry, but I don't know what that means :-(");
        }
    }

    /** Adds one task, persists the new list, and returns the change response. */
    private String addAndSave(Task task) throws LittleDaisyException {
        tasks.add(task);
        storage.save(tasks);
        return ui.getAddedMessage(task, tasks.size());
    }
}
