package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SpectralArrowEntity extends PersistentProjectileEntity {
   private static final int DEFAULT_DURATION = 200;
   private int duration = 200;

   public SpectralArrowEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   public SpectralArrowEntity(World world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom) {
      super(EntityType.SPECTRAL_ARROW, owner, world, stack, shotFrom);
   }

   public SpectralArrowEntity(World world, double x, double y, double z, ItemStack stack, @Nullable ItemStack shotFrom) {
      super(EntityType.SPECTRAL_ARROW, x, y, z, world, stack, shotFrom);
   }

   public void tick() {
      super.tick();
      if (this.getWorld().isClient && !this.isInGround()) {
         this.getWorld().addParticleClient(ParticleTypes.INSTANT_EFFECT, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
      }

   }

   protected void onHit(LivingEntity target) {
      super.onHit(target);
      StatusEffectInstance statusEffectInstance = new StatusEffectInstance(StatusEffects.GLOWING, this.duration, 0);
      target.addStatusEffect(statusEffectInstance, this.getEffectCause());
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.duration = view.getInt("Duration", 200);
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putInt("Duration", this.duration);
   }

   protected ItemStack getDefaultItemStack() {
      return new ItemStack(Items.SPECTRAL_ARROW);
   }
}
