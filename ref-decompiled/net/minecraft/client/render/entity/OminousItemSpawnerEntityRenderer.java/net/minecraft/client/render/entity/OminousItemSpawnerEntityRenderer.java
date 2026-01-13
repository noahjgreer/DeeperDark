/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemStackEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.OminousItemSpawnerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class OminousItemSpawnerEntityRenderer
extends EntityRenderer<OminousItemSpawnerEntity, ItemStackEntityRenderState> {
    private static final float field_50231 = 40.0f;
    private static final int field_50232 = 50;
    private final ItemModelManager itemModelManager;
    private final Random random = Random.create();

    protected OminousItemSpawnerEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemModelManager = context.getItemModelManager();
    }

    @Override
    public ItemStackEntityRenderState createRenderState() {
        return new ItemStackEntityRenderState();
    }

    @Override
    public void updateRenderState(OminousItemSpawnerEntity ominousItemSpawnerEntity, ItemStackEntityRenderState itemStackEntityRenderState, float f) {
        super.updateRenderState(ominousItemSpawnerEntity, itemStackEntityRenderState, f);
        ItemStack itemStack = ominousItemSpawnerEntity.getItem();
        itemStackEntityRenderState.update(ominousItemSpawnerEntity, itemStack, this.itemModelManager);
    }

    @Override
    public void render(ItemStackEntityRenderState itemStackEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        float f;
        if (itemStackEntityRenderState.itemRenderState.isEmpty()) {
            return;
        }
        matrixStack.push();
        if (itemStackEntityRenderState.age <= 50.0f) {
            f = Math.min(itemStackEntityRenderState.age, 50.0f) / 50.0f;
            matrixStack.scale(f, f, f);
        }
        f = MathHelper.wrapDegrees(itemStackEntityRenderState.age * 40.0f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(f));
        ItemEntityRenderer.render(matrixStack, orderedRenderCommandQueue, 0xF000F0, itemStackEntityRenderState, this.random);
        matrixStack.pop();
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
