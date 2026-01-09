package net.minecraft.loot.provider.number;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public record StorageLootNumberProvider(Identifier storage, NbtPathArgumentType.NbtPath path) implements LootNumberProvider {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Identifier.CODEC.fieldOf("storage").forGetter(StorageLootNumberProvider::storage), NbtPathArgumentType.NbtPath.CODEC.fieldOf("path").forGetter(StorageLootNumberProvider::path)).apply(instance, StorageLootNumberProvider::new);
   });

   public StorageLootNumberProvider(Identifier identifier, NbtPathArgumentType.NbtPath nbtPath) {
      this.storage = identifier;
      this.path = nbtPath;
   }

   public LootNumberProviderType getType() {
      return LootNumberProviderTypes.STORAGE;
   }

   private Number getNumber(LootContext context, Number fallback) {
      NbtCompound nbtCompound = context.getWorld().getServer().getDataCommandStorage().get(this.storage);

      try {
         List list = this.path.get(nbtCompound);
         if (list.size() == 1) {
            Object var6 = list.getFirst();
            if (var6 instanceof AbstractNbtNumber) {
               AbstractNbtNumber abstractNbtNumber = (AbstractNbtNumber)var6;
               return abstractNbtNumber.numberValue();
            }
         }
      } catch (CommandSyntaxException var7) {
      }

      return fallback;
   }

   public float nextFloat(LootContext context) {
      return this.getNumber(context, 0.0F).floatValue();
   }

   public int nextInt(LootContext context) {
      return this.getNumber(context, 0).intValue();
   }

   public Identifier storage() {
      return this.storage;
   }

   public NbtPathArgumentType.NbtPath path() {
      return this.path;
   }
}
