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
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.DisplayEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.DisplayEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemDisplayEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public static class DisplayEntityRenderer.ItemDisplayEntityRenderer
extends DisplayEntityRenderer<DisplayEntity.ItemDisplayEntity, DisplayEntity.ItemDisplayEntity.Data, ItemDisplayEntityRenderState> {
    private final ItemModelManager itemModelManager;

    protected DisplayEntityRenderer.ItemDisplayEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemModelManager = context.getItemModelManager();
    }

    @Override
    public ItemDisplayEntityRenderState createRenderState() {
        return new ItemDisplayEntityRenderState();
    }

    @Override
    public void updateRenderState(DisplayEntity.ItemDisplayEntity itemDisplayEntity, ItemDisplayEntityRenderState itemDisplayEntityRenderState, float f) {
        super.updateRenderState(itemDisplayEntity, itemDisplayEntityRenderState, f);
        DisplayEntity.ItemDisplayEntity.Data data = itemDisplayEntity.getData();
        if (data != null) {
            this.itemModelManager.updateForNonLivingEntity(itemDisplayEntityRenderState.itemRenderState, data.itemStack(), data.itemTransform(), itemDisplayEntity);
        } else {
            itemDisplayEntityRenderState.itemRenderState.clear();
        }
    }

    @Override
    public void render(ItemDisplayEntityRenderState itemDisplayEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, float f) {
        if (itemDisplayEntityRenderState.itemRenderState.isEmpty()) {
            return;
        }
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotation((float)Math.PI));
        itemDisplayEntityRenderState.itemRenderState.render(matrixStack, orderedRenderCommandQueue, i, OverlayTexture.DEFAULT_UV, itemDisplayEntityRenderState.outlineColor);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Override
    protected /* synthetic */ float getShadowRadius(EntityRenderState state) {
        return super.getShadowRadius((DisplayEntityRenderState)state);
    }

    @Override
    protected /* synthetic */ int getBlockLight(Entity entity, BlockPos pos) {
        return super.getBlockLight((DisplayEntity)entity, pos);
    }

    @Override
    protected /* synthetic */ int getSkyLight(Entity entity, BlockPos pos) {
        return super.getSkyLight((DisplayEntity)entity, pos);
    }
}
