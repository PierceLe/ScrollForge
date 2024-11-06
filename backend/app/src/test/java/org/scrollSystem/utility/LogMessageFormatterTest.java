package org.scrollSystem.utility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.logging.LogRecord;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogMessageFormatterTest {

    private LogMessageFormatter logMessageFormatter;

    @BeforeEach
    public void setUp() {
        logMessageFormatter = new LogMessageFormatter();
    }

    @Test
    public void testFormatLogMessage() {
        String expectedMessage = "Test log message";
        LogRecord logRecord = new LogRecord(java.util.logging.Level.INFO, expectedMessage);
        String formattedMessage = logMessageFormatter.format(logRecord);
        assertEquals(expectedMessage + System.lineSeparator(), formattedMessage);
    }
}
