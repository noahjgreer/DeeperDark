/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.NbtScanner;

class NbtFloat.1
implements NbtType.OfFixedSize<NbtFloat> {
    NbtFloat.1() {
    }

    @Override
    public NbtFloat read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
        return NbtFloat.of(NbtFloat.1.readFloat(dataInput, nbtSizeTracker));
    }

    @Override
    public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
        return visitor.visitFloat(NbtFloat.1.readFloat(input, tracker));
    }

    private static float readFloat(DataInput input, NbtSizeTracker tracker) throws IOException {
        tracker.add(12L);
        return input.readFloat();
    }

    @Override
    public int getSizeInBytes() {
        return 4;
    }

    @Override
    public String getCrashReportName() {
        return "FLOAT";
    }

    @Override
    public String getCommandFeedbackName() {
        return "TAG_Float";
    }

    @Override
    public /* synthetic */ NbtElement read(DataInput input, NbtSizeTracker tracker) throws IOException {
        return this.read(input, tracker);
    }
}
