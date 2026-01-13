/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.NbtScanner;

class NbtShort.1
implements NbtType.OfFixedSize<NbtShort> {
    NbtShort.1() {
    }

    @Override
    public NbtShort read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
        return NbtShort.of(NbtShort.1.readShort(dataInput, nbtSizeTracker));
    }

    @Override
    public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
        return visitor.visitShort(NbtShort.1.readShort(input, tracker));
    }

    private static short readShort(DataInput input, NbtSizeTracker tracker) throws IOException {
        tracker.add(10L);
        return input.readShort();
    }

    @Override
    public int getSizeInBytes() {
        return 2;
    }

    @Override
    public String getCrashReportName() {
        return "SHORT";
    }

    @Override
    public String getCommandFeedbackName() {
        return "TAG_Short";
    }

    @Override
    public /* synthetic */ NbtElement read(DataInput input, NbtSizeTracker tracker) throws IOException {
        return this.read(input, tracker);
    }
}
