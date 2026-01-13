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
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;

public record UsedEnderEyeCriterion.Conditions(Optional<LootContextPredicate> player, NumberRange.DoubleRange distance) implements AbstractCriterion.Conditions
{
    public static final Codec<UsedEnderEyeCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(UsedEnderEyeCriterion.Conditions::player), (App)NumberRange.DoubleRange.CODEC.optionalFieldOf("distance", (Object)NumberRange.DoubleRange.ANY).forGetter(UsedEnderEyeCriterion.Conditions::distance)).apply((Applicative)instance, UsedEnderEyeCriterion.Conditions::new));

    public boolean matches(double distance) {
        return this.distance.testSqrt(distance);
    }
}
