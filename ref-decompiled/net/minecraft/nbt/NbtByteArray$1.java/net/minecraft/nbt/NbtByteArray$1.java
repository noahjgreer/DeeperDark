/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.NbtScanner;

class NbtByteArray.1
implements NbtType.OfVariableSize<NbtByteArray> {
    NbtByteArray.1() {
    }

    @Override
    public NbtByteArray read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
        return new NbtByteArray(NbtByteArray.1.readByteArray(dataInput, nbtSizeTracker));
    }

    @Override
    public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
        return visitor.visitByteArray(NbtByteArray.1.readByteArray(input, tracker));
    }

    private static byte[] readByteArray(DataInput input, NbtSizeTracker tracker) throws IOException {
        tracker.add(24L);
        int i = input.readInt();
        tracker.add(1L, i);
        byte[] bs = new byte[i];
        input.readFully(bs);
        return bs;
    }

    @Override
    public void skip(DataInput input, NbtSizeTracker tracker) throws IOException {
        input.skipBytes(input.readInt() * 1);
    }

    @Override
    public String getCrashReportName() {
        return "BYTE[]";
    }

    @Override
    public String getCommandFeedbackName() {
        return "TAG_Byte_Array";
    }

    @Override
    public /* synthetic */ NbtElement read(DataInput input, NbtSizeTracker tracker) throws IOException {
        return this.read(input, tracker);
    }
}
