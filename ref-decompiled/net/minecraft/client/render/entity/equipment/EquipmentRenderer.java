/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.TexturedRenderLayers
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.equipment.EquipmentModel$Dyeable
 *  net.minecraft.client.render.entity.equipment.EquipmentModel$Layer
 *  net.minecraft.client.render.entity.equipment.EquipmentModel$LayerType
 *  net.minecraft.client.render.entity.equipment.EquipmentModelLoader
 *  net.minecraft.client.render.entity.equipment.EquipmentRenderer
 *  net.minecraft.client.render.entity.equipment.EquipmentRenderer$LayerTextureKey
 *  net.minecraft.client.render.entity.equipment.EquipmentRenderer$TrimSpriteKey
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.texture.SpriteAtlasTexture
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.DyedColorComponent
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.equipment.EquipmentAsset
 *  net.minecraft.item.equipment.trim.ArmorTrim
 *  net.minecraft.item.equipment.trim.ArmorTrimPattern
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.ColorHelper
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.entity.equipment;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class EquipmentRenderer {
    private static final int field_54178 = 0;
    private final EquipmentModelLoader equipmentModelLoader;
    private final Function<LayerTextureKey, Identifier> layerTextures;
    private final Function<TrimSpriteKey, Sprite> trimSprites;

    public EquipmentRenderer(EquipmentModelLoader equipmentModelLoader, SpriteAtlasTexture armorTrimsAtlas) {
        this.equipmentModelLoader = equipmentModelLoader;
        this.layerTextures = Util.memoize(key -> key.layer.getFullTextureId(key.layerType));
        this.trimSprites = Util.memoize(key -> armorTrimsAtlas.getSprite(key.getTexture()));
    }

    public <S> void render(EquipmentModel.LayerType layerType, RegistryKey<EquipmentAsset> assetKey, Model<? super S> model, S state, ItemStack stack, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int outlineColor) {
        this.render(layerType, assetKey, model, state, stack, matrices, queue, light, null, outlineColor, 1);
    }

    public <S> void render(EquipmentModel.LayerType layerType, RegistryKey<EquipmentAsset> assetKey, Model<? super S> model, S state, ItemStack stack, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, @Nullable Identifier textureId, int outlineColor, int initialOrder) {
        List list = this.equipmentModelLoader.get(assetKey).getLayers(layerType);
        if (list.isEmpty()) {
            return;
        }
        int i = DyedColorComponent.getColor((ItemStack)stack, (int)0);
        boolean bl = stack.hasGlint();
        int j = initialOrder;
        for (EquipmentModel.Layer layer : list) {
            int k = EquipmentRenderer.getDyeColor((EquipmentModel.Layer)layer, (int)i);
            if (k == 0) continue;
            Identifier identifier = layer.usePlayerTexture() && textureId != null ? textureId : (Identifier)this.layerTextures.apply(new LayerTextureKey(layerType, layer));
            queue.getBatchingQueue(j++).submitModel(model, state, matrices, RenderLayers.armorCutoutNoCull((Identifier)identifier), light, OverlayTexture.DEFAULT_UV, k, null, outlineColor, null);
            if (bl) {
                queue.getBatchingQueue(j++).submitModel(model, state, matrices, RenderLayers.armorEntityGlint(), light, OverlayTexture.DEFAULT_UV, k, null, outlineColor, null);
            }
            bl = false;
        }
        ArmorTrim armorTrim = (ArmorTrim)stack.get(DataComponentTypes.TRIM);
        if (armorTrim != null) {
            Sprite sprite = (Sprite)this.trimSprites.apply(new TrimSpriteKey(armorTrim, layerType, assetKey));
            RenderLayer renderLayer = TexturedRenderLayers.getArmorTrims((boolean)((ArmorTrimPattern)armorTrim.pattern().value()).decal());
            queue.getBatchingQueue(j++).submitModel(model, state, matrices, renderLayer, light, OverlayTexture.DEFAULT_UV, -1, sprite, outlineColor, null);
        }
    }

    private static int getDyeColor(EquipmentModel.Layer layer, int dyeColor) {
        Optional optional = layer.dyeable();
        if (optional.isPresent()) {
            int i = ((EquipmentModel.Dyeable)optional.get()).colorWhenUndyed().map(ColorHelper::fullAlpha).orElse(0);
            return dyeColor != 0 ? dyeColor : i;
        }
        return -1;
    }
}

