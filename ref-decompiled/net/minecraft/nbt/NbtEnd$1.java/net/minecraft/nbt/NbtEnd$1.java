/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtEnd;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.NbtScanner;

class NbtEnd.1
implements NbtType<NbtEnd> {
    NbtEnd.1() {
    }

    @Override
    public NbtEnd read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) {
        nbtSizeTracker.add(8L);
        return INSTANCE;
    }

    @Override
    public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) {
        tracker.add(8L);
        return visitor.visitEnd();
    }

    @Override
    public void skip(DataInput input, int count, NbtSizeTracker tracker) {
    }

    @Override
    public void skip(DataInput input, NbtSizeTracker tracker) {
    }

    @Override
    public String getCrashReportName() {
        return "END";
    }

    @Override
    public String getCommandFeedbackName() {
        return "TAG_End";
    }

    @Override
    public /* synthetic */ NbtElement read(DataInput input, NbtSizeTracker tracker) throws IOException {
        return this.read(input, tracker);
    }
}
