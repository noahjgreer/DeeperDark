/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.nbt.NbtType;

public static interface NbtType.OfVariableSize<T extends NbtElement>
extends NbtType<T> {
    @Override
    default public void skip(DataInput input, int count, NbtSizeTracker tracker) throws IOException {
        for (int i = 0; i < count; ++i) {
            this.skip(input, tracker);
        }
    }
}
