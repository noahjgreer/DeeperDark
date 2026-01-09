package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SculkCatalystBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SculkCatalystBlock extends BlockWithEntity {
   public static final MapCodec CODEC = createCodec(SculkCatalystBlock::new);
   public static final BooleanProperty BLOOM;
   private final IntProvider experience = ConstantIntProvider.create(5);

   public MapCodec getCodec() {
      return CODEC;
   }

   public SculkCatalystBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(BLOOM, false));
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(BLOOM);
   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if ((Boolean)state.get(BLOOM)) {
         world.setBlockState(pos, (BlockState)state.with(BLOOM, false), 3);
      }

   }

   @Nullable
   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new SculkCatalystBlockEntity(pos, state);
   }

   @Nullable
   public BlockEntityTicker getTicker(World world, BlockState state, BlockEntityType type) {
      return world.isClient ? null : validateTicker(type, BlockEntityType.SCULK_CATALYST, SculkCatalystBlockEntity::tick);
   }

   protected void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
      super.onStacksDropped(state, world, pos, tool, dropExperience);
      if (dropExperience) {
         this.dropExperienceWhenMined(world, pos, tool, this.experience);
      }

   }

   static {
      BLOOM = Properties.BLOOM;
   }
}
