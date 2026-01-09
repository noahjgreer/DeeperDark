package net.minecraft.entity.projectile.thrown;

import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.AbstractCandleBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class PotionEntity extends ThrownItemEntity {
   public static final double field_30667 = 4.0;
   protected static final double WATER_POTION_EXPLOSION_SQUARED_RADIUS = 16.0;
   public static final Predicate AFFECTED_BY_WATER = (entity) -> {
      return entity.hurtByWater() || entity.isOnFire();
   };

   public PotionEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   public PotionEntity(EntityType type, World world, LivingEntity owner, ItemStack stack) {
      super(type, owner, world, stack);
   }

   public PotionEntity(EntityType type, World world, double x, double y, double z, ItemStack stack) {
      super(type, x, y, z, world, stack);
   }

   protected double getGravity() {
      return 0.05;
   }

   protected void onBlockHit(BlockHitResult blockHitResult) {
      super.onBlockHit(blockHitResult);
      if (!this.getWorld().isClient) {
         ItemStack itemStack = this.getStack();
         Direction direction = blockHitResult.getSide();
         BlockPos blockPos = blockHitResult.getBlockPos();
         BlockPos blockPos2 = blockPos.offset(direction);
         PotionContentsComponent potionContentsComponent = (PotionContentsComponent)itemStack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
         if (potionContentsComponent.matches(Potions.WATER)) {
            this.extinguishFire(blockPos2);
            this.extinguishFire(blockPos2.offset(direction.getOpposite()));
            Iterator var7 = Direction.Type.HORIZONTAL.iterator();

            while(var7.hasNext()) {
               Direction direction2 = (Direction)var7.next();
               this.extinguishFire(blockPos2.offset(direction2));
            }
         }

      }
   }

   protected void onCollision(HitResult hitResult) {
      super.onCollision(hitResult);
      World var3 = this.getWorld();
      if (var3 instanceof ServerWorld serverWorld) {
         ItemStack itemStack = this.getStack();
         PotionContentsComponent potionContentsComponent = (PotionContentsComponent)itemStack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
         if (potionContentsComponent.matches(Potions.WATER)) {
            this.explodeWaterPotion(serverWorld);
         } else if (potionContentsComponent.hasEffects()) {
            this.spawnAreaEffectCloud(serverWorld, itemStack, hitResult);
         }

         int i = potionContentsComponent.potion().isPresent() && ((Potion)((RegistryEntry)potionContentsComponent.potion().get()).value()).hasInstantEffect() ? 2007 : 2002;
         serverWorld.syncWorldEvent(i, this.getBlockPos(), potionContentsComponent.getColor());
         this.discard();
      }
   }

   private void explodeWaterPotion(ServerWorld world) {
      Box box = this.getBoundingBox().expand(4.0, 2.0, 4.0);
      List list = this.getWorld().getEntitiesByClass(LivingEntity.class, box, AFFECTED_BY_WATER);
      Iterator var4 = list.iterator();

      while(var4.hasNext()) {
         LivingEntity livingEntity = (LivingEntity)var4.next();
         double d = this.squaredDistanceTo(livingEntity);
         if (d < 16.0) {
            if (livingEntity.hurtByWater()) {
               livingEntity.damage(world, this.getDamageSources().indirectMagic(this, this.getOwner()), 1.0F);
            }

            if (livingEntity.isOnFire() && livingEntity.isAlive()) {
               livingEntity.extinguishWithSound();
            }
         }
      }

      List list2 = this.getWorld().getNonSpectatingEntities(AxolotlEntity.class, box);
      Iterator var9 = list2.iterator();

      while(var9.hasNext()) {
         AxolotlEntity axolotlEntity = (AxolotlEntity)var9.next();
         axolotlEntity.hydrateFromPotion();
      }

   }

   protected abstract void spawnAreaEffectCloud(ServerWorld world, ItemStack stack, HitResult hitResult);

   private void extinguishFire(BlockPos pos) {
      BlockState blockState = this.getWorld().getBlockState(pos);
      if (blockState.isIn(BlockTags.FIRE)) {
         this.getWorld().breakBlock(pos, false, this);
      } else if (AbstractCandleBlock.isLitCandle(blockState)) {
         AbstractCandleBlock.extinguish((PlayerEntity)null, blockState, this.getWorld(), pos);
      } else if (CampfireBlock.isLitCampfire(blockState)) {
         this.getWorld().syncWorldEvent((Entity)null, 1009, pos, 0);
         CampfireBlock.extinguish(this.getOwner(), this.getWorld(), pos, blockState);
         this.getWorld().setBlockState(pos, (BlockState)blockState.with(CampfireBlock.LIT, false));
      }

   }

   public DoubleDoubleImmutablePair getKnockback(LivingEntity target, DamageSource source) {
      double d = target.getPos().x - this.getPos().x;
      double e = target.getPos().z - this.getPos().z;
      return DoubleDoubleImmutablePair.of(d, e);
   }
}
