/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.item.model.special.SpecialModelRenderer
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  org.joml.Vector3fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.model.special;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface SpecialModelRenderer<T> {
    public void render(@Nullable T var1, ItemDisplayContext var2, MatrixStack var3, OrderedRenderCommandQueue var4, int var5, int var6, boolean var7, int var8);

    public void collectVertices(Consumer<Vector3fc> var1);

    public @Nullable T getData(ItemStack var1);
}

