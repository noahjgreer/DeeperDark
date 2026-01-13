/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.entity.effect;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;

record StatusEffectInstance.Parameters(int amplifier, int duration, boolean ambient, boolean showParticles, boolean showIcon, Optional<StatusEffectInstance.Parameters> hiddenEffect) {
    public static final MapCodec<StatusEffectInstance.Parameters> CODEC = MapCodec.recursive((String)"MobEffectInstance.Details", codec -> RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.UNSIGNED_BYTE.optionalFieldOf("amplifier", (Object)0).forGetter(StatusEffectInstance.Parameters::amplifier), (App)Codec.INT.optionalFieldOf("duration", (Object)0).forGetter(StatusEffectInstance.Parameters::duration), (App)Codec.BOOL.optionalFieldOf("ambient", (Object)false).forGetter(StatusEffectInstance.Parameters::ambient), (App)Codec.BOOL.optionalFieldOf("show_particles", (Object)true).forGetter(StatusEffectInstance.Parameters::showParticles), (App)Codec.BOOL.optionalFieldOf("show_icon").forGetter(parameters -> Optional.of(parameters.showIcon())), (App)codec.optionalFieldOf("hidden_effect").forGetter(StatusEffectInstance.Parameters::hiddenEffect)).apply((Applicative)instance, StatusEffectInstance.Parameters::create)));
    public static final PacketCodec<ByteBuf, StatusEffectInstance.Parameters> PACKET_CODEC = PacketCodec.recursive(packetCodec -> PacketCodec.tuple(PacketCodecs.VAR_INT, StatusEffectInstance.Parameters::amplifier, PacketCodecs.VAR_INT, StatusEffectInstance.Parameters::duration, PacketCodecs.BOOLEAN, StatusEffectInstance.Parameters::ambient, PacketCodecs.BOOLEAN, StatusEffectInstance.Parameters::showParticles, PacketCodecs.BOOLEAN, StatusEffectInstance.Parameters::showIcon, packetCodec.collect(PacketCodecs::optional), StatusEffectInstance.Parameters::hiddenEffect, StatusEffectInstance.Parameters::new));

    private static StatusEffectInstance.Parameters create(int amplifier, int duration, boolean ambient, boolean showParticles, Optional<Boolean> showIcon, Optional<StatusEffectInstance.Parameters> hiddenEffect) {
        return new StatusEffectInstance.Parameters(amplifier, duration, ambient, showParticles, showIcon.orElse(showParticles), hiddenEffect);
    }
}
