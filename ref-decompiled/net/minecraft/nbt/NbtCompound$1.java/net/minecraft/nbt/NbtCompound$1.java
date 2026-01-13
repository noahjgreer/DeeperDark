/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.nbt;

import com.google.common.collect.Maps;
import java.io.DataInput;
import java.io.IOException;
import java.util.HashMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.NbtTypes;
import net.minecraft.nbt.scanner.NbtScanner;

class NbtCompound.1
implements NbtType.OfVariableSize<NbtCompound> {
    NbtCompound.1() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public NbtCompound read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
        nbtSizeTracker.pushStack();
        try {
            NbtCompound nbtCompound = NbtCompound.1.readCompound(dataInput, nbtSizeTracker);
            return nbtCompound;
        }
        finally {
            nbtSizeTracker.popStack();
        }
    }

    private static NbtCompound readCompound(DataInput input, NbtSizeTracker tracker) throws IOException {
        byte b;
        tracker.add(48L);
        HashMap map = Maps.newHashMap();
        while ((b = input.readByte()) != 0) {
            NbtElement nbtElement;
            String string = NbtCompound.1.readString(input, tracker);
            if (map.put(string, nbtElement = NbtCompound.read(NbtTypes.byId(b), string, input, tracker)) != null) continue;
            tracker.add(36L);
        }
        return new NbtCompound(map);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
        tracker.pushStack();
        try {
            NbtScanner.Result result = NbtCompound.1.scanCompound(input, visitor, tracker);
            return result;
        }
        finally {
            tracker.popStack();
        }
    }

    private static NbtScanner.Result scanCompound(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
        byte b;
        tracker.add(48L);
        block13: while ((b = input.readByte()) != 0) {
            NbtType<?> nbtType = NbtTypes.byId(b);
            switch (visitor.visitSubNbtType(nbtType)) {
                case HALT: {
                    return NbtScanner.Result.HALT;
                }
                case BREAK: {
                    NbtString.skip(input);
                    nbtType.skip(input, tracker);
                    break block13;
                }
                case SKIP: {
                    NbtString.skip(input);
                    nbtType.skip(input, tracker);
                    continue block13;
                }
                default: {
                    String string = NbtCompound.1.readString(input, tracker);
                    switch (visitor.startSubNbt(nbtType, string)) {
                        case HALT: {
                            return NbtScanner.Result.HALT;
                        }
                        case BREAK: {
                            nbtType.skip(input, tracker);
                            break block13;
                        }
                        case SKIP: {
                            nbtType.skip(input, tracker);
                            continue block13;
                        }
                    }
                    tracker.add(36L);
                    switch (nbtType.doAccept(input, visitor, tracker)) {
                        case HALT: {
                            return NbtScanner.Result.HALT;
                        }
                    }
                    continue block13;
                }
            }
        }
        if (b != 0) {
            while ((b = input.readByte()) != 0) {
                NbtString.skip(input);
                NbtTypes.byId(b).skip(input, tracker);
            }
        }
        return visitor.endNested();
    }

    private static String readString(DataInput input, NbtSizeTracker tracker) throws IOException {
        String string = input.readUTF();
        tracker.add(28L);
        tracker.add(2L, string.length());
        return string;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void skip(DataInput input, NbtSizeTracker tracker) throws IOException {
        tracker.pushStack();
        try {
            byte b;
            while ((b = input.readByte()) != 0) {
                NbtString.skip(input);
                NbtTypes.byId(b).skip(input, tracker);
            }
        }
        finally {
            tracker.popStack();
        }
    }

    @Override
    public String getCrashReportName() {
        return "COMPOUND";
    }

    @Override
    public String getCommandFeedbackName() {
        return "TAG_Compound";
    }

    @Override
    public /* synthetic */ NbtElement read(DataInput input, NbtSizeTracker tracker) throws IOException {
        return this.read(input, tracker);
    }
}
