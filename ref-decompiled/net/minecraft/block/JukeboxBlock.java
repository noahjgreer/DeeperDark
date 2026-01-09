package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.JukeboxPlayableComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class JukeboxBlock extends BlockWithEntity {
   public static final MapCodec CODEC = createCodec(JukeboxBlock::new);
   public static final BooleanProperty HAS_RECORD;

   public MapCodec getCodec() {
      return CODEC;
   }

   public JukeboxBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(HAS_RECORD, false));
   }

   public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
      super.onPlaced(world, pos, state, placer, itemStack);
      NbtComponent nbtComponent = (NbtComponent)itemStack.getOrDefault(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.DEFAULT);
      if (nbtComponent.contains("RecordItem")) {
         world.setBlockState(pos, (BlockState)state.with(HAS_RECORD, true), 2);
      }

   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      if ((Boolean)state.get(HAS_RECORD)) {
         BlockEntity var7 = world.getBlockEntity(pos);
         if (var7 instanceof JukeboxBlockEntity) {
            JukeboxBlockEntity jukeboxBlockEntity = (JukeboxBlockEntity)var7;
            jukeboxBlockEntity.dropRecord();
            return ActionResult.SUCCESS;
         }
      }

      return ActionResult.PASS;
   }

   protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
      if ((Boolean)state.get(HAS_RECORD)) {
         return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
      } else {
         ItemStack itemStack = player.getStackInHand(hand);
         ActionResult actionResult = JukeboxPlayableComponent.tryPlayStack(world, pos, itemStack, player);
         return (ActionResult)(!actionResult.isAccepted() ? ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION : actionResult);
      }
   }

   protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
      ItemScatterer.onStateReplaced(state, world, pos);
   }

   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new JukeboxBlockEntity(pos, state);
   }

   public boolean emitsRedstonePower(BlockState state) {
      return true;
   }

   public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
      BlockEntity var6 = world.getBlockEntity(pos);
      if (var6 instanceof JukeboxBlockEntity jukeboxBlockEntity) {
         if (jukeboxBlockEntity.getManager().isPlaying()) {
            return 15;
         }
      }

      return 0;
   }

   protected boolean hasComparatorOutput(BlockState state) {
      return true;
   }

   protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
      BlockEntity var5 = world.getBlockEntity(pos);
      if (var5 instanceof JukeboxBlockEntity jukeboxBlockEntity) {
         return jukeboxBlockEntity.getComparatorOutput();
      } else {
         return 0;
      }
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(HAS_RECORD);
   }

   @Nullable
   public BlockEntityTicker getTicker(World world, BlockState state, BlockEntityType type) {
      return (Boolean)state.get(HAS_RECORD) ? validateTicker(type, BlockEntityType.JUKEBOX, JukeboxBlockEntity::tick) : null;
   }

   static {
      HAS_RECORD = Properties.HAS_RECORD;
   }
}
