package net.minecraft.entity.decoration;

import java.util.Optional;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

public class EndCrystalEntity extends Entity {
   private static final TrackedData BEAM_TARGET;
   private static final TrackedData SHOW_BOTTOM;
   private static final boolean DEFAULT_SHOW_BOTTOM = true;
   public int endCrystalAge;

   public EndCrystalEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.intersectionChecked = true;
      this.endCrystalAge = this.random.nextInt(100000);
   }

   public EndCrystalEntity(World world, double x, double y, double z) {
      this(EntityType.END_CRYSTAL, world);
      this.setPosition(x, y, z);
   }

   protected Entity.MoveEffect getMoveEffect() {
      return Entity.MoveEffect.NONE;
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      builder.add(BEAM_TARGET, Optional.empty());
      builder.add(SHOW_BOTTOM, true);
   }

   public void tick() {
      ++this.endCrystalAge;
      this.tickBlockCollision();
      this.tickPortalTeleportation();
      if (this.getWorld() instanceof ServerWorld) {
         BlockPos blockPos = this.getBlockPos();
         if (((ServerWorld)this.getWorld()).getEnderDragonFight() != null && this.getWorld().getBlockState(blockPos).isAir()) {
            this.getWorld().setBlockState(blockPos, AbstractFireBlock.getState(this.getWorld(), blockPos));
         }
      }

   }

   protected void writeCustomData(WriteView view) {
      view.putNullable("beam_target", BlockPos.CODEC, this.getBeamTarget());
      view.putBoolean("ShowBottom", this.shouldShowBottom());
   }

   protected void readCustomData(ReadView view) {
      this.setBeamTarget((BlockPos)view.read("beam_target", BlockPos.CODEC).orElse((Object)null));
      this.setShowBottom(view.getBoolean("ShowBottom", true));
   }

   public boolean canHit() {
      return true;
   }

   public final boolean clientDamage(DamageSource source) {
      if (this.isAlwaysInvulnerableTo(source)) {
         return false;
      } else {
         return !(source.getAttacker() instanceof EnderDragonEntity);
      }
   }

   public final boolean damage(ServerWorld world, DamageSource source, float amount) {
      if (this.isAlwaysInvulnerableTo(source)) {
         return false;
      } else if (source.getAttacker() instanceof EnderDragonEntity) {
         return false;
      } else {
         if (!this.isRemoved()) {
            this.remove(Entity.RemovalReason.KILLED);
            if (!source.isIn(DamageTypeTags.IS_EXPLOSION)) {
               DamageSource damageSource = source.getAttacker() != null ? this.getDamageSources().explosion(this, source.getAttacker()) : null;
               world.createExplosion(this, damageSource, (ExplosionBehavior)null, this.getX(), this.getY(), this.getZ(), 6.0F, false, World.ExplosionSourceType.BLOCK);
            }

            this.crystalDestroyed(world, source);
         }

         return true;
      }
   }

   public void kill(ServerWorld world) {
      this.crystalDestroyed(world, this.getDamageSources().generic());
      super.kill(world);
   }

   private void crystalDestroyed(ServerWorld world, DamageSource source) {
      EnderDragonFight enderDragonFight = world.getEnderDragonFight();
      if (enderDragonFight != null) {
         enderDragonFight.crystalDestroyed(this, source);
      }

   }

   public void setBeamTarget(@Nullable BlockPos beamTarget) {
      this.getDataTracker().set(BEAM_TARGET, Optional.ofNullable(beamTarget));
   }

   @Nullable
   public BlockPos getBeamTarget() {
      return (BlockPos)((Optional)this.getDataTracker().get(BEAM_TARGET)).orElse((Object)null);
   }

   public void setShowBottom(boolean showBottom) {
      this.getDataTracker().set(SHOW_BOTTOM, showBottom);
   }

   public boolean shouldShowBottom() {
      return (Boolean)this.getDataTracker().get(SHOW_BOTTOM);
   }

   public boolean shouldRender(double distance) {
      return super.shouldRender(distance) || this.getBeamTarget() != null;
   }

   public ItemStack getPickBlockStack() {
      return new ItemStack(Items.END_CRYSTAL);
   }

   static {
      BEAM_TARGET = DataTracker.registerData(EndCrystalEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_POS);
      SHOW_BOTTOM = DataTracker.registerData(EndCrystalEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
   }
}
