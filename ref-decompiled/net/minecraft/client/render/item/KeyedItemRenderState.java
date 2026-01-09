package net.minecraft.client.render.item;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class KeyedItemRenderState extends ItemRenderState {
   private final List modelKey = new ArrayList();

   public void addModelKey(Object modelKey) {
      this.modelKey.add(modelKey);
   }

   public Object getModelKey() {
      return this.modelKey;
   }
}
