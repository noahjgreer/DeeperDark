/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import com.google.common.collect.Multimap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.ErrorCollectingSpriteGetter;
import net.minecraft.client.render.model.SimpleModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
static class BakedModelManager.1
implements ErrorCollectingSpriteGetter {
    private final Sprite missingBlockSprite;
    private final Sprite missingItemSprite;
    final /* synthetic */ SpriteLoader.StitchResult field_61871;
    final /* synthetic */ SpriteLoader.StitchResult field_64469;
    final /* synthetic */ Multimap field_55478;
    final /* synthetic */ Multimap field_55479;

    BakedModelManager.1() {
        this.field_61871 = stitchResult;
        this.field_64469 = stitchResult2;
        this.field_55478 = multimap;
        this.field_55479 = multimap2;
        this.missingBlockSprite = this.field_61871.missing();
        this.missingItemSprite = this.field_64469.missing();
    }

    @Override
    public Sprite get(SpriteIdentifier id, SimpleModel model) {
        Sprite sprite;
        Identifier identifier = id.getAtlasId();
        boolean bl = identifier.equals(BLOCK_OR_ITEM);
        boolean bl2 = identifier.equals(SpriteAtlasTexture.ITEMS_ATLAS_TEXTURE);
        boolean bl3 = identifier.equals(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        if ((bl || bl2) && (sprite = this.field_64469.getSprite(id.getTextureId())) != null) {
            return sprite;
        }
        if ((bl || bl3) && (sprite = this.field_61871.getSprite(id.getTextureId())) != null) {
            return sprite;
        }
        this.field_55478.put((Object)model.name(), (Object)id);
        return bl2 ? this.missingItemSprite : this.missingBlockSprite;
    }

    @Override
    public Sprite getMissing(String name, SimpleModel model) {
        this.field_55479.put((Object)model.name(), (Object)name);
        return this.missingBlockSprite;
    }
}
