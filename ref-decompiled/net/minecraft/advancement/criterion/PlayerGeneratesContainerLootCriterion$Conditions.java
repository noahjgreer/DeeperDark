/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.advancement.criterion;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.loot.LootTable;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.RegistryKey;

public record PlayerGeneratesContainerLootCriterion.Conditions(Optional<LootContextPredicate> player, RegistryKey<LootTable> lootTable) implements AbstractCriterion.Conditions
{
    public static final Codec<PlayerGeneratesContainerLootCriterion.Conditions> CODEC = RecordCodecBuilder.create((T instance) -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(PlayerGeneratesContainerLootCriterion.Conditions::player), (App)LootTable.TABLE_KEY.fieldOf("loot_table").forGetter(PlayerGeneratesContainerLootCriterion.Conditions::lootTable)).apply((Applicative)instance, PlayerGeneratesContainerLootCriterion.Conditions::new));

    public static AdvancementCriterion<PlayerGeneratesContainerLootCriterion.Conditions> create(RegistryKey<LootTable> registryKey) {
        return Criteria.PLAYER_GENERATES_CONTAINER_LOOT.create(new PlayerGeneratesContainerLootCriterion.Conditions(Optional.empty(), registryKey));
    }

    public boolean test(RegistryKey<LootTable> lootTable) {
        return this.lootTable == lootTable;
    }
}
