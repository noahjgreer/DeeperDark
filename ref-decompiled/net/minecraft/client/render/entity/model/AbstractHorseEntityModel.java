package net.minecraft.client.render.entity.model;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.entity.state.LivingHorseEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class AbstractHorseEntityModel extends EntityModel {
   private static final float EATING_GRASS_ANIMATION_HEAD_BASE_PITCH = 2.1816616F;
   private static final float ANGRY_ANIMATION_FRONT_LEG_PITCH_MULTIPLIER = 1.0471976F;
   private static final float ANGRY_ANIMATION_BODY_PITCH_MULTIPLIER = 0.7853982F;
   private static final float HEAD_TAIL_BASE_PITCH = 0.5235988F;
   private static final float ANGRY_ANIMATION_HIND_LEG_PITCH_MULTIPLIER = 0.2617994F;
   protected static final String HEAD_PARTS = "head_parts";
   protected static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 16.2F, 1.36F, 2.7272F, 2.0F, 20.0F, Set.of("head_parts"));
   protected final ModelPart body;
   protected final ModelPart head;
   private final ModelPart rightHindLeg;
   private final ModelPart leftHindLeg;
   private final ModelPart rightFrontLeg;
   private final ModelPart leftFrontLeg;
   private final ModelPart tail;

   public AbstractHorseEntityModel(ModelPart modelPart) {
      super(modelPart);
      this.body = modelPart.getChild("body");
      this.head = modelPart.getChild("head_parts");
      this.rightHindLeg = modelPart.getChild("right_hind_leg");
      this.leftHindLeg = modelPart.getChild("left_hind_leg");
      this.rightFrontLeg = modelPart.getChild("right_front_leg");
      this.leftFrontLeg = modelPart.getChild("left_front_leg");
      this.tail = this.body.getChild("tail");
   }

   public static ModelData getModelData(Dilation dilation) {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();
      ModelPartData modelPartData2 = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 32).cuboid(-5.0F, -8.0F, -17.0F, 10.0F, 10.0F, 22.0F, new Dilation(0.05F)), ModelTransform.origin(0.0F, 11.0F, 5.0F));
      ModelPartData modelPartData3 = modelPartData.addChild("head_parts", ModelPartBuilder.create().uv(0, 35).cuboid(-2.05F, -6.0F, -2.0F, 4.0F, 12.0F, 7.0F), ModelTransform.of(0.0F, 4.0F, -12.0F, 0.5235988F, 0.0F, 0.0F));
      ModelPartData modelPartData4 = modelPartData3.addChild("head", ModelPartBuilder.create().uv(0, 13).cuboid(-3.0F, -11.0F, -2.0F, 6.0F, 5.0F, 7.0F, dilation), ModelTransform.NONE);
      modelPartData3.addChild("mane", ModelPartBuilder.create().uv(56, 36).cuboid(-1.0F, -11.0F, 5.01F, 2.0F, 16.0F, 2.0F, dilation), ModelTransform.NONE);
      modelPartData3.addChild("upper_mouth", ModelPartBuilder.create().uv(0, 25).cuboid(-2.0F, -11.0F, -7.0F, 4.0F, 5.0F, 5.0F, dilation), ModelTransform.NONE);
      modelPartData.addChild("left_hind_leg", ModelPartBuilder.create().uv(48, 21).mirrored().cuboid(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, dilation), ModelTransform.origin(4.0F, 14.0F, 7.0F));
      modelPartData.addChild("right_hind_leg", ModelPartBuilder.create().uv(48, 21).cuboid(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, dilation), ModelTransform.origin(-4.0F, 14.0F, 7.0F));
      modelPartData.addChild("left_front_leg", ModelPartBuilder.create().uv(48, 21).mirrored().cuboid(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, dilation), ModelTransform.origin(4.0F, 14.0F, -10.0F));
      modelPartData.addChild("right_front_leg", ModelPartBuilder.create().uv(48, 21).cuboid(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, dilation), ModelTransform.origin(-4.0F, 14.0F, -10.0F));
      modelPartData2.addChild("tail", ModelPartBuilder.create().uv(42, 36).cuboid(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 4.0F, dilation), ModelTransform.of(0.0F, -5.0F, 2.0F, 0.5235988F, 0.0F, 0.0F));
      modelPartData4.addChild("left_ear", ModelPartBuilder.create().uv(19, 16).cuboid(0.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.NONE);
      modelPartData4.addChild("right_ear", ModelPartBuilder.create().uv(19, 16).cuboid(-2.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, new Dilation(-0.001F)), ModelTransform.NONE);
      return modelData;
   }

   public static ModelData getBabyHorseModelData(Dilation dilation) {
      return BABY_TRANSFORMER.apply(getBabyModelData(dilation));
   }

   protected static ModelData getBabyModelData(Dilation dilation) {
      ModelData modelData = getModelData(dilation);
      ModelPartData modelPartData = modelData.getRoot();
      Dilation dilation2 = dilation.add(0.0F, 5.5F, 0.0F);
      modelPartData.addChild("left_hind_leg", ModelPartBuilder.create().uv(48, 21).mirrored().cuboid(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, dilation2), ModelTransform.origin(4.0F, 14.0F, 7.0F));
      modelPartData.addChild("right_hind_leg", ModelPartBuilder.create().uv(48, 21).cuboid(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, dilation2), ModelTransform.origin(-4.0F, 14.0F, 7.0F));
      modelPartData.addChild("left_front_leg", ModelPartBuilder.create().uv(48, 21).mirrored().cuboid(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, dilation2), ModelTransform.origin(4.0F, 14.0F, -10.0F));
      modelPartData.addChild("right_front_leg", ModelPartBuilder.create().uv(48, 21).cuboid(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, dilation2), ModelTransform.origin(-4.0F, 14.0F, -10.0F));
      return modelData;
   }

   public void setAngles(LivingHorseEntityRenderState livingHorseEntityRenderState) {
      super.setAngles(livingHorseEntityRenderState);
      float f = MathHelper.clamp(livingHorseEntityRenderState.relativeHeadYaw, -20.0F, 20.0F);
      float g = livingHorseEntityRenderState.pitch * 0.017453292F;
      float h = livingHorseEntityRenderState.limbSwingAmplitude;
      float i = livingHorseEntityRenderState.limbSwingAnimationProgress;
      if (h > 0.2F) {
         g += MathHelper.cos(i * 0.8F) * 0.15F * h;
      }

      float j = livingHorseEntityRenderState.eatingGrassAnimationProgress;
      float k = livingHorseEntityRenderState.angryAnimationProgress;
      float l = 1.0F - k;
      float m = livingHorseEntityRenderState.eatingAnimationProgress;
      boolean bl = livingHorseEntityRenderState.waggingTail;
      this.head.pitch = 0.5235988F + g;
      this.head.yaw = f * 0.017453292F;
      float n = livingHorseEntityRenderState.touchingWater ? 0.2F : 1.0F;
      float o = MathHelper.cos(n * i * 0.6662F + 3.1415927F);
      float p = o * 0.8F * h;
      float q = (1.0F - Math.max(k, j)) * (0.5235988F + g + m * MathHelper.sin(livingHorseEntityRenderState.age) * 0.05F);
      this.head.pitch = k * (0.2617994F + g) + j * (2.1816616F + MathHelper.sin(livingHorseEntityRenderState.age) * 0.05F) + q;
      this.head.yaw = k * f * 0.017453292F + (1.0F - Math.max(k, j)) * this.head.yaw;
      float r = livingHorseEntityRenderState.ageScale;
      ModelPart var10000 = this.head;
      var10000.originY += MathHelper.lerp(j, MathHelper.lerp(k, 0.0F, -8.0F * r), 7.0F * r);
      this.head.originZ = MathHelper.lerp(k, this.head.originZ, -4.0F * r);
      this.body.pitch = k * -0.7853982F + l * this.body.pitch;
      float s = 0.2617994F * k;
      float t = MathHelper.cos(livingHorseEntityRenderState.age * 0.6F + 3.1415927F);
      var10000 = this.leftFrontLeg;
      var10000.originY -= 12.0F * r * k;
      var10000 = this.leftFrontLeg;
      var10000.originZ += 4.0F * r * k;
      this.rightFrontLeg.originY = this.leftFrontLeg.originY;
      this.rightFrontLeg.originZ = this.leftFrontLeg.originZ;
      float u = (-1.0471976F + t) * k + p * l;
      float v = (-1.0471976F - t) * k - p * l;
      this.leftHindLeg.pitch = s - o * 0.5F * h * l;
      this.rightHindLeg.pitch = s + o * 0.5F * h * l;
      this.leftFrontLeg.pitch = u;
      this.rightFrontLeg.pitch = v;
      this.tail.pitch = 0.5235988F + h * 0.75F;
      var10000 = this.tail;
      var10000.originY += h * r;
      var10000 = this.tail;
      var10000.originZ += h * 2.0F * r;
      if (bl) {
         this.tail.yaw = MathHelper.cos(livingHorseEntityRenderState.age * 0.7F);
      } else {
         this.tail.yaw = 0.0F;
      }

   }
}
