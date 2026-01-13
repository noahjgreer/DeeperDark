/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.advancement.criterion.Criteria
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.CrafterBlock
 *  net.minecraft.block.CrafterBlock$1
 *  net.minecraft.block.dispenser.ItemDispenserBehavior
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityTicker
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.CrafterBlockEntity
 *  net.minecraft.block.entity.HopperBlockEntity
 *  net.minecraft.block.enums.Orientation
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.recipe.CraftingRecipe
 *  net.minecraft.recipe.RecipeCache
 *  net.minecraft.recipe.RecipeEntry
 *  net.minecraft.recipe.input.CraftingRecipeInput
 *  net.minecraft.recipe.input.RecipeInput
 *  net.minecraft.registry.RegistryWrapper$WrapperLookup
 *  net.minecraft.screen.NamedScreenHandlerFactory
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.ItemScatterer
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.World
 *  net.minecraft.world.block.WireOrientation
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.CrafterBlock;
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
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class CrafterBlock
extends BlockWithEntity {
    public static final MapCodec<CrafterBlock> CODEC = CrafterBlock.createCodec(CrafterBlock::new);
    public static final BooleanProperty CRAFTING = Properties.CRAFTING;
    public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;
    private static final EnumProperty<Orientation> ORIENTATION = Properties.ORIENTATION;
    private static final int field_46802 = 6;
    private static final int TRIGGER_DELAY = 4;
    private static final RecipeCache RECIPE_CACHE = new RecipeCache(10);
    private static final int field_50015 = 17;

    public CrafterBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)ORIENTATION, (Comparable)Orientation.NORTH_UP)).with((Property)TRIGGERED, (Comparable)Boolean.valueOf(false))).with((Property)CRAFTING, (Comparable)Boolean.valueOf(false)));
    }

    protected MapCodec<CrafterBlock> getCodec() {
        return CODEC;
    }

    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CrafterBlockEntity) {
            CrafterBlockEntity crafterBlockEntity = (CrafterBlockEntity)blockEntity;
            return crafterBlockEntity.getComparatorOutput();
        }
        return 0;
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        boolean bl = world.isReceivingRedstonePower(pos);
        boolean bl2 = (Boolean)state.get((Property)TRIGGERED);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (bl && !bl2) {
            world.scheduleBlockTick(pos, (Block)this, 4);
            world.setBlockState(pos, (BlockState)state.with((Property)TRIGGERED, (Comparable)Boolean.valueOf(true)), 2);
            this.setTriggered(blockEntity, true);
        } else if (!bl && bl2) {
            world.setBlockState(pos, (BlockState)((BlockState)state.with((Property)TRIGGERED, (Comparable)Boolean.valueOf(false))).with((Property)CRAFTING, (Comparable)Boolean.valueOf(false)), 2);
            this.setTriggered(blockEntity, false);
        }
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.craft(state, world, pos);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : CrafterBlock.validateTicker(type, (BlockEntityType)BlockEntityType.CRAFTER, CrafterBlockEntity::tickCrafting);
    }

    private void setTriggered(@Nullable BlockEntity blockEntity, boolean triggered) {
        if (blockEntity instanceof CrafterBlockEntity) {
            CrafterBlockEntity crafterBlockEntity = (CrafterBlockEntity)blockEntity;
            crafterBlockEntity.setTriggered(triggered);
        }
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        CrafterBlockEntity crafterBlockEntity = new CrafterBlockEntity(pos, state);
        crafterBlockEntity.setTriggered(state.contains((Property)TRIGGERED) && (Boolean)state.get((Property)TRIGGERED) != false);
        return crafterBlockEntity;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getPlayerLookDirection().getOpposite();
        Direction direction2 = switch (1.field_46804[direction.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> ctx.getHorizontalPlayerFacing().getOpposite();
            case 2 -> ctx.getHorizontalPlayerFacing();
            case 3, 4, 5, 6 -> Direction.UP;
        };
        return (BlockState)((BlockState)this.getDefaultState().with((Property)ORIENTATION, (Comparable)Orientation.byDirections((Direction)direction, (Direction)direction2))).with((Property)TRIGGERED, (Comparable)Boolean.valueOf(ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos())));
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (((Boolean)state.get((Property)TRIGGERED)).booleanValue()) {
            world.scheduleBlockTick(pos, (Block)this, 4);
        }
    }

    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        ItemScatterer.onStateReplaced((BlockState)state, (World)world, (BlockPos)pos);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockEntity blockEntity;
        if (!world.isClient() && (blockEntity = world.getBlockEntity(pos)) instanceof CrafterBlockEntity) {
            CrafterBlockEntity crafterBlockEntity = (CrafterBlockEntity)blockEntity;
            player.openHandledScreen((NamedScreenHandlerFactory)crafterBlockEntity);
        }
        return ActionResult.SUCCESS;
    }

    protected void craft(BlockState state, ServerWorld world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof CrafterBlockEntity)) {
            return;
        }
        CrafterBlockEntity crafterBlockEntity = (CrafterBlockEntity)blockEntity;
        CraftingRecipeInput craftingRecipeInput = crafterBlockEntity.createRecipeInput();
        Optional optional = CrafterBlock.getCraftingRecipe((ServerWorld)world, (CraftingRecipeInput)craftingRecipeInput);
        if (optional.isEmpty()) {
            world.syncWorldEvent(1050, pos, 0);
            return;
        }
        RecipeEntry recipeEntry = (RecipeEntry)optional.get();
        ItemStack itemStack = ((CraftingRecipe)recipeEntry.value()).craft((RecipeInput)craftingRecipeInput, (RegistryWrapper.WrapperLookup)world.getRegistryManager());
        if (itemStack.isEmpty()) {
            world.syncWorldEvent(1050, pos, 0);
            return;
        }
        crafterBlockEntity.setCraftingTicksRemaining(6);
        world.setBlockState(pos, (BlockState)state.with((Property)CRAFTING, (Comparable)Boolean.valueOf(true)), 2);
        itemStack.onCraftByCrafter((World)world);
        this.transferOrSpawnStack(world, pos, crafterBlockEntity, itemStack, state, recipeEntry);
        for (ItemStack itemStack2 : ((CraftingRecipe)recipeEntry.value()).getRecipeRemainders(craftingRecipeInput)) {
            if (itemStack2.isEmpty()) continue;
            this.transferOrSpawnStack(world, pos, crafterBlockEntity, itemStack2, state, recipeEntry);
        }
        crafterBlockEntity.getHeldStacks().forEach(stack -> {
            if (stack.isEmpty()) {
                return;
            }
            stack.decrement(1);
        });
        crafterBlockEntity.markDirty();
    }

    public static Optional<RecipeEntry<CraftingRecipe>> getCraftingRecipe(ServerWorld world, CraftingRecipeInput input) {
        return RECIPE_CACHE.getRecipe(world, input);
    }

    private void transferOrSpawnStack(ServerWorld world, BlockPos pos, CrafterBlockEntity blockEntity, ItemStack stack, BlockState state, RecipeEntry<?> recipe) {
        Direction direction = ((Orientation)state.get((Property)ORIENTATION)).getFacing();
        Inventory inventory = HopperBlockEntity.getInventoryAt((World)world, (BlockPos)pos.offset(direction));
        ItemStack itemStack = stack.copy();
        if (inventory != null && (inventory instanceof CrafterBlockEntity || stack.getCount() > inventory.getMaxCount(stack))) {
            ItemStack itemStack2;
            ItemStack itemStack3;
            while (!itemStack.isEmpty() && (itemStack3 = HopperBlockEntity.transfer((Inventory)blockEntity, (Inventory)inventory, (ItemStack)(itemStack2 = itemStack.copyWithCount(1)), (Direction)direction.getOpposite())).isEmpty()) {
                itemStack.decrement(1);
            }
        } else if (inventory != null) {
            int i;
            while (!itemStack.isEmpty() && (i = itemStack.getCount()) != (itemStack = HopperBlockEntity.transfer((Inventory)blockEntity, (Inventory)inventory, (ItemStack)itemStack, (Direction)direction.getOpposite())).getCount()) {
            }
        }
        if (!itemStack.isEmpty()) {
            Vec3d vec3d = Vec3d.ofCenter((Vec3i)pos);
            Vec3d vec3d2 = vec3d.offset(direction, 0.7);
            ItemDispenserBehavior.spawnItem((World)world, (ItemStack)itemStack, (int)6, (Direction)direction, (Position)vec3d2);
            for (ServerPlayerEntity serverPlayerEntity : world.getNonSpectatingEntities(ServerPlayerEntity.class, Box.of((Vec3d)vec3d, (double)17.0, (double)17.0, (double)17.0))) {
                Criteria.CRAFTER_RECIPE_CRAFTED.trigger(serverPlayerEntity, recipe.id(), (List)blockEntity.getHeldStacks());
            }
            world.syncWorldEvent(1049, pos, 0);
            world.syncWorldEvent(2010, pos, direction.getIndex());
        }
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)ORIENTATION, (Comparable)rotation.getDirectionTransformation().mapJigsawOrientation((Orientation)state.get((Property)ORIENTATION)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return (BlockState)state.with((Property)ORIENTATION, (Comparable)mirror.getDirectionTransformation().mapJigsawOrientation((Orientation)state.get((Property)ORIENTATION)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{ORIENTATION, TRIGGERED, CRAFTING});
    }
}

