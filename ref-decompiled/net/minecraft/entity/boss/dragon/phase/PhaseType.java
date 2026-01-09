package net.minecraft.entity.boss.dragon.phase;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

public class PhaseType {
   private static PhaseType[] types = new PhaseType[0];
   public static final PhaseType HOLDING_PATTERN = register(HoldingPatternPhase.class, "HoldingPattern");
   public static final PhaseType STRAFE_PLAYER = register(StrafePlayerPhase.class, "StrafePlayer");
   public static final PhaseType LANDING_APPROACH = register(LandingApproachPhase.class, "LandingApproach");
   public static final PhaseType LANDING = register(LandingPhase.class, "Landing");
   public static final PhaseType TAKEOFF = register(TakeoffPhase.class, "Takeoff");
   public static final PhaseType SITTING_FLAMING = register(SittingFlamingPhase.class, "SittingFlaming");
   public static final PhaseType SITTING_SCANNING = register(SittingScanningPhase.class, "SittingScanning");
   public static final PhaseType SITTING_ATTACKING = register(SittingAttackingPhase.class, "SittingAttacking");
   public static final PhaseType CHARGING_PLAYER = register(ChargingPlayerPhase.class, "ChargingPlayer");
   public static final PhaseType DYING = register(DyingPhase.class, "Dying");
   public static final PhaseType HOVER = register(HoverPhase.class, "Hover");
   private final Class phaseClass;
   private final int id;
   private final String name;

   private PhaseType(int id, Class phaseClass, String name) {
      this.id = id;
      this.phaseClass = phaseClass;
      this.name = name;
   }

   public Phase create(EnderDragonEntity dragon) {
      try {
         Constructor constructor = this.getConstructor();
         return (Phase)constructor.newInstance(dragon);
      } catch (Exception var3) {
         throw new Error(var3);
      }
   }

   protected Constructor getConstructor() throws NoSuchMethodException {
      return this.phaseClass.getConstructor(EnderDragonEntity.class);
   }

   public int getTypeId() {
      return this.id;
   }

   public String toString() {
      return this.name + " (#" + this.id + ")";
   }

   public static PhaseType getFromId(int id) {
      return id >= 0 && id < types.length ? types[id] : HOLDING_PATTERN;
   }

   public static int count() {
      return types.length;
   }

   private static PhaseType register(Class phaseClass, String name) {
      PhaseType phaseType = new PhaseType(types.length, phaseClass, name);
      types = (PhaseType[])Arrays.copyOf(types, types.length + 1);
      types[phaseType.getTypeId()] = phaseType;
      return phaseType;
   }
}
