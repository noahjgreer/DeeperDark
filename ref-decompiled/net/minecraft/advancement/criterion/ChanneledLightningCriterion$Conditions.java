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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;

public record ChanneledLightningCriterion.Conditions(Optional<LootContextPredicate> player, List<LootContextPredicate> victims) implements AbstractCriterion.Conditions
{
    public static final Codec<ChanneledLightningCriterion.Conditions> CODEC = RecordCodecBuilder.create((T instance) -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(ChanneledLightningCriterion.Conditions::player), (App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.listOf().optionalFieldOf("victims", List.of()).forGetter(ChanneledLightningCriterion.Conditions::victims)).apply((Applicative)instance, ChanneledLightningCriterion.Conditions::new));

    public static AdvancementCriterion<ChanneledLightningCriterion.Conditions> create(EntityPredicate.Builder ... victims) {
        return Criteria.CHANNELED_LIGHTNING.create(new ChanneledLightningCriterion.Conditions(Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicates(victims)));
    }

    public boolean matches(Collection<? extends LootContext> victims) {
        for (LootContextPredicate lootContextPredicate : this.victims) {
            boolean bl = false;
            for (LootContext lootContext : victims) {
                if (!lootContextPredicate.test(lootContext)) continue;
                bl = true;
                break;
            }
            if (bl) continue;
            return false;
        }
        return true;
    }

    @Override
    public void validate(LootContextPredicateValidator validator) {
        AbstractCriterion.Conditions.super.validate(validator);
        validator.validateEntityPredicates(this.victims, "victims");
    }
}
