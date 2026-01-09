package net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Optional;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;

@Unmodifiable
public class BlockPos extends Vec3i {
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;
   private static final Logger LOGGER;
   public static final BlockPos ORIGIN;
   public static final int SIZE_BITS_XZ;
   public static final int SIZE_BITS_Y;
   private static final long BITS_X;
   private static final long BITS_Y;
   private static final long BITS_Z;
   private static final int field_33083 = 0;
   private static final int BIT_SHIFT_Z;
   private static final int BIT_SHIFT_X;
   public static final int MAX_XZ;

   public BlockPos(int i, int j, int k) {
      super(i, j, k);
   }

   public BlockPos(Vec3i pos) {
      this(pos.getX(), pos.getY(), pos.getZ());
   }

   public static long offset(long value, Direction direction) {
      return add(value, direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
   }

   public static long add(long value, int x, int y, int z) {
      return asLong(unpackLongX(value) + x, unpackLongY(value) + y, unpackLongZ(value) + z);
   }

   public static int unpackLongX(long packedPos) {
      return (int)(packedPos << 64 - BIT_SHIFT_X - SIZE_BITS_XZ >> 64 - SIZE_BITS_XZ);
   }

   public static int unpackLongY(long packedPos) {
      return (int)(packedPos << 64 - SIZE_BITS_Y >> 64 - SIZE_BITS_Y);
   }

   public static int unpackLongZ(long packedPos) {
      return (int)(packedPos << 64 - BIT_SHIFT_Z - SIZE_BITS_XZ >> 64 - SIZE_BITS_XZ);
   }

   public static BlockPos fromLong(long packedPos) {
      return new BlockPos(unpackLongX(packedPos), unpackLongY(packedPos), unpackLongZ(packedPos));
   }

   public static BlockPos ofFloored(double x, double y, double z) {
      return new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
   }

   public static BlockPos ofFloored(Position pos) {
      return ofFloored(pos.getX(), pos.getY(), pos.getZ());
   }

   public static BlockPos min(BlockPos a, BlockPos b) {
      return new BlockPos(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(), b.getZ()));
   }

   public static BlockPos max(BlockPos a, BlockPos b) {
      return new BlockPos(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()), Math.max(a.getZ(), b.getZ()));
   }

   public long asLong() {
      return asLong(this.getX(), this.getY(), this.getZ());
   }

   public static long asLong(int x, int y, int z) {
      long l = 0L;
      l |= ((long)x & BITS_X) << BIT_SHIFT_X;
      l |= ((long)y & BITS_Y) << 0;
      l |= ((long)z & BITS_Z) << BIT_SHIFT_Z;
      return l;
   }

   public static long removeChunkSectionLocalY(long y) {
      return y & -16L;
   }

   public BlockPos add(int i, int j, int k) {
      return i == 0 && j == 0 && k == 0 ? this : new BlockPos(this.getX() + i, this.getY() + j, this.getZ() + k);
   }

   public Vec3d toCenterPos() {
      return Vec3d.ofCenter(this);
   }

   public Vec3d toBottomCenterPos() {
      return Vec3d.ofBottomCenter(this);
   }

   public BlockPos add(Vec3i vec3i) {
      return this.add(vec3i.getX(), vec3i.getY(), vec3i.getZ());
   }

   public BlockPos subtract(Vec3i vec3i) {
      return this.add(-vec3i.getX(), -vec3i.getY(), -vec3i.getZ());
   }

   public BlockPos multiply(int i) {
      if (i == 1) {
         return this;
      } else {
         return i == 0 ? ORIGIN : new BlockPos(this.getX() * i, this.getY() * i, this.getZ() * i);
      }
   }

   public BlockPos up() {
      return this.offset(Direction.UP);
   }

   public BlockPos up(int distance) {
      return this.offset(Direction.UP, distance);
   }

   public BlockPos down() {
      return this.offset(Direction.DOWN);
   }

   public BlockPos down(int i) {
      return this.offset(Direction.DOWN, i);
   }

   public BlockPos north() {
      return this.offset(Direction.NORTH);
   }

   public BlockPos north(int distance) {
      return this.offset(Direction.NORTH, distance);
   }

   public BlockPos south() {
      return this.offset(Direction.SOUTH);
   }

   public BlockPos south(int distance) {
      return this.offset(Direction.SOUTH, distance);
   }

   public BlockPos west() {
      return this.offset(Direction.WEST);
   }

   public BlockPos west(int distance) {
      return this.offset(Direction.WEST, distance);
   }

   public BlockPos east() {
      return this.offset(Direction.EAST);
   }

   public BlockPos east(int distance) {
      return this.offset(Direction.EAST, distance);
   }

   public BlockPos offset(Direction direction) {
      return new BlockPos(this.getX() + direction.getOffsetX(), this.getY() + direction.getOffsetY(), this.getZ() + direction.getOffsetZ());
   }

   public BlockPos offset(Direction direction, int i) {
      return i == 0 ? this : new BlockPos(this.getX() + direction.getOffsetX() * i, this.getY() + direction.getOffsetY() * i, this.getZ() + direction.getOffsetZ() * i);
   }

   public BlockPos offset(Direction.Axis axis, int i) {
      if (i == 0) {
         return this;
      } else {
         int j = axis == Direction.Axis.X ? i : 0;
         int k = axis == Direction.Axis.Y ? i : 0;
         int l = axis == Direction.Axis.Z ? i : 0;
         return new BlockPos(this.getX() + j, this.getY() + k, this.getZ() + l);
      }
   }

   public BlockPos rotate(BlockRotation rotation) {
      switch (rotation) {
         case NONE:
         default:
            return this;
         case CLOCKWISE_90:
            return new BlockPos(-this.getZ(), this.getY(), this.getX());
         case CLOCKWISE_180:
            return new BlockPos(-this.getX(), this.getY(), -this.getZ());
         case COUNTERCLOCKWISE_90:
            return new BlockPos(this.getZ(), this.getY(), -this.getX());
      }
   }

   public BlockPos crossProduct(Vec3i pos) {
      return new BlockPos(this.getY() * pos.getZ() - this.getZ() * pos.getY(), this.getZ() * pos.getX() - this.getX() * pos.getZ(), this.getX() * pos.getY() - this.getY() * pos.getX());
   }

   public BlockPos withY(int y) {
      return new BlockPos(this.getX(), y, this.getZ());
   }

   public BlockPos toImmutable() {
      return this;
   }

   public Mutable mutableCopy() {
      return new Mutable(this.getX(), this.getY(), this.getZ());
   }

   public Vec3d clampToWithin(Vec3d pos) {
      return new Vec3d(MathHelper.clamp(pos.x, (double)((float)this.getX() + 1.0E-5F), (double)this.getX() + 1.0 - 9.999999747378752E-6), MathHelper.clamp(pos.y, (double)((float)this.getY() + 1.0E-5F), (double)this.getY() + 1.0 - 9.999999747378752E-6), MathHelper.clamp(pos.z, (double)((float)this.getZ() + 1.0E-5F), (double)this.getZ() + 1.0 - 9.999999747378752E-6));
   }

   public static Iterable iterateRandomly(Random random, int count, BlockPos around, int range) {
      return iterateRandomly(random, count, around.getX() - range, around.getY() - range, around.getZ() - range, around.getX() + range, around.getY() + range, around.getZ() + range);
   }

   /** @deprecated */
   @Deprecated
   public static Stream streamSouthEastSquare(BlockPos pos) {
      return Stream.of(pos, pos.south(), pos.east(), pos.south().east());
   }

   public static Iterable iterateRandomly(Random random, int count, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      int i = maxX - minX + 1;
      int j = maxY - minY + 1;
      int k = maxZ - minZ + 1;
      return () -> {
         return new AbstractIterator() {
            final Mutable pos = new Mutable();
            int remaining = i;

            protected BlockPos computeNext() {
               if (this.remaining <= 0) {
                  return (BlockPos)this.endOfData();
               } else {
                  BlockPos blockPos = this.pos.set(j + random.nextInt(k), l + random.nextInt(m), n + random.nextInt(o));
                  --this.remaining;
                  return blockPos;
               }
            }

            // $FF: synthetic method
            protected Object computeNext() {
               return this.computeNext();
            }
         };
      };
   }

   public static Iterable iterateOutwards(BlockPos center, int rangeX, int rangeY, int rangeZ) {
      int i = rangeX + rangeY + rangeZ;
      int j = center.getX();
      int k = center.getY();
      int l = center.getZ();
      return () -> {
         return new AbstractIterator() {
            private final Mutable pos = new Mutable();
            private int manhattanDistance;
            private int limitX;
            private int limitY;
            private int dx;
            private int dy;
            private boolean swapZ;

            protected BlockPos computeNext() {
               if (this.swapZ) {
                  this.swapZ = false;
                  this.pos.setZ(i - (this.pos.getZ() - i));
                  return this.pos;
               } else {
                  Mutable blockPos;
                  for(blockPos = null; blockPos == null; ++this.dy) {
                     if (this.dy > this.limitY) {
                        ++this.dx;
                        if (this.dx > this.limitX) {
                           ++this.manhattanDistance;
                           if (this.manhattanDistance > j) {
                              return (BlockPos)this.endOfData();
                           }

                           this.limitX = Math.min(k, this.manhattanDistance);
                           this.dx = -this.limitX;
                        }

                        this.limitY = Math.min(l, this.manhattanDistance - Math.abs(this.dx));
                        this.dy = -this.limitY;
                     }

                     int ix = this.dx;
                     int jx = this.dy;
                     int kx = this.manhattanDistance - Math.abs(ix) - Math.abs(jx);
                     if (kx <= m) {
                        this.swapZ = kx != 0;
                        blockPos = this.pos.set(n + ix, o + jx, i + kx);
                     }
                  }

                  return blockPos;
               }
            }

            // $FF: synthetic method
            protected Object computeNext() {
               return this.computeNext();
            }
         };
      };
   }

   public static Optional findClosest(BlockPos pos, int horizontalRange, int verticalRange, Predicate condition) {
      Iterator var4 = iterateOutwards(pos, horizontalRange, verticalRange, horizontalRange).iterator();

      BlockPos blockPos;
      do {
         if (!var4.hasNext()) {
            return Optional.empty();
         }

         blockPos = (BlockPos)var4.next();
      } while(!condition.test(blockPos));

      return Optional.of(blockPos);
   }

   public static Stream streamOutwards(BlockPos center, int maxX, int maxY, int maxZ) {
      return StreamSupport.stream(iterateOutwards(center, maxX, maxY, maxZ).spliterator(), false);
   }

   public static Iterable iterate(Box box) {
      BlockPos blockPos = ofFloored(box.minX, box.minY, box.minZ);
      BlockPos blockPos2 = ofFloored(box.maxX, box.maxY, box.maxZ);
      return iterate(blockPos, blockPos2);
   }

   public static Iterable iterate(BlockPos start, BlockPos end) {
      return iterate(Math.min(start.getX(), end.getX()), Math.min(start.getY(), end.getY()), Math.min(start.getZ(), end.getZ()), Math.max(start.getX(), end.getX()), Math.max(start.getY(), end.getY()), Math.max(start.getZ(), end.getZ()));
   }

   public static Stream stream(BlockPos start, BlockPos end) {
      return StreamSupport.stream(iterate(start, end).spliterator(), false);
   }

   public static Stream stream(BlockBox box) {
      return stream(Math.min(box.getMinX(), box.getMaxX()), Math.min(box.getMinY(), box.getMaxY()), Math.min(box.getMinZ(), box.getMaxZ()), Math.max(box.getMinX(), box.getMaxX()), Math.max(box.getMinY(), box.getMaxY()), Math.max(box.getMinZ(), box.getMaxZ()));
   }

   public static Stream stream(Box box) {
      return stream(MathHelper.floor(box.minX), MathHelper.floor(box.minY), MathHelper.floor(box.minZ), MathHelper.floor(box.maxX), MathHelper.floor(box.maxY), MathHelper.floor(box.maxZ));
   }

   public static Stream stream(int startX, int startY, int startZ, int endX, int endY, int endZ) {
      return StreamSupport.stream(iterate(startX, startY, startZ, endX, endY, endZ).spliterator(), false);
   }

   public static Iterable iterate(int startX, int startY, int startZ, int endX, int endY, int endZ) {
      int i = endX - startX + 1;
      int j = endY - startY + 1;
      int k = endZ - startZ + 1;
      int l = i * j * k;
      return () -> {
         return new AbstractIterator() {
            private final Mutable pos = new Mutable();
            private int index;

            protected BlockPos computeNext() {
               if (this.index == i) {
                  return (BlockPos)this.endOfData();
               } else {
                  int ix = this.index % j;
                  int jx = this.index / j;
                  int kx = jx % k;
                  int lx = jx / k;
                  ++this.index;
                  return this.pos.set(l + ix, m + kx, n + lx);
               }
            }

            // $FF: synthetic method
            protected Object computeNext() {
               return this.computeNext();
            }
         };
      };
   }

   public static Iterable iterateInSquare(BlockPos center, int radius, Direction firstDirection, Direction secondDirection) {
      Validate.validState(firstDirection.getAxis() != secondDirection.getAxis(), "The two directions cannot be on the same axis", new Object[0]);
      return () -> {
         return new AbstractIterator() {
            private final Direction[] directions = new Direction[]{direction, direction2, direction.getOpposite(), direction2.getOpposite()};
            private final Mutable pos = blockPos.mutableCopy().move(direction2);
            private final int maxDirectionChanges = 4 * i;
            private int directionChangeCount = -1;
            private int maxSteps;
            private int steps;
            private int currentX;
            private int currentY;
            private int currentZ;

            {
               this.currentX = this.pos.getX();
               this.currentY = this.pos.getY();
               this.currentZ = this.pos.getZ();
            }

            protected Mutable computeNext() {
               this.pos.set(this.currentX, this.currentY, this.currentZ).move(this.directions[(this.directionChangeCount + 4) % 4]);
               this.currentX = this.pos.getX();
               this.currentY = this.pos.getY();
               this.currentZ = this.pos.getZ();
               if (this.steps >= this.maxSteps) {
                  if (this.directionChangeCount >= this.maxDirectionChanges) {
                     return (Mutable)this.endOfData();
                  }

                  ++this.directionChangeCount;
                  this.steps = 0;
                  this.maxSteps = this.directionChangeCount / 2 + 1;
               }

               ++this.steps;
               return this.pos;
            }

            // $FF: synthetic method
            protected Object computeNext() {
               return this.computeNext();
            }
         };
      };
   }

   public static int iterateRecursively(BlockPos pos, int maxDepth, int maxIterations, BiConsumer nextQueuer, Function callback) {
      Queue queue = new ArrayDeque();
      LongSet longSet = new LongOpenHashSet();
      queue.add(Pair.of(pos, 0));
      int i = 0;

      while(!queue.isEmpty()) {
         Pair pair = (Pair)queue.poll();
         BlockPos blockPos = (BlockPos)pair.getLeft();
         int j = (Integer)pair.getRight();
         long l = blockPos.asLong();
         if (longSet.add(l)) {
            IterationState iterationState = (IterationState)callback.apply(blockPos);
            if (iterationState != BlockPos.IterationState.SKIP) {
               if (iterationState == BlockPos.IterationState.STOP) {
                  break;
               }

               ++i;
               if (i >= maxIterations) {
                  return i;
               }

               if (j < maxDepth) {
                  nextQueuer.accept(blockPos, (queuedPos) -> {
                     queue.add(Pair.of(queuedPos, j + 1));
                  });
               }
            }
         }
      }

      return i;
   }

   // $FF: synthetic method
   public Vec3i crossProduct(final Vec3i vec) {
      return this.crossProduct(vec);
   }

   // $FF: synthetic method
   public Vec3i offset(final Direction.Axis axis, final int distance) {
      return this.offset(axis, distance);
   }

   // $FF: synthetic method
   public Vec3i offset(final Direction direction, final int distance) {
      return this.offset(direction, distance);
   }

   // $FF: synthetic method
   public Vec3i offset(final Direction direction) {
      return this.offset(direction);
   }

   // $FF: synthetic method
   public Vec3i east(final int distance) {
      return this.east(distance);
   }

   // $FF: synthetic method
   public Vec3i east() {
      return this.east();
   }

   // $FF: synthetic method
   public Vec3i west(final int distance) {
      return this.west(distance);
   }

   // $FF: synthetic method
   public Vec3i west() {
      return this.west();
   }

   // $FF: synthetic method
   public Vec3i south(final int distance) {
      return this.south(distance);
   }

   // $FF: synthetic method
   public Vec3i south() {
      return this.south();
   }

   // $FF: synthetic method
   public Vec3i north(final int distance) {
      return this.north(distance);
   }

   // $FF: synthetic method
   public Vec3i north() {
      return this.north();
   }

   // $FF: synthetic method
   public Vec3i down(final int distance) {
      return this.down(distance);
   }

   // $FF: synthetic method
   public Vec3i down() {
      return this.down();
   }

   // $FF: synthetic method
   public Vec3i up(final int distance) {
      return this.up(distance);
   }

   // $FF: synthetic method
   public Vec3i up() {
      return this.up();
   }

   // $FF: synthetic method
   public Vec3i multiply(final int scale) {
      return this.multiply(scale);
   }

   // $FF: synthetic method
   public Vec3i subtract(final Vec3i vec) {
      return this.subtract(vec);
   }

   // $FF: synthetic method
   public Vec3i add(final Vec3i vec) {
      return this.add(vec);
   }

   // $FF: synthetic method
   public Vec3i add(final int x, final int y, final int z) {
      return this.add(x, y, z);
   }

   static {
      CODEC = Codec.INT_STREAM.comapFlatMap((stream) -> {
         return Util.decodeFixedLengthArray((IntStream)stream, 3).map((values) -> {
            return new BlockPos(values[0], values[1], values[2]);
         });
      }, (pos) -> {
         return IntStream.of(new int[]{pos.getX(), pos.getY(), pos.getZ()});
      }).stable();
      PACKET_CODEC = new PacketCodec() {
         public BlockPos decode(ByteBuf byteBuf) {
            return PacketByteBuf.readBlockPos(byteBuf);
         }

         public void encode(ByteBuf byteBuf, BlockPos blockPos) {
            PacketByteBuf.writeBlockPos(byteBuf, blockPos);
         }

         // $FF: synthetic method
         public void encode(final Object object, final Object object2) {
            this.encode((ByteBuf)object, (BlockPos)object2);
         }

         // $FF: synthetic method
         public Object decode(final Object object) {
            return this.decode((ByteBuf)object);
         }
      };
      LOGGER = LogUtils.getLogger();
      ORIGIN = new BlockPos(0, 0, 0);
      SIZE_BITS_XZ = 1 + MathHelper.floorLog2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
      SIZE_BITS_Y = 64 - 2 * SIZE_BITS_XZ;
      BITS_X = (1L << SIZE_BITS_XZ) - 1L;
      BITS_Y = (1L << SIZE_BITS_Y) - 1L;
      BITS_Z = (1L << SIZE_BITS_XZ) - 1L;
      BIT_SHIFT_Z = SIZE_BITS_Y;
      BIT_SHIFT_X = SIZE_BITS_Y + SIZE_BITS_XZ;
      MAX_XZ = (1 << SIZE_BITS_XZ) / 2 - 1;
   }

   public static class Mutable extends BlockPos {
      public Mutable() {
         this(0, 0, 0);
      }

      public Mutable(int i, int j, int k) {
         super(i, j, k);
      }

      public Mutable(double x, double y, double z) {
         this(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
      }

      public BlockPos add(int i, int j, int k) {
         return super.add(i, j, k).toImmutable();
      }

      public BlockPos multiply(int i) {
         return super.multiply(i).toImmutable();
      }

      public BlockPos offset(Direction direction, int i) {
         return super.offset(direction, i).toImmutable();
      }

      public BlockPos offset(Direction.Axis axis, int i) {
         return super.offset(axis, i).toImmutable();
      }

      public BlockPos rotate(BlockRotation rotation) {
         return super.rotate(rotation).toImmutable();
      }

      public Mutable set(int x, int y, int z) {
         this.setX(x);
         this.setY(y);
         this.setZ(z);
         return this;
      }

      public Mutable set(double x, double y, double z) {
         return this.set(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
      }

      public Mutable set(Vec3i pos) {
         return this.set(pos.getX(), pos.getY(), pos.getZ());
      }

      public Mutable set(long pos) {
         return this.set(unpackLongX(pos), unpackLongY(pos), unpackLongZ(pos));
      }

      public Mutable set(AxisCycleDirection axis, int x, int y, int z) {
         return this.set(axis.choose(x, y, z, Direction.Axis.X), axis.choose(x, y, z, Direction.Axis.Y), axis.choose(x, y, z, Direction.Axis.Z));
      }

      public Mutable set(Vec3i pos, Direction direction) {
         return this.set(pos.getX() + direction.getOffsetX(), pos.getY() + direction.getOffsetY(), pos.getZ() + direction.getOffsetZ());
      }

      public Mutable set(Vec3i pos, int x, int y, int z) {
         return this.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
      }

      public Mutable set(Vec3i vec1, Vec3i vec2) {
         return this.set(vec1.getX() + vec2.getX(), vec1.getY() + vec2.getY(), vec1.getZ() + vec2.getZ());
      }

      public Mutable move(Direction direction) {
         return this.move(direction, 1);
      }

      public Mutable move(Direction direction, int distance) {
         return this.set(this.getX() + direction.getOffsetX() * distance, this.getY() + direction.getOffsetY() * distance, this.getZ() + direction.getOffsetZ() * distance);
      }

      public Mutable move(int dx, int dy, int dz) {
         return this.set(this.getX() + dx, this.getY() + dy, this.getZ() + dz);
      }

      public Mutable move(Vec3i vec) {
         return this.set(this.getX() + vec.getX(), this.getY() + vec.getY(), this.getZ() + vec.getZ());
      }

      public Mutable clamp(Direction.Axis axis, int min, int max) {
         switch (axis) {
            case X:
               return this.set(MathHelper.clamp(this.getX(), min, max), this.getY(), this.getZ());
            case Y:
               return this.set(this.getX(), MathHelper.clamp(this.getY(), min, max), this.getZ());
            case Z:
               return this.set(this.getX(), this.getY(), MathHelper.clamp(this.getZ(), min, max));
            default:
               throw new IllegalStateException("Unable to clamp axis " + String.valueOf(axis));
         }
      }

      public Mutable setX(int i) {
         super.setX(i);
         return this;
      }

      public Mutable setY(int i) {
         super.setY(i);
         return this;
      }

      public Mutable setZ(int i) {
         super.setZ(i);
         return this;
      }

      public BlockPos toImmutable() {
         return new BlockPos(this);
      }

      // $FF: synthetic method
      public Vec3i crossProduct(final Vec3i vec) {
         return super.crossProduct(vec);
      }

      // $FF: synthetic method
      public Vec3i offset(final Direction.Axis axis, final int distance) {
         return this.offset(axis, distance);
      }

      // $FF: synthetic method
      public Vec3i offset(final Direction direction, final int distance) {
         return this.offset(direction, distance);
      }

      // $FF: synthetic method
      public Vec3i offset(final Direction direction) {
         return super.offset(direction);
      }

      // $FF: synthetic method
      public Vec3i east(final int distance) {
         return super.east(distance);
      }

      // $FF: synthetic method
      public Vec3i east() {
         return super.east();
      }

      // $FF: synthetic method
      public Vec3i west(final int distance) {
         return super.west(distance);
      }

      // $FF: synthetic method
      public Vec3i west() {
         return super.west();
      }

      // $FF: synthetic method
      public Vec3i south(final int distance) {
         return super.south(distance);
      }

      // $FF: synthetic method
      public Vec3i south() {
         return super.south();
      }

      // $FF: synthetic method
      public Vec3i north(final int distance) {
         return super.north(distance);
      }

      // $FF: synthetic method
      public Vec3i north() {
         return super.north();
      }

      // $FF: synthetic method
      public Vec3i down(final int distance) {
         return super.down(distance);
      }

      // $FF: synthetic method
      public Vec3i down() {
         return super.down();
      }

      // $FF: synthetic method
      public Vec3i up(final int distance) {
         return super.up(distance);
      }

      // $FF: synthetic method
      public Vec3i up() {
         return super.up();
      }

      // $FF: synthetic method
      public Vec3i multiply(final int scale) {
         return this.multiply(scale);
      }

      // $FF: synthetic method
      public Vec3i subtract(final Vec3i vec) {
         return super.subtract(vec);
      }

      // $FF: synthetic method
      public Vec3i add(final Vec3i vec) {
         return super.add(vec);
      }

      // $FF: synthetic method
      public Vec3i add(final int x, final int y, final int z) {
         return this.add(x, y, z);
      }

      // $FF: synthetic method
      public Vec3i setZ(final int z) {
         return this.setZ(z);
      }

      // $FF: synthetic method
      public Vec3i setY(final int y) {
         return this.setY(y);
      }

      // $FF: synthetic method
      public Vec3i setX(final int x) {
         return this.setX(x);
      }
   }

   public static enum IterationState {
      ACCEPT,
      SKIP,
      STOP;

      // $FF: synthetic method
      private static IterationState[] method_65259() {
         return new IterationState[]{ACCEPT, SKIP, STOP};
      }
   }
}
