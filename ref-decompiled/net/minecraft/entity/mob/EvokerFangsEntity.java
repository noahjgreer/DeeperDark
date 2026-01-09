package net.minecraft.entity.mob;

import java.util.Iterator;
import java.util.List;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EvokerFangsEntity extends Entity implements Ownable {
   public static final int field_30662 = 20;
   public static final int field_30663 = 2;
   public static final int field_30664 = 14;
   private static final int DEFAULT_WARMUP = 0;
   private int warmup;
   private boolean startedAttack;
   private int ticksLeft;
   private boolean playingAnimation;
   @Nullable
   private LazyEntityReference owner;

   public EvokerFangsEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.warmup = 0;
      this.ticksLeft = 22;
   }

   public EvokerFangsEntity(World world, double x, double y, double z, float yaw, int warmup, LivingEntity owner) {
      this(EntityType.EVOKER_FANGS, world);
      this.warmup = warmup;
      this.setOwner(owner);
      this.setYaw(yaw * 57.295776F);
      this.setPosition(x, y, z);
   }

   protected void initDataTracker(DataTracker.Builder builder) {
   }

   public void setOwner(@Nullable LivingEntity owner) {
      this.owner = owner != null ? new LazyEntityReference(owner) : null;
   }

   @Nullable
   public LivingEntity getOwner() {
      return (LivingEntity)LazyEntityReference.resolve(this.owner, this.getWorld(), LivingEntity.class);
   }

   protected void readCustomData(ReadView view) {
      this.warmup = view.getInt("Warmup", 0);
      this.owner = LazyEntityReference.fromData(view, "Owner");
   }

   protected void writeCustomData(WriteView view) {
      view.putInt("Warmup", this.warmup);
      LazyEntityReference.writeData(this.owner, view, "Owner");
   }

   public void tick() {
      super.tick();
      if (this.getWorld().isClient) {
         if (this.playingAnimation) {
            --this.ticksLeft;
            if (this.ticksLeft == 14) {
               for(int i = 0; i < 12; ++i) {
                  double d = this.getX() + (this.random.nextDouble() * 2.0 - 1.0) * (double)this.getWidth() * 0.5;
                  double e = this.getY() + 0.05 + this.random.nextDouble();
                  double f = this.getZ() + (this.random.nextDouble() * 2.0 - 1.0) * (double)this.getWidth() * 0.5;
                  double g = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
                  double h = 0.3 + this.random.nextDouble() * 0.3;
                  double j = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
                  this.getWorld().addParticleClient(ParticleTypes.CRIT, d, e + 1.0, f, g, h, j);
               }
            }
         }
      } else if (--this.warmup < 0) {
         if (this.warmup == -8) {
            List list = this.getWorld().getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox().expand(0.2, 0.0, 0.2));
            Iterator var15 = list.iterator();

            while(var15.hasNext()) {
               LivingEntity livingEntity = (LivingEntity)var15.next();
               this.damage(livingEntity);
            }
         }

         if (!this.startedAttack) {
            this.getWorld().sendEntityStatus(this, (byte)4);
            this.startedAttack = true;
         }

         if (--this.ticksLeft < 0) {
            this.discard();
         }
      }

   }

   private void damage(LivingEntity target) {
      LivingEntity livingEntity = this.getOwner();
      if (target.isAlive() && !target.isInvulnerable() && target != livingEntity) {
         if (livingEntity == null) {
            target.serverDamage(this.getDamageSources().magic(), 6.0F);
         } else {
            if (livingEntity.isTeammate(target)) {
               return;
            }

            DamageSource damageSource = this.getDamageSources().indirectMagic(this, livingEntity);
            World var5 = this.getWorld();
            if (var5 instanceof ServerWorld) {
               ServerWorld serverWorld = (ServerWorld)var5;
               if (target.damage(serverWorld, damageSource, 6.0F)) {
                  EnchantmentHelper.onTargetDamaged(serverWorld, target, damageSource);
               }
            }
         }

      }
   }

   public void handleStatus(byte status) {
      super.handleStatus(status);
      if (status == 4) {
         this.playingAnimation = true;
         if (!this.isSilent()) {
            this.getWorld().playSoundClient(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_EVOKER_FANGS_ATTACK, this.getSoundCategory(), 1.0F, this.random.nextFloat() * 0.2F + 0.85F, false);
         }
      }

   }

   public float getAnimationProgress(float tickProgress) {
      if (!this.playingAnimation) {
         return 0.0F;
      } else {
         int i = this.ticksLeft - 2;
         return i <= 0 ? 1.0F : 1.0F - ((float)i - tickProgress) / 20.0F;
      }
   }

   public boolean damage(ServerWorld world, DamageSource source, float amount) {
      return false;
   }

   // $FF: synthetic method
   @Nullable
   public Entity getOwner() {
      return this.getOwner();
   }
}
