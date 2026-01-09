package net.minecraft.world.gen.root;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class RootPlacerType {
   public static final RootPlacerType MANGROVE_ROOT_PLACER;
   private final MapCodec codec;

   private static RootPlacerType register(String id, MapCodec codec) {
      return (RootPlacerType)Registry.register(Registries.ROOT_PLACER_TYPE, (String)id, new RootPlacerType(codec));
   }

   public RootPlacerType(MapCodec codec) {
      this.codec = codec;
   }

   public MapCodec getCodec() {
      return this.codec;
   }

   static {
      MANGROVE_ROOT_PLACER = register("mangrove_root_placer", MangroveRootPlacer.CODEC);
   }
}
