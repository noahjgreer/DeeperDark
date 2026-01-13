/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.RedstoneOreBlock
 *  net.minecraft.block.RedstoneTorchBlock
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.BlockItem
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.particle.DustParticleEffect
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.math.intprovider.IntProvider
 *  net.minecraft.util.math.intprovider.UniformIntProvider
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.World
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/*
 * Exception performing whole class analysis ignored.
 */
public class RedstoneOreBlock
extends Block {
    public static final MapCodec<RedstoneOreBlock> CODEC = RedstoneOreBlock.createCodec(RedstoneOreBlock::new);
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    public MapCodec<RedstoneOreBlock> getCodec() {
        return CODEC;
    }

    public RedstoneOreBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)this.getDefaultState().with((Property)LIT, (Comparable)Boolean.valueOf(false)));
    }

    protected void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        RedstoneOreBlock.light((BlockState)state, (World)world, (BlockPos)pos);
        super.onBlockBreakStart(state, world, pos, player);
    }

    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!entity.bypassesSteppingEffects()) {
            RedstoneOreBlock.light((BlockState)state, (World)world, (BlockPos)pos);
        }
        super.onSteppedOn(world, pos, state, entity);
    }

    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient()) {
            RedstoneOreBlock.spawnParticles((World)world, (BlockPos)pos);
        } else {
            RedstoneOreBlock.light((BlockState)state, (World)world, (BlockPos)pos);
        }
        if (stack.getItem() instanceof BlockItem && new ItemPlacementContext(player, hand, stack, hit).canPlace()) {
            return ActionResult.PASS;
        }
        return ActionResult.SUCCESS;
    }

    private static void light(BlockState state, World world, BlockPos pos) {
        RedstoneOreBlock.spawnParticles((World)world, (BlockPos)pos);
        if (!((Boolean)state.get((Property)LIT)).booleanValue()) {
            world.setBlockState(pos, (BlockState)state.with((Property)LIT, (Comparable)Boolean.valueOf(true)), 3);
        }
    }

    protected boolean hasRandomTicks(BlockState state) {
        return (Boolean)state.get((Property)LIT);
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (((Boolean)state.get((Property)LIT)).booleanValue()) {
            world.setBlockState(pos, (BlockState)state.with((Property)LIT, (Comparable)Boolean.valueOf(false)), 3);
        }
    }

    protected void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
        super.onStacksDropped(state, world, pos, tool, dropExperience);
        if (dropExperience) {
            this.dropExperienceWhenMined(world, pos, tool, (IntProvider)UniformIntProvider.create((int)1, (int)5));
        }
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (((Boolean)state.get((Property)LIT)).booleanValue()) {
            RedstoneOreBlock.spawnParticles((World)world, (BlockPos)pos);
        }
    }

    private static void spawnParticles(World world, BlockPos pos) {
        double d = 0.5625;
        Random random = world.random;
        for (Direction direction : Direction.values()) {
            BlockPos blockPos = pos.offset(direction);
            if (world.getBlockState(blockPos).isOpaqueFullCube()) continue;
            Direction.Axis axis = direction.getAxis();
            double e = axis == Direction.Axis.X ? 0.5 + 0.5625 * (double)direction.getOffsetX() : (double)random.nextFloat();
            double f = axis == Direction.Axis.Y ? 0.5 + 0.5625 * (double)direction.getOffsetY() : (double)random.nextFloat();
            double g = axis == Direction.Axis.Z ? 0.5 + 0.5625 * (double)direction.getOffsetZ() : (double)random.nextFloat();
            world.addParticleClient((ParticleEffect)DustParticleEffect.DEFAULT, (double)pos.getX() + e, (double)pos.getY() + f, (double)pos.getZ() + g, 0.0, 0.0, 0.0);
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{LIT});
    }
}

