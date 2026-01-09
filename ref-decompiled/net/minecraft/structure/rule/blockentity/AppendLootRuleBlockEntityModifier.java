package net.minecraft.structure.rule.blockentity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class AppendLootRuleBlockEntityModifier implements RuleBlockEntityModifier {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(LootTable.TABLE_KEY.fieldOf("loot_table").forGetter((modifier) -> {
         return modifier.lootTable;
      })).apply(instance, AppendLootRuleBlockEntityModifier::new);
   });
   private final RegistryKey lootTable;

   public AppendLootRuleBlockEntityModifier(RegistryKey lootTable) {
      this.lootTable = lootTable;
   }

   public NbtCompound modifyBlockEntityNbt(Random random, @Nullable NbtCompound nbt) {
      NbtCompound nbtCompound = nbt == null ? new NbtCompound() : nbt.copy();
      nbtCompound.put("LootTable", LootTable.TABLE_KEY, this.lootTable);
      nbtCompound.putLong("LootTableSeed", random.nextLong());
      return nbtCompound;
   }

   public RuleBlockEntityModifierType getType() {
      return RuleBlockEntityModifierType.APPEND_LOOT;
   }
}
