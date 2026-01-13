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
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;

public record CuredZombieVillagerCriterion.Conditions(Optional<LootContextPredicate> player, Optional<LootContextPredicate> zombie, Optional<LootContextPredicate> villager) implements AbstractCriterion.Conditions
{
    public static final Codec<CuredZombieVillagerCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(CuredZombieVillagerCriterion.Conditions::player), (App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("zombie").forGetter(CuredZombieVillagerCriterion.Conditions::zombie), (App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("villager").forGetter(CuredZombieVillagerCriterion.Conditions::villager)).apply((Applicative)instance, CuredZombieVillagerCriterion.Conditions::new));

    public static AdvancementCriterion<CuredZombieVillagerCriterion.Conditions> any() {
        return Criteria.CURED_ZOMBIE_VILLAGER.create(new CuredZombieVillagerCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.empty()));
    }

    public boolean matches(LootContext zombie, LootContext villager) {
        if (this.zombie.isPresent() && !this.zombie.get().test(zombie)) {
            return false;
        }
        return !this.villager.isPresent() || this.villager.get().test(villager);
    }

    @Override
    public void validate(LootContextPredicateValidator validator) {
        AbstractCriterion.Conditions.super.validate(validator);
        validator.validateEntityPredicate(this.zombie, "zombie");
        validator.validateEntityPredicate(this.villager, "villager");
    }
}
