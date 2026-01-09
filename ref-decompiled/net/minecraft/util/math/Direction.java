package net.minecraft.util.math;

import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public enum Direction implements StringIdentifiable {
   DOWN(0, 1, -1, "down", Direction.AxisDirection.NEGATIVE, Direction.Axis.Y, new Vec3i(0, -1, 0)),
   UP(1, 0, -1, "up", Direction.AxisDirection.POSITIVE, Direction.Axis.Y, new Vec3i(0, 1, 0)),
   NORTH(2, 3, 2, "north", Direction.AxisDirection.NEGATIVE, Direction.Axis.Z, new Vec3i(0, 0, -1)),
   SOUTH(3, 2, 0, "south", Direction.AxisDirection.POSITIVE, Direction.Axis.Z, new Vec3i(0, 0, 1)),
   WEST(4, 5, 1, "west", Direction.AxisDirection.NEGATIVE, Direction.Axis.X, new Vec3i(-1, 0, 0)),
   EAST(5, 4, 3, "east", Direction.AxisDirection.POSITIVE, Direction.Axis.X, new Vec3i(1, 0, 0));

   public static final StringIdentifiable.EnumCodec CODEC = StringIdentifiable.createCodec(Direction::values);
   public static final Codec VERTICAL_CODEC = CODEC.validate(Direction::validateVertical);
   public static final IntFunction INDEX_TO_VALUE_FUNCTION = ValueLists.createIndexToValueFunction(Direction::getIndex, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.WRAP);
   public static final PacketCodec PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_VALUE_FUNCTION, Direction::getIndex);
   /** @deprecated */
   @Deprecated
   public static final Codec INDEX_CODEC = Codec.BYTE.xmap(Direction::byIndex, (direction) -> {
      return (byte)direction.getIndex();
   });
   /** @deprecated */
   @Deprecated
   public static final Codec HORIZONTAL_QUARTER_TURNS_CODEC = Codec.BYTE.xmap(Direction::fromHorizontalQuarterTurns, (direction) -> {
      return (byte)direction.getHorizontalQuarterTurns();
   });
   private final int index;
   private final int oppositeIndex;
   private final int horizontalQuarterTurns;
   private final String id;
   private final Axis axis;
   private final AxisDirection direction;
   private final Vec3i vec3i;
   private final Vec3d doubleVector;
   private final Vector3fc floatVector;
   private static final Direction[] ALL = values();
   private static final Direction[] VALUES = (Direction[])Arrays.stream(ALL).sorted(Comparator.comparingInt((direction) -> {
      return direction.index;
   })).toArray((i) -> {
      return new Direction[i];
   });
   private static final Direction[] HORIZONTAL = (Direction[])Arrays.stream(ALL).filter((direction) -> {
      return direction.getAxis().isHorizontal();
   }).sorted(Comparator.comparingInt((direction) -> {
      return direction.horizontalQuarterTurns;
   })).toArray((i) -> {
      return new Direction[i];
   });

   private Direction(final int index, final int oppositeIndex, final int horizontalQuarterTurns, final String id, final AxisDirection direction, final Axis axis, final Vec3i vector) {
      this.index = index;
      this.horizontalQuarterTurns = horizontalQuarterTurns;
      this.oppositeIndex = oppositeIndex;
      this.id = id;
      this.axis = axis;
      this.direction = direction;
      this.vec3i = vector;
      this.doubleVector = Vec3d.of(vector);
      this.floatVector = new Vector3f((float)vector.getX(), (float)vector.getY(), (float)vector.getZ());
   }

   public static Direction[] getEntityFacingOrder(Entity entity) {
      float f = entity.getPitch(1.0F) * 0.017453292F;
      float g = -entity.getYaw(1.0F) * 0.017453292F;
      float h = MathHelper.sin(f);
      float i = MathHelper.cos(f);
      float j = MathHelper.sin(g);
      float k = MathHelper.cos(g);
      boolean bl = j > 0.0F;
      boolean bl2 = h < 0.0F;
      boolean bl3 = k > 0.0F;
      float l = bl ? j : -j;
      float m = bl2 ? -h : h;
      float n = bl3 ? k : -k;
      float o = l * i;
      float p = n * i;
      Direction direction = bl ? EAST : WEST;
      Direction direction2 = bl2 ? UP : DOWN;
      Direction direction3 = bl3 ? SOUTH : NORTH;
      if (l > n) {
         if (m > o) {
            return listClosest(direction2, direction, direction3);
         } else {
            return p > m ? listClosest(direction, direction3, direction2) : listClosest(direction, direction2, direction3);
         }
      } else if (m > p) {
         return listClosest(direction2, direction3, direction);
      } else {
         return o > m ? listClosest(direction3, direction, direction2) : listClosest(direction3, direction2, direction);
      }
   }

   private static Direction[] listClosest(Direction first, Direction second, Direction third) {
      return new Direction[]{first, second, third, third.getOpposite(), second.getOpposite(), first.getOpposite()};
   }

   public static Direction transform(Matrix4fc matrix, Direction direction) {
      Vector3f vector3f = matrix.transformDirection(direction.floatVector, new Vector3f());
      return getFacing(vector3f.x(), vector3f.y(), vector3f.z());
   }

   public static Collection shuffle(Random random) {
      return Util.copyShuffled((Object[])values(), random);
   }

   public static Stream stream() {
      return Stream.of(ALL);
   }

   public static float getHorizontalDegreesOrThrow(Direction direction) {
      float var10000;
      switch (direction.ordinal()) {
         case 2:
            var10000 = 180.0F;
            break;
         case 3:
            var10000 = 0.0F;
            break;
         case 4:
            var10000 = 90.0F;
            break;
         case 5:
            var10000 = -90.0F;
            break;
         default:
            throw new IllegalStateException("No y-Rot for vertical axis: " + String.valueOf(direction));
      }

      return var10000;
   }

   public Quaternionf getRotationQuaternion() {
      Quaternionf var10000;
      switch (this.ordinal()) {
         case 0:
            var10000 = (new Quaternionf()).rotationX(3.1415927F);
            break;
         case 1:
            var10000 = new Quaternionf();
            break;
         case 2:
            var10000 = (new Quaternionf()).rotationXYZ(1.5707964F, 0.0F, 3.1415927F);
            break;
         case 3:
            var10000 = (new Quaternionf()).rotationX(1.5707964F);
            break;
         case 4:
            var10000 = (new Quaternionf()).rotationXYZ(1.5707964F, 0.0F, 1.5707964F);
            break;
         case 5:
            var10000 = (new Quaternionf()).rotationXYZ(1.5707964F, 0.0F, -1.5707964F);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public int getIndex() {
      return this.index;
   }

   public int getHorizontalQuarterTurns() {
      return this.horizontalQuarterTurns;
   }

   public AxisDirection getDirection() {
      return this.direction;
   }

   public static Direction getLookDirectionForAxis(Entity entity, Axis axis) {
      Direction var10000;
      switch (axis.ordinal()) {
         case 0:
            var10000 = EAST.pointsTo(entity.getYaw(1.0F)) ? EAST : WEST;
            break;
         case 1:
            var10000 = entity.getPitch(1.0F) < 0.0F ? UP : DOWN;
            break;
         case 2:
            var10000 = SOUTH.pointsTo(entity.getYaw(1.0F)) ? SOUTH : NORTH;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public Direction getOpposite() {
      return byIndex(this.oppositeIndex);
   }

   public Direction rotateClockwise(Axis axis) {
      Direction var10000;
      switch (axis.ordinal()) {
         case 0:
            var10000 = this != WEST && this != EAST ? this.rotateXClockwise() : this;
            break;
         case 1:
            var10000 = this != UP && this != DOWN ? this.rotateYClockwise() : this;
            break;
         case 2:
            var10000 = this != NORTH && this != SOUTH ? this.rotateZClockwise() : this;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public Direction rotateCounterclockwise(Axis axis) {
      Direction var10000;
      switch (axis.ordinal()) {
         case 0:
            var10000 = this != WEST && this != EAST ? this.rotateXCounterclockwise() : this;
            break;
         case 1:
            var10000 = this != UP && this != DOWN ? this.rotateYCounterclockwise() : this;
            break;
         case 2:
            var10000 = this != NORTH && this != SOUTH ? this.rotateZCounterclockwise() : this;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public Direction rotateYClockwise() {
      Direction var10000;
      switch (this.ordinal()) {
         case 2:
            var10000 = EAST;
            break;
         case 3:
            var10000 = WEST;
            break;
         case 4:
            var10000 = NORTH;
            break;
         case 5:
            var10000 = SOUTH;
            break;
         default:
            throw new IllegalStateException("Unable to get Y-rotated facing of " + String.valueOf(this));
      }

      return var10000;
   }

   private Direction rotateXClockwise() {
      Direction var10000;
      switch (this.ordinal()) {
         case 0:
            var10000 = SOUTH;
            break;
         case 1:
            var10000 = NORTH;
            break;
         case 2:
            var10000 = DOWN;
            break;
         case 3:
            var10000 = UP;
            break;
         default:
            throw new IllegalStateException("Unable to get X-rotated facing of " + String.valueOf(this));
      }

      return var10000;
   }

   private Direction rotateXCounterclockwise() {
      Direction var10000;
      switch (this.ordinal()) {
         case 0:
            var10000 = NORTH;
            break;
         case 1:
            var10000 = SOUTH;
            break;
         case 2:
            var10000 = UP;
            break;
         case 3:
            var10000 = DOWN;
            break;
         default:
            throw new IllegalStateException("Unable to get X-rotated facing of " + String.valueOf(this));
      }

      return var10000;
   }

   private Direction rotateZClockwise() {
      Direction var10000;
      switch (this.ordinal()) {
         case 0:
            var10000 = WEST;
            break;
         case 1:
            var10000 = EAST;
            break;
         case 2:
         case 3:
         default:
            throw new IllegalStateException("Unable to get Z-rotated facing of " + String.valueOf(this));
         case 4:
            var10000 = UP;
            break;
         case 5:
            var10000 = DOWN;
      }

      return var10000;
   }

   private Direction rotateZCounterclockwise() {
      Direction var10000;
      switch (this.ordinal()) {
         case 0:
            var10000 = EAST;
            break;
         case 1:
            var10000 = WEST;
            break;
         case 2:
         case 3:
         default:
            throw new IllegalStateException("Unable to get Z-rotated facing of " + String.valueOf(this));
         case 4:
            var10000 = DOWN;
            break;
         case 5:
            var10000 = UP;
      }

      return var10000;
   }

   public Direction rotateYCounterclockwise() {
      Direction var10000;
      switch (this.ordinal()) {
         case 2:
            var10000 = WEST;
            break;
         case 3:
            var10000 = EAST;
            break;
         case 4:
            var10000 = SOUTH;
            break;
         case 5:
            var10000 = NORTH;
            break;
         default:
            throw new IllegalStateException("Unable to get CCW facing of " + String.valueOf(this));
      }

      return var10000;
   }

   public int getOffsetX() {
      return this.vec3i.getX();
   }

   public int getOffsetY() {
      return this.vec3i.getY();
   }

   public int getOffsetZ() {
      return this.vec3i.getZ();
   }

   public Vector3f getUnitVector() {
      return new Vector3f(this.floatVector);
   }

   public String getId() {
      return this.id;
   }

   public Axis getAxis() {
      return this.axis;
   }

   @Nullable
   public static Direction byId(@Nullable String id) {
      return (Direction)CODEC.byId(id);
   }

   public static Direction byIndex(int index) {
      return VALUES[MathHelper.abs(index % VALUES.length)];
   }

   public static Direction fromHorizontalQuarterTurns(int quarterTurns) {
      return HORIZONTAL[MathHelper.abs(quarterTurns % HORIZONTAL.length)];
   }

   public static Direction fromHorizontalDegrees(double angle) {
      return fromHorizontalQuarterTurns(MathHelper.floor(angle / 90.0 + 0.5) & 3);
   }

   public static Direction from(Axis axis, AxisDirection direction) {
      Direction var10000;
      switch (axis.ordinal()) {
         case 0:
            var10000 = direction == Direction.AxisDirection.POSITIVE ? EAST : WEST;
            break;
         case 1:
            var10000 = direction == Direction.AxisDirection.POSITIVE ? UP : DOWN;
            break;
         case 2:
            var10000 = direction == Direction.AxisDirection.POSITIVE ? SOUTH : NORTH;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public float getPositiveHorizontalDegrees() {
      return (float)((this.horizontalQuarterTurns & 3) * 90);
   }

   public static Direction random(Random random) {
      return (Direction)Util.getRandom((Object[])ALL, random);
   }

   public static Direction getFacing(double x, double y, double z) {
      return getFacing((float)x, (float)y, (float)z);
   }

   public static Direction getFacing(float x, float y, float z) {
      Direction direction = NORTH;
      float f = Float.MIN_VALUE;
      Direction[] var5 = ALL;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Direction direction2 = var5[var7];
         float g = x * (float)direction2.vec3i.getX() + y * (float)direction2.vec3i.getY() + z * (float)direction2.vec3i.getZ();
         if (g > f) {
            f = g;
            direction = direction2;
         }
      }

      return direction;
   }

   public static Direction getFacing(Vec3d vec) {
      return getFacing(vec.x, vec.y, vec.z);
   }

   @Nullable
   @Contract("_,_,_,!null->!null;_,_,_,_->_")
   public static Direction fromVector(int x, int y, int z, @Nullable Direction fallback) {
      int i = Math.abs(x);
      int j = Math.abs(y);
      int k = Math.abs(z);
      if (i > k && i > j) {
         return x < 0 ? WEST : EAST;
      } else if (k > i && k > j) {
         return z < 0 ? NORTH : SOUTH;
      } else if (j > i && j > k) {
         return y < 0 ? DOWN : UP;
      } else {
         return fallback;
      }
   }

   @Nullable
   @Contract("_,!null->!null;_,_->_")
   public static Direction fromVector(Vec3i vec, @Nullable Direction fallback) {
      return fromVector(vec.getX(), vec.getY(), vec.getZ(), fallback);
   }

   public String toString() {
      return this.id;
   }

   public String asString() {
      return this.id;
   }

   private static DataResult validateVertical(Direction direction) {
      return direction.getAxis().isVertical() ? DataResult.success(direction) : DataResult.error(() -> {
         return "Expected a vertical direction";
      });
   }

   public static Direction get(AxisDirection direction, Axis axis) {
      Direction[] var2 = ALL;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Direction direction2 = var2[var4];
         if (direction2.getDirection() == direction && direction2.getAxis() == axis) {
            return direction2;
         }
      }

      String var10002 = String.valueOf(direction);
      throw new IllegalArgumentException("No such direction: " + var10002 + " " + String.valueOf(axis));
   }

   public Vec3i getVector() {
      return this.vec3i;
   }

   public Vec3d getDoubleVector() {
      return this.doubleVector;
   }

   public Vector3fc getFloatVector() {
      return this.floatVector;
   }

   public boolean pointsTo(float yaw) {
      float f = yaw * 0.017453292F;
      float g = -MathHelper.sin(f);
      float h = MathHelper.cos(f);
      return (float)this.vec3i.getX() * g + (float)this.vec3i.getZ() * h > 0.0F;
   }

   // $FF: synthetic method
   private static Direction[] method_36931() {
      return new Direction[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
   }

   public static enum Axis implements StringIdentifiable, Predicate {
      X("x") {
         public int choose(int x, int y, int z) {
            return x;
         }

         public boolean choose(boolean x, boolean y, boolean z) {
            return x;
         }

         public double choose(double x, double y, double z) {
            return x;
         }

         public Direction getPositiveDirection() {
            return Direction.EAST;
         }

         public Direction getNegativeDirection() {
            return Direction.WEST;
         }

         // $FF: synthetic method
         public boolean test(@Nullable final Object object) {
            return super.test((Direction)object);
         }
      },
      Y("y") {
         public int choose(int x, int y, int z) {
            return y;
         }

         public double choose(double x, double y, double z) {
            return y;
         }

         public boolean choose(boolean x, boolean y, boolean z) {
            return y;
         }

         public Direction getPositiveDirection() {
            return Direction.UP;
         }

         public Direction getNegativeDirection() {
            return Direction.DOWN;
         }

         // $FF: synthetic method
         public boolean test(@Nullable final Object object) {
            return super.test((Direction)object);
         }
      },
      Z("z") {
         public int choose(int x, int y, int z) {
            return z;
         }

         public double choose(double x, double y, double z) {
            return z;
         }

         public boolean choose(boolean x, boolean y, boolean z) {
            return z;
         }

         public Direction getPositiveDirection() {
            return Direction.SOUTH;
         }

         public Direction getNegativeDirection() {
            return Direction.NORTH;
         }

         // $FF: synthetic method
         public boolean test(@Nullable final Object object) {
            return super.test((Direction)object);
         }
      };

      public static final Axis[] VALUES = values();
      public static final StringIdentifiable.EnumCodec CODEC = StringIdentifiable.createCodec(Axis::values);
      private final String id;

      Axis(final String id) {
         this.id = id;
      }

      @Nullable
      public static Axis fromId(String id) {
         return (Axis)CODEC.byId(id);
      }

      public String getId() {
         return this.id;
      }

      public boolean isVertical() {
         return this == Y;
      }

      public boolean isHorizontal() {
         return this == X || this == Z;
      }

      public abstract Direction getPositiveDirection();

      public abstract Direction getNegativeDirection();

      public Direction[] getDirections() {
         return new Direction[]{this.getPositiveDirection(), this.getNegativeDirection()};
      }

      public String toString() {
         return this.id;
      }

      public static Axis pickRandomAxis(Random random) {
         return (Axis)Util.getRandom((Object[])VALUES, random);
      }

      public boolean test(@Nullable Direction direction) {
         return direction != null && direction.getAxis() == this;
      }

      public Type getType() {
         Type var10000;
         switch (this.ordinal()) {
            case 0:
            case 2:
               var10000 = Direction.Type.HORIZONTAL;
               break;
            case 1:
               var10000 = Direction.Type.VERTICAL;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public String asString() {
         return this.id;
      }

      public abstract int choose(int x, int y, int z);

      public abstract double choose(double x, double y, double z);

      public abstract boolean choose(boolean x, boolean y, boolean z);

      // $FF: synthetic method
      public boolean test(@Nullable final Object object) {
         return this.test((Direction)object);
      }

      // $FF: synthetic method
      private static Axis[] method_36932() {
         return new Axis[]{X, Y, Z};
      }
   }

   public static enum AxisDirection {
      POSITIVE(1, "Towards positive"),
      NEGATIVE(-1, "Towards negative");

      private final int offset;
      private final String description;

      private AxisDirection(final int offset, final String description) {
         this.offset = offset;
         this.description = description;
      }

      public int offset() {
         return this.offset;
      }

      public String getDescription() {
         return this.description;
      }

      public String toString() {
         return this.description;
      }

      public AxisDirection getOpposite() {
         return this == POSITIVE ? NEGATIVE : POSITIVE;
      }

      // $FF: synthetic method
      private static AxisDirection[] method_36933() {
         return new AxisDirection[]{POSITIVE, NEGATIVE};
      }
   }

   public static enum Type implements Iterable, Predicate {
      HORIZONTAL(new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}, new Axis[]{Direction.Axis.X, Direction.Axis.Z}),
      VERTICAL(new Direction[]{Direction.UP, Direction.DOWN}, new Axis[]{Direction.Axis.Y});

      private final Direction[] facingArray;
      private final Axis[] axisArray;

      private Type(final Direction[] facingArray, final Axis[] axisArray) {
         this.facingArray = facingArray;
         this.axisArray = axisArray;
      }

      public Direction random(Random random) {
         return (Direction)Util.getRandom((Object[])this.facingArray, random);
      }

      public Axis randomAxis(Random random) {
         return (Axis)Util.getRandom((Object[])this.axisArray, random);
      }

      public boolean test(@Nullable Direction direction) {
         return direction != null && direction.getAxis().getType() == this;
      }

      public Iterator iterator() {
         return Iterators.forArray(this.facingArray);
      }

      public Stream stream() {
         return Arrays.stream(this.facingArray);
      }

      public List getShuffled(Random random) {
         return Util.copyShuffled((Object[])this.facingArray, random);
      }

      public int getFacingCount() {
         return this.facingArray.length;
      }

      // $FF: synthetic method
      public boolean test(@Nullable final Object direction) {
         return this.test((Direction)direction);
      }

      // $FF: synthetic method
      private static Type[] method_36934() {
         return new Type[]{HORIZONTAL, VERTICAL};
      }
   }
}
