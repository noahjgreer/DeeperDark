/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.block.entity.BannerBlockEntityRenderer
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.model.ShieldEntityModel
 *  net.minecraft.client.render.item.model.special.ShieldModelRenderer
 *  net.minecraft.client.render.item.model.special.SpecialModelRenderer
 *  net.minecraft.client.render.model.ModelBaker
 *  net.minecraft.client.texture.SpriteHolder
 *  net.minecraft.client.util.SpriteIdentifier
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.component.ComponentMap
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.BannerPatternsComponent
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.DyeColor
 *  net.minecraft.util.Unit
 *  org.joml.Vector3fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.model.special;

import java.util.Objects;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Unit;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ShieldModelRenderer
implements SpecialModelRenderer<ComponentMap> {
    private final SpriteHolder spriteHolder;
    private final ShieldEntityModel model;

    public ShieldModelRenderer(SpriteHolder spriteHolder, ShieldEntityModel model) {
        this.spriteHolder = spriteHolder;
        this.model = model;
    }

    public @Nullable ComponentMap getData(ItemStack itemStack) {
        return itemStack.getImmutableComponents();
    }

    public void render(@Nullable ComponentMap componentMap, ItemDisplayContext itemDisplayContext, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, int j, boolean bl, int k) {
        BannerPatternsComponent bannerPatternsComponent = componentMap != null ? (BannerPatternsComponent)componentMap.getOrDefault(DataComponentTypes.BANNER_PATTERNS, (Object)BannerPatternsComponent.DEFAULT) : BannerPatternsComponent.DEFAULT;
        DyeColor dyeColor = componentMap != null ? (DyeColor)componentMap.get(DataComponentTypes.BASE_COLOR) : null;
        boolean bl2 = !bannerPatternsComponent.layers().isEmpty() || dyeColor != null;
        matrixStack.push();
        matrixStack.scale(1.0f, -1.0f, -1.0f);
        SpriteIdentifier spriteIdentifier = bl2 ? ModelBaker.SHIELD_BASE : ModelBaker.SHIELD_BASE_NO_PATTERN;
        orderedRenderCommandQueue.submitModelPart(this.model.getHandle(), matrixStack, this.model.getLayer(spriteIdentifier.getAtlasId()), i, j, this.spriteHolder.getSprite(spriteIdentifier), false, false, -1, null, k);
        if (bl2) {
            BannerBlockEntityRenderer.renderCanvas((SpriteHolder)this.spriteHolder, (MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (int)i, (int)j, (Model)this.model, (Object)Unit.INSTANCE, (SpriteIdentifier)spriteIdentifier, (boolean)false, (DyeColor)Objects.requireNonNullElse(dyeColor, DyeColor.WHITE), (BannerPatternsComponent)bannerPatternsComponent, (boolean)bl, null, (int)k);
        } else {
            orderedRenderCommandQueue.submitModelPart(this.model.getPlate(), matrixStack, this.model.getLayer(spriteIdentifier.getAtlasId()), i, j, this.spriteHolder.getSprite(spriteIdentifier), false, bl, -1, null, k);
        }
        matrixStack.pop();
    }

    public void collectVertices(Consumer<Vector3fc> consumer) {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.scale(1.0f, -1.0f, -1.0f);
        this.model.getRootPart().collectVertices(matrixStack, consumer);
    }

    public /* synthetic */ @Nullable Object getData(ItemStack stack) {
        return this.getData(stack);
    }
}

