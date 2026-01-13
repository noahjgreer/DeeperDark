/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;

public record AssetInfo.SkinAssetInfo(Identifier texturePath, String url) implements AssetInfo.TextureAsset
{
    @Override
    public Identifier id() {
        return this.texturePath;
    }
}
