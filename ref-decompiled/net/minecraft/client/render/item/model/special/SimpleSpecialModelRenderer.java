/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer
 *  net.minecraft.client.render.item.model.special.SpecialModelRenderer
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
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
    default public @Nullable Void getData(ItemStack itemStack) {
        return null;
    }

    default public void render(@Nullable Void void_, ItemDisplayContext itemDisplayContext, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, int j, boolean bl, int k) {
        this.render(itemDisplayContext, matrixStack, orderedRenderCommandQueue, i, j, bl, k);
    }

    public void render(ItemDisplayContext var1, MatrixStack var2, OrderedRenderCommandQueue var3, int var4, int var5, boolean var6, int var7);

    default public /* synthetic */ @Nullable Object getData(ItemStack stack) {
        return this.getData(stack);
    }
}

