package net.minecraft.entity.vehicle;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

public class TntMinecartEntity extends AbstractMinecartEntity {
   private static final byte PRIME_TNT_STATUS = 10;
   private static final String EXPLOSION_POWER_NBT_KEY = "explosion_power";
   private static final String EXPLOSION_SPEED_FACTOR_NBT_KEY = "explosion_speed_factor";
   private static final String FUSE_NBT_KEY = "fuse";
   private static final float DEFAULT_EXPLOSION_POWER = 4.0F;
   private static final float DEFAULT_EXPLOSION_SPEED_FACTOR = 1.0F;
   private static final int DEFAULT_FUSE_TICKS = -1;
   @Nullable
   private DamageSource damageSource;
   private int fuseTicks = -1;
   private float explosionPower = 4.0F;
   private float explosionSpeedFactor = 1.0F;

   public TntMinecartEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   public BlockState getDefaultContainedBlock() {
      return Blocks.TNT.getDefaultState();
   }

   public void tick() {
      super.tick();
      if (this.fuseTicks > 0) {
         --this.fuseTicks;
         this.getWorld().addParticleClient(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
      } else if (this.fuseTicks == 0) {
         this.explode(this.damageSource, this.getVelocity().horizontalLengthSquared());
      }

      if (this.horizontalCollision) {
         double d = this.getVelocity().horizontalLengthSquared();
         if (d >= 0.009999999776482582) {
            this.explode(d);
         }
      }

   }

   public boolean damage(ServerWorld world, DamageSource source, float amount) {
      Entity entity = source.getSource();
      if (entity instanceof PersistentProjectileEntity persistentProjectileEntity) {
         if (persistentProjectileEntity.isOnFire()) {
            DamageSource damageSource = this.getDamageSources().explosion(this, source.getAttacker());
            this.explode(damageSource, persistentProjectileEntity.getVelocity().lengthSquared());
         }
      }

      return super.damage(world, source, amount);
   }

   public void killAndDropSelf(ServerWorld world, DamageSource damageSource) {
      double d = this.getVelocity().horizontalLengthSquared();
      if (!shouldDetonate(damageSource) && !(d >= 0.009999999776482582)) {
         this.killAndDropItem(world, this.asItem());
      } else {
         if (this.fuseTicks < 0) {
            this.prime(damageSource);
            this.fuseTicks = this.random.nextInt(20) + this.random.nextInt(20);
         }

      }
   }

   protected Item asItem() {
      return Items.TNT_MINECART;
   }

   public ItemStack getPickBlockStack() {
      return new ItemStack(Items.TNT_MINECART);
   }

   protected void explode(double power) {
      this.explode((DamageSource)null, power);
   }

   protected void explode(@Nullable DamageSource damageSource, double power) {
      World var5 = this.getWorld();
      if (var5 instanceof ServerWorld serverWorld) {
         if (serverWorld.getGameRules().getBoolean(GameRules.TNT_EXPLODES)) {
            double d = Math.min(Math.sqrt(power), 5.0);
            serverWorld.createExplosion(this, damageSource, (ExplosionBehavior)null, this.getX(), this.getY(), this.getZ(), (float)((double)this.explosionPower + (double)this.explosionSpeedFactor * this.random.nextDouble() * 1.5 * d), false, World.ExplosionSourceType.TNT);
            this.discard();
         } else if (this.isPrimed()) {
            this.discard();
         }
      }

   }

   public boolean handleFallDamage(double fallDistance, float damagePerDistance, DamageSource damageSource) {
      if (fallDistance >= 3.0) {
         double d = fallDistance / 10.0;
         this.explode(d * d);
      }

      return super.handleFallDamage(fallDistance, damagePerDistance, damageSource);
   }

   public void onActivatorRail(int x, int y, int z, boolean powered) {
      if (powered && this.fuseTicks < 0) {
         this.prime((DamageSource)null);
      }

   }

   public void handleStatus(byte status) {
      if (status == 10) {
         this.prime((DamageSource)null);
      } else {
         super.handleStatus(status);
      }

   }

   public void prime(@Nullable DamageSource source) {
      World var3 = this.getWorld();
      if (var3 instanceof ServerWorld serverWorld) {
         if (!serverWorld.getGameRules().getBoolean(GameRules.TNT_EXPLODES)) {
            return;
         }
      }

      this.fuseTicks = 80;
      if (!this.getWorld().isClient) {
         if (source != null && this.damageSource == null) {
            this.damageSource = this.getDamageSources().explosion(this, source.getAttacker());
         }

         this.getWorld().sendEntityStatus(this, (byte)10);
         if (!this.isSilent()) {
            this.getWorld().playSound((Entity)null, this.getX(), this.getY(), this.getZ(), (SoundEvent)SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
         }
      }

   }

   public int getFuseTicks() {
      return this.fuseTicks;
   }

   public boolean isPrimed() {
      return this.fuseTicks > -1;
   }

   public float getEffectiveExplosionResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState, float max) {
      return !this.isPrimed() || !blockState.isIn(BlockTags.RAILS) && !world.getBlockState(pos.up()).isIn(BlockTags.RAILS) ? super.getEffectiveExplosionResistance(explosion, world, pos, blockState, fluidState, max) : 0.0F;
   }

   public boolean canExplosionDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float explosionPower) {
      return !this.isPrimed() || !state.isIn(BlockTags.RAILS) && !world.getBlockState(pos.up()).isIn(BlockTags.RAILS) ? super.canExplosionDestroyBlock(explosion, world, pos, state, explosionPower) : false;
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.fuseTicks = view.getInt("fuse", -1);
      this.explosionPower = MathHelper.clamp(view.getFloat("explosion_power", 4.0F), 0.0F, 128.0F);
      this.explosionSpeedFactor = MathHelper.clamp(view.getFloat("explosion_speed_factor", 1.0F), 0.0F, 128.0F);
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putInt("fuse", this.fuseTicks);
      if (this.explosionPower != 4.0F) {
         view.putFloat("explosion_power", this.explosionPower);
      }

      if (this.explosionSpeedFactor != 1.0F) {
         view.putFloat("explosion_speed_factor", this.explosionSpeedFactor);
      }

   }

   boolean shouldAlwaysKill(DamageSource source) {
      return shouldDetonate(source);
   }

   private static boolean shouldDetonate(DamageSource source) {
      Entity var2 = source.getSource();
      if (var2 instanceof ProjectileEntity projectileEntity) {
         return projectileEntity.isOnFire();
      } else {
         return source.isIn(DamageTypeTags.IS_FIRE) || source.isIn(DamageTypeTags.IS_EXPLOSION);
      }
   }
}
