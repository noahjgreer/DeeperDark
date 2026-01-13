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

static class NbtType.1
implements NbtType<NbtEnd> {
    final /* synthetic */ int field_21047;

    NbtType.1(int i) {
        this.field_21047 = i;
    }

    private IOException createException() {
        return new IOException("Invalid tag id: " + this.field_21047);
    }

    @Override
    public NbtEnd read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
        throw this.createException();
    }

    @Override
    public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
        throw this.createException();
    }

    @Override
    public void skip(DataInput input, int count, NbtSizeTracker tracker) throws IOException {
        throw this.createException();
    }

    @Override
    public void skip(DataInput input, NbtSizeTracker tracker) throws IOException {
        throw this.createException();
    }

    @Override
    public String getCrashReportName() {
        return "INVALID[" + this.field_21047 + "]";
    }

    @Override
    public String getCommandFeedbackName() {
        return "UNKNOWN_" + this.field_21047;
    }

    @Override
    public /* synthetic */ NbtElement read(DataInput input, NbtSizeTracker tracker) throws IOException {
        return this.read(input, tracker);
    }
}
