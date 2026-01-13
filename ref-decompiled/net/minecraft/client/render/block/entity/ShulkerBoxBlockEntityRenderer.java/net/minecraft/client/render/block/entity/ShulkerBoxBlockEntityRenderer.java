/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 *  org.joml.Vector3fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.ShulkerBoxBlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ShulkerBoxBlockEntityRenderer
implements BlockEntityRenderer<ShulkerBoxBlockEntity, ShulkerBoxBlockEntityRenderState> {
    private final SpriteHolder materials;
    private final ShulkerBoxBlockModel model;

    public ShulkerBoxBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this(ctx.loadedEntityModels(), ctx.spriteHolder());
    }

    public ShulkerBoxBlockEntityRenderer(SpecialModelRenderer.BakeContext context) {
        this(context.entityModelSet(), context.spriteHolder());
    }

    public ShulkerBoxBlockEntityRenderer(LoadedEntityModels models, SpriteHolder materials) {
        this.materials = materials;
        this.model = new ShulkerBoxBlockModel(models.getModelPart(EntityModelLayers.SHULKER_BOX));
    }

    @Override
    public ShulkerBoxBlockEntityRenderState createRenderState() {
        return new ShulkerBoxBlockEntityRenderState();
    }

    @Override
    public void updateRenderState(ShulkerBoxBlockEntity shulkerBoxBlockEntity, ShulkerBoxBlockEntityRenderState shulkerBoxBlockEntityRenderState, float f, Vec3d vec3d,  @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        BlockEntityRenderer.super.updateRenderState(shulkerBoxBlockEntity, shulkerBoxBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        shulkerBoxBlockEntityRenderState.facing = shulkerBoxBlockEntity.getCachedState().get(ShulkerBoxBlock.FACING, Direction.UP);
        shulkerBoxBlockEntityRenderState.dyeColor = shulkerBoxBlockEntity.getColor();
        shulkerBoxBlockEntityRenderState.animationProgress = shulkerBoxBlockEntity.getAnimationProgress(f);
    }

    @Override
    public void render(ShulkerBoxBlockEntityRenderState shulkerBoxBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        DyeColor dyeColor = shulkerBoxBlockEntityRenderState.dyeColor;
        SpriteIdentifier spriteIdentifier = dyeColor == null ? TexturedRenderLayers.SHULKER_TEXTURE_ID : TexturedRenderLayers.getShulkerBoxTextureId(dyeColor);
        this.render(matrixStack, orderedRenderCommandQueue, shulkerBoxBlockEntityRenderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, shulkerBoxBlockEntityRenderState.facing, shulkerBoxBlockEntityRenderState.animationProgress, shulkerBoxBlockEntityRenderState.crumblingOverlay, spriteIdentifier, 0);
    }

    public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, Direction facing, float openness,  @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay, SpriteIdentifier spriteId, int i) {
        matrices.push();
        this.setTransforms(matrices, facing, openness);
        queue.submitModel(this.model, Float.valueOf(openness), matrices, spriteId.getRenderLayer(this.model::getLayer), light, overlay, -1, this.materials.getSprite(spriteId), i, crumblingOverlay);
        matrices.pop();
    }

    private void setTransforms(MatrixStack matrices, Direction facing, float openness) {
        matrices.translate(0.5f, 0.5f, 0.5f);
        float f = 0.9995f;
        matrices.scale(0.9995f, 0.9995f, 0.9995f);
        matrices.multiply((Quaternionfc)facing.getRotationQuaternion());
        matrices.scale(1.0f, -1.0f, -1.0f);
        matrices.translate(0.0f, -1.0f, 0.0f);
        this.model.setAngles(Float.valueOf(openness));
    }

    public void collectVertices(Direction facing, float openness, Consumer<Vector3fc> consumer) {
        MatrixStack matrixStack = new MatrixStack();
        this.setTransforms(matrixStack, facing, openness);
        this.model.getRootPart().collectVertices(matrixStack, consumer);
    }

    @Override
    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Environment(value=EnvType.CLIENT)
    static class ShulkerBoxBlockModel
    extends Model<Float> {
        private final ModelPart lid;

        public ShulkerBoxBlockModel(ModelPart root) {
            super(root, RenderLayers::entityCutoutNoCull);
            this.lid = root.getChild("lid");
        }

        @Override
        public void setAngles(Float float_) {
            super.setAngles(float_);
            this.lid.setOrigin(0.0f, 24.0f - float_.floatValue() * 0.5f * 16.0f, 0.0f);
            this.lid.yaw = 270.0f * float_.floatValue() * ((float)Math.PI / 180);
        }
    }
}
