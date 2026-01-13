/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import net.minecraft.structure.WoodlandMansionGenerator;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

static class WoodlandMansionGenerator.MansionParameters {
    private static final int SIZE = 11;
    private static final int UNSET = 0;
    private static final int CORRIDOR = 1;
    private static final int ROOM = 2;
    private static final int STAIRCASE = 3;
    private static final int UNUSED = 4;
    private static final int OUTSIDE = 5;
    private static final int SMALL_ROOM_FLAG = 65536;
    private static final int MEDIUM_ROOM_FLAG = 131072;
    private static final int BIG_ROOM_FLAG = 262144;
    private static final int ORIGIN_CELL_FLAG = 0x100000;
    private static final int ENTRANCE_CELL_FLAG = 0x200000;
    private static final int STAIRCASE_CELL_FLAG = 0x400000;
    private static final int CARPET_CELL_FLAG = 0x800000;
    private static final int ROOM_SIZE_MASK = 983040;
    private static final int ROOM_ID_MASK = 65535;
    private final Random random;
    final WoodlandMansionGenerator.FlagMatrix baseLayout;
    final WoodlandMansionGenerator.FlagMatrix thirdFloorLayout;
    final WoodlandMansionGenerator.FlagMatrix[] roomFlagsByFloor;
    final int entranceI;
    final int entranceJ;

    public WoodlandMansionGenerator.MansionParameters(Random random) {
        this.random = random;
        int i = 11;
        this.entranceI = 7;
        this.entranceJ = 4;
        this.baseLayout = new WoodlandMansionGenerator.FlagMatrix(11, 11, 5);
        this.baseLayout.fill(this.entranceI, this.entranceJ, this.entranceI + 1, this.entranceJ + 1, 3);
        this.baseLayout.fill(this.entranceI - 1, this.entranceJ, this.entranceI - 1, this.entranceJ + 1, 2);
        this.baseLayout.fill(this.entranceI + 2, this.entranceJ - 2, this.entranceI + 3, this.entranceJ + 3, 5);
        this.baseLayout.fill(this.entranceI + 1, this.entranceJ - 2, this.entranceI + 1, this.entranceJ - 1, 1);
        this.baseLayout.fill(this.entranceI + 1, this.entranceJ + 2, this.entranceI + 1, this.entranceJ + 3, 1);
        this.baseLayout.set(this.entranceI - 1, this.entranceJ - 1, 1);
        this.baseLayout.set(this.entranceI - 1, this.entranceJ + 2, 1);
        this.baseLayout.fill(0, 0, 11, 1, 5);
        this.baseLayout.fill(0, 9, 11, 11, 5);
        this.layoutCorridor(this.baseLayout, this.entranceI, this.entranceJ - 2, Direction.WEST, 6);
        this.layoutCorridor(this.baseLayout, this.entranceI, this.entranceJ + 3, Direction.WEST, 6);
        this.layoutCorridor(this.baseLayout, this.entranceI - 2, this.entranceJ - 1, Direction.WEST, 3);
        this.layoutCorridor(this.baseLayout, this.entranceI - 2, this.entranceJ + 2, Direction.WEST, 3);
        while (this.adjustLayoutWithRooms(this.baseLayout)) {
        }
        this.roomFlagsByFloor = new WoodlandMansionGenerator.FlagMatrix[3];
        this.roomFlagsByFloor[0] = new WoodlandMansionGenerator.FlagMatrix(11, 11, 5);
        this.roomFlagsByFloor[1] = new WoodlandMansionGenerator.FlagMatrix(11, 11, 5);
        this.roomFlagsByFloor[2] = new WoodlandMansionGenerator.FlagMatrix(11, 11, 5);
        this.updateRoomFlags(this.baseLayout, this.roomFlagsByFloor[0]);
        this.updateRoomFlags(this.baseLayout, this.roomFlagsByFloor[1]);
        this.roomFlagsByFloor[0].fill(this.entranceI + 1, this.entranceJ, this.entranceI + 1, this.entranceJ + 1, 0x800000);
        this.roomFlagsByFloor[1].fill(this.entranceI + 1, this.entranceJ, this.entranceI + 1, this.entranceJ + 1, 0x800000);
        this.thirdFloorLayout = new WoodlandMansionGenerator.FlagMatrix(this.baseLayout.n, this.baseLayout.m, 5);
        this.layoutThirdFloor();
        this.updateRoomFlags(this.thirdFloorLayout, this.roomFlagsByFloor[2]);
    }

    public static boolean isInsideMansion(WoodlandMansionGenerator.FlagMatrix layout, int i, int j) {
        int k = layout.get(i, j);
        return k == 1 || k == 2 || k == 3 || k == 4;
    }

    public boolean isRoomId(WoodlandMansionGenerator.FlagMatrix layout, int i, int j, int floor, int roomId) {
        return (this.roomFlagsByFloor[floor].get(i, j) & 0xFFFF) == roomId;
    }

    public @Nullable Direction findConnectedRoomDirection(WoodlandMansionGenerator.FlagMatrix layout, int i, int j, int floor, int roomId) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            if (!this.isRoomId(layout, i + direction.getOffsetX(), j + direction.getOffsetZ(), floor, roomId)) continue;
            return direction;
        }
        return null;
    }

    private void layoutCorridor(WoodlandMansionGenerator.FlagMatrix layout, int i, int j, Direction direction, int length) {
        Direction direction2;
        if (length <= 0) {
            return;
        }
        layout.set(i, j, 1);
        layout.update(i + direction.getOffsetX(), j + direction.getOffsetZ(), 0, 1);
        for (int k = 0; k < 8; ++k) {
            direction2 = Direction.fromHorizontalQuarterTurns(this.random.nextInt(4));
            if (direction2 == direction.getOpposite() || direction2 == Direction.EAST && this.random.nextBoolean()) continue;
            int l = i + direction.getOffsetX();
            int m = j + direction.getOffsetZ();
            if (layout.get(l + direction2.getOffsetX(), m + direction2.getOffsetZ()) != 0 || layout.get(l + direction2.getOffsetX() * 2, m + direction2.getOffsetZ() * 2) != 0) continue;
            this.layoutCorridor(layout, i + direction.getOffsetX() + direction2.getOffsetX(), j + direction.getOffsetZ() + direction2.getOffsetZ(), direction2, length - 1);
            break;
        }
        Direction direction3 = direction.rotateYClockwise();
        direction2 = direction.rotateYCounterclockwise();
        layout.update(i + direction3.getOffsetX(), j + direction3.getOffsetZ(), 0, 2);
        layout.update(i + direction2.getOffsetX(), j + direction2.getOffsetZ(), 0, 2);
        layout.update(i + direction.getOffsetX() + direction3.getOffsetX(), j + direction.getOffsetZ() + direction3.getOffsetZ(), 0, 2);
        layout.update(i + direction.getOffsetX() + direction2.getOffsetX(), j + direction.getOffsetZ() + direction2.getOffsetZ(), 0, 2);
        layout.update(i + direction.getOffsetX() * 2, j + direction.getOffsetZ() * 2, 0, 2);
        layout.update(i + direction3.getOffsetX() * 2, j + direction3.getOffsetZ() * 2, 0, 2);
        layout.update(i + direction2.getOffsetX() * 2, j + direction2.getOffsetZ() * 2, 0, 2);
    }

    private boolean adjustLayoutWithRooms(WoodlandMansionGenerator.FlagMatrix layout) {
        boolean bl = false;
        for (int i = 0; i < layout.m; ++i) {
            for (int j = 0; j < layout.n; ++j) {
                if (layout.get(j, i) != 0) continue;
                int k = 0;
                k += WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j + 1, i) ? 1 : 0;
                k += WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j - 1, i) ? 1 : 0;
                k += WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i + 1) ? 1 : 0;
                if ((k += WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i - 1) ? 1 : 0) >= 3) {
                    layout.set(j, i, 2);
                    bl = true;
                    continue;
                }
                if (k != 2) continue;
                int l = 0;
                l += WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j + 1, i + 1) ? 1 : 0;
                l += WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j - 1, i + 1) ? 1 : 0;
                l += WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j + 1, i - 1) ? 1 : 0;
                if ((l += WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j - 1, i - 1) ? 1 : 0) > 1) continue;
                layout.set(j, i, 2);
                bl = true;
            }
        }
        return bl;
    }

    private void layoutThirdFloor() {
        int l;
        int j;
        ArrayList list = Lists.newArrayList();
        WoodlandMansionGenerator.FlagMatrix flagMatrix = this.roomFlagsByFloor[1];
        for (int i = 0; i < this.thirdFloorLayout.m; ++i) {
            for (j = 0; j < this.thirdFloorLayout.n; ++j) {
                int k = flagMatrix.get(j, i);
                l = k & 0xF0000;
                if (l != 131072 || (k & 0x200000) != 0x200000) continue;
                list.add(new Pair<Integer, Integer>(j, i));
            }
        }
        if (list.isEmpty()) {
            this.thirdFloorLayout.fill(0, 0, this.thirdFloorLayout.n, this.thirdFloorLayout.m, 5);
            return;
        }
        Pair pair = (Pair)list.get(this.random.nextInt(list.size()));
        j = flagMatrix.get((Integer)pair.getLeft(), (Integer)pair.getRight());
        flagMatrix.set((Integer)pair.getLeft(), (Integer)pair.getRight(), j | 0x400000);
        Direction direction = this.findConnectedRoomDirection(this.baseLayout, (Integer)pair.getLeft(), (Integer)pair.getRight(), 1, j & 0xFFFF);
        l = (Integer)pair.getLeft() + direction.getOffsetX();
        int m = (Integer)pair.getRight() + direction.getOffsetZ();
        for (int n = 0; n < this.thirdFloorLayout.m; ++n) {
            for (int o = 0; o < this.thirdFloorLayout.n; ++o) {
                if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(this.baseLayout, o, n)) {
                    this.thirdFloorLayout.set(o, n, 5);
                    continue;
                }
                if (o == (Integer)pair.getLeft() && n == (Integer)pair.getRight()) {
                    this.thirdFloorLayout.set(o, n, 3);
                    continue;
                }
                if (o != l || n != m) continue;
                this.thirdFloorLayout.set(o, n, 3);
                this.roomFlagsByFloor[2].set(o, n, 0x800000);
            }
        }
        ArrayList list2 = Lists.newArrayList();
        for (Direction direction2 : Direction.Type.HORIZONTAL) {
            if (this.thirdFloorLayout.get(l + direction2.getOffsetX(), m + direction2.getOffsetZ()) != 0) continue;
            list2.add(direction2);
        }
        if (list2.isEmpty()) {
            this.thirdFloorLayout.fill(0, 0, this.thirdFloorLayout.n, this.thirdFloorLayout.m, 5);
            flagMatrix.set((Integer)pair.getLeft(), (Integer)pair.getRight(), j);
            return;
        }
        Direction direction3 = (Direction)list2.get(this.random.nextInt(list2.size()));
        this.layoutCorridor(this.thirdFloorLayout, l + direction3.getOffsetX(), m + direction3.getOffsetZ(), direction3, 4);
        while (this.adjustLayoutWithRooms(this.thirdFloorLayout)) {
        }
    }

    private void updateRoomFlags(WoodlandMansionGenerator.FlagMatrix layout, WoodlandMansionGenerator.FlagMatrix roomFlags) {
        int i;
        ObjectArrayList objectArrayList = new ObjectArrayList();
        for (i = 0; i < layout.m; ++i) {
            for (int j = 0; j < layout.n; ++j) {
                if (layout.get(j, i) != 2) continue;
                objectArrayList.add(new Pair<Integer, Integer>(j, i));
            }
        }
        Util.shuffle(objectArrayList, this.random);
        i = 10;
        for (Pair pair : objectArrayList) {
            int l;
            int k = (Integer)pair.getLeft();
            if (roomFlags.get(k, l = ((Integer)pair.getRight()).intValue()) != 0) continue;
            int m = k;
            int n = k;
            int o = l;
            int p = l;
            int q = 65536;
            if (roomFlags.get(k + 1, l) == 0 && roomFlags.get(k, l + 1) == 0 && roomFlags.get(k + 1, l + 1) == 0 && layout.get(k + 1, l) == 2 && layout.get(k, l + 1) == 2 && layout.get(k + 1, l + 1) == 2) {
                ++n;
                ++p;
                q = 262144;
            } else if (roomFlags.get(k - 1, l) == 0 && roomFlags.get(k, l + 1) == 0 && roomFlags.get(k - 1, l + 1) == 0 && layout.get(k - 1, l) == 2 && layout.get(k, l + 1) == 2 && layout.get(k - 1, l + 1) == 2) {
                --m;
                ++p;
                q = 262144;
            } else if (roomFlags.get(k - 1, l) == 0 && roomFlags.get(k, l - 1) == 0 && roomFlags.get(k - 1, l - 1) == 0 && layout.get(k - 1, l) == 2 && layout.get(k, l - 1) == 2 && layout.get(k - 1, l - 1) == 2) {
                --m;
                --o;
                q = 262144;
            } else if (roomFlags.get(k + 1, l) == 0 && layout.get(k + 1, l) == 2) {
                ++n;
                q = 131072;
            } else if (roomFlags.get(k, l + 1) == 0 && layout.get(k, l + 1) == 2) {
                ++p;
                q = 131072;
            } else if (roomFlags.get(k - 1, l) == 0 && layout.get(k - 1, l) == 2) {
                --m;
                q = 131072;
            } else if (roomFlags.get(k, l - 1) == 0 && layout.get(k, l - 1) == 2) {
                --o;
                q = 131072;
            }
            int r = this.random.nextBoolean() ? m : n;
            int s = this.random.nextBoolean() ? o : p;
            int t = 0x200000;
            if (!layout.anyMatchAround(r, s, 1)) {
                r = r == m ? n : m;
                int n2 = s = s == o ? p : o;
                if (!layout.anyMatchAround(r, s, 1)) {
                    int n3 = s = s == o ? p : o;
                    if (!layout.anyMatchAround(r, s, 1)) {
                        r = r == m ? n : m;
                        int n4 = s = s == o ? p : o;
                        if (!layout.anyMatchAround(r, s, 1)) {
                            t = 0;
                            r = m;
                            s = o;
                        }
                    }
                }
            }
            for (int u = o; u <= p; ++u) {
                for (int v = m; v <= n; ++v) {
                    if (v == r && u == s) {
                        roomFlags.set(v, u, 0x100000 | t | q | i);
                        continue;
                    }
                    roomFlags.set(v, u, q | i);
                }
            }
            ++i;
        }
    }
}
