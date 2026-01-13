/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.component.type;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;

public record SuspiciousStewEffectsComponent.StewEffect(RegistryEntry<StatusEffect> effect, int duration) {
    public static final Codec<SuspiciousStewEffectsComponent.StewEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)StatusEffect.ENTRY_CODEC.fieldOf("id").forGetter(SuspiciousStewEffectsComponent.StewEffect::effect), (App)Codec.INT.lenientOptionalFieldOf("duration", (Object)160).forGetter(SuspiciousStewEffectsComponent.StewEffect::duration)).apply((Applicative)instance, SuspiciousStewEffectsComponent.StewEffect::new));
    public static final PacketCodec<RegistryByteBuf, SuspiciousStewEffectsComponent.StewEffect> PACKET_CODEC = PacketCodec.tuple(StatusEffect.ENTRY_PACKET_CODEC, SuspiciousStewEffectsComponent.StewEffect::effect, PacketCodecs.VAR_INT, SuspiciousStewEffectsComponent.StewEffect::duration, SuspiciousStewEffectsComponent.StewEffect::new);

    public StatusEffectInstance createStatusEffectInstance() {
        return new StatusEffectInstance(this.effect, this.duration);
    }
}
