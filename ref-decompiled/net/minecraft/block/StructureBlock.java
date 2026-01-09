package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class StructureBlock extends BlockWithEntity implements OperatorBlock {
   public static final MapCodec CODEC = createCodec(StructureBlock::new);
   public static final EnumProperty MODE;

   public MapCodec getCodec() {
      return CODEC;
   }

   public StructureBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(MODE, StructureBlockMode.LOAD));
   }

   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new StructureBlockBlockEntity(pos, state);
   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof StructureBlockBlockEntity) {
         return (ActionResult)(((StructureBlockBlockEntity)blockEntity).openScreen(player) ? ActionResult.SUCCESS : ActionResult.PASS);
      } else {
         return ActionResult.PASS;
      }
   }

   public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
      if (!world.isClient) {
         if (placer != null) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof StructureBlockBlockEntity) {
               ((StructureBlockBlockEntity)blockEntity).setAuthor(placer);
            }
         }

      }
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(MODE);
   }

   protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
      if (world instanceof ServerWorld) {
         BlockEntity blockEntity = world.getBlockEntity(pos);
         if (blockEntity instanceof StructureBlockBlockEntity) {
            StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)blockEntity;
            boolean bl = world.isReceivingRedstonePower(pos);
            boolean bl2 = structureBlockBlockEntity.isPowered();
            if (bl && !bl2) {
               structureBlockBlockEntity.setPowered(true);
               this.doAction((ServerWorld)world, structureBlockBlockEntity);
            } else if (!bl && bl2) {
               structureBlockBlockEntity.setPowered(false);
            }

         }
      }
   }

   private void doAction(ServerWorld world, StructureBlockBlockEntity blockEntity) {
      switch (blockEntity.getMode()) {
         case SAVE:
            blockEntity.saveStructure(false);
            break;
         case LOAD:
            blockEntity.loadAndPlaceStructure(world);
            break;
         case CORNER:
            blockEntity.unloadStructure();
         case DATA:
      }

   }

   static {
      MODE = Properties.STRUCTURE_BLOCK_MODE;
   }
}
