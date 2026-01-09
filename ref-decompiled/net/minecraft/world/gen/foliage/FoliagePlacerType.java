package net.minecraft.world.gen.foliage;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class FoliagePlacerType {
   public static final FoliagePlacerType BLOB_FOLIAGE_PLACER;
   public static final FoliagePlacerType SPRUCE_FOLIAGE_PLACER;
   public static final FoliagePlacerType PINE_FOLIAGE_PLACER;
   public static final FoliagePlacerType ACACIA_FOLIAGE_PLACER;
   public static final FoliagePlacerType BUSH_FOLIAGE_PLACER;
   public static final FoliagePlacerType FANCY_FOLIAGE_PLACER;
   public static final FoliagePlacerType JUNGLE_FOLIAGE_PLACER;
   public static final FoliagePlacerType MEGA_PINE_FOLIAGE_PLACER;
   public static final FoliagePlacerType DARK_OAK_FOLIAGE_PLACER;
   public static final FoliagePlacerType RANDOM_SPREAD_FOLIAGE_PLACER;
   public static final FoliagePlacerType CHERRY_FOLIAGE_PLACER;
   private final MapCodec codec;

   private static FoliagePlacerType register(String id, MapCodec codec) {
      return (FoliagePlacerType)Registry.register(Registries.FOLIAGE_PLACER_TYPE, (String)id, new FoliagePlacerType(codec));
   }

   public FoliagePlacerType(MapCodec codec) {
      this.codec = codec;
   }

   public MapCodec getCodec() {
      return this.codec;
   }

   static {
      BLOB_FOLIAGE_PLACER = register("blob_foliage_placer", BlobFoliagePlacer.CODEC);
      SPRUCE_FOLIAGE_PLACER = register("spruce_foliage_placer", SpruceFoliagePlacer.CODEC);
      PINE_FOLIAGE_PLACER = register("pine_foliage_placer", PineFoliagePlacer.CODEC);
      ACACIA_FOLIAGE_PLACER = register("acacia_foliage_placer", AcaciaFoliagePlacer.CODEC);
      BUSH_FOLIAGE_PLACER = register("bush_foliage_placer", BushFoliagePlacer.CODEC);
      FANCY_FOLIAGE_PLACER = register("fancy_foliage_placer", LargeOakFoliagePlacer.CODEC);
      JUNGLE_FOLIAGE_PLACER = register("jungle_foliage_placer", JungleFoliagePlacer.CODEC);
      MEGA_PINE_FOLIAGE_PLACER = register("mega_pine_foliage_placer", MegaPineFoliagePlacer.CODEC);
      DARK_OAK_FOLIAGE_PLACER = register("dark_oak_foliage_placer", DarkOakFoliagePlacer.CODEC);
      RANDOM_SPREAD_FOLIAGE_PLACER = register("random_spread_foliage_placer", RandomSpreadFoliagePlacer.CODEC);
      CHERRY_FOLIAGE_PLACER = register("cherry_foliage_placer", CherryFoliagePlacer.CODEC);
   }
}
