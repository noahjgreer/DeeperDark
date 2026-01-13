/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.entity.Sherds
 *  net.minecraft.client.render.block.entity.DecoratedPotBlockEntityRenderer
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.item.model.special.DecoratedPotModelRenderer
 *  net.minecraft.client.render.item.model.special.SpecialModelRenderer
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  org.joml.Vector3fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.model.special;

import java.util.Objects;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.Sherds;
import net.minecraft.client.render.block.entity.DecoratedPotBlockEntityRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DecoratedPotModelRenderer
implements SpecialModelRenderer<Sherds> {
    private final DecoratedPotBlockEntityRenderer blockEntityRenderer;

    public DecoratedPotModelRenderer(DecoratedPotBlockEntityRenderer blockEntityRenderer) {
        this.blockEntityRenderer = blockEntityRenderer;
    }

    public @Nullable Sherds getData(ItemStack itemStack) {
        return (Sherds)itemStack.get(DataComponentTypes.POT_DECORATIONS);
    }

    public void render(@Nullable Sherds sherds, ItemDisplayContext itemDisplayContext, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, int j, boolean bl, int k) {
        this.blockEntityRenderer.render(matrixStack, orderedRenderCommandQueue, i, j, Objects.requireNonNullElse(sherds, Sherds.DEFAULT), k);
    }

    public void collectVertices(Consumer<Vector3fc> consumer) {
        this.blockEntityRenderer.collectVertices(consumer);
    }

    public /* synthetic */ @Nullable Object getData(ItemStack stack) {
        return this.getData(stack);
    }
}

