package net.minecraft.client.render;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.entity.vehicle.MinecartController;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.waypoint.TrackedWaypoint;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class Camera implements TrackedWaypoint.YawProvider {
   private static final float BASE_CAMERA_DISTANCE = 4.0F;
   private static final Vector3f HORIZONTAL = new Vector3f(0.0F, 0.0F, -1.0F);
   private static final Vector3f VERTICAL = new Vector3f(0.0F, 1.0F, 0.0F);
   private static final Vector3f DIAGONAL = new Vector3f(-1.0F, 0.0F, 0.0F);
   private boolean ready;
   private BlockView area;
   private Entity focusedEntity;
   private Vec3d pos;
   private final BlockPos.Mutable blockPos;
   private final Vector3f horizontalPlane;
   private final Vector3f verticalPlane;
   private final Vector3f diagonalPlane;
   private float pitch;
   private float yaw;
   private final Quaternionf rotation;
   private boolean thirdPerson;
   private float cameraY;
   private float lastCameraY;
   private float lastTickProgress;
   public static final float field_32133 = 0.083333336F;

   public Camera() {
      this.pos = Vec3d.ZERO;
      this.blockPos = new BlockPos.Mutable();
      this.horizontalPlane = new Vector3f(HORIZONTAL);
      this.verticalPlane = new Vector3f(VERTICAL);
      this.diagonalPlane = new Vector3f(DIAGONAL);
      this.rotation = new Quaternionf();
   }

   public void update(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickProgress) {
      label44: {
         this.ready = true;
         this.area = area;
         this.focusedEntity = focusedEntity;
         this.thirdPerson = thirdPerson;
         this.lastTickProgress = tickProgress;
         if (focusedEntity.hasVehicle()) {
            Entity var8 = focusedEntity.getVehicle();
            if (var8 instanceof MinecartEntity) {
               MinecartEntity minecartEntity = (MinecartEntity)var8;
               MinecartController var15 = minecartEntity.getController();
               if (var15 instanceof ExperimentalMinecartController) {
                  ExperimentalMinecartController experimentalMinecartController = (ExperimentalMinecartController)var15;
                  if (experimentalMinecartController.hasCurrentLerpSteps()) {
                     Vec3d vec3d = minecartEntity.getPassengerRidingPos(focusedEntity).subtract(minecartEntity.getPos()).subtract(focusedEntity.getVehicleAttachmentPos(minecartEntity)).add(new Vec3d(0.0, (double)MathHelper.lerp(tickProgress, this.lastCameraY, this.cameraY), 0.0));
                     this.setRotation(focusedEntity.getYaw(tickProgress), focusedEntity.getPitch(tickProgress));
                     this.setPos(experimentalMinecartController.getLerpedPosition(tickProgress).add(vec3d));
                     break label44;
                  }
               }
            }
         }

         this.setRotation(focusedEntity.getYaw(tickProgress), focusedEntity.getPitch(tickProgress));
         this.setPos(MathHelper.lerp((double)tickProgress, focusedEntity.lastX, focusedEntity.getX()), MathHelper.lerp((double)tickProgress, focusedEntity.lastY, focusedEntity.getY()) + (double)MathHelper.lerp(tickProgress, this.lastCameraY, this.cameraY), MathHelper.lerp((double)tickProgress, focusedEntity.lastZ, focusedEntity.getZ()));
      }

      if (thirdPerson) {
         if (inverseView) {
            this.setRotation(this.yaw + 180.0F, -this.pitch);
         }

         float f = 4.0F;
         float g = 1.0F;
         if (focusedEntity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)focusedEntity;
            g = livingEntity.getScale();
            f = (float)livingEntity.getAttributeValue(EntityAttributes.CAMERA_DISTANCE);
         }

         float h = g;
         float i = f;
         if (focusedEntity.hasVehicle()) {
            Entity var11 = focusedEntity.getVehicle();
            if (var11 instanceof LivingEntity) {
               LivingEntity livingEntity2 = (LivingEntity)var11;
               h = livingEntity2.getScale();
               i = (float)livingEntity2.getAttributeValue(EntityAttributes.CAMERA_DISTANCE);
            }
         }

         this.moveBy(-this.clipToSpace(Math.max(g * f, h * i)), 0.0F, 0.0F);
      } else if (focusedEntity instanceof LivingEntity && ((LivingEntity)focusedEntity).isSleeping()) {
         Direction direction = ((LivingEntity)focusedEntity).getSleepingDirection();
         this.setRotation(direction != null ? direction.getPositiveHorizontalDegrees() - 180.0F : 0.0F, 0.0F);
         this.moveBy(0.0F, 0.3F, 0.0F);
      }

   }

   public void updateEyeHeight() {
      if (this.focusedEntity != null) {
         this.lastCameraY = this.cameraY;
         this.cameraY += (this.focusedEntity.getStandingEyeHeight() - this.cameraY) * 0.5F;
      }

   }

   private float clipToSpace(float f) {
      float g = 0.1F;

      for(int i = 0; i < 8; ++i) {
         float h = (float)((i & 1) * 2 - 1);
         float j = (float)((i >> 1 & 1) * 2 - 1);
         float k = (float)((i >> 2 & 1) * 2 - 1);
         Vec3d vec3d = this.pos.add((double)(h * 0.1F), (double)(j * 0.1F), (double)(k * 0.1F));
         Vec3d vec3d2 = vec3d.add((new Vec3d(this.horizontalPlane)).multiply((double)(-f)));
         HitResult hitResult = this.area.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, this.focusedEntity));
         if (hitResult.getType() != HitResult.Type.MISS) {
            float l = (float)hitResult.getPos().squaredDistanceTo(this.pos);
            if (l < MathHelper.square(f)) {
               f = MathHelper.sqrt(l);
            }
         }
      }

      return f;
   }

   protected void moveBy(float f, float g, float h) {
      Vector3f vector3f = (new Vector3f(h, g, -f)).rotate(this.rotation);
      this.setPos(new Vec3d(this.pos.x + (double)vector3f.x, this.pos.y + (double)vector3f.y, this.pos.z + (double)vector3f.z));
   }

   protected void setRotation(float yaw, float pitch) {
      this.pitch = pitch;
      this.yaw = yaw;
      this.rotation.rotationYXZ(3.1415927F - yaw * 0.017453292F, -pitch * 0.017453292F, 0.0F);
      HORIZONTAL.rotate(this.rotation, this.horizontalPlane);
      VERTICAL.rotate(this.rotation, this.verticalPlane);
      DIAGONAL.rotate(this.rotation, this.diagonalPlane);
   }

   protected void setPos(double x, double y, double z) {
      this.setPos(new Vec3d(x, y, z));
   }

   protected void setPos(Vec3d pos) {
      this.pos = pos;
      this.blockPos.set(pos.x, pos.y, pos.z);
   }

   public Vec3d getPos() {
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

   public Projection getProjection() {
      MinecraftClient minecraftClient = MinecraftClient.getInstance();
      double d = (double)minecraftClient.getWindow().getFramebufferWidth() / (double)minecraftClient.getWindow().getFramebufferHeight();
      double e = Math.tan((double)((float)(Integer)minecraftClient.options.getFov().getValue() * 0.017453292F) / 2.0) * 0.05000000074505806;
      double f = e * d;
      Vec3d vec3d = (new Vec3d(this.horizontalPlane)).multiply(0.05000000074505806);
      Vec3d vec3d2 = (new Vec3d(this.diagonalPlane)).multiply(f);
      Vec3d vec3d3 = (new Vec3d(this.verticalPlane)).multiply(e);
      return new Projection(vec3d, vec3d2, vec3d3);
   }

   public CameraSubmersionType getSubmersionType() {
      if (!this.ready) {
         return CameraSubmersionType.NONE;
      } else {
         FluidState fluidState = this.area.getFluidState(this.blockPos);
         if (fluidState.isIn(FluidTags.WATER) && this.pos.y < (double)((float)this.blockPos.getY() + fluidState.getHeight(this.area, this.blockPos))) {
            return CameraSubmersionType.WATER;
         } else {
            Projection projection = this.getProjection();
            List list = Arrays.asList(projection.center, projection.getBottomRight(), projection.getTopRight(), projection.getBottomLeft(), projection.getTopLeft());
            Iterator var4 = list.iterator();

            while(var4.hasNext()) {
               Vec3d vec3d = (Vec3d)var4.next();
               Vec3d vec3d2 = this.pos.add(vec3d);
               BlockPos blockPos = BlockPos.ofFloored(vec3d2);
               FluidState fluidState2 = this.area.getFluidState(blockPos);
               if (fluidState2.isIn(FluidTags.LAVA)) {
                  if (vec3d2.y <= (double)(fluidState2.getHeight(this.area, blockPos) + (float)blockPos.getY())) {
                     return CameraSubmersionType.LAVA;
                  }
               } else {
                  BlockState blockState = this.area.getBlockState(blockPos);
                  if (blockState.isOf(Blocks.POWDER_SNOW)) {
                     return CameraSubmersionType.POWDER_SNOW;
                  }
               }
            }

            return CameraSubmersionType.NONE;
         }
      }
   }

   public final Vector3f getHorizontalPlane() {
      return this.horizontalPlane;
   }

   public final Vector3f getVerticalPlane() {
      return this.verticalPlane;
   }

   public final Vector3f getDiagonalPlane() {
      return this.diagonalPlane;
   }

   public void reset() {
      this.area = null;
      this.focusedEntity = null;
      this.ready = false;
   }

   public float getLastTickProgress() {
      return this.lastTickProgress;
   }

   public float getCameraYaw() {
      return MathHelper.wrapDegrees(this.getYaw());
   }

   public Vec3d getCameraPos() {
      return this.getPos();
   }

   @Environment(EnvType.CLIENT)
   public static class Projection {
      final Vec3d center;
      private final Vec3d x;
      private final Vec3d y;

      Projection(Vec3d center, Vec3d x, Vec3d y) {
         this.center = center;
         this.x = x;
         this.y = y;
      }

      public Vec3d getBottomRight() {
         return this.center.add(this.y).add(this.x);
      }

      public Vec3d getTopRight() {
         return this.center.add(this.y).subtract(this.x);
      }

      public Vec3d getBottomLeft() {
         return this.center.subtract(this.y).add(this.x);
      }

      public Vec3d getTopLeft() {
         return this.center.subtract(this.y).subtract(this.x);
      }

      public Vec3d getPosition(float factorX, float factorY) {
         return this.center.add(this.y.multiply((double)factorY)).subtract(this.x.multiply((double)factorX));
      }
   }
}
