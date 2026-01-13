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
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;

public record ConstructBeaconCriterion.Conditions(Optional<LootContextPredicate> player, NumberRange.IntRange level) implements AbstractCriterion.Conditions
{
    public static final Codec<ConstructBeaconCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(ConstructBeaconCriterion.Conditions::player), (App)NumberRange.IntRange.CODEC.optionalFieldOf("level", (Object)NumberRange.IntRange.ANY).forGetter(ConstructBeaconCriterion.Conditions::level)).apply((Applicative)instance, ConstructBeaconCriterion.Conditions::new));

    public static AdvancementCriterion<ConstructBeaconCriterion.Conditions> create() {
        return Criteria.CONSTRUCT_BEACON.create(new ConstructBeaconCriterion.Conditions(Optional.empty(), NumberRange.IntRange.ANY));
    }

    public static AdvancementCriterion<ConstructBeaconCriterion.Conditions> level(NumberRange.IntRange level) {
        return Criteria.CONSTRUCT_BEACON.create(new ConstructBeaconCriterion.Conditions(Optional.empty(), level));
    }

    public boolean matches(int level) {
        return this.level.test(level);
    }
}
