package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class HappyGhastEntityRenderState extends LivingEntityRenderState {
   public ItemStack harnessStack;
   public boolean hasPassengers;
   public boolean hasRopes;

   public HappyGhastEntityRenderState() {
      this.harnessStack = ItemStack.EMPTY;
   }
}
