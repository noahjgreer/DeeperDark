package net.minecraft.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

public class ButtonBlock extends WallMountedBlock {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(BlockSetType.CODEC.fieldOf("block_set_type").forGetter((block) -> {
         return block.blockSetType;
      }), Codec.intRange(1, 1024).fieldOf("ticks_to_stay_pressed").forGetter((block) -> {
         return block.pressTicks;
      }), createSettingsCodec()).apply(instance, ButtonBlock::new);
   });
   public static final BooleanProperty POWERED;
   private final BlockSetType blockSetType;
   private final int pressTicks;
   private final Function shapeFunction;

   public MapCodec getCodec() {
      return CODEC;
   }

   public ButtonBlock(BlockSetType blockSetType, int pressTicks, AbstractBlock.Settings settings) {
      super(settings.sounds(blockSetType.soundType()));
      this.blockSetType = blockSetType;
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(POWERED, false)).with(FACE, BlockFace.WALL));
      this.pressTicks = pressTicks;
      this.shapeFunction = this.createShapeFunction();
   }

   private Function createShapeFunction() {
      VoxelShape voxelShape = Block.createCubeShape(14.0);
      VoxelShape voxelShape2 = Block.createCubeShape(12.0);
      Map map = VoxelShapes.createBlockFaceHorizontalFacingShapeMap(Block.createCuboidZShape(6.0, 4.0, 8.0, 16.0));
      return this.createShapeFunction((state) -> {
         return VoxelShapes.combineAndSimplify((VoxelShape)((Map)map.get(state.get(FACE))).get(state.get(FACING)), (Boolean)state.get(POWERED) ? voxelShape : voxelShape2, BooleanBiFunction.ONLY_FIRST);
      });
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (VoxelShape)this.shapeFunction.apply(state);
   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      if ((Boolean)state.get(POWERED)) {
         return ActionResult.CONSUME;
      } else {
         this.powerOn(state, world, pos, player);
         return ActionResult.SUCCESS;
      }
   }

   protected void onExploded(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer stackMerger) {
      if (explosion.canTriggerBlocks() && !(Boolean)state.get(POWERED)) {
         this.powerOn(state, world, pos, (PlayerEntity)null);
      }

      super.onExploded(state, world, pos, explosion, stackMerger);
   }

   public void powerOn(BlockState state, World world, BlockPos pos, @Nullable PlayerEntity player) {
      world.setBlockState(pos, (BlockState)state.with(POWERED, true), 3);
      this.updateNeighbors(state, world, pos);
      world.scheduleBlockTick(pos, this, this.pressTicks);
      this.playClickSound(player, world, pos, true);
      world.emitGameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);
   }

   protected void playClickSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos, boolean powered) {
      world.playSound(powered ? player : null, pos, this.getClickSound(powered), SoundCategory.BLOCKS);
   }

   protected SoundEvent getClickSound(boolean powered) {
      return powered ? this.blockSetType.buttonClickOn() : this.blockSetType.buttonClickOff();
   }

   protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
      if (!moved && (Boolean)state.get(POWERED)) {
         this.updateNeighbors(state, world, pos);
      }

   }

   protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
      return (Boolean)state.get(POWERED) ? 15 : 0;
   }

   protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
      return (Boolean)state.get(POWERED) && getDirection(state) == direction ? 15 : 0;
   }

   protected boolean emitsRedstonePower(BlockState state) {
      return true;
   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if ((Boolean)state.get(POWERED)) {
         this.tryPowerWithProjectiles(state, world, pos);
      }
   }

   protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
      if (!world.isClient && this.blockSetType.canButtonBeActivatedByArrows() && !(Boolean)state.get(POWERED)) {
         this.tryPowerWithProjectiles(state, world, pos);
      }
   }

   protected void tryPowerWithProjectiles(BlockState state, World world, BlockPos pos) {
      PersistentProjectileEntity persistentProjectileEntity = this.blockSetType.canButtonBeActivatedByArrows() ? (PersistentProjectileEntity)world.getNonSpectatingEntities(PersistentProjectileEntity.class, state.getOutlineShape(world, pos).getBoundingBox().offset(pos)).stream().findFirst().orElse((Object)null) : null;
      boolean bl = persistentProjectileEntity != null;
      boolean bl2 = (Boolean)state.get(POWERED);
      if (bl != bl2) {
         world.setBlockState(pos, (BlockState)state.with(POWERED, bl), 3);
         this.updateNeighbors(state, world, pos);
         this.playClickSound((PlayerEntity)null, world, pos, bl);
         world.emitGameEvent(persistentProjectileEntity, bl ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pos);
      }

      if (bl) {
         world.scheduleBlockTick(new BlockPos(pos), this, this.pressTicks);
      }

   }

   private void updateNeighbors(BlockState state, World world, BlockPos pos) {
      Direction direction = getDirection(state).getOpposite();
      WireOrientation wireOrientation = OrientationHelper.getEmissionOrientation(world, direction, direction.getAxis().isHorizontal() ? Direction.UP : (Direction)state.get(FACING));
      world.updateNeighborsAlways(pos, this, wireOrientation);
      world.updateNeighborsAlways(pos.offset(direction), this, wireOrientation);
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(FACING, POWERED, FACE);
   }

   static {
      POWERED = Properties.POWERED;
   }
}
