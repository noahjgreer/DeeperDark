package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class LlamaEntityRenderState extends LivingEntityRenderState {
   public LlamaEntity.Variant variant;
   public boolean hasChest;
   public ItemStack bodyArmor;
   public boolean trader;

   public LlamaEntityRenderState() {
      this.variant = LlamaEntity.Variant.DEFAULT;
      this.bodyArmor = ItemStack.EMPTY;
   }
}
