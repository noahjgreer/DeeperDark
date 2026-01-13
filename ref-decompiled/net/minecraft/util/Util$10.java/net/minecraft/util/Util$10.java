/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.function.BooleanSupplier;

static class Util.10
implements BooleanSupplier {
    final /* synthetic */ Path field_37274;

    Util.10(Path path) {
        this.field_37274 = path;
    }

    @Override
    public boolean getAsBoolean() {
        return Files.isRegularFile(this.field_37274, new LinkOption[0]);
    }

    public String toString() {
        return "verify that " + String.valueOf(this.field_37274) + " is present";
    }
}
