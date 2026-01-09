package net.minecraft.loot.provider.nbt;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

public record StorageLootNbtProvider(Identifier source) implements LootNbtProvider {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Identifier.CODEC.fieldOf("source").forGetter(StorageLootNbtProvider::source)).apply(instance, StorageLootNbtProvider::new);
   });

   public StorageLootNbtProvider(Identifier source) {
      this.source = source;
   }

   public LootNbtProviderType getType() {
      return LootNbtProviderTypes.STORAGE;
   }

   public NbtElement getNbt(LootContext context) {
      return context.getWorld().getServer().getDataCommandStorage().get(this.source);
   }

   public Set getRequiredParameters() {
      return Set.of();
   }

   public Identifier source() {
      return this.source;
   }
}
