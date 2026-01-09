package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

public class TntBlock extends Block {
   public static final MapCodec CODEC = createCodec(TntBlock::new);
   public static final BooleanProperty UNSTABLE;

   public MapCodec getCodec() {
      return CODEC;
   }

   public TntBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)this.getDefaultState().with(UNSTABLE, false));
   }

   protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
      if (!oldState.isOf(state.getBlock())) {
         if (world.isReceivingRedstonePower(pos) && primeTnt(world, pos)) {
            world.removeBlock(pos, false);
         }

      }
   }

   protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
      if (world.isReceivingRedstonePower(pos) && primeTnt(world, pos)) {
         world.removeBlock(pos, false);
      }

   }

   public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
      if (!world.isClient() && !player.getAbilities().creativeMode && (Boolean)state.get(UNSTABLE)) {
         primeTnt(world, pos);
      }

      return super.onBreak(world, pos, state, player);
   }

   public void onDestroyedByExplosion(ServerWorld world, BlockPos pos, Explosion explosion) {
      if (world.getGameRules().getBoolean(GameRules.TNT_EXPLODES)) {
         TntEntity tntEntity = new TntEntity(world, (double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, explosion.getCausingEntity());
         int i = tntEntity.getFuse();
         tntEntity.setFuse((short)(world.random.nextInt(i / 4) + i / 8));
         world.spawnEntity(tntEntity);
      }
   }

   public static boolean primeTnt(World world, BlockPos pos) {
      return primeTnt(world, pos, (LivingEntity)null);
   }

   private static boolean primeTnt(World world, BlockPos pos, @Nullable LivingEntity igniter) {
      if (world instanceof ServerWorld serverWorld) {
         if (serverWorld.getGameRules().getBoolean(GameRules.TNT_EXPLODES)) {
            TntEntity tntEntity = new TntEntity(world, (double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, igniter);
            world.spawnEntity(tntEntity);
            world.playSound((Entity)null, tntEntity.getX(), tntEntity.getY(), tntEntity.getZ(), (SoundEvent)SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.emitGameEvent(igniter, GameEvent.PRIME_FUSE, pos);
            return true;
         }
      }

      return false;
   }

   protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
      if (!stack.isOf(Items.FLINT_AND_STEEL) && !stack.isOf(Items.FIRE_CHARGE)) {
         return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
      } else {
         if (primeTnt(world, pos, player)) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
            Item item = stack.getItem();
            if (stack.isOf(Items.FLINT_AND_STEEL)) {
               stack.damage(1, player, (EquipmentSlot)LivingEntity.getSlotForHand(hand));
            } else {
               stack.decrementUnlessCreative(1, player);
            }

            player.incrementStat(Stats.USED.getOrCreateStat(item));
         } else if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            if (!serverWorld.getGameRules().getBoolean(GameRules.TNT_EXPLODES)) {
               player.sendMessage(Text.translatable("block.minecraft.tnt.disabled"), true);
               return ActionResult.PASS;
            }
         }

         return ActionResult.SUCCESS;
      }
   }

   protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
      if (world instanceof ServerWorld serverWorld) {
         BlockPos blockPos = hit.getBlockPos();
         Entity entity = projectile.getOwner();
         if (projectile.isOnFire() && projectile.canModifyAt(serverWorld, blockPos) && primeTnt(world, blockPos, entity instanceof LivingEntity ? (LivingEntity)entity : null)) {
            world.removeBlock(blockPos, false);
         }
      }

   }

   public boolean shouldDropItemsOnExplosion(Explosion explosion) {
      return false;
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(UNSTABLE);
   }

   static {
      UNSTABLE = Properties.UNSTABLE;
   }
}
