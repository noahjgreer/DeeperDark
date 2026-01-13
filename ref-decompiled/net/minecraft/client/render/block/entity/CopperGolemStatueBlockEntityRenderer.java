/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.Block
 *  net.minecraft.block.CopperGolemStatueBlock
 *  net.minecraft.block.CopperGolemStatueBlock$Pose
 *  net.minecraft.block.Oxidizable$OxidationLevel
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.CopperGolemStatueBlockEntity
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory$Context
 *  net.minecraft.client.render.block.entity.CopperGolemStatueBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.model.CopperGolemStatueModel
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.CopperGolemStatueBlockEntityRenderState
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.passive.CopperGolemOxidationLevels
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3d
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.CopperGolemStatueBlock;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CopperGolemStatueBlockEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.model.CopperGolemStatueModel;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.CopperGolemStatueBlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.CopperGolemOxidationLevels;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CopperGolemStatueBlockEntityRenderer
implements BlockEntityRenderer<CopperGolemStatueBlockEntity, CopperGolemStatueBlockEntityRenderState> {
    private final Map<CopperGolemStatueBlock.Pose, CopperGolemStatueModel> models = new HashMap();

    public CopperGolemStatueBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        LoadedEntityModels loadedEntityModels = context.loadedEntityModels();
        this.models.put(CopperGolemStatueBlock.Pose.STANDING, new CopperGolemStatueModel(loadedEntityModels.getModelPart(EntityModelLayers.COPPER_GOLEM)));
        this.models.put(CopperGolemStatueBlock.Pose.RUNNING, new CopperGolemStatueModel(loadedEntityModels.getModelPart(EntityModelLayers.COPPER_GOLEM_RUNNING)));
        this.models.put(CopperGolemStatueBlock.Pose.SITTING, new CopperGolemStatueModel(loadedEntityModels.getModelPart(EntityModelLayers.COPPER_GOLEM_SITTING)));
        this.models.put(CopperGolemStatueBlock.Pose.STAR, new CopperGolemStatueModel(loadedEntityModels.getModelPart(EntityModelLayers.COPPER_GOLEM_STAR)));
    }

    public CopperGolemStatueBlockEntityRenderState createRenderState() {
        return new CopperGolemStatueBlockEntityRenderState();
    }

    public void updateRenderState(CopperGolemStatueBlockEntity copperGolemStatueBlockEntity, CopperGolemStatueBlockEntityRenderState copperGolemStatueBlockEntityRenderState, float f, Vec3d vec3d, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        super.updateRenderState((BlockEntity)copperGolemStatueBlockEntity, (BlockEntityRenderState)copperGolemStatueBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        copperGolemStatueBlockEntityRenderState.facing = (Direction)copperGolemStatueBlockEntity.getCachedState().get((Property)CopperGolemStatueBlock.FACING);
        copperGolemStatueBlockEntityRenderState.pose = (CopperGolemStatueBlock.Pose)copperGolemStatueBlockEntity.getCachedState().get((Property)Properties.COPPER_GOLEM_POSE);
    }

    public void render(CopperGolemStatueBlockEntityRenderState copperGolemStatueBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        Block block = copperGolemStatueBlockEntityRenderState.blockState.getBlock();
        if (block instanceof CopperGolemStatueBlock) {
            CopperGolemStatueBlock copperGolemStatueBlock = (CopperGolemStatueBlock)block;
            matrixStack.push();
            matrixStack.translate(0.5f, 0.0f, 0.5f);
            CopperGolemStatueModel copperGolemStatueModel = (CopperGolemStatueModel)this.models.get(copperGolemStatueBlockEntityRenderState.pose);
            Direction direction = copperGolemStatueBlockEntityRenderState.facing;
            RenderLayer renderLayer = RenderLayers.entityCutoutNoCull((Identifier)CopperGolemOxidationLevels.get((Oxidizable.OxidationLevel)copperGolemStatueBlock.getOxidationLevel()).texture());
            orderedRenderCommandQueue.submitModel((Model)copperGolemStatueModel, (Object)direction, matrixStack, renderLayer, copperGolemStatueBlockEntityRenderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, 0, copperGolemStatueBlockEntityRenderState.crumblingOverlay);
            matrixStack.pop();
        }
    }

    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

