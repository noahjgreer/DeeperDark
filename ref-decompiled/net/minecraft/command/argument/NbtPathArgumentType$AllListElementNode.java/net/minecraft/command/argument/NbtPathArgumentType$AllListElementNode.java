/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 */
package net.minecraft.command.argument;

import com.google.common.collect.Iterables;
import java.lang.invoke.LambdaMetafactory;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

static class NbtPathArgumentType.AllListElementNode
implements NbtPathArgumentType.PathNode {
    public static final NbtPathArgumentType.AllListElementNode INSTANCE = new NbtPathArgumentType.AllListElementNode();

    private NbtPathArgumentType.AllListElementNode() {
    }

    @Override
    public void get(NbtElement current, List<NbtElement> results) {
        if (current instanceof AbstractNbtList) {
            AbstractNbtList abstractNbtList = (AbstractNbtList)current;
            Iterables.addAll(results, (Iterable)abstractNbtList);
        }
    }

    @Override
    public void getOrInit(NbtElement current, Supplier<NbtElement> source, List<NbtElement> results) {
        if (current instanceof AbstractNbtList) {
            AbstractNbtList abstractNbtList = (AbstractNbtList)current;
            if (abstractNbtList.isEmpty()) {
                NbtElement nbtElement = source.get();
                if (abstractNbtList.addElement(0, nbtElement)) {
                    results.add(nbtElement);
                }
            } else {
                Iterables.addAll(results, (Iterable)abstractNbtList);
            }
        }
    }

    @Override
    public NbtElement init() {
        return new NbtList();
    }

    @Override
    public int set(NbtElement current, Supplier<NbtElement> source) {
        if (current instanceof AbstractNbtList) {
            AbstractNbtList abstractNbtList = (AbstractNbtList)current;
            int i = abstractNbtList.size();
            if (i == 0) {
                abstractNbtList.addElement(0, source.get());
                return 1;
            }
            NbtElement nbtElement = source.get();
            int j = i - (int)abstractNbtList.stream().filter((Predicate<NbtElement>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Z, equals(java.lang.Object ), (Lnet/minecraft/nbt/NbtElement;)Z)((NbtElement)nbtElement)).count();
            if (j == 0) {
                return 0;
            }
            abstractNbtList.clear();
            if (!abstractNbtList.addElement(0, nbtElement)) {
                return 0;
            }
            for (int k = 1; k < i; ++k) {
                abstractNbtList.addElement(k, source.get());
            }
            return j;
        }
        return 0;
    }

    @Override
    public int clear(NbtElement current) {
        AbstractNbtList abstractNbtList;
        int i;
        if (current instanceof AbstractNbtList && (i = (abstractNbtList = (AbstractNbtList)current).size()) > 0) {
            abstractNbtList.clear();
            return i;
        }
        return 0;
    }
}
