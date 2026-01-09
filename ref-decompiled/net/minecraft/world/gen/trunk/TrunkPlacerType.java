package net.minecraft.world.gen.trunk;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class TrunkPlacerType {
   public static final TrunkPlacerType STRAIGHT_TRUNK_PLACER;
   public static final TrunkPlacerType FORKING_TRUNK_PLACER;
   public static final TrunkPlacerType GIANT_TRUNK_PLACER;
   public static final TrunkPlacerType MEGA_JUNGLE_TRUNK_PLACER;
   public static final TrunkPlacerType DARK_OAK_TRUNK_PLACER;
   public static final TrunkPlacerType FANCY_TRUNK_PLACER;
   public static final TrunkPlacerType BENDING_TRUNK_PLACER;
   public static final TrunkPlacerType UPWARDS_BRANCHING_TRUNK_PLACER;
   public static final TrunkPlacerType CHERRY_TRUNK_PLACER;
   private final MapCodec codec;

   private static TrunkPlacerType register(String id, MapCodec codec) {
      return (TrunkPlacerType)Registry.register(Registries.TRUNK_PLACER_TYPE, (String)id, new TrunkPlacerType(codec));
   }

   public TrunkPlacerType(MapCodec codec) {
      this.codec = codec;
   }

   public MapCodec getCodec() {
      return this.codec;
   }

   static {
      STRAIGHT_TRUNK_PLACER = register("straight_trunk_placer", StraightTrunkPlacer.CODEC);
      FORKING_TRUNK_PLACER = register("forking_trunk_placer", ForkingTrunkPlacer.CODEC);
      GIANT_TRUNK_PLACER = register("giant_trunk_placer", GiantTrunkPlacer.CODEC);
      MEGA_JUNGLE_TRUNK_PLACER = register("mega_jungle_trunk_placer", MegaJungleTrunkPlacer.CODEC);
      DARK_OAK_TRUNK_PLACER = register("dark_oak_trunk_placer", DarkOakTrunkPlacer.CODEC);
      FANCY_TRUNK_PLACER = register("fancy_trunk_placer", LargeOakTrunkPlacer.CODEC);
      BENDING_TRUNK_PLACER = register("bending_trunk_placer", BendingTrunkPlacer.CODEC);
      UPWARDS_BRANCHING_TRUNK_PLACER = register("upwards_branching_trunk_placer", UpwardsBranchingTrunkPlacer.CODEC);
      CHERRY_TRUNK_PLACER = register("cherry_trunk_placer", CherryTrunkPlacer.CODEC);
   }
}
