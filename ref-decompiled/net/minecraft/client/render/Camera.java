/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.enums.CameraSubmersionType
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.render.Camera$Projection
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.attribute.EntityAttributes
 *  net.minecraft.entity.vehicle.ExperimentalMinecartController
 *  net.minecraft.entity.vehicle.MinecartEntity
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.registry.tag.FluidTags
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$Mutable
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext$FluidHandling
 *  net.minecraft.world.RaycastContext$ShapeType
 *  net.minecraft.world.World
 *  net.minecraft.world.attribute.EnvironmentAttributeInterpolator
 *  net.minecraft.world.waypoint.TrackedWaypoint$YawProvider
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render;

import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.attribute.EnvironmentAttributeInterpolator;
import net.minecraft.world.waypoint.TrackedWaypoint;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public class Camera
implements TrackedWaypoint.YawProvider {
    private static final float BASE_CAMERA_DISTANCE = 4.0f;
    private static final Vector3f HORIZONTAL = new Vector3f(0.0f, 0.0f, -1.0f);
    private static final Vector3f VERTICAL = new Vector3f(0.0f, 1.0f, 0.0f);
    private static final Vector3f DIAGONAL = new Vector3f(-1.0f, 0.0f, 0.0f);
    private boolean ready;
    private World area;
    private Entity focusedEntity;
    private Vec3d pos = Vec3d.ZERO;
    private final BlockPos.Mutable blockPos = new BlockPos.Mutable();
    private final Vector3f horizontalPlane = new Vector3f((Vector3fc)HORIZONTAL);
    private final Vector3f verticalPlane = new Vector3f((Vector3fc)VERTICAL);
    private final Vector3f diagonalPlane = new Vector3f((Vector3fc)DIAGONAL);
    private float pitch;
    private float yaw;
    private final Quaternionf rotation = new Quaternionf();
    private boolean thirdPerson;
    private float cameraY;
    private float lastCameraY;
    private float lastTickProgress;
    private final EnvironmentAttributeInterpolator environmentAttributeInterpolator = new EnvironmentAttributeInterpolator();

    public void update(World area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickProgress) {
        ExperimentalMinecartController experimentalMinecartController;
        MinecartEntity minecartEntity;
        Entity entity;
        this.ready = true;
        this.area = area;
        this.focusedEntity = focusedEntity;
        this.thirdPerson = thirdPerson;
        this.lastTickProgress = tickProgress;
        if (focusedEntity.hasVehicle() && (entity = focusedEntity.getVehicle()) instanceof MinecartEntity && (entity = (minecartEntity = (MinecartEntity)entity).getController()) instanceof ExperimentalMinecartController && (experimentalMinecartController = (ExperimentalMinecartController)entity).hasCurrentLerpSteps()) {
            Vec3d vec3d = minecartEntity.getPassengerRidingPos(focusedEntity).subtract(minecartEntity.getEntityPos()).subtract(focusedEntity.getVehicleAttachmentPos((Entity)minecartEntity)).add(new Vec3d(0.0, (double)MathHelper.lerp((float)tickProgress, (float)this.lastCameraY, (float)this.cameraY), 0.0));
            this.setRotation(focusedEntity.getYaw(tickProgress), focusedEntity.getPitch(tickProgress));
            this.setPos(experimentalMinecartController.getLerpedPosition(tickProgress).add(vec3d));
        } else {
            this.setRotation(focusedEntity.getYaw(tickProgress), focusedEntity.getPitch(tickProgress));
            this.setPos(MathHelper.lerp((double)tickProgress, (double)focusedEntity.lastX, (double)focusedEntity.getX()), MathHelper.lerp((double)tickProgress, (double)focusedEntity.lastY, (double)focusedEntity.getY()) + (double)MathHelper.lerp((float)tickProgress, (float)this.lastCameraY, (float)this.cameraY), MathHelper.lerp((double)tickProgress, (double)focusedEntity.lastZ, (double)focusedEntity.getZ()));
        }
        if (thirdPerson) {
            Entity entity2;
            if (inverseView) {
                this.setRotation(this.yaw + 180.0f, -this.pitch);
            }
            float f = 4.0f;
            float g = 1.0f;
            if (focusedEntity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)focusedEntity;
                g = livingEntity.getScale();
                f = (float)livingEntity.getAttributeValue(EntityAttributes.CAMERA_DISTANCE);
            }
            float h = g;
            float i = f;
            if (focusedEntity.hasVehicle() && (entity2 = focusedEntity.getVehicle()) instanceof LivingEntity) {
                LivingEntity livingEntity2 = (LivingEntity)entity2;
                h = livingEntity2.getScale();
                i = (float)livingEntity2.getAttributeValue(EntityAttributes.CAMERA_DISTANCE);
            }
            this.moveBy(-this.clipToSpace(Math.max(g * f, h * i)), 0.0f, 0.0f);
        } else if (focusedEntity instanceof LivingEntity && ((LivingEntity)focusedEntity).isSleeping()) {
            Direction direction = ((LivingEntity)focusedEntity).getSleepingDirection();
            this.setRotation(direction != null ? direction.getPositiveHorizontalDegrees() - 180.0f : 0.0f, 0.0f);
            this.moveBy(0.0f, 0.3f, 0.0f);
        }
    }

    public void updateEyeHeight() {
        if (this.focusedEntity != null) {
            this.lastCameraY = this.cameraY;
            this.cameraY += (this.focusedEntity.getStandingEyeHeight() - this.cameraY) * 0.5f;
            this.environmentAttributeInterpolator.update(this.area, this.pos);
        }
    }

    private float clipToSpace(float distance) {
        float f = 0.1f;
        for (int i = 0; i < 8; ++i) {
            float k;
            Vec3d vec3d2;
            float g = (i & 1) * 2 - 1;
            float h = (i >> 1 & 1) * 2 - 1;
            float j = (i >> 2 & 1) * 2 - 1;
            Vec3d vec3d = this.pos.add((double)(g * 0.1f), (double)(h * 0.1f), (double)(j * 0.1f));
            BlockHitResult hitResult = this.area.raycast(new RaycastContext(vec3d, vec3d2 = vec3d.add(new Vec3d((Vector3fc)this.horizontalPlane).multiply((double)(-distance))), RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, this.focusedEntity));
            if (hitResult.getType() == HitResult.Type.MISS || !((k = (float)hitResult.getPos().squaredDistanceTo(this.pos)) < MathHelper.square((float)distance))) continue;
            distance = MathHelper.sqrt((float)k);
        }
        return distance;
    }

    protected void moveBy(float surge, float heave, float sway) {
        Vector3f vector3f = new Vector3f(sway, heave, -surge).rotate((Quaternionfc)this.rotation);
        this.setPos(new Vec3d(this.pos.x + (double)vector3f.x, this.pos.y + (double)vector3f.y, this.pos.z + (double)vector3f.z));
    }

    protected void setRotation(float yaw, float pitch) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.rotation.rotationYXZ((float)Math.PI - yaw * ((float)Math.PI / 180), -pitch * ((float)Math.PI / 180), 0.0f);
        HORIZONTAL.rotate((Quaternionfc)this.rotation, this.horizontalPlane);
        VERTICAL.rotate((Quaternionfc)this.rotation, this.verticalPlane);
        DIAGONAL.rotate((Quaternionfc)this.rotation, this.diagonalPlane);
    }

    protected void setPos(double x, double y, double z) {
        this.setPos(new Vec3d(x, y, z));
    }

    protected void setPos(Vec3d pos) {
        this.pos = pos;
        this.blockPos.set(pos.x, pos.y, pos.z);
    }

    public Vec3d getCameraPos() {
        return this.pos;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getCameraYaw() {
        return MathHelper.wrapDegrees((float)this.getYaw());
    }

    public Quaternionf getRotation() {
        return this.rotation;
    }

    public Entity getFocusedEntity() {
        return this.focusedEntity;
    }

    public boolean isReady() {
        return this.ready;
    }

    public boolean isThirdPerson() {
        return this.thirdPerson;
    }

    public EnvironmentAttributeInterpolator getEnvironmentAttributeInterpolator() {
        return this.environmentAttributeInterpolator;
    }

    public Projection getProjection() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        double d = (double)minecraftClient.getWindow().getFramebufferWidth() / (double)minecraftClient.getWindow().getFramebufferHeight();
        double e = Math.tan((double)((float)((Integer)minecraftClient.options.getFov().getValue()).intValue() * ((float)Math.PI / 180)) / 2.0) * (double)0.05f;
        double f = e * d;
        Vec3d vec3d = new Vec3d((Vector3fc)this.horizontalPlane).multiply((double)0.05f);
        Vec3d vec3d2 = new Vec3d((Vector3fc)this.diagonalPlane).multiply(f);
        Vec3d vec3d3 = new Vec3d((Vector3fc)this.verticalPlane).multiply(e);
        return new Projection(vec3d, vec3d2, vec3d3);
    }

    public CameraSubmersionType getSubmersionType() {
        if (!this.ready) {
            return CameraSubmersionType.NONE;
        }
        FluidState fluidState = this.area.getFluidState((BlockPos)this.blockPos);
        if (fluidState.isIn(FluidTags.WATER) && this.pos.y < (double)((float)this.blockPos.getY() + fluidState.getHeight((BlockView)this.area, (BlockPos)this.blockPos))) {
            return CameraSubmersionType.WATER;
        }
        Projection projection = this.getProjection();
        List<Vec3d> list = Arrays.asList(projection.center, projection.getBottomRight(), projection.getTopRight(), projection.getBottomLeft(), projection.getTopLeft());
        for (Vec3d vec3d : list) {
            Vec3d vec3d2 = this.pos.add(vec3d);
            BlockPos blockPos = BlockPos.ofFloored((Position)vec3d2);
            FluidState fluidState2 = this.area.getFluidState(blockPos);
            if (fluidState2.isIn(FluidTags.LAVA)) {
                if (!(vec3d2.y <= (double)(fluidState2.getHeight((BlockView)this.area, blockPos) + (float)blockPos.getY()))) continue;
                return CameraSubmersionType.LAVA;
            }
            BlockState blockState = this.area.getBlockState(blockPos);
            if (!blockState.isOf(Blocks.POWDER_SNOW)) continue;
            return CameraSubmersionType.POWDER_SNOW;
        }
        return CameraSubmersionType.NONE;
    }

    public Vector3fc getHorizontalPlane() {
        return this.horizontalPlane;
    }

    public Vector3fc getVerticalPlane() {
        return this.verticalPlane;
    }

    public Vector3fc getDiagonalPlane() {
        return this.diagonalPlane;
    }

    public void reset() {
        this.area = null;
        this.focusedEntity = null;
        this.environmentAttributeInterpolator.clear();
        this.ready = false;
    }

    public float getLastTickProgress() {
        return this.lastTickProgress;
    }
}

