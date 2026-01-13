/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataOutput;
import java.io.IOException;
import java.io.UTFDataFormatException;
import net.minecraft.util.DelegatingDataOutput;
import net.minecraft.util.Util;

public static class NbtIo.InvalidUtfSkippingDataOutput
extends DelegatingDataOutput {
    public NbtIo.InvalidUtfSkippingDataOutput(DataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void writeUTF(String string) throws IOException {
        try {
            super.writeUTF(string);
        }
        catch (UTFDataFormatException uTFDataFormatException) {
            Util.logErrorOrPause("Failed to write NBT String", uTFDataFormatException);
            super.writeUTF("");
        }
    }
}
