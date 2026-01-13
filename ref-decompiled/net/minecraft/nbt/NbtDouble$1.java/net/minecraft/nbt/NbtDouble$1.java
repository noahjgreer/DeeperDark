/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.NbtScanner;

class NbtDouble.1
implements NbtType.OfFixedSize<NbtDouble> {
    NbtDouble.1() {
    }

    @Override
    public NbtDouble read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
        return NbtDouble.of(NbtDouble.1.readDouble(dataInput, nbtSizeTracker));
    }

    @Override
    public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
        return visitor.visitDouble(NbtDouble.1.readDouble(input, tracker));
    }

    private static double readDouble(DataInput input, NbtSizeTracker tracker) throws IOException {
        tracker.add(16L);
        return input.readDouble();
    }

    @Override
    public int getSizeInBytes() {
        return 8;
    }

    @Override
    public String getCrashReportName() {
        return "DOUBLE";
    }

    @Override
    public String getCommandFeedbackName() {
        return "TAG_Double";
    }

    @Override
    public /* synthetic */ NbtElement read(DataInput input, NbtSizeTracker tracker) throws IOException {
        return this.read(input, tracker);
    }
}
