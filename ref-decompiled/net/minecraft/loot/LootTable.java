package net.minecraft.loot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.loot.v3.FabricLootTableBuilder;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;

public class LootTable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec TABLE_KEY;
   public static final ContextType GENERIC;
   public static final long DEFAULT_SEED = 0L;
   public static final Codec CODEC;
   public static final Codec ENTRY_CODEC;
   public static final LootTable EMPTY;
   private final ContextType type;
   private final Optional randomSequenceId;
   private final List pools;
   private final List functions;
   private final BiFunction combinedFunction;

   LootTable(ContextType type, Optional randomSequenceId, List pools, List functions) {
      this.type = type;
      this.randomSequenceId = randomSequenceId;
      this.pools = pools;
      this.functions = functions;
      this.combinedFunction = LootFunctionTypes.join(functions);
   }

   public static Consumer processStacks(ServerWorld world, Consumer consumer) {
      return (stack) -> {
         if (stack.isItemEnabled(world.getEnabledFeatures())) {
            if (stack.getCount() < stack.getMaxCount()) {
               consumer.accept(stack);
            } else {
               int i = stack.getCount();

               while(i > 0) {
                  ItemStack itemStack = stack.copyWithCount(Math.min(stack.getMaxCount(), i));
                  i -= itemStack.getCount();
                  consumer.accept(itemStack);
               }
            }

         }
      };
   }

   public void generateUnprocessedLoot(LootWorldContext parameters, Consumer lootConsumer) {
      this.generateUnprocessedLoot((new LootContext.Builder(parameters)).build(this.randomSequenceId), lootConsumer);
   }

   public void generateUnprocessedLoot(LootContext context, Consumer lootConsumer) {
      LootContext.Entry entry = LootContext.table(this);
      if (context.markActive(entry)) {
         Consumer consumer = LootFunction.apply(this.combinedFunction, lootConsumer, context);
         Iterator var5 = this.pools.iterator();

         while(var5.hasNext()) {
            LootPool lootPool = (LootPool)var5.next();
            lootPool.addGeneratedLoot(consumer, context);
         }

         context.markInactive(entry);
      } else {
         LOGGER.warn("Detected infinite loop in loot tables");
      }

   }

   public void generateLoot(LootWorldContext parameters, long seed, Consumer lootConsumer) {
      this.generateUnprocessedLoot((new LootContext.Builder(parameters)).random(seed).build(this.randomSequenceId), processStacks(parameters.getWorld(), lootConsumer));
   }

   public void generateLoot(LootWorldContext parameters, Consumer lootConsumer) {
      this.generateUnprocessedLoot(parameters, processStacks(parameters.getWorld(), lootConsumer));
   }

   public void generateLoot(LootContext context, Consumer lootConsumer) {
      this.generateUnprocessedLoot(context, processStacks(context.getWorld(), lootConsumer));
   }

   public ObjectArrayList generateLoot(LootWorldContext parameters, Random random) {
      return this.generateLoot((new LootContext.Builder(parameters)).random(random).build(this.randomSequenceId));
   }

   public ObjectArrayList generateLoot(LootWorldContext parameters, long seed) {
      return this.generateLoot((new LootContext.Builder(parameters)).random(seed).build(this.randomSequenceId));
   }

   public ObjectArrayList generateLoot(LootWorldContext parameters) {
      return this.generateLoot((new LootContext.Builder(parameters)).build(this.randomSequenceId));
   }

   private ObjectArrayList generateLoot(LootContext context) {
      ObjectArrayList objectArrayList = new ObjectArrayList();
      Objects.requireNonNull(objectArrayList);
      this.generateLoot(context, objectArrayList::add);
      return objectArrayList;
   }

   public ContextType getType() {
      return this.type;
   }

   public void validate(LootTableReporter reporter) {
      int i;
      for(i = 0; i < this.pools.size(); ++i) {
         ((LootPool)this.pools.get(i)).validate(reporter.makeChild(new ErrorReporter.NamedListElementContext("pools", i)));
      }

      for(i = 0; i < this.functions.size(); ++i) {
         ((LootFunction)this.functions.get(i)).validate(reporter.makeChild(new ErrorReporter.NamedListElementContext("functions", i)));
      }

   }

   public void supplyInventory(Inventory inventory, LootWorldContext parameters, long seed) {
      LootContext lootContext = (new LootContext.Builder(parameters)).random(seed).build(this.randomSequenceId);
      ObjectArrayList objectArrayList = this.generateLoot(lootContext);
      Random random = lootContext.getRandom();
      List list = this.getFreeSlots(inventory, random);
      this.spreadStacks(objectArrayList, list.size(), random);
      ObjectListIterator var9 = objectArrayList.iterator();

      while(var9.hasNext()) {
         ItemStack itemStack = (ItemStack)var9.next();
         if (list.isEmpty()) {
            LOGGER.warn("Tried to over-fill a container");
            return;
         }

         if (itemStack.isEmpty()) {
            inventory.setStack((Integer)list.remove(list.size() - 1), ItemStack.EMPTY);
         } else {
            inventory.setStack((Integer)list.remove(list.size() - 1), itemStack);
         }
      }

   }

   private void spreadStacks(ObjectArrayList stacks, int freeSlots, Random random) {
      List list = Lists.newArrayList();
      Iterator iterator = stacks.iterator();

      while(iterator.hasNext()) {
         ItemStack itemStack = (ItemStack)iterator.next();
         if (itemStack.isEmpty()) {
            iterator.remove();
         } else if (itemStack.getCount() > 1) {
            list.add(itemStack);
            iterator.remove();
         }
      }

      while(freeSlots - stacks.size() - list.size() > 0 && !list.isEmpty()) {
         ItemStack itemStack2 = (ItemStack)list.remove(MathHelper.nextInt(random, 0, list.size() - 1));
         int i = MathHelper.nextInt(random, 1, itemStack2.getCount() / 2);
         ItemStack itemStack3 = itemStack2.split(i);
         if (itemStack2.getCount() > 1 && random.nextBoolean()) {
            list.add(itemStack2);
         } else {
            stacks.add(itemStack2);
         }

         if (itemStack3.getCount() > 1 && random.nextBoolean()) {
            list.add(itemStack3);
         } else {
            stacks.add(itemStack3);
         }
      }

      stacks.addAll(list);
      Util.shuffle((List)stacks, random);
   }

   private List getFreeSlots(Inventory inventory, Random random) {
      ObjectArrayList objectArrayList = new ObjectArrayList();

      for(int i = 0; i < inventory.size(); ++i) {
         if (inventory.getStack(i).isEmpty()) {
            objectArrayList.add(i);
         }
      }

      Util.shuffle((List)objectArrayList, random);
      return objectArrayList;
   }

   public static Builder builder() {
      return new Builder();
   }

   static {
      TABLE_KEY = RegistryKey.createCodec(RegistryKeys.LOOT_TABLE);
      GENERIC = LootContextTypes.GENERIC;
      CODEC = Codec.lazyInitialized(() -> {
         return RecordCodecBuilder.create((instance) -> {
            return instance.group(LootContextTypes.CODEC.lenientOptionalFieldOf("type", GENERIC).forGetter((table) -> {
               return table.type;
            }), Identifier.CODEC.optionalFieldOf("random_sequence").forGetter((table) -> {
               return table.randomSequenceId;
            }), LootPool.CODEC.listOf().optionalFieldOf("pools", List.of()).forGetter((table) -> {
               return table.pools;
            }), LootFunctionTypes.CODEC.listOf().optionalFieldOf("functions", List.of()).forGetter((table) -> {
               return table.functions;
            })).apply(instance, LootTable::new);
         });
      });
      ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.LOOT_TABLE, CODEC);
      EMPTY = new LootTable(LootContextTypes.EMPTY, Optional.empty(), List.of(), List.of());
   }

   public static class Builder implements LootFunctionConsumingBuilder, FabricLootTableBuilder {
      private final ImmutableList.Builder pools = ImmutableList.builder();
      private final ImmutableList.Builder functions = ImmutableList.builder();
      private ContextType type;
      private Optional randomSequenceId;

      public Builder() {
         this.type = LootTable.GENERIC;
         this.randomSequenceId = Optional.empty();
      }

      public Builder pool(LootPool.Builder poolBuilder) {
         this.pools.add(poolBuilder.build());
         return this;
      }

      public Builder type(ContextType type) {
         this.type = type;
         return this;
      }

      public Builder randomSequenceId(Identifier randomSequenceId) {
         this.randomSequenceId = Optional.of(randomSequenceId);
         return this;
      }

      public Builder apply(LootFunction.Builder builder) {
         this.functions.add(builder.build());
         return this;
      }

      public Builder getThisFunctionConsumingBuilder() {
         return this;
      }

      public LootTable build() {
         return new LootTable(this.type, this.randomSequenceId, this.pools.build(), this.functions.build());
      }

      // $FF: synthetic method
      public LootFunctionConsumingBuilder getThisFunctionConsumingBuilder() {
         return this.getThisFunctionConsumingBuilder();
      }

      // $FF: synthetic method
      public LootFunctionConsumingBuilder apply(final LootFunction.Builder function) {
         return this.apply(function);
      }
   }
}
