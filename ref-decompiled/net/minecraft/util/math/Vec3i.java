package net.minecraft.util.math;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.stream.IntStream;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Unmodifiable;

@Unmodifiable
public class Vec3i implements Comparable {
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;
   public static final Vec3i ZERO;
   private int x;
   private int y;
   private int z;

   public static Codec createOffsetCodec(int maxAbsValue) {
      return CODEC.validate((vec) -> {
         return Math.abs(vec.getX()) < maxAbsValue && Math.abs(vec.getY()) < maxAbsValue && Math.abs(vec.getZ()) < maxAbsValue ? DataResult.success(vec) : DataResult.error(() -> {
            return "Position out of range, expected at most " + maxAbsValue + ": " + String.valueOf(vec);
         });
      });
   }

   public Vec3i(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof Vec3i)) {
         return false;
      } else {
         Vec3i vec3i = (Vec3i)o;
         if (this.getX() != vec3i.getX()) {
            return false;
         } else if (this.getY() != vec3i.getY()) {
            return false;
         } else {
            return this.getZ() == vec3i.getZ();
         }
      }
   }

   public int hashCode() {
      return (this.getY() + this.getZ() * 31) * 31 + this.getX();
   }

   public int compareTo(Vec3i vec3i) {
      if (this.getY() == vec3i.getY()) {
         return this.getZ() == vec3i.getZ() ? this.getX() - vec3i.getX() : this.getZ() - vec3i.getZ();
      } else {
         return this.getY() - vec3i.getY();
      }
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getZ() {
      return this.z;
   }

   protected Vec3i setX(int x) {
      this.x = x;
      return this;
   }

   protected Vec3i setY(int y) {
      this.y = y;
      return this;
   }

   protected Vec3i setZ(int z) {
      this.z = z;
      return this;
   }

   public Vec3i add(int x, int y, int z) {
      return x == 0 && y == 0 && z == 0 ? this : new Vec3i(this.getX() + x, this.getY() + y, this.getZ() + z);
   }

   public Vec3i add(Vec3i vec) {
      return this.add(vec.getX(), vec.getY(), vec.getZ());
   }

   public Vec3i subtract(Vec3i vec) {
      return this.add(-vec.getX(), -vec.getY(), -vec.getZ());
   }

   public Vec3i multiply(int scale) {
      if (scale == 1) {
         return this;
      } else {
         return scale == 0 ? ZERO : new Vec3i(this.getX() * scale, this.getY() * scale, this.getZ() * scale);
      }
   }

   public Vec3i up() {
      return this.up(1);
   }

   public Vec3i up(int distance) {
      return this.offset(Direction.UP, distance);
   }

   public Vec3i down() {
      return this.down(1);
   }

   public Vec3i down(int distance) {
      return this.offset(Direction.DOWN, distance);
   }

   public Vec3i north() {
      return this.north(1);
   }

   public Vec3i north(int distance) {
      return this.offset(Direction.NORTH, distance);
   }

   public Vec3i south() {
      return this.south(1);
   }

   public Vec3i south(int distance) {
      return this.offset(Direction.SOUTH, distance);
   }

   public Vec3i west() {
      return this.west(1);
   }

   public Vec3i west(int distance) {
      return this.offset(Direction.WEST, distance);
   }

   public Vec3i east() {
      return this.east(1);
   }

   public Vec3i east(int distance) {
      return this.offset(Direction.EAST, distance);
   }

   public Vec3i offset(Direction direction) {
      return this.offset((Direction)direction, 1);
   }

   public Vec3i offset(Direction direction, int distance) {
      return distance == 0 ? this : new Vec3i(this.getX() + direction.getOffsetX() * distance, this.getY() + direction.getOffsetY() * distance, this.getZ() + direction.getOffsetZ() * distance);
   }

   public Vec3i offset(Direction.Axis axis, int distance) {
      if (distance == 0) {
         return this;
      } else {
         int i = axis == Direction.Axis.X ? distance : 0;
         int j = axis == Direction.Axis.Y ? distance : 0;
         int k = axis == Direction.Axis.Z ? distance : 0;
         return new Vec3i(this.getX() + i, this.getY() + j, this.getZ() + k);
      }
   }

   public Vec3i crossProduct(Vec3i vec) {
      return new Vec3i(this.getY() * vec.getZ() - this.getZ() * vec.getY(), this.getZ() * vec.getX() - this.getX() * vec.getZ(), this.getX() * vec.getY() - this.getY() * vec.getX());
   }

   public boolean isWithinDistance(Vec3i vec, double distance) {
      return this.getSquaredDistance(vec) < MathHelper.square(distance);
   }

   public boolean isWithinDistance(Position pos, double distance) {
      return this.getSquaredDistance(pos) < MathHelper.square(distance);
   }

   public double getSquaredDistance(Vec3i vec) {
      return this.getSquaredDistance((double)vec.getX(), (double)vec.getY(), (double)vec.getZ());
   }

   public double getSquaredDistance(Position pos) {
      return this.getSquaredDistanceFromCenter(pos.getX(), pos.getY(), pos.getZ());
   }

   public double getSquaredDistanceFromCenter(double x, double y, double z) {
      double d = (double)this.getX() + 0.5 - x;
      double e = (double)this.getY() + 0.5 - y;
      double f = (double)this.getZ() + 0.5 - z;
      return d * d + e * e + f * f;
   }

   public double getSquaredDistance(double x, double y, double z) {
      double d = (double)this.getX() - x;
      double e = (double)this.getY() - y;
      double f = (double)this.getZ() - z;
      return d * d + e * e + f * f;
   }

   public int getManhattanDistance(Vec3i vec) {
      float f = (float)Math.abs(vec.getX() - this.getX());
      float g = (float)Math.abs(vec.getY() - this.getY());
      float h = (float)Math.abs(vec.getZ() - this.getZ());
      return (int)(f + g + h);
   }

   public int getChebyshevDistance(Vec3i vec) {
      int i = Math.abs(this.getX() - vec.getX());
      int j = Math.abs(this.getY() - vec.getY());
      int k = Math.abs(this.getZ() - vec.getZ());
      return Math.max(Math.max(i, j), k);
   }

   public int getComponentAlongAxis(Direction.Axis axis) {
      return axis.choose(this.x, this.y, this.z);
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
   }

   public String toShortString() {
      int var10000 = this.getX();
      return "" + var10000 + ", " + this.getY() + ", " + this.getZ();
   }

   // $FF: synthetic method
   public int compareTo(final Object vec) {
      return this.compareTo((Vec3i)vec);
   }

   static {
      CODEC = Codec.INT_STREAM.comapFlatMap((stream) -> {
         return Util.decodeFixedLengthArray((IntStream)stream, 3).map((coordinates) -> {
            return new Vec3i(coordinates[0], coordinates[1], coordinates[2]);
         });
      }, (vec) -> {
         return IntStream.of(new int[]{vec.getX(), vec.getY(), vec.getZ()});
      });
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, Vec3i::getX, PacketCodecs.VAR_INT, Vec3i::getY, PacketCodecs.VAR_INT, Vec3i::getZ, Vec3i::new);
      ZERO = new Vec3i(0, 0, 0);
   }
}
