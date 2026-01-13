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
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public record LevitationCriterion.Conditions(Optional<LootContextPredicate> player, Optional<DistancePredicate> distance, NumberRange.IntRange duration) implements AbstractCriterion.Conditions
{
    public static final Codec<LevitationCriterion.Conditions> CODEC = RecordCodecBuilder.create((T instance) -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(LevitationCriterion.Conditions::player), (App)DistancePredicate.CODEC.optionalFieldOf("distance").forGetter(LevitationCriterion.Conditions::distance), (App)NumberRange.IntRange.CODEC.optionalFieldOf("duration", (Object)NumberRange.IntRange.ANY).forGetter(LevitationCriterion.Conditions::duration)).apply((Applicative)instance, LevitationCriterion.Conditions::new));

    public static AdvancementCriterion<LevitationCriterion.Conditions> create(DistancePredicate distance) {
        return Criteria.LEVITATION.create(new LevitationCriterion.Conditions(Optional.empty(), Optional.of(distance), NumberRange.IntRange.ANY));
    }

    public boolean matches(ServerPlayerEntity player, Vec3d distance, int duration) {
        if (this.distance.isPresent() && !this.distance.get().test(distance.x, distance.y, distance.z, player.getX(), player.getY(), player.getZ())) {
            return false;
        }
        return this.duration.test(duration);
    }
}
