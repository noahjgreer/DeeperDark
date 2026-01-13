/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.NbtScanner;

class NbtIntArray.1
implements NbtType.OfVariableSize<NbtIntArray> {
    NbtIntArray.1() {
    }

    @Override
    public NbtIntArray read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
        return new NbtIntArray(NbtIntArray.1.readIntArray(dataInput, nbtSizeTracker));
    }

    @Override
    public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
        return visitor.visitIntArray(NbtIntArray.1.readIntArray(input, tracker));
    }

    private static int[] readIntArray(DataInput input, NbtSizeTracker tracker) throws IOException {
        tracker.add(24L);
        int i = input.readInt();
        tracker.add(4L, i);
        int[] is = new int[i];
        for (int j = 0; j < i; ++j) {
            is[j] = input.readInt();
        }
        return is;
    }

    @Override
    public void skip(DataInput input, NbtSizeTracker tracker) throws IOException {
        input.skipBytes(input.readInt() * 4);
    }

    @Override
    public String getCrashReportName() {
        return "INT[]";
    }

    @Override
    public String getCommandFeedbackName() {
        return "TAG_Int_Array";
    }

    @Override
    public /* synthetic */ NbtElement read(DataInput input, NbtSizeTracker tracker) throws IOException {
        return this.read(input, tracker);
    }
}
