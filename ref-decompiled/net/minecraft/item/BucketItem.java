package net.minecraft.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class BucketItem extends Item implements FluidModificationItem {
   private final Fluid fluid;

   public BucketItem(Fluid fluid, Item.Settings settings) {
      super(settings);
      this.fluid = fluid;
   }

   public ActionResult use(World world, PlayerEntity user, Hand hand) {
      ItemStack itemStack = user.getStackInHand(hand);
      BlockHitResult blockHitResult = raycast(world, user, this.fluid == Fluids.EMPTY ? RaycastContext.FluidHandling.SOURCE_ONLY : RaycastContext.FluidHandling.NONE);
      if (blockHitResult.getType() == HitResult.Type.MISS) {
         return ActionResult.PASS;
      } else if (blockHitResult.getType() != HitResult.Type.BLOCK) {
         return ActionResult.PASS;
      } else {
         BlockPos blockPos = blockHitResult.getBlockPos();
         Direction direction = blockHitResult.getSide();
         BlockPos blockPos2 = blockPos.offset(direction);
         if (world.canEntityModifyAt(user, blockPos) && user.canPlaceOn(blockPos2, direction, itemStack)) {
            BlockState blockState;
            ItemStack itemStack2;
            if (this.fluid == Fluids.EMPTY) {
               blockState = world.getBlockState(blockPos);
               Block var14 = blockState.getBlock();
               if (var14 instanceof FluidDrainable) {
                  FluidDrainable fluidDrainable = (FluidDrainable)var14;
                  itemStack2 = fluidDrainable.tryDrainFluid(user, world, blockPos, blockState);
                  if (!itemStack2.isEmpty()) {
                     user.incrementStat(Stats.USED.getOrCreateStat(this));
                     fluidDrainable.getBucketFillSound().ifPresent((sound) -> {
                        user.playSound(sound, 1.0F, 1.0F);
                     });
                     world.emitGameEvent(user, GameEvent.FLUID_PICKUP, blockPos);
                     ItemStack itemStack3 = ItemUsage.exchangeStack(itemStack, user, itemStack2);
                     if (!world.isClient) {
                        Criteria.FILLED_BUCKET.trigger((ServerPlayerEntity)user, itemStack2);
                     }

                     return ActionResult.SUCCESS.withNewHandStack(itemStack3);
                  }
               }

               return ActionResult.FAIL;
            } else {
               blockState = world.getBlockState(blockPos);
               BlockPos blockPos3 = blockState.getBlock() instanceof FluidFillable && this.fluid == Fluids.WATER ? blockPos : blockPos2;
               if (this.placeFluid(user, world, blockPos3, blockHitResult)) {
                  this.onEmptied(user, world, itemStack, blockPos3);
                  if (user instanceof ServerPlayerEntity) {
                     Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)user, blockPos3, itemStack);
                  }

                  user.incrementStat(Stats.USED.getOrCreateStat(this));
                  itemStack2 = ItemUsage.exchangeStack(itemStack, user, getEmptiedStack(itemStack, user));
                  return ActionResult.SUCCESS.withNewHandStack(itemStack2);
               } else {
                  return ActionResult.FAIL;
               }
            }
         } else {
            return ActionResult.FAIL;
         }
      }
   }

   public static ItemStack getEmptiedStack(ItemStack stack, PlayerEntity player) {
      return !player.isInCreativeMode() ? new ItemStack(Items.BUCKET) : stack;
   }

   public void onEmptied(@Nullable LivingEntity user, World world, ItemStack stack, BlockPos pos) {
   }

   public boolean placeFluid(@Nullable LivingEntity user, World world, BlockPos pos, @Nullable BlockHitResult hitResult) {
      Fluid var6 = this.fluid;
      if (!(var6 instanceof FlowableFluid flowableFluid)) {
         return false;
      } else {
         FlowableFluid flowableFluid;
         Block block;
         boolean bl;
         FluidFillable fluidFillable;
         BlockState blockState;
         boolean var10000;
         label82: {
            blockState = world.getBlockState(pos);
            block = blockState.getBlock();
            bl = blockState.canBucketPlace(this.fluid);
            if (!blockState.isAir() && !bl) {
               label80: {
                  if (block instanceof FluidFillable) {
                     fluidFillable = (FluidFillable)block;
                     if (fluidFillable.canFillWithFluid(user, world, pos, blockState, this.fluid)) {
                        break label80;
                     }
                  }

                  var10000 = false;
                  break label82;
               }
            }

            var10000 = true;
         }

         boolean bl2 = var10000;
         if (!bl2) {
            return hitResult != null && this.placeFluid(user, world, hitResult.getBlockPos().offset(hitResult.getSide()), (BlockHitResult)null);
         } else if (world.getDimension().ultrawarm() && this.fluid.isIn(FluidTags.WATER)) {
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();
            world.playSound(user, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

            for(int l = 0; l < 8; ++l) {
               world.addParticleClient(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0, 0.0, 0.0);
            }

            return true;
         } else {
            if (block instanceof FluidFillable) {
               fluidFillable = (FluidFillable)block;
               if (this.fluid == Fluids.WATER) {
                  fluidFillable.tryFillWithFluid(world, pos, blockState, flowableFluid.getStill(false));
                  this.playEmptyingSound(user, world, pos);
                  return true;
               }
            }

            if (!world.isClient && bl && !blockState.isLiquid()) {
               world.breakBlock(pos, true);
            }

            if (!world.setBlockState(pos, this.fluid.getDefaultState().getBlockState(), 11) && !blockState.getFluidState().isStill()) {
               return false;
            } else {
               this.playEmptyingSound(user, world, pos);
               return true;
            }
         }
      }
   }

   protected void playEmptyingSound(@Nullable LivingEntity user, WorldAccess world, BlockPos pos) {
      SoundEvent soundEvent = this.fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
      world.playSound(user, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
      world.emitGameEvent((Entity)user, (RegistryEntry)GameEvent.FLUID_PLACE, (BlockPos)pos);
   }
}
