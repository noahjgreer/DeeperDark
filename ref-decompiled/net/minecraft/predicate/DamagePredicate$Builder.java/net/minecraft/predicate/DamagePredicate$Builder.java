/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.predicate;

import java.util.Optional;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;

public static class DamagePredicate.Builder {
    private NumberRange.DoubleRange dealt = NumberRange.DoubleRange.ANY;
    private NumberRange.DoubleRange taken = NumberRange.DoubleRange.ANY;
    private Optional<EntityPredicate> sourceEntity = Optional.empty();
    private Optional<Boolean> blocked = Optional.empty();
    private Optional<DamageSourcePredicate> type = Optional.empty();

    public static DamagePredicate.Builder create() {
        return new DamagePredicate.Builder();
    }

    public DamagePredicate.Builder dealt(NumberRange.DoubleRange dealt) {
        this.dealt = dealt;
        return this;
    }

    public DamagePredicate.Builder taken(NumberRange.DoubleRange taken) {
        this.taken = taken;
        return this;
    }

    public DamagePredicate.Builder sourceEntity(EntityPredicate sourceEntity) {
        this.sourceEntity = Optional.of(sourceEntity);
        return this;
    }

    public DamagePredicate.Builder blocked(Boolean blocked) {
        this.blocked = Optional.of(blocked);
        return this;
    }

    public DamagePredicate.Builder type(DamageSourcePredicate type) {
        this.type = Optional.of(type);
        return this;
    }

    public DamagePredicate.Builder type(DamageSourcePredicate.Builder builder) {
        this.type = Optional.of(builder.build());
        return this;
    }

    public DamagePredicate build() {
        return new DamagePredicate(this.dealt, this.taken, this.sourceEntity, this.blocked, this.type);
    }
}
