/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.OperatorBlock
 *  net.minecraft.block.TestBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.TestBlockEntity
 *  net.minecraft.block.enums.TestBlockMode
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.BlockStateComponent
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.block.WireOrientation
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.OperatorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.TestBlockEntity;
import net.minecraft.block.enums.TestBlockMode;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class TestBlock
extends BlockWithEntity
implements OperatorBlock {
    public static final MapCodec<TestBlock> CODEC = TestBlock.createCodec(TestBlock::new);
    public static final EnumProperty<TestBlockMode> MODE = Properties.TEST_BLOCK_MODE;

    public TestBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TestBlockEntity(pos, state);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        TestBlockMode testBlockMode;
        BlockStateComponent blockStateComponent = (BlockStateComponent)ctx.getStack().get(DataComponentTypes.BLOCK_STATE);
        BlockState blockState = this.getDefaultState();
        if (blockStateComponent != null && (testBlockMode = (TestBlockMode)blockStateComponent.getValue((Property)MODE)) != null) {
            blockState = (BlockState)blockState.with((Property)MODE, (Comparable)testBlockMode);
        }
        return blockState;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{MODE});
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof TestBlockEntity)) {
            return ActionResult.PASS;
        }
        TestBlockEntity testBlockEntity = (TestBlockEntity)blockEntity;
        if (!player.isCreativeLevelTwoOp()) {
            return ActionResult.PASS;
        }
        if (world.isClient()) {
            player.openTestBlockScreen(testBlockEntity);
        }
        return ActionResult.SUCCESS;
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        TestBlockEntity testBlockEntity = TestBlock.getBlockEntityOnServer((World)world, (BlockPos)pos);
        if (testBlockEntity == null) {
            return;
        }
        testBlockEntity.reset();
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        TestBlockEntity testBlockEntity = TestBlock.getBlockEntityOnServer((World)world, (BlockPos)pos);
        if (testBlockEntity == null) {
            return;
        }
        if (testBlockEntity.getMode() == TestBlockMode.START) {
            return;
        }
        boolean bl = world.isReceivingRedstonePower(pos);
        boolean bl2 = testBlockEntity.isPowered();
        if (bl && !bl2) {
            testBlockEntity.setPowered(true);
            testBlockEntity.trigger();
        } else if (!bl && bl2) {
            testBlockEntity.setPowered(false);
        }
    }

    private static @Nullable TestBlockEntity getBlockEntityOnServer(World world, BlockPos pos) {
        ServerWorld serverWorld;
        BlockEntity blockEntity;
        if (world instanceof ServerWorld && (blockEntity = (serverWorld = (ServerWorld)world).getBlockEntity(pos)) instanceof TestBlockEntity) {
            TestBlockEntity testBlockEntity = (TestBlockEntity)blockEntity;
            return testBlockEntity;
        }
        return null;
    }

    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (state.get((Property)MODE) != TestBlockMode.START) {
            return 0;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TestBlockEntity) {
            TestBlockEntity testBlockEntity = (TestBlockEntity)blockEntity;
            return testBlockEntity.isPowered() ? 15 : 0;
        }
        return 0;
    }

    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        ItemStack itemStack = super.getPickStack(world, pos, state, includeData);
        return TestBlock.applyBlockStateToStack((ItemStack)itemStack, (TestBlockMode)((TestBlockMode)state.get((Property)MODE)));
    }

    public static ItemStack applyBlockStateToStack(ItemStack stack, TestBlockMode mode) {
        stack.set(DataComponentTypes.BLOCK_STATE, (Object)((BlockStateComponent)stack.getOrDefault(DataComponentTypes.BLOCK_STATE, (Object)BlockStateComponent.DEFAULT)).with((Property)MODE, (Comparable)mode));
        return stack;
    }

    protected MapCodec<TestBlock> getCodec() {
        return CODEC;
    }
}

