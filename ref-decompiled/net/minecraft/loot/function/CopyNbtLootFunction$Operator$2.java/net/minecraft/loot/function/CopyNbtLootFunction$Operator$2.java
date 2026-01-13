/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.loot.function;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

final class CopyNbtLootFunction.Operator.2
extends CopyNbtLootFunction.Operator {
    CopyNbtLootFunction.Operator.2(String string2) {
    }

    @Override
    public void merge(NbtElement itemNbt, NbtPathArgumentType.NbtPath targetPath, List<NbtElement> sourceNbts) throws CommandSyntaxException {
        List<NbtElement> list = targetPath.getOrInit(itemNbt, NbtList::new);
        list.forEach(foundNbt -> {
            if (foundNbt instanceof NbtList) {
                sourceNbts.forEach(sourceNbt -> ((NbtList)foundNbt).add(sourceNbt.copy()));
            }
        });
    }
}
