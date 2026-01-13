/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.function.BooleanSupplier;

static class Util.9
implements BooleanSupplier {
    final /* synthetic */ Path field_37251;

    Util.9(Path path) {
        this.field_37251 = path;
    }

    @Override
    public boolean getAsBoolean() {
        return !Files.exists(this.field_37251, new LinkOption[0]);
    }

    public String toString() {
        return "verify that " + String.valueOf(this.field_37251) + " is deleted";
    }
}
