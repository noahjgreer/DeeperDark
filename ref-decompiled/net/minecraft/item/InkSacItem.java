package net.minecraft.item;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class InkSacItem extends Item implements SignChangingItem {
   public InkSacItem(Item.Settings settings) {
      super(settings);
   }

   public boolean useOnSign(World world, SignBlockEntity signBlockEntity, boolean front, PlayerEntity player) {
      if (signBlockEntity.changeText((text) -> {
         return text.withGlowing(false);
      }, front)) {
         world.playSound((Entity)null, signBlockEntity.getPos(), SoundEvents.ITEM_INK_SAC_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
         return true;
      } else {
         return false;
      }
   }
}
