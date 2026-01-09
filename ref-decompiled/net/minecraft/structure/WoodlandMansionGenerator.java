package net.minecraft.structure;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import org.jetbrains.annotations.Nullable;

public class WoodlandMansionGenerator {
   public static void addPieces(StructureTemplateManager manager, BlockPos pos, BlockRotation rotation, List pieces, Random random) {
      MansionParameters mansionParameters = new MansionParameters(random);
      LayoutGenerator layoutGenerator = new LayoutGenerator(manager, random);
      layoutGenerator.generate(pos, rotation, pieces, mansionParameters);
   }

   private static class MansionParameters {
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
      private static final int ORIGIN_CELL_FLAG = 1048576;
      private static final int ENTRANCE_CELL_FLAG = 2097152;
      private static final int STAIRCASE_CELL_FLAG = 4194304;
      private static final int CARPET_CELL_FLAG = 8388608;
      private static final int ROOM_SIZE_MASK = 983040;
      private static final int ROOM_ID_MASK = 65535;
      private final Random random;
      final FlagMatrix baseLayout;
      final FlagMatrix thirdFloorLayout;
      final FlagMatrix[] roomFlagsByFloor;
      final int entranceI;
      final int entranceJ;

      public MansionParameters(Random random) {
         this.random = random;
         int i = true;
         this.entranceI = 7;
         this.entranceJ = 4;
         this.baseLayout = new FlagMatrix(11, 11, 5);
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

         while(this.adjustLayoutWithRooms(this.baseLayout)) {
         }

         this.roomFlagsByFloor = new FlagMatrix[3];
         this.roomFlagsByFloor[0] = new FlagMatrix(11, 11, 5);
         this.roomFlagsByFloor[1] = new FlagMatrix(11, 11, 5);
         this.roomFlagsByFloor[2] = new FlagMatrix(11, 11, 5);
         this.updateRoomFlags(this.baseLayout, this.roomFlagsByFloor[0]);
         this.updateRoomFlags(this.baseLayout, this.roomFlagsByFloor[1]);
         this.roomFlagsByFloor[0].fill(this.entranceI + 1, this.entranceJ, this.entranceI + 1, this.entranceJ + 1, 8388608);
         this.roomFlagsByFloor[1].fill(this.entranceI + 1, this.entranceJ, this.entranceI + 1, this.entranceJ + 1, 8388608);
         this.thirdFloorLayout = new FlagMatrix(this.baseLayout.n, this.baseLayout.m, 5);
         this.layoutThirdFloor();
         this.updateRoomFlags(this.thirdFloorLayout, this.roomFlagsByFloor[2]);
      }

      public static boolean isInsideMansion(FlagMatrix layout, int i, int j) {
         int k = layout.get(i, j);
         return k == 1 || k == 2 || k == 3 || k == 4;
      }

      public boolean isRoomId(FlagMatrix layout, int i, int j, int floor, int roomId) {
         return (this.roomFlagsByFloor[floor].get(i, j) & '\uffff') == roomId;
      }

      @Nullable
      public Direction findConnectedRoomDirection(FlagMatrix layout, int i, int j, int floor, int roomId) {
         Iterator var6 = Direction.Type.HORIZONTAL.iterator();

         Direction direction;
         do {
            if (!var6.hasNext()) {
               return null;
            }

            direction = (Direction)var6.next();
         } while(!this.isRoomId(layout, i + direction.getOffsetX(), j + direction.getOffsetZ(), floor, roomId));

         return direction;
      }

      private void layoutCorridor(FlagMatrix layout, int i, int j, Direction direction, int length) {
         if (length > 0) {
            layout.set(i, j, 1);
            layout.update(i + direction.getOffsetX(), j + direction.getOffsetZ(), 0, 1);

            Direction direction2;
            for(int k = 0; k < 8; ++k) {
               direction2 = Direction.fromHorizontalQuarterTurns(this.random.nextInt(4));
               if (direction2 != direction.getOpposite() && (direction2 != Direction.EAST || !this.random.nextBoolean())) {
                  int l = i + direction.getOffsetX();
                  int m = j + direction.getOffsetZ();
                  if (layout.get(l + direction2.getOffsetX(), m + direction2.getOffsetZ()) == 0 && layout.get(l + direction2.getOffsetX() * 2, m + direction2.getOffsetZ() * 2) == 0) {
                     this.layoutCorridor(layout, i + direction.getOffsetX() + direction2.getOffsetX(), j + direction.getOffsetZ() + direction2.getOffsetZ(), direction2, length - 1);
                     break;
                  }
               }
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
      }

      private boolean adjustLayoutWithRooms(FlagMatrix layout) {
         boolean bl = false;

         for(int i = 0; i < layout.m; ++i) {
            for(int j = 0; j < layout.n; ++j) {
               if (layout.get(j, i) == 0) {
                  int k = 0;
                  k += isInsideMansion(layout, j + 1, i) ? 1 : 0;
                  k += isInsideMansion(layout, j - 1, i) ? 1 : 0;
                  k += isInsideMansion(layout, j, i + 1) ? 1 : 0;
                  k += isInsideMansion(layout, j, i - 1) ? 1 : 0;
                  if (k >= 3) {
                     layout.set(j, i, 2);
                     bl = true;
                  } else if (k == 2) {
                     int l = 0;
                     l += isInsideMansion(layout, j + 1, i + 1) ? 1 : 0;
                     l += isInsideMansion(layout, j - 1, i + 1) ? 1 : 0;
                     l += isInsideMansion(layout, j + 1, i - 1) ? 1 : 0;
                     l += isInsideMansion(layout, j - 1, i - 1) ? 1 : 0;
                     if (l <= 1) {
                        layout.set(j, i, 2);
                        bl = true;
                     }
                  }
               }
            }
         }

         return bl;
      }

      private void layoutThirdFloor() {
         List list = Lists.newArrayList();
         FlagMatrix flagMatrix = this.roomFlagsByFloor[1];

         int j;
         int l;
         for(int i = 0; i < this.thirdFloorLayout.m; ++i) {
            for(j = 0; j < this.thirdFloorLayout.n; ++j) {
               int k = flagMatrix.get(j, i);
               l = k & 983040;
               if (l == 131072 && (k & 2097152) == 2097152) {
                  list.add(new Pair(j, i));
               }
            }
         }

         if (list.isEmpty()) {
            this.thirdFloorLayout.fill(0, 0, this.thirdFloorLayout.n, this.thirdFloorLayout.m, 5);
         } else {
            Pair pair = (Pair)list.get(this.random.nextInt(list.size()));
            j = flagMatrix.get((Integer)pair.getLeft(), (Integer)pair.getRight());
            flagMatrix.set((Integer)pair.getLeft(), (Integer)pair.getRight(), j | 4194304);
            Direction direction = this.findConnectedRoomDirection(this.baseLayout, (Integer)pair.getLeft(), (Integer)pair.getRight(), 1, j & '\uffff');
            l = (Integer)pair.getLeft() + direction.getOffsetX();
            int m = (Integer)pair.getRight() + direction.getOffsetZ();

            for(int n = 0; n < this.thirdFloorLayout.m; ++n) {
               for(int o = 0; o < this.thirdFloorLayout.n; ++o) {
                  if (!isInsideMansion(this.baseLayout, o, n)) {
                     this.thirdFloorLayout.set(o, n, 5);
                  } else if (o == (Integer)pair.getLeft() && n == (Integer)pair.getRight()) {
                     this.thirdFloorLayout.set(o, n, 3);
                  } else if (o == l && n == m) {
                     this.thirdFloorLayout.set(o, n, 3);
                     this.roomFlagsByFloor[2].set(o, n, 8388608);
                  }
               }
            }

            List list2 = Lists.newArrayList();
            Iterator var14 = Direction.Type.HORIZONTAL.iterator();

            while(var14.hasNext()) {
               Direction direction2 = (Direction)var14.next();
               if (this.thirdFloorLayout.get(l + direction2.getOffsetX(), m + direction2.getOffsetZ()) == 0) {
                  list2.add(direction2);
               }
            }

            if (list2.isEmpty()) {
               this.thirdFloorLayout.fill(0, 0, this.thirdFloorLayout.n, this.thirdFloorLayout.m, 5);
               flagMatrix.set((Integer)pair.getLeft(), (Integer)pair.getRight(), j);
            } else {
               Direction direction3 = (Direction)list2.get(this.random.nextInt(list2.size()));
               this.layoutCorridor(this.thirdFloorLayout, l + direction3.getOffsetX(), m + direction3.getOffsetZ(), direction3, 4);

               while(this.adjustLayoutWithRooms(this.thirdFloorLayout)) {
               }

            }
         }
      }

      private void updateRoomFlags(FlagMatrix layout, FlagMatrix roomFlags) {
         ObjectArrayList objectArrayList = new ObjectArrayList();

         int i;
         for(i = 0; i < layout.m; ++i) {
            for(int j = 0; j < layout.n; ++j) {
               if (layout.get(j, i) == 2) {
                  objectArrayList.add(new Pair(j, i));
               }
            }
         }

         Util.shuffle((List)objectArrayList, this.random);
         i = 10;
         ObjectListIterator var19 = objectArrayList.iterator();

         while(true) {
            int k;
            int l;
            do {
               if (!var19.hasNext()) {
                  return;
               }

               Pair pair = (Pair)var19.next();
               k = (Integer)pair.getLeft();
               l = (Integer)pair.getRight();
            } while(roomFlags.get(k, l) != 0);

            int m = k;
            int n = k;
            int o = l;
            int p = l;
            int q = 65536;
            if (roomFlags.get(k + 1, l) == 0 && roomFlags.get(k, l + 1) == 0 && roomFlags.get(k + 1, l + 1) == 0 && layout.get(k + 1, l) == 2 && layout.get(k, l + 1) == 2 && layout.get(k + 1, l + 1) == 2) {
               n = k + 1;
               p = l + 1;
               q = 262144;
            } else if (roomFlags.get(k - 1, l) == 0 && roomFlags.get(k, l + 1) == 0 && roomFlags.get(k - 1, l + 1) == 0 && layout.get(k - 1, l) == 2 && layout.get(k, l + 1) == 2 && layout.get(k - 1, l + 1) == 2) {
               m = k - 1;
               p = l + 1;
               q = 262144;
            } else if (roomFlags.get(k - 1, l) == 0 && roomFlags.get(k, l - 1) == 0 && roomFlags.get(k - 1, l - 1) == 0 && layout.get(k - 1, l) == 2 && layout.get(k, l - 1) == 2 && layout.get(k - 1, l - 1) == 2) {
               m = k - 1;
               o = l - 1;
               q = 262144;
            } else if (roomFlags.get(k + 1, l) == 0 && layout.get(k + 1, l) == 2) {
               n = k + 1;
               q = 131072;
            } else if (roomFlags.get(k, l + 1) == 0 && layout.get(k, l + 1) == 2) {
               p = l + 1;
               q = 131072;
            } else if (roomFlags.get(k - 1, l) == 0 && layout.get(k - 1, l) == 2) {
               m = k - 1;
               q = 131072;
            } else if (roomFlags.get(k, l - 1) == 0 && layout.get(k, l - 1) == 2) {
               o = l - 1;
               q = 131072;
            }

            int r = this.random.nextBoolean() ? m : n;
            int s = this.random.nextBoolean() ? o : p;
            int t = 2097152;
            if (!layout.anyMatchAround(r, s, 1)) {
               r = r == m ? n : m;
               s = s == o ? p : o;
               if (!layout.anyMatchAround(r, s, 1)) {
                  s = s == o ? p : o;
                  if (!layout.anyMatchAround(r, s, 1)) {
                     r = r == m ? n : m;
                     s = s == o ? p : o;
                     if (!layout.anyMatchAround(r, s, 1)) {
                        t = 0;
                        r = m;
                        s = o;
                     }
                  }
               }
            }

            for(int u = o; u <= p; ++u) {
               for(int v = m; v <= n; ++v) {
                  if (v == r && u == s) {
                     roomFlags.set(v, u, 1048576 | t | q | i);
                  } else {
                     roomFlags.set(v, u, q | i);
                  }
               }
            }

            ++i;
         }
      }
   }

   static class LayoutGenerator {
      private final StructureTemplateManager manager;
      private final Random random;
      private int entranceI;
      private int entranceJ;

      public LayoutGenerator(StructureTemplateManager manager, Random random) {
         this.manager = manager;
         this.random = random;
      }

      public void generate(BlockPos pos, BlockRotation rotation, List pieces, MansionParameters parameters) {
         GenerationPiece generationPiece = new GenerationPiece();
         generationPiece.position = pos;
         generationPiece.rotation = rotation;
         generationPiece.template = "wall_flat";
         GenerationPiece generationPiece2 = new GenerationPiece();
         this.addEntrance(pieces, generationPiece);
         generationPiece2.position = generationPiece.position.up(8);
         generationPiece2.rotation = generationPiece.rotation;
         generationPiece2.template = "wall_window";
         if (!pieces.isEmpty()) {
         }

         FlagMatrix flagMatrix = parameters.baseLayout;
         FlagMatrix flagMatrix2 = parameters.thirdFloorLayout;
         this.entranceI = parameters.entranceI + 1;
         this.entranceJ = parameters.entranceJ + 1;
         int i = parameters.entranceI + 1;
         int j = parameters.entranceJ;
         this.addOuterWall(pieces, generationPiece, flagMatrix, Direction.SOUTH, this.entranceI, this.entranceJ, i, j);
         this.addOuterWall(pieces, generationPiece2, flagMatrix, Direction.SOUTH, this.entranceI, this.entranceJ, i, j);
         GenerationPiece generationPiece3 = new GenerationPiece();
         generationPiece3.position = generationPiece.position.up(19);
         generationPiece3.rotation = generationPiece.rotation;
         generationPiece3.template = "wall_window";
         boolean bl = false;

         int l;
         for(int k = 0; k < flagMatrix2.m && !bl; ++k) {
            for(l = flagMatrix2.n - 1; l >= 0 && !bl; --l) {
               if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(flagMatrix2, l, k)) {
                  generationPiece3.position = generationPiece3.position.offset(rotation.rotate(Direction.SOUTH), 8 + (k - this.entranceJ) * 8);
                  generationPiece3.position = generationPiece3.position.offset(rotation.rotate(Direction.EAST), (l - this.entranceI) * 8);
                  this.addWallPiece(pieces, generationPiece3);
                  this.addOuterWall(pieces, generationPiece3, flagMatrix2, Direction.SOUTH, l, k, l, k);
                  bl = true;
               }
            }
         }

         this.addRoof(pieces, pos.up(16), rotation, flagMatrix, flagMatrix2);
         this.addRoof(pieces, pos.up(27), rotation, flagMatrix2, (FlagMatrix)null);
         if (!pieces.isEmpty()) {
         }

         RoomPool[] roomPools = new RoomPool[]{new FirstFloorRoomPool(), new SecondFloorRoomPool(), new ThirdFloorRoomPool()};

         for(l = 0; l < 3; ++l) {
            BlockPos blockPos = pos.up(8 * l + (l == 2 ? 3 : 0));
            FlagMatrix flagMatrix3 = parameters.roomFlagsByFloor[l];
            FlagMatrix flagMatrix4 = l == 2 ? flagMatrix2 : flagMatrix;
            String string = l == 0 ? "carpet_south_1" : "carpet_south_2";
            String string2 = l == 0 ? "carpet_west_1" : "carpet_west_2";

            for(int m = 0; m < flagMatrix4.m; ++m) {
               for(int n = 0; n < flagMatrix4.n; ++n) {
                  if (flagMatrix4.get(n, m) == 1) {
                     BlockPos blockPos2 = blockPos.offset(rotation.rotate(Direction.SOUTH), 8 + (m - this.entranceJ) * 8);
                     blockPos2 = blockPos2.offset(rotation.rotate(Direction.EAST), (n - this.entranceI) * 8);
                     pieces.add(new Piece(this.manager, "corridor_floor", blockPos2, rotation));
                     if (flagMatrix4.get(n, m - 1) == 1 || (flagMatrix3.get(n, m - 1) & 8388608) == 8388608) {
                        pieces.add(new Piece(this.manager, "carpet_north", blockPos2.offset((Direction)rotation.rotate(Direction.EAST), 1).up(), rotation));
                     }

                     if (flagMatrix4.get(n + 1, m) == 1 || (flagMatrix3.get(n + 1, m) & 8388608) == 8388608) {
                        pieces.add(new Piece(this.manager, "carpet_east", blockPos2.offset((Direction)rotation.rotate(Direction.SOUTH), 1).offset((Direction)rotation.rotate(Direction.EAST), 5).up(), rotation));
                     }

                     if (flagMatrix4.get(n, m + 1) == 1 || (flagMatrix3.get(n, m + 1) & 8388608) == 8388608) {
                        pieces.add(new Piece(this.manager, string, blockPos2.offset((Direction)rotation.rotate(Direction.SOUTH), 5).offset((Direction)rotation.rotate(Direction.WEST), 1), rotation));
                     }

                     if (flagMatrix4.get(n - 1, m) == 1 || (flagMatrix3.get(n - 1, m) & 8388608) == 8388608) {
                        pieces.add(new Piece(this.manager, string2, blockPos2.offset((Direction)rotation.rotate(Direction.WEST), 1).offset((Direction)rotation.rotate(Direction.NORTH), 1), rotation));
                     }
                  }
               }
            }

            String string3 = l == 0 ? "indoors_wall_1" : "indoors_wall_2";
            String string4 = l == 0 ? "indoors_door_1" : "indoors_door_2";
            List list = Lists.newArrayList();

            for(int o = 0; o < flagMatrix4.m; ++o) {
               for(int p = 0; p < flagMatrix4.n; ++p) {
                  boolean bl2 = l == 2 && flagMatrix4.get(p, o) == 3;
                  if (flagMatrix4.get(p, o) == 2 || bl2) {
                     int q = flagMatrix3.get(p, o);
                     int r = q & 983040;
                     int s = q & '\uffff';
                     bl2 = bl2 && (q & 8388608) == 8388608;
                     list.clear();
                     if ((q & 2097152) == 2097152) {
                        Iterator var29 = Direction.Type.HORIZONTAL.iterator();

                        while(var29.hasNext()) {
                           Direction direction = (Direction)var29.next();
                           if (flagMatrix4.get(p + direction.getOffsetX(), o + direction.getOffsetZ()) == 1) {
                              list.add(direction);
                           }
                        }
                     }

                     Direction direction2 = null;
                     if (!list.isEmpty()) {
                        direction2 = (Direction)list.get(this.random.nextInt(list.size()));
                     } else if ((q & 1048576) == 1048576) {
                        direction2 = Direction.UP;
                     }

                     BlockPos blockPos3 = blockPos.offset(rotation.rotate(Direction.SOUTH), 8 + (o - this.entranceJ) * 8);
                     blockPos3 = blockPos3.offset(rotation.rotate(Direction.EAST), -1 + (p - this.entranceI) * 8);
                     if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(flagMatrix4, p - 1, o) && !parameters.isRoomId(flagMatrix4, p - 1, o, l, s)) {
                        pieces.add(new Piece(this.manager, direction2 == Direction.WEST ? string4 : string3, blockPos3, rotation));
                     }

                     BlockPos blockPos4;
                     if (flagMatrix4.get(p + 1, o) == 1 && !bl2) {
                        blockPos4 = blockPos3.offset((Direction)rotation.rotate(Direction.EAST), 8);
                        pieces.add(new Piece(this.manager, direction2 == Direction.EAST ? string4 : string3, blockPos4, rotation));
                     }

                     if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(flagMatrix4, p, o + 1) && !parameters.isRoomId(flagMatrix4, p, o + 1, l, s)) {
                        blockPos4 = blockPos3.offset((Direction)rotation.rotate(Direction.SOUTH), 7);
                        blockPos4 = blockPos4.offset((Direction)rotation.rotate(Direction.EAST), 7);
                        pieces.add(new Piece(this.manager, direction2 == Direction.SOUTH ? string4 : string3, blockPos4, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                     }

                     if (flagMatrix4.get(p, o - 1) == 1 && !bl2) {
                        blockPos4 = blockPos3.offset((Direction)rotation.rotate(Direction.NORTH), 1);
                        blockPos4 = blockPos4.offset((Direction)rotation.rotate(Direction.EAST), 7);
                        pieces.add(new Piece(this.manager, direction2 == Direction.NORTH ? string4 : string3, blockPos4, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                     }

                     if (r == 65536) {
                        this.addSmallRoom(pieces, blockPos3, rotation, direction2, roomPools[l]);
                     } else {
                        Direction direction3;
                        if (r == 131072 && direction2 != null) {
                           direction3 = parameters.findConnectedRoomDirection(flagMatrix4, p, o, l, s);
                           boolean bl3 = (q & 4194304) == 4194304;
                           this.addMediumRoom(pieces, blockPos3, rotation, direction3, direction2, roomPools[l], bl3);
                        } else if (r == 262144 && direction2 != null && direction2 != Direction.UP) {
                           direction3 = direction2.rotateYClockwise();
                           if (!parameters.isRoomId(flagMatrix4, p + direction3.getOffsetX(), o + direction3.getOffsetZ(), l, s)) {
                              direction3 = direction3.getOpposite();
                           }

                           this.addBigRoom(pieces, blockPos3, rotation, direction3, direction2, roomPools[l]);
                        } else if (r == 262144 && direction2 == Direction.UP) {
                           this.addBigSecretRoom(pieces, blockPos3, rotation, roomPools[l]);
                        }
                     }
                  }
               }
            }
         }

      }

      private void addOuterWall(List pieces, GenerationPiece wallPiece, FlagMatrix layout, Direction direction, int startI, int startJ, int endI, int endJ) {
         int i = startI;
         int j = startJ;
         Direction direction2 = direction;

         do {
            if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, i + direction.getOffsetX(), j + direction.getOffsetZ())) {
               this.turnLeft(pieces, wallPiece);
               direction = direction.rotateYClockwise();
               if (i != endI || j != endJ || direction2 != direction) {
                  this.addWallPiece(pieces, wallPiece);
               }
            } else if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, i + direction.getOffsetX(), j + direction.getOffsetZ()) && WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, i + direction.getOffsetX() + direction.rotateYCounterclockwise().getOffsetX(), j + direction.getOffsetZ() + direction.rotateYCounterclockwise().getOffsetZ())) {
               this.turnRight(pieces, wallPiece);
               i += direction.getOffsetX();
               j += direction.getOffsetZ();
               direction = direction.rotateYCounterclockwise();
            } else {
               i += direction.getOffsetX();
               j += direction.getOffsetZ();
               if (i != endI || j != endJ || direction2 != direction) {
                  this.addWallPiece(pieces, wallPiece);
               }
            }
         } while(i != endI || j != endJ || direction2 != direction);

      }

      private void addRoof(List pieces, BlockPos pos, BlockRotation rotation, FlagMatrix layout, @Nullable FlagMatrix nextFloorLayout) {
         int i;
         int j;
         BlockPos blockPos;
         boolean bl;
         BlockPos blockPos2;
         for(i = 0; i < layout.m; ++i) {
            for(j = 0; j < layout.n; ++j) {
               blockPos = pos.offset(rotation.rotate(Direction.SOUTH), 8 + (i - this.entranceJ) * 8);
               blockPos = blockPos.offset(rotation.rotate(Direction.EAST), (j - this.entranceI) * 8);
               bl = nextFloorLayout != null && WoodlandMansionGenerator.MansionParameters.isInsideMansion(nextFloorLayout, j, i);
               if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i) && !bl) {
                  pieces.add(new Piece(this.manager, "roof", blockPos.up(3), rotation));
                  if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j + 1, i)) {
                     blockPos2 = blockPos.offset((Direction)rotation.rotate(Direction.EAST), 6);
                     pieces.add(new Piece(this.manager, "roof_front", blockPos2, rotation));
                  }

                  if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j - 1, i)) {
                     blockPos2 = blockPos.offset((Direction)rotation.rotate(Direction.EAST), 0);
                     blockPos2 = blockPos2.offset((Direction)rotation.rotate(Direction.SOUTH), 7);
                     pieces.add(new Piece(this.manager, "roof_front", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_180)));
                  }

                  if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i - 1)) {
                     blockPos2 = blockPos.offset((Direction)rotation.rotate(Direction.WEST), 1);
                     pieces.add(new Piece(this.manager, "roof_front", blockPos2, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                  }

                  if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i + 1)) {
                     blockPos2 = blockPos.offset((Direction)rotation.rotate(Direction.EAST), 6);
                     blockPos2 = blockPos2.offset((Direction)rotation.rotate(Direction.SOUTH), 6);
                     pieces.add(new Piece(this.manager, "roof_front", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                  }
               }
            }
         }

         if (nextFloorLayout != null) {
            for(i = 0; i < layout.m; ++i) {
               for(j = 0; j < layout.n; ++j) {
                  blockPos = pos.offset(rotation.rotate(Direction.SOUTH), 8 + (i - this.entranceJ) * 8);
                  blockPos = blockPos.offset(rotation.rotate(Direction.EAST), (j - this.entranceI) * 8);
                  bl = WoodlandMansionGenerator.MansionParameters.isInsideMansion(nextFloorLayout, j, i);
                  if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i) && bl) {
                     if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j + 1, i)) {
                        blockPos2 = blockPos.offset((Direction)rotation.rotate(Direction.EAST), 7);
                        pieces.add(new Piece(this.manager, "small_wall", blockPos2, rotation));
                     }

                     if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j - 1, i)) {
                        blockPos2 = blockPos.offset((Direction)rotation.rotate(Direction.WEST), 1);
                        blockPos2 = blockPos2.offset((Direction)rotation.rotate(Direction.SOUTH), 6);
                        pieces.add(new Piece(this.manager, "small_wall", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_180)));
                     }

                     if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i - 1)) {
                        blockPos2 = blockPos.offset((Direction)rotation.rotate(Direction.WEST), 0);
                        blockPos2 = blockPos2.offset((Direction)rotation.rotate(Direction.NORTH), 1);
                        pieces.add(new Piece(this.manager, "small_wall", blockPos2, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                     }

                     if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i + 1)) {
                        blockPos2 = blockPos.offset((Direction)rotation.rotate(Direction.EAST), 6);
                        blockPos2 = blockPos2.offset((Direction)rotation.rotate(Direction.SOUTH), 7);
                        pieces.add(new Piece(this.manager, "small_wall", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                     }

                     if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j + 1, i)) {
                        if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i - 1)) {
                           blockPos2 = blockPos.offset((Direction)rotation.rotate(Direction.EAST), 7);
                           blockPos2 = blockPos2.offset((Direction)rotation.rotate(Direction.NORTH), 2);
                           pieces.add(new Piece(this.manager, "small_wall_corner", blockPos2, rotation));
                        }

                        if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i + 1)) {
                           blockPos2 = blockPos.offset((Direction)rotation.rotate(Direction.EAST), 8);
                           blockPos2 = blockPos2.offset((Direction)rotation.rotate(Direction.SOUTH), 7);
                           pieces.add(new Piece(this.manager, "small_wall_corner", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                        }
                     }

                     if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j - 1, i)) {
                        if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i - 1)) {
                           blockPos2 = blockPos.offset((Direction)rotation.rotate(Direction.WEST), 2);
                           blockPos2 = blockPos2.offset((Direction)rotation.rotate(Direction.NORTH), 1);
                           pieces.add(new Piece(this.manager, "small_wall_corner", blockPos2, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                        }

                        if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i + 1)) {
                           blockPos2 = blockPos.offset((Direction)rotation.rotate(Direction.WEST), 1);
                           blockPos2 = blockPos2.offset((Direction)rotation.rotate(Direction.SOUTH), 8);
                           pieces.add(new Piece(this.manager, "small_wall_corner", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_180)));
                        }
                     }
                  }
               }
            }
         }

         for(i = 0; i < layout.m; ++i) {
            for(j = 0; j < layout.n; ++j) {
               blockPos = pos.offset(rotation.rotate(Direction.SOUTH), 8 + (i - this.entranceJ) * 8);
               blockPos = blockPos.offset(rotation.rotate(Direction.EAST), (j - this.entranceI) * 8);
               bl = nextFloorLayout != null && WoodlandMansionGenerator.MansionParameters.isInsideMansion(nextFloorLayout, j, i);
               if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i) && !bl) {
                  BlockPos blockPos3;
                  if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j + 1, i)) {
                     blockPos2 = blockPos.offset((Direction)rotation.rotate(Direction.EAST), 6);
                     if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i + 1)) {
                        blockPos3 = blockPos2.offset((Direction)rotation.rotate(Direction.SOUTH), 6);
                        pieces.add(new Piece(this.manager, "roof_corner", blockPos3, rotation));
                     } else if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j + 1, i + 1)) {
                        blockPos3 = blockPos2.offset((Direction)rotation.rotate(Direction.SOUTH), 5);
                        pieces.add(new Piece(this.manager, "roof_inner_corner", blockPos3, rotation));
                     }

                     if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i - 1)) {
                        pieces.add(new Piece(this.manager, "roof_corner", blockPos2, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                     } else if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j + 1, i - 1)) {
                        blockPos3 = blockPos.offset((Direction)rotation.rotate(Direction.EAST), 9);
                        blockPos3 = blockPos3.offset((Direction)rotation.rotate(Direction.NORTH), 2);
                        pieces.add(new Piece(this.manager, "roof_inner_corner", blockPos3, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                     }
                  }

                  if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j - 1, i)) {
                     blockPos2 = blockPos.offset((Direction)rotation.rotate(Direction.EAST), 0);
                     blockPos2 = blockPos2.offset((Direction)rotation.rotate(Direction.SOUTH), 0);
                     if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i + 1)) {
                        blockPos3 = blockPos2.offset((Direction)rotation.rotate(Direction.SOUTH), 6);
                        pieces.add(new Piece(this.manager, "roof_corner", blockPos3, rotation.rotate(BlockRotation.CLOCKWISE_90)));
                     } else if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j - 1, i + 1)) {
                        blockPos3 = blockPos2.offset((Direction)rotation.rotate(Direction.SOUTH), 8);
                        blockPos3 = blockPos3.offset((Direction)rotation.rotate(Direction.WEST), 3);
                        pieces.add(new Piece(this.manager, "roof_inner_corner", blockPos3, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
                     }

                     if (!WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j, i - 1)) {
                        pieces.add(new Piece(this.manager, "roof_corner", blockPos2, rotation.rotate(BlockRotation.CLOCKWISE_180)));
                     } else if (WoodlandMansionGenerator.MansionParameters.isInsideMansion(layout, j - 1, i - 1)) {
                        blockPos3 = blockPos2.offset((Direction)rotation.rotate(Direction.SOUTH), 1);
                        pieces.add(new Piece(this.manager, "roof_inner_corner", blockPos3, rotation.rotate(BlockRotation.CLOCKWISE_180)));
                     }
                  }
               }
            }
         }

      }

      private void addEntrance(List pieces, GenerationPiece wallPiece) {
         Direction direction = wallPiece.rotation.rotate(Direction.WEST);
         pieces.add(new Piece(this.manager, "entrance", wallPiece.position.offset((Direction)direction, 9), wallPiece.rotation));
         wallPiece.position = wallPiece.position.offset((Direction)wallPiece.rotation.rotate(Direction.SOUTH), 16);
      }

      private void addWallPiece(List pieces, GenerationPiece wallPiece) {
         pieces.add(new Piece(this.manager, wallPiece.template, wallPiece.position.offset((Direction)wallPiece.rotation.rotate(Direction.EAST), 7), wallPiece.rotation));
         wallPiece.position = wallPiece.position.offset((Direction)wallPiece.rotation.rotate(Direction.SOUTH), 8);
      }

      private void turnLeft(List pieces, GenerationPiece wallPiece) {
         wallPiece.position = wallPiece.position.offset((Direction)wallPiece.rotation.rotate(Direction.SOUTH), -1);
         pieces.add(new Piece(this.manager, "wall_corner", wallPiece.position, wallPiece.rotation));
         wallPiece.position = wallPiece.position.offset((Direction)wallPiece.rotation.rotate(Direction.SOUTH), -7);
         wallPiece.position = wallPiece.position.offset((Direction)wallPiece.rotation.rotate(Direction.WEST), -6);
         wallPiece.rotation = wallPiece.rotation.rotate(BlockRotation.CLOCKWISE_90);
      }

      private void turnRight(List pieces, GenerationPiece wallPiece) {
         wallPiece.position = wallPiece.position.offset((Direction)wallPiece.rotation.rotate(Direction.SOUTH), 6);
         wallPiece.position = wallPiece.position.offset((Direction)wallPiece.rotation.rotate(Direction.EAST), 8);
         wallPiece.rotation = wallPiece.rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90);
      }

      private void addSmallRoom(List pieces, BlockPos pos, BlockRotation rotation, Direction direction, RoomPool pool) {
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
         pieces.add(new Piece(this.manager, string, blockPos2, blockRotation));
      }

      private void addMediumRoom(List pieces, BlockPos pos, BlockRotation rotation, Direction connectedRoomDirection, Direction entranceDirection, RoomPool pool, boolean staircase) {
         BlockPos blockPos;
         if (entranceDirection == Direction.EAST && connectedRoomDirection == Direction.SOUTH) {
            blockPos = pos.offset((Direction)rotation.rotate(Direction.EAST), 1);
            pieces.add(new Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation));
         } else if (entranceDirection == Direction.EAST && connectedRoomDirection == Direction.NORTH) {
            blockPos = pos.offset((Direction)rotation.rotate(Direction.EAST), 1);
            blockPos = blockPos.offset((Direction)rotation.rotate(Direction.SOUTH), 6);
            pieces.add(new Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation, BlockMirror.LEFT_RIGHT));
         } else if (entranceDirection == Direction.WEST && connectedRoomDirection == Direction.NORTH) {
            blockPos = pos.offset((Direction)rotation.rotate(Direction.EAST), 7);
            blockPos = blockPos.offset((Direction)rotation.rotate(Direction.SOUTH), 6);
            pieces.add(new Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_180)));
         } else if (entranceDirection == Direction.WEST && connectedRoomDirection == Direction.SOUTH) {
            blockPos = pos.offset((Direction)rotation.rotate(Direction.EAST), 7);
            pieces.add(new Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation, BlockMirror.FRONT_BACK));
         } else if (entranceDirection == Direction.SOUTH && connectedRoomDirection == Direction.EAST) {
            blockPos = pos.offset((Direction)rotation.rotate(Direction.EAST), 1);
            pieces.add(new Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_90), BlockMirror.LEFT_RIGHT));
         } else if (entranceDirection == Direction.SOUTH && connectedRoomDirection == Direction.WEST) {
            blockPos = pos.offset((Direction)rotation.rotate(Direction.EAST), 7);
            pieces.add(new Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_90)));
         } else if (entranceDirection == Direction.NORTH && connectedRoomDirection == Direction.WEST) {
            blockPos = pos.offset((Direction)rotation.rotate(Direction.EAST), 7);
            blockPos = blockPos.offset((Direction)rotation.rotate(Direction.SOUTH), 6);
            pieces.add(new Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_90), BlockMirror.FRONT_BACK));
         } else if (entranceDirection == Direction.NORTH && connectedRoomDirection == Direction.EAST) {
            blockPos = pos.offset((Direction)rotation.rotate(Direction.EAST), 1);
            blockPos = blockPos.offset((Direction)rotation.rotate(Direction.SOUTH), 6);
            pieces.add(new Piece(this.manager, pool.getMediumFunctionalRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
         } else if (entranceDirection == Direction.SOUTH && connectedRoomDirection == Direction.NORTH) {
            blockPos = pos.offset((Direction)rotation.rotate(Direction.EAST), 1);
            blockPos = blockPos.offset((Direction)rotation.rotate(Direction.NORTH), 8);
            pieces.add(new Piece(this.manager, pool.getMediumGenericRoom(this.random, staircase), blockPos, rotation));
         } else if (entranceDirection == Direction.NORTH && connectedRoomDirection == Direction.SOUTH) {
            blockPos = pos.offset((Direction)rotation.rotate(Direction.EAST), 7);
            blockPos = blockPos.offset((Direction)rotation.rotate(Direction.SOUTH), 14);
            pieces.add(new Piece(this.manager, pool.getMediumGenericRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_180)));
         } else if (entranceDirection == Direction.WEST && connectedRoomDirection == Direction.EAST) {
            blockPos = pos.offset((Direction)rotation.rotate(Direction.EAST), 15);
            pieces.add(new Piece(this.manager, pool.getMediumGenericRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_90)));
         } else if (entranceDirection == Direction.EAST && connectedRoomDirection == Direction.WEST) {
            blockPos = pos.offset((Direction)rotation.rotate(Direction.WEST), 7);
            blockPos = blockPos.offset((Direction)rotation.rotate(Direction.SOUTH), 6);
            pieces.add(new Piece(this.manager, pool.getMediumGenericRoom(this.random, staircase), blockPos, rotation.rotate(BlockRotation.COUNTERCLOCKWISE_90)));
         } else if (entranceDirection == Direction.UP && connectedRoomDirection == Direction.EAST) {
            blockPos = pos.offset((Direction)rotation.rotate(Direction.EAST), 15);
            pieces.add(new Piece(this.manager, pool.getMediumSecretRoom(this.random), blockPos, rotation.rotate(BlockRotation.CLOCKWISE_90)));
         } else if (entranceDirection == Direction.UP && connectedRoomDirection == Direction.SOUTH) {
            blockPos = pos.offset((Direction)rotation.rotate(Direction.EAST), 1);
            blockPos = blockPos.offset((Direction)rotation.rotate(Direction.NORTH), 0);
            pieces.add(new Piece(this.manager, pool.getMediumSecretRoom(this.random), blockPos, rotation));
         }

      }

      private void addBigRoom(List pieces, BlockPos pos, BlockRotation rotation, Direction connectedRoomDirection, Direction entranceDirection, RoomPool pool) {
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

         BlockPos blockPos = pos.offset((Direction)rotation.rotate(Direction.EAST), i);
         blockPos = blockPos.offset((Direction)rotation.rotate(Direction.SOUTH), j);
         pieces.add(new Piece(this.manager, pool.getBigRoom(this.random), blockPos, blockRotation, blockMirror));
      }

      private void addBigSecretRoom(List pieces, BlockPos pos, BlockRotation rotation, RoomPool pool) {
         BlockPos blockPos = pos.offset((Direction)rotation.rotate(Direction.EAST), 1);
         pieces.add(new Piece(this.manager, pool.getBigSecretRoom(this.random), blockPos, rotation, BlockMirror.NONE));
      }
   }

   static class ThirdFloorRoomPool extends SecondFloorRoomPool {
   }

   private static class SecondFloorRoomPool extends RoomPool {
      SecondFloorRoomPool() {
      }

      public String getSmallRoom(Random random) {
         int var10000 = random.nextInt(5);
         return "1x1_b" + (var10000 + 1);
      }

      public String getSmallSecretRoom(Random random) {
         int var10000 = random.nextInt(4);
         return "1x1_as" + (var10000 + 1);
      }

      public String getMediumFunctionalRoom(Random random, boolean staircase) {
         if (staircase) {
            return "1x2_c_stairs";
         } else {
            int var10000 = random.nextInt(4);
            return "1x2_c" + (var10000 + 1);
         }
      }

      public String getMediumGenericRoom(Random random, boolean staircase) {
         if (staircase) {
            return "1x2_d_stairs";
         } else {
            int var10000 = random.nextInt(5);
            return "1x2_d" + (var10000 + 1);
         }
      }

      public String getMediumSecretRoom(Random random) {
         int var10000 = random.nextInt(1);
         return "1x2_se" + (var10000 + 1);
      }

      public String getBigRoom(Random random) {
         int var10000 = random.nextInt(5);
         return "2x2_b" + (var10000 + 1);
      }

      public String getBigSecretRoom(Random random) {
         return "2x2_s1";
      }
   }

   private static class FirstFloorRoomPool extends RoomPool {
      FirstFloorRoomPool() {
      }

      public String getSmallRoom(Random random) {
         int var10000 = random.nextInt(5);
         return "1x1_a" + (var10000 + 1);
      }

      public String getSmallSecretRoom(Random random) {
         int var10000 = random.nextInt(4);
         return "1x1_as" + (var10000 + 1);
      }

      public String getMediumFunctionalRoom(Random random, boolean staircase) {
         int var10000 = random.nextInt(9);
         return "1x2_a" + (var10000 + 1);
      }

      public String getMediumGenericRoom(Random random, boolean staircase) {
         int var10000 = random.nextInt(5);
         return "1x2_b" + (var10000 + 1);
      }

      public String getMediumSecretRoom(Random random) {
         int var10000 = random.nextInt(2);
         return "1x2_s" + (var10000 + 1);
      }

      public String getBigRoom(Random random) {
         int var10000 = random.nextInt(4);
         return "2x2_a" + (var10000 + 1);
      }

      public String getBigSecretRoom(Random random) {
         return "2x2_s1";
      }
   }

   private abstract static class RoomPool {
      RoomPool() {
      }

      public abstract String getSmallRoom(Random random);

      public abstract String getSmallSecretRoom(Random random);

      public abstract String getMediumFunctionalRoom(Random random, boolean staircase);

      public abstract String getMediumGenericRoom(Random random, boolean staircase);

      public abstract String getMediumSecretRoom(Random random);

      public abstract String getBigRoom(Random random);

      public abstract String getBigSecretRoom(Random random);
   }

   private static class FlagMatrix {
      private final int[][] array;
      final int n;
      final int m;
      private final int fallback;

      public FlagMatrix(int n, int m, int fallback) {
         this.n = n;
         this.m = m;
         this.fallback = fallback;
         this.array = new int[n][m];
      }

      public void set(int i, int j, int value) {
         if (i >= 0 && i < this.n && j >= 0 && j < this.m) {
            this.array[i][j] = value;
         }

      }

      public void fill(int i0, int j0, int i1, int j1, int value) {
         for(int i = j0; i <= j1; ++i) {
            for(int j = i0; j <= i1; ++j) {
               this.set(j, i, value);
            }
         }

      }

      public int get(int i, int j) {
         return i >= 0 && i < this.n && j >= 0 && j < this.m ? this.array[i][j] : this.fallback;
      }

      public void update(int i, int j, int expected, int newValue) {
         if (this.get(i, j) == expected) {
            this.set(i, j, newValue);
         }

      }

      public boolean anyMatchAround(int i, int j, int value) {
         return this.get(i - 1, j) == value || this.get(i + 1, j) == value || this.get(i, j + 1) == value || this.get(i, j - 1) == value;
      }
   }

   private static class GenerationPiece {
      public BlockRotation rotation;
      public BlockPos position;
      public String template;

      GenerationPiece() {
      }
   }

   public static class Piece extends SimpleStructurePiece {
      public Piece(StructureTemplateManager manager, String template, BlockPos pos, BlockRotation rotation) {
         this(manager, template, pos, rotation, BlockMirror.NONE);
      }

      public Piece(StructureTemplateManager manager, String template, BlockPos pos, BlockRotation rotation, BlockMirror mirror) {
         super(StructurePieceType.WOODLAND_MANSION, 0, manager, getId(template), template, createPlacementData(mirror, rotation), pos);
      }

      public Piece(StructureTemplateManager manager, NbtCompound nbt) {
         super(StructurePieceType.WOODLAND_MANSION, nbt, manager, (id) -> {
            return createPlacementData((BlockMirror)nbt.get("Mi", BlockMirror.ENUM_NAME_CODEC).orElseThrow(), (BlockRotation)nbt.get("Rot", BlockRotation.ENUM_NAME_CODEC).orElseThrow());
         });
      }

      protected Identifier getId() {
         return getId(this.templateIdString);
      }

      private static Identifier getId(String identifier) {
         return Identifier.ofVanilla("woodland_mansion/" + identifier);
      }

      private static StructurePlacementData createPlacementData(BlockMirror mirror, BlockRotation rotation) {
         return (new StructurePlacementData()).setIgnoreEntities(true).setRotation(rotation).setMirror(mirror).addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
      }

      protected void writeNbt(StructureContext context, NbtCompound nbt) {
         super.writeNbt(context, nbt);
         nbt.put("Rot", BlockRotation.ENUM_NAME_CODEC, this.placementData.getRotation());
         nbt.put("Mi", BlockMirror.ENUM_NAME_CODEC, this.placementData.getMirror());
      }

      protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
         if (metadata.startsWith("Chest")) {
            BlockRotation blockRotation = this.placementData.getRotation();
            BlockState blockState = Blocks.CHEST.getDefaultState();
            if ("ChestWest".equals(metadata)) {
               blockState = (BlockState)blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.WEST));
            } else if ("ChestEast".equals(metadata)) {
               blockState = (BlockState)blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.EAST));
            } else if ("ChestSouth".equals(metadata)) {
               blockState = (BlockState)blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.SOUTH));
            } else if ("ChestNorth".equals(metadata)) {
               blockState = (BlockState)blockState.with(ChestBlock.FACING, blockRotation.rotate(Direction.NORTH));
            }

            this.addChest(world, boundingBox, random, pos, LootTables.WOODLAND_MANSION_CHEST, blockState);
         } else {
            List list = new ArrayList();
            label60:
            switch (metadata) {
               case "Mage":
                  list.add((MobEntity)EntityType.EVOKER.create(world.toServerWorld(), SpawnReason.STRUCTURE));
                  break;
               case "Warrior":
                  list.add((MobEntity)EntityType.VINDICATOR.create(world.toServerWorld(), SpawnReason.STRUCTURE));
                  break;
               case "Group of Allays":
                  int i = world.getRandom().nextInt(3) + 1;
                  int j = 0;

                  while(true) {
                     if (j >= i) {
                        break label60;
                     }

                     list.add((MobEntity)EntityType.ALLAY.create(world.toServerWorld(), SpawnReason.STRUCTURE));
                     ++j;
                  }
               default:
                  return;
            }

            Iterator var7 = list.iterator();

            while(var7.hasNext()) {
               MobEntity mobEntity = (MobEntity)var7.next();
               if (mobEntity != null) {
                  mobEntity.setPersistent();
                  mobEntity.refreshPositionAndAngles(pos, 0.0F, 0.0F);
                  mobEntity.initialize(world, world.getLocalDifficulty(mobEntity.getBlockPos()), SpawnReason.STRUCTURE, (EntityData)null);
                  world.spawnEntityAndPassengers(mobEntity);
                  world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
               }
            }
         }

      }
   }
}
