/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.predicate.entity;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import net.minecraft.predicate.entity.LocationPredicate;

public static final class EntityPredicate.PositionalPredicates
extends Record {
    final Optional<LocationPredicate> located;
    final Optional<LocationPredicate> steppingOn;
    final Optional<LocationPredicate> affectsMovement;
    public static final MapCodec<EntityPredicate.PositionalPredicates> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)LocationPredicate.CODEC.optionalFieldOf("location").forGetter(EntityPredicate.PositionalPredicates::located), (App)LocationPredicate.CODEC.optionalFieldOf("stepping_on").forGetter(EntityPredicate.PositionalPredicates::steppingOn), (App)LocationPredicate.CODEC.optionalFieldOf("movement_affected_by").forGetter(EntityPredicate.PositionalPredicates::affectsMovement)).apply((Applicative)instance, EntityPredicate.PositionalPredicates::new));

    public EntityPredicate.PositionalPredicates(Optional<LocationPredicate> located, Optional<LocationPredicate> steppingOn, Optional<LocationPredicate> affectsMovement) {
        this.located = located;
        this.steppingOn = steppingOn;
        this.affectsMovement = affectsMovement;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{EntityPredicate.PositionalPredicates.class, "located;steppingOn;affectsMovement", "located", "steppingOn", "affectsMovement"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{EntityPredicate.PositionalPredicates.class, "located;steppingOn;affectsMovement", "located", "steppingOn", "affectsMovement"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{EntityPredicate.PositionalPredicates.class, "located;steppingOn;affectsMovement", "located", "steppingOn", "affectsMovement"}, this, object);
    }

    public Optional<LocationPredicate> located() {
        return this.located;
    }

    public Optional<LocationPredicate> steppingOn() {
        return this.steppingOn;
    }

    public Optional<LocationPredicate> affectsMovement() {
        return this.affectsMovement;
    }
}
