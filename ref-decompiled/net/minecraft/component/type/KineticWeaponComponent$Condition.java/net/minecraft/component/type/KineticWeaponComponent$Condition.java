/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.component.type;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;

public record KineticWeaponComponent.Condition(int maxDurationTicks, float minSpeed, float minRelativeSpeed) {
    public static final Codec<KineticWeaponComponent.Condition> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codecs.NON_NEGATIVE_INT.fieldOf("max_duration_ticks").forGetter(KineticWeaponComponent.Condition::maxDurationTicks), (App)Codec.FLOAT.optionalFieldOf("min_speed", (Object)Float.valueOf(0.0f)).forGetter(KineticWeaponComponent.Condition::minSpeed), (App)Codec.FLOAT.optionalFieldOf("min_relative_speed", (Object)Float.valueOf(0.0f)).forGetter(KineticWeaponComponent.Condition::minRelativeSpeed)).apply((Applicative)instance, KineticWeaponComponent.Condition::new));
    public static final PacketCodec<ByteBuf, KineticWeaponComponent.Condition> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, KineticWeaponComponent.Condition::maxDurationTicks, PacketCodecs.FLOAT, KineticWeaponComponent.Condition::minSpeed, PacketCodecs.FLOAT, KineticWeaponComponent.Condition::minRelativeSpeed, KineticWeaponComponent.Condition::new);

    public boolean isSatisfied(int durationTicks, double speed, double relativeSpeed, double minSpeedMultiplier) {
        return durationTicks <= this.maxDurationTicks && speed >= (double)this.minSpeed * minSpeedMultiplier && relativeSpeed >= (double)this.minRelativeSpeed * minSpeedMultiplier;
    }

    public static Optional<KineticWeaponComponent.Condition> ofMinSpeed(int maxDurationTicks, float minSpeed) {
        return Optional.of(new KineticWeaponComponent.Condition(maxDurationTicks, minSpeed, 0.0f));
    }

    public static Optional<KineticWeaponComponent.Condition> ofMinRelativeSpeed(int maxDurationTicks, float minRelativeSpeed) {
        return Optional.of(new KineticWeaponComponent.Condition(maxDurationTicks, 0.0f, minRelativeSpeed));
    }
}
