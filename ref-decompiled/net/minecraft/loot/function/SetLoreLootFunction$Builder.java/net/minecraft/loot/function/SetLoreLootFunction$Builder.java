/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.SetLoreLootFunction;
import net.minecraft.text.Text;
import net.minecraft.util.collection.ListOperation;

public static class SetLoreLootFunction.Builder
extends ConditionalLootFunction.Builder<SetLoreLootFunction.Builder> {
    private Optional<LootContext.EntityReference> target = Optional.empty();
    private final ImmutableList.Builder<Text> lore = ImmutableList.builder();
    private ListOperation operation = ListOperation.Append.INSTANCE;

    public SetLoreLootFunction.Builder operation(ListOperation operation) {
        this.operation = operation;
        return this;
    }

    public SetLoreLootFunction.Builder target(LootContext.EntityReference target) {
        this.target = Optional.of(target);
        return this;
    }

    public SetLoreLootFunction.Builder lore(Text lore) {
        this.lore.add((Object)lore);
        return this;
    }

    @Override
    protected SetLoreLootFunction.Builder getThisBuilder() {
        return this;
    }

    @Override
    public LootFunction build() {
        return new SetLoreLootFunction(this.getConditions(), (List<Text>)this.lore.build(), this.operation, this.target);
    }

    @Override
    protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
        return this.getThisBuilder();
    }
}
