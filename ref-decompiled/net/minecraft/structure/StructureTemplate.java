package net.minecraft.structure;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class StructureTemplate {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String PALETTE_KEY = "palette";
   public static final String PALETTES_KEY = "palettes";
   public static final String ENTITIES_KEY = "entities";
   public static final String BLOCKS_KEY = "blocks";
   public static final String BLOCKS_POS_KEY = "pos";
   public static final String BLOCKS_STATE_KEY = "state";
   public static final String BLOCKS_NBT_KEY = "nbt";
   public static final String ENTITIES_POS_KEY = "pos";
   public static final String ENTITIES_BLOCK_POS_KEY = "blockPos";
   public static final String ENTITIES_NBT_KEY = "nbt";
   public static final String SIZE_KEY = "size";
   private final List blockInfoLists = Lists.newArrayList();
   private final List entities = Lists.newArrayList();
   private Vec3i size;
   private String author;

   public StructureTemplate() {
      this.size = Vec3i.ZERO;
      this.author = "?";
   }

   public Vec3i getSize() {
      return this.size;
   }

   public void setAuthor(String author) {
      this.author = author;
   }

   public String getAuthor() {
      return this.author;
   }

   public void saveFromWorld(World world, BlockPos start, Vec3i dimensions, boolean includeEntities, List ignoredBlocks) {
      if (dimensions.getX() >= 1 && dimensions.getY() >= 1 && dimensions.getZ() >= 1) {
         BlockPos blockPos = start.add(dimensions).add(-1, -1, -1);
         List list = Lists.newArrayList();
         List list2 = Lists.newArrayList();
         List list3 = Lists.newArrayList();
         BlockPos blockPos2 = new BlockPos(Math.min(start.getX(), blockPos.getX()), Math.min(start.getY(), blockPos.getY()), Math.min(start.getZ(), blockPos.getZ()));
         BlockPos blockPos3 = new BlockPos(Math.max(start.getX(), blockPos.getX()), Math.max(start.getY(), blockPos.getY()), Math.max(start.getZ(), blockPos.getZ()));
         this.size = dimensions;
         ErrorReporter.Logging logging = new ErrorReporter.Logging(LOGGER);

         try {
            Iterator var13 = BlockPos.iterate(blockPos2, blockPos3).iterator();

            while(var13.hasNext()) {
               BlockPos blockPos4 = (BlockPos)var13.next();
               BlockPos blockPos5 = blockPos4.subtract(blockPos2);
               BlockState blockState = world.getBlockState(blockPos4);
               Stream var10000 = ignoredBlocks.stream();
               Objects.requireNonNull(blockState);
               if (!var10000.anyMatch(blockState::isOf)) {
                  BlockEntity blockEntity = world.getBlockEntity(blockPos4);
                  StructureBlockInfo structureBlockInfo;
                  if (blockEntity != null) {
                     NbtWriteView nbtWriteView = NbtWriteView.create(logging, world.getRegistryManager());
                     blockEntity.writeDataWithId(nbtWriteView);
                     structureBlockInfo = new StructureBlockInfo(blockPos5, blockState, nbtWriteView.getNbt());
                  } else {
                     structureBlockInfo = new StructureBlockInfo(blockPos5, blockState, (NbtCompound)null);
                  }

                  categorize(structureBlockInfo, list, list2, list3);
               }
            }

            List list4 = combineSorted(list, list2, list3);
            this.blockInfoLists.clear();
            this.blockInfoLists.add(new PalettedBlockInfoList(list4));
            if (includeEntities) {
               this.addEntitiesFromWorld(world, blockPos2, blockPos3, logging);
            } else {
               this.entities.clear();
            }
         } catch (Throwable var21) {
            try {
               logging.close();
            } catch (Throwable var20) {
               var21.addSuppressed(var20);
            }

            throw var21;
         }

         logging.close();
      }
   }

   private static void categorize(StructureBlockInfo blockInfo, List fullBlocks, List blocksWithNbt, List otherBlocks) {
      if (blockInfo.nbt != null) {
         blocksWithNbt.add(blockInfo);
      } else if (!blockInfo.state.getBlock().hasDynamicBounds() && blockInfo.state.isFullCube(EmptyBlockView.INSTANCE, BlockPos.ORIGIN)) {
         fullBlocks.add(blockInfo);
      } else {
         otherBlocks.add(blockInfo);
      }

   }

   private static List combineSorted(List fullBlocks, List blocksWithNbt, List otherBlocks) {
      Comparator comparator = Comparator.comparingInt((blockInfo) -> {
         return blockInfo.pos.getY();
      }).thenComparingInt((blockInfo) -> {
         return blockInfo.pos.getX();
      }).thenComparingInt((blockInfo) -> {
         return blockInfo.pos.getZ();
      });
      fullBlocks.sort(comparator);
      otherBlocks.sort(comparator);
      blocksWithNbt.sort(comparator);
      List list = Lists.newArrayList();
      list.addAll(fullBlocks);
      list.addAll(otherBlocks);
      list.addAll(blocksWithNbt);
      return list;
   }

   private void addEntitiesFromWorld(World world, BlockPos firstCorner, BlockPos secondCorner, ErrorReporter errorReporter) {
      List list = world.getEntitiesByClass(Entity.class, Box.enclosing(firstCorner, secondCorner), (entityx) -> {
         return !(entityx instanceof PlayerEntity);
      });
      this.entities.clear();

      Vec3d vec3d;
      NbtWriteView nbtWriteView;
      BlockPos blockPos;
      for(Iterator var6 = list.iterator(); var6.hasNext(); this.entities.add(new StructureEntityInfo(vec3d, blockPos, nbtWriteView.getNbt().copy()))) {
         Entity entity = (Entity)var6.next();
         vec3d = new Vec3d(entity.getX() - (double)firstCorner.getX(), entity.getY() - (double)firstCorner.getY(), entity.getZ() - (double)firstCorner.getZ());
         nbtWriteView = NbtWriteView.create(errorReporter.makeChild(entity.getErrorReporterContext()), entity.getRegistryManager());
         entity.saveData(nbtWriteView);
         if (entity instanceof PaintingEntity paintingEntity) {
            blockPos = paintingEntity.getAttachedBlockPos().subtract(firstCorner);
         } else {
            blockPos = BlockPos.ofFloored(vec3d);
         }
      }

   }

   public List getInfosForBlock(BlockPos pos, StructurePlacementData placementData, Block block) {
      return this.getInfosForBlock(pos, placementData, block, true);
   }

   public List getJigsawInfos(BlockPos pos, BlockRotation rotation) {
      if (this.blockInfoLists.isEmpty()) {
         return new ArrayList();
      } else {
         StructurePlacementData structurePlacementData = (new StructurePlacementData()).setRotation(rotation);
         List list = structurePlacementData.getRandomBlockInfos(this.blockInfoLists, pos).getOrCreateJigsawBlockInfos();
         List list2 = new ArrayList(list.size());
         Iterator var6 = list.iterator();

         while(var6.hasNext()) {
            JigsawBlockInfo jigsawBlockInfo = (JigsawBlockInfo)var6.next();
            StructureBlockInfo structureBlockInfo = jigsawBlockInfo.info;
            list2.add(jigsawBlockInfo.withInfo(new StructureBlockInfo(transform(structurePlacementData, structureBlockInfo.pos()).add(pos), structureBlockInfo.state.rotate(structurePlacementData.getRotation()), structureBlockInfo.nbt)));
         }

         return list2;
      }
   }

   public ObjectArrayList getInfosForBlock(BlockPos pos, StructurePlacementData placementData, Block block, boolean transformed) {
      ObjectArrayList objectArrayList = new ObjectArrayList();
      BlockBox blockBox = placementData.getBoundingBox();
      if (this.blockInfoLists.isEmpty()) {
         return objectArrayList;
      } else {
         Iterator var7 = placementData.getRandomBlockInfos(this.blockInfoLists, pos).getAllOf(block).iterator();

         while(true) {
            StructureBlockInfo structureBlockInfo;
            BlockPos blockPos;
            do {
               if (!var7.hasNext()) {
                  return objectArrayList;
               }

               structureBlockInfo = (StructureBlockInfo)var7.next();
               blockPos = transformed ? transform(placementData, structureBlockInfo.pos).add(pos) : structureBlockInfo.pos;
            } while(blockBox != null && !blockBox.contains(blockPos));

            objectArrayList.add(new StructureBlockInfo(blockPos, structureBlockInfo.state.rotate(placementData.getRotation()), structureBlockInfo.nbt));
         }
      }
   }

   public BlockPos transformBox(StructurePlacementData placementData1, BlockPos pos1, StructurePlacementData placementData2, BlockPos pos2) {
      BlockPos blockPos = transform(placementData1, pos1);
      BlockPos blockPos2 = transform(placementData2, pos2);
      return blockPos.subtract(blockPos2);
   }

   public static BlockPos transform(StructurePlacementData placementData, BlockPos pos) {
      return transformAround(pos, placementData.getMirror(), placementData.getRotation(), placementData.getPosition());
   }

   public boolean place(ServerWorldAccess world, BlockPos pos, BlockPos pivot, StructurePlacementData placementData, Random random, int flags) {
      if (this.blockInfoLists.isEmpty()) {
         return false;
      } else {
         List list = placementData.getRandomBlockInfos(this.blockInfoLists, pos).getAll();
         if ((!list.isEmpty() || !placementData.shouldIgnoreEntities() && !this.entities.isEmpty()) && this.size.getX() >= 1 && this.size.getY() >= 1 && this.size.getZ() >= 1) {
            BlockBox blockBox = placementData.getBoundingBox();
            List list2 = Lists.newArrayListWithCapacity(placementData.shouldApplyWaterlogging() ? list.size() : 0);
            List list3 = Lists.newArrayListWithCapacity(placementData.shouldApplyWaterlogging() ? list.size() : 0);
            List list4 = Lists.newArrayListWithCapacity(list.size());
            int i = Integer.MAX_VALUE;
            int j = Integer.MAX_VALUE;
            int k = Integer.MAX_VALUE;
            int l = Integer.MIN_VALUE;
            int m = Integer.MIN_VALUE;
            int n = Integer.MIN_VALUE;
            List list5 = process(world, pos, pivot, placementData, list);
            ErrorReporter.Logging logging = new ErrorReporter.Logging(LOGGER);

            try {
               Iterator var20 = list5.iterator();

               label166:
               while(true) {
                  BlockEntity blockEntity;
                  if (!var20.hasNext()) {
                     boolean bl = true;
                     Direction[] directions = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

                     label158:
                     while(true) {
                        Iterator iterator;
                        int o;
                        BlockState blockState2;
                        if (bl && !list2.isEmpty()) {
                           bl = false;
                           iterator = list2.iterator();

                           while(true) {
                              if (!iterator.hasNext()) {
                                 continue label158;
                              }

                              BlockPos blockPos2 = (BlockPos)iterator.next();
                              FluidState fluidState2 = world.getFluidState(blockPos2);

                              for(o = 0; o < directions.length && !fluidState2.isStill(); ++o) {
                                 BlockPos blockPos3 = blockPos2.offset(directions[o]);
                                 FluidState fluidState3 = world.getFluidState(blockPos3);
                                 if (fluidState3.isStill() && !list3.contains(blockPos3)) {
                                    fluidState2 = fluidState3;
                                 }
                              }

                              if (fluidState2.isStill()) {
                                 blockState2 = world.getBlockState(blockPos2);
                                 Block block = blockState2.getBlock();
                                 if (block instanceof FluidFillable) {
                                    ((FluidFillable)block).tryFillWithFluid(world, blockPos2, blockState2, fluidState2);
                                    bl = true;
                                    iterator.remove();
                                 }
                              }
                           }
                        }

                        if (i <= l) {
                           if (!placementData.shouldUpdateNeighbors()) {
                              VoxelSet voxelSet = new BitSetVoxelSet(l - i + 1, m - j + 1, n - k + 1);
                              int p = i;
                              int q = j;
                              o = k;
                              Iterator var26 = list4.iterator();

                              while(var26.hasNext()) {
                                 Pair pair = (Pair)var26.next();
                                 BlockPos blockPos4 = (BlockPos)pair.getFirst();
                                 voxelSet.set(blockPos4.getX() - p, blockPos4.getY() - q, blockPos4.getZ() - o);
                              }

                              updateCorner(world, flags, voxelSet, p, q, o);
                           }

                           iterator = list4.iterator();

                           while(iterator.hasNext()) {
                              Pair pair2 = (Pair)iterator.next();
                              BlockPos blockPos5 = (BlockPos)pair2.getFirst();
                              if (!placementData.shouldUpdateNeighbors()) {
                                 blockState2 = world.getBlockState(blockPos5);
                                 BlockState blockState3 = Block.postProcessState(blockState2, world, blockPos5);
                                 if (blockState2 != blockState3) {
                                    world.setBlockState(blockPos5, blockState3, flags & -2 | 16);
                                 }

                                 world.updateNeighbors(blockPos5, blockState3.getBlock());
                              }

                              if (pair2.getSecond() != null) {
                                 blockEntity = world.getBlockEntity(blockPos5);
                                 if (blockEntity != null) {
                                    blockEntity.markDirty();
                                 }
                              }
                           }
                        }

                        if (!placementData.shouldIgnoreEntities()) {
                           this.spawnEntities(world, pos, placementData.getMirror(), placementData.getRotation(), placementData.getPosition(), blockBox, placementData.shouldInitializeMobs(), logging);
                        }
                        break label166;
                     }
                  }

                  StructureBlockInfo structureBlockInfo = (StructureBlockInfo)var20.next();
                  BlockPos blockPos = structureBlockInfo.pos;
                  if (blockBox == null || blockBox.contains(blockPos)) {
                     FluidState fluidState = placementData.shouldApplyWaterlogging() ? world.getFluidState(blockPos) : null;
                     BlockState blockState = structureBlockInfo.state.mirror(placementData.getMirror()).rotate(placementData.getRotation());
                     if (structureBlockInfo.nbt != null) {
                        world.setBlockState(blockPos, Blocks.BARRIER.getDefaultState(), 820);
                     }

                     if (world.setBlockState(blockPos, blockState, flags)) {
                        i = Math.min(i, blockPos.getX());
                        j = Math.min(j, blockPos.getY());
                        k = Math.min(k, blockPos.getZ());
                        l = Math.max(l, blockPos.getX());
                        m = Math.max(m, blockPos.getY());
                        n = Math.max(n, blockPos.getZ());
                        list4.add(Pair.of(blockPos, structureBlockInfo.nbt));
                        if (structureBlockInfo.nbt != null) {
                           blockEntity = world.getBlockEntity(blockPos);
                           if (blockEntity != null) {
                              if (blockEntity instanceof LootableInventory) {
                                 structureBlockInfo.nbt.putLong("LootTableSeed", random.nextLong());
                              }

                              blockEntity.read(NbtReadView.create(logging.makeChild(blockEntity.getReporterContext()), world.getRegistryManager(), structureBlockInfo.nbt));
                           }
                        }

                        if (fluidState != null) {
                           if (blockState.getFluidState().isStill()) {
                              list3.add(blockPos);
                           } else if (blockState.getBlock() instanceof FluidFillable) {
                              ((FluidFillable)blockState.getBlock()).tryFillWithFluid(world, blockPos, blockState, fluidState);
                              if (!fluidState.isStill()) {
                                 list2.add(blockPos);
                              }
                           }
                        }
                     }
                  }
               }
            } catch (Throwable var30) {
               try {
                  logging.close();
               } catch (Throwable var29) {
                  var30.addSuppressed(var29);
               }

               throw var30;
            }

            logging.close();
            return true;
         } else {
            return false;
         }
      }
   }

   public static void updateCorner(WorldAccess world, int flags, VoxelSet set, BlockPos startPos) {
      updateCorner(world, flags, set, startPos.getX(), startPos.getY(), startPos.getZ());
   }

   public static void updateCorner(WorldAccess world, int flags, VoxelSet set, int startX, int startY, int startZ) {
      BlockPos.Mutable mutable = new BlockPos.Mutable();
      BlockPos.Mutable mutable2 = new BlockPos.Mutable();
      set.forEachDirection((direction, x, y, z) -> {
         mutable.set(startX + x, startY + y, startZ + z);
         mutable2.set(mutable, (Direction)direction);
         BlockState blockState = world.getBlockState(mutable);
         BlockState blockState2 = world.getBlockState(mutable2);
         BlockState blockState3 = blockState.getStateForNeighborUpdate(world, world, mutable, direction, mutable2, blockState2, world.getRandom());
         if (blockState != blockState3) {
            world.setBlockState(mutable, blockState3, flags & -2);
         }

         BlockState blockState4 = blockState2.getStateForNeighborUpdate(world, world, mutable2, direction.getOpposite(), mutable, blockState3, world.getRandom());
         if (blockState2 != blockState4) {
            world.setBlockState(mutable2, blockState4, flags & -2);
         }

      });
   }

   public static List process(ServerWorldAccess world, BlockPos pos, BlockPos pivot, StructurePlacementData placementData, List infos) {
      List list = new ArrayList();
      List list2 = new ArrayList();
      Iterator var7 = infos.iterator();

      while(var7.hasNext()) {
         StructureBlockInfo structureBlockInfo = (StructureBlockInfo)var7.next();
         BlockPos blockPos = transform(placementData, structureBlockInfo.pos).add(pos);
         StructureBlockInfo structureBlockInfo2 = new StructureBlockInfo(blockPos, structureBlockInfo.state, structureBlockInfo.nbt != null ? structureBlockInfo.nbt.copy() : null);

         for(Iterator iterator = placementData.getProcessors().iterator(); structureBlockInfo2 != null && iterator.hasNext(); structureBlockInfo2 = ((StructureProcessor)iterator.next()).process(world, pos, pivot, structureBlockInfo, structureBlockInfo2, placementData)) {
         }

         if (structureBlockInfo2 != null) {
            ((List)list2).add(structureBlockInfo2);
            list.add(structureBlockInfo);
         }
      }

      StructureProcessor structureProcessor;
      for(var7 = placementData.getProcessors().iterator(); var7.hasNext(); list2 = structureProcessor.reprocess(world, pos, pivot, list, (List)list2, placementData)) {
         structureProcessor = (StructureProcessor)var7.next();
      }

      return (List)list2;
   }

   private void spawnEntities(ServerWorldAccess world, BlockPos pos, BlockMirror mirror, BlockRotation rotation, BlockPos pivot, @Nullable BlockBox area, boolean initializeMobs, ErrorReporter errorReporter) {
      Iterator var9 = this.entities.iterator();

      while(true) {
         StructureEntityInfo structureEntityInfo;
         BlockPos blockPos;
         do {
            if (!var9.hasNext()) {
               return;
            }

            structureEntityInfo = (StructureEntityInfo)var9.next();
            blockPos = transformAround(structureEntityInfo.blockPos, mirror, rotation, pivot).add(pos);
         } while(area != null && !area.contains(blockPos));

         NbtCompound nbtCompound = structureEntityInfo.nbt.copy();
         Vec3d vec3d = transformAround(structureEntityInfo.pos, mirror, rotation, pivot);
         Vec3d vec3d2 = vec3d.add((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
         NbtList nbtList = new NbtList();
         nbtList.add(NbtDouble.of(vec3d2.x));
         nbtList.add(NbtDouble.of(vec3d2.y));
         nbtList.add(NbtDouble.of(vec3d2.z));
         nbtCompound.put("Pos", nbtList);
         nbtCompound.remove("UUID");
         getEntity(errorReporter, world, nbtCompound).ifPresent((entity) -> {
            float f = entity.applyRotation(rotation);
            f += entity.applyMirror(mirror) - entity.getYaw();
            entity.refreshPositionAndAngles(vec3d2.x, vec3d2.y, vec3d2.z, f, entity.getPitch());
            if (initializeMobs && entity instanceof MobEntity) {
               ((MobEntity)entity).initialize(world, world.getLocalDifficulty(BlockPos.ofFloored(vec3d2)), SpawnReason.STRUCTURE, (EntityData)null);
            }

            world.spawnEntityAndPassengers(entity);
         });
      }
   }

   private static Optional getEntity(ErrorReporter errorReporter, ServerWorldAccess world, NbtCompound nbt) {
      try {
         return EntityType.getEntityFromData(NbtReadView.create(errorReporter, world.getRegistryManager(), nbt), world.toServerWorld(), SpawnReason.STRUCTURE);
      } catch (Exception var4) {
         return Optional.empty();
      }
   }

   public Vec3i getRotatedSize(BlockRotation rotation) {
      switch (rotation) {
         case COUNTERCLOCKWISE_90:
         case CLOCKWISE_90:
            return new Vec3i(this.size.getZ(), this.size.getY(), this.size.getX());
         default:
            return this.size;
      }
   }

   public static BlockPos transformAround(BlockPos pos, BlockMirror mirror, BlockRotation rotation, BlockPos pivot) {
      int i = pos.getX();
      int j = pos.getY();
      int k = pos.getZ();
      boolean bl = true;
      switch (mirror) {
         case LEFT_RIGHT:
            k = -k;
            break;
         case FRONT_BACK:
            i = -i;
            break;
         default:
            bl = false;
      }

      int l = pivot.getX();
      int m = pivot.getZ();
      switch (rotation) {
         case COUNTERCLOCKWISE_90:
            return new BlockPos(l - m + k, j, l + m - i);
         case CLOCKWISE_90:
            return new BlockPos(l + m - k, j, m - l + i);
         case CLOCKWISE_180:
            return new BlockPos(l + l - i, j, m + m - k);
         default:
            return bl ? new BlockPos(i, j, k) : pos;
      }
   }

   public static Vec3d transformAround(Vec3d point, BlockMirror mirror, BlockRotation rotation, BlockPos pivot) {
      double d = point.x;
      double e = point.y;
      double f = point.z;
      boolean bl = true;
      switch (mirror) {
         case LEFT_RIGHT:
            f = 1.0 - f;
            break;
         case FRONT_BACK:
            d = 1.0 - d;
            break;
         default:
            bl = false;
      }

      int i = pivot.getX();
      int j = pivot.getZ();
      switch (rotation) {
         case COUNTERCLOCKWISE_90:
            return new Vec3d((double)(i - j) + f, e, (double)(i + j + 1) - d);
         case CLOCKWISE_90:
            return new Vec3d((double)(i + j + 1) - f, e, (double)(j - i) + d);
         case CLOCKWISE_180:
            return new Vec3d((double)(i + i + 1) - d, e, (double)(j + j + 1) - f);
         default:
            return bl ? new Vec3d(d, e, f) : point;
      }
   }

   public BlockPos offsetByTransformedSize(BlockPos pos, BlockMirror mirror, BlockRotation rotation) {
      return applyTransformedOffset(pos, mirror, rotation, this.getSize().getX(), this.getSize().getZ());
   }

   public static BlockPos applyTransformedOffset(BlockPos pos, BlockMirror mirror, BlockRotation rotation, int offsetX, int offsetZ) {
      --offsetX;
      --offsetZ;
      int i = mirror == BlockMirror.FRONT_BACK ? offsetX : 0;
      int j = mirror == BlockMirror.LEFT_RIGHT ? offsetZ : 0;
      BlockPos blockPos = pos;
      switch (rotation) {
         case COUNTERCLOCKWISE_90:
            blockPos = pos.add(j, 0, offsetX - i);
            break;
         case CLOCKWISE_90:
            blockPos = pos.add(offsetZ - j, 0, i);
            break;
         case CLOCKWISE_180:
            blockPos = pos.add(offsetX - i, 0, offsetZ - j);
            break;
         case NONE:
            blockPos = pos.add(i, 0, j);
      }

      return blockPos;
   }

   public BlockBox calculateBoundingBox(StructurePlacementData placementData, BlockPos pos) {
      return this.calculateBoundingBox(pos, placementData.getRotation(), placementData.getPosition(), placementData.getMirror());
   }

   public BlockBox calculateBoundingBox(BlockPos pos, BlockRotation rotation, BlockPos pivot, BlockMirror mirror) {
      return createBox(pos, rotation, pivot, mirror, this.size);
   }

   @VisibleForTesting
   protected static BlockBox createBox(BlockPos pos, BlockRotation rotation, BlockPos pivot, BlockMirror mirror, Vec3i dimensions) {
      Vec3i vec3i = dimensions.add(-1, -1, -1);
      BlockPos blockPos = transformAround(BlockPos.ORIGIN, mirror, rotation, pivot);
      BlockPos blockPos2 = transformAround(BlockPos.ORIGIN.add(vec3i), mirror, rotation, pivot);
      return BlockBox.create(blockPos, blockPos2).move(pos);
   }

   public NbtCompound writeNbt(NbtCompound nbt) {
      if (this.blockInfoLists.isEmpty()) {
         nbt.put("blocks", new NbtList());
         nbt.put("palette", new NbtList());
      } else {
         List list = Lists.newArrayList();
         Palette palette = new Palette();
         list.add(palette);

         for(int i = 1; i < this.blockInfoLists.size(); ++i) {
            list.add(new Palette());
         }

         NbtList nbtList = new NbtList();
         List list2 = ((PalettedBlockInfoList)this.blockInfoLists.get(0)).getAll();

         for(int j = 0; j < list2.size(); ++j) {
            StructureBlockInfo structureBlockInfo = (StructureBlockInfo)list2.get(j);
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.put("pos", this.createNbtIntList(structureBlockInfo.pos.getX(), structureBlockInfo.pos.getY(), structureBlockInfo.pos.getZ()));
            int k = palette.getId(structureBlockInfo.state);
            nbtCompound.putInt("state", k);
            if (structureBlockInfo.nbt != null) {
               nbtCompound.put("nbt", structureBlockInfo.nbt);
            }

            nbtList.add(nbtCompound);

            for(int l = 1; l < this.blockInfoLists.size(); ++l) {
               Palette palette2 = (Palette)list.get(l);
               palette2.set(((StructureBlockInfo)((PalettedBlockInfoList)this.blockInfoLists.get(l)).getAll().get(j)).state, k);
            }
         }

         nbt.put("blocks", nbtList);
         NbtList nbtList2;
         Iterator var18;
         if (list.size() == 1) {
            nbtList2 = new NbtList();
            var18 = palette.iterator();

            while(var18.hasNext()) {
               BlockState blockState = (BlockState)var18.next();
               nbtList2.add(NbtHelper.fromBlockState(blockState));
            }

            nbt.put("palette", nbtList2);
         } else {
            nbtList2 = new NbtList();
            var18 = list.iterator();

            while(var18.hasNext()) {
               Palette palette3 = (Palette)var18.next();
               NbtList nbtList3 = new NbtList();
               Iterator var22 = palette3.iterator();

               while(var22.hasNext()) {
                  BlockState blockState2 = (BlockState)var22.next();
                  nbtList3.add(NbtHelper.fromBlockState(blockState2));
               }

               nbtList2.add(nbtList3);
            }

            nbt.put("palettes", nbtList2);
         }
      }

      NbtList nbtList4 = new NbtList();

      NbtCompound nbtCompound2;
      for(Iterator var13 = this.entities.iterator(); var13.hasNext(); nbtList4.add(nbtCompound2)) {
         StructureEntityInfo structureEntityInfo = (StructureEntityInfo)var13.next();
         nbtCompound2 = new NbtCompound();
         nbtCompound2.put("pos", this.createNbtDoubleList(structureEntityInfo.pos.x, structureEntityInfo.pos.y, structureEntityInfo.pos.z));
         nbtCompound2.put("blockPos", this.createNbtIntList(structureEntityInfo.blockPos.getX(), structureEntityInfo.blockPos.getY(), structureEntityInfo.blockPos.getZ()));
         if (structureEntityInfo.nbt != null) {
            nbtCompound2.put("nbt", structureEntityInfo.nbt);
         }
      }

      nbt.put("entities", nbtList4);
      nbt.put("size", this.createNbtIntList(this.size.getX(), this.size.getY(), this.size.getZ()));
      return NbtHelper.putDataVersion(nbt);
   }

   public void readNbt(RegistryEntryLookup blockLookup, NbtCompound nbt) {
      this.blockInfoLists.clear();
      this.entities.clear();
      NbtList nbtList = nbt.getListOrEmpty("size");
      this.size = new Vec3i(nbtList.getInt(0, 0), nbtList.getInt(1, 0), nbtList.getInt(2, 0));
      NbtList nbtList2 = nbt.getListOrEmpty("blocks");
      Optional optional = nbt.getList("palettes");
      if (optional.isPresent()) {
         for(int i = 0; i < ((NbtList)optional.get()).size(); ++i) {
            this.loadPalettedBlockInfo(blockLookup, ((NbtList)optional.get()).getListOrEmpty(i), nbtList2);
         }
      } else {
         this.loadPalettedBlockInfo(blockLookup, nbt.getListOrEmpty("palette"), nbtList2);
      }

      nbt.getListOrEmpty("entities").streamCompounds().forEach((nbtx) -> {
         NbtList nbtList = nbtx.getListOrEmpty("pos");
         Vec3d vec3d = new Vec3d(nbtList.getDouble(0, 0.0), nbtList.getDouble(1, 0.0), nbtList.getDouble(2, 0.0));
         NbtList nbtList2 = nbtx.getListOrEmpty("blockPos");
         BlockPos blockPos = new BlockPos(nbtList2.getInt(0, 0), nbtList2.getInt(1, 0), nbtList2.getInt(2, 0));
         nbtx.getCompound("nbt").ifPresent((blockEntityNbt) -> {
            this.entities.add(new StructureEntityInfo(vec3d, blockPos, blockEntityNbt));
         });
      });
   }

   private void loadPalettedBlockInfo(RegistryEntryLookup blockLookup, NbtList palette, NbtList blocks) {
      Palette palette2 = new Palette();

      for(int i = 0; i < palette.size(); ++i) {
         palette2.set(NbtHelper.toBlockState(blockLookup, palette.getCompoundOrEmpty(i)), i);
      }

      List list = Lists.newArrayList();
      List list2 = Lists.newArrayList();
      List list3 = Lists.newArrayList();
      blocks.streamCompounds().forEach((nbt) -> {
         NbtList nbtList = nbt.getListOrEmpty("pos");
         BlockPos blockPos = new BlockPos(nbtList.getInt(0, 0), nbtList.getInt(1, 0), nbtList.getInt(2, 0));
         BlockState blockState = palette2.getState(nbt.getInt("state", 0));
         NbtCompound nbtCompound = (NbtCompound)nbt.getCompound("nbt").orElse((Object)null);
         StructureBlockInfo structureBlockInfo = new StructureBlockInfo(blockPos, blockState, nbtCompound);
         categorize(structureBlockInfo, list, list2, list3);
      });
      List list4 = combineSorted(list, list2, list3);
      this.blockInfoLists.add(new PalettedBlockInfoList(list4));
   }

   private NbtList createNbtIntList(int... ints) {
      NbtList nbtList = new NbtList();
      int[] var3 = ints;
      int var4 = ints.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         int i = var3[var5];
         nbtList.add(NbtInt.of(i));
      }

      return nbtList;
   }

   private NbtList createNbtDoubleList(double... doubles) {
      NbtList nbtList = new NbtList();
      double[] var3 = doubles;
      int var4 = doubles.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         double d = var3[var5];
         nbtList.add(NbtDouble.of(d));
      }

      return nbtList;
   }

   public static JigsawBlockEntity.Joint readJoint(NbtCompound nbt, BlockState state) {
      return (JigsawBlockEntity.Joint)nbt.get("joint", JigsawBlockEntity.Joint.CODEC).orElseGet(() -> {
         return getJointFromFacing(state);
      });
   }

   public static JigsawBlockEntity.Joint getJointFromFacing(BlockState state) {
      return JigsawBlock.getFacing(state).getAxis().isHorizontal() ? JigsawBlockEntity.Joint.ALIGNED : JigsawBlockEntity.Joint.ROLLABLE;
   }

   public static record StructureBlockInfo(BlockPos pos, BlockState state, @Nullable NbtCompound nbt) {
      final BlockPos pos;
      final BlockState state;
      @Nullable
      final NbtCompound nbt;

      public StructureBlockInfo(BlockPos pos, BlockState state, @Nullable NbtCompound nbt) {
         this.pos = pos;
         this.state = state;
         this.nbt = nbt;
      }

      public String toString() {
         return String.format(Locale.ROOT, "<StructureBlockInfo | %s | %s | %s>", this.pos, this.state, this.nbt);
      }

      public BlockPos pos() {
         return this.pos;
      }

      public BlockState state() {
         return this.state;
      }

      @Nullable
      public NbtCompound nbt() {
         return this.nbt;
      }
   }

   public static final class PalettedBlockInfoList {
      private final List infos;
      private final Map blockToInfos = Maps.newHashMap();
      @Nullable
      private List jigsawBlockInfos;

      PalettedBlockInfoList(List infos) {
         this.infos = infos;
      }

      public List getOrCreateJigsawBlockInfos() {
         if (this.jigsawBlockInfos == null) {
            this.jigsawBlockInfos = this.getAllOf(Blocks.JIGSAW).stream().map(JigsawBlockInfo::of).toList();
         }

         return this.jigsawBlockInfos;
      }

      public List getAll() {
         return this.infos;
      }

      public List getAllOf(Block block) {
         return (List)this.blockToInfos.computeIfAbsent(block, (block2) -> {
            return (List)this.infos.stream().filter((info) -> {
               return info.state.isOf(block2);
            }).collect(Collectors.toList());
         });
      }
   }

   public static class StructureEntityInfo {
      public final Vec3d pos;
      public final BlockPos blockPos;
      public final NbtCompound nbt;

      public StructureEntityInfo(Vec3d pos, BlockPos blockPos, NbtCompound nbt) {
         this.pos = pos;
         this.blockPos = blockPos;
         this.nbt = nbt;
      }
   }

   public static record JigsawBlockInfo(StructureBlockInfo info, JigsawBlockEntity.Joint jointType, Identifier name, RegistryKey pool, Identifier target, int placementPriority, int selectionPriority) {
      final StructureBlockInfo info;

      public JigsawBlockInfo(StructureBlockInfo structureBlockInfo, JigsawBlockEntity.Joint joint, Identifier identifier, RegistryKey registryKey, Identifier identifier2, int i, int j) {
         this.info = structureBlockInfo;
         this.jointType = joint;
         this.name = identifier;
         this.pool = registryKey;
         this.target = identifier2;
         this.placementPriority = i;
         this.selectionPriority = j;
      }

      public static JigsawBlockInfo of(StructureBlockInfo structureBlockInfo) {
         NbtCompound nbtCompound = (NbtCompound)Objects.requireNonNull(structureBlockInfo.nbt(), () -> {
            return String.valueOf(structureBlockInfo) + " nbt was null";
         });
         return new JigsawBlockInfo(structureBlockInfo, StructureTemplate.readJoint(nbtCompound, structureBlockInfo.state()), (Identifier)nbtCompound.get("name", Identifier.CODEC).orElse(JigsawBlockEntity.DEFAULT_NAME), (RegistryKey)nbtCompound.get("pool", JigsawBlockEntity.STRUCTURE_POOL_KEY_CODEC).orElse(StructurePools.EMPTY), (Identifier)nbtCompound.get("target", Identifier.CODEC).orElse(JigsawBlockEntity.DEFAULT_NAME), nbtCompound.getInt("placement_priority", 0), nbtCompound.getInt("selection_priority", 0));
      }

      public String toString() {
         return String.format(Locale.ROOT, "<JigsawBlockInfo | %s | %s | name: %s | pool: %s | target: %s | placement: %d | selection: %d | %s>", this.info.pos, this.info.state, this.name, this.pool.getValue(), this.target, this.placementPriority, this.selectionPriority, this.info.nbt);
      }

      public JigsawBlockInfo withInfo(StructureBlockInfo structureBlockInfo) {
         return new JigsawBlockInfo(structureBlockInfo, this.jointType, this.name, this.pool, this.target, this.placementPriority, this.selectionPriority);
      }

      public StructureBlockInfo info() {
         return this.info;
      }

      public JigsawBlockEntity.Joint jointType() {
         return this.jointType;
      }

      public Identifier name() {
         return this.name;
      }

      public RegistryKey pool() {
         return this.pool;
      }

      public Identifier target() {
         return this.target;
      }

      public int placementPriority() {
         return this.placementPriority;
      }

      public int selectionPriority() {
         return this.selectionPriority;
      }
   }

   static class Palette implements Iterable {
      public static final BlockState AIR;
      private final IdList ids = new IdList(16);
      private int currentIndex;

      public int getId(BlockState state) {
         int i = this.ids.getRawId(state);
         if (i == -1) {
            i = this.currentIndex++;
            this.ids.set(state, i);
         }

         return i;
      }

      @Nullable
      public BlockState getState(int id) {
         BlockState blockState = (BlockState)this.ids.get(id);
         return blockState == null ? AIR : blockState;
      }

      public Iterator iterator() {
         return this.ids.iterator();
      }

      public void set(BlockState state, int id) {
         this.ids.set(state, id);
      }

      static {
         AIR = Blocks.AIR.getDefaultState();
      }
   }
}
