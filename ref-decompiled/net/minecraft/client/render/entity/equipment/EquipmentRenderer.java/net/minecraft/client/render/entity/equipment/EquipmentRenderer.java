/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.entity.equipment;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
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
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import org.jspecify.annotations.Nullable;

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
        List<EquipmentModel.Layer> list = this.equipmentModelLoader.get(assetKey).getLayers(layerType);
        if (list.isEmpty()) {
            return;
        }
        int i = DyedColorComponent.getColor(stack, 0);
        boolean bl = stack.hasGlint();
        int j = initialOrder;
        for (EquipmentModel.Layer layer : list) {
            int k = EquipmentRenderer.getDyeColor(layer, i);
            if (k == 0) continue;
            Identifier identifier = layer.usePlayerTexture() && textureId != null ? textureId : this.layerTextures.apply(new LayerTextureKey(layerType, layer));
            queue.getBatchingQueue(j++).submitModel(model, state, matrices, RenderLayers.armorCutoutNoCull(identifier), light, OverlayTexture.DEFAULT_UV, k, null, outlineColor, null);
            if (bl) {
                queue.getBatchingQueue(j++).submitModel(model, state, matrices, RenderLayers.armorEntityGlint(), light, OverlayTexture.DEFAULT_UV, k, null, outlineColor, null);
            }
            bl = false;
        }
        ArmorTrim armorTrim = stack.get(DataComponentTypes.TRIM);
        if (armorTrim != null) {
            Sprite sprite = this.trimSprites.apply(new TrimSpriteKey(armorTrim, layerType, assetKey));
            RenderLayer renderLayer = TexturedRenderLayers.getArmorTrims(armorTrim.pattern().value().decal());
            queue.getBatchingQueue(j++).submitModel(model, state, matrices, renderLayer, light, OverlayTexture.DEFAULT_UV, -1, sprite, outlineColor, null);
        }
    }

    private static int getDyeColor(EquipmentModel.Layer layer, int dyeColor) {
        Optional<EquipmentModel.Dyeable> optional = layer.dyeable();
        if (optional.isPresent()) {
            int i = optional.get().colorWhenUndyed().map(ColorHelper::fullAlpha).orElse(0);
            return dyeColor != 0 ? dyeColor : i;
        }
        return -1;
    }

    @Environment(value=EnvType.CLIENT)
    static final class LayerTextureKey
    extends Record {
        final EquipmentModel.LayerType layerType;
        final EquipmentModel.Layer layer;

        LayerTextureKey(EquipmentModel.LayerType layerType, EquipmentModel.Layer layer) {
            this.layerType = layerType;
            this.layer = layer;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{LayerTextureKey.class, "layerType;layer", "layerType", "layer"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{LayerTextureKey.class, "layerType;layer", "layerType", "layer"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{LayerTextureKey.class, "layerType;layer", "layerType", "layer"}, this, object);
        }

        public EquipmentModel.LayerType layerType() {
            return this.layerType;
        }

        public EquipmentModel.Layer layer() {
            return this.layer;
        }
    }

    @Environment(value=EnvType.CLIENT)
    record TrimSpriteKey(ArmorTrim trim, EquipmentModel.LayerType layerType, RegistryKey<EquipmentAsset> equipmentAssetId) {
        public Identifier getTexture() {
            return this.trim.getTextureId(this.layerType.getTrimsDirectory(), this.equipmentAssetId);
        }
    }
}
