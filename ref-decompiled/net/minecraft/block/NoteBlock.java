/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.NoteBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.SkullBlockEntity
 *  net.minecraft.block.enums.NoteBlockInstrument
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.tag.ItemTags
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.stat.Stats
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.Hand
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.block.WireOrientation
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class NoteBlock
extends Block {
    public static final MapCodec<NoteBlock> CODEC = NoteBlock.createCodec(NoteBlock::new);
    public static final EnumProperty<NoteBlockInstrument> INSTRUMENT = Properties.INSTRUMENT;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final IntProperty NOTE = Properties.NOTE;
    public static final int field_41678 = 3;

    public MapCodec<NoteBlock> getCodec() {
        return CODEC;
    }

    public NoteBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)INSTRUMENT, (Comparable)NoteBlockInstrument.HARP)).with((Property)NOTE, (Comparable)Integer.valueOf(0))).with((Property)POWERED, (Comparable)Boolean.valueOf(false)));
    }

    private BlockState getStateWithInstrument(WorldView world, BlockPos pos, BlockState state) {
        NoteBlockInstrument noteBlockInstrument = world.getBlockState(pos.up()).getInstrument();
        if (noteBlockInstrument.isNotBaseBlock()) {
            return (BlockState)state.with((Property)INSTRUMENT, (Comparable)noteBlockInstrument);
        }
        NoteBlockInstrument noteBlockInstrument2 = world.getBlockState(pos.down()).getInstrument();
        NoteBlockInstrument noteBlockInstrument3 = noteBlockInstrument2.isNotBaseBlock() ? NoteBlockInstrument.HARP : noteBlockInstrument2;
        return (BlockState)state.with((Property)INSTRUMENT, (Comparable)noteBlockInstrument3);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getStateWithInstrument((WorldView)ctx.getWorld(), ctx.getBlockPos(), this.getDefaultState());
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        boolean bl;
        boolean bl2 = bl = direction.getAxis() == Direction.Axis.Y;
        if (bl) {
            return this.getStateWithInstrument(world, pos, state);
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        boolean bl = world.isReceivingRedstonePower(pos);
        if (bl != (Boolean)state.get((Property)POWERED)) {
            if (bl) {
                this.playNote(null, state, world, pos);
            }
            world.setBlockState(pos, (BlockState)state.with((Property)POWERED, (Comparable)Boolean.valueOf(bl)), 3);
        }
    }

    private void playNote(@Nullable Entity entity, BlockState state, World world, BlockPos pos) {
        if (((NoteBlockInstrument)state.get((Property)INSTRUMENT)).isNotBaseBlock() || world.getBlockState(pos.up()).isAir()) {
            world.addSyncedBlockEvent(pos, (Block)this, 0, 0);
            world.emitGameEvent(entity, (RegistryEntry)GameEvent.NOTE_BLOCK_PLAY, pos);
        }
    }

    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.isIn(ItemTags.NOTEBLOCK_TOP_INSTRUMENTS) && hit.getSide() == Direction.UP) {
            return ActionResult.PASS;
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()) {
            state = (BlockState)state.cycle((Property)NOTE);
            world.setBlockState(pos, state, 3);
            this.playNote((Entity)player, state, world, pos);
            player.incrementStat(Stats.TUNE_NOTEBLOCK);
        }
        return ActionResult.SUCCESS;
    }

    protected void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (world.isClient()) {
            return;
        }
        this.playNote((Entity)player, state, world, pos);
        player.incrementStat(Stats.PLAY_NOTEBLOCK);
    }

    public static float getNotePitch(int note) {
        return (float)Math.pow(2.0, (double)(note - 12) / 12.0);
    }

    protected boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        RegistryEntry registryEntry;
        float f;
        NoteBlockInstrument noteBlockInstrument = (NoteBlockInstrument)state.get((Property)INSTRUMENT);
        if (noteBlockInstrument.canBePitched()) {
            int i = (Integer)state.get((Property)NOTE);
            f = NoteBlock.getNotePitch((int)i);
            world.addParticleClient((ParticleEffect)ParticleTypes.NOTE, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, (double)i / 24.0, 0.0, 0.0);
        } else {
            f = 1.0f;
        }
        if (noteBlockInstrument.hasCustomSound()) {
            Identifier identifier = this.getCustomSound(world, pos);
            if (identifier == null) {
                return false;
            }
            registryEntry = RegistryEntry.of((Object)SoundEvent.of((Identifier)identifier));
        } else {
            registryEntry = noteBlockInstrument.getSound();
        }
        world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, registryEntry, SoundCategory.RECORDS, 3.0f, f, world.random.nextLong());
        return true;
    }

    private @Nullable Identifier getCustomSound(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos.up());
        if (blockEntity instanceof SkullBlockEntity) {
            SkullBlockEntity skullBlockEntity = (SkullBlockEntity)blockEntity;
            return skullBlockEntity.getNoteBlockSound();
        }
        return null;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{INSTRUMENT, POWERED, NOTE});
    }
}

