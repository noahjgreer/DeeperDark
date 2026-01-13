/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BooleanSupplier;

static class Util.8
implements BooleanSupplier {
    final /* synthetic */ Path field_26352;

    Util.8(Path path) {
        this.field_26352 = path;
    }

    @Override
    public boolean getAsBoolean() {
        try {
            Files.deleteIfExists(this.field_26352);
            return true;
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to delete", (Throwable)iOException);
            return false;
        }
    }

    public String toString() {
        return "delete old " + String.valueOf(this.field_26352);
    }
}
