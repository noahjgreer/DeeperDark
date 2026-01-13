/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.NbtScanner;

class NbtByte.1
implements NbtType.OfFixedSize<NbtByte> {
    NbtByte.1() {
    }

    @Override
    public NbtByte read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
        return NbtByte.of(NbtByte.1.readByte(dataInput, nbtSizeTracker));
    }

    @Override
    public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
        return visitor.visitByte(NbtByte.1.readByte(input, tracker));
    }

    private static byte readByte(DataInput input, NbtSizeTracker tracker) throws IOException {
        tracker.add(9L);
        return input.readByte();
    }

    @Override
    public int getSizeInBytes() {
        return 1;
    }

    @Override
    public String getCrashReportName() {
        return "BYTE";
    }

    @Override
    public String getCommandFeedbackName() {
        return "TAG_Byte";
    }

    @Override
    public /* synthetic */ NbtElement read(DataInput input, NbtSizeTracker tracker) throws IOException {
        return this.read(input, tracker);
    }
}
