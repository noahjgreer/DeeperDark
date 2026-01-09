package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Nameable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EnchantingTableBlock extends BlockWithEntity {
   public static final MapCodec CODEC = createCodec(EnchantingTableBlock::new);
   public static final List POWER_PROVIDER_OFFSETS = BlockPos.stream(-2, 0, -2, 2, 1, 2).filter((pos) -> {
      return Math.abs(pos.getX()) == 2 || Math.abs(pos.getZ()) == 2;
   }).map(BlockPos::toImmutable).toList();
   private static final VoxelShape SHAPE = Block.createColumnShape(16.0, 0.0, 12.0);

   public MapCodec getCodec() {
      return CODEC;
   }

   public EnchantingTableBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   public static boolean canAccessPowerProvider(World world, BlockPos tablePos, BlockPos providerOffset) {
      return world.getBlockState(tablePos.add(providerOffset)).isIn(BlockTags.ENCHANTMENT_POWER_PROVIDER) && world.getBlockState(tablePos.add(providerOffset.getX() / 2, providerOffset.getY(), providerOffset.getZ() / 2)).isIn(BlockTags.ENCHANTMENT_POWER_TRANSMITTER);
   }

   protected boolean hasSidedTransparency(BlockState state) {
      return true;
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPE;
   }

   public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
      super.randomDisplayTick(state, world, pos, random);
      Iterator var5 = POWER_PROVIDER_OFFSETS.iterator();

      while(var5.hasNext()) {
         BlockPos blockPos = (BlockPos)var5.next();
         if (random.nextInt(16) == 0 && canAccessPowerProvider(world, pos, blockPos)) {
            world.addParticleClient(ParticleTypes.ENCHANT, (double)pos.getX() + 0.5, (double)pos.getY() + 2.0, (double)pos.getZ() + 0.5, (double)((float)blockPos.getX() + random.nextFloat()) - 0.5, (double)((float)blockPos.getY() - random.nextFloat() - 1.0F), (double)((float)blockPos.getZ() + random.nextFloat()) - 0.5);
         }
      }

   }

   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new EnchantingTableBlockEntity(pos, state);
   }

   @Nullable
   public BlockEntityTicker getTicker(World world, BlockState state, BlockEntityType type) {
      return world.isClient ? validateTicker(type, BlockEntityType.ENCHANTING_TABLE, EnchantingTableBlockEntity::tick) : null;
   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      if (!world.isClient) {
         player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
      }

      return ActionResult.SUCCESS;
   }

   @Nullable
   protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof EnchantingTableBlockEntity) {
         Text text = ((Nameable)blockEntity).getDisplayName();
         return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
            return new EnchantmentScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
         }, text);
      } else {
         return null;
      }
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      return false;
   }
}
