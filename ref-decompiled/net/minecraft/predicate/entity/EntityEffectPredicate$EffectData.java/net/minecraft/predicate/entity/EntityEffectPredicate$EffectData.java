/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.predicate.entity;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.predicate.NumberRange;
import org.jspecify.annotations.Nullable;

public record EntityEffectPredicate.EffectData(NumberRange.IntRange amplifier, NumberRange.IntRange duration, Optional<Boolean> ambient, Optional<Boolean> visible) {
    public static final Codec<EntityEffectPredicate.EffectData> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)NumberRange.IntRange.CODEC.optionalFieldOf("amplifier", (Object)NumberRange.IntRange.ANY).forGetter(EntityEffectPredicate.EffectData::amplifier), (App)NumberRange.IntRange.CODEC.optionalFieldOf("duration", (Object)NumberRange.IntRange.ANY).forGetter(EntityEffectPredicate.EffectData::duration), (App)Codec.BOOL.optionalFieldOf("ambient").forGetter(EntityEffectPredicate.EffectData::ambient), (App)Codec.BOOL.optionalFieldOf("visible").forGetter(EntityEffectPredicate.EffectData::visible)).apply((Applicative)instance, EntityEffectPredicate.EffectData::new));

    public EntityEffectPredicate.EffectData() {
        this(NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, Optional.empty(), Optional.empty());
    }

    public boolean test(@Nullable StatusEffectInstance statusEffectInstance) {
        if (statusEffectInstance == null) {
            return false;
        }
        if (!this.amplifier.test(statusEffectInstance.getAmplifier())) {
            return false;
        }
        if (!this.duration.test(statusEffectInstance.getDuration())) {
            return false;
        }
        if (this.ambient.isPresent() && this.ambient.get().booleanValue() != statusEffectInstance.isAmbient()) {
            return false;
        }
        return !this.visible.isPresent() || this.visible.get().booleanValue() == statusEffectInstance.shouldShowParticles();
    }
}
