/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.RailBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.structure.MineshaftGenerator;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePiecesHolder;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.MineshaftStructure;
import org.jspecify.annotations.Nullable;

public static class MineshaftGenerator.MineshaftCorridor
extends MineshaftGenerator.MineshaftPart {
    private final boolean hasRails;
    private final boolean hasCobwebs;
    private boolean hasSpawner;
    private final int length;

    public MineshaftGenerator.MineshaftCorridor(NbtCompound nbt) {
        super(StructurePieceType.MINESHAFT_CORRIDOR, nbt);
        this.hasRails = nbt.getBoolean("hr", false);
        this.hasCobwebs = nbt.getBoolean("sc", false);
        this.hasSpawner = nbt.getBoolean("hps", false);
        this.length = nbt.getInt("Num", 0);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        super.writeNbt(context, nbt);
        nbt.putBoolean("hr", this.hasRails);
        nbt.putBoolean("sc", this.hasCobwebs);
        nbt.putBoolean("hps", this.hasSpawner);
        nbt.putInt("Num", this.length);
    }

    public MineshaftGenerator.MineshaftCorridor(int chainLength, Random random, BlockBox boundingBox, Direction orientation, MineshaftStructure.Type type) {
        super(StructurePieceType.MINESHAFT_CORRIDOR, chainLength, type, boundingBox);
        this.setOrientation(orientation);
        this.hasRails = random.nextInt(3) == 0;
        this.hasCobwebs = !this.hasRails && random.nextInt(23) == 0;
        this.length = this.getFacing().getAxis() == Direction.Axis.Z ? boundingBox.getBlockCountZ() / 5 : boundingBox.getBlockCountX() / 5;
    }

    /*
     * Enabled aggressive block sorting
     */
    public static @Nullable BlockBox getBoundingBox(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation) {
        int i = random.nextInt(3) + 2;
        while (i > 0) {
            int j = i * 5;
            BlockBox blockBox = switch (orientation) {
                default -> new BlockBox(0, 0, -(j - 1), 2, 2, 0);
                case Direction.SOUTH -> new BlockBox(0, 0, 0, 2, 2, j - 1);
                case Direction.WEST -> new BlockBox(-(j - 1), 0, 0, 0, 2, 2);
                case Direction.EAST -> new BlockBox(0, 0, 0, j - 1, 2, 2);
            };
            blockBox.move(x, y, z);
            if (holder.getIntersecting(blockBox) == null) {
                return blockBox;
            }
            --i;
        }
        return null;
    }

    @Override
    public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
        block24: {
            int i = this.getChainLength();
            int j = random.nextInt(4);
            Direction direction = this.getFacing();
            if (direction != null) {
                switch (direction) {
                    default: {
                        if (j <= 1) {
                            MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX(), this.boundingBox.getMinY() - 1 + random.nextInt(3), this.boundingBox.getMinZ() - 1, direction, i);
                            break;
                        }
                        if (j == 2) {
                            MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY() - 1 + random.nextInt(3), this.boundingBox.getMinZ(), Direction.WEST, i);
                            break;
                        }
                        MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY() - 1 + random.nextInt(3), this.boundingBox.getMinZ(), Direction.EAST, i);
                        break;
                    }
                    case SOUTH: {
                        if (j <= 1) {
                            MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX(), this.boundingBox.getMinY() - 1 + random.nextInt(3), this.boundingBox.getMaxZ() + 1, direction, i);
                            break;
                        }
                        if (j == 2) {
                            MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY() - 1 + random.nextInt(3), this.boundingBox.getMaxZ() - 3, Direction.WEST, i);
                            break;
                        }
                        MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY() - 1 + random.nextInt(3), this.boundingBox.getMaxZ() - 3, Direction.EAST, i);
                        break;
                    }
                    case WEST: {
                        if (j <= 1) {
                            MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY() - 1 + random.nextInt(3), this.boundingBox.getMinZ(), direction, i);
                            break;
                        }
                        if (j == 2) {
                            MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX(), this.boundingBox.getMinY() - 1 + random.nextInt(3), this.boundingBox.getMinZ() - 1, Direction.NORTH, i);
                            break;
                        }
                        MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX(), this.boundingBox.getMinY() - 1 + random.nextInt(3), this.boundingBox.getMaxZ() + 1, Direction.SOUTH, i);
                        break;
                    }
                    case EAST: {
                        if (j <= 1) {
                            MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY() - 1 + random.nextInt(3), this.boundingBox.getMinZ(), direction, i);
                            break;
                        }
                        if (j == 2) {
                            MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() - 3, this.boundingBox.getMinY() - 1 + random.nextInt(3), this.boundingBox.getMinZ() - 1, Direction.NORTH, i);
                            break;
                        }
                        MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() - 3, this.boundingBox.getMinY() - 1 + random.nextInt(3), this.boundingBox.getMaxZ() + 1, Direction.SOUTH, i);
                    }
                }
            }
            if (i >= 8) break block24;
            if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                int k = this.boundingBox.getMinZ() + 3;
                while (k + 3 <= this.boundingBox.getMaxZ()) {
                    int l = random.nextInt(5);
                    if (l == 0) {
                        MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY(), k, Direction.WEST, i + 1);
                    } else if (l == 1) {
                        MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY(), k, Direction.EAST, i + 1);
                    }
                    k += 5;
                }
            } else {
                int k = this.boundingBox.getMinX() + 3;
                while (k + 3 <= this.boundingBox.getMaxX()) {
                    int l = random.nextInt(5);
                    if (l == 0) {
                        MineshaftGenerator.pieceGenerator(start, holder, random, k, this.boundingBox.getMinY(), this.boundingBox.getMinZ() - 1, Direction.NORTH, i + 1);
                    } else if (l == 1) {
                        MineshaftGenerator.pieceGenerator(start, holder, random, k, this.boundingBox.getMinY(), this.boundingBox.getMaxZ() + 1, Direction.SOUTH, i + 1);
                    }
                    k += 5;
                }
            }
        }
    }

    @Override
    protected boolean addChest(StructureWorldAccess world, BlockBox boundingBox, Random random, int x, int y, int z, RegistryKey<LootTable> lootTable) {
        BlockPos.Mutable blockPos = this.offsetPos(x, y, z);
        if (boundingBox.contains(blockPos) && world.getBlockState(blockPos).isAir() && !world.getBlockState(((BlockPos)blockPos).down()).isAir()) {
            BlockState blockState = (BlockState)Blocks.RAIL.getDefaultState().with(RailBlock.SHAPE, random.nextBoolean() ? RailShape.NORTH_SOUTH : RailShape.EAST_WEST);
            this.addBlock(world, blockState, x, y, z, boundingBox);
            ChestMinecartEntity chestMinecartEntity = EntityType.CHEST_MINECART.create(world.toServerWorld(), SpawnReason.CHUNK_GENERATION);
            if (chestMinecartEntity != null) {
                chestMinecartEntity.initPosition((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
                chestMinecartEntity.setLootTable(lootTable, random.nextLong());
                world.spawnEntity(chestMinecartEntity);
            }
            return true;
        }
        return false;
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        int p;
        int o;
        int n;
        if (this.cannotGenerate(world, chunkBox)) {
            return;
        }
        boolean i = false;
        int j = 2;
        boolean k = false;
        int l = 2;
        int m = this.length * 5 - 1;
        BlockState blockState = this.mineshaftType.getPlanks();
        this.fillWithOutline(world, chunkBox, 0, 0, 0, 2, 1, m, AIR, AIR, false);
        this.fillWithOutlineUnderSeaLevel(world, chunkBox, random, 0.8f, 0, 2, 0, 2, 2, m, AIR, AIR, false, false);
        if (this.hasCobwebs) {
            this.fillWithOutlineUnderSeaLevel(world, chunkBox, random, 0.6f, 0, 0, 0, 2, 1, m, Blocks.COBWEB.getDefaultState(), AIR, false, true);
        }
        for (n = 0; n < this.length; ++n) {
            o = 2 + n * 5;
            this.generateSupports(world, chunkBox, 0, 0, o, 2, 2, random);
            this.addCobwebsUnderground(world, chunkBox, random, 0.1f, 0, 2, o - 1);
            this.addCobwebsUnderground(world, chunkBox, random, 0.1f, 2, 2, o - 1);
            this.addCobwebsUnderground(world, chunkBox, random, 0.1f, 0, 2, o + 1);
            this.addCobwebsUnderground(world, chunkBox, random, 0.1f, 2, 2, o + 1);
            this.addCobwebsUnderground(world, chunkBox, random, 0.05f, 0, 2, o - 2);
            this.addCobwebsUnderground(world, chunkBox, random, 0.05f, 2, 2, o - 2);
            this.addCobwebsUnderground(world, chunkBox, random, 0.05f, 0, 2, o + 2);
            this.addCobwebsUnderground(world, chunkBox, random, 0.05f, 2, 2, o + 2);
            if (random.nextInt(100) == 0) {
                this.addChest(world, chunkBox, random, 2, 0, o - 1, LootTables.ABANDONED_MINESHAFT_CHEST);
            }
            if (random.nextInt(100) == 0) {
                this.addChest(world, chunkBox, random, 0, 0, o + 1, LootTables.ABANDONED_MINESHAFT_CHEST);
            }
            if (!this.hasCobwebs || this.hasSpawner) continue;
            p = 1;
            int q = o - 1 + random.nextInt(3);
            BlockPos.Mutable blockPos = this.offsetPos(1, 0, q);
            if (!chunkBox.contains(blockPos) || !this.isUnderSeaLevel(world, 1, 0, q, chunkBox)) continue;
            this.hasSpawner = true;
            world.setBlockState(blockPos, Blocks.SPAWNER.getDefaultState(), 2);
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (!(blockEntity instanceof MobSpawnerBlockEntity)) continue;
            MobSpawnerBlockEntity mobSpawnerBlockEntity = (MobSpawnerBlockEntity)blockEntity;
            mobSpawnerBlockEntity.setEntityType(EntityType.CAVE_SPIDER, random);
        }
        for (n = 0; n <= 2; ++n) {
            for (o = 0; o <= m; ++o) {
                this.tryPlaceFloor(world, chunkBox, blockState, n, -1, o);
            }
        }
        n = 2;
        this.fillSupportBeam(world, chunkBox, 0, -1, 2);
        if (this.length > 1) {
            o = m - 2;
            this.fillSupportBeam(world, chunkBox, 0, -1, o);
        }
        if (this.hasRails) {
            BlockState blockState2 = (BlockState)Blocks.RAIL.getDefaultState().with(RailBlock.SHAPE, RailShape.NORTH_SOUTH);
            for (p = 0; p <= m; ++p) {
                BlockState blockState3 = this.getBlockAt(world, 1, -1, p, chunkBox);
                if (blockState3.isAir() || !blockState3.isOpaqueFullCube()) continue;
                float f = this.isUnderSeaLevel(world, 1, 0, p, chunkBox) ? 0.7f : 0.9f;
                this.addBlockWithRandomThreshold(world, chunkBox, random, f, 1, 0, p, blockState2);
            }
        }
    }

    private void fillSupportBeam(StructureWorldAccess world, BlockBox box, int x, int y, int z) {
        BlockState blockState = this.mineshaftType.getLog();
        BlockState blockState2 = this.mineshaftType.getPlanks();
        if (this.getBlockAt(world, x, y, z, box).isOf(blockState2.getBlock())) {
            this.fillSupportBeam(world, blockState, x, y, z, box);
        }
        if (this.getBlockAt(world, x + 2, y, z, box).isOf(blockState2.getBlock())) {
            this.fillSupportBeam(world, blockState, x + 2, y, z, box);
        }
    }

    @Override
    protected void fillDownwards(StructureWorldAccess world, BlockState state, int x, int y, int z, BlockBox box) {
        BlockPos.Mutable mutable = this.offsetPos(x, y, z);
        if (!box.contains(mutable)) {
            return;
        }
        int i = mutable.getY();
        while (this.canReplace(world.getBlockState(mutable)) && mutable.getY() > world.getBottomY() + 1) {
            mutable.move(Direction.DOWN);
        }
        if (!this.isUpsideSolidFullSquare(world, mutable, world.getBlockState(mutable))) {
            return;
        }
        while (mutable.getY() < i) {
            mutable.move(Direction.UP);
            world.setBlockState(mutable, state, 2);
        }
    }

    protected void fillSupportBeam(StructureWorldAccess world, BlockState state, int x, int y, int z, BlockBox box) {
        BlockPos.Mutable mutable = this.offsetPos(x, y, z);
        if (!box.contains(mutable)) {
            return;
        }
        int i = mutable.getY();
        int j = 1;
        boolean bl = true;
        boolean bl2 = true;
        while (bl || bl2) {
            boolean bl3;
            BlockState blockState;
            if (bl) {
                mutable.setY(i - j);
                blockState = world.getBlockState(mutable);
                boolean bl4 = bl3 = this.canReplace(blockState) && !blockState.isOf(Blocks.LAVA);
                if (!bl3 && this.isUpsideSolidFullSquare(world, mutable, blockState)) {
                    MineshaftGenerator.MineshaftCorridor.fillColumn(world, state, mutable, i - j + 1, i);
                    return;
                }
                boolean bl5 = bl = j <= 20 && bl3 && mutable.getY() > world.getBottomY() + 1;
            }
            if (bl2) {
                mutable.setY(i + j);
                blockState = world.getBlockState(mutable);
                bl3 = this.canReplace(blockState);
                if (!bl3 && this.sideCoversSmallSquare(world, mutable, blockState)) {
                    world.setBlockState(mutable.setY(i + 1), this.mineshaftType.getFence(), 2);
                    MineshaftGenerator.MineshaftCorridor.fillColumn(world, Blocks.IRON_CHAIN.getDefaultState(), mutable, i + 2, i + j);
                    return;
                }
                bl2 = j <= 50 && bl3 && mutable.getY() < world.getTopYInclusive();
            }
            ++j;
        }
    }

    private static void fillColumn(StructureWorldAccess world, BlockState state, BlockPos.Mutable pos, int startY, int endY) {
        for (int i = startY; i < endY; ++i) {
            world.setBlockState(pos.setY(i), state, 2);
        }
    }

    private boolean isUpsideSolidFullSquare(WorldView world, BlockPos pos, BlockState state) {
        return state.isSideSolidFullSquare(world, pos, Direction.UP);
    }

    private boolean sideCoversSmallSquare(WorldView world, BlockPos pos, BlockState state) {
        return Block.sideCoversSmallSquare(world, pos, Direction.DOWN) && !(state.getBlock() instanceof FallingBlock);
    }

    private void generateSupports(StructureWorldAccess world, BlockBox boundingBox, int minX, int minY, int z, int maxY, int maxX, Random random) {
        if (!this.isSolidCeiling(world, boundingBox, minX, maxX, maxY, z)) {
            return;
        }
        BlockState blockState = this.mineshaftType.getPlanks();
        BlockState blockState2 = this.mineshaftType.getFence();
        this.fillWithOutline(world, boundingBox, minX, minY, z, minX, maxY - 1, z, (BlockState)blockState2.with(FenceBlock.WEST, true), AIR, false);
        this.fillWithOutline(world, boundingBox, maxX, minY, z, maxX, maxY - 1, z, (BlockState)blockState2.with(FenceBlock.EAST, true), AIR, false);
        if (random.nextInt(4) == 0) {
            this.fillWithOutline(world, boundingBox, minX, maxY, z, minX, maxY, z, blockState, AIR, false);
            this.fillWithOutline(world, boundingBox, maxX, maxY, z, maxX, maxY, z, blockState, AIR, false);
        } else {
            this.fillWithOutline(world, boundingBox, minX, maxY, z, maxX, maxY, z, blockState, AIR, false);
            this.addBlockWithRandomThreshold(world, boundingBox, random, 0.05f, minX + 1, maxY, z - 1, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.SOUTH));
            this.addBlockWithRandomThreshold(world, boundingBox, random, 0.05f, minX + 1, maxY, z + 1, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.NORTH));
        }
    }

    private void addCobwebsUnderground(StructureWorldAccess world, BlockBox box, Random random, float threshold, int x, int y, int z) {
        if (this.isUnderSeaLevel(world, x, y, z, box) && random.nextFloat() < threshold && this.hasSolidNeighborBlocks(world, box, x, y, z, 2)) {
            this.addBlock(world, Blocks.COBWEB.getDefaultState(), x, y, z, box);
        }
    }

    private boolean hasSolidNeighborBlocks(StructureWorldAccess world, BlockBox box, int x, int y, int z, int count) {
        BlockPos.Mutable mutable = this.offsetPos(x, y, z);
        int i = 0;
        for (Direction direction : Direction.values()) {
            mutable.move(direction);
            if (box.contains(mutable) && world.getBlockState(mutable).isSideSolidFullSquare(world, mutable, direction.getOpposite()) && ++i >= count) {
                return true;
            }
            mutable.move(direction.getOpposite());
        }
        return false;
    }
}
