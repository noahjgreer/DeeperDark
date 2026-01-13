/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.loot.function;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.provider.nbt.LootNbtProvider;

public static class CopyNbtLootFunction.Builder
extends ConditionalLootFunction.Builder<CopyNbtLootFunction.Builder> {
    private final LootNbtProvider source;
    private final List<CopyNbtLootFunction.Operation> operations = Lists.newArrayList();

    CopyNbtLootFunction.Builder(LootNbtProvider source) {
        this.source = source;
    }

    public CopyNbtLootFunction.Builder withOperation(String source, String target, CopyNbtLootFunction.Operator operator) {
        try {
            this.operations.add(new CopyNbtLootFunction.Operation(NbtPathArgumentType.NbtPath.parse(source), NbtPathArgumentType.NbtPath.parse(target), operator));
        }
        catch (CommandSyntaxException commandSyntaxException) {
            throw new IllegalArgumentException(commandSyntaxException);
        }
        return this;
    }

    public CopyNbtLootFunction.Builder withOperation(String source, String target) {
        return this.withOperation(source, target, CopyNbtLootFunction.Operator.REPLACE);
    }

    @Override
    protected CopyNbtLootFunction.Builder getThisBuilder() {
        return this;
    }

    @Override
    public LootFunction build() {
        return new CopyNbtLootFunction(this.getConditions(), this.source, this.operations);
    }

    @Override
    protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
        return this.getThisBuilder();
    }
}
