/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.block.entity.BannerBlockEntityRenderer
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.item.model.special.BannerModelRenderer
 *  net.minecraft.client.render.item.model.special.SpecialModelRenderer
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.BannerPatternsComponent
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.DyeColor
 *  org.joml.Vector3fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.model.special;

import java.util.Objects;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BannerModelRenderer
implements SpecialModelRenderer<BannerPatternsComponent> {
    private final BannerBlockEntityRenderer blockEntityRenderer;
    private final DyeColor baseColor;

    public BannerModelRenderer(DyeColor baseColor, BannerBlockEntityRenderer blockEntityRenderer) {
        this.blockEntityRenderer = blockEntityRenderer;
        this.baseColor = baseColor;
    }

    public @Nullable BannerPatternsComponent getData(ItemStack itemStack) {
        return (BannerPatternsComponent)itemStack.get(DataComponentTypes.BANNER_PATTERNS);
    }

    public void render(@Nullable BannerPatternsComponent bannerPatternsComponent, ItemDisplayContext itemDisplayContext, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, int j, boolean bl, int k) {
        this.blockEntityRenderer.renderAsItem(matrixStack, orderedRenderCommandQueue, i, j, this.baseColor, Objects.requireNonNullElse(bannerPatternsComponent, BannerPatternsComponent.DEFAULT), k);
    }

    public void collectVertices(Consumer<Vector3fc> consumer) {
        this.blockEntityRenderer.collectVertices(consumer);
    }

    public /* synthetic */ @Nullable Object getData(ItemStack stack) {
        return this.getData(stack);
    }
}

