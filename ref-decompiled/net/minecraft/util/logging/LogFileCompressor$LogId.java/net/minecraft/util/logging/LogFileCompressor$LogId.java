/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util.logging;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.jspecify.annotations.Nullable;

public record LogFileCompressor.LogId(LocalDate date, int index) {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    public static @Nullable LogFileCompressor.LogId fromFileName(String fileName) {
        int i = fileName.indexOf("-");
        if (i == -1) {
            return null;
        }
        String string = fileName.substring(0, i);
        String string2 = fileName.substring(i + 1);
        try {
            return new LogFileCompressor.LogId(LocalDate.parse(string, DATE_TIME_FORMATTER), Integer.parseInt(string2));
        }
        catch (NumberFormatException | DateTimeParseException runtimeException) {
            return null;
        }
    }

    @Override
    public String toString() {
        return DATE_TIME_FORMATTER.format(this.date) + "-" + this.index;
    }

    public String getFileName(String extension) {
        return String.valueOf(this) + extension;
    }
}
