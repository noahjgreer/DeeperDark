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
import java.util.Optional;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;

public record BlocksAttacksComponent.DamageReduction(float horizontalBlockingAngle, Optional<RegistryEntryList<DamageType>> type, float base, float factor) {
    public static final Codec<BlocksAttacksComponent.DamageReduction> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codecs.POSITIVE_FLOAT.optionalFieldOf("horizontal_blocking_angle", (Object)Float.valueOf(90.0f)).forGetter(BlocksAttacksComponent.DamageReduction::horizontalBlockingAngle), (App)RegistryCodecs.entryList(RegistryKeys.DAMAGE_TYPE).optionalFieldOf("type").forGetter(BlocksAttacksComponent.DamageReduction::type), (App)Codec.FLOAT.fieldOf("base").forGetter(BlocksAttacksComponent.DamageReduction::base), (App)Codec.FLOAT.fieldOf("factor").forGetter(BlocksAttacksComponent.DamageReduction::factor)).apply((Applicative)instance, BlocksAttacksComponent.DamageReduction::new));
    public static final PacketCodec<RegistryByteBuf, BlocksAttacksComponent.DamageReduction> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.FLOAT, BlocksAttacksComponent.DamageReduction::horizontalBlockingAngle, PacketCodecs.registryEntryList(RegistryKeys.DAMAGE_TYPE).collect(PacketCodecs::optional), BlocksAttacksComponent.DamageReduction::type, PacketCodecs.FLOAT, BlocksAttacksComponent.DamageReduction::base, PacketCodecs.FLOAT, BlocksAttacksComponent.DamageReduction::factor, BlocksAttacksComponent.DamageReduction::new);

    public float getReductionAmount(DamageSource source, float damage, double angle) {
        if (angle > (double)((float)Math.PI / 180 * this.horizontalBlockingAngle)) {
            return 0.0f;
        }
        if (this.type.isPresent() && !this.type.get().contains(source.getTypeRegistryEntry())) {
            return 0.0f;
        }
        return MathHelper.clamp(this.base + this.factor * damage, 0.0f, damage);
    }
}
