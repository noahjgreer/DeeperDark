/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.entity;

import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class EntityRenderer<T extends Entity, S extends EntityRenderState> {
    private static final float field_61797 = 0.5f;
    private static final float field_61798 = 32.0f;
    public static final float field_32921 = 0.025f;
    protected final EntityRenderManager dispatcher;
    private final TextRenderer textRenderer;
    protected float shadowRadius;
    protected float shadowOpacity = 1.0f;

    protected EntityRenderer(EntityRendererFactory.Context context) {
        this.dispatcher = context.getRenderDispatcher();
        this.textRenderer = context.getTextRenderer();
    }

    public final int getLight(T entity, float tickProgress) {
        BlockPos blockPos = BlockPos.ofFloored(((Entity)entity).getClientCameraPosVec(tickProgress));
        return LightmapTextureManager.pack(this.getBlockLight(entity, blockPos), this.getSkyLight(entity, blockPos));
    }

    protected int getSkyLight(T entity, BlockPos pos) {
        return ((Entity)entity).getEntityWorld().getLightLevel(LightType.SKY, pos);
    }

    protected int getBlockLight(T entity, BlockPos pos) {
        if (((Entity)entity).isOnFire()) {
            return 15;
        }
        return ((Entity)entity).getEntityWorld().getLightLevel(LightType.BLOCK, pos);
    }

    public boolean shouldRender(T entity, Frustum frustum, double x, double y, double z) {
        Leashable leashable;
        Entity entity2;
        if (!((Entity)entity).shouldRender(x, y, z)) {
            return false;
        }
        if (!this.canBeCulled(entity)) {
            return true;
        }
        Box box = this.getBoundingBox(entity).expand(0.5);
        if (box.isNaN() || box.getAverageSideLength() == 0.0) {
            box = new Box(((Entity)entity).getX() - 2.0, ((Entity)entity).getY() - 2.0, ((Entity)entity).getZ() - 2.0, ((Entity)entity).getX() + 2.0, ((Entity)entity).getY() + 2.0, ((Entity)entity).getZ() + 2.0);
        }
        if (frustum.isVisible(box)) {
            return true;
        }
        if (entity instanceof Leashable && (entity2 = (leashable = (Leashable)entity).getLeashHolder()) != null) {
            Box box2 = this.dispatcher.getRenderer(entity2).getBoundingBox(entity2);
            return frustum.isVisible(box2) || frustum.isVisible(box.union(box2));
        }
        return false;
    }

    protected Box getBoundingBox(T entity) {
        return ((Entity)entity).getBoundingBox();
    }

    protected boolean canBeCulled(T entity) {
        return true;
    }

    public Vec3d getPositionOffset(S state) {
        if (((EntityRenderState)state).positionOffset != null) {
            return ((EntityRenderState)state).positionOffset;
        }
        return Vec3d.ZERO;
    }

    public void render(S renderState, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        if (((EntityRenderState)renderState).leashDatas != null) {
            for (EntityRenderState.LeashData leashData : ((EntityRenderState)renderState).leashDatas) {
                queue.submitLeash(matrices, leashData);
            }
        }
        this.renderLabelIfPresent(renderState, matrices, queue, cameraState);
    }

    protected boolean hasLabel(T entity, double squaredDistanceToCamera) {
        return ((Entity)entity).shouldRenderName() || ((Entity)entity).hasCustomName() && entity == this.dispatcher.targetedEntity;
    }

    public TextRenderer getTextRenderer() {
        return this.textRenderer;
    }

    protected void renderLabelIfPresent(S state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraRenderState) {
        if (((EntityRenderState)state).displayName != null) {
            queue.submitLabel(matrices, ((EntityRenderState)state).nameLabelPos, 0, ((EntityRenderState)state).displayName, !((EntityRenderState)state).sneaking, ((EntityRenderState)state).light, ((EntityRenderState)state).squaredDistanceToCamera, cameraRenderState);
        }
    }

    protected @Nullable Text getDisplayName(T entity) {
        return ((Entity)entity).getDisplayName();
    }

    protected float getShadowRadius(S state) {
        return this.shadowRadius;
    }

    protected float getShadowOpacity(S state) {
        return this.shadowOpacity;
    }

    public abstract S createRenderState();

    public final S getAndUpdateRenderState(T entity, float tickProgress) {
        S entityRenderState = this.createRenderState();
        this.updateRenderState(entity, entityRenderState, tickProgress);
        this.updateShadow(entity, entityRenderState);
        return entityRenderState;
    }

    public void updateRenderState(T entity, S state, float tickProgress) {
        Leashable leashable;
        Entity entity2;
        ExperimentalMinecartController experimentalMinecartController;
        AbstractMinecartEntity abstractMinecartEntity;
        Object object;
        ((EntityRenderState)state).entityType = ((Entity)entity).getType();
        ((EntityRenderState)state).x = MathHelper.lerp((double)tickProgress, ((Entity)entity).lastRenderX, ((Entity)entity).getX());
        ((EntityRenderState)state).y = MathHelper.lerp((double)tickProgress, ((Entity)entity).lastRenderY, ((Entity)entity).getY());
        ((EntityRenderState)state).z = MathHelper.lerp((double)tickProgress, ((Entity)entity).lastRenderZ, ((Entity)entity).getZ());
        ((EntityRenderState)state).invisible = ((Entity)entity).isInvisible();
        ((EntityRenderState)state).age = (float)((Entity)entity).age + tickProgress;
        ((EntityRenderState)state).width = ((Entity)entity).getWidth();
        ((EntityRenderState)state).height = ((Entity)entity).getHeight();
        ((EntityRenderState)state).standingEyeHeight = ((Entity)entity).getStandingEyeHeight();
        if (((Entity)entity).hasVehicle() && (object = ((Entity)entity).getVehicle()) instanceof AbstractMinecartEntity && (object = (abstractMinecartEntity = (AbstractMinecartEntity)object).getController()) instanceof ExperimentalMinecartController && (experimentalMinecartController = (ExperimentalMinecartController)object).hasCurrentLerpSteps()) {
            double d = MathHelper.lerp((double)tickProgress, abstractMinecartEntity.lastRenderX, abstractMinecartEntity.getX());
            double e = MathHelper.lerp((double)tickProgress, abstractMinecartEntity.lastRenderY, abstractMinecartEntity.getY());
            double f = MathHelper.lerp((double)tickProgress, abstractMinecartEntity.lastRenderZ, abstractMinecartEntity.getZ());
            ((EntityRenderState)state).positionOffset = experimentalMinecartController.getLerpedPosition(tickProgress).subtract(new Vec3d(d, e, f));
        } else {
            ((EntityRenderState)state).positionOffset = null;
        }
        if (this.dispatcher.camera != null) {
            boolean bl;
            ((EntityRenderState)state).squaredDistanceToCamera = this.dispatcher.getSquaredDistanceToCamera((Entity)entity);
            boolean bl2 = bl = ((EntityRenderState)state).squaredDistanceToCamera < 4096.0 && this.hasLabel(entity, ((EntityRenderState)state).squaredDistanceToCamera);
            if (bl) {
                ((EntityRenderState)state).displayName = this.getDisplayName(entity);
                ((EntityRenderState)state).nameLabelPos = ((Entity)entity).getAttachments().getPointNullable(EntityAttachmentType.NAME_TAG, 0, ((Entity)entity).getLerpedYaw(tickProgress));
            } else {
                ((EntityRenderState)state).displayName = null;
            }
        }
        ((EntityRenderState)state).sneaking = ((Entity)entity).isSneaky();
        World world = ((Entity)entity).getEntityWorld();
        if (entity instanceof Leashable && (entity2 = (leashable = (Leashable)entity).getLeashHolder()) instanceof Entity) {
            int m;
            Entity entity22 = entity2;
            float g = ((Entity)entity).lerpYaw(tickProgress) * ((float)Math.PI / 180);
            Vec3d vec3d = leashable.getLeashOffset(tickProgress);
            BlockPos blockPos = BlockPos.ofFloored(((Entity)entity).getCameraPosVec(tickProgress));
            BlockPos blockPos2 = BlockPos.ofFloored(entity22.getCameraPosVec(tickProgress));
            int i = this.getBlockLight(entity, blockPos);
            int j = this.dispatcher.getRenderer(entity22).getBlockLight(entity22, blockPos2);
            int k = world.getLightLevel(LightType.SKY, blockPos);
            int l = world.getLightLevel(LightType.SKY, blockPos2);
            boolean bl2 = entity22.hasQuadLeashAttachmentPoints() && leashable.canUseQuadLeashAttachmentPoint();
            int n = m = bl2 ? 4 : 1;
            if (((EntityRenderState)state).leashDatas == null || ((EntityRenderState)state).leashDatas.size() != m) {
                ((EntityRenderState)state).leashDatas = new ArrayList<EntityRenderState.LeashData>(m);
                for (int n2 = 0; n2 < m; ++n2) {
                    ((EntityRenderState)state).leashDatas.add(new EntityRenderState.LeashData());
                }
            }
            if (bl2) {
                float h = entity22.lerpYaw(tickProgress) * ((float)Math.PI / 180);
                Vec3d vec3d2 = entity22.getLerpedPos(tickProgress);
                Vec3d[] vec3ds = leashable.getQuadLeashOffsets();
                Vec3d[] vec3ds2 = entity22.getHeldQuadLeashOffsets();
                for (int o = 0; o < m; ++o) {
                    EntityRenderState.LeashData leashData = ((EntityRenderState)state).leashDatas.get(o);
                    leashData.offset = vec3ds[o].rotateY(-g);
                    leashData.startPos = ((Entity)entity).getLerpedPos(tickProgress).add(leashData.offset);
                    leashData.endPos = vec3d2.add(vec3ds2[o].rotateY(-h));
                    leashData.leashedEntityBlockLight = i;
                    leashData.leashHolderBlockLight = j;
                    leashData.leashedEntitySkyLight = k;
                    leashData.leashHolderSkyLight = l;
                    leashData.slack = false;
                }
            } else {
                Vec3d vec3d3 = vec3d.rotateY(-g);
                EntityRenderState.LeashData leashData2 = ((EntityRenderState)state).leashDatas.getFirst();
                leashData2.offset = vec3d3;
                leashData2.startPos = ((Entity)entity).getLerpedPos(tickProgress).add(vec3d3);
                leashData2.endPos = entity22.getLeashPos(tickProgress);
                leashData2.leashedEntityBlockLight = i;
                leashData2.leashHolderBlockLight = j;
                leashData2.leashedEntitySkyLight = k;
                leashData2.leashHolderSkyLight = l;
            }
        } else {
            ((EntityRenderState)state).leashDatas = null;
        }
        ((EntityRenderState)state).onFire = ((Entity)entity).doesRenderOnFire();
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        boolean bl3 = minecraftClient.hasOutline((Entity)entity);
        ((EntityRenderState)state).outlineColor = bl3 ? ColorHelper.fullAlpha(((Entity)entity).getTeamColorValue()) : 0;
        ((EntityRenderState)state).light = this.getLight(entity, tickProgress);
    }

    protected void updateShadow(T entity, S renderState) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        World world = ((Entity)entity).getEntityWorld();
        this.updateShadow(renderState, minecraftClient, world);
    }

    private void updateShadow(S renderState, MinecraftClient client, World world) {
        ((EntityRenderState)renderState).shadowPieces.clear();
        if (client.options.getEntityShadows().getValue().booleanValue() && !((EntityRenderState)renderState).invisible) {
            double d;
            float g;
            float f;
            ((EntityRenderState)renderState).shadowRadius = f = Math.min(this.getShadowRadius(renderState), 32.0f);
            if (f > 0.0f && (g = (float)((1.0 - (d = ((EntityRenderState)renderState).squaredDistanceToCamera) / 256.0) * (double)this.getShadowOpacity(renderState))) > 0.0f) {
                int i = MathHelper.floor(((EntityRenderState)renderState).x - (double)f);
                int j = MathHelper.floor(((EntityRenderState)renderState).x + (double)f);
                int k = MathHelper.floor(((EntityRenderState)renderState).z - (double)f);
                int l = MathHelper.floor(((EntityRenderState)renderState).z + (double)f);
                float h = Math.min(g / 0.5f - 1.0f, f);
                int m = MathHelper.floor(((EntityRenderState)renderState).y - (double)h);
                int n = MathHelper.floor(((EntityRenderState)renderState).y);
                BlockPos.Mutable mutable = new BlockPos.Mutable();
                for (int o = k; o <= l; ++o) {
                    for (int p = i; p <= j; ++p) {
                        mutable.set(p, 0, o);
                        Chunk chunk = world.getChunk(mutable);
                        for (int q = m; q <= n; ++q) {
                            mutable.setY(q);
                            this.addShadowPiece(renderState, world, g, mutable, chunk);
                        }
                    }
                }
            }
        } else {
            ((EntityRenderState)renderState).shadowRadius = 0.0f;
        }
    }

    private void addShadowPiece(S renderState, World world, float shadowOpacity, BlockPos.Mutable pos, Chunk chunk) {
        float f = shadowOpacity - (float)(((EntityRenderState)renderState).y - (double)pos.getY()) * 0.5f;
        Vec3i blockPos = pos.down();
        BlockState blockState = chunk.getBlockState((BlockPos)blockPos);
        if (blockState.getRenderType() == BlockRenderType.INVISIBLE) {
            return;
        }
        int i = world.getLightLevel(pos);
        if (i <= 3) {
            return;
        }
        if (!blockState.isFullCube(chunk, (BlockPos)blockPos)) {
            return;
        }
        VoxelShape voxelShape = blockState.getOutlineShape(chunk, (BlockPos)blockPos);
        if (voxelShape.isEmpty()) {
            return;
        }
        float g = MathHelper.clamp(f * 0.5f * LightmapTextureManager.getBrightness(world.getDimension(), i), 0.0f, 1.0f);
        float h = (float)((double)pos.getX() - ((EntityRenderState)renderState).x);
        float j = (float)((double)pos.getY() - ((EntityRenderState)renderState).y);
        float k = (float)((double)pos.getZ() - ((EntityRenderState)renderState).z);
        ((EntityRenderState)renderState).shadowPieces.add(new EntityRenderState.ShadowPiece(h, j, k, voxelShape, g));
    }

    private static @Nullable Entity getServerEntity(Entity clientEntity) {
        ServerWorld serverWorld;
        IntegratedServer integratedServer = MinecraftClient.getInstance().getServer();
        if (integratedServer != null && (serverWorld = integratedServer.getWorld(clientEntity.getEntityWorld().getRegistryKey())) != null) {
            return serverWorld.getEntityById(clientEntity.getId());
        }
        return null;
    }
}
