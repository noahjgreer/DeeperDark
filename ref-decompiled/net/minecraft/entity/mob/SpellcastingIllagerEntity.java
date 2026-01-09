package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.function.IntFunction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.TintedParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class SpellcastingIllagerEntity extends IllagerEntity {
   private static final TrackedData SPELL;
   private static final int DEFAULT_SPELL_TICKS = 0;
   protected int spellTicks = 0;
   private Spell spell;

   protected SpellcastingIllagerEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.spell = SpellcastingIllagerEntity.Spell.NONE;
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(SPELL, (byte)0);
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.spellTicks = view.getInt("SpellTicks", 0);
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putInt("SpellTicks", this.spellTicks);
   }

   public IllagerEntity.State getState() {
      if (this.isSpellcasting()) {
         return IllagerEntity.State.SPELLCASTING;
      } else {
         return this.isCelebrating() ? IllagerEntity.State.CELEBRATING : IllagerEntity.State.CROSSED;
      }
   }

   public boolean isSpellcasting() {
      if (this.getWorld().isClient) {
         return (Byte)this.dataTracker.get(SPELL) > 0;
      } else {
         return this.spellTicks > 0;
      }
   }

   public void setSpell(Spell spell) {
      this.spell = spell;
      this.dataTracker.set(SPELL, (byte)spell.id);
   }

   protected Spell getSpell() {
      return !this.getWorld().isClient ? this.spell : SpellcastingIllagerEntity.Spell.byId((Byte)this.dataTracker.get(SPELL));
   }

   protected void mobTick(ServerWorld world) {
      super.mobTick(world);
      if (this.spellTicks > 0) {
         --this.spellTicks;
      }

   }

   public void tick() {
      super.tick();
      if (this.getWorld().isClient && this.isSpellcasting()) {
         Spell spell = this.getSpell();
         float f = (float)spell.particleVelocity[0];
         float g = (float)spell.particleVelocity[1];
         float h = (float)spell.particleVelocity[2];
         float i = this.bodyYaw * 0.017453292F + MathHelper.cos((float)this.age * 0.6662F) * 0.25F;
         float j = MathHelper.cos(i);
         float k = MathHelper.sin(i);
         double d = 0.6 * (double)this.getScale();
         double e = 1.8 * (double)this.getScale();
         this.getWorld().addParticleClient(TintedParticleEffect.create(ParticleTypes.ENTITY_EFFECT, f, g, h), this.getX() + (double)j * d, this.getY() + e, this.getZ() + (double)k * d, 0.0, 0.0, 0.0);
         this.getWorld().addParticleClient(TintedParticleEffect.create(ParticleTypes.ENTITY_EFFECT, f, g, h), this.getX() - (double)j * d, this.getY() + e, this.getZ() - (double)k * d, 0.0, 0.0, 0.0);
      }

   }

   protected int getSpellTicks() {
      return this.spellTicks;
   }

   protected abstract SoundEvent getCastSpellSound();

   static {
      SPELL = DataTracker.registerData(SpellcastingIllagerEntity.class, TrackedDataHandlerRegistry.BYTE);
   }

   protected static enum Spell {
      NONE(0, 0.0, 0.0, 0.0),
      SUMMON_VEX(1, 0.7, 0.7, 0.8),
      FANGS(2, 0.4, 0.3, 0.35),
      WOLOLO(3, 0.7, 0.5, 0.2),
      DISAPPEAR(4, 0.3, 0.3, 0.8),
      BLINDNESS(5, 0.1, 0.1, 0.2);

      private static final IntFunction BY_ID = ValueLists.createIndexToValueFunction((spell) -> {
         return spell.id;
      }, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.ZERO);
      final int id;
      final double[] particleVelocity;

      private Spell(final int id, final double particleVelocityX, final double particleVelocityY, final double particleVelocityZ) {
         this.id = id;
         this.particleVelocity = new double[]{particleVelocityX, particleVelocityY, particleVelocityZ};
      }

      public static Spell byId(int id) {
         return (Spell)BY_ID.apply(id);
      }

      // $FF: synthetic method
      private static Spell[] method_36658() {
         return new Spell[]{NONE, SUMMON_VEX, FANGS, WOLOLO, DISAPPEAR, BLINDNESS};
      }
   }

   protected abstract class CastSpellGoal extends Goal {
      protected int spellCooldown;
      protected int startTime;

      public boolean canStart() {
         LivingEntity livingEntity = SpellcastingIllagerEntity.this.getTarget();
         if (livingEntity != null && livingEntity.isAlive()) {
            if (SpellcastingIllagerEntity.this.isSpellcasting()) {
               return false;
            } else {
               return SpellcastingIllagerEntity.this.age >= this.startTime;
            }
         } else {
            return false;
         }
      }

      public boolean shouldContinue() {
         LivingEntity livingEntity = SpellcastingIllagerEntity.this.getTarget();
         return livingEntity != null && livingEntity.isAlive() && this.spellCooldown > 0;
      }

      public void start() {
         this.spellCooldown = this.getTickCount(this.getInitialCooldown());
         SpellcastingIllagerEntity.this.spellTicks = this.getSpellTicks();
         this.startTime = SpellcastingIllagerEntity.this.age + this.startTimeDelay();
         SoundEvent soundEvent = this.getSoundPrepare();
         if (soundEvent != null) {
            SpellcastingIllagerEntity.this.playSound(soundEvent, 1.0F, 1.0F);
         }

         SpellcastingIllagerEntity.this.setSpell(this.getSpell());
      }

      public void tick() {
         --this.spellCooldown;
         if (this.spellCooldown == 0) {
            this.castSpell();
            SpellcastingIllagerEntity.this.playSound(SpellcastingIllagerEntity.this.getCastSpellSound(), 1.0F, 1.0F);
         }

      }

      protected abstract void castSpell();

      protected int getInitialCooldown() {
         return 20;
      }

      protected abstract int getSpellTicks();

      protected abstract int startTimeDelay();

      @Nullable
      protected abstract SoundEvent getSoundPrepare();

      protected abstract Spell getSpell();
   }

   protected class LookAtTargetGoal extends Goal {
      public LookAtTargetGoal() {
         this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
      }

      public boolean canStart() {
         return SpellcastingIllagerEntity.this.getSpellTicks() > 0;
      }

      public void start() {
         super.start();
         SpellcastingIllagerEntity.this.navigation.stop();
      }

      public void stop() {
         super.stop();
         SpellcastingIllagerEntity.this.setSpell(SpellcastingIllagerEntity.Spell.NONE);
      }

      public void tick() {
         if (SpellcastingIllagerEntity.this.getTarget() != null) {
            SpellcastingIllagerEntity.this.getLookControl().lookAt(SpellcastingIllagerEntity.this.getTarget(), (float)SpellcastingIllagerEntity.this.getMaxHeadRotation(), (float)SpellcastingIllagerEntity.this.getMaxLookPitchChange());
         }

      }
   }
}
