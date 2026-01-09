package net.minecraft.loot.entry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LootPoolEntryTypes {
   public static final Codec CODEC;
   public static final LootPoolEntryType EMPTY;
   public static final LootPoolEntryType ITEM;
   public static final LootPoolEntryType LOOT_TABLE;
   public static final LootPoolEntryType DYNAMIC;
   public static final LootPoolEntryType TAG;
   public static final LootPoolEntryType ALTERNATIVES;
   public static final LootPoolEntryType SEQUENCE;
   public static final LootPoolEntryType GROUP;

   private static LootPoolEntryType register(String id, MapCodec codec) {
      return (LootPoolEntryType)Registry.register(Registries.LOOT_POOL_ENTRY_TYPE, (Identifier)Identifier.ofVanilla(id), new LootPoolEntryType(codec));
   }

   static {
      CODEC = Registries.LOOT_POOL_ENTRY_TYPE.getCodec().dispatch(LootPoolEntry::getType, LootPoolEntryType::codec);
      EMPTY = register("empty", EmptyEntry.CODEC);
      ITEM = register("item", ItemEntry.CODEC);
      LOOT_TABLE = register("loot_table", LootTableEntry.CODEC);
      DYNAMIC = register("dynamic", DynamicEntry.CODEC);
      TAG = register("tag", TagEntry.CODEC);
      ALTERNATIVES = register("alternatives", AlternativeEntry.CODEC);
      SEQUENCE = register("sequence", SequenceEntry.CODEC);
      GROUP = register("group", GroupEntry.CODEC);
   }
}
