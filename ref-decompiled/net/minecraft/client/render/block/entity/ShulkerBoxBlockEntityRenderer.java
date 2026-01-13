/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.ShulkerBoxBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.ShulkerBoxBlockEntity
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.TexturedRenderLayers
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory$Context
 *  net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer$ShulkerBoxBlockModel
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.ShulkerBoxBlockEntityRenderState
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.item.model.special.SpecialModelRenderer$BakeContext
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.texture.SpriteHolder
 *  net.minecraft.client.util.SpriteIdentifier
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.DyeColor
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Quaternionfc
 *  org.joml.Vector3fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
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
import net.minecraft.state.property.Property;
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

    public ShulkerBoxBlockEntityRenderState createRenderState() {
        return new ShulkerBoxBlockEntityRenderState();
    }

    public void updateRenderState(ShulkerBoxBlockEntity shulkerBoxBlockEntity, ShulkerBoxBlockEntityRenderState shulkerBoxBlockEntityRenderState, float f, Vec3d vec3d, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        super.updateRenderState((BlockEntity)shulkerBoxBlockEntity, (BlockEntityRenderState)shulkerBoxBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        shulkerBoxBlockEntityRenderState.facing = (Direction)shulkerBoxBlockEntity.getCachedState().get((Property)ShulkerBoxBlock.FACING, (Comparable)Direction.UP);
        shulkerBoxBlockEntityRenderState.dyeColor = shulkerBoxBlockEntity.getColor();
        shulkerBoxBlockEntityRenderState.animationProgress = shulkerBoxBlockEntity.getAnimationProgress(f);
    }

    public void render(ShulkerBoxBlockEntityRenderState shulkerBoxBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        DyeColor dyeColor = shulkerBoxBlockEntityRenderState.dyeColor;
        SpriteIdentifier spriteIdentifier = dyeColor == null ? TexturedRenderLayers.SHULKER_TEXTURE_ID : TexturedRenderLayers.getShulkerBoxTextureId((DyeColor)dyeColor);
        this.render(matrixStack, orderedRenderCommandQueue, shulkerBoxBlockEntityRenderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, shulkerBoxBlockEntityRenderState.facing, shulkerBoxBlockEntityRenderState.animationProgress, shulkerBoxBlockEntityRenderState.crumblingOverlay, spriteIdentifier, 0);
    }

    public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, Direction facing, float openness, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay, SpriteIdentifier spriteId, int i) {
        matrices.push();
        this.setTransforms(matrices, facing, openness);
        queue.submitModel((Model)this.model, (Object)Float.valueOf(openness), matrices, spriteId.getRenderLayer(arg_0 -> ((ShulkerBoxBlockModel)this.model).getLayer(arg_0)), light, overlay, -1, this.materials.getSprite(spriteId), i, crumblingOverlay);
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

    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

