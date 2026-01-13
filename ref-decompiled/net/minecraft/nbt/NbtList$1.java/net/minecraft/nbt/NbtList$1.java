/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.nbt.InvalidNbtException;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.NbtTypes;
import net.minecraft.nbt.scanner.NbtScanner;

class NbtList.1
implements NbtType.OfVariableSize<NbtList> {
    NbtList.1() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public NbtList read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
        nbtSizeTracker.pushStack();
        try {
            NbtList nbtList = NbtList.1.readList(dataInput, nbtSizeTracker);
            return nbtList;
        }
        finally {
            nbtSizeTracker.popStack();
        }
    }

    private static NbtList readList(DataInput input, NbtSizeTracker tracker) throws IOException {
        tracker.add(36L);
        byte b = input.readByte();
        int i = NbtList.1.readListLength(input);
        if (b == 0 && i > 0) {
            throw new InvalidNbtException("Missing type on ListTag");
        }
        tracker.add(4L, i);
        NbtType<?> nbtType = NbtTypes.byId(b);
        NbtList nbtList = new NbtList(new ArrayList<NbtElement>(i));
        for (int j = 0; j < i; ++j) {
            nbtList.unwrapAndAdd((NbtElement)nbtType.read(input, tracker));
        }
        return nbtList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
        tracker.pushStack();
        try {
            NbtScanner.Result result = NbtList.1.scanList(input, visitor, tracker);
            return result;
        }
        finally {
            tracker.popStack();
        }
    }

    /*
     * Exception decompiling
     */
    private static NbtScanner.Result scanList(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [8[CASE], 4[SWITCH]], but top level block is 9[SWITCH]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doClass(Driver.java:84)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:78)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private static int readListLength(DataInput input) throws IOException {
        int i = input.readInt();
        if (i < 0) {
            throw new InvalidNbtException("ListTag length cannot be negative: " + i);
        }
        return i;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void skip(DataInput input, NbtSizeTracker tracker) throws IOException {
        tracker.pushStack();
        try {
            NbtType<?> nbtType = NbtTypes.byId(input.readByte());
            int i = input.readInt();
            nbtType.skip(input, i, tracker);
        }
        finally {
            tracker.popStack();
        }
    }

    @Override
    public String getCrashReportName() {
        return "LIST";
    }

    @Override
    public String getCommandFeedbackName() {
        return "TAG_List";
    }

    @Override
    public /* synthetic */ NbtElement read(DataInput input, NbtSizeTracker tracker) throws IOException {
        return this.read(input, tracker);
    }
}
