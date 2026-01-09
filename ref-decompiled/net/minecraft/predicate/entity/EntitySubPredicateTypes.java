package net.minecraft.predicate.entity;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class EntitySubPredicateTypes {
   public static final MapCodec LIGHTNING;
   public static final MapCodec FISHING_HOOK;
   public static final MapCodec PLAYER;
   public static final MapCodec SLIME;
   public static final MapCodec RAIDER;
   public static final MapCodec SHEEP;

   private static MapCodec register(String id, MapCodec codec) {
      return (MapCodec)Registry.register(Registries.ENTITY_SUB_PREDICATE_TYPE, (String)id, codec);
   }

   public static MapCodec getDefault(Registry registry) {
      return LIGHTNING;
   }

   static {
      LIGHTNING = register("lightning", LightningBoltPredicate.CODEC);
      FISHING_HOOK = register("fishing_hook", FishingHookPredicate.CODEC);
      PLAYER = register("player", PlayerPredicate.CODEC);
      SLIME = register("slime", SlimePredicate.CODEC);
      RAIDER = register("raider", RaiderPredicate.CODEC);
      SHEEP = register("sheep", SheepPredicate.CODEC);
   }
}
