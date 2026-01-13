/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.logging;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.logging.LogFileCompressor;

public static class LogFileCompressor.LogFileIterable
implements Iterable<LogFileCompressor.LogFile> {
    private final List<LogFileCompressor.LogFile> logs;

    LogFileCompressor.LogFileIterable(List<LogFileCompressor.LogFile> logs) {
        this.logs = new ArrayList<LogFileCompressor.LogFile>(logs);
    }

    public LogFileCompressor.LogFileIterable removeExpired(LocalDate currentDate, int retentionDays) {
        this.logs.removeIf(log -> {
            LogFileCompressor.LogId logId = log.id();
            LocalDate localDate2 = logId.date().plusDays(retentionDays);
            if (!currentDate.isBefore(localDate2)) {
                try {
                    Files.delete(log.path());
                    return true;
                }
                catch (IOException iOException) {
                    LOGGER.warn("Failed to delete expired event log file: {}", (Object)log.path(), (Object)iOException);
                }
            }
            return false;
        });
        return this;
    }

    public LogFileCompressor.LogFileIterable compressAll() {
        ListIterator<LogFileCompressor.LogFile> listIterator = this.logs.listIterator();
        while (listIterator.hasNext()) {
            LogFileCompressor.LogFile logFile = listIterator.next();
            try {
                listIterator.set(logFile.compress());
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to compress event log file: {}", (Object)logFile.path(), (Object)iOException);
            }
        }
        return this;
    }

    @Override
    public Iterator<LogFileCompressor.LogFile> iterator() {
        return this.logs.iterator();
    }

    public Stream<LogFileCompressor.LogFile> stream() {
        return this.logs.stream();
    }

    public Set<LogFileCompressor.LogId> toIdSet() {
        return this.logs.stream().map(LogFileCompressor.LogFile::id).collect(Collectors.toSet());
    }
}
