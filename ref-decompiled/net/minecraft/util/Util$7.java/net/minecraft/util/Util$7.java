/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BooleanSupplier;

static class Util.7
implements BooleanSupplier {
    final /* synthetic */ Path field_26348;
    final /* synthetic */ Path field_26349;

    Util.7() {
        this.field_26348 = path;
        this.field_26349 = path2;
    }

    @Override
    public boolean getAsBoolean() {
        try {
            Files.move(this.field_26348, this.field_26349, new CopyOption[0]);
            return true;
        }
        catch (IOException iOException) {
            LOGGER.error("Failed to rename", (Throwable)iOException);
            return false;
        }
    }

    public String toString() {
        return "rename " + String.valueOf(this.field_26348) + " to " + String.valueOf(this.field_26349);
    }
}
