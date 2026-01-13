/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.LecternBlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class LecternBlockEntityRenderer
implements BlockEntityRenderer<LecternBlockEntity, LecternBlockEntityRenderState> {
    private final SpriteHolder spriteHolder;
    private final BookModel book;
    private final BookModel.BookModelState bookModelState = new BookModel.BookModelState(0.0f, 0.1f, 0.9f, 1.2f);

    public LecternBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.spriteHolder = ctx.spriteHolder();
        this.book = new BookModel(ctx.getLayerModelPart(EntityModelLayers.BOOK));
    }

    @Override
    public LecternBlockEntityRenderState createRenderState() {
        return new LecternBlockEntityRenderState();
    }

    @Override
    public void updateRenderState(LecternBlockEntity lecternBlockEntity, LecternBlockEntityRenderState lecternBlockEntityRenderState, float f, Vec3d vec3d,  @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        BlockEntityRenderer.super.updateRenderState(lecternBlockEntity, lecternBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        lecternBlockEntityRenderState.hasBook = lecternBlockEntity.getCachedState().get(LecternBlock.HAS_BOOK);
        lecternBlockEntityRenderState.bookRotationDegrees = lecternBlockEntity.getCachedState().get(LecternBlock.FACING).rotateYClockwise().getPositiveHorizontalDegrees();
    }

    @Override
    public void render(LecternBlockEntityRenderState lecternBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (!lecternBlockEntityRenderState.hasBook) {
            return;
        }
        matrixStack.push();
        matrixStack.translate(0.5f, 1.0625f, 0.5f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(-lecternBlockEntityRenderState.bookRotationDegrees));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(67.5f));
        matrixStack.translate(0.0f, -0.125f, 0.0f);
        orderedRenderCommandQueue.submitModel(this.book, this.bookModelState, matrixStack, EnchantingTableBlockEntityRenderer.BOOK_TEXTURE.getRenderLayer(RenderLayers::entitySolid), lecternBlockEntityRenderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, -1, this.spriteHolder.getSprite(EnchantingTableBlockEntityRenderer.BOOK_TEXTURE), 0, lecternBlockEntityRenderState.crumblingOverlay);
        matrixStack.pop();
    }

    @Override
    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
