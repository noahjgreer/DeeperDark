package net.minecraft.world.block;

import com.google.common.annotations.VisibleForTesting;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public class WireOrientation {
   public static final PacketCodec PACKET_CODEC = PacketCodecs.indexed(WireOrientation::fromOrdinal, WireOrientation::ordinal);
   private static final WireOrientation[] VALUES = (WireOrientation[])Util.make(() -> {
      WireOrientation[] wireOrientations = new WireOrientation[48];
      initializeValuesArray(new WireOrientation(Direction.UP, Direction.NORTH, WireOrientation.SideBias.LEFT), wireOrientations);
      return wireOrientations;
   });
   private final Direction up;
   private final Direction front;
   private final Direction right;
   private final SideBias sideBias;
   private final int ordinal;
   private final List directionsByPriority;
   private final List horizontalDirections;
   private final List verticalDirections;
   private final Map siblingsByFront = new EnumMap(Direction.class);
   private final Map siblingsByUp = new EnumMap(Direction.class);
   private final Map siblingsBySideBias = new EnumMap(SideBias.class);

   private WireOrientation(Direction up, Direction front, SideBias sideBias) {
      this.up = up;
      this.front = front;
      this.sideBias = sideBias;
      this.ordinal = ordinalFromComponents(up, front, sideBias);
      Vec3i vec3i = front.getVector().crossProduct(up.getVector());
      Direction direction = Direction.fromVector(vec3i, (Direction)null);
      Objects.requireNonNull(direction);
      if (this.sideBias == WireOrientation.SideBias.RIGHT) {
         this.right = direction;
      } else {
         this.right = direction.getOpposite();
      }

      this.directionsByPriority = List.of(this.front.getOpposite(), this.front, this.right, this.right.getOpposite(), this.up.getOpposite(), this.up);
      this.horizontalDirections = this.directionsByPriority.stream().filter((directionx) -> {
         return directionx.getAxis() != this.up.getAxis();
      }).toList();
      this.verticalDirections = this.directionsByPriority.stream().filter((directionx) -> {
         return directionx.getAxis() == this.up.getAxis();
      }).toList();
   }

   public static WireOrientation of(Direction up, Direction front, SideBias sideBias) {
      return VALUES[ordinalFromComponents(up, front, sideBias)];
   }

   public WireOrientation withUp(Direction direction) {
      return (WireOrientation)this.siblingsByUp.get(direction);
   }

   public WireOrientation withFront(Direction direction) {
      return (WireOrientation)this.siblingsByFront.get(direction);
   }

   public WireOrientation withFrontIfNotUp(Direction direction) {
      return direction.getAxis() == this.up.getAxis() ? this : (WireOrientation)this.siblingsByFront.get(direction);
   }

   public WireOrientation withFrontAndSideBias(Direction direction) {
      WireOrientation wireOrientation = this.withFront(direction);
      return this.front == wireOrientation.right ? wireOrientation.withOppositeSideBias() : wireOrientation;
   }

   public WireOrientation withSideBias(SideBias sideBias) {
      return (WireOrientation)this.siblingsBySideBias.get(sideBias);
   }

   public WireOrientation withOppositeSideBias() {
      return this.withSideBias(this.sideBias.opposite());
   }

   public Direction getFront() {
      return this.front;
   }

   public Direction getUp() {
      return this.up;
   }

   public Direction getRight() {
      return this.right;
   }

   public SideBias getSideBias() {
      return this.sideBias;
   }

   public List getDirectionsByPriority() {
      return this.directionsByPriority;
   }

   public List getHorizontalDirections() {
      return this.horizontalDirections;
   }

   public List getVerticalDirections() {
      return this.verticalDirections;
   }

   public String toString() {
      String var10000 = String.valueOf(this.up);
      return "[up=" + var10000 + ",front=" + String.valueOf(this.front) + ",sideBias=" + String.valueOf(this.sideBias) + "]";
   }

   public int ordinal() {
      return this.ordinal;
   }

   public static WireOrientation fromOrdinal(int ordinal) {
      return VALUES[ordinal];
   }

   public static WireOrientation random(Random random) {
      return (WireOrientation)Util.getRandom((Object[])VALUES, random);
   }

   private static WireOrientation initializeValuesArray(WireOrientation prime, WireOrientation[] valuesOut) {
      if (valuesOut[prime.ordinal()] != null) {
         return valuesOut[prime.ordinal()];
      } else {
         valuesOut[prime.ordinal()] = prime;
         SideBias[] var2 = WireOrientation.SideBias.values();
         int var3 = var2.length;

         int var4;
         for(var4 = 0; var4 < var3; ++var4) {
            SideBias sideBias = var2[var4];
            prime.siblingsBySideBias.put(sideBias, initializeValuesArray(new WireOrientation(prime.up, prime.front, sideBias), valuesOut));
         }

         Direction[] var7 = Direction.values();
         var3 = var7.length;

         Direction direction2;
         Direction direction;
         for(var4 = 0; var4 < var3; ++var4) {
            direction = var7[var4];
            direction2 = prime.up;
            if (direction == prime.up) {
               direction2 = prime.front.getOpposite();
            }

            if (direction == prime.up.getOpposite()) {
               direction2 = prime.front;
            }

            prime.siblingsByFront.put(direction, initializeValuesArray(new WireOrientation(direction2, direction, prime.sideBias), valuesOut));
         }

         var7 = Direction.values();
         var3 = var7.length;

         for(var4 = 0; var4 < var3; ++var4) {
            direction = var7[var4];
            direction2 = prime.front;
            if (direction == prime.front) {
               direction2 = prime.up.getOpposite();
            }

            if (direction == prime.front.getOpposite()) {
               direction2 = prime.up;
            }

            prime.siblingsByUp.put(direction, initializeValuesArray(new WireOrientation(direction, direction2, prime.sideBias), valuesOut));
         }

         return prime;
      }
   }

   @VisibleForTesting
   protected static int ordinalFromComponents(Direction up, Direction front, SideBias sideBias) {
      if (up.getAxis() == front.getAxis()) {
         throw new IllegalStateException("Up-vector and front-vector can not be on the same axis");
      } else {
         int i;
         if (up.getAxis() == Direction.Axis.Y) {
            i = front.getAxis() == Direction.Axis.X ? 1 : 0;
         } else {
            i = front.getAxis() == Direction.Axis.Y ? 1 : 0;
         }

         int j = i << 1 | front.getDirection().ordinal();
         return ((up.ordinal() << 2) + j << 1) + sideBias.ordinal();
      }
   }

   public static enum SideBias {
      LEFT("left"),
      RIGHT("right");

      private final String name;

      private SideBias(final String name) {
         this.name = name;
      }

      public SideBias opposite() {
         return this == LEFT ? RIGHT : LEFT;
      }

      public String toString() {
         return this.name;
      }

      // $FF: synthetic method
      private static SideBias[] method_61864() {
         return new SideBias[]{LEFT, RIGHT};
      }
   }
}
