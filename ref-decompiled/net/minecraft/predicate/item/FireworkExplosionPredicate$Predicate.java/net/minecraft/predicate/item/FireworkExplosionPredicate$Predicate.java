/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.predicate.item;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.component.type.FireworkExplosionComponent;

public record FireworkExplosionPredicate.Predicate(Optional<FireworkExplosionComponent.Type> shape, Optional<Boolean> twinkle, Optional<Boolean> trail) implements Predicate<FireworkExplosionComponent>
{
    public static final Codec<FireworkExplosionPredicate.Predicate> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)FireworkExplosionComponent.Type.CODEC.optionalFieldOf("shape").forGetter(FireworkExplosionPredicate.Predicate::shape), (App)Codec.BOOL.optionalFieldOf("has_twinkle").forGetter(FireworkExplosionPredicate.Predicate::twinkle), (App)Codec.BOOL.optionalFieldOf("has_trail").forGetter(FireworkExplosionPredicate.Predicate::trail)).apply((Applicative)instance, FireworkExplosionPredicate.Predicate::new));

    @Override
    public boolean test(FireworkExplosionComponent fireworkExplosionComponent) {
        if (this.shape.isPresent() && this.shape.get() != fireworkExplosionComponent.shape()) {
            return false;
        }
        if (this.twinkle.isPresent() && this.twinkle.get().booleanValue() != fireworkExplosionComponent.hasTwinkle()) {
            return false;
        }
        return !this.trail.isPresent() || this.trail.get().booleanValue() == fireworkExplosionComponent.hasTrail();
    }

    @Override
    public /* synthetic */ boolean test(Object fireworkExplosionComponent) {
        return this.test((FireworkExplosionComponent)fireworkExplosionComponent);
    }
}
