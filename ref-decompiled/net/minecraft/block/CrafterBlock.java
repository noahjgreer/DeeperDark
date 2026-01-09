package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import java.util.Optional;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.enums.Orientation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeCache;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class CrafterBlock extends BlockWithEntity {
   public static final MapCodec CODEC = createCodec(CrafterBlock::new);
   public static final BooleanProperty CRAFTING;
   public static final BooleanProperty TRIGGERED;
   private static final EnumProperty ORIENTATION;
   private static final int field_46802 = 6;
   private static final int TRIGGER_DELAY = 4;
   private static final RecipeCache RECIPE_CACHE;
   private static final int field_50015 = 17;

   public CrafterBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(ORIENTATION, Orientation.NORTH_UP)).with(TRIGGERED, false)).with(CRAFTING, false));
   }

   protected MapCodec getCodec() {
      return CODEC;
   }

   protected boolean hasComparatorOutput(BlockState state) {
      return true;
   }

   protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof CrafterBlockEntity crafterBlockEntity) {
         return crafterBlockEntity.getComparatorOutput();
      } else {
         return 0;
      }
   }

   protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
      boolean bl = world.isReceivingRedstonePower(pos);
      boolean bl2 = (Boolean)state.get(TRIGGERED);
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (bl && !bl2) {
         world.scheduleBlockTick(pos, this, 4);
         world.setBlockState(pos, (BlockState)state.with(TRIGGERED, true), 2);
         this.setTriggered(blockEntity, true);
      } else if (!bl && bl2) {
         world.setBlockState(pos, (BlockState)((BlockState)state.with(TRIGGERED, false)).with(CRAFTING, false), 2);
         this.setTriggered(blockEntity, false);
      }

   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      this.craft(state, world, pos);
   }

   @Nullable
   public BlockEntityTicker getTicker(World world, BlockState state, BlockEntityType type) {
      return world.isClient ? null : validateTicker(type, BlockEntityType.CRAFTER, CrafterBlockEntity::tickCrafting);
   }

   private void setTriggered(@Nullable BlockEntity blockEntity, boolean triggered) {
      if (blockEntity instanceof CrafterBlockEntity crafterBlockEntity) {
         crafterBlockEntity.setTriggered(triggered);
      }

   }

   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      CrafterBlockEntity crafterBlockEntity = new CrafterBlockEntity(pos, state);
      crafterBlockEntity.setTriggered(state.contains(TRIGGERED) && (Boolean)state.get(TRIGGERED));
      return crafterBlockEntity;
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      Direction direction = ctx.getPlayerLookDirection().getOpposite();
      Direction var10000;
      switch (direction) {
         case DOWN:
            var10000 = ctx.getHorizontalPlayerFacing().getOpposite();
            break;
         case UP:
            var10000 = ctx.getHorizontalPlayerFacing();
            break;
         case NORTH:
         case SOUTH:
         case WEST:
         case EAST:
            var10000 = Direction.UP;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      Direction direction2 = var10000;
      return (BlockState)((BlockState)this.getDefaultState().with(ORIENTATION, Orientation.byDirections(direction, direction2))).with(TRIGGERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
   }

   public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
      if ((Boolean)state.get(TRIGGERED)) {
         world.scheduleBlockTick(pos, this, 4);
      }

   }

   protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
      ItemScatterer.onStateReplaced(state, world, pos);
   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      if (!world.isClient) {
         BlockEntity var7 = world.getBlockEntity(pos);
         if (var7 instanceof CrafterBlockEntity) {
            CrafterBlockEntity crafterBlockEntity = (CrafterBlockEntity)var7;
            player.openHandledScreen(crafterBlockEntity);
         }
      }

      return ActionResult.SUCCESS;
   }

   protected void craft(BlockState state, ServerWorld world, BlockPos pos) {
      BlockEntity var5 = world.getBlockEntity(pos);
      if (var5 instanceof CrafterBlockEntity crafterBlockEntity) {
         CraftingRecipeInput craftingRecipeInput = crafterBlockEntity.createRecipeInput();
         Optional optional = getCraftingRecipe(world, craftingRecipeInput);
         if (optional.isEmpty()) {
            world.syncWorldEvent(1050, pos, 0);
         } else {
            RecipeEntry recipeEntry = (RecipeEntry)optional.get();
            ItemStack itemStack = ((CraftingRecipe)recipeEntry.value()).craft(craftingRecipeInput, world.getRegistryManager());
            if (itemStack.isEmpty()) {
               world.syncWorldEvent(1050, pos, 0);
            } else {
               crafterBlockEntity.setCraftingTicksRemaining(6);
               world.setBlockState(pos, (BlockState)state.with(CRAFTING, true), 2);
               itemStack.onCraftByCrafter(world);
               this.transferOrSpawnStack(world, pos, crafterBlockEntity, itemStack, state, recipeEntry);
               Iterator var9 = ((CraftingRecipe)recipeEntry.value()).getRecipeRemainders(craftingRecipeInput).iterator();

               while(var9.hasNext()) {
                  ItemStack itemStack2 = (ItemStack)var9.next();
                  if (!itemStack2.isEmpty()) {
                     this.transferOrSpawnStack(world, pos, crafterBlockEntity, itemStack2, state, recipeEntry);
                  }
               }

               crafterBlockEntity.getHeldStacks().forEach((stack) -> {
                  if (!stack.isEmpty()) {
                     stack.decrement(1);
                  }
               });
               crafterBlockEntity.markDirty();
            }
         }
      }
   }

   public static Optional getCraftingRecipe(ServerWorld world, CraftingRecipeInput input) {
      return RECIPE_CACHE.getRecipe(world, input);
   }

   private void transferOrSpawnStack(ServerWorld world, BlockPos pos, CrafterBlockEntity blockEntity, ItemStack stack, BlockState state, RecipeEntry recipe) {
      Direction direction = ((Orientation)state.get(ORIENTATION)).getFacing();
      Inventory inventory = HopperBlockEntity.getInventoryAt(world, pos.offset(direction));
      ItemStack itemStack = stack.copy();
      if (inventory != null && (inventory instanceof CrafterBlockEntity || stack.getCount() > inventory.getMaxCount(stack))) {
         while(!itemStack.isEmpty()) {
            ItemStack itemStack2 = itemStack.copyWithCount(1);
            ItemStack itemStack3 = HopperBlockEntity.transfer(blockEntity, inventory, itemStack2, direction.getOpposite());
            if (!itemStack3.isEmpty()) {
               break;
            }

            itemStack.decrement(1);
         }
      } else if (inventory != null) {
         while(!itemStack.isEmpty()) {
            int i = itemStack.getCount();
            itemStack = HopperBlockEntity.transfer(blockEntity, inventory, itemStack, direction.getOpposite());
            if (i == itemStack.getCount()) {
               break;
            }
         }
      }

      if (!itemStack.isEmpty()) {
         Vec3d vec3d = Vec3d.ofCenter(pos);
         Vec3d vec3d2 = vec3d.offset(direction, 0.7);
         ItemDispenserBehavior.spawnItem(world, itemStack, 6, direction, vec3d2);
         Iterator var12 = world.getNonSpectatingEntities(ServerPlayerEntity.class, Box.of(vec3d, 17.0, 17.0, 17.0)).iterator();

         while(var12.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var12.next();
            Criteria.CRAFTER_RECIPE_CRAFTED.trigger(serverPlayerEntity, recipe.id(), blockEntity.getHeldStacks());
         }

         world.syncWorldEvent(1049, pos, 0);
         world.syncWorldEvent(2010, pos, direction.getIndex());
      }

   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(ORIENTATION, rotation.getDirectionTransformation().mapJigsawOrientation((Orientation)state.get(ORIENTATION)));
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      return (BlockState)state.with(ORIENTATION, mirror.getDirectionTransformation().mapJigsawOrientation((Orientation)state.get(ORIENTATION)));
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(ORIENTATION, TRIGGERED, CRAFTING);
   }

   static {
      CRAFTING = Properties.CRAFTING;
      TRIGGERED = Properties.TRIGGERED;
      ORIENTATION = Properties.ORIENTATION;
      RECIPE_CACHE = new RecipeCache(10);
   }
}
