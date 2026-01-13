/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.NbtScanner;

class NbtLong.1
implements NbtType.OfFixedSize<NbtLong> {
    NbtLong.1() {
    }

    @Override
    public NbtLong read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
        return NbtLong.of(NbtLong.1.readLong(dataInput, nbtSizeTracker));
    }

    @Override
    public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
        return visitor.visitLong(NbtLong.1.readLong(input, tracker));
    }

    private static long readLong(DataInput input, NbtSizeTracker tracker) throws IOException {
        tracker.add(16L);
        return input.readLong();
    }

    @Override
    public int getSizeInBytes() {
        return 8;
    }

    @Override
    public String getCrashReportName() {
        return "LONG";
    }

    @Override
    public String getCommandFeedbackName() {
        return "TAG_Long";
    }

    @Override
    public /* synthetic */ NbtElement read(DataInput input, NbtSizeTracker tracker) throws IOException {
        return this.read(input, tracker);
    }
}
