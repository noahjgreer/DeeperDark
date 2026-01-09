package net.minecraft.structure.pool;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.block.JigsawBlock;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureLiquidSettings;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.alias.StructurePoolAliasLookup;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.PriorityIterator;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.DimensionPadding;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.minecraft.world.gen.structure.Structure;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

public class StructurePoolBasedGenerator {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int HEIGHT_NOT_SET = Integer.MIN_VALUE;

   public static Optional generate(Structure.Context context, RegistryEntry structurePool, Optional id, int size, BlockPos pos, boolean useExpansionHack, Optional projectStartToHeightmap, int maxDistanceFromCenter, StructurePoolAliasLookup aliasLookup, DimensionPadding dimensionPadding, StructureLiquidSettings liquidSettings) {
      DynamicRegistryManager dynamicRegistryManager = context.dynamicRegistryManager();
      ChunkGenerator chunkGenerator = context.chunkGenerator();
      StructureTemplateManager structureTemplateManager = context.structureTemplateManager();
      HeightLimitView heightLimitView = context.world();
      ChunkRandom chunkRandom = context.random();
      Registry registry = dynamicRegistryManager.getOrThrow(RegistryKeys.TEMPLATE_POOL);
      BlockRotation blockRotation = BlockRotation.random(chunkRandom);
      StructurePool structurePool2 = (StructurePool)structurePool.getKey().flatMap((key) -> {
         return registry.getOptionalValue(aliasLookup.lookup(key));
      }).orElse((StructurePool)structurePool.value());
      StructurePoolElement structurePoolElement = structurePool2.getRandomElement(chunkRandom);
      if (structurePoolElement == EmptyPoolElement.INSTANCE) {
         return Optional.empty();
      } else {
         BlockPos blockPos;
         if (id.isPresent()) {
            Identifier identifier = (Identifier)id.get();
            Optional optional = findStartingJigsawPos(structurePoolElement, identifier, pos, blockRotation, structureTemplateManager, chunkRandom);
            if (optional.isEmpty()) {
               LOGGER.error("No starting jigsaw {} found in start pool {}", identifier, structurePool.getKey().map((key) -> {
                  return key.getValue().toString();
               }).orElse("<unregistered>"));
               return Optional.empty();
            }

            blockPos = (BlockPos)optional.get();
         } else {
            blockPos = pos;
         }

         Vec3i vec3i = blockPos.subtract(pos);
         BlockPos blockPos2 = pos.subtract(vec3i);
         PoolStructurePiece poolStructurePiece = new PoolStructurePiece(structureTemplateManager, structurePoolElement, blockPos2, structurePoolElement.getGroundLevelDelta(), blockRotation, structurePoolElement.getBoundingBox(structureTemplateManager, blockPos2, blockRotation), liquidSettings);
         BlockBox blockBox = poolStructurePiece.getBoundingBox();
         int i = (blockBox.getMaxX() + blockBox.getMinX()) / 2;
         int j = (blockBox.getMaxZ() + blockBox.getMinZ()) / 2;
         int k = projectStartToHeightmap.isEmpty() ? blockPos2.getY() : pos.getY() + chunkGenerator.getHeightOnGround(i, j, (Heightmap.Type)projectStartToHeightmap.get(), heightLimitView, context.noiseConfig());
         int l = blockBox.getMinY() + poolStructurePiece.getGroundLevelDelta();
         poolStructurePiece.translate(0, k - l, 0);
         if (method_65173(heightLimitView, dimensionPadding, poolStructurePiece.getBoundingBox())) {
            LOGGER.debug("Center piece {} with bounding box {} does not fit dimension padding {}", new Object[]{structurePoolElement, poolStructurePiece.getBoundingBox(), dimensionPadding});
            return Optional.empty();
         } else {
            int m = k + vec3i.getY();
            return Optional.of(new Structure.StructurePosition(new BlockPos(i, m, j), (collector) -> {
               List list = Lists.newArrayList();
               list.add(poolStructurePiece);
               if (size > 0) {
                  Box box = new Box((double)(i - maxDistanceFromCenter), (double)Math.max(m - maxDistanceFromCenter, heightLimitView.getBottomY() + dimensionPadding.bottom()), (double)(j - maxDistanceFromCenter), (double)(i + maxDistanceFromCenter + 1), (double)Math.min(m + maxDistanceFromCenter + 1, heightLimitView.getTopYInclusive() + 1 - dimensionPadding.top()), (double)(j + maxDistanceFromCenter + 1));
                  VoxelShape voxelShape = VoxelShapes.combineAndSimplify(VoxelShapes.cuboid(box), VoxelShapes.cuboid(Box.from(blockBox)), BooleanBiFunction.ONLY_FIRST);
                  generate(context.noiseConfig(), size, useExpansionHack, chunkGenerator, structureTemplateManager, heightLimitView, chunkRandom, registry, poolStructurePiece, list, voxelShape, aliasLookup, liquidSettings);
                  Objects.requireNonNull(collector);
                  list.forEach(collector::addPiece);
               }
            }));
         }
      }
   }

   private static boolean method_65173(HeightLimitView heightLimitView, DimensionPadding dimensionPadding, BlockBox blockBox) {
      if (dimensionPadding == DimensionPadding.NONE) {
         return false;
      } else {
         int i = heightLimitView.getBottomY() + dimensionPadding.bottom();
         int j = heightLimitView.getTopYInclusive() - dimensionPadding.top();
         return blockBox.getMinY() < i || blockBox.getMaxY() > j;
      }
   }

   private static Optional findStartingJigsawPos(StructurePoolElement pool, Identifier id, BlockPos pos, BlockRotation rotation, StructureTemplateManager structureManager, ChunkRandom random) {
      List list = pool.getStructureBlockInfos(structureManager, pos, rotation, random);
      Iterator var7 = list.iterator();

      StructureTemplate.JigsawBlockInfo jigsawBlockInfo;
      do {
         if (!var7.hasNext()) {
            return Optional.empty();
         }

         jigsawBlockInfo = (StructureTemplate.JigsawBlockInfo)var7.next();
      } while(!id.equals(jigsawBlockInfo.name()));

      return Optional.of(jigsawBlockInfo.info().pos());
   }

   private static void generate(NoiseConfig noiseConfig, int maxSize, boolean modifyBoundingBox, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, HeightLimitView heightLimitView, Random random, Registry structurePoolRegistry, PoolStructurePiece firstPiece, List pieces, VoxelShape pieceShape, StructurePoolAliasLookup aliasLookup, StructureLiquidSettings liquidSettings) {
      StructurePoolGenerator structurePoolGenerator = new StructurePoolGenerator(structurePoolRegistry, maxSize, chunkGenerator, structureTemplateManager, pieces, random);
      structurePoolGenerator.generatePiece(firstPiece, new MutableObject(pieceShape), 0, modifyBoundingBox, heightLimitView, noiseConfig, aliasLookup, liquidSettings);

      while(structurePoolGenerator.structurePieces.hasNext()) {
         ShapedPoolStructurePiece shapedPoolStructurePiece = (ShapedPoolStructurePiece)structurePoolGenerator.structurePieces.next();
         structurePoolGenerator.generatePiece(shapedPoolStructurePiece.piece, shapedPoolStructurePiece.pieceShape, shapedPoolStructurePiece.depth, modifyBoundingBox, heightLimitView, noiseConfig, aliasLookup, liquidSettings);
      }

   }

   public static boolean generate(ServerWorld world, RegistryEntry structurePool, Identifier id, int size, BlockPos pos, boolean keepJigsaws) {
      ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
      StructureTemplateManager structureTemplateManager = world.getStructureTemplateManager();
      StructureAccessor structureAccessor = world.getStructureAccessor();
      Random random = world.getRandom();
      Structure.Context context = new Structure.Context(world.getRegistryManager(), chunkGenerator, chunkGenerator.getBiomeSource(), world.getChunkManager().getNoiseConfig(), structureTemplateManager, world.getSeed(), new ChunkPos(pos), world, (biome) -> {
         return true;
      });
      Optional optional = generate(context, structurePool, Optional.of(id), size, pos, false, Optional.empty(), 128, StructurePoolAliasLookup.EMPTY, JigsawStructure.DEFAULT_DIMENSION_PADDING, JigsawStructure.DEFAULT_LIQUID_SETTINGS);
      if (optional.isPresent()) {
         StructurePiecesCollector structurePiecesCollector = ((Structure.StructurePosition)optional.get()).generate();
         Iterator var13 = structurePiecesCollector.toList().pieces().iterator();

         while(var13.hasNext()) {
            StructurePiece structurePiece = (StructurePiece)var13.next();
            if (structurePiece instanceof PoolStructurePiece) {
               PoolStructurePiece poolStructurePiece = (PoolStructurePiece)structurePiece;
               poolStructurePiece.generate(world, structureAccessor, chunkGenerator, random, BlockBox.infinite(), pos, keepJigsaws);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   static final class StructurePoolGenerator {
      private final Registry registry;
      private final int maxSize;
      private final ChunkGenerator chunkGenerator;
      private final StructureTemplateManager structureTemplateManager;
      private final List children;
      private final Random random;
      final PriorityIterator structurePieces = new PriorityIterator();

      StructurePoolGenerator(Registry registry, int maxSize, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, List children, Random random) {
         this.registry = registry;
         this.maxSize = maxSize;
         this.chunkGenerator = chunkGenerator;
         this.structureTemplateManager = structureTemplateManager;
         this.children = children;
         this.random = random;
      }

      void generatePiece(PoolStructurePiece piece, MutableObject pieceShape, int depth, boolean modifyBoundingBox, HeightLimitView world, NoiseConfig noiseConfig, StructurePoolAliasLookup aliasLookup, StructureLiquidSettings liquidSettings) {
         StructurePoolElement structurePoolElement = piece.getPoolElement();
         BlockPos blockPos = piece.getPos();
         BlockRotation blockRotation = piece.getRotation();
         StructurePool.Projection projection = structurePoolElement.getProjection();
         boolean bl = projection == StructurePool.Projection.RIGID;
         MutableObject mutableObject = new MutableObject();
         BlockBox blockBox = piece.getBoundingBox();
         int i = blockBox.getMinY();
         Iterator var17 = structurePoolElement.getStructureBlockInfos(this.structureTemplateManager, blockPos, blockRotation, this.random).iterator();

         while(true) {
            label129:
            while(var17.hasNext()) {
               StructureTemplate.JigsawBlockInfo jigsawBlockInfo = (StructureTemplate.JigsawBlockInfo)var17.next();
               StructureTemplate.StructureBlockInfo structureBlockInfo = jigsawBlockInfo.info();
               Direction direction = JigsawBlock.getFacing(structureBlockInfo.state());
               BlockPos blockPos2 = structureBlockInfo.pos();
               BlockPos blockPos3 = blockPos2.offset(direction);
               int j = blockPos2.getY() - i;
               int k = Integer.MIN_VALUE;
               RegistryKey registryKey = aliasLookup.lookup(jigsawBlockInfo.pool());
               Optional optional = this.registry.getOptional(registryKey);
               if (optional.isEmpty()) {
                  StructurePoolBasedGenerator.LOGGER.warn("Empty or non-existent pool: {}", registryKey.getValue());
               } else {
                  RegistryEntry registryEntry = (RegistryEntry)optional.get();
                  if (((StructurePool)registryEntry.value()).getElementCount() == 0 && !registryEntry.matchesKey(StructurePools.EMPTY)) {
                     StructurePoolBasedGenerator.LOGGER.warn("Empty or non-existent pool: {}", registryKey.getValue());
                  } else {
                     RegistryEntry registryEntry2 = ((StructurePool)registryEntry.value()).getFallback();
                     if (((StructurePool)registryEntry2.value()).getElementCount() == 0 && !registryEntry2.matchesKey(StructurePools.EMPTY)) {
                        StructurePoolBasedGenerator.LOGGER.warn("Empty or non-existent fallback pool: {}", registryEntry2.getKey().map((key) -> {
                           return key.getValue().toString();
                        }).orElse("<unregistered>"));
                     } else {
                        boolean bl2 = blockBox.contains(blockPos3);
                        MutableObject mutableObject2;
                        if (bl2) {
                           mutableObject2 = mutableObject;
                           if (mutableObject.getValue() == null) {
                              mutableObject.setValue(VoxelShapes.cuboid(Box.from(blockBox)));
                           }
                        } else {
                           mutableObject2 = pieceShape;
                        }

                        List list = Lists.newArrayList();
                        if (depth != this.maxSize) {
                           list.addAll(((StructurePool)registryEntry.value()).getElementIndicesInRandomOrder(this.random));
                        }

                        list.addAll(((StructurePool)registryEntry2.value()).getElementIndicesInRandomOrder(this.random));
                        int l = jigsawBlockInfo.placementPriority();
                        Iterator var33 = list.iterator();

                        while(var33.hasNext()) {
                           StructurePoolElement structurePoolElement2 = (StructurePoolElement)var33.next();
                           if (structurePoolElement2 == EmptyPoolElement.INSTANCE) {
                              break;
                           }

                           Iterator var35 = BlockRotation.randomRotationOrder(this.random).iterator();

                           label125:
                           while(var35.hasNext()) {
                              BlockRotation blockRotation2 = (BlockRotation)var35.next();
                              List list2 = structurePoolElement2.getStructureBlockInfos(this.structureTemplateManager, BlockPos.ORIGIN, blockRotation2, this.random);
                              BlockBox blockBox2 = structurePoolElement2.getBoundingBox(this.structureTemplateManager, BlockPos.ORIGIN, blockRotation2);
                              int m;
                              if (modifyBoundingBox && blockBox2.getBlockCountY() <= 16) {
                                 m = list2.stream().mapToInt((jigsawInfo) -> {
                                    StructureTemplate.StructureBlockInfo structureBlockInfo = jigsawInfo.info();
                                    if (!blockBox2.contains(structureBlockInfo.pos().offset(JigsawBlock.getFacing(structureBlockInfo.state())))) {
                                       return 0;
                                    } else {
                                       RegistryKey registryKey = aliasLookup.lookup(jigsawInfo.pool());
                                       Optional optional = this.registry.getOptional(registryKey);
                                       Optional optional2 = optional.map((entry) -> {
                                          return ((StructurePool)entry.value()).getFallback();
                                       });
                                       int i = (Integer)optional.map((entry) -> {
                                          return ((StructurePool)entry.value()).getHighestY(this.structureTemplateManager);
                                       }).orElse(0);
                                       int j = (Integer)optional2.map((entry) -> {
                                          return ((StructurePool)entry.value()).getHighestY(this.structureTemplateManager);
                                       }).orElse(0);
                                       return Math.max(i, j);
                                    }
                                 }).max().orElse(0);
                              } else {
                                 m = 0;
                              }

                              Iterator var40 = list2.iterator();

                              StructurePool.Projection projection2;
                              boolean bl3;
                              int o;
                              int p;
                              int q;
                              BlockBox blockBox4;
                              BlockPos blockPos6;
                              int s;
                              do {
                                 StructureTemplate.JigsawBlockInfo jigsawBlockInfo2;
                                 do {
                                    if (!var40.hasNext()) {
                                       continue label125;
                                    }

                                    jigsawBlockInfo2 = (StructureTemplate.JigsawBlockInfo)var40.next();
                                 } while(!JigsawBlock.attachmentMatches(jigsawBlockInfo, jigsawBlockInfo2));

                                 BlockPos blockPos4 = jigsawBlockInfo2.info().pos();
                                 BlockPos blockPos5 = blockPos3.subtract(blockPos4);
                                 BlockBox blockBox3 = structurePoolElement2.getBoundingBox(this.structureTemplateManager, blockPos5, blockRotation2);
                                 int n = blockBox3.getMinY();
                                 projection2 = structurePoolElement2.getProjection();
                                 bl3 = projection2 == StructurePool.Projection.RIGID;
                                 o = blockPos4.getY();
                                 p = j - o + JigsawBlock.getFacing(structureBlockInfo.state()).getOffsetY();
                                 if (bl && bl3) {
                                    q = i + p;
                                 } else {
                                    if (k == Integer.MIN_VALUE) {
                                       k = this.chunkGenerator.getHeightOnGround(blockPos2.getX(), blockPos2.getZ(), Heightmap.Type.WORLD_SURFACE_WG, world, noiseConfig);
                                    }

                                    q = k - o;
                                 }

                                 int r = q - n;
                                 blockBox4 = blockBox3.offset(0, r, 0);
                                 blockPos6 = blockPos5.add(0, r, 0);
                                 if (m > 0) {
                                    s = Math.max(m + 1, blockBox4.getMaxY() - blockBox4.getMinY());
                                    blockBox4.encompass(new BlockPos(blockBox4.getMinX(), blockBox4.getMinY() + s, blockBox4.getMinZ()));
                                 }
                              } while(VoxelShapes.matchesAnywhere((VoxelShape)mutableObject2.getValue(), VoxelShapes.cuboid(Box.from(blockBox4).contract(0.25)), BooleanBiFunction.ONLY_SECOND));

                              mutableObject2.setValue(VoxelShapes.combine((VoxelShape)mutableObject2.getValue(), VoxelShapes.cuboid(Box.from(blockBox4)), BooleanBiFunction.ONLY_FIRST));
                              s = piece.getGroundLevelDelta();
                              int t;
                              if (bl3) {
                                 t = s - p;
                              } else {
                                 t = structurePoolElement2.getGroundLevelDelta();
                              }

                              PoolStructurePiece poolStructurePiece = new PoolStructurePiece(this.structureTemplateManager, structurePoolElement2, blockPos6, t, blockRotation2, blockBox4, liquidSettings);
                              int u;
                              if (bl) {
                                 u = i + j;
                              } else if (bl3) {
                                 u = q + o;
                              } else {
                                 if (k == Integer.MIN_VALUE) {
                                    k = this.chunkGenerator.getHeightOnGround(blockPos2.getX(), blockPos2.getZ(), Heightmap.Type.WORLD_SURFACE_WG, world, noiseConfig);
                                 }

                                 u = k + p / 2;
                              }

                              piece.addJunction(new JigsawJunction(blockPos3.getX(), u - j + s, blockPos3.getZ(), p, projection2));
                              poolStructurePiece.addJunction(new JigsawJunction(blockPos2.getX(), u - o + t, blockPos2.getZ(), -p, projection));
                              this.children.add(poolStructurePiece);
                              if (depth + 1 <= this.maxSize) {
                                 ShapedPoolStructurePiece shapedPoolStructurePiece = new ShapedPoolStructurePiece(poolStructurePiece, mutableObject2, depth + 1);
                                 this.structurePieces.enqueue(shapedPoolStructurePiece, l);
                              }
                              continue label129;
                           }
                        }
                     }
                  }
               }
            }

            return;
         }
      }
   }

   private static record ShapedPoolStructurePiece(PoolStructurePiece piece, MutableObject pieceShape, int depth) {
      final PoolStructurePiece piece;
      final MutableObject pieceShape;
      final int depth;

      ShapedPoolStructurePiece(PoolStructurePiece piece, MutableObject pieceShape, int currentSize) {
         this.piece = piece;
         this.pieceShape = pieceShape;
         this.depth = currentSize;
      }

      public PoolStructurePiece piece() {
         return this.piece;
      }

      public MutableObject pieceShape() {
         return this.pieceShape;
      }

      public int depth() {
         return this.depth;
      }
   }
}
