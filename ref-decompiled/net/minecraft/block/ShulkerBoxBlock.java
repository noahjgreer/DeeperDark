/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.FacingBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.ShulkerBoxBlock
 *  net.minecraft.block.ShulkerBoxBlock$1
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityTicker
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.ShulkerBoxBlockEntity
 *  net.minecraft.block.entity.ShulkerBoxBlockEntity$AnimationStage
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.entity.mob.PiglinBrain
 *  net.minecraft.entity.mob.ShulkerEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.loot.context.LootContextParameters
 *  net.minecraft.loot.context.LootWorldContext$Builder
 *  net.minecraft.screen.NamedScreenHandlerFactory
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.stat.Stats
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.DyeColor
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.ItemScatterer
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class ShulkerBoxBlock
extends BlockWithEntity {
    public static final MapCodec<ShulkerBoxBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)DyeColor.CODEC.optionalFieldOf("color").forGetter(block -> Optional.ofNullable(block.color)), (App)ShulkerBoxBlock.createSettingsCodec()).apply((Applicative)instance, (color, settings) -> new ShulkerBoxBlock((DyeColor)color.orElse(null), settings)));
    public static final Map<Direction, VoxelShape> SHAPES_BY_DIRECTION = VoxelShapes.createFacingShapeMap((VoxelShape)Block.createCuboidZShape((double)16.0, (double)0.0, (double)1.0));
    public static final EnumProperty<Direction> FACING = FacingBlock.FACING;
    public static final Identifier CONTENTS_DYNAMIC_DROP_ID = Identifier.ofVanilla((String)"contents");
    private final @Nullable DyeColor color;

    public MapCodec<ShulkerBoxBlock> getCodec() {
        return CODEC;
    }

    public ShulkerBoxBlock(@Nullable DyeColor color, AbstractBlock.Settings settings) {
        super(settings);
        this.color = color;
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.UP));
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ShulkerBoxBlockEntity(this.color, pos, state);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return ShulkerBoxBlock.validateTicker(type, (BlockEntityType)BlockEntityType.SHULKER_BOX, ShulkerBoxBlockEntity::tick);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world instanceof ServerWorld) {
            ShulkerBoxBlockEntity shulkerBoxBlockEntity;
            ServerWorld serverWorld = (ServerWorld)world;
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ShulkerBoxBlockEntity && ShulkerBoxBlock.canOpen((BlockState)state, (World)world, (BlockPos)pos, (ShulkerBoxBlockEntity)(shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)blockEntity))) {
                player.openHandledScreen((NamedScreenHandlerFactory)shulkerBoxBlockEntity);
                player.incrementStat(Stats.OPEN_SHULKER_BOX);
                PiglinBrain.onGuardedBlockInteracted((ServerWorld)serverWorld, (PlayerEntity)player, (boolean)true);
            }
        }
        return ActionResult.SUCCESS;
    }

    private static boolean canOpen(BlockState state, World world, BlockPos pos, ShulkerBoxBlockEntity entity) {
        if (entity.getAnimationStage() != ShulkerBoxBlockEntity.AnimationStage.CLOSED) {
            return true;
        }
        Box box = ShulkerEntity.calculateBoundingBox((float)1.0f, (Direction)((Direction)state.get((Property)FACING)), (float)0.0f, (float)0.5f, (Vec3d)pos.toBottomCenterPos()).contract(1.0E-6);
        return world.isSpaceEmpty(box);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with((Property)FACING, (Comparable)ctx.getSide());
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING});
    }

    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ShulkerBoxBlockEntity) {
            ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)blockEntity;
            if (!world.isClient() && player.shouldSkipBlockDrops() && !shulkerBoxBlockEntity.isEmpty()) {
                ItemStack itemStack = ShulkerBoxBlock.getItemStack((DyeColor)this.getColor());
                itemStack.applyComponentsFrom(blockEntity.createComponentMap());
                ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, itemStack);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity((Entity)itemEntity);
            } else {
                shulkerBoxBlockEntity.generateLoot(player);
            }
        }
        return super.onBreak(world, pos, state, player);
    }

    protected List<ItemStack> getDroppedStacks(BlockState state, LootWorldContext.Builder builder) {
        BlockEntity blockEntity = (BlockEntity)builder.getOptional(LootContextParameters.BLOCK_ENTITY);
        if (blockEntity instanceof ShulkerBoxBlockEntity) {
            ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)blockEntity;
            builder = builder.addDynamicDrop(CONTENTS_DYNAMIC_DROP_ID, consumer -> {
                for (int i = 0; i < shulkerBoxBlockEntity.size(); ++i) {
                    consumer.accept(shulkerBoxBlockEntity.getStack(i));
                }
            });
        }
        return super.getDroppedStacks(state, builder);
    }

    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        ItemScatterer.onStateReplaced((BlockState)state, (World)world, (BlockPos)pos);
    }

    protected VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        ShulkerBoxBlockEntity shulkerBoxBlockEntity;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ShulkerBoxBlockEntity && !(shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)blockEntity).suffocates()) {
            return (VoxelShape)SHAPES_BY_DIRECTION.get(((Direction)state.get((Property)FACING)).getOpposite());
        }
        return VoxelShapes.fullCube();
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ShulkerBoxBlockEntity) {
            ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)blockEntity;
            return VoxelShapes.cuboid((Box)shulkerBoxBlockEntity.getBoundingBox(state));
        }
        return VoxelShapes.fullCube();
    }

    protected boolean isTransparent(BlockState state) {
        return false;
    }

    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
        return ScreenHandler.calculateComparatorOutput((BlockEntity)world.getBlockEntity(pos));
    }

    public static Block get(@Nullable DyeColor dyeColor) {
        if (dyeColor == null) {
            return Blocks.SHULKER_BOX;
        }
        return switch (1.field_11497[dyeColor.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> Blocks.WHITE_SHULKER_BOX;
            case 2 -> Blocks.ORANGE_SHULKER_BOX;
            case 3 -> Blocks.MAGENTA_SHULKER_BOX;
            case 4 -> Blocks.LIGHT_BLUE_SHULKER_BOX;
            case 5 -> Blocks.YELLOW_SHULKER_BOX;
            case 6 -> Blocks.LIME_SHULKER_BOX;
            case 7 -> Blocks.PINK_SHULKER_BOX;
            case 8 -> Blocks.GRAY_SHULKER_BOX;
            case 9 -> Blocks.LIGHT_GRAY_SHULKER_BOX;
            case 10 -> Blocks.CYAN_SHULKER_BOX;
            case 11 -> Blocks.BLUE_SHULKER_BOX;
            case 12 -> Blocks.BROWN_SHULKER_BOX;
            case 13 -> Blocks.GREEN_SHULKER_BOX;
            case 14 -> Blocks.RED_SHULKER_BOX;
            case 15 -> Blocks.BLACK_SHULKER_BOX;
            case 16 -> Blocks.PURPLE_SHULKER_BOX;
        };
    }

    public @Nullable DyeColor getColor() {
        return this.color;
    }

    public static ItemStack getItemStack(@Nullable DyeColor color) {
        return new ItemStack((ItemConvertible)ShulkerBoxBlock.get((DyeColor)color));
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)FACING, (Comparable)rotation.rotate((Direction)state.get((Property)FACING)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get((Property)FACING)));
    }
}

