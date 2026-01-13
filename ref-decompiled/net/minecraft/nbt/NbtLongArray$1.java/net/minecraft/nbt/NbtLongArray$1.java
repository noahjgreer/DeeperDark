/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.NbtScanner;

class NbtLongArray.1
implements NbtType.OfVariableSize<NbtLongArray> {
    NbtLongArray.1() {
    }

    @Override
    public NbtLongArray read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
        return new NbtLongArray(NbtLongArray.1.readLongArray(dataInput, nbtSizeTracker));
    }

    @Override
    public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
        return visitor.visitLongArray(NbtLongArray.1.readLongArray(input, tracker));
    }

    private static long[] readLongArray(DataInput input, NbtSizeTracker tracker) throws IOException {
        tracker.add(24L);
        int i = input.readInt();
        tracker.add(8L, i);
        long[] ls = new long[i];
        for (int j = 0; j < i; ++j) {
            ls[j] = input.readLong();
        }
        return ls;
    }

    @Override
    public void skip(DataInput input, NbtSizeTracker tracker) throws IOException {
        input.skipBytes(input.readInt() * 8);
    }

    @Override
    public String getCrashReportName() {
        return "LONG[]";
    }

    @Override
    public String getCommandFeedbackName() {
        return "TAG_Long_Array";
    }

    @Override
    public /* synthetic */ NbtElement read(DataInput input, NbtSizeTracker tracker) throws IOException {
        return this.read(input, tracker);
    }
}
