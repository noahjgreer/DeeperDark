package net.minecraft.client.render.item.property.bool;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record FishingRodCastProperty() implements BooleanProperty {
   public static final MapCodec CODEC = MapCodec.unit(new FishingRodCastProperty());

   public boolean test(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
      if (entity instanceof PlayerEntity playerEntity) {
         if (playerEntity.fishHook != null) {
            Arm arm = FishingBobberEntityRenderer.getArmHoldingRod(playerEntity);
            return entity.getStackInArm(arm) == stack;
         }
      }

      return false;
   }

   public MapCodec getCodec() {
      return CODEC;
   }
}
