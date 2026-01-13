/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;

public record AssetInfo.TextureAssetInfo(Identifier id, Identifier texturePath) implements AssetInfo.TextureAsset
{
    public static final Codec<AssetInfo.TextureAssetInfo> CODEC = Identifier.CODEC.xmap(AssetInfo.TextureAssetInfo::new, AssetInfo.TextureAssetInfo::id);
    public static final MapCodec<AssetInfo.TextureAssetInfo> MAP_CODEC = CODEC.fieldOf("asset_id");
    public static final PacketCodec<ByteBuf, AssetInfo.TextureAssetInfo> PACKET_CODEC = Identifier.PACKET_CODEC.xmap(AssetInfo.TextureAssetInfo::new, AssetInfo.TextureAssetInfo::id);

    public AssetInfo.TextureAssetInfo(Identifier id) {
        this(id, id.withPath(path -> "textures/" + path + ".png"));
    }
}
