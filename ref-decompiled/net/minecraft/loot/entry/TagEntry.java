package net.minecraft.loot.entry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;

public class TagEntry extends LeafEntry {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(TagKey.unprefixedCodec(RegistryKeys.ITEM).fieldOf("name").forGetter((entry) -> {
         return entry.name;
      }), Codec.BOOL.fieldOf("expand").forGetter((entry) -> {
         return entry.expand;
      })).and(addLeafFields(instance)).apply(instance, TagEntry::new);
   });
   private final TagKey name;
   private final boolean expand;

   private TagEntry(TagKey name, boolean expand, int weight, int quality, List conditions, List functions) {
      super(weight, quality, conditions, functions);
      this.name = name;
      this.expand = expand;
   }

   public LootPoolEntryType getType() {
      return LootPoolEntryTypes.TAG;
   }

   public void generateLoot(Consumer lootConsumer, LootContext context) {
      Registries.ITEM.iterateEntries(this.name).forEach((entry) -> {
         lootConsumer.accept(new ItemStack(entry));
      });
   }

   private boolean grow(LootContext context, Consumer lootChoiceExpander) {
      if (!this.test(context)) {
         return false;
      } else {
         Iterator var3 = Registries.ITEM.iterateEntries(this.name).iterator();

         while(var3.hasNext()) {
            final RegistryEntry registryEntry = (RegistryEntry)var3.next();
            lootChoiceExpander.accept(new LeafEntry.Choice(this) {
               public void generateLoot(Consumer lootConsumer, LootContext context) {
                  lootConsumer.accept(new ItemStack(registryEntry));
               }
            });
         }

         return true;
      }
   }

   public boolean expand(LootContext lootContext, Consumer consumer) {
      return this.expand ? this.grow(lootContext, consumer) : super.expand(lootContext, consumer);
   }

   public static LeafEntry.Builder builder(TagKey name) {
      return builder((weight, quality, conditions, functions) -> {
         return new TagEntry(name, false, weight, quality, conditions, functions);
      });
   }

   public static LeafEntry.Builder expandBuilder(TagKey name) {
      return builder((weight, quality, conditions, functions) -> {
         return new TagEntry(name, true, weight, quality, conditions, functions);
      });
   }
}
