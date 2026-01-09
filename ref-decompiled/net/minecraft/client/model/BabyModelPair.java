package net.minecraft.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record BabyModelPair(Model adultModel, Model babyModel) {
   public BabyModelPair(Model model, Model model2) {
      this.adultModel = model;
      this.babyModel = model2;
   }

   public Model get(boolean baby) {
      return baby ? this.babyModel : this.adultModel;
   }

   public Model adultModel() {
      return this.adultModel;
   }

   public Model babyModel() {
      return this.babyModel;
   }
}
