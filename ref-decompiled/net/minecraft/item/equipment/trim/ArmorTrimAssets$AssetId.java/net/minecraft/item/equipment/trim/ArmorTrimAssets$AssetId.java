/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.item.equipment.trim;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public record ArmorTrimAssets.AssetId(String suffix) {
    public static final Codec<ArmorTrimAssets.AssetId> CODEC = Codecs.IDENTIFIER_PATH.xmap(ArmorTrimAssets.AssetId::new, ArmorTrimAssets.AssetId::suffix);
    public static final PacketCodec<ByteBuf, ArmorTrimAssets.AssetId> PACKET_CODEC = PacketCodecs.STRING.xmap(ArmorTrimAssets.AssetId::new, ArmorTrimAssets.AssetId::suffix);

    public ArmorTrimAssets.AssetId {
        if (!Identifier.isPathValid(suffix)) {
            throw new IllegalArgumentException("Invalid string to use as a resource path element: " + suffix);
        }
    }
}
