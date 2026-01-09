package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ShulkerBoxBlock extends BlockWithEntity {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(DyeColor.CODEC.optionalFieldOf("color").forGetter((block) -> {
         return Optional.ofNullable(block.color);
      }), createSettingsCodec()).apply(instance, (color, settings) -> {
         return new ShulkerBoxBlock((DyeColor)color.orElse((Object)null), settings);
      });
   });
   public static final Map SHAPES_BY_DIRECTION = VoxelShapes.createFacingShapeMap(Block.createCuboidZShape(16.0, 0.0, 1.0));
   public static final EnumProperty FACING;
   public static final Identifier CONTENTS_DYNAMIC_DROP_ID;
   @Nullable
   private final DyeColor color;

   public MapCodec getCodec() {
      return CODEC;
   }

   public ShulkerBoxBlock(@Nullable DyeColor color, AbstractBlock.Settings settings) {
      super(settings);
      this.color = color;
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.UP));
   }

   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new ShulkerBoxBlockEntity(this.color, pos, state);
   }

   @Nullable
   public BlockEntityTicker getTicker(World world, BlockState state, BlockEntityType type) {
      return validateTicker(type, BlockEntityType.SHULKER_BOX, ShulkerBoxBlockEntity::tick);
   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      if (world instanceof ServerWorld serverWorld) {
         BlockEntity var8 = world.getBlockEntity(pos);
         if (var8 instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity) {
            if (canOpen(state, world, pos, shulkerBoxBlockEntity)) {
               player.openHandledScreen(shulkerBoxBlockEntity);
               player.incrementStat(Stats.OPEN_SHULKER_BOX);
               PiglinBrain.onGuardedBlockInteracted(serverWorld, player, true);
            }
         }
      }

      return ActionResult.SUCCESS;
   }

   private static boolean canOpen(BlockState state, World world, BlockPos pos, ShulkerBoxBlockEntity entity) {
      if (entity.getAnimationStage() != ShulkerBoxBlockEntity.AnimationStage.CLOSED) {
         return true;
      } else {
         Box box = ShulkerEntity.calculateBoundingBox(1.0F, (Direction)state.get(FACING), 0.0F, 0.5F, pos.toBottomCenterPos()).contract(1.0E-6);
         return world.isSpaceEmpty(box);
      }
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      return (BlockState)this.getDefaultState().with(FACING, ctx.getSide());
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(FACING);
   }

   public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity) {
         if (!world.isClient && player.shouldSkipBlockDrops() && !shulkerBoxBlockEntity.isEmpty()) {
            ItemStack itemStack = getItemStack(this.getColor());
            itemStack.applyComponentsFrom(blockEntity.createComponentMap());
            ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, itemStack);
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);
         } else {
            shulkerBoxBlockEntity.generateLoot(player);
         }
      }

      return super.onBreak(world, pos, state, player);
   }

   protected List getDroppedStacks(BlockState state, LootWorldContext.Builder builder) {
      BlockEntity blockEntity = (BlockEntity)builder.getOptional(LootContextParameters.BLOCK_ENTITY);
      if (blockEntity instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity) {
         builder = builder.addDynamicDrop(CONTENTS_DYNAMIC_DROP_ID, (lootConsumer) -> {
            for(int i = 0; i < shulkerBoxBlockEntity.size(); ++i) {
               lootConsumer.accept(shulkerBoxBlockEntity.getStack(i));
            }

         });
      }

      return super.getDroppedStacks(state, builder);
   }

   protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
      ItemScatterer.onStateReplaced(state, world, pos);
   }

   protected VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity) {
         if (!shulkerBoxBlockEntity.suffocates()) {
            return (VoxelShape)SHAPES_BY_DIRECTION.get(((Direction)state.get(FACING)).getOpposite());
         }
      }

      return VoxelShapes.fullCube();
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof ShulkerBoxBlockEntity shulkerBoxBlockEntity) {
         return VoxelShapes.cuboid(shulkerBoxBlockEntity.getBoundingBox(state));
      } else {
         return VoxelShapes.fullCube();
      }
   }

   protected boolean isTransparent(BlockState state) {
      return false;
   }

   protected boolean hasComparatorOutput(BlockState state) {
      return true;
   }

   protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
      return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
   }

   public static Block get(@Nullable DyeColor dyeColor) {
      if (dyeColor == null) {
         return Blocks.SHULKER_BOX;
      } else {
         Block var10000;
         switch (dyeColor) {
            case WHITE:
               var10000 = Blocks.WHITE_SHULKER_BOX;
               break;
            case ORANGE:
               var10000 = Blocks.ORANGE_SHULKER_BOX;
               break;
            case MAGENTA:
               var10000 = Blocks.MAGENTA_SHULKER_BOX;
               break;
            case LIGHT_BLUE:
               var10000 = Blocks.LIGHT_BLUE_SHULKER_BOX;
               break;
            case YELLOW:
               var10000 = Blocks.YELLOW_SHULKER_BOX;
               break;
            case LIME:
               var10000 = Blocks.LIME_SHULKER_BOX;
               break;
            case PINK:
               var10000 = Blocks.PINK_SHULKER_BOX;
               break;
            case GRAY:
               var10000 = Blocks.GRAY_SHULKER_BOX;
               break;
            case LIGHT_GRAY:
               var10000 = Blocks.LIGHT_GRAY_SHULKER_BOX;
               break;
            case CYAN:
               var10000 = Blocks.CYAN_SHULKER_BOX;
               break;
            case BLUE:
               var10000 = Blocks.BLUE_SHULKER_BOX;
               break;
            case BROWN:
               var10000 = Blocks.BROWN_SHULKER_BOX;
               break;
            case GREEN:
               var10000 = Blocks.GREEN_SHULKER_BOX;
               break;
            case RED:
               var10000 = Blocks.RED_SHULKER_BOX;
               break;
            case BLACK:
               var10000 = Blocks.BLACK_SHULKER_BOX;
               break;
            case PURPLE:
               var10000 = Blocks.PURPLE_SHULKER_BOX;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }
   }

   @Nullable
   public DyeColor getColor() {
      return this.color;
   }

   public static ItemStack getItemStack(@Nullable DyeColor color) {
      return new ItemStack(get(color));
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
   }

   static {
      FACING = FacingBlock.FACING;
      CONTENTS_DYNAMIC_DROP_ID = Identifier.ofVanilla("contents");
   }
}
