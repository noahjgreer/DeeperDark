package net.minecraft.client.render.model.json;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.GeometryBakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AxisRotation;

@Environment(EnvType.CLIENT)
public record ModelVariant(Identifier modelId, ModelState modelState) implements BlockModelPart.Unbaked {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Identifier.CODEC.fieldOf("model").forGetter(ModelVariant::modelId), ModelVariant.ModelState.CODEC.forGetter(ModelVariant::modelState)).apply(instance, ModelVariant::new);
   });
   public static final Codec CODEC;

   public ModelVariant(Identifier model) {
      this(model, ModelVariant.ModelState.DEFAULT);
   }

   public ModelVariant(Identifier location, ModelState modelState) {
      this.modelId = location;
      this.modelState = modelState;
   }

   public ModelVariant withRotationX(AxisRotation amount) {
      return this.setState(this.modelState.setRotationX(amount));
   }

   public ModelVariant withRotationY(AxisRotation amount) {
      return this.setState(this.modelState.setRotationY(amount));
   }

   public ModelVariant withUVLock(boolean uvLock) {
      return this.setState(this.modelState.setUVLock(uvLock));
   }

   public ModelVariant withModel(Identifier modelId) {
      return new ModelVariant(modelId, this.modelState);
   }

   public ModelVariant setState(ModelState modelState) {
      return new ModelVariant(this.modelId, modelState);
   }

   public ModelVariant with(ModelVariantOperator variantOperator) {
      return (ModelVariant)variantOperator.apply(this);
   }

   public BlockModelPart bake(Baker baker) {
      return GeometryBakedModel.create(baker, this.modelId, this.modelState.asModelBakeSettings());
   }

   public void resolve(ResolvableModel.Resolver resolver) {
      resolver.markDependency(this.modelId);
   }

   public Identifier modelId() {
      return this.modelId;
   }

   public ModelState modelState() {
      return this.modelState;
   }

   static {
      CODEC = MAP_CODEC.codec();
   }

   @Environment(EnvType.CLIENT)
   public static record ModelState(AxisRotation x, AxisRotation y, boolean uvLock) {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(AxisRotation.CODEC.optionalFieldOf("x", AxisRotation.R0).forGetter(ModelState::x), AxisRotation.CODEC.optionalFieldOf("y", AxisRotation.R0).forGetter(ModelState::y), Codec.BOOL.optionalFieldOf("uvlock", false).forGetter(ModelState::uvLock)).apply(instance, ModelState::new);
      });
      public static final ModelState DEFAULT;

      public ModelState(AxisRotation axisRotation, AxisRotation axisRotation2, boolean bl) {
         this.x = axisRotation;
         this.y = axisRotation2;
         this.uvLock = bl;
      }

      public ModelBakeSettings asModelBakeSettings() {
         net.minecraft.client.render.model.ModelRotation modelRotation = net.minecraft.client.render.model.ModelRotation.rotate(this.x, this.y);
         return (ModelBakeSettings)(this.uvLock ? modelRotation.getUVModel() : modelRotation);
      }

      public ModelState setRotationX(AxisRotation amount) {
         return new ModelState(amount, this.y, this.uvLock);
      }

      public ModelState setRotationY(AxisRotation amount) {
         return new ModelState(this.x, amount, this.uvLock);
      }

      public ModelState setUVLock(boolean uvLock) {
         return new ModelState(this.x, this.y, uvLock);
      }

      public AxisRotation x() {
         return this.x;
      }

      public AxisRotation y() {
         return this.y;
      }

      public boolean uvLock() {
         return this.uvLock;
      }

      static {
         DEFAULT = new ModelState(AxisRotation.R0, AxisRotation.R0, false);
      }
   }
}
