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
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;

public record BlocksAttacksComponent.ItemDamage(float threshold, float base, float factor) {
    public static final Codec<BlocksAttacksComponent.ItemDamage> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codecs.NON_NEGATIVE_FLOAT.fieldOf("threshold").forGetter(BlocksAttacksComponent.ItemDamage::threshold), (App)Codec.FLOAT.fieldOf("base").forGetter(BlocksAttacksComponent.ItemDamage::base), (App)Codec.FLOAT.fieldOf("factor").forGetter(BlocksAttacksComponent.ItemDamage::factor)).apply((Applicative)instance, BlocksAttacksComponent.ItemDamage::new));
    public static final PacketCodec<ByteBuf, BlocksAttacksComponent.ItemDamage> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.FLOAT, BlocksAttacksComponent.ItemDamage::threshold, PacketCodecs.FLOAT, BlocksAttacksComponent.ItemDamage::base, PacketCodecs.FLOAT, BlocksAttacksComponent.ItemDamage::factor, BlocksAttacksComponent.ItemDamage::new);
    public static final BlocksAttacksComponent.ItemDamage DEFAULT = new BlocksAttacksComponent.ItemDamage(1.0f, 0.0f, 1.0f);

    public int calculate(float itemDamage) {
        if (itemDamage < this.threshold) {
            return 0;
        }
        return MathHelper.floor(this.base + this.factor * itemDamage);
    }
}
