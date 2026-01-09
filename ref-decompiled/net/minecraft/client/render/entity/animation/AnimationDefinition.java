package net.minecraft.client.render.entity.animation;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;

@Environment(EnvType.CLIENT)
public record AnimationDefinition(float lengthInSeconds, boolean looping, Map boneAnimations) {
   public AnimationDefinition(float f, boolean bl, Map map) {
      this.lengthInSeconds = f;
      this.looping = bl;
      this.boneAnimations = map;
   }

   public Animation createAnimation(ModelPart root) {
      return Animation.of(root, this);
   }

   public float lengthInSeconds() {
      return this.lengthInSeconds;
   }

   public boolean looping() {
      return this.looping;
   }

   public Map boneAnimations() {
      return this.boneAnimations;
   }

   @Environment(EnvType.CLIENT)
   public static class Builder {
      private final float lengthInSeconds;
      private final Map transformations = Maps.newHashMap();
      private boolean looping;

      public static Builder create(float lengthInSeconds) {
         return new Builder(lengthInSeconds);
      }

      private Builder(float lengthInSeconds) {
         this.lengthInSeconds = lengthInSeconds;
      }

      public Builder looping() {
         this.looping = true;
         return this;
      }

      public Builder addBoneAnimation(String name, Transformation transformation) {
         ((List)this.transformations.computeIfAbsent(name, (namex) -> {
            return new ArrayList();
         })).add(transformation);
         return this;
      }

      public AnimationDefinition build() {
         return new AnimationDefinition(this.lengthInSeconds, this.looping, this.transformations);
      }
   }
}
