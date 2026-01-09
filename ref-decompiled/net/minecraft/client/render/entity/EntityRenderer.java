package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Iterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.state.EntityHitbox;
import net.minecraft.client.render.entity.state.EntityHitboxAndView;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.entity.vehicle.MinecartController;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public abstract class EntityRenderer {
   protected static final float field_32921 = 0.025F;
   public static final int field_52257 = 24;
   public static final float field_60152 = 0.05F;
   protected final EntityRenderDispatcher dispatcher;
   private final TextRenderer textRenderer;
   protected float shadowRadius;
   protected float shadowOpacity = 1.0F;
   private final EntityRenderState state = this.createRenderState();

   protected EntityRenderer(EntityRendererFactory.Context context) {
      this.dispatcher = context.getRenderDispatcher();
      this.textRenderer = context.getTextRenderer();
   }

   public final int getLight(Entity entity, float tickProgress) {
      BlockPos blockPos = BlockPos.ofFloored(entity.getClientCameraPosVec(tickProgress));
      return LightmapTextureManager.pack(this.getBlockLight(entity, blockPos), this.getSkyLight(entity, blockPos));
   }

   protected int getSkyLight(Entity entity, BlockPos pos) {
      return entity.getWorld().getLightLevel(LightType.SKY, pos);
   }

   protected int getBlockLight(Entity entity, BlockPos pos) {
      return entity.isOnFire() ? 15 : entity.getWorld().getLightLevel(LightType.BLOCK, pos);
   }

   public boolean shouldRender(Entity entity, Frustum frustum, double x, double y, double z) {
      if (!entity.shouldRender(x, y, z)) {
         return false;
      } else if (!this.canBeCulled(entity)) {
         return true;
      } else {
         Box box = this.getBoundingBox(entity).expand(0.5);
         if (box.isNaN() || box.getAverageSideLength() == 0.0) {
            box = new Box(entity.getX() - 2.0, entity.getY() - 2.0, entity.getZ() - 2.0, entity.getX() + 2.0, entity.getY() + 2.0, entity.getZ() + 2.0);
         }

         if (frustum.isVisible(box)) {
            return true;
         } else {
            if (entity instanceof Leashable) {
               Leashable leashable = (Leashable)entity;
               Entity entity2 = leashable.getLeashHolder();
               if (entity2 != null) {
                  Box box2 = this.dispatcher.getRenderer(entity2).getBoundingBox(entity2);
                  return frustum.isVisible(box2) || frustum.isVisible(box.union(box2));
               }
            }

            return false;
         }
      }
   }

   protected Box getBoundingBox(Entity entity) {
      return entity.getBoundingBox();
   }

   protected boolean canBeCulled(Entity entity) {
      return true;
   }

   public Vec3d getPositionOffset(EntityRenderState state) {
      return state.positionOffset != null ? state.positionOffset : Vec3d.ZERO;
   }

   public void render(EntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
      if (state.leashDatas != null) {
         Iterator var5 = state.leashDatas.iterator();

         while(var5.hasNext()) {
            EntityRenderState.LeashData leashData = (EntityRenderState.LeashData)var5.next();
            renderLeash(matrices, vertexConsumers, leashData);
         }
      }

      if (state.displayName != null) {
         this.renderLabelIfPresent(state, state.displayName, matrices, vertexConsumers, light);
      }

   }

   private static void renderLeash(MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState.LeashData leashData) {
      float f = (float)(leashData.endPos.x - leashData.startPos.x);
      float g = (float)(leashData.endPos.y - leashData.startPos.y);
      float h = (float)(leashData.endPos.z - leashData.startPos.z);
      float i = MathHelper.inverseSqrt(f * f + h * h) * 0.05F / 2.0F;
      float j = h * i;
      float k = f * i;
      matrices.push();
      matrices.translate(leashData.offset);
      VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLeash());
      Matrix4f matrix4f = matrices.peek().getPositionMatrix();

      int l;
      for(l = 0; l <= 24; ++l) {
         renderLeashSegment(vertexConsumer, matrix4f, f, g, h, 0.05F, 0.05F, j, k, l, false, leashData);
      }

      for(l = 24; l >= 0; --l) {
         renderLeashSegment(vertexConsumer, matrix4f, f, g, h, 0.05F, 0.0F, j, k, l, true, leashData);
      }

      matrices.pop();
   }

   private static void renderLeashSegment(VertexConsumer vertexConsumer, Matrix4f matrix, float leashedEntityX, float leashedEntityY, float leashedEntityZ, float f, float g, float h, float i, int segment, boolean bl, EntityRenderState.LeashData leashData) {
      float j = (float)segment / 24.0F;
      int k = (int)MathHelper.lerp(j, (float)leashData.leashedEntityBlockLight, (float)leashData.leashHolderBlockLight);
      int l = (int)MathHelper.lerp(j, (float)leashData.leashedEntitySkyLight, (float)leashData.leashHolderSkyLight);
      int m = LightmapTextureManager.pack(k, l);
      float n = segment % 2 == (bl ? 1 : 0) ? 0.7F : 1.0F;
      float o = 0.5F * n;
      float p = 0.4F * n;
      float q = 0.3F * n;
      float r = leashedEntityX * j;
      float s;
      if (leashData.field_60161) {
         s = leashedEntityY > 0.0F ? leashedEntityY * j * j : leashedEntityY - leashedEntityY * (1.0F - j) * (1.0F - j);
      } else {
         s = leashedEntityY * j;
      }

      float t = leashedEntityZ * j;
      vertexConsumer.vertex(matrix, r - h, s + g, t + i).color(o, p, q, 1.0F).light(m);
      vertexConsumer.vertex(matrix, r + h, s + f - g, t - i).color(o, p, q, 1.0F).light(m);
   }

   protected boolean hasLabel(Entity entity, double squaredDistanceToCamera) {
      return entity.shouldRenderName() || entity.hasCustomName() && entity == this.dispatcher.targetedEntity;
   }

   public TextRenderer getTextRenderer() {
      return this.textRenderer;
   }

   protected void renderLabelIfPresent(EntityRenderState state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
      Vec3d vec3d = state.nameLabelPos;
      if (vec3d != null) {
         boolean bl = !state.sneaking;
         int i = "deadmau5".equals(text.getString()) ? -10 : 0;
         matrices.push();
         matrices.translate(vec3d.x, vec3d.y + 0.5, vec3d.z);
         matrices.multiply(this.dispatcher.getRotation());
         matrices.scale(0.025F, -0.025F, 0.025F);
         Matrix4f matrix4f = matrices.peek().getPositionMatrix();
         TextRenderer textRenderer = this.getTextRenderer();
         float f = (float)(-textRenderer.getWidth((StringVisitable)text)) / 2.0F;
         int j = (int)(MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F) * 255.0F) << 24;
         textRenderer.draw(text, f, (float)i, -2130706433, false, matrix4f, vertexConsumers, bl ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, j, light);
         if (bl) {
            textRenderer.draw((Text)text, f, (float)i, -1, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.applyEmission(light, 2));
         }

         matrices.pop();
      }
   }

   @Nullable
   protected Text getDisplayName(Entity entity) {
      return entity.getDisplayName();
   }

   protected float getShadowRadius(EntityRenderState state) {
      return this.shadowRadius;
   }

   protected float getShadowOpacity(EntityRenderState state) {
      return this.shadowOpacity;
   }

   public abstract EntityRenderState createRenderState();

   public final EntityRenderState getAndUpdateRenderState(Entity entity, float tickProgress) {
      EntityRenderState entityRenderState = this.state;
      this.updateRenderState(entity, entityRenderState, tickProgress);
      return entityRenderState;
   }

   public void updateRenderState(Entity entity, EntityRenderState state, float tickProgress) {
      label90: {
         state.entityType = entity.getType();
         state.x = MathHelper.lerp((double)tickProgress, entity.lastRenderX, entity.getX());
         state.y = MathHelper.lerp((double)tickProgress, entity.lastRenderY, entity.getY());
         state.z = MathHelper.lerp((double)tickProgress, entity.lastRenderZ, entity.getZ());
         state.invisible = entity.isInvisible();
         state.age = (float)entity.age + tickProgress;
         state.width = entity.getWidth();
         state.height = entity.getHeight();
         state.standingEyeHeight = entity.getStandingEyeHeight();
         if (entity.hasVehicle()) {
            Entity var6 = entity.getVehicle();
            if (var6 instanceof AbstractMinecartEntity) {
               AbstractMinecartEntity abstractMinecartEntity = (AbstractMinecartEntity)var6;
               MinecartController var26 = abstractMinecartEntity.getController();
               if (var26 instanceof ExperimentalMinecartController) {
                  ExperimentalMinecartController experimentalMinecartController = (ExperimentalMinecartController)var26;
                  if (experimentalMinecartController.hasCurrentLerpSteps()) {
                     double d = MathHelper.lerp((double)tickProgress, abstractMinecartEntity.lastRenderX, abstractMinecartEntity.getX());
                     double e = MathHelper.lerp((double)tickProgress, abstractMinecartEntity.lastRenderY, abstractMinecartEntity.getY());
                     double f = MathHelper.lerp((double)tickProgress, abstractMinecartEntity.lastRenderZ, abstractMinecartEntity.getZ());
                     state.positionOffset = experimentalMinecartController.getLerpedPosition(tickProgress).subtract(new Vec3d(d, e, f));
                     break label90;
                  }
               }
            }
         }

         state.positionOffset = null;
      }

      state.squaredDistanceToCamera = this.dispatcher.getSquaredDistanceToCamera(entity);
      boolean bl = state.squaredDistanceToCamera < 4096.0 && this.hasLabel(entity, state.squaredDistanceToCamera);
      if (bl) {
         state.displayName = this.getDisplayName(entity);
         state.nameLabelPos = entity.getAttachments().getPointNullable(EntityAttachmentType.NAME_TAG, 0, entity.getLerpedYaw(tickProgress));
      } else {
         state.displayName = null;
      }

      label77: {
         state.sneaking = entity.isSneaky();
         if (entity instanceof Leashable leashable) {
            Entity var7 = leashable.getLeashHolder();
            if (var7 instanceof Entity) {
               float g = entity.lerpYaw(tickProgress) * 0.017453292F;
               Vec3d vec3d = leashable.getLeashOffset(tickProgress);
               BlockPos blockPos = BlockPos.ofFloored(entity.getCameraPosVec(tickProgress));
               BlockPos blockPos2 = BlockPos.ofFloored(var7.getCameraPosVec(tickProgress));
               int i = this.getBlockLight(entity, blockPos);
               int j = this.dispatcher.getRenderer(var7).getBlockLight(var7, blockPos2);
               int k = entity.getWorld().getLightLevel(LightType.SKY, blockPos);
               int l = entity.getWorld().getLightLevel(LightType.SKY, blockPos2);
               boolean bl2 = var7.hasQuadLeashAttachmentPoints() && leashable.canUseQuadLeashAttachmentPoint();
               int m = bl2 ? 4 : 1;
               if (state.leashDatas == null || state.leashDatas.size() != m) {
                  state.leashDatas = new ArrayList(m);

                  for(int n = 0; n < m; ++n) {
                     state.leashDatas.add(new EntityRenderState.LeashData());
                  }
               }

               if (bl2) {
                  float h = var7.lerpYaw(tickProgress) * 0.017453292F;
                  Vec3d vec3d2 = var7.getLerpedPos(tickProgress);
                  Vec3d[] vec3ds = leashable.getQuadLeashOffsets();
                  Vec3d[] vec3ds2 = var7.getHeldQuadLeashOffsets();
                  int o = 0;

                  while(true) {
                     if (o >= m) {
                        break label77;
                     }

                     EntityRenderState.LeashData leashData = (EntityRenderState.LeashData)state.leashDatas.get(o);
                     leashData.offset = vec3ds[o].rotateY(-g);
                     leashData.startPos = entity.getLerpedPos(tickProgress).add(leashData.offset);
                     leashData.endPos = vec3d2.add(vec3ds2[o].rotateY(-h));
                     leashData.leashedEntityBlockLight = i;
                     leashData.leashHolderBlockLight = j;
                     leashData.leashedEntitySkyLight = k;
                     leashData.leashHolderSkyLight = l;
                     leashData.field_60161 = false;
                     ++o;
                  }
               } else {
                  Vec3d vec3d3 = vec3d.rotateY(-g);
                  EntityRenderState.LeashData leashData2 = (EntityRenderState.LeashData)state.leashDatas.getFirst();
                  leashData2.offset = vec3d3;
                  leashData2.startPos = entity.getLerpedPos(tickProgress).add(vec3d3);
                  leashData2.endPos = var7.getLeashPos(tickProgress);
                  leashData2.leashedEntityBlockLight = i;
                  leashData2.leashHolderBlockLight = j;
                  leashData2.leashedEntitySkyLight = k;
                  leashData2.leashHolderSkyLight = l;
                  break label77;
               }
            }
         }

         state.leashDatas = null;
      }

      state.onFire = entity.doesRenderOnFire();
      MinecraftClient minecraftClient = MinecraftClient.getInstance();
      if (minecraftClient.getEntityRenderDispatcher().shouldRenderHitboxes() && !state.invisible && !minecraftClient.hasReducedDebugInfo()) {
         this.updateDebugState(entity, state, tickProgress);
      } else {
         state.hitbox = null;
         state.debugInfo = null;
      }

   }

   private void updateDebugState(Entity entity, EntityRenderState state, float tickProgress) {
      state.hitbox = this.createHitbox(entity, tickProgress, false);
      state.debugInfo = null;
   }

   private EntityHitboxAndView createHitbox(Entity entity, float tickProgress, boolean green) {
      ImmutableList.Builder builder = new ImmutableList.Builder();
      Box box = entity.getBoundingBox();
      EntityHitbox entityHitbox;
      if (green) {
         entityHitbox = new EntityHitbox(box.minX - entity.getX(), box.minY - entity.getY(), box.minZ - entity.getZ(), box.maxX - entity.getX(), box.maxY - entity.getY(), box.maxZ - entity.getZ(), 0.0F, 1.0F, 0.0F);
      } else {
         entityHitbox = new EntityHitbox(box.minX - entity.getX(), box.minY - entity.getY(), box.minZ - entity.getZ(), box.maxX - entity.getX(), box.maxY - entity.getY(), box.maxZ - entity.getZ(), 1.0F, 1.0F, 1.0F);
      }

      builder.add(entityHitbox);
      Entity entity2 = entity.getVehicle();
      if (entity2 != null) {
         float f = Math.min(entity2.getWidth(), entity.getWidth()) / 2.0F;
         float g = 0.0625F;
         Vec3d vec3d = entity2.getPassengerRidingPos(entity).subtract(entity.getPos());
         EntityHitbox entityHitbox2 = new EntityHitbox(vec3d.x - (double)f, vec3d.y, vec3d.z - (double)f, vec3d.x + (double)f, vec3d.y + 0.0625, vec3d.z + (double)f, 1.0F, 1.0F, 0.0F);
         builder.add(entityHitbox2);
      }

      this.appendHitboxes(entity, builder, tickProgress);
      Vec3d vec3d2 = entity.getRotationVec(tickProgress);
      return new EntityHitboxAndView(vec3d2.x, vec3d2.y, vec3d2.z, builder.build());
   }

   protected void appendHitboxes(Entity entity, ImmutableList.Builder builder, float tickProgress) {
   }

   @Nullable
   private static Entity getServerEntity(Entity clientEntity) {
      IntegratedServer integratedServer = MinecraftClient.getInstance().getServer();
      if (integratedServer != null) {
         ServerWorld serverWorld = integratedServer.getWorld(clientEntity.getWorld().getRegistryKey());
         if (serverWorld != null) {
            return serverWorld.getEntityById(clientEntity.getId());
         }
      }

      return null;
   }
}
