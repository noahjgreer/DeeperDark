package net.minecraft.util.math;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.hit.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Box {
   private static final double EPSILON = 1.0E-7;
   public final double minX;
   public final double minY;
   public final double minZ;
   public final double maxX;
   public final double maxY;
   public final double maxZ;

   public Box(double x1, double y1, double z1, double x2, double y2, double z2) {
      this.minX = Math.min(x1, x2);
      this.minY = Math.min(y1, y2);
      this.minZ = Math.min(z1, z2);
      this.maxX = Math.max(x1, x2);
      this.maxY = Math.max(y1, y2);
      this.maxZ = Math.max(z1, z2);
   }

   public Box(BlockPos pos) {
      this((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1));
   }

   public Box(Vec3d pos1, Vec3d pos2) {
      this(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z);
   }

   public static Box from(BlockBox mutable) {
      return new Box((double)mutable.getMinX(), (double)mutable.getMinY(), (double)mutable.getMinZ(), (double)(mutable.getMaxX() + 1), (double)(mutable.getMaxY() + 1), (double)(mutable.getMaxZ() + 1));
   }

   public static Box from(Vec3d pos) {
      return new Box(pos.x, pos.y, pos.z, pos.x + 1.0, pos.y + 1.0, pos.z + 1.0);
   }

   public static Box enclosing(BlockPos pos1, BlockPos pos2) {
      return new Box((double)Math.min(pos1.getX(), pos2.getX()), (double)Math.min(pos1.getY(), pos2.getY()), (double)Math.min(pos1.getZ(), pos2.getZ()), (double)(Math.max(pos1.getX(), pos2.getX()) + 1), (double)(Math.max(pos1.getY(), pos2.getY()) + 1), (double)(Math.max(pos1.getZ(), pos2.getZ()) + 1));
   }

   public Box withMinX(double minX) {
      return new Box(minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
   }

   public Box withMinY(double minY) {
      return new Box(this.minX, minY, this.minZ, this.maxX, this.maxY, this.maxZ);
   }

   public Box withMinZ(double minZ) {
      return new Box(this.minX, this.minY, minZ, this.maxX, this.maxY, this.maxZ);
   }

   public Box withMaxX(double maxX) {
      return new Box(this.minX, this.minY, this.minZ, maxX, this.maxY, this.maxZ);
   }

   public Box withMaxY(double maxY) {
      return new Box(this.minX, this.minY, this.minZ, this.maxX, maxY, this.maxZ);
   }

   public Box withMaxZ(double maxZ) {
      return new Box(this.minX, this.minY, this.minZ, this.maxX, this.maxY, maxZ);
   }

   public double getMin(Direction.Axis axis) {
      return axis.choose(this.minX, this.minY, this.minZ);
   }

   public double getMax(Direction.Axis axis) {
      return axis.choose(this.maxX, this.maxY, this.maxZ);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof Box)) {
         return false;
      } else {
         Box box = (Box)o;
         if (Double.compare(box.minX, this.minX) != 0) {
            return false;
         } else if (Double.compare(box.minY, this.minY) != 0) {
            return false;
         } else if (Double.compare(box.minZ, this.minZ) != 0) {
            return false;
         } else if (Double.compare(box.maxX, this.maxX) != 0) {
            return false;
         } else if (Double.compare(box.maxY, this.maxY) != 0) {
            return false;
         } else {
            return Double.compare(box.maxZ, this.maxZ) == 0;
         }
      }
   }

   public int hashCode() {
      long l = Double.doubleToLongBits(this.minX);
      int i = (int)(l ^ l >>> 32);
      l = Double.doubleToLongBits(this.minY);
      i = 31 * i + (int)(l ^ l >>> 32);
      l = Double.doubleToLongBits(this.minZ);
      i = 31 * i + (int)(l ^ l >>> 32);
      l = Double.doubleToLongBits(this.maxX);
      i = 31 * i + (int)(l ^ l >>> 32);
      l = Double.doubleToLongBits(this.maxY);
      i = 31 * i + (int)(l ^ l >>> 32);
      l = Double.doubleToLongBits(this.maxZ);
      i = 31 * i + (int)(l ^ l >>> 32);
      return i;
   }

   public Box shrink(double x, double y, double z) {
      double d = this.minX;
      double e = this.minY;
      double f = this.minZ;
      double g = this.maxX;
      double h = this.maxY;
      double i = this.maxZ;
      if (x < 0.0) {
         d -= x;
      } else if (x > 0.0) {
         g -= x;
      }

      if (y < 0.0) {
         e -= y;
      } else if (y > 0.0) {
         h -= y;
      }

      if (z < 0.0) {
         f -= z;
      } else if (z > 0.0) {
         i -= z;
      }

      return new Box(d, e, f, g, h, i);
   }

   public Box stretch(Vec3d scale) {
      return this.stretch(scale.x, scale.y, scale.z);
   }

   public Box stretch(double x, double y, double z) {
      double d = this.minX;
      double e = this.minY;
      double f = this.minZ;
      double g = this.maxX;
      double h = this.maxY;
      double i = this.maxZ;
      if (x < 0.0) {
         d += x;
      } else if (x > 0.0) {
         g += x;
      }

      if (y < 0.0) {
         e += y;
      } else if (y > 0.0) {
         h += y;
      }

      if (z < 0.0) {
         f += z;
      } else if (z > 0.0) {
         i += z;
      }

      return new Box(d, e, f, g, h, i);
   }

   public Box expand(double x, double y, double z) {
      double d = this.minX - x;
      double e = this.minY - y;
      double f = this.minZ - z;
      double g = this.maxX + x;
      double h = this.maxY + y;
      double i = this.maxZ + z;
      return new Box(d, e, f, g, h, i);
   }

   public Box expand(double value) {
      return this.expand(value, value, value);
   }

   public Box intersection(Box box) {
      double d = Math.max(this.minX, box.minX);
      double e = Math.max(this.minY, box.minY);
      double f = Math.max(this.minZ, box.minZ);
      double g = Math.min(this.maxX, box.maxX);
      double h = Math.min(this.maxY, box.maxY);
      double i = Math.min(this.maxZ, box.maxZ);
      return new Box(d, e, f, g, h, i);
   }

   public Box union(Box box) {
      double d = Math.min(this.minX, box.minX);
      double e = Math.min(this.minY, box.minY);
      double f = Math.min(this.minZ, box.minZ);
      double g = Math.max(this.maxX, box.maxX);
      double h = Math.max(this.maxY, box.maxY);
      double i = Math.max(this.maxZ, box.maxZ);
      return new Box(d, e, f, g, h, i);
   }

   public Box offset(double x, double y, double z) {
      return new Box(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
   }

   public Box offset(BlockPos blockPos) {
      return new Box(this.minX + (double)blockPos.getX(), this.minY + (double)blockPos.getY(), this.minZ + (double)blockPos.getZ(), this.maxX + (double)blockPos.getX(), this.maxY + (double)blockPos.getY(), this.maxZ + (double)blockPos.getZ());
   }

   public Box offset(Vec3d vec) {
      return this.offset(vec.x, vec.y, vec.z);
   }

   public Box offset(Vector3f offset) {
      return this.offset((double)offset.x, (double)offset.y, (double)offset.z);
   }

   public boolean intersects(Box box) {
      return this.intersects(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
   }

   public boolean intersects(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      return this.minX < maxX && this.maxX > minX && this.minY < maxY && this.maxY > minY && this.minZ < maxZ && this.maxZ > minZ;
   }

   public boolean intersects(Vec3d pos1, Vec3d pos2) {
      return this.intersects(Math.min(pos1.x, pos2.x), Math.min(pos1.y, pos2.y), Math.min(pos1.z, pos2.z), Math.max(pos1.x, pos2.x), Math.max(pos1.y, pos2.y), Math.max(pos1.z, pos2.z));
   }

   public boolean contains(BlockPos pos) {
      return this.intersects((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1));
   }

   public boolean contains(Vec3d pos) {
      return this.contains(pos.x, pos.y, pos.z);
   }

   public boolean contains(double x, double y, double z) {
      return x >= this.minX && x < this.maxX && y >= this.minY && y < this.maxY && z >= this.minZ && z < this.maxZ;
   }

   public double getAverageSideLength() {
      double d = this.getLengthX();
      double e = this.getLengthY();
      double f = this.getLengthZ();
      return (d + e + f) / 3.0;
   }

   public double getLengthX() {
      return this.maxX - this.minX;
   }

   public double getLengthY() {
      return this.maxY - this.minY;
   }

   public double getLengthZ() {
      return this.maxZ - this.minZ;
   }

   public Box contract(double x, double y, double z) {
      return this.expand(-x, -y, -z);
   }

   public Box contract(double value) {
      return this.expand(-value);
   }

   public Optional raycast(Vec3d from, Vec3d to) {
      return raycast(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ, from, to);
   }

   public static Optional raycast(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Vec3d from, Vec3d to) {
      double[] ds = new double[]{1.0};
      double d = to.x - from.x;
      double e = to.y - from.y;
      double f = to.z - from.z;
      Direction direction = traceCollisionSide(minX, minY, minZ, maxX, maxY, maxZ, from, ds, (Direction)null, d, e, f);
      if (direction == null) {
         return Optional.empty();
      } else {
         double g = ds[0];
         return Optional.of(from.add(g * d, g * e, g * f));
      }
   }

   @Nullable
   public static BlockHitResult raycast(Iterable boxes, Vec3d from, Vec3d to, BlockPos pos) {
      double[] ds = new double[]{1.0};
      Direction direction = null;
      double d = to.x - from.x;
      double e = to.y - from.y;
      double f = to.z - from.z;

      Box box;
      for(Iterator var12 = boxes.iterator(); var12.hasNext(); direction = traceCollisionSide(box.offset(pos), from, ds, direction, d, e, f)) {
         box = (Box)var12.next();
      }

      if (direction == null) {
         return null;
      } else {
         double g = ds[0];
         return new BlockHitResult(from.add(g * d, g * e, g * f), direction, pos, false);
      }
   }

   @Nullable
   private static Direction traceCollisionSide(Box box, Vec3d intersectingVector, double[] traceDistanceResult, @Nullable Direction approachDirection, double deltaX, double deltaY, double deltaZ) {
      return traceCollisionSide(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, intersectingVector, traceDistanceResult, approachDirection, deltaX, deltaY, deltaZ);
   }

   @Nullable
   private static Direction traceCollisionSide(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Vec3d intersectingVector, double[] traceDistanceResult, @Nullable Direction approachDirection, double deltaX, double deltaY, double deltaZ) {
      if (deltaX > 1.0E-7) {
         approachDirection = traceCollisionSide(traceDistanceResult, approachDirection, deltaX, deltaY, deltaZ, minX, minY, maxY, minZ, maxZ, Direction.WEST, intersectingVector.x, intersectingVector.y, intersectingVector.z);
      } else if (deltaX < -1.0E-7) {
         approachDirection = traceCollisionSide(traceDistanceResult, approachDirection, deltaX, deltaY, deltaZ, maxX, minY, maxY, minZ, maxZ, Direction.EAST, intersectingVector.x, intersectingVector.y, intersectingVector.z);
      }

      if (deltaY > 1.0E-7) {
         approachDirection = traceCollisionSide(traceDistanceResult, approachDirection, deltaY, deltaZ, deltaX, minY, minZ, maxZ, minX, maxX, Direction.DOWN, intersectingVector.y, intersectingVector.z, intersectingVector.x);
      } else if (deltaY < -1.0E-7) {
         approachDirection = traceCollisionSide(traceDistanceResult, approachDirection, deltaY, deltaZ, deltaX, maxY, minZ, maxZ, minX, maxX, Direction.UP, intersectingVector.y, intersectingVector.z, intersectingVector.x);
      }

      if (deltaZ > 1.0E-7) {
         approachDirection = traceCollisionSide(traceDistanceResult, approachDirection, deltaZ, deltaX, deltaY, minZ, minX, maxX, minY, maxY, Direction.NORTH, intersectingVector.z, intersectingVector.x, intersectingVector.y);
      } else if (deltaZ < -1.0E-7) {
         approachDirection = traceCollisionSide(traceDistanceResult, approachDirection, deltaZ, deltaX, deltaY, maxZ, minX, maxX, minY, maxY, Direction.SOUTH, intersectingVector.z, intersectingVector.x, intersectingVector.y);
      }

      return approachDirection;
   }

   @Nullable
   private static Direction traceCollisionSide(double[] traceDistanceResult, @Nullable Direction approachDirection, double deltaX, double deltaY, double deltaZ, double begin, double minX, double maxX, double minZ, double maxZ, Direction resultDirection, double startX, double startY, double startZ) {
      double d = (begin - startX) / deltaX;
      double e = startY + d * deltaY;
      double f = startZ + d * deltaZ;
      if (0.0 < d && d < traceDistanceResult[0] && minX - 1.0E-7 < e && e < maxX + 1.0E-7 && minZ - 1.0E-7 < f && f < maxZ + 1.0E-7) {
         traceDistanceResult[0] = d;
         return resultDirection;
      } else {
         return approachDirection;
      }
   }

   public boolean collides(Vec3d pos, List boundingBoxes) {
      Vec3d vec3d = this.getCenter();
      Vec3d vec3d2 = vec3d.add(pos);
      Iterator var5 = boundingBoxes.iterator();

      Box box2;
      do {
         if (!var5.hasNext()) {
            return false;
         }

         Box box = (Box)var5.next();
         box2 = box.expand(this.getLengthX() * 0.5, this.getLengthY() * 0.5, this.getLengthZ() * 0.5);
         if (box2.contains(vec3d2) || box2.contains(vec3d)) {
            return true;
         }
      } while(!box2.raycast(vec3d, vec3d2).isPresent());

      return true;
   }

   public double squaredMagnitude(Vec3d pos) {
      double d = Math.max(Math.max(this.minX - pos.x, pos.x - this.maxX), 0.0);
      double e = Math.max(Math.max(this.minY - pos.y, pos.y - this.maxY), 0.0);
      double f = Math.max(Math.max(this.minZ - pos.z, pos.z - this.maxZ), 0.0);
      return MathHelper.squaredMagnitude(d, e, f);
   }

   public double squaredMagnitude(Box other) {
      double d = Math.max(Math.max(this.minX - other.maxX, other.minX - this.maxX), 0.0);
      double e = Math.max(Math.max(this.minY - other.maxY, other.minY - this.maxY), 0.0);
      double f = Math.max(Math.max(this.minZ - other.maxZ, other.minZ - this.maxZ), 0.0);
      return MathHelper.squaredMagnitude(d, e, f);
   }

   public String toString() {
      return "AABB[" + this.minX + ", " + this.minY + ", " + this.minZ + "] -> [" + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
   }

   public boolean isNaN() {
      return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.minZ) || Double.isNaN(this.maxX) || Double.isNaN(this.maxY) || Double.isNaN(this.maxZ);
   }

   public Vec3d getCenter() {
      return new Vec3d(MathHelper.lerp(0.5, this.minX, this.maxX), MathHelper.lerp(0.5, this.minY, this.maxY), MathHelper.lerp(0.5, this.minZ, this.maxZ));
   }

   public Vec3d getHorizontalCenter() {
      return new Vec3d(MathHelper.lerp(0.5, this.minX, this.maxX), this.minY, MathHelper.lerp(0.5, this.minZ, this.maxZ));
   }

   public Vec3d getMinPos() {
      return new Vec3d(this.minX, this.minY, this.minZ);
   }

   public Vec3d getMaxPos() {
      return new Vec3d(this.maxX, this.maxY, this.maxZ);
   }

   public static Box of(Vec3d center, double dx, double dy, double dz) {
      return new Box(center.x - dx / 2.0, center.y - dy / 2.0, center.z - dz / 2.0, center.x + dx / 2.0, center.y + dy / 2.0, center.z + dz / 2.0);
   }

   public static class Builder {
      private float minX = Float.POSITIVE_INFINITY;
      private float minY = Float.POSITIVE_INFINITY;
      private float minZ = Float.POSITIVE_INFINITY;
      private float maxX = Float.NEGATIVE_INFINITY;
      private float maxY = Float.NEGATIVE_INFINITY;
      private float maxZ = Float.NEGATIVE_INFINITY;

      public void encompass(Vector3fc vec) {
         this.minX = Math.min(this.minX, vec.x());
         this.minY = Math.min(this.minY, vec.y());
         this.minZ = Math.min(this.minZ, vec.z());
         this.maxX = Math.max(this.maxX, vec.x());
         this.maxY = Math.max(this.maxY, vec.y());
         this.maxZ = Math.max(this.maxZ, vec.z());
      }

      public Box build() {
         return new Box((double)this.minX, (double)this.minY, (double)this.minZ, (double)this.maxX, (double)this.maxY, (double)this.maxZ);
      }
   }
}
