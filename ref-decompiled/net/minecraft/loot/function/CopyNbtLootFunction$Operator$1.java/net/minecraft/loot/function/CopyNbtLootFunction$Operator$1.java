/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.loot.function;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.nbt.NbtElement;

final class CopyNbtLootFunction.Operator.1
extends CopyNbtLootFunction.Operator {
    CopyNbtLootFunction.Operator.1(String string2) {
    }

    @Override
    public void merge(NbtElement itemNbt, NbtPathArgumentType.NbtPath targetPath, List<NbtElement> sourceNbts) throws CommandSyntaxException {
        targetPath.put(itemNbt, (NbtElement)Iterables.getLast(sourceNbts));
    }
}
