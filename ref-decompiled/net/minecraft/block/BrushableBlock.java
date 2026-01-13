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
 *  net.minecraft.block.BrushableBlock
 *  net.minecraft.block.Falling
 *  net.minecraft.block.FallingBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BrushableBlockEntity
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.FallingBlockEntity
 *  net.minecraft.particle.BlockStateParticleEffect
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.registry.Registries
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Falling;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class BrushableBlock
extends BlockWithEntity
implements Falling {
    public static final MapCodec<BrushableBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Registries.BLOCK.getCodec().fieldOf("turns_into").forGetter(BrushableBlock::getBaseBlock), (App)Registries.SOUND_EVENT.getCodec().fieldOf("brush_sound").forGetter(BrushableBlock::getBrushingSound), (App)Registries.SOUND_EVENT.getCodec().fieldOf("brush_completed_sound").forGetter(BrushableBlock::getBrushingCompleteSound), (App)BrushableBlock.createSettingsCodec()).apply((Applicative)instance, BrushableBlock::new));
    private static final IntProperty DUSTED = Properties.DUSTED;
    public static final int field_42773 = 2;
    private final Block baseBlock;
    private final SoundEvent brushingSound;
    private final SoundEvent brushingCompleteSound;

    public MapCodec<BrushableBlock> getCodec() {
        return CODEC;
    }

    public BrushableBlock(Block baseBlock, SoundEvent brushingSound, SoundEvent brushingCompleteSound, AbstractBlock.Settings settings) {
        super(settings);
        this.baseBlock = baseBlock;
        this.brushingSound = brushingSound;
        this.brushingCompleteSound = brushingCompleteSound;
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)DUSTED, (Comparable)Integer.valueOf(0)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{DUSTED});
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.scheduleBlockTick(pos, (Block)this, 2);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        tickView.scheduleBlockTick(pos, (Block)this, 2);
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BrushableBlockEntity) {
            BrushableBlockEntity brushableBlockEntity = (BrushableBlockEntity)blockEntity;
            brushableBlockEntity.scheduledTick(world);
        }
        if (!FallingBlock.canFallThrough((BlockState)world.getBlockState(pos.down())) || pos.getY() < world.getBottomY()) {
            return;
        }
        FallingBlockEntity fallingBlockEntity = FallingBlockEntity.spawnFromBlock((World)world, (BlockPos)pos, (BlockState)state);
        fallingBlockEntity.setDestroyedOnLanding();
    }

    public void onDestroyedOnLanding(World world, BlockPos pos, FallingBlockEntity fallingBlockEntity) {
        Vec3d vec3d = fallingBlockEntity.getBoundingBox().getCenter();
        world.syncWorldEvent(2001, BlockPos.ofFloored((Position)vec3d), Block.getRawIdFromState((BlockState)fallingBlockEntity.getBlockState()));
        world.emitGameEvent((Entity)fallingBlockEntity, (RegistryEntry)GameEvent.BLOCK_DESTROY, vec3d);
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        BlockPos blockPos;
        if (random.nextInt(16) == 0 && FallingBlock.canFallThrough((BlockState)world.getBlockState(blockPos = pos.down()))) {
            double d = (double)pos.getX() + random.nextDouble();
            double e = (double)pos.getY() - 0.05;
            double f = (double)pos.getZ() + random.nextDouble();
            world.addParticleClient((ParticleEffect)new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, state), d, e, f, 0.0, 0.0, 0.0);
        }
    }

    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BrushableBlockEntity(pos, state);
    }

    public Block getBaseBlock() {
        return this.baseBlock;
    }

    public SoundEvent getBrushingSound() {
        return this.brushingSound;
    }

    public SoundEvent getBrushingCompleteSound() {
        return this.brushingCompleteSound;
    }
}

