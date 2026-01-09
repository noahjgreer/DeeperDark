package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PressurePlateBlock extends AbstractPressurePlateBlock {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(BlockSetType.CODEC.fieldOf("block_set_type").forGetter((block) -> {
         return block.blockSetType;
      }), createSettingsCodec()).apply(instance, PressurePlateBlock::new);
   });
   public static final BooleanProperty POWERED;

   public MapCodec getCodec() {
      return CODEC;
   }

   public PressurePlateBlock(BlockSetType type, AbstractBlock.Settings settings) {
      super(settings, type);
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(POWERED, false));
   }

   protected int getRedstoneOutput(BlockState state) {
      return (Boolean)state.get(POWERED) ? 15 : 0;
   }

   protected BlockState setRedstoneOutput(BlockState state, int rsOut) {
      return (BlockState)state.with(POWERED, rsOut > 0);
   }

   protected int getRedstoneOutput(World world, BlockPos pos) {
      Class var10000;
      switch (this.blockSetType.pressurePlateSensitivity()) {
         case EVERYTHING:
            var10000 = Entity.class;
            break;
         case MOBS:
            var10000 = LivingEntity.class;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      Class class_ = var10000;
      return getEntityCount(world, BOX.offset(pos), class_) > 0 ? 15 : 0;
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(POWERED);
   }

   static {
      POWERED = Properties.POWERED;
   }
}
