package net.minecraft.loot.context;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextType;

public class LootContextTypes {
   private static final BiMap MAP = HashBiMap.create();
   public static final Codec CODEC;
   public static final ContextType EMPTY;
   public static final ContextType CHEST;
   public static final ContextType COMMAND;
   public static final ContextType SELECTOR;
   public static final ContextType FISHING;
   public static final ContextType ENTITY;
   public static final ContextType EQUIPMENT;
   public static final ContextType ARCHAEOLOGY;
   public static final ContextType GIFT;
   public static final ContextType BARTER;
   public static final ContextType VAULT;
   public static final ContextType ADVANCEMENT_REWARD;
   public static final ContextType ADVANCEMENT_ENTITY;
   public static final ContextType ADVANCEMENT_LOCATION;
   public static final ContextType BLOCK_USE;
   public static final ContextType GENERIC;
   public static final ContextType BLOCK;
   public static final ContextType SHEARING;
   public static final ContextType ENCHANTED_DAMAGE;
   public static final ContextType ENCHANTED_ITEM;
   public static final ContextType ENCHANTED_LOCATION;
   public static final ContextType ENCHANTED_ENTITY;
   public static final ContextType HIT_BLOCK;

   private static ContextType register(String name, Consumer type) {
      ContextType.Builder builder = new ContextType.Builder();
      type.accept(builder);
      ContextType contextType = builder.build();
      Identifier identifier = Identifier.ofVanilla(name);
      ContextType contextType2 = (ContextType)MAP.put(identifier, contextType);
      if (contextType2 != null) {
         throw new IllegalStateException("Loot table parameter set " + String.valueOf(identifier) + " is already registered");
      } else {
         return contextType;
      }
   }

   static {
      Codec var10000 = Identifier.CODEC;
      Function var10001 = (id) -> {
         return (DataResult)Optional.ofNullable((ContextType)MAP.get(id)).map(DataResult::success).orElseGet(() -> {
            return DataResult.error(() -> {
               return "No parameter set exists with id: '" + String.valueOf(id) + "'";
            });
         });
      };
      BiMap var10002 = MAP.inverse();
      Objects.requireNonNull(var10002);
      CODEC = var10000.comapFlatMap(var10001, var10002::get);
      EMPTY = register("empty", (builder) -> {
      });
      CHEST = register("chest", (builder) -> {
         builder.require(LootContextParameters.ORIGIN).allow(LootContextParameters.THIS_ENTITY);
      });
      COMMAND = register("command", (builder) -> {
         builder.require(LootContextParameters.ORIGIN).allow(LootContextParameters.THIS_ENTITY);
      });
      SELECTOR = register("selector", (builder) -> {
         builder.require(LootContextParameters.ORIGIN).require(LootContextParameters.THIS_ENTITY);
      });
      FISHING = register("fishing", (builder) -> {
         builder.require(LootContextParameters.ORIGIN).require(LootContextParameters.TOOL).allow(LootContextParameters.THIS_ENTITY);
      });
      ENTITY = register("entity", (builder) -> {
         builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ORIGIN).require(LootContextParameters.DAMAGE_SOURCE).allow(LootContextParameters.ATTACKING_ENTITY).allow(LootContextParameters.DIRECT_ATTACKING_ENTITY).allow(LootContextParameters.LAST_DAMAGE_PLAYER);
      });
      EQUIPMENT = register("equipment", (builder) -> {
         builder.require(LootContextParameters.ORIGIN).require(LootContextParameters.THIS_ENTITY);
      });
      ARCHAEOLOGY = register("archaeology", (builder) -> {
         builder.require(LootContextParameters.ORIGIN).require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.TOOL);
      });
      GIFT = register("gift", (builder) -> {
         builder.require(LootContextParameters.ORIGIN).require(LootContextParameters.THIS_ENTITY);
      });
      BARTER = register("barter", (builder) -> {
         builder.require(LootContextParameters.THIS_ENTITY);
      });
      VAULT = register("vault", (builder) -> {
         builder.require(LootContextParameters.ORIGIN).allow(LootContextParameters.THIS_ENTITY).allow(LootContextParameters.TOOL);
      });
      ADVANCEMENT_REWARD = register("advancement_reward", (builder) -> {
         builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ORIGIN);
      });
      ADVANCEMENT_ENTITY = register("advancement_entity", (builder) -> {
         builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ORIGIN);
      });
      ADVANCEMENT_LOCATION = register("advancement_location", (builder) -> {
         builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ORIGIN).require(LootContextParameters.TOOL).require(LootContextParameters.BLOCK_STATE);
      });
      BLOCK_USE = register("block_use", (builder) -> {
         builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ORIGIN).require(LootContextParameters.BLOCK_STATE);
      });
      GENERIC = register("generic", (builder) -> {
         builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.LAST_DAMAGE_PLAYER).require(LootContextParameters.DAMAGE_SOURCE).require(LootContextParameters.ATTACKING_ENTITY).require(LootContextParameters.DIRECT_ATTACKING_ENTITY).require(LootContextParameters.ORIGIN).require(LootContextParameters.BLOCK_STATE).require(LootContextParameters.BLOCK_ENTITY).require(LootContextParameters.TOOL).require(LootContextParameters.EXPLOSION_RADIUS);
      });
      BLOCK = register("block", (builder) -> {
         builder.require(LootContextParameters.BLOCK_STATE).require(LootContextParameters.ORIGIN).require(LootContextParameters.TOOL).allow(LootContextParameters.THIS_ENTITY).allow(LootContextParameters.BLOCK_ENTITY).allow(LootContextParameters.EXPLOSION_RADIUS);
      });
      SHEARING = register("shearing", (builder) -> {
         builder.require(LootContextParameters.ORIGIN).require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.TOOL);
      });
      ENCHANTED_DAMAGE = register("enchanted_damage", (builder) -> {
         builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ENCHANTMENT_LEVEL).require(LootContextParameters.ORIGIN).require(LootContextParameters.DAMAGE_SOURCE).allow(LootContextParameters.DIRECT_ATTACKING_ENTITY).allow(LootContextParameters.ATTACKING_ENTITY);
      });
      ENCHANTED_ITEM = register("enchanted_item", (builder) -> {
         builder.require(LootContextParameters.TOOL).require(LootContextParameters.ENCHANTMENT_LEVEL);
      });
      ENCHANTED_LOCATION = register("enchanted_location", (builder) -> {
         builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ENCHANTMENT_LEVEL).require(LootContextParameters.ORIGIN).require(LootContextParameters.ENCHANTMENT_ACTIVE);
      });
      ENCHANTED_ENTITY = register("enchanted_entity", (builder) -> {
         builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ENCHANTMENT_LEVEL).require(LootContextParameters.ORIGIN);
      });
      HIT_BLOCK = register("hit_block", (builder) -> {
         builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ENCHANTMENT_LEVEL).require(LootContextParameters.ORIGIN).require(LootContextParameters.BLOCK_STATE);
      });
   }
}
