/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.model.special;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface SimpleSpecialModelRenderer
extends SpecialModelRenderer<Void> {
    @Override
    default public @Nullable Void getData(ItemStack itemStack) {
        return null;
    }

    @Override
    default public void render(@Nullable Void void_, ItemDisplayContext itemDisplayContext, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, int j, boolean bl, int k) {
        this.render(itemDisplayContext, matrixStack, orderedRenderCommandQueue, i, j, bl, k);
    }

    public void render(ItemDisplayContext var1, MatrixStack var2, OrderedRenderCommandQueue var3, int var4, int var5, boolean var6, int var7);

    @Override
    default public /* synthetic */ @Nullable Object getData(ItemStack stack) {
        return this.getData(stack);
    }
}
