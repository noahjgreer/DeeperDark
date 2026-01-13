/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public static class OceanMonumentGenerator.Base
extends OceanMonumentGenerator.Piece {
    private static final int SIZE_X = 58;
    private static final int SIZE_Y = 22;
    private static final int SIZE_Z = 58;
    public static final int BIOME_CHECK_RADIUS = 29;
    private static final int field_31605 = 61;
    private OceanMonumentGenerator.PieceSetting entryPieceSetting;
    private OceanMonumentGenerator.PieceSetting coreRoomPieceSetting;
    private final List<OceanMonumentGenerator.Piece> children = Lists.newArrayList();

    public OceanMonumentGenerator.Base(Random random, int x, int z, Direction orientation) {
        super(StructurePieceType.OCEAN_MONUMENT_BASE, orientation, 0, OceanMonumentGenerator.Base.createBox(x, 39, z, orientation, 58, 23, 58));
        this.setOrientation(orientation);
        List<OceanMonumentGenerator.PieceSetting> list = this.setupPieces(random);
        this.entryPieceSetting.used = true;
        this.children.add(new OceanMonumentGenerator.Entry(orientation, this.entryPieceSetting));
        this.children.add(new OceanMonumentGenerator.CoreRoom(orientation, this.coreRoomPieceSetting));
        ArrayList list2 = Lists.newArrayList();
        list2.add(new OceanMonumentGenerator.DoubleXYRoomFactory());
        list2.add(new OceanMonumentGenerator.DoubleYZRoomFactory());
        list2.add(new OceanMonumentGenerator.DoubleZRoomFactory());
        list2.add(new OceanMonumentGenerator.DoubleXRoomFactory());
        list2.add(new OceanMonumentGenerator.DoubleYRoomFactory());
        list2.add(new OceanMonumentGenerator.SimpleRoomTopFactory());
        list2.add(new OceanMonumentGenerator.SimpleRoomFactory());
        block0: for (OceanMonumentGenerator.PieceSetting pieceSetting : list) {
            if (pieceSetting.used || pieceSetting.isAboveLevelThree()) continue;
            for (OceanMonumentGenerator.PieceFactory pieceFactory : list2) {
                if (!pieceFactory.canGenerate(pieceSetting)) continue;
                this.children.add(pieceFactory.generate(orientation, pieceSetting, random));
                continue block0;
            }
        }
        BlockPos.Mutable blockPos = this.offsetPos(9, 0, 22);
        for (OceanMonumentGenerator.Piece piece : this.children) {
            piece.getBoundingBox().move(blockPos);
        }
        BlockBox blockBox = BlockBox.create(this.offsetPos(1, 1, 1), this.offsetPos(23, 8, 21));
        BlockBox blockBox2 = BlockBox.create(this.offsetPos(34, 1, 1), this.offsetPos(56, 8, 21));
        BlockBox blockBox3 = BlockBox.create(this.offsetPos(22, 13, 22), this.offsetPos(35, 17, 35));
        int i = random.nextInt();
        this.children.add(new OceanMonumentGenerator.WingRoom(orientation, blockBox, i++));
        this.children.add(new OceanMonumentGenerator.WingRoom(orientation, blockBox2, i++));
        this.children.add(new OceanMonumentGenerator.Penthouse(orientation, blockBox3));
    }

    public OceanMonumentGenerator.Base(NbtCompound nbt) {
        super(StructurePieceType.OCEAN_MONUMENT_BASE, nbt);
    }

    private List<OceanMonumentGenerator.PieceSetting> setupPieces(Random random) {
        int o;
        int n;
        int m;
        int l;
        int k;
        int j;
        int i;
        OceanMonumentGenerator.PieceSetting[] pieceSettings = new OceanMonumentGenerator.PieceSetting[75];
        for (i = 0; i < 5; ++i) {
            for (j = 0; j < 4; ++j) {
                k = 0;
                l = OceanMonumentGenerator.Base.getIndex(i, 0, j);
                pieceSettings[l] = new OceanMonumentGenerator.PieceSetting(l);
            }
        }
        for (i = 0; i < 5; ++i) {
            for (j = 0; j < 4; ++j) {
                k = 1;
                l = OceanMonumentGenerator.Base.getIndex(i, 1, j);
                pieceSettings[l] = new OceanMonumentGenerator.PieceSetting(l);
            }
        }
        for (i = 1; i < 4; ++i) {
            for (j = 0; j < 2; ++j) {
                k = 2;
                l = OceanMonumentGenerator.Base.getIndex(i, 2, j);
                pieceSettings[l] = new OceanMonumentGenerator.PieceSetting(l);
            }
        }
        this.entryPieceSetting = pieceSettings[TWO_ZERO_ZERO_INDEX];
        for (i = 0; i < 5; ++i) {
            for (j = 0; j < 5; ++j) {
                for (k = 0; k < 3; ++k) {
                    l = OceanMonumentGenerator.Base.getIndex(i, k, j);
                    if (pieceSettings[l] == null) continue;
                    for (Direction direction : Direction.values()) {
                        int p;
                        m = i + direction.getOffsetX();
                        n = k + direction.getOffsetY();
                        o = j + direction.getOffsetZ();
                        if (m < 0 || m >= 5 || o < 0 || o >= 5 || n < 0 || n >= 3 || pieceSettings[p = OceanMonumentGenerator.Base.getIndex(m, n, o)] == null) continue;
                        if (o == j) {
                            pieceSettings[l].setNeighbor(direction, pieceSettings[p]);
                            continue;
                        }
                        pieceSettings[l].setNeighbor(direction.getOpposite(), pieceSettings[p]);
                    }
                }
            }
        }
        OceanMonumentGenerator.PieceSetting pieceSetting = new OceanMonumentGenerator.PieceSetting(1003);
        OceanMonumentGenerator.PieceSetting pieceSetting2 = new OceanMonumentGenerator.PieceSetting(1001);
        OceanMonumentGenerator.PieceSetting pieceSetting3 = new OceanMonumentGenerator.PieceSetting(1002);
        pieceSettings[TWO_TWO_ZERO_INDEX].setNeighbor(Direction.UP, pieceSetting);
        pieceSettings[ZERO_ONE_ZERO_INDEX].setNeighbor(Direction.SOUTH, pieceSetting2);
        pieceSettings[FOUR_ONE_ZERO_INDEX].setNeighbor(Direction.SOUTH, pieceSetting3);
        pieceSetting.used = true;
        pieceSetting2.used = true;
        pieceSetting3.used = true;
        this.entryPieceSetting.entrance = true;
        this.coreRoomPieceSetting = pieceSettings[OceanMonumentGenerator.Base.getIndex(random.nextInt(4), 0, 2)];
        this.coreRoomPieceSetting.used = true;
        this.coreRoomPieceSetting.neighbors[Direction.EAST.getIndex()].used = true;
        this.coreRoomPieceSetting.neighbors[Direction.NORTH.getIndex()].used = true;
        this.coreRoomPieceSetting.neighbors[Direction.EAST.getIndex()].neighbors[Direction.NORTH.getIndex()].used = true;
        this.coreRoomPieceSetting.neighbors[Direction.UP.getIndex()].used = true;
        this.coreRoomPieceSetting.neighbors[Direction.EAST.getIndex()].neighbors[Direction.UP.getIndex()].used = true;
        this.coreRoomPieceSetting.neighbors[Direction.NORTH.getIndex()].neighbors[Direction.UP.getIndex()].used = true;
        this.coreRoomPieceSetting.neighbors[Direction.EAST.getIndex()].neighbors[Direction.NORTH.getIndex()].neighbors[Direction.UP.getIndex()].used = true;
        ObjectArrayList objectArrayList = new ObjectArrayList();
        for (OceanMonumentGenerator.PieceSetting pieceSetting4 : pieceSettings) {
            if (pieceSetting4 == null) continue;
            pieceSetting4.checkNeighborStates();
            objectArrayList.add((Object)pieceSetting4);
        }
        pieceSetting.checkNeighborStates();
        Util.shuffle(objectArrayList, random);
        int q = 1;
        for (OceanMonumentGenerator.PieceSetting pieceSetting5 : objectArrayList) {
            int r = 0;
            for (m = 0; r < 2 && m < 5; ++m) {
                n = random.nextInt(6);
                if (!pieceSetting5.neighborPresences[n]) continue;
                o = Direction.byIndex(n).getOpposite().getIndex();
                pieceSetting5.neighborPresences[n] = false;
                pieceSetting5.neighbors[n].neighborPresences[o] = false;
                if (pieceSetting5.hasEntranceConnection(q++) && pieceSetting5.neighbors[n].hasEntranceConnection(q++)) {
                    ++r;
                    continue;
                }
                pieceSetting5.neighborPresences[n] = true;
                pieceSetting5.neighbors[n].neighborPresences[o] = true;
            }
        }
        objectArrayList.add((Object)pieceSetting);
        objectArrayList.add((Object)pieceSetting2);
        objectArrayList.add((Object)pieceSetting3);
        return objectArrayList;
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        int j;
        int i = Math.max(world.getSeaLevel(), 64) - this.boundingBox.getMinY();
        this.setAirAndWater(world, chunkBox, 0, 0, 0, 58, i, 58);
        this.generateWing(false, 0, world, random, chunkBox);
        this.generateWing(true, 33, world, random, chunkBox);
        this.generateEntranceArches(world, random, chunkBox);
        this.generateEntranceWall(world, random, chunkBox);
        this.generateRoof(world, random, chunkBox);
        this.generateLevelOne(world, random, chunkBox);
        this.generateLevelTwo(world, random, chunkBox);
        this.generateLevelThree(world, random, chunkBox);
        for (j = 0; j < 7; ++j) {
            int k = 0;
            while (k < 7) {
                if (k == 0 && j == 3) {
                    k = 6;
                }
                int l = j * 9;
                int m = k * 9;
                for (int n = 0; n < 4; ++n) {
                    for (int o = 0; o < 4; ++o) {
                        this.addBlock(world, PRISMARINE_BRICKS, l + n, 0, m + o, chunkBox);
                        this.fillDownwards(world, PRISMARINE_BRICKS, l + n, -1, m + o, chunkBox);
                    }
                }
                if (j == 0 || j == 6) {
                    ++k;
                    continue;
                }
                k += 6;
            }
        }
        for (j = 0; j < 5; ++j) {
            this.setAirAndWater(world, chunkBox, -1 - j, 0 + j * 2, -1 - j, -1 - j, 23, 58 + j);
            this.setAirAndWater(world, chunkBox, 58 + j, 0 + j * 2, -1 - j, 58 + j, 23, 58 + j);
            this.setAirAndWater(world, chunkBox, 0 - j, 0 + j * 2, -1 - j, 57 + j, 23, -1 - j);
            this.setAirAndWater(world, chunkBox, 0 - j, 0 + j * 2, 58 + j, 57 + j, 23, 58 + j);
        }
        for (OceanMonumentGenerator.Piece piece : this.children) {
            if (!piece.getBoundingBox().intersects(chunkBox)) continue;
            piece.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pivot);
        }
    }

    private void generateWing(boolean side, int start, StructureWorldAccess world, Random random, BlockBox box) {
        int i = 24;
        if (this.boxIntersects(box, start, 0, start + 23, 20)) {
            int l;
            int j;
            this.fillWithOutline(world, box, start + 0, 0, 0, start + 24, 0, 20, PRISMARINE, PRISMARINE, false);
            this.setAirAndWater(world, box, start + 0, 1, 0, start + 24, 10, 20);
            for (j = 0; j < 4; ++j) {
                this.fillWithOutline(world, box, start + j, j + 1, j, start + j, j + 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, start + j + 7, j + 5, j + 7, start + j + 7, j + 5, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, start + 17 - j, j + 5, j + 7, start + 17 - j, j + 5, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, start + 24 - j, j + 1, j, start + 24 - j, j + 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, start + j + 1, j + 1, j, start + 23 - j, j + 1, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, start + j + 8, j + 5, j + 7, start + 16 - j, j + 5, j + 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            this.fillWithOutline(world, box, start + 4, 4, 4, start + 6, 4, 20, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, start + 7, 4, 4, start + 17, 4, 6, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, start + 18, 4, 4, start + 20, 4, 20, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, start + 11, 8, 11, start + 13, 8, 20, PRISMARINE, PRISMARINE, false);
            this.addBlock(world, ALSO_PRISMARINE_BRICKS, start + 12, 9, 12, box);
            this.addBlock(world, ALSO_PRISMARINE_BRICKS, start + 12, 9, 15, box);
            this.addBlock(world, ALSO_PRISMARINE_BRICKS, start + 12, 9, 18, box);
            j = start + (side ? 19 : 5);
            int k = start + (side ? 5 : 19);
            for (l = 20; l >= 5; l -= 3) {
                this.addBlock(world, ALSO_PRISMARINE_BRICKS, j, 5, l, box);
            }
            for (l = 19; l >= 7; l -= 3) {
                this.addBlock(world, ALSO_PRISMARINE_BRICKS, k, 5, l, box);
            }
            for (l = 0; l < 4; ++l) {
                int m = side ? start + 24 - (17 - l * 3) : start + 17 - l * 3;
                this.addBlock(world, ALSO_PRISMARINE_BRICKS, m, 5, 5, box);
            }
            this.addBlock(world, ALSO_PRISMARINE_BRICKS, k, 5, 5, box);
            this.fillWithOutline(world, box, start + 11, 1, 12, start + 13, 7, 12, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, start + 12, 1, 11, start + 12, 7, 13, PRISMARINE, PRISMARINE, false);
        }
    }

    private void generateEntranceArches(StructureWorldAccess world, Random random, BlockBox box) {
        if (this.boxIntersects(box, 22, 5, 35, 17)) {
            this.setAirAndWater(world, box, 25, 0, 0, 32, 8, 20);
            for (int i = 0; i < 4; ++i) {
                this.fillWithOutline(world, box, 24, 2, 5 + i * 4, 24, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 22, 4, 5 + i * 4, 23, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.addBlock(world, PRISMARINE_BRICKS, 25, 5, 5 + i * 4, box);
                this.addBlock(world, PRISMARINE_BRICKS, 26, 6, 5 + i * 4, box);
                this.addBlock(world, SEA_LANTERN, 26, 5, 5 + i * 4, box);
                this.fillWithOutline(world, box, 33, 2, 5 + i * 4, 33, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 34, 4, 5 + i * 4, 35, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.addBlock(world, PRISMARINE_BRICKS, 32, 5, 5 + i * 4, box);
                this.addBlock(world, PRISMARINE_BRICKS, 31, 6, 5 + i * 4, box);
                this.addBlock(world, SEA_LANTERN, 31, 5, 5 + i * 4, box);
                this.fillWithOutline(world, box, 27, 6, 5 + i * 4, 30, 6, 5 + i * 4, PRISMARINE, PRISMARINE, false);
            }
        }
    }

    private void generateEntranceWall(StructureWorldAccess world, Random random, BlockBox box) {
        if (this.boxIntersects(box, 15, 20, 42, 21)) {
            int i;
            this.fillWithOutline(world, box, 15, 0, 21, 42, 0, 21, PRISMARINE, PRISMARINE, false);
            this.setAirAndWater(world, box, 26, 1, 21, 31, 3, 21);
            this.fillWithOutline(world, box, 21, 12, 21, 36, 12, 21, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 17, 11, 21, 40, 11, 21, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 16, 10, 21, 41, 10, 21, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 15, 7, 21, 42, 9, 21, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 16, 6, 21, 41, 6, 21, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 17, 5, 21, 40, 5, 21, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 21, 4, 21, 36, 4, 21, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 22, 3, 21, 26, 3, 21, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 31, 3, 21, 35, 3, 21, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 23, 2, 21, 25, 2, 21, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 32, 2, 21, 34, 2, 21, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 28, 4, 20, 29, 4, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.addBlock(world, PRISMARINE_BRICKS, 27, 3, 21, box);
            this.addBlock(world, PRISMARINE_BRICKS, 30, 3, 21, box);
            this.addBlock(world, PRISMARINE_BRICKS, 26, 2, 21, box);
            this.addBlock(world, PRISMARINE_BRICKS, 31, 2, 21, box);
            this.addBlock(world, PRISMARINE_BRICKS, 25, 1, 21, box);
            this.addBlock(world, PRISMARINE_BRICKS, 32, 1, 21, box);
            for (i = 0; i < 7; ++i) {
                this.addBlock(world, DARK_PRISMARINE, 28 - i, 6 + i, 21, box);
                this.addBlock(world, DARK_PRISMARINE, 29 + i, 6 + i, 21, box);
            }
            for (i = 0; i < 4; ++i) {
                this.addBlock(world, DARK_PRISMARINE, 28 - i, 9 + i, 21, box);
                this.addBlock(world, DARK_PRISMARINE, 29 + i, 9 + i, 21, box);
            }
            this.addBlock(world, DARK_PRISMARINE, 28, 12, 21, box);
            this.addBlock(world, DARK_PRISMARINE, 29, 12, 21, box);
            for (i = 0; i < 3; ++i) {
                this.addBlock(world, DARK_PRISMARINE, 22 - i * 2, 8, 21, box);
                this.addBlock(world, DARK_PRISMARINE, 22 - i * 2, 9, 21, box);
                this.addBlock(world, DARK_PRISMARINE, 35 + i * 2, 8, 21, box);
                this.addBlock(world, DARK_PRISMARINE, 35 + i * 2, 9, 21, box);
            }
            this.setAirAndWater(world, box, 15, 13, 21, 42, 15, 21);
            this.setAirAndWater(world, box, 15, 1, 21, 15, 6, 21);
            this.setAirAndWater(world, box, 16, 1, 21, 16, 5, 21);
            this.setAirAndWater(world, box, 17, 1, 21, 20, 4, 21);
            this.setAirAndWater(world, box, 21, 1, 21, 21, 3, 21);
            this.setAirAndWater(world, box, 22, 1, 21, 22, 2, 21);
            this.setAirAndWater(world, box, 23, 1, 21, 24, 1, 21);
            this.setAirAndWater(world, box, 42, 1, 21, 42, 6, 21);
            this.setAirAndWater(world, box, 41, 1, 21, 41, 5, 21);
            this.setAirAndWater(world, box, 37, 1, 21, 40, 4, 21);
            this.setAirAndWater(world, box, 36, 1, 21, 36, 3, 21);
            this.setAirAndWater(world, box, 33, 1, 21, 34, 1, 21);
            this.setAirAndWater(world, box, 35, 1, 21, 35, 2, 21);
        }
    }

    private void generateRoof(StructureWorldAccess world, Random random, BlockBox box) {
        if (this.boxIntersects(box, 21, 21, 36, 36)) {
            this.fillWithOutline(world, box, 21, 0, 22, 36, 0, 36, PRISMARINE, PRISMARINE, false);
            this.setAirAndWater(world, box, 21, 1, 22, 36, 23, 36);
            for (int i = 0; i < 4; ++i) {
                this.fillWithOutline(world, box, 21 + i, 13 + i, 21 + i, 36 - i, 13 + i, 21 + i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 21 + i, 13 + i, 36 - i, 36 - i, 13 + i, 36 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 21 + i, 13 + i, 22 + i, 21 + i, 13 + i, 35 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, box, 36 - i, 13 + i, 22 + i, 36 - i, 13 + i, 35 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            this.fillWithOutline(world, box, 25, 16, 25, 32, 16, 32, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 25, 17, 25, 25, 19, 25, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 32, 17, 25, 32, 19, 25, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 25, 17, 32, 25, 19, 32, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 32, 17, 32, 32, 19, 32, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.addBlock(world, PRISMARINE_BRICKS, 26, 20, 26, box);
            this.addBlock(world, PRISMARINE_BRICKS, 27, 21, 27, box);
            this.addBlock(world, SEA_LANTERN, 27, 20, 27, box);
            this.addBlock(world, PRISMARINE_BRICKS, 26, 20, 31, box);
            this.addBlock(world, PRISMARINE_BRICKS, 27, 21, 30, box);
            this.addBlock(world, SEA_LANTERN, 27, 20, 30, box);
            this.addBlock(world, PRISMARINE_BRICKS, 31, 20, 31, box);
            this.addBlock(world, PRISMARINE_BRICKS, 30, 21, 30, box);
            this.addBlock(world, SEA_LANTERN, 30, 20, 30, box);
            this.addBlock(world, PRISMARINE_BRICKS, 31, 20, 26, box);
            this.addBlock(world, PRISMARINE_BRICKS, 30, 21, 27, box);
            this.addBlock(world, SEA_LANTERN, 30, 20, 27, box);
            this.fillWithOutline(world, box, 28, 21, 27, 29, 21, 27, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 27, 21, 28, 27, 21, 29, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 28, 21, 30, 29, 21, 30, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 30, 21, 28, 30, 21, 29, PRISMARINE, PRISMARINE, false);
        }
    }

    private void generateLevelOne(StructureWorldAccess world, Random random, BlockBox box) {
        int i;
        if (this.boxIntersects(box, 0, 21, 6, 58)) {
            this.fillWithOutline(world, box, 0, 0, 21, 6, 0, 57, PRISMARINE, PRISMARINE, false);
            this.setAirAndWater(world, box, 0, 1, 21, 6, 7, 57);
            this.fillWithOutline(world, box, 4, 4, 21, 6, 4, 53, PRISMARINE, PRISMARINE, false);
            for (i = 0; i < 4; ++i) {
                this.fillWithOutline(world, box, i, i + 1, 21, i, i + 1, 57 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            for (i = 23; i < 53; i += 3) {
                this.addBlock(world, ALSO_PRISMARINE_BRICKS, 5, 5, i, box);
            }
            this.addBlock(world, ALSO_PRISMARINE_BRICKS, 5, 5, 52, box);
            for (i = 0; i < 4; ++i) {
                this.fillWithOutline(world, box, i, i + 1, 21, i, i + 1, 57 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            this.fillWithOutline(world, box, 4, 1, 52, 6, 3, 52, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 5, 1, 51, 5, 3, 53, PRISMARINE, PRISMARINE, false);
        }
        if (this.boxIntersects(box, 51, 21, 58, 58)) {
            this.fillWithOutline(world, box, 51, 0, 21, 57, 0, 57, PRISMARINE, PRISMARINE, false);
            this.setAirAndWater(world, box, 51, 1, 21, 57, 7, 57);
            this.fillWithOutline(world, box, 51, 4, 21, 53, 4, 53, PRISMARINE, PRISMARINE, false);
            for (i = 0; i < 4; ++i) {
                this.fillWithOutline(world, box, 57 - i, i + 1, 21, 57 - i, i + 1, 57 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            for (i = 23; i < 53; i += 3) {
                this.addBlock(world, ALSO_PRISMARINE_BRICKS, 52, 5, i, box);
            }
            this.addBlock(world, ALSO_PRISMARINE_BRICKS, 52, 5, 52, box);
            this.fillWithOutline(world, box, 51, 1, 52, 53, 3, 52, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 52, 1, 51, 52, 3, 53, PRISMARINE, PRISMARINE, false);
        }
        if (this.boxIntersects(box, 0, 51, 57, 57)) {
            this.fillWithOutline(world, box, 7, 0, 51, 50, 0, 57, PRISMARINE, PRISMARINE, false);
            this.setAirAndWater(world, box, 7, 1, 51, 50, 10, 57);
            for (i = 0; i < 4; ++i) {
                this.fillWithOutline(world, box, i + 1, i + 1, 57 - i, 56 - i, i + 1, 57 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
        }
    }

    private void generateLevelTwo(StructureWorldAccess world, Random random, BlockBox box) {
        int i;
        if (this.boxIntersects(box, 7, 21, 13, 50)) {
            this.fillWithOutline(world, box, 7, 0, 21, 13, 0, 50, PRISMARINE, PRISMARINE, false);
            this.setAirAndWater(world, box, 7, 1, 21, 13, 10, 50);
            this.fillWithOutline(world, box, 11, 8, 21, 13, 8, 53, PRISMARINE, PRISMARINE, false);
            for (i = 0; i < 4; ++i) {
                this.fillWithOutline(world, box, i + 7, i + 5, 21, i + 7, i + 5, 54, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            for (i = 21; i <= 45; i += 3) {
                this.addBlock(world, ALSO_PRISMARINE_BRICKS, 12, 9, i, box);
            }
        }
        if (this.boxIntersects(box, 44, 21, 50, 54)) {
            this.fillWithOutline(world, box, 44, 0, 21, 50, 0, 50, PRISMARINE, PRISMARINE, false);
            this.setAirAndWater(world, box, 44, 1, 21, 50, 10, 50);
            this.fillWithOutline(world, box, 44, 8, 21, 46, 8, 53, PRISMARINE, PRISMARINE, false);
            for (i = 0; i < 4; ++i) {
                this.fillWithOutline(world, box, 50 - i, i + 5, 21, 50 - i, i + 5, 54, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            for (i = 21; i <= 45; i += 3) {
                this.addBlock(world, ALSO_PRISMARINE_BRICKS, 45, 9, i, box);
            }
        }
        if (this.boxIntersects(box, 8, 44, 49, 54)) {
            this.fillWithOutline(world, box, 14, 0, 44, 43, 0, 50, PRISMARINE, PRISMARINE, false);
            this.setAirAndWater(world, box, 14, 1, 44, 43, 10, 50);
            for (i = 12; i <= 45; i += 3) {
                this.addBlock(world, ALSO_PRISMARINE_BRICKS, i, 9, 45, box);
                this.addBlock(world, ALSO_PRISMARINE_BRICKS, i, 9, 52, box);
                if (i != 12 && i != 18 && i != 24 && i != 33 && i != 39 && i != 45) continue;
                this.addBlock(world, ALSO_PRISMARINE_BRICKS, i, 9, 47, box);
                this.addBlock(world, ALSO_PRISMARINE_BRICKS, i, 9, 50, box);
                this.addBlock(world, ALSO_PRISMARINE_BRICKS, i, 10, 45, box);
                this.addBlock(world, ALSO_PRISMARINE_BRICKS, i, 10, 46, box);
                this.addBlock(world, ALSO_PRISMARINE_BRICKS, i, 10, 51, box);
                this.addBlock(world, ALSO_PRISMARINE_BRICKS, i, 10, 52, box);
                this.addBlock(world, ALSO_PRISMARINE_BRICKS, i, 11, 47, box);
                this.addBlock(world, ALSO_PRISMARINE_BRICKS, i, 11, 50, box);
                this.addBlock(world, ALSO_PRISMARINE_BRICKS, i, 12, 48, box);
                this.addBlock(world, ALSO_PRISMARINE_BRICKS, i, 12, 49, box);
            }
            for (i = 0; i < 3; ++i) {
                this.fillWithOutline(world, box, 8 + i, 5 + i, 54, 49 - i, 5 + i, 54, PRISMARINE, PRISMARINE, false);
            }
            this.fillWithOutline(world, box, 11, 8, 54, 46, 8, 54, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, 14, 8, 44, 43, 8, 53, PRISMARINE, PRISMARINE, false);
        }
    }

    private void generateLevelThree(StructureWorldAccess world, Random random, BlockBox box) {
        int i;
        if (this.boxIntersects(box, 14, 21, 20, 43)) {
            this.fillWithOutline(world, box, 14, 0, 21, 20, 0, 43, PRISMARINE, PRISMARINE, false);
            this.setAirAndWater(world, box, 14, 1, 22, 20, 14, 43);
            this.fillWithOutline(world, box, 18, 12, 22, 20, 12, 39, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 18, 12, 21, 20, 12, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            for (i = 0; i < 4; ++i) {
                this.fillWithOutline(world, box, i + 14, i + 9, 21, i + 14, i + 9, 43 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            for (i = 23; i <= 39; i += 3) {
                this.addBlock(world, ALSO_PRISMARINE_BRICKS, 19, 13, i, box);
            }
        }
        if (this.boxIntersects(box, 37, 21, 43, 43)) {
            this.fillWithOutline(world, box, 37, 0, 21, 43, 0, 43, PRISMARINE, PRISMARINE, false);
            this.setAirAndWater(world, box, 37, 1, 22, 43, 14, 43);
            this.fillWithOutline(world, box, 37, 12, 22, 39, 12, 39, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, 37, 12, 21, 39, 12, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            for (i = 0; i < 4; ++i) {
                this.fillWithOutline(world, box, 43 - i, i + 9, 21, 43 - i, i + 9, 43 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            for (i = 23; i <= 39; i += 3) {
                this.addBlock(world, ALSO_PRISMARINE_BRICKS, 38, 13, i, box);
            }
        }
        if (this.boxIntersects(box, 15, 37, 42, 43)) {
            this.fillWithOutline(world, box, 21, 0, 37, 36, 0, 43, PRISMARINE, PRISMARINE, false);
            this.setAirAndWater(world, box, 21, 1, 37, 36, 14, 43);
            this.fillWithOutline(world, box, 21, 12, 37, 36, 12, 39, PRISMARINE, PRISMARINE, false);
            for (i = 0; i < 4; ++i) {
                this.fillWithOutline(world, box, 15 + i, i + 9, 43 - i, 42 - i, i + 9, 43 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            for (i = 21; i <= 36; i += 3) {
                this.addBlock(world, ALSO_PRISMARINE_BRICKS, i, 13, 38, box);
            }
        }
    }
}
