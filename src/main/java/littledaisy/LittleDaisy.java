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
        new LittleDaisy(DATA_FILE).run();
    }

    /** Reads and executes commands until the user exits or input ends. */
    public void run() {
        ui.showWelcome();
        if (loadingError != null) {
            ui.showError(loadingError);
        }

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            ParsedCommand parsed = Parser.parse(ui.readCommand());
            try {
                isExit = execute(parsed);
            } catch (LittleDaisyException e) {
                ui.showError(e.getMessage());
            }
        }
        ui.showGoodbye();
        ui.close();
    }

    /** Executes one parsed command and returns whether it exits the app. */
    private boolean execute(ParsedCommand parsed) throws LittleDaisyException {
        switch (parsed.command()) {
            case BYE:
                return true;
            case LIST:
                ui.showList(tasks);
                break;
            case MARK: {
                Task task = tasks.get(Parser.parseTaskIndex(parsed.arguments(), tasks.size()));
                task.markAsDone();
                storage.save(tasks);
                ui.showMarked(task);
                break;
            }
            case UNMARK: {
                Task task = tasks.get(Parser.parseTaskIndex(parsed.arguments(), tasks.size()));
                task.markAsNotDone();
                storage.save(tasks);
                ui.showUnmarked(task);
                break;
            }
            case DELETE: {
                int index = Parser.parseTaskIndex(parsed.arguments(), tasks.size());
                Task removed = tasks.delete(index);
                storage.save(tasks);
                ui.showDeleted(removed, tasks.size());
                break;
            }
            case FIND:
                ui.showMatches(tasks.find(Parser.parseFindKeyword(parsed.arguments())));
                break;
            case TODO:
                addAndSave(Parser.parseTodo(parsed.arguments()));
                break;
            case DEADLINE:
                addAndSave(Parser.parseDeadline(parsed.arguments()));
                break;
            case EVENT:
                addAndSave(Parser.parseEvent(parsed.arguments()));
                break;
            default:
                throw new LittleDaisyException(
                        "I'm sorry, but I don't know what that means :-(");
        }
        return false;
    }

    /** Adds one task, persists the new list, and reports the change. */
    private void addAndSave(Task task) throws LittleDaisyException {
        tasks.add(task);
        storage.save(tasks);
        ui.showAdded(task, tasks.size());
    }
}
