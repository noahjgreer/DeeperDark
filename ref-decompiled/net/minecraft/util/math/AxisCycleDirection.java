package net.minecraft.util.math;

public enum AxisCycleDirection {
   NONE {
      public int choose(int x, int y, int z, Direction.Axis axis) {
         return axis.choose(x, y, z);
      }

      public double choose(double x, double y, double z, Direction.Axis axis) {
         return axis.choose(x, y, z);
      }

      public Direction.Axis cycle(Direction.Axis axis) {
         return axis;
      }

      public AxisCycleDirection opposite() {
         return this;
      }
   },
   FORWARD {
      public int choose(int x, int y, int z, Direction.Axis axis) {
         return axis.choose(z, x, y);
      }

      public double choose(double x, double y, double z, Direction.Axis axis) {
         return axis.choose(z, x, y);
      }

      public Direction.Axis cycle(Direction.Axis axis) {
         return AXES[Math.floorMod(axis.ordinal() + 1, 3)];
      }

      public AxisCycleDirection opposite() {
         return BACKWARD;
      }
   },
   BACKWARD {
      public int choose(int x, int y, int z, Direction.Axis axis) {
         return axis.choose(y, z, x);
      }

      public double choose(double x, double y, double z, Direction.Axis axis) {
         return axis.choose(y, z, x);
      }

      public Direction.Axis cycle(Direction.Axis axis) {
         return AXES[Math.floorMod(axis.ordinal() - 1, 3)];
      }

      public AxisCycleDirection opposite() {
         return FORWARD;
      }
   };

   public static final Direction.Axis[] AXES = Direction.Axis.values();
   public static final AxisCycleDirection[] VALUES = values();

   public abstract int choose(int x, int y, int z, Direction.Axis axis);

   public abstract double choose(double x, double y, double z, Direction.Axis axis);

   public abstract Direction.Axis cycle(Direction.Axis axis);

   public abstract AxisCycleDirection opposite();

   public static AxisCycleDirection between(Direction.Axis from, Direction.Axis to) {
      return VALUES[Math.floorMod(to.ordinal() - from.ordinal(), 3)];
   }

   // $FF: synthetic method
   private static AxisCycleDirection[] method_36930() {
      return new AxisCycleDirection[]{NONE, FORWARD, BACKWARD};
   }
}
