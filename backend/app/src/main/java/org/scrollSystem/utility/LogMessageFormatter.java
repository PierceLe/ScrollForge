package org.scrollSystem.utility;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogMessageFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        // Only return the log message
        return record.getMessage() + System.lineSeparator();
    }
}
