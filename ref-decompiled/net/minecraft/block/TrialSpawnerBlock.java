package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TrialSpawnerBlock extends BlockWithEntity {
   public static final MapCodec CODEC = createCodec(TrialSpawnerBlock::new);
   public static final EnumProperty TRIAL_SPAWNER_STATE;
   public static final BooleanProperty OMINOUS;

   public MapCodec getCodec() {
      return CODEC;
   }

   public TrialSpawnerBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(TRIAL_SPAWNER_STATE, TrialSpawnerState.INACTIVE)).with(OMINOUS, false));
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(TRIAL_SPAWNER_STATE, OMINOUS);
   }

   @Nullable
   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new TrialSpawnerBlockEntity(pos, state);
   }

   @Nullable
   public BlockEntityTicker getTicker(World world, BlockState state, BlockEntityType type) {
      BlockEntityTicker var10000;
      if (world instanceof ServerWorld serverWorld) {
         var10000 = validateTicker(type, BlockEntityType.TRIAL_SPAWNER, (worldx, pos, statex, blockEntity) -> {
            blockEntity.getSpawner().tickServer(serverWorld, pos, (Boolean)statex.getOrEmpty(Properties.OMINOUS).orElse(false));
         });
      } else {
         var10000 = validateTicker(type, BlockEntityType.TRIAL_SPAWNER, (worldx, pos, statex, blockEntity) -> {
            blockEntity.getSpawner().tickClient(worldx, pos, (Boolean)statex.getOrEmpty(Properties.OMINOUS).orElse(false));
         });
      }

      return var10000;
   }

   static {
      TRIAL_SPAWNER_STATE = Properties.TRIAL_SPAWNER_STATE;
      OMINOUS = Properties.OMINOUS;
   }
}
