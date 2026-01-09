package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class HappyGhastRidingSoundInstance extends MovingSoundInstance {
   private static final float field_59985 = 0.0F;
   private static final float field_59986 = 1.0F;
   private final PlayerEntity player;
   private final HappyGhastEntity happyGhast;

   public HappyGhastRidingSoundInstance(PlayerEntity player, HappyGhastEntity happyGhast) {
      super(SoundEvents.ENTITY_HAPPY_GHAST_RIDING, happyGhast.getSoundCategory(), SoundInstance.createRandom());
      this.player = player;
      this.happyGhast = happyGhast;
      this.attenuationType = SoundInstance.AttenuationType.NONE;
      this.repeat = true;
      this.repeatDelay = 0;
      this.volume = 0.0F;
   }

   public boolean shouldAlwaysPlay() {
      return true;
   }

   public void tick() {
      if (!this.happyGhast.isRemoved() && this.player.hasVehicle() && this.player.getVehicle() == this.happyGhast) {
         float f = (float)this.happyGhast.getVelocity().length();
         if (f >= 0.01F) {
            this.volume = 5.0F * MathHelper.clampedLerp(0.0F, 1.0F, f);
         } else {
            this.volume = 0.0F;
         }

      } else {
         this.setDone();
      }
   }
}
