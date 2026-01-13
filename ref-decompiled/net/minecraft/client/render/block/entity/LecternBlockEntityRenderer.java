/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.LecternBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.LecternBlockEntity
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory$Context
 *  net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.LecternBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.LecternBlockEntityRenderState
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.model.BookModel
 *  net.minecraft.client.render.entity.model.BookModel$BookModelState
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.texture.SpriteHolder
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Quaternionfc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.client.model.Model;
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
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
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

    public LecternBlockEntityRenderState createRenderState() {
        return new LecternBlockEntityRenderState();
    }

    public void updateRenderState(LecternBlockEntity lecternBlockEntity, LecternBlockEntityRenderState lecternBlockEntityRenderState, float f, Vec3d vec3d, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        super.updateRenderState((BlockEntity)lecternBlockEntity, (BlockEntityRenderState)lecternBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        lecternBlockEntityRenderState.hasBook = (Boolean)lecternBlockEntity.getCachedState().get((Property)LecternBlock.HAS_BOOK);
        lecternBlockEntityRenderState.bookRotationDegrees = ((Direction)lecternBlockEntity.getCachedState().get((Property)LecternBlock.FACING)).rotateYClockwise().getPositiveHorizontalDegrees();
    }

    public void render(LecternBlockEntityRenderState lecternBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (!lecternBlockEntityRenderState.hasBook) {
            return;
        }
        matrixStack.push();
        matrixStack.translate(0.5f, 1.0625f, 0.5f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(-lecternBlockEntityRenderState.bookRotationDegrees));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(67.5f));
        matrixStack.translate(0.0f, -0.125f, 0.0f);
        orderedRenderCommandQueue.submitModel((Model)this.book, (Object)this.bookModelState, matrixStack, EnchantingTableBlockEntityRenderer.BOOK_TEXTURE.getRenderLayer(RenderLayers::entitySolid), lecternBlockEntityRenderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, -1, this.spriteHolder.getSprite(EnchantingTableBlockEntityRenderer.BOOK_TEXTURE), 0, lecternBlockEntityRenderState.crumblingOverlay);
        matrixStack.pop();
    }

    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

