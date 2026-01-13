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
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public record ChangedDimensionCriterion.Conditions(Optional<LootContextPredicate> player, Optional<RegistryKey<World>> from, Optional<RegistryKey<World>> to) implements AbstractCriterion.Conditions
{
    public static final Codec<ChangedDimensionCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(ChangedDimensionCriterion.Conditions::player), (App)RegistryKey.createCodec(RegistryKeys.WORLD).optionalFieldOf("from").forGetter(ChangedDimensionCriterion.Conditions::from), (App)RegistryKey.createCodec(RegistryKeys.WORLD).optionalFieldOf("to").forGetter(ChangedDimensionCriterion.Conditions::to)).apply((Applicative)instance, ChangedDimensionCriterion.Conditions::new));

    public static AdvancementCriterion<ChangedDimensionCriterion.Conditions> create() {
        return Criteria.CHANGED_DIMENSION.create(new ChangedDimensionCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.empty()));
    }

    public static AdvancementCriterion<ChangedDimensionCriterion.Conditions> create(RegistryKey<World> from, RegistryKey<World> to) {
        return Criteria.CHANGED_DIMENSION.create(new ChangedDimensionCriterion.Conditions(Optional.empty(), Optional.of(from), Optional.of(to)));
    }

    public static AdvancementCriterion<ChangedDimensionCriterion.Conditions> to(RegistryKey<World> to) {
        return Criteria.CHANGED_DIMENSION.create(new ChangedDimensionCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.of(to)));
    }

    public static AdvancementCriterion<ChangedDimensionCriterion.Conditions> from(RegistryKey<World> from) {
        return Criteria.CHANGED_DIMENSION.create(new ChangedDimensionCriterion.Conditions(Optional.empty(), Optional.of(from), Optional.empty()));
    }

    public boolean matches(RegistryKey<World> from, RegistryKey<World> to) {
        if (this.from.isPresent() && this.from.get() != from) {
            return false;
        }
        return !this.to.isPresent() || this.to.get() == to;
    }
}
