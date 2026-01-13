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
import net.minecraft.util.dynamic.Codecs;

public record SpearMobsCriterion.Conditions(Optional<LootContextPredicate> player, Optional<Integer> count) implements AbstractCriterion.Conditions
{
    public static final Codec<SpearMobsCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(SpearMobsCriterion.Conditions::player), (App)Codecs.POSITIVE_INT.optionalFieldOf("count").forGetter(SpearMobsCriterion.Conditions::count)).apply((Applicative)instance, SpearMobsCriterion.Conditions::new));

    public static AdvancementCriterion<SpearMobsCriterion.Conditions> method_76462(int i) {
        return Criteria.SPEAR_MOBS.create(new SpearMobsCriterion.Conditions(Optional.empty(), Optional.of(i)));
    }

    public boolean test(int count) {
        return this.count.isEmpty() || count >= this.count.get();
    }
}
