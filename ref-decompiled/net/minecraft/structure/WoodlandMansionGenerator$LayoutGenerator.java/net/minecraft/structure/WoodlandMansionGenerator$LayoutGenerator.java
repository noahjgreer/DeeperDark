/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.WoodlandMansionGenerator;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

static class WoodlandMansionGenerator.LayoutGenerator {
    private final StructureTemplateManager manager;
    private final Random random;
    private int entranceI;
    private int entranceJ;

    public WoodlandMansionGenerator.LayoutGenerator(StructureTemplateManager manager, Random random) {
        this.manager = manager;
        this.random = random;
    }

    public void generate(BlockPos pos, BlockRotation rotation, List<WoodlandMansionGenerator.Piece> pieces, WoodlandMansionGenerator.MansionParameters parameters) {
        int l;
        WoodlandMansionGenerator.GenerationPiece generationPiece = new WoodlandMansionGenerator.GenerationPiece();
        generationPiece.position = pos;
        generationPiece.rotation = rotation;
        generationPiece.template = "wall_flat";
        WoodlandMansionGenerator.GenerationPiece generationPiece2 = new WoodlandMansionGenerator.GenerationPiece();
        this.addEntrance(pieces, generationPiece);
        generationPiece2.position = generationPiece.position.up(8);
        generationPiece2.rotation = generationPiece.rotation;
        generationPiece2.template = "wall_window";
        if (!pieces.isEmpty()) {
            // empty if block
        }
        WoodlandMansionGenerator.FlagMatrix flagMatrix = parameters.baseLayout;
        WoodlandMansionGenerator.FlagMatrix flagMatrix2 = parameters.thirdFloorLayout;
        this.entranceI = parameters.entranceI + 1;
        this.entranceJ = parameters.entranceJ + 1;
        int i = parameters.entranceI + 1;
        int j = parameters.entranceJ;
        this.addOuterWall(pieces, generationPiece, flagMatrix, Direction.SOUTH, this.entranceI, this.entranceJ, i, j);
        this.addOuterWall(pieces, generationPiece2, flagMatrix, Direction.SOUTH, this.entranceI, this.entranceJ, i, j);
        WoodlandMansionGenerator.GenerationPiece generationPiece3 = new WoodlandMansionGenerator.GenerationPiece();
        generationPiece3.position = generationPiece.position.up(19);
        generationPiece3.rotation = generationPiece.rotation;
        generationPiece3.template = "wall_window";
        boolean bl = false;
        for (int k = 0; k < flagMatrix2.m && !bl; ++k) {
            for (l = flagMatrix2.n - 1; l >= 0 && !bl; --l) {
                if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(flagMatrix2, l, k)) continue;
                generationPiece3.position = generationPiece3.position.offset(rotation.rotate(Direction.SOUTH), 8 + (k - this.entranceJ) * 8);
                generationPiece3.position = generationPiece3.position.offset(rotation.rotate(Direction.EAST), (l - this.entranceI) * 8);
                this.addWallPiece(pieces, generationPiece3);
                this.addOuterWall(pieces, generationPiece3, flagMatrix2, Direction.SOUTH, l, k, l, k);
                bl = true;
            }
        }
        this.addRoof(pieces, pos.up(16), rotation, flagMatrix, flagMatrix2);
        this.addRoof(pieces, pos.up(27), rotation, flagMatrix2, null);
        if (!pieces.isEmpty()) {
            // empty if block
        }
        WoodlandMansionGenerator.RoomPool[] roomPools = new WoodlandMansionGenerator.RoomPool[]{new WoodlandMansionGenerator.FirstFloorRoomPool(), new WoodlandMansionGenerator.SecondFloorRoomPool(), new WoodlandMansionGenerator.ThirdFloorRoomPool()};
        for (l = 0; l < 3; ++l) {
            BlockPos blockPos = pos.up(8 * l + (l == 2 ? 3 : 0));
            WoodlandMansionGenerator.FlagMatrix flagMatrix3 = parameters.roomFlagsByFloor[l];
            WoodlandMansionGenerator.FlagMatrix flagMatrix4 = l == 2 ? flagMatrix2 : flagMatrix;
            String string = l == 0 ? "carpet_south_1" : "carpet_south_2";
            String string2 = l == 0 ? "carpet_west_1" : "carpet_west_2";
            for (int m = 0; m < flagMatrix4.m; ++m) {
                for (int n = 0; n < flagMatrix4.n; ++n) {
                    if (flagMatrix4.get(n, m) != 1) continue;
                    BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.SOUTH), 8 + (m - this.entranceJ) * 8);
                    blockPos2 = blockPos2.offset(rotation.rotate(Direction.EAST), (n - this.entranceI) * 8);
                    pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "corridor_floor", blockPos2, rotation));
                    if (flagMatrix4.get(n, m - 1) == 1 || (flagMatrix3.get(n, m - 1) & 0x800000) == 0x800000) {
                        pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "carpet_north", blockPos2.offset(rotation.rotate(Direction.EAST), 1).up(), rotation));
                    }
                    if (flagMatrix4.get(n + 1, m) == 1 || (flagMatrix3.get(n + 1, m) & 0x800000) == 0x800000) {
                        pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "carpet_east", blockPos2.offset(rotation.rotate(Direction.SOUTH), 1).offset(rotation.rotate(Direction.EAST), 5).up(), rotation));
                    }
                    if (flagMatrix4.get(n, m + 1) == 1 || (flagMatrix3.get(n, m + 1) & 0x800000) == 0x800000) {
                        pieces.add(new WoodlandMansionGenerator.Piece(this.manager, string, blockPos2.offset(rotation.rotate(Direction.SOUTH), 5).offset(rotation.rotate(Direction.WEST), 1), rotation));
                    }
                    if (flagMatrix4.get(n - 1, m) != 1 && (flagMatrix3.get(n - 1, m) & 0x800000) != 0x800000) continue;
                    pieces.add(new WoodlandMansionGenerator.Piece(this.manager, string2, blockPos2.offset(rotation.rotate(Direction.WEST), 1).offset(rotation.rotate(Direction.NORTH), 1), rotation));
                }
            }
            String string3 = l == 0 ? "indoors_wall_1" : "indoors_wall_2";
            String string4 = l == 0 ? "indoors_door_1" : "indoors_door_2";
            ArrayList list = Lists.newArrayList();
            for (int o = 0; o < flagMatrix4.m; ++o) {
                for (int p = 0; p < flagMatrix4.n; ++p) {
                    Direction direction3;
                    BlockPos blockPos4;
                    boolean bl2;
                    boolean bl3 = bl2 = l == 2 && flagMatrix4.get(p, o) == 3;
                    if (flagMatrix4.get(p, o) != 2 && !bl2) continue;
                    int q = flagMatrix3.get(p, o);
                    int r = q & 0xF0000;
                    int s = q & 0xFFFF;
                    bl2 = bl2 && (q & 0x800000) == 0x800000;
                    list.clear();
                    if ((q & 0x200000) == 0x200000) {
                        for (Direction direction : Direction.Type.HORIZONTAL) {
                            if (flagMatrix4.get(p + direction.getOffsetX(), o + direction.getOffsetZ()) != 1) continue;
                            list.add(direction);
                        }
                    }
                    Direction direction2 = null;
                    if (!list.isEmpty()) {
                        direction2 = (Direction)list.get(this.random.nextInt(list.size()));
                    } else if ((q & 0x100000) == 0x100000) {
                        direction2 = Direction.UP;
                    }
                    BlockPos blockPos3 = blockPos.offset(rotation.rotate(Direction.SOUTH), 8 + (o - this.entranceJ) * 8);
                    blockPos3 = blockPos3.offset(rotation.rotate(Direction.EAST), -1 + (p - this.entranceI) * 8);
                    if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(flagMatrix4, p - 1, o) && !parameters.isRoomId(flagMatrix4, p - 1, o, l, s)) {
                        pieces.add(new WoodlandMansionGenerator.Piece(this.manager, direction2 == Direction.WEST ? string4 : string3, blockPos3, rotation));
                    }
                    if (flagMatrix4.get(p + 1, o) == 1 && !bl2) {
                        blockPos4 = blockPos3.offset(rotation.rotate(Direction.EAST), 8);
                        pieces.add(new WoodlandMansionGenerator.Piece(this.manager, direction2 == Direction.EAST ? string4 : string3, blockPos4, rotation));
                    }
                    if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(flagMatrix4, p, o + 1) && !parameters.isRoomId(flagMatrix4, p, o + 1, l, s)) {
                        blockPos4 = blockPos3.offset(rotation.rotate(Direction.SOUTH), 7);
                        blockPos4 = blockPos4.offset(rotation.rotate(Direction.EAST), 7);
                        pieces.add(new WoodlandMansionGenerator.Piece(this.manager, direction2 == Direction.SOUTH ? string4 : string3, blockPos4, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                    }
                    if (flagMatrix4.get(p, o - 1) == 1 && !bl2) {
                        blockPos4 = blockPos3.offset(rotation.rotate(Direction.NORTH), 1);
                        blockPos4 = blockPos4.offset(rotation.rotate(Direction.EAST), 7);
                        pieces.add(new WoodlandMansionGenerator.Piece(this.manager, direction2 == Direction.NORTH ? string4 : string3, blockPos4, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                    }
                    if (r == 65536) {
                        this.addSmallRoom(pieces, blockPos3, rotation, direction2, roomPools[l]);
                        continue;
                    }
                    if (r == 131072 && direction2 != null) {
                        direction3 = parameters.findConnectedRoomDirection(flagMatrix4, p, o, l, s);
                        boolean bl32 = (q & 0x400000) == 0x400000;
                        this.addMediumRoom(pieces, blockPos3, rotation, direction3, direction2, roomPools[l], bl32);
                        continue;
                    }
                    if (r == 262144 && direction2 != null && direction2 != Direction.UP) {
                        direction3 = direction2.rotateYClockwise();
                        if (!parameters.isRoomId(flagMatrix4, p + direction3.getOffsetX(), o + direction3.getOffsetZ(), l, s)) {
                            direction3 = direction3.getOpposite();
                        }
                        this.addBigRoom(pieces, blockPos3, rotation, direction3, direction2, roomPools[l]);
                        continue;
                    }
                    if (r != 262144 || direction2 != Direction.UP) continue;
                    this.addBigSecretRoom(pieces, blockPos3, rotation, roomPools[l]);
                }
            }
        }
    }

    private void addOuterWall(List<WoodlandMansionGenerator.Piece> pieces, WoodlandMansionGenerator.GenerationPiece wallPiece, WoodlandMansionGenerator.FlagMatrix layout, Direction direction, int startI, int startJ, int endI, int endJ) {
        int i = startI;
        int j = startJ;
        Direction direction2 = direction;
        do {
            if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, i + direction.getOffsetX(), j + direction.getOffsetZ())) {
                this.turnLeft(pieces, wallPiece);
                direction = direction.rotateYClockwise();
                if (i == endI && j == endJ && direction2 == direction) continue;
                this.addWallPiece(pieces, wallPiece);
                continue;
            }
            if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, i + direction.getOffsetX(), j + direction.getOffsetZ()) && WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, i + direction.getOffsetX() + direction.rotateYCounterclockwise().getOffsetX(), j + direction.getOffsetZ() + direction.rotateYCounterclockwise().getOffsetZ())) {
                this.turnRight(pieces, wallPiece);
                i += direction.getOffsetX();
                j += direction.getOffsetZ();
                direction = direction.rotateYCounterclockwise();
                continue;
            }
            if ((i += direction.getOffsetX()) == endI && (j += direction.getOffsetZ()) == endJ && direction2 == direction) continue;
            this.addWallPiece(pieces, wallPiece);
        } while (i != endI || j != endJ || direction2 != direction);
    }

    private void addRoof(List<WoodlandMansionGenerator.Piece> pieces, BlockPos pos, BlockRotation rotation, WoodlandMansionGenerator.FlagMatrix layout,  @Nullable WoodlandMansionGenerator.FlagMatrix nextFloorLayout) {
        BlockPos blockPos2;
        boolean bl;
        BlockPos blockPos;
        int j;
        int i;
        for (i = 0; i < layout.m; ++i) {
            for (j = 0; j < layout.n; ++j) {
                blockPos = pos;
                blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 8 + (i - this.entranceJ) * 8);
                blockPos = blockPos.offset(rotation.rotate(Direction.EAST), (j - this.entranceI) * 8);
                boolean bl2 = bl = nextFloorLayout != null && WoodlandMansionGenerator.MansionParameters.isInsideMansion(nextFloorLayout, j, i);
                if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i) || bl) continue;
                pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof", blockPos.up(3), rotation));
                if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j + 1, i)) {
                    blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 6);
                    pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_front", blockPos2, rotation));
                }
                if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j - 1, i)) {
                    blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 0);
                    blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 7);
                    pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_front", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_180)));
                }
                if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i - 1)) {
                    blockPos2 = blockPos.offset(rotation.rotate(Direction.WEST), 1);
                    pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_front", blockPos2, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                }
                if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i + 1)) continue;
                blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 6);
                blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 6);
                pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_front", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_90)));
            }
        }
        if (nextFloorLayout != null) {
            for (i = 0; i < layout.m; ++i) {
                for (j = 0; j < layout.n; ++j) {
                    blockPos = pos;
                    blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 8 + (i - this.entranceJ) * 8);
                    blockPos = blockPos.offset(rotation.rotate(Direction.EAST), (j - this.entranceI) * 8);
                    bl = WoodlandMansionGenerator.MansionParameters.isInsideMansion(nextFloorLayout, j, i);
                    if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i) || !bl) continue;
                    if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j + 1, i)) {
                        blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 7);
                        pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall", blockPos2, rotation));
                    }
                    if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j - 1, i)) {
                        blockPos2 = blockPos.offset(rotation.rotate(Direction.WEST), 1);
                        blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 6);
                        pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_180)));
                    }
                    if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i - 1)) {
                        blockPos2 = blockPos.offset(rotation.rotate(Direction.WEST), 0);
                        blockPos2 = blockPos2.offset(rotation.rotate(Direction.NORTH), 1);
                        pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall", blockPos2, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                    }
                    if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i + 1)) {
                        blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 6);
                        blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 7);
                        pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                    }
                    if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j + 1, i)) {
                        if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i - 1)) {
                            blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 7);
                            blockPos2 = blockPos2.offset(rotation.rotate(Direction.NORTH), 2);
                            pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall_corner", blockPos2, rotation));
                        }
                        if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i + 1)) {
                            blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 8);
                            blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 7);
                            pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall_corner", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                        }
                    }
                    if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j - 1, i)) continue;
                    if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i - 1)) {
                        blockPos2 = blockPos.offset(rotation.rotate(Direction.WEST), 2);
                        blockPos2 = blockPos2.offset(rotation.rotate(Direction.NORTH), 1);
                        pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall_corner", blockPos2, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                    }
                    if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i + 1)) continue;
                    blockPos2 = blockPos.offset(rotation.rotate(Direction.WEST), 1);
                    blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 8);
                    pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "small_wall_corner", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_180)));
                }
            }
        }
        for (i = 0; i < layout.m; ++i) {
            for (j = 0; j < layout.n; ++j) {
                BlockPos blockPos3;
                blockPos = pos;
                blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 8 + (i - this.entranceJ) * 8);
                blockPos = blockPos.offset(rotation.rotate(Direction.EAST), (j - this.entranceI) * 8);
                boolean bl3 = bl = nextFloorLayout != null && WoodlandMansionGenerator.MansionParameters.isInsideMansion(nextFloorLayout, j, i);
                if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i) || bl) continue;
                if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j + 1, i)) {
                    blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 6);
                    if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i + 1)) {
                        blockPos3 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 6);
                        pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_corner", blockPos3, rotation));
                    } else if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j + 1, i + 1)) {
                        blockPos3 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 5);
                        pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_inner_corner", blockPos3, rotation));
                    }
                    if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i - 1)) {
                        pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_corner", blockPos2, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                    } else if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j + 1, i - 1)) {
                        blockPos3 = blockPos.offset(rotation.rotate(Direction.EAST), 9);
                        blockPos3 = blockPos3.offset(rotation.rotate(Direction.NORTH), 2);
                        pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_inner_corner", blockPos3, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                    }
                }
                if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j - 1, i)) continue;
                blockPos2 = blockPos.offset(rotation.rotate(Direction.EAST), 0);
                blockPos2 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 0);
                if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i + 1)) {
                    blockPos3 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 6);
                    pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_corner", blockPos3, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                } else if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j - 1, i + 1)) {
                    blockPos3 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 8);
                    blockPos3 = blockPos3.offset(rotation.rotate(Direction.WEST), 3);
                    pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_inner_corner", blockPos3, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                }
                if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i - 1)) {
                    pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_corner", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_180)));
                    continue;
                }
                if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j - 1, i - 1)) continue;
                blockPos3 = blockPos2.offset(rotation.rotate(Direction.SOUTH), 1);
                pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "roof_inner_corner", blockPos3, rotation.rotate(BlockRotation.CLOCKWISE_180)));
            }
        }
    }

    private void addEntrance(List<WoodlandMansionGenerator.Piece> pieces, WoodlandMansionGenerator.GenerationPiece wallPiece) {
        Direction direction = wallPiece.rotation.rotate(Direction.WEST);
        pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "entrance", wallPiece.position.offset(direction, 9), wallPiece.rotation));
        wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.SOUTH), 16);
    }

    private void addWallPiece(List<WoodlandMansionGenerator.Piece> pieces, WoodlandMansionGenerator.GenerationPiece wallPiece) {
        pieces.add(new WoodlandMansionGenerator.Piece(this.manager, wallPiece.template, wallPiece.position.offset(wallPiece.rotation.rotate(Direction.EAST), 7), wallPiece.rotation));
        wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.SOUTH), 8);
    }

    private void turnLeft(List<WoodlandMansionGenerator.Piece> pieces, WoodlandMansionGenerator.GenerationPiece wallPiece) {
        wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.SOUTH), -1);
        pieces.add(new WoodlandMansionGenerator.Piece(this.manager, "wall_corner", wallPiece.position, wallPiece.rotation));
        wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.SOUTH), -7);
        wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.WEST), -6);
        wallPiece.rotation = wallPiece.rotation.rotate(BlockRotation.CLOCKWISE_90);
    }

    private void turnRight(List<WoodlandMansionGenerator.Piece> pieces, WoodlandMansionGenerator.GenerationPiece wallPiece) {
        wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.SOUTH), 6);
        wallPiece.position = wallPiece.position.offset(wallPiece.rotation.rotate(Direction.EAST), 8);
        wallPiece.rotation = wallPiece.rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
    }

    private void addSmallRoom(List<WoodlandMansionGenerator.Piece> pieces, BlockPos pos, BlockRotation rotation, Direction direction, WoodlandMansionGenerator.RoomPool pool) {
        BlockRotation blockRotation = BlockRotation.NONE;
        String string = pool.getSmallRoom(this.random);
        if (direction != Direction.EAST) {
            if (direction == Direction.NORTH) {
                blockRotation = blockRotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
            } else if (direction == Direction.WEST) {
                blockRotation = blockRotation.rotate(BlockRotation.CLOCKWISE_180);
            } else if (direction == Direction.SOUTH) {
                blockRotation = blockRotation.rotate(BlockRotation.CLOCKWISE_90);
            } else {
                string = pool.getSmallSecretRoom(this.random);
            }
        }
        BlockPos blockPos = StructureTemplate.applyTransformedOffset(new BlockPos(1, 0, 0), BlockMirror.NONE, blockRotation, 7, 7);
        blockRotation = blockRotation.rotate(rotation);
        blockPos = blockPos.rotate(rotation);
        BlockPos blockPos2 = pos.add(blockPos.getX(), 0, blockPos.getZ());
        pieces.add(new WoodlandMansionGenerator.Piece(this.manager, string, blockPos2, blockRotation));
    }

    private void addMediumRoom(List<WoodlandMansionGenerator.Piece> pieces, BlockPos pos, BlockRotation rotation, Direction connectedRoomDirection, Direction entranceDirection, WoodlandMansionGenerator.RoomPool pool, boolean staircase) {
        if (entranceDirection == Direction.EAST && connectedRoomDirection == Direction.SOUTH) {
            BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
            pieces.add(new WoodlandMansionGenerator.Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation));
        } else if (entranceDirection == Direction.EAST && connectedRoomDirection == Direction.NORTH) {
            BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
            blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 6);
            pieces.add(new WoodlandMansionGenerator.Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation, BlockMirror.LEFT_RIGHT));
        } else if (entranceDirection == Direction.WEST && connectedRoomDirection == Direction.NORTH) {
            BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 7);
            blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 6);
            pieces.add(new WoodlandMansionGenerator.Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_180)));
        } else if (entranceDirection == Direction.WEST && connectedRoomDirection == Direction.SOUTH) {
            BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 7);
            pieces.add(new WoodlandMansionGenerator.Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation, BlockMirror.FRONT_BACK));
        } else if (entranceDirection == Direction.SOUTH && connectedRoomDirection == Direction.EAST) {
            BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
            pieces.add(new WoodlandMansionGenerator.Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_90), BlockMirror.LEFT_RIGHT));
        } else if (entranceDirection == Direction.SOUTH && connectedRoomDirection == Direction.WEST) {
            BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 7);
            pieces.add(new WoodlandMansionGenerator.Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_90)));
        } else if (entranceDirection == Direction.NORTH && connectedRoomDirection == Direction.WEST) {
            BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 7);
            blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 6);
            pieces.add(new WoodlandMansionGenerator.Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_90), BlockMirror.FRONT_BACK));
        } else if (entranceDirection == Direction.NORTH && connectedRoomDirection == Direction.EAST) {
            BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
            blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 6);
            pieces.add(new WoodlandMansionGenerator.Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
        } else if (entranceDirection == Direction.SOUTH && connectedRoomDirection == Direction.NORTH) {
            BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
            blockPos = blockPos.offset(rotation.rotate(Direction.NORTH), 8);
            pieces.add(new WoodlandMansionGenerator.Piece(this.manager, pool.getMediumGenericRoom(this.random, staircase), blockPos, rotation));
        } else if (entranceDirection == Direction.NORTH && connectedRoomDirection == Direction.SOUTH) {
            BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 7);
            blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 14);
            pieces.add(new WoodlandMansionGenerator.Piece(this.manager, pool.getMediumGenericRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_180)));
        } else if (entranceDirection == Direction.WEST && connectedRoomDirection == Direction.EAST) {
            BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 15);
            pieces.add(new WoodlandMansionGenerator.Piece(this.manager, pool.getMediumGenericRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_90)));
        } else if (entranceDirection == Direction.EAST && connectedRoomDirection == Direction.WEST) {
            BlockPos blockPos = pos.offset(rotation.rotate(Direction.WEST), 7);
            blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), 6);
            pieces.add(new WoodlandMansionGenerator.Piece(this.manager, pool.getMediumGenericRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
        } else if (entranceDirection == Direction.UP && connectedRoomDirection == Direction.EAST) {
            BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 15);
            pieces.add(new WoodlandMansionGenerator.Piece(this.manager, pool.getMediumSecretRoom(this.random), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_90)));
        } else if (entranceDirection == Direction.UP && connectedRoomDirection == Direction.SOUTH) {
            BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
            blockPos = blockPos.offset(rotation.rotate(Direction.NORTH), 0);
            pieces.add(new WoodlandMansionGenerator.Piece(this.manager, pool.getMediumSecretRoom(this.random), blockPos, rotation));
        }
    }

    private void addBigRoom(List<WoodlandMansionGenerator.Piece> pieces, BlockPos pos, BlockRotation rotation, Direction connectedRoomDirection, Direction entranceDirection, WoodlandMansionGenerator.RoomPool pool) {
        int i = 0;
        int j = 0;
        BlockRotation blockRotation = rotation;
        BlockMirror blockMirror = BlockMirror.NONE;
        if (entranceDirection == Direction.EAST && connectedRoomDirection == Direction.SOUTH) {
            i = -7;
        } else if (entranceDirection == Direction.EAST && connectedRoomDirection == Direction.NORTH) {
            i = -7;
            j = 6;
            blockMirror = BlockMirror.LEFT_RIGHT;
        } else if (entranceDirection == Direction.NORTH && connectedRoomDirection == Direction.EAST) {
            i = 1;
            j = 14;
            blockRotation = rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
        } else if (entranceDirection == Direction.NORTH && connectedRoomDirection == Direction.WEST) {
            i = 7;
            j = 14;
            blockRotation = rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
            blockMirror = BlockMirror.LEFT_RIGHT;
        } else if (entranceDirection == Direction.SOUTH && connectedRoomDirection == Direction.WEST) {
            i = 7;
            j = -8;
            blockRotation = rotation.rotate(BlockRotation.CLOCKWISE_90);
        } else if (entranceDirection == Direction.SOUTH && connectedRoomDirection == Direction.EAST) {
            i = 1;
            j = -8;
            blockRotation = rotation.rotate(BlockRotation.CLOCKWISE_90);
            blockMirror = BlockMirror.LEFT_RIGHT;
        } else if (entranceDirection == Direction.WEST && connectedRoomDirection == Direction.NORTH) {
            i = 15;
            j = 6;
            blockRotation = rotation.rotate(BlockRotation.CLOCKWISE_180);
        } else if (entranceDirection == Direction.WEST && connectedRoomDirection == Direction.SOUTH) {
            i = 15;
            blockMirror = BlockMirror.FRONT_BACK;
        }
        BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), i);
        blockPos = blockPos.offset(rotation.rotate(Direction.SOUTH), j);
        pieces.add(new WoodlandMansionGenerator.Piece(this.manager, pool.getBigRoom(this.random), blockPos, blockRotation, blockMirror));
    }

    private void addBigSecretRoom(List<WoodlandMansionGenerator.Piece> pieces, BlockPos pos, BlockRotation rotation, WoodlandMansionGenerator.RoomPool pool) {
        BlockPos blockPos = pos.offset(rotation.rotate(Direction.EAST), 1);
        pieces.add(new WoodlandMansionGenerator.Piece(this.manager, pool.getBigSecretRoom(this.random), blockPos, rotation, BlockMirror.NONE));
    }
}
