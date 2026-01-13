/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.NbtScanner;

class NbtString.1
implements NbtType.OfVariableSize<NbtString> {
    NbtString.1() {
    }

    @Override
    public NbtString read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
        return NbtString.of(NbtString.1.readString(dataInput, nbtSizeTracker));
    }

    @Override
    public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
        return visitor.visitString(NbtString.1.readString(input, tracker));
    }

    private static String readString(DataInput input, NbtSizeTracker tracker) throws IOException {
        tracker.add(36L);
        String string = input.readUTF();
        tracker.add(2L, string.length());
        return string;
    }

    @Override
    public void skip(DataInput input, NbtSizeTracker tracker) throws IOException {
        NbtString.skip(input);
    }

    @Override
    public String getCrashReportName() {
        return "STRING";
    }

    @Override
    public String getCommandFeedbackName() {
        return "TAG_String";
    }

    @Override
    public /* synthetic */ NbtElement read(DataInput input, NbtSizeTracker tracker) throws IOException {
        return this.read(input, tracker);
    }
}
