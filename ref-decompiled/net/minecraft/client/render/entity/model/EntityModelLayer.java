package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public record EntityModelLayer(Identifier id, String name) {
   public EntityModelLayer(Identifier id, String name) {
      this.id = id;
      this.name = name;
   }

   public String toString() {
      String var10000 = String.valueOf(this.id);
      return var10000 + "#" + this.name;
   }

   public Identifier id() {
      return this.id;
   }

   public String name() {
      return this.name;
   }
}
