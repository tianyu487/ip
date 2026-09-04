package littledaisy;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LittleDaisyTest {
    @TempDir
    Path tempDirectory;

    @Test
    void getResponse_addListFindAndError_responsesReflectCommands() {
        LittleDaisy littleDaisy = new LittleDaisy(tempDirectory.resolve("tasks.txt"));

        assertTrue(littleDaisy.getResponse("todo Finish GUI").contains("Finish GUI"));
        assertTrue(littleDaisy.getResponse("list").contains("1.[T][ ] Finish GUI"));
        assertTrue(littleDaisy.getResponse("find gui").contains("Finish GUI"));
        assertTrue(littleDaisy.getResponse("dance").startsWith("OOPS!!!"));
    }

    @Test
    void getResponse_savedTask_newInstanceLoadsTask() {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        LittleDaisy firstInstance = new LittleDaisy(dataFile);
        firstInstance.getResponse("deadline submit /by 2026-09-04");

        LittleDaisy reloadedInstance = new LittleDaisy(dataFile);

        assertTrue(reloadedInstance.getResponse("list").contains("submit (by: Sep 4 2026)"));
    }
}
