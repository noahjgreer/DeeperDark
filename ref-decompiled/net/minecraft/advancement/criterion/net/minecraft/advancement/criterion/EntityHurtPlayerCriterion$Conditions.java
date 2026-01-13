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
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public record EntityHurtPlayerCriterion.Conditions(Optional<LootContextPredicate> player, Optional<DamagePredicate> damage) implements AbstractCriterion.Conditions
{
    public static final Codec<EntityHurtPlayerCriterion.Conditions> CODEC = RecordCodecBuilder.create((T instance) -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(EntityHurtPlayerCriterion.Conditions::player), (App)DamagePredicate.CODEC.optionalFieldOf("damage").forGetter(EntityHurtPlayerCriterion.Conditions::damage)).apply((Applicative)instance, EntityHurtPlayerCriterion.Conditions::new));

    public static AdvancementCriterion<EntityHurtPlayerCriterion.Conditions> create() {
        return Criteria.ENTITY_HURT_PLAYER.create(new EntityHurtPlayerCriterion.Conditions(Optional.empty(), Optional.empty()));
    }

    public static AdvancementCriterion<EntityHurtPlayerCriterion.Conditions> create(DamagePredicate predicate) {
        return Criteria.ENTITY_HURT_PLAYER.create(new EntityHurtPlayerCriterion.Conditions(Optional.empty(), Optional.of(predicate)));
    }

    public static AdvancementCriterion<EntityHurtPlayerCriterion.Conditions> create(DamagePredicate.Builder damageBuilder) {
        return Criteria.ENTITY_HURT_PLAYER.create(new EntityHurtPlayerCriterion.Conditions(Optional.empty(), Optional.of(damageBuilder.build())));
    }

    public boolean matches(ServerPlayerEntity player, DamageSource damageSource, float dealt, float taken, boolean blocked) {
        return !this.damage.isPresent() || this.damage.get().test(player, damageSource, dealt, taken, blocked);
    }
}
