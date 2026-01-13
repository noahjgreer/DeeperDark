/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.command.argument;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.nbt.NbtElement;

static interface NbtPathArgumentType.PathNode {
    public void get(NbtElement var1, List<NbtElement> var2);

    public void getOrInit(NbtElement var1, Supplier<NbtElement> var2, List<NbtElement> var3);

    public NbtElement init();

    public int set(NbtElement var1, Supplier<NbtElement> var2);

    public int clear(NbtElement var1);

    default public List<NbtElement> get(List<NbtElement> elements) {
        return this.process(elements, this::get);
    }

    default public List<NbtElement> getOrInit(List<NbtElement> elements, Supplier<NbtElement> supplier) {
        return this.process(elements, (current, results) -> this.getOrInit((NbtElement)current, supplier, (List<NbtElement>)results));
    }

    default public List<NbtElement> process(List<NbtElement> elements, BiConsumer<NbtElement, List<NbtElement>> action) {
        ArrayList list = Lists.newArrayList();
        for (NbtElement nbtElement : elements) {
            action.accept(nbtElement, list);
        }
        return list;
    }
}
