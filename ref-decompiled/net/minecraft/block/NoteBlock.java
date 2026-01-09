package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
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
import org.jetbrains.annotations.Nullable;

public class NoteBlock extends Block {
   public static final MapCodec CODEC = createCodec(NoteBlock::new);
   public static final EnumProperty INSTRUMENT;
   public static final BooleanProperty POWERED;
   public static final IntProperty NOTE;
   public static final int field_41678 = 3;

   public MapCodec getCodec() {
      return CODEC;
   }

   public NoteBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(INSTRUMENT, NoteBlockInstrument.HARP)).with(NOTE, 0)).with(POWERED, false));
   }

   private BlockState getStateWithInstrument(WorldView world, BlockPos pos, BlockState state) {
      NoteBlockInstrument noteBlockInstrument = world.getBlockState(pos.up()).getInstrument();
      if (noteBlockInstrument.isNotBaseBlock()) {
         return (BlockState)state.with(INSTRUMENT, noteBlockInstrument);
      } else {
         NoteBlockInstrument noteBlockInstrument2 = world.getBlockState(pos.down()).getInstrument();
         NoteBlockInstrument noteBlockInstrument3 = noteBlockInstrument2.isNotBaseBlock() ? NoteBlockInstrument.HARP : noteBlockInstrument2;
         return (BlockState)state.with(INSTRUMENT, noteBlockInstrument3);
      }
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      return this.getStateWithInstrument(ctx.getWorld(), ctx.getBlockPos(), this.getDefaultState());
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      boolean bl = direction.getAxis() == Direction.Axis.Y;
      return bl ? this.getStateWithInstrument(world, pos, state) : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
      boolean bl = world.isReceivingRedstonePower(pos);
      if (bl != (Boolean)state.get(POWERED)) {
         if (bl) {
            this.playNote((Entity)null, state, world, pos);
         }

         world.setBlockState(pos, (BlockState)state.with(POWERED, bl), 3);
      }

   }

   private void playNote(@Nullable Entity entity, BlockState state, World world, BlockPos pos) {
      if (((NoteBlockInstrument)state.get(INSTRUMENT)).isNotBaseBlock() || world.getBlockState(pos.up()).isAir()) {
         world.addSyncedBlockEvent(pos, this, 0, 0);
         world.emitGameEvent(entity, GameEvent.NOTE_BLOCK_PLAY, pos);
      }

   }

   protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
      return (ActionResult)(stack.isIn(ItemTags.NOTEBLOCK_TOP_INSTRUMENTS) && hit.getSide() == Direction.UP ? ActionResult.PASS : super.onUseWithItem(stack, state, world, pos, player, hand, hit));
   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      if (!world.isClient) {
         state = (BlockState)state.cycle(NOTE);
         world.setBlockState(pos, state, 3);
         this.playNote(player, state, world, pos);
         player.incrementStat(Stats.TUNE_NOTEBLOCK);
      }

      return ActionResult.SUCCESS;
   }

   protected void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
      if (!world.isClient) {
         this.playNote(player, state, world, pos);
         player.incrementStat(Stats.PLAY_NOTEBLOCK);
      }
   }

   public static float getNotePitch(int note) {
      return (float)Math.pow(2.0, (double)(note - 12) / 12.0);
   }

   protected boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
      NoteBlockInstrument noteBlockInstrument = (NoteBlockInstrument)state.get(INSTRUMENT);
      float f;
      if (noteBlockInstrument.canBePitched()) {
         int i = (Integer)state.get(NOTE);
         f = getNotePitch(i);
         world.addParticleClient(ParticleTypes.NOTE, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, (double)i / 24.0, 0.0, 0.0);
      } else {
         f = 1.0F;
      }

      RegistryEntry registryEntry;
      if (noteBlockInstrument.hasCustomSound()) {
         Identifier identifier = this.getCustomSound(world, pos);
         if (identifier == null) {
            return false;
         }

         registryEntry = RegistryEntry.of(SoundEvent.of(identifier));
      } else {
         registryEntry = noteBlockInstrument.getSound();
      }

      world.playSound((Entity)null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, (RegistryEntry)registryEntry, SoundCategory.RECORDS, 3.0F, f, world.random.nextLong());
      return true;
   }

   @Nullable
   private Identifier getCustomSound(World world, BlockPos pos) {
      BlockEntity var4 = world.getBlockEntity(pos.up());
      if (var4 instanceof SkullBlockEntity skullBlockEntity) {
         return skullBlockEntity.getNoteBlockSound();
      } else {
         return null;
      }
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(INSTRUMENT, POWERED, NOTE);
   }

   static {
      INSTRUMENT = Properties.INSTRUMENT;
      POWERED = Properties.POWERED;
      NOTE = Properties.NOTE;
   }
}
