package net.minecraft.client.render.model.json;

import java.util.function.UnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface ModelVariantOperator extends UnaryOperator {
   Settings ROTATION_X = ModelVariant::withRotationX;
   Settings ROTATION_Y = ModelVariant::withRotationY;
   Settings MODEL = ModelVariant::withModel;
   Settings UV_LOCK = ModelVariant::withUVLock;

   default ModelVariantOperator then(ModelVariantOperator variant) {
      return (variantx) -> {
         return (ModelVariant)variant.apply((ModelVariant)this.apply(variantx));
      };
   }

   @FunctionalInterface
   @Environment(EnvType.CLIENT)
   public interface Settings {
      ModelVariant apply(ModelVariant variant, Object value);

      default ModelVariantOperator withValue(Object value) {
         return (setting) -> {
            return this.apply(setting, value);
         };
      }
   }
}
