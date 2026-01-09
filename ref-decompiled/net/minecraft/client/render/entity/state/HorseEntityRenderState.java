package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.HorseMarking;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class HorseEntityRenderState extends LivingHorseEntityRenderState {
   public HorseColor color;
   public HorseMarking marking;
   public ItemStack armor;

   public HorseEntityRenderState() {
      this.color = HorseColor.WHITE;
      this.marking = HorseMarking.NONE;
      this.armor = ItemStack.EMPTY;
   }
}
