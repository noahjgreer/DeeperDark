/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.BrewingStandBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityTicker
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.BrewingStandBlockEntity
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.screen.NamedScreenHandlerFactory
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.stat.Stats
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.ItemScatterer
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class BrewingStandBlock
extends BlockWithEntity {
    public static final MapCodec<BrewingStandBlock> CODEC = BrewingStandBlock.createCodec(BrewingStandBlock::new);
    public static final BooleanProperty[] BOTTLE_PROPERTIES = new BooleanProperty[]{Properties.HAS_BOTTLE_0, Properties.HAS_BOTTLE_1, Properties.HAS_BOTTLE_2};
    private static final VoxelShape SHAPE = VoxelShapes.union((VoxelShape)Block.createColumnShape((double)2.0, (double)2.0, (double)14.0), (VoxelShape)Block.createColumnShape((double)14.0, (double)0.0, (double)2.0));

    public MapCodec<BrewingStandBlock> getCodec() {
        return CODEC;
    }

    public BrewingStandBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)BOTTLE_PROPERTIES[0], (Comparable)Boolean.valueOf(false))).with((Property)BOTTLE_PROPERTIES[1], (Comparable)Boolean.valueOf(false))).with((Property)BOTTLE_PROPERTIES[2], (Comparable)Boolean.valueOf(false)));
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BrewingStandBlockEntity(pos, state);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : BrewingStandBlock.validateTicker(type, (BlockEntityType)BlockEntityType.BREWING_STAND, BrewingStandBlockEntity::tick);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockEntity blockEntity;
        if (!world.isClient() && (blockEntity = world.getBlockEntity(pos)) instanceof BrewingStandBlockEntity) {
            BrewingStandBlockEntity brewingStandBlockEntity = (BrewingStandBlockEntity)blockEntity;
            player.openHandledScreen((NamedScreenHandlerFactory)brewingStandBlockEntity);
            player.incrementStat(Stats.INTERACT_WITH_BREWINGSTAND);
        }
        return ActionResult.SUCCESS;
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        double d = (double)pos.getX() + 0.4 + (double)random.nextFloat() * 0.2;
        double e = (double)pos.getY() + 0.7 + (double)random.nextFloat() * 0.3;
        double f = (double)pos.getZ() + 0.4 + (double)random.nextFloat() * 0.2;
        world.addParticleClient((ParticleEffect)ParticleTypes.SMOKE, d, e, f, 0.0, 0.0, 0.0);
    }

    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        ItemScatterer.onStateReplaced((BlockState)state, (World)world, (BlockPos)pos);
    }

    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
        return ScreenHandler.calculateComparatorOutput((BlockEntity)world.getBlockEntity(pos));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{BOTTLE_PROPERTIES[0], BOTTLE_PROPERTIES[1], BOTTLE_PROPERTIES[2]});
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}

