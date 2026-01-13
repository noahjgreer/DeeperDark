/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.provider.nbt;

import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootEntityValueSource;
import net.minecraft.nbt.NbtElement;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.util.context.ContextParameter;

record ContextLootNbtProvider.EntityTarget(ContextParameter<? extends Entity> contextParam) implements LootEntityValueSource.ContextComponentBased<Entity, NbtElement>
{
    @Override
    public NbtElement get(Entity entity) {
        return NbtPredicate.entityToNbt(entity);
    }
}
