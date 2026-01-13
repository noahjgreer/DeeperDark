/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockState
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.render.MapRenderer
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.block.BlockRenderManager
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.ItemFrameEntityRenderer
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.ItemFrameEntityRenderState
 *  net.minecraft.client.render.model.BlockStateManagers
 *  net.minecraft.client.render.model.BlockStateModel
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.texture.SpriteAtlasTexture
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.component.type.MapIdComponent
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.decoration.ItemFrameEntity
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.map.MapState
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemFrameEntityRenderState;
import net.minecraft.client.render.model.BlockStateManagers;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class ItemFrameEntityRenderer<T extends ItemFrameEntity>
extends EntityRenderer<T, ItemFrameEntityRenderState> {
    public static final int GLOW_FRAME_BLOCK_LIGHT = 5;
    public static final int field_32933 = 30;
    private final ItemModelManager itemModelManager;
    private final MapRenderer mapRenderer;
    private final BlockRenderManager blockRenderManager;

    public ItemFrameEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemModelManager = context.getItemModelManager();
        this.mapRenderer = context.getMapRenderer();
        this.blockRenderManager = context.getBlockRenderManager();
    }

    protected int getBlockLight(T itemFrameEntity, BlockPos blockPos) {
        if (itemFrameEntity.getType() == EntityType.GLOW_ITEM_FRAME) {
            return Math.max(5, super.getBlockLight(itemFrameEntity, blockPos));
        }
        return super.getBlockLight(itemFrameEntity, blockPos);
    }

    public void render(ItemFrameEntityRenderState itemFrameEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        float g;
        float f;
        super.render((EntityRenderState)itemFrameEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
        matrixStack.push();
        Direction direction = itemFrameEntityRenderState.facing;
        Vec3d vec3d = this.getPositionOffset(itemFrameEntityRenderState);
        matrixStack.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
        double d = 0.46875;
        matrixStack.translate((double)direction.getOffsetX() * 0.46875, (double)direction.getOffsetY() * 0.46875, (double)direction.getOffsetZ() * 0.46875);
        if (direction.getAxis().isHorizontal()) {
            f = 0.0f;
            g = 180.0f - direction.getPositiveHorizontalDegrees();
        } else {
            f = -90 * direction.getDirection().offset();
            g = 180.0f;
        }
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(f));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(g));
        if (!itemFrameEntityRenderState.invisible) {
            BlockState blockState = BlockStateManagers.getStateForItemFrame((boolean)itemFrameEntityRenderState.glow, (itemFrameEntityRenderState.mapId != null ? 1 : 0) != 0);
            BlockStateModel blockStateModel = this.blockRenderManager.getModel(blockState);
            matrixStack.push();
            matrixStack.translate(-0.5f, -0.5f, -0.5f);
            orderedRenderCommandQueue.submitBlockStateModel(matrixStack, RenderLayers.entitySolidZOffsetForward((Identifier)SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE), blockStateModel, 1.0f, 1.0f, 1.0f, itemFrameEntityRenderState.light, OverlayTexture.DEFAULT_UV, itemFrameEntityRenderState.outlineColor);
            matrixStack.pop();
        }
        if (itemFrameEntityRenderState.invisible) {
            matrixStack.translate(0.0f, 0.0f, 0.5f);
        } else {
            matrixStack.translate(0.0f, 0.0f, 0.4375f);
        }
        if (itemFrameEntityRenderState.mapId != null) {
            int i = itemFrameEntityRenderState.rotation % 4 * 2;
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees((float)i * 360.0f / 8.0f));
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
            float h = 0.0078125f;
            matrixStack.scale(0.0078125f, 0.0078125f, 0.0078125f);
            matrixStack.translate(-64.0f, -64.0f, 0.0f);
            matrixStack.translate(0.0f, 0.0f, -1.0f);
            int j = this.getLight(itemFrameEntityRenderState.glow, 15728850, itemFrameEntityRenderState.light);
            this.mapRenderer.draw(itemFrameEntityRenderState.mapRenderState, matrixStack, orderedRenderCommandQueue, true, j);
        } else if (!itemFrameEntityRenderState.itemRenderState.isEmpty()) {
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees((float)itemFrameEntityRenderState.rotation * 360.0f / 8.0f));
            int i = this.getLight(itemFrameEntityRenderState.glow, 0xF000F0, itemFrameEntityRenderState.light);
            matrixStack.scale(0.5f, 0.5f, 0.5f);
            itemFrameEntityRenderState.itemRenderState.render(matrixStack, orderedRenderCommandQueue, i, OverlayTexture.DEFAULT_UV, itemFrameEntityRenderState.outlineColor);
        }
        matrixStack.pop();
    }

    private int getLight(boolean glow, int glowLight, int regularLight) {
        return glow ? glowLight : regularLight;
    }

    public Vec3d getPositionOffset(ItemFrameEntityRenderState itemFrameEntityRenderState) {
        return new Vec3d((double)((float)itemFrameEntityRenderState.facing.getOffsetX() * 0.3f), -0.25, (double)((float)itemFrameEntityRenderState.facing.getOffsetZ() * 0.3f));
    }

    protected boolean hasLabel(T itemFrameEntity, double d) {
        return MinecraftClient.isHudEnabled() && this.dispatcher.targetedEntity == itemFrameEntity && itemFrameEntity.getHeldItemStack().getCustomName() != null;
    }

    protected Text getDisplayName(T itemFrameEntity) {
        return itemFrameEntity.getHeldItemStack().getName();
    }

    public ItemFrameEntityRenderState createRenderState() {
        return new ItemFrameEntityRenderState();
    }

    public void updateRenderState(T itemFrameEntity, ItemFrameEntityRenderState itemFrameEntityRenderState, float f) {
        MapState mapState;
        MapIdComponent mapIdComponent;
        super.updateRenderState(itemFrameEntity, (EntityRenderState)itemFrameEntityRenderState, f);
        itemFrameEntityRenderState.facing = itemFrameEntity.getHorizontalFacing();
        ItemStack itemStack = itemFrameEntity.getHeldItemStack();
        this.itemModelManager.updateForNonLivingEntity(itemFrameEntityRenderState.itemRenderState, itemStack, ItemDisplayContext.FIXED, itemFrameEntity);
        itemFrameEntityRenderState.rotation = itemFrameEntity.getRotation();
        itemFrameEntityRenderState.glow = itemFrameEntity.getType() == EntityType.GLOW_ITEM_FRAME;
        itemFrameEntityRenderState.mapId = null;
        if (!itemStack.isEmpty() && (mapIdComponent = itemFrameEntity.getMapId(itemStack)) != null && (mapState = itemFrameEntity.getEntityWorld().getMapState(mapIdComponent)) != null) {
            this.mapRenderer.update(mapIdComponent, mapState, itemFrameEntityRenderState.mapRenderState);
            itemFrameEntityRenderState.mapId = mapIdComponent;
        }
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    protected /* synthetic */ Text getDisplayName(Entity entity) {
        return this.getDisplayName((ItemFrameEntity)entity);
    }
}

