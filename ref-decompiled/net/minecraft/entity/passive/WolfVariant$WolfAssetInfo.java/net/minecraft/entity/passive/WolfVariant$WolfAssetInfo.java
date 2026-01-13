/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.entity.passive;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.AssetInfo;

public record WolfVariant.WolfAssetInfo(AssetInfo.TextureAssetInfo wild, AssetInfo.TextureAssetInfo tame, AssetInfo.TextureAssetInfo angry) {
    public static final Codec<WolfVariant.WolfAssetInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)AssetInfo.TextureAssetInfo.CODEC.fieldOf("wild").forGetter(WolfVariant.WolfAssetInfo::wild), (App)AssetInfo.TextureAssetInfo.CODEC.fieldOf("tame").forGetter(WolfVariant.WolfAssetInfo::tame), (App)AssetInfo.TextureAssetInfo.CODEC.fieldOf("angry").forGetter(WolfVariant.WolfAssetInfo::angry)).apply((Applicative)instance, WolfVariant.WolfAssetInfo::new));
}
