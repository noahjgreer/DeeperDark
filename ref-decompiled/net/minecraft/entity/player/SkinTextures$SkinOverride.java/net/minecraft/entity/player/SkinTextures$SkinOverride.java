/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.entity.player;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.AssetInfo;

public static final class SkinTextures.SkinOverride
extends Record {
    final Optional<AssetInfo.TextureAssetInfo> body;
    final Optional<AssetInfo.TextureAssetInfo> cape;
    final Optional<AssetInfo.TextureAssetInfo> elytra;
    final Optional<PlayerSkinType> model;
    public static final SkinTextures.SkinOverride EMPTY = new SkinTextures.SkinOverride(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    public static final MapCodec<SkinTextures.SkinOverride> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)AssetInfo.TextureAssetInfo.CODEC.optionalFieldOf("texture").forGetter(SkinTextures.SkinOverride::body), (App)AssetInfo.TextureAssetInfo.CODEC.optionalFieldOf("cape").forGetter(SkinTextures.SkinOverride::cape), (App)AssetInfo.TextureAssetInfo.CODEC.optionalFieldOf("elytra").forGetter(SkinTextures.SkinOverride::elytra), (App)PlayerSkinType.CODEC.optionalFieldOf("model").forGetter(SkinTextures.SkinOverride::model)).apply((Applicative)instance, SkinTextures.SkinOverride::create));
    public static final PacketCodec<ByteBuf, SkinTextures.SkinOverride> PACKET_CODEC = PacketCodec.tuple(AssetInfo.TextureAssetInfo.PACKET_CODEC.collect(PacketCodecs::optional), SkinTextures.SkinOverride::body, AssetInfo.TextureAssetInfo.PACKET_CODEC.collect(PacketCodecs::optional), SkinTextures.SkinOverride::cape, AssetInfo.TextureAssetInfo.PACKET_CODEC.collect(PacketCodecs::optional), SkinTextures.SkinOverride::elytra, PlayerSkinType.PACKET_CODEC.collect(PacketCodecs::optional), SkinTextures.SkinOverride::model, SkinTextures.SkinOverride::create);

    public SkinTextures.SkinOverride(Optional<AssetInfo.TextureAssetInfo> body, Optional<AssetInfo.TextureAssetInfo> cape, Optional<AssetInfo.TextureAssetInfo> elytra, Optional<PlayerSkinType> model) {
        this.body = body;
        this.cape = cape;
        this.elytra = elytra;
        this.model = model;
    }

    public static SkinTextures.SkinOverride create(Optional<AssetInfo.TextureAssetInfo> texture, Optional<AssetInfo.TextureAssetInfo> cape, Optional<AssetInfo.TextureAssetInfo> elytra, Optional<PlayerSkinType> model) {
        if (texture.isEmpty() && cape.isEmpty() && elytra.isEmpty() && model.isEmpty()) {
            return EMPTY;
        }
        return new SkinTextures.SkinOverride(texture, cape, elytra, model);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SkinTextures.SkinOverride.class, "body;cape;elytra;model", "body", "cape", "elytra", "model"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SkinTextures.SkinOverride.class, "body;cape;elytra;model", "body", "cape", "elytra", "model"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SkinTextures.SkinOverride.class, "body;cape;elytra;model", "body", "cape", "elytra", "model"}, this, object);
    }

    public Optional<AssetInfo.TextureAssetInfo> body() {
        return this.body;
    }

    public Optional<AssetInfo.TextureAssetInfo> cape() {
        return this.cape;
    }

    public Optional<AssetInfo.TextureAssetInfo> elytra() {
        return this.elytra;
    }

    public Optional<PlayerSkinType> model() {
        return this.model;
    }
}
