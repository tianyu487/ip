/** A command word paired with the remaining text on its input line. */
public record ParsedCommand(Command command, String arguments) {
}
