/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.NbtScanner;

class NbtInt.1
implements NbtType.OfFixedSize<NbtInt> {
    NbtInt.1() {
    }

    @Override
    public NbtInt read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
        return NbtInt.of(NbtInt.1.readInt(dataInput, nbtSizeTracker));
    }

    @Override
    public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
        return visitor.visitInt(NbtInt.1.readInt(input, tracker));
    }

    private static int readInt(DataInput input, NbtSizeTracker tracker) throws IOException {
        tracker.add(12L);
        return input.readInt();
    }

    @Override
    public int getSizeInBytes() {
        return 4;
    }

    @Override
    public String getCrashReportName() {
        return "INT";
    }

    @Override
    public String getCommandFeedbackName() {
        return "TAG_Int";
    }

    @Override
    public /* synthetic */ NbtElement read(DataInput input, NbtSizeTracker tracker) throws IOException {
        return this.read(input, tracker);
    }
}
