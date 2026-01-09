package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EightWayDirection;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.tick.Tick;
import org.slf4j.Logger;

public class UpgradeData {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final UpgradeData NO_UPGRADE_DATA;
   private static final String INDICES_KEY = "Indices";
   private static final EightWayDirection[] EIGHT_WAYS;
   private static final Codec BLOCK_TICKS_CODEC;
   private static final Codec FLUID_TICKS_CODEC;
   private final EnumSet sidesToUpgrade;
   private final List blockTicks;
   private final List fluidTicks;
   private final int[][] centerIndicesToUpgrade;
   static final Map BLOCK_TO_LOGIC;
   static final Set CALLBACK_LOGICS;

   private UpgradeData(HeightLimitView world) {
      this.sidesToUpgrade = EnumSet.noneOf(EightWayDirection.class);
      this.blockTicks = Lists.newArrayList();
      this.fluidTicks = Lists.newArrayList();
      this.centerIndicesToUpgrade = new int[world.countVerticalSections()][];
   }

   public UpgradeData(NbtCompound nbt, HeightLimitView world) {
      this(world);
      nbt.getCompound("Indices").ifPresent((indices) -> {
         for(int i = 0; i < this.centerIndicesToUpgrade.length; ++i) {
            this.centerIndicesToUpgrade[i] = (int[])indices.getIntArray(String.valueOf(i)).orElse((Object)null);
         }

      });
      int i = nbt.getInt("Sides", 0);
      EightWayDirection[] var4 = EightWayDirection.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         EightWayDirection eightWayDirection = var4[var6];
         if ((i & 1 << eightWayDirection.ordinal()) != 0) {
            this.sidesToUpgrade.add(eightWayDirection);
         }
      }

      Optional var10000 = nbt.get("neighbor_block_ticks", BLOCK_TICKS_CODEC);
      List var10001 = this.blockTicks;
      Objects.requireNonNull(var10001);
      var10000.ifPresent(var10001::addAll);
      var10000 = nbt.get("neighbor_fluid_ticks", FLUID_TICKS_CODEC);
      var10001 = this.fluidTicks;
      Objects.requireNonNull(var10001);
      var10000.ifPresent(var10001::addAll);
   }

   private UpgradeData(UpgradeData data) {
      this.sidesToUpgrade = EnumSet.noneOf(EightWayDirection.class);
      this.blockTicks = Lists.newArrayList();
      this.fluidTicks = Lists.newArrayList();
      this.sidesToUpgrade.addAll(data.sidesToUpgrade);
      this.blockTicks.addAll(data.blockTicks);
      this.fluidTicks.addAll(data.fluidTicks);
      this.centerIndicesToUpgrade = new int[data.centerIndicesToUpgrade.length][];

      for(int i = 0; i < data.centerIndicesToUpgrade.length; ++i) {
         int[] is = data.centerIndicesToUpgrade[i];
         this.centerIndicesToUpgrade[i] = is != null ? IntArrays.copy(is) : null;
      }

   }

   public void upgrade(WorldChunk chunk) {
      this.upgradeCenter(chunk);
      EightWayDirection[] var2 = EIGHT_WAYS;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EightWayDirection eightWayDirection = var2[var4];
         upgradeSide(chunk, eightWayDirection);
      }

      World world = chunk.getWorld();
      this.blockTicks.forEach((tick) -> {
         Block block = tick.type() == Blocks.AIR ? world.getBlockState(tick.pos()).getBlock() : (Block)tick.type();
         world.scheduleBlockTick(tick.pos(), block, tick.delay(), tick.priority());
      });
      this.fluidTicks.forEach((tick) -> {
         Fluid fluid = tick.type() == Fluids.EMPTY ? world.getFluidState(tick.pos()).getFluid() : (Fluid)tick.type();
         world.scheduleFluidTick(tick.pos(), fluid, tick.delay(), tick.priority());
      });
      CALLBACK_LOGICS.forEach((logic) -> {
         logic.postUpdate(world);
      });
   }

   private static void upgradeSide(WorldChunk chunk, EightWayDirection side) {
      World world = chunk.getWorld();
      if (chunk.getUpgradeData().sidesToUpgrade.remove(side)) {
         Set set = side.getDirections();
         int i = false;
         int j = true;
         boolean bl = set.contains(Direction.EAST);
         boolean bl2 = set.contains(Direction.WEST);
         boolean bl3 = set.contains(Direction.SOUTH);
         boolean bl4 = set.contains(Direction.NORTH);
         boolean bl5 = set.size() == 1;
         ChunkPos chunkPos = chunk.getPos();
         int k = chunkPos.getStartX() + (bl5 && (bl4 || bl3) ? 1 : (bl2 ? 0 : 15));
         int l = chunkPos.getStartX() + (bl5 && (bl4 || bl3) ? 14 : (bl2 ? 0 : 15));
         int m = chunkPos.getStartZ() + (!bl5 || !bl && !bl2 ? (bl4 ? 0 : 15) : 1);
         int n = chunkPos.getStartZ() + (bl5 && (bl || bl2) ? 14 : (bl4 ? 0 : 15));
         Direction[] directions = Direction.values();
         BlockPos.Mutable mutable = new BlockPos.Mutable();
         Iterator var18 = BlockPos.iterate(k, world.getBottomY(), m, l, world.getTopYInclusive(), n).iterator();

         while(var18.hasNext()) {
            BlockPos blockPos = (BlockPos)var18.next();
            BlockState blockState = world.getBlockState(blockPos);
            BlockState blockState2 = blockState;
            Direction[] var22 = directions;
            int var23 = directions.length;

            for(int var24 = 0; var24 < var23; ++var24) {
               Direction direction = var22[var24];
               mutable.set(blockPos, (Direction)direction);
               blockState2 = applyAdjacentBlock(blockState2, direction, world, blockPos, mutable);
            }

            Block.replace(blockState, blockState2, world, blockPos, 18);
         }

      }
   }

   private static BlockState applyAdjacentBlock(BlockState oldState, Direction dir, WorldAccess world, BlockPos currentPos, BlockPos otherPos) {
      return ((Logic)BLOCK_TO_LOGIC.getOrDefault(oldState.getBlock(), UpgradeData.BuiltinLogic.DEFAULT)).getUpdatedState(oldState, dir, world.getBlockState(otherPos), world, currentPos, otherPos);
   }

   private void upgradeCenter(WorldChunk chunk) {
      BlockPos.Mutable mutable = new BlockPos.Mutable();
      BlockPos.Mutable mutable2 = new BlockPos.Mutable();
      ChunkPos chunkPos = chunk.getPos();
      WorldAccess worldAccess = chunk.getWorld();

      int i;
      for(i = 0; i < this.centerIndicesToUpgrade.length; ++i) {
         ChunkSection chunkSection = chunk.getSection(i);
         int[] is = this.centerIndicesToUpgrade[i];
         this.centerIndicesToUpgrade[i] = null;
         if (is != null && is.length > 0) {
            Direction[] directions = Direction.values();
            PalettedContainer palettedContainer = chunkSection.getBlockStateContainer();
            int j = chunk.sectionIndexToCoord(i);
            int k = ChunkSectionPos.getBlockCoord(j);
            int[] var13 = is;
            int var14 = is.length;

            for(int var15 = 0; var15 < var14; ++var15) {
               int l = var13[var15];
               int m = l & 15;
               int n = l >> 8 & 15;
               int o = l >> 4 & 15;
               mutable.set(chunkPos.getStartX() + m, k + n, chunkPos.getStartZ() + o);
               BlockState blockState = (BlockState)palettedContainer.get(l);
               BlockState blockState2 = blockState;
               Direction[] var22 = directions;
               int var23 = directions.length;

               for(int var24 = 0; var24 < var23; ++var24) {
                  Direction direction = var22[var24];
                  mutable2.set(mutable, (Direction)direction);
                  if (ChunkSectionPos.getSectionCoord(mutable.getX()) == chunkPos.x && ChunkSectionPos.getSectionCoord(mutable.getZ()) == chunkPos.z) {
                     blockState2 = applyAdjacentBlock(blockState2, direction, worldAccess, mutable, mutable2);
                  }
               }

               Block.replace(blockState, blockState2, worldAccess, mutable, 18);
            }
         }
      }

      for(i = 0; i < this.centerIndicesToUpgrade.length; ++i) {
         if (this.centerIndicesToUpgrade[i] != null) {
            LOGGER.warn("Discarding update data for section {} for chunk ({} {})", new Object[]{worldAccess.sectionIndexToCoord(i), chunkPos.x, chunkPos.z});
         }

         this.centerIndicesToUpgrade[i] = null;
      }

   }

   public boolean isDone() {
      int[][] var1 = this.centerIndicesToUpgrade;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         int[] is = var1[var3];
         if (is != null) {
            return false;
         }
      }

      return this.sidesToUpgrade.isEmpty();
   }

   public NbtCompound toNbt() {
      NbtCompound nbtCompound = new NbtCompound();
      NbtCompound nbtCompound2 = new NbtCompound();

      int i;
      for(i = 0; i < this.centerIndicesToUpgrade.length; ++i) {
         String string = String.valueOf(i);
         if (this.centerIndicesToUpgrade[i] != null && this.centerIndicesToUpgrade[i].length != 0) {
            nbtCompound2.putIntArray(string, this.centerIndicesToUpgrade[i]);
         }
      }

      if (!nbtCompound2.isEmpty()) {
         nbtCompound.put("Indices", nbtCompound2);
      }

      i = 0;

      EightWayDirection eightWayDirection;
      for(Iterator var6 = this.sidesToUpgrade.iterator(); var6.hasNext(); i |= 1 << eightWayDirection.ordinal()) {
         eightWayDirection = (EightWayDirection)var6.next();
      }

      nbtCompound.putByte("Sides", (byte)i);
      if (!this.blockTicks.isEmpty()) {
         nbtCompound.put("neighbor_block_ticks", BLOCK_TICKS_CODEC, this.blockTicks);
      }

      if (!this.fluidTicks.isEmpty()) {
         nbtCompound.put("neighbor_fluid_ticks", FLUID_TICKS_CODEC, this.fluidTicks);
      }

      return nbtCompound;
   }

   public UpgradeData copy() {
      return this == NO_UPGRADE_DATA ? NO_UPGRADE_DATA : new UpgradeData(this);
   }

   static {
      NO_UPGRADE_DATA = new UpgradeData(EmptyBlockView.INSTANCE);
      EIGHT_WAYS = EightWayDirection.values();
      BLOCK_TICKS_CODEC = Tick.createCodec(Registries.BLOCK.getCodec().orElse(Blocks.AIR)).listOf();
      FLUID_TICKS_CODEC = Tick.createCodec(Registries.FLUID.getCodec().orElse(Fluids.EMPTY)).listOf();
      BLOCK_TO_LOGIC = new IdentityHashMap();
      CALLBACK_LOGICS = Sets.newHashSet();
   }

   static enum BuiltinLogic implements Logic {
      BLACKLIST(new Block[]{Blocks.OBSERVER, Blocks.NETHER_PORTAL, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL, Blocks.DRAGON_EGG, Blocks.GRAVEL, Blocks.SAND, Blocks.RED_SAND, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.CHERRY_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.PALE_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN, Blocks.PALE_OAK_WALL_SIGN, Blocks.OAK_HANGING_SIGN, Blocks.SPRUCE_HANGING_SIGN, Blocks.BIRCH_HANGING_SIGN, Blocks.ACACIA_HANGING_SIGN, Blocks.JUNGLE_HANGING_SIGN, Blocks.DARK_OAK_HANGING_SIGN, Blocks.PALE_OAK_HANGING_SIGN, Blocks.OAK_WALL_HANGING_SIGN, Blocks.SPRUCE_WALL_HANGING_SIGN, Blocks.BIRCH_WALL_HANGING_SIGN, Blocks.ACACIA_WALL_HANGING_SIGN, Blocks.JUNGLE_WALL_HANGING_SIGN, Blocks.DARK_OAK_WALL_HANGING_SIGN, Blocks.PALE_OAK_WALL_HANGING_SIGN}) {
         public BlockState getUpdatedState(BlockState oldState, Direction direction, BlockState otherState, WorldAccess world, BlockPos currentPos, BlockPos otherPos) {
            return oldState;
         }
      },
      DEFAULT(new Block[0]) {
         public BlockState getUpdatedState(BlockState oldState, Direction direction, BlockState otherState, WorldAccess world, BlockPos currentPos, BlockPos otherPos) {
            return oldState.getStateForNeighborUpdate(world, world, currentPos, direction, otherPos, world.getBlockState(otherPos), world.getRandom());
         }
      },
      CHEST(new Block[]{Blocks.CHEST, Blocks.TRAPPED_CHEST}) {
         public BlockState getUpdatedState(BlockState oldState, Direction direction, BlockState otherState, WorldAccess world, BlockPos currentPos, BlockPos otherPos) {
            if (otherState.isOf(oldState.getBlock()) && direction.getAxis().isHorizontal() && oldState.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE && otherState.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE) {
               Direction direction2 = (Direction)oldState.get(ChestBlock.FACING);
               if (direction.getAxis() != direction2.getAxis() && direction2 == otherState.get(ChestBlock.FACING)) {
                  ChestType chestType = direction == direction2.rotateYClockwise() ? ChestType.LEFT : ChestType.RIGHT;
                  world.setBlockState(otherPos, (BlockState)otherState.with(ChestBlock.CHEST_TYPE, chestType.getOpposite()), 18);
                  if (direction2 == Direction.NORTH || direction2 == Direction.EAST) {
                     BlockEntity blockEntity = world.getBlockEntity(currentPos);
                     BlockEntity blockEntity2 = world.getBlockEntity(otherPos);
                     if (blockEntity instanceof ChestBlockEntity && blockEntity2 instanceof ChestBlockEntity) {
                        ChestBlockEntity.copyInventory((ChestBlockEntity)blockEntity, (ChestBlockEntity)blockEntity2);
                     }
                  }

                  return (BlockState)oldState.with(ChestBlock.CHEST_TYPE, chestType);
               }
            }

            return oldState;
         }
      },
      LEAVES(true, new Block[]{Blocks.ACACIA_LEAVES, Blocks.CHERRY_LEAVES, Blocks.BIRCH_LEAVES, Blocks.PALE_OAK_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES}) {
         private final ThreadLocal distanceToPositions = ThreadLocal.withInitial(() -> {
            return Lists.newArrayListWithCapacity(7);
         });

         public BlockState getUpdatedState(BlockState oldState, Direction direction, BlockState otherState, WorldAccess world, BlockPos currentPos, BlockPos otherPos) {
            BlockState blockState = oldState.getStateForNeighborUpdate(world, world, currentPos, direction, otherPos, world.getBlockState(otherPos), world.getRandom());
            if (oldState != blockState) {
               int i = (Integer)blockState.get(Properties.DISTANCE_1_7);
               List list = (List)this.distanceToPositions.get();
               if (list.isEmpty()) {
                  for(int j = 0; j < 7; ++j) {
                     list.add(new ObjectOpenHashSet());
                  }
               }

               ((ObjectSet)list.get(i)).add(currentPos.toImmutable());
            }

            return oldState;
         }

         public void postUpdate(WorldAccess world) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            List list = (List)this.distanceToPositions.get();

            label44:
            for(int i = 2; i < list.size(); ++i) {
               int j = i - 1;
               ObjectSet objectSet = (ObjectSet)list.get(j);
               ObjectSet objectSet2 = (ObjectSet)list.get(i);
               ObjectIterator var8 = objectSet.iterator();

               while(true) {
                  BlockPos blockPos;
                  BlockState blockState;
                  do {
                     do {
                        if (!var8.hasNext()) {
                           continue label44;
                        }

                        blockPos = (BlockPos)var8.next();
                        blockState = world.getBlockState(blockPos);
                     } while((Integer)blockState.get(Properties.DISTANCE_1_7) < j);

                     world.setBlockState(blockPos, (BlockState)blockState.with(Properties.DISTANCE_1_7, j), 18);
                  } while(i == 7);

                  Direction[] var11 = DIRECTIONS;
                  int var12 = var11.length;

                  for(int var13 = 0; var13 < var12; ++var13) {
                     Direction direction = var11[var13];
                     mutable.set(blockPos, (Direction)direction);
                     BlockState blockState2 = world.getBlockState(mutable);
                     if (blockState2.contains(Properties.DISTANCE_1_7) && (Integer)blockState.get(Properties.DISTANCE_1_7) > i) {
                        objectSet2.add(mutable.toImmutable());
                     }
                  }
               }
            }

            list.clear();
         }
      },
      STEM_BLOCK(new Block[]{Blocks.MELON_STEM, Blocks.PUMPKIN_STEM}) {
         public BlockState getUpdatedState(BlockState oldState, Direction direction, BlockState otherState, WorldAccess world, BlockPos currentPos, BlockPos otherPos) {
            if ((Integer)oldState.get(StemBlock.AGE) == 7) {
               Block block = oldState.isOf(Blocks.PUMPKIN_STEM) ? Blocks.PUMPKIN : Blocks.MELON;
               if (otherState.isOf(block)) {
                  return (BlockState)(oldState.isOf(Blocks.PUMPKIN_STEM) ? Blocks.ATTACHED_PUMPKIN_STEM : Blocks.ATTACHED_MELON_STEM).getDefaultState().with(HorizontalFacingBlock.FACING, direction);
               }
            }

            return oldState;
         }
      };

      public static final Direction[] DIRECTIONS = Direction.values();

      BuiltinLogic(final Block... blocks) {
         this(false, blocks);
      }

      BuiltinLogic(final boolean addCallback, final Block... blocks) {
         Block[] var5 = blocks;
         int var6 = blocks.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Block block = var5[var7];
            UpgradeData.BLOCK_TO_LOGIC.put(block, this);
         }

         if (addCallback) {
            UpgradeData.CALLBACK_LOGICS.add(this);
         }

      }

      // $FF: synthetic method
      private static BuiltinLogic[] method_36743() {
         return new BuiltinLogic[]{BLACKLIST, DEFAULT, CHEST, LEAVES, STEM_BLOCK};
      }
   }

   public interface Logic {
      BlockState getUpdatedState(BlockState oldState, Direction direction, BlockState otherState, WorldAccess world, BlockPos currentPos, BlockPos otherPos);

      default void postUpdate(WorldAccess world) {
      }
   }
}
