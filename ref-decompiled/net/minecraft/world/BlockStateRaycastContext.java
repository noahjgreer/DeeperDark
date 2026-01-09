package net.minecraft.world;

import java.util.function.Predicate;
import net.minecraft.util.math.Vec3d;

public class BlockStateRaycastContext {
   private final Vec3d start;
   private final Vec3d end;
   private final Predicate statePredicate;

   public BlockStateRaycastContext(Vec3d start, Vec3d end, Predicate statePredicate) {
      this.start = start;
      this.end = end;
      this.statePredicate = statePredicate;
   }

   public Vec3d getEnd() {
      return this.end;
   }

   public Vec3d getStart() {
      return this.start;
   }

   public Predicate getStatePredicate() {
      return this.statePredicate;
   }
}
