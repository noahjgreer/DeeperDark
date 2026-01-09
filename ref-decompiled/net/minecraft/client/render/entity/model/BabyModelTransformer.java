package net.minecraft.client.render.entity.model;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartData;

@Environment(EnvType.CLIENT)
public record BabyModelTransformer(boolean scaleHead, float babyYHeadOffset, float babyZHeadOffset, float babyHeadScale, float babyBodyScale, float bodyYOffset, Set headParts) implements ModelTransformer {
   public BabyModelTransformer(Set headParts) {
      this(false, 5.0F, 2.0F, headParts);
   }

   public BabyModelTransformer(boolean scaleHead, float babyYHeadOffset, float babyZHeadOffset, Set headParts) {
      this(scaleHead, babyYHeadOffset, babyZHeadOffset, 2.0F, 2.0F, 24.0F, headParts);
   }

   public BabyModelTransformer(boolean bl, float f, float g, float h, float i, float j, Set set) {
      this.scaleHead = bl;
      this.babyYHeadOffset = f;
      this.babyZHeadOffset = g;
      this.babyHeadScale = h;
      this.babyBodyScale = i;
      this.bodyYOffset = j;
      this.headParts = set;
   }

   public ModelData apply(ModelData modelData) {
      float f = this.scaleHead ? 1.5F / this.babyHeadScale : 1.0F;
      float g = 1.0F / this.babyBodyScale;
      UnaryOperator unaryOperator = (modelTransform) -> {
         return modelTransform.moveOrigin(0.0F, this.babyYHeadOffset, this.babyZHeadOffset).scaled(f);
      };
      UnaryOperator unaryOperator2 = (modelTransform) -> {
         return modelTransform.moveOrigin(0.0F, this.bodyYOffset, 0.0F).scaled(g);
      };
      ModelData modelData2 = new ModelData();
      Iterator var7 = modelData.getRoot().getChildren().iterator();

      while(var7.hasNext()) {
         Map.Entry entry = (Map.Entry)var7.next();
         String string = (String)entry.getKey();
         ModelPartData modelPartData = (ModelPartData)entry.getValue();
         modelData2.getRoot().addChild(string, modelPartData.applyTransformer(this.headParts.contains(string) ? unaryOperator : unaryOperator2));
      }

      return modelData2;
   }

   public boolean scaleHead() {
      return this.scaleHead;
   }

   public float babyYHeadOffset() {
      return this.babyYHeadOffset;
   }

   public float babyZHeadOffset() {
      return this.babyZHeadOffset;
   }

   public float babyHeadScale() {
      return this.babyHeadScale;
   }

   public float babyBodyScale() {
      return this.babyBodyScale;
   }

   public float bodyYOffset() {
      return this.bodyYOffset;
   }

   public Set headParts() {
      return this.headParts;
   }
}
