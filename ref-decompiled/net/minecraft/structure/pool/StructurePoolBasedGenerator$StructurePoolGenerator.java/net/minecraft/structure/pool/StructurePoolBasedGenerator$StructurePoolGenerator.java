/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure.pool;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.JigsawBlock;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureLiquidSettings;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.pool.alias.StructurePoolAliasLookup;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.collection.PriorityIterator;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jspecify.annotations.Nullable;

static final class StructurePoolBasedGenerator.StructurePoolGenerator {
    private final Registry<StructurePool> registry;
    private final int maxSize;
    private final ChunkGenerator chunkGenerator;
    private final StructureTemplateManager structureTemplateManager;
    private final List<? super PoolStructurePiece> children;
    private final Random random;
    final PriorityIterator<StructurePoolBasedGenerator.ShapedPoolStructurePiece> structurePieces = new PriorityIterator();

    StructurePoolBasedGenerator.StructurePoolGenerator(Registry<StructurePool> registry, int maxSize, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, List<? super PoolStructurePiece> children, Random random) {
        this.registry = registry;
        this.maxSize = maxSize;
        this.chunkGenerator = chunkGenerator;
        this.structureTemplateManager = structureTemplateManager;
        this.children = children;
        this.random = random;
    }

    void generatePiece(PoolStructurePiece piece, MutableObject<VoxelShape> pieceShape, int depth, boolean modifyBoundingBox, HeightLimitView world, NoiseConfig noiseConfig, StructurePoolAliasLookup aliasLookup, StructureLiquidSettings liquidSettings) {
        StructurePoolElement structurePoolElement = piece.getPoolElement();
        BlockPos blockPos = piece.getPos();
        BlockRotation blockRotation = piece.getRotation();
        StructurePool.Projection projection = structurePoolElement.getProjection();
        boolean bl = projection == StructurePool.Projection.RIGID;
        MutableObject<@Nullable VoxelShape> mutableObject = new MutableObject<VoxelShape>();
        BlockBox blockBox = piece.getBoundingBox();
        int i = blockBox.getMinY();
        block0: for (StructureTemplate.JigsawBlockInfo jigsawBlockInfo : structurePoolElement.getStructureBlockInfos(this.structureTemplateManager, blockPos, blockRotation, this.random)) {
            StructurePoolElement structurePoolElement2;
            MutableObject<VoxelShape> mutableObject2;
            StructureTemplate.StructureBlockInfo structureBlockInfo = jigsawBlockInfo.info();
            Direction direction = JigsawBlock.getFacing(structureBlockInfo.state());
            BlockPos blockPos2 = structureBlockInfo.pos();
            BlockPos blockPos3 = blockPos2.offset(direction);
            int j = blockPos2.getY() - i;
            int k = Integer.MIN_VALUE;
            RegistryKey<StructurePool> registryKey = aliasLookup.lookup(jigsawBlockInfo.pool());
            Optional optional = this.registry.getOptional(registryKey);
            if (optional.isEmpty()) {
                LOGGER.warn("Empty or non-existent pool: {}", (Object)registryKey.getValue());
                continue;
            }
            RegistryEntry registryEntry = (RegistryEntry)optional.get();
            if (((StructurePool)registryEntry.value()).getElementCount() == 0 && !registryEntry.matchesKey(StructurePools.EMPTY)) {
                LOGGER.warn("Empty or non-existent pool: {}", (Object)registryKey.getValue());
                continue;
            }
            RegistryEntry<StructurePool> registryEntry2 = ((StructurePool)registryEntry.value()).getFallback();
            if (registryEntry2.value().getElementCount() == 0 && !registryEntry2.matchesKey(StructurePools.EMPTY)) {
                LOGGER.warn("Empty or non-existent fallback pool: {}", (Object)registryEntry2.getKey().map(key -> key.getValue().toString()).orElse("<unregistered>"));
                continue;
            }
            boolean bl2 = blockBox.contains(blockPos3);
            if (bl2) {
                mutableObject2 = mutableObject;
                if (mutableObject.get() == null) {
                    mutableObject.setValue((Object)VoxelShapes.cuboid(Box.from(blockBox)));
                }
            } else {
                mutableObject2 = pieceShape;
            }
            ArrayList list = Lists.newArrayList();
            if (depth != this.maxSize) {
                list.addAll(((StructurePool)registryEntry.value()).getElementIndicesInRandomOrder(this.random));
            }
            list.addAll(registryEntry2.value().getElementIndicesInRandomOrder(this.random));
            int l = jigsawBlockInfo.placementPriority();
            Iterator iterator = list.iterator();
            while (iterator.hasNext() && (structurePoolElement2 = (StructurePoolElement)iterator.next()) != EmptyPoolElement.INSTANCE) {
                for (BlockRotation blockRotation2 : BlockRotation.randomRotationOrder(this.random)) {
                    List<StructureTemplate.JigsawBlockInfo> list2 = structurePoolElement2.getStructureBlockInfos(this.structureTemplateManager, BlockPos.ORIGIN, blockRotation2, this.random);
                    BlockBox blockBox2 = structurePoolElement2.getBoundingBox(this.structureTemplateManager, BlockPos.ORIGIN, blockRotation2);
                    int m = !modifyBoundingBox || blockBox2.getBlockCountY() > 16 ? 0 : list2.stream().mapToInt(jigsawInfo -> {
                        StructureTemplate.StructureBlockInfo structureBlockInfo = jigsawInfo.info();
                        if (!blockBox2.contains(structureBlockInfo.pos().offset(JigsawBlock.getFacing(structureBlockInfo.state())))) {
                            return 0;
                        }
                        RegistryKey<StructurePool> registryKey = aliasLookup.lookup(jigsawInfo.pool());
                        Optional optional = this.registry.getOptional(registryKey);
                        Optional<RegistryEntry> optional2 = optional.map(entry -> ((StructurePool)entry.value()).getFallback());
                        int i = optional.map(entry -> ((StructurePool)entry.value()).getHighestY(this.structureTemplateManager)).orElse(0);
                        int j = optional2.map(entry -> ((StructurePool)entry.value()).getHighestY(this.structureTemplateManager)).orElse(0);
                        return Math.max(i, j);
                    }).max().orElse(0);
                    for (StructureTemplate.JigsawBlockInfo jigsawBlockInfo2 : list2) {
                        int u;
                        int s;
                        int q;
                        if (!JigsawBlock.attachmentMatches(jigsawBlockInfo, jigsawBlockInfo2)) continue;
                        BlockPos blockPos4 = jigsawBlockInfo2.info().pos();
                        BlockPos blockPos5 = blockPos3.subtract(blockPos4);
                        BlockBox blockBox3 = structurePoolElement2.getBoundingBox(this.structureTemplateManager, blockPos5, blockRotation2);
                        int n = blockBox3.getMinY();
                        StructurePool.Projection projection2 = structurePoolElement2.getProjection();
                        boolean bl3 = projection2 == StructurePool.Projection.RIGID;
                        int o = blockPos4.getY();
                        int p = j - o + JigsawBlock.getFacing(structureBlockInfo.state()).getOffsetY();
                        if (bl && bl3) {
                            q = i + p;
                        } else {
                            if (k == Integer.MIN_VALUE) {
                                k = this.chunkGenerator.getHeightOnGround(blockPos2.getX(), blockPos2.getZ(), Heightmap.Type.WORLD_SURFACE_WG, world, noiseConfig);
                            }
                            q = k - o;
                        }
                        int r = q - n;
                        BlockBox blockBox4 = blockBox3.offset(0, r, 0);
                        BlockPos blockPos6 = blockPos5.add(0, r, 0);
                        if (m > 0) {
                            s = Math.max(m + 1, blockBox4.getMaxY() - blockBox4.getMinY());
                            blockBox4.encompass(new BlockPos(blockBox4.getMinX(), blockBox4.getMinY() + s, blockBox4.getMinZ()));
                        }
                        if (VoxelShapes.matchesAnywhere((VoxelShape)mutableObject2.get(), VoxelShapes.cuboid(Box.from(blockBox4).contract(0.25)), BooleanBiFunction.ONLY_SECOND)) continue;
                        mutableObject2.setValue((Object)VoxelShapes.combine((VoxelShape)mutableObject2.get(), VoxelShapes.cuboid(Box.from(blockBox4)), BooleanBiFunction.ONLY_FIRST));
                        s = piece.getGroundLevelDelta();
                        int t = bl3 ? s - p : structurePoolElement2.getGroundLevelDelta();
                        PoolStructurePiece poolStructurePiece = new PoolStructurePiece(this.structureTemplateManager, structurePoolElement2, blockPos6, t, blockRotation2, blockBox4, liquidSettings);
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
                        if (depth + 1 > this.maxSize) continue block0;
                        StructurePoolBasedGenerator.ShapedPoolStructurePiece shapedPoolStructurePiece = new StructurePoolBasedGenerator.ShapedPoolStructurePiece(poolStructurePiece, mutableObject2, depth + 1);
                        this.structurePieces.enqueue(shapedPoolStructurePiece, l);
                        continue block0;
                    }
                }
            }
        }
    }
}
