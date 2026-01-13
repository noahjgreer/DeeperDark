/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.ItemEntityRenderer
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.ItemEntityRenderState
 *  net.minecraft.client.render.entity.state.ItemStackEntityRenderState
 *  net.minecraft.client.render.item.ItemRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.util.math.random.Random
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemEntityRenderState;
import net.minecraft.client.render.entity.state.ItemStackEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.joml.Quaternionfc;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ItemEntityRenderer
extends EntityRenderer<ItemEntity, ItemEntityRenderState> {
    private static final float field_56954 = 0.0625f;
    private static final float field_32924 = 0.15f;
    private static final float field_56955 = 0.0625f;
    private final ItemModelManager itemModelManager;
    private final Random random = Random.create();

    public ItemEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemModelManager = context.getItemModelManager();
        this.shadowRadius = 0.15f;
        this.shadowOpacity = 0.75f;
    }

    public ItemEntityRenderState createRenderState() {
        return new ItemEntityRenderState();
    }

    public void updateRenderState(ItemEntity itemEntity, ItemEntityRenderState itemEntityRenderState, float f) {
        super.updateRenderState((Entity)itemEntity, (EntityRenderState)itemEntityRenderState, f);
        itemEntityRenderState.uniqueOffset = itemEntity.uniqueOffset;
        itemEntityRenderState.update((Entity)itemEntity, itemEntity.getStack(), this.itemModelManager);
    }

    public void render(ItemEntityRenderState itemEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (itemEntityRenderState.itemRenderState.isEmpty()) {
            return;
        }
        matrixStack.push();
        Box box = itemEntityRenderState.itemRenderState.getModelBoundingBox();
        float f = -((float)box.minY) + 0.0625f;
        float g = MathHelper.sin((double)(itemEntityRenderState.age / 10.0f + itemEntityRenderState.uniqueOffset)) * 0.1f + 0.1f;
        matrixStack.translate(0.0f, g + f, 0.0f);
        float h = ItemEntity.getRotation((float)itemEntityRenderState.age, (float)itemEntityRenderState.uniqueOffset);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotation(h));
        ItemEntityRenderer.render((MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (int)itemEntityRenderState.light, (ItemStackEntityRenderState)itemEntityRenderState, (Random)this.random, (Box)box);
        matrixStack.pop();
        super.render((EntityRenderState)itemEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    public static void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, ItemStackEntityRenderState state, Random random) {
        ItemEntityRenderer.render((MatrixStack)matrices, (OrderedRenderCommandQueue)queue, (int)light, (ItemStackEntityRenderState)state, (Random)random, (Box)state.itemRenderState.getModelBoundingBox());
    }

    public static void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, ItemStackEntityRenderState state, Random random, Box boundingBox) {
        int i = state.renderedAmount;
        if (i == 0) {
            return;
        }
        random.setSeed((long)state.seed);
        ItemRenderState itemRenderState = state.itemRenderState;
        float f = (float)boundingBox.getLengthZ();
        if (f > 0.0625f) {
            itemRenderState.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, state.outlineColor);
            for (int j = 1; j < i; ++j) {
                matrices.push();
                float g = (random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                float h = (random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                float k = (random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                matrices.translate(g, h, k);
                itemRenderState.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, state.outlineColor);
                matrices.pop();
            }
        } else {
            float l = f * 1.5f;
            matrices.translate(0.0f, 0.0f, -(l * (float)(i - 1) / 2.0f));
            itemRenderState.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, state.outlineColor);
            matrices.translate(0.0f, 0.0f, l);
            for (int m = 1; m < i; ++m) {
                matrices.push();
                float h = (random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                float k = (random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                matrices.translate(h, k, 0.0f);
                itemRenderState.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, state.outlineColor);
                matrices.pop();
                matrices.translate(0.0f, 0.0f, l);
            }
        }
    }

    public static void renderStack(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, ItemStackEntityRenderState state, Random random) {
        Box box = state.itemRenderState.getModelBoundingBox();
        int i = state.renderedAmount;
        if (i == 0) {
            return;
        }
        random.setSeed((long)state.seed);
        ItemRenderState itemRenderState = state.itemRenderState;
        float f = (float)box.getLengthZ();
        if (f > 0.0625f) {
            itemRenderState.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, state.outlineColor);
            for (int j = 1; j < i; ++j) {
                matrices.push();
                float g = (random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                float h = (random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                float k = (random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                matrices.translate(g, h, k);
                itemRenderState.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, state.outlineColor);
                matrices.pop();
            }
        } else {
            float l = f * 1.5f;
            matrices.translate(0.0f, 0.0f, -(l * (float)(i - 1) / 2.0f));
            itemRenderState.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, state.outlineColor);
            matrices.translate(0.0f, 0.0f, l);
            for (int m = 1; m < i; ++m) {
                matrices.push();
                float h = (random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                float k = (random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                matrices.translate(h, k, 0.0f);
                itemRenderState.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, state.outlineColor);
                matrices.pop();
                matrices.translate(0.0f, 0.0f, l);
            }
        }
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

