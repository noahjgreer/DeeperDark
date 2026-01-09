package net.minecraft.loot.function;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.nbt.ContextLootNbtProvider;
import net.minecraft.loot.provider.nbt.LootNbtProvider;
import net.minecraft.loot.provider.nbt.LootNbtProviderTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.StringIdentifiable;
import org.apache.commons.lang3.mutable.MutableObject;

public class CopyNbtLootFunction extends ConditionalLootFunction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(instance.group(LootNbtProviderTypes.CODEC.fieldOf("source").forGetter((function) -> {
         return function.source;
      }), CopyNbtLootFunction.Operation.CODEC.listOf().fieldOf("ops").forGetter((function) -> {
         return function.operations;
      }))).apply(instance, CopyNbtLootFunction::new);
   });
   private final LootNbtProvider source;
   private final List operations;

   CopyNbtLootFunction(List conditions, LootNbtProvider source, List operations) {
      super(conditions);
      this.source = source;
      this.operations = List.copyOf(operations);
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.COPY_CUSTOM_DATA;
   }

   public Set getAllowedParameters() {
      return this.source.getRequiredParameters();
   }

   public ItemStack process(ItemStack stack, LootContext context) {
      NbtElement nbtElement = this.source.getNbt(context);
      if (nbtElement == null) {
         return stack;
      } else {
         MutableObject mutableObject = new MutableObject();
         Supplier supplier = () -> {
            if (mutableObject.getValue() == null) {
               mutableObject.setValue(((NbtComponent)stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT)).copyNbt());
            }

            return (NbtElement)mutableObject.getValue();
         };
         this.operations.forEach((operation) -> {
            operation.execute(supplier, nbtElement);
         });
         NbtCompound nbtCompound = (NbtCompound)mutableObject.getValue();
         if (nbtCompound != null) {
            NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, nbtCompound);
         }

         return stack;
      }
   }

   /** @deprecated */
   @Deprecated
   public static Builder builder(LootNbtProvider source) {
      return new Builder(source);
   }

   public static Builder builder(LootContext.EntityTarget target) {
      return new Builder(ContextLootNbtProvider.fromTarget(target));
   }

   public static class Builder extends ConditionalLootFunction.Builder {
      private final LootNbtProvider source;
      private final List operations = Lists.newArrayList();

      Builder(LootNbtProvider source) {
         this.source = source;
      }

      public Builder withOperation(String source, String target, Operator operator) {
         try {
            this.operations.add(new Operation(NbtPathArgumentType.NbtPath.parse(source), NbtPathArgumentType.NbtPath.parse(target), operator));
            return this;
         } catch (CommandSyntaxException var5) {
            throw new IllegalArgumentException(var5);
         }
      }

      public Builder withOperation(String source, String target) {
         return this.withOperation(source, target, CopyNbtLootFunction.Operator.REPLACE);
      }

      protected Builder getThisBuilder() {
         return this;
      }

      public LootFunction build() {
         return new CopyNbtLootFunction(this.getConditions(), this.source, this.operations);
      }

      // $FF: synthetic method
      protected ConditionalLootFunction.Builder getThisBuilder() {
         return this.getThisBuilder();
      }
   }

   private static record Operation(NbtPathArgumentType.NbtPath parsedSourcePath, NbtPathArgumentType.NbtPath parsedTargetPath, Operator operator) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(NbtPathArgumentType.NbtPath.CODEC.fieldOf("source").forGetter(Operation::parsedSourcePath), NbtPathArgumentType.NbtPath.CODEC.fieldOf("target").forGetter(Operation::parsedTargetPath), CopyNbtLootFunction.Operator.CODEC.fieldOf("op").forGetter(Operation::operator)).apply(instance, Operation::new);
      });

      Operation(NbtPathArgumentType.NbtPath nbtPath, NbtPathArgumentType.NbtPath nbtPath2, Operator operator) {
         this.parsedSourcePath = nbtPath;
         this.parsedTargetPath = nbtPath2;
         this.operator = operator;
      }

      public void execute(Supplier itemNbtGetter, NbtElement sourceEntityNbt) {
         try {
            List list = this.parsedSourcePath.get(sourceEntityNbt);
            if (!list.isEmpty()) {
               this.operator.merge((NbtElement)itemNbtGetter.get(), this.parsedTargetPath, list);
            }
         } catch (CommandSyntaxException var4) {
         }

      }

      public NbtPathArgumentType.NbtPath parsedSourcePath() {
         return this.parsedSourcePath;
      }

      public NbtPathArgumentType.NbtPath parsedTargetPath() {
         return this.parsedTargetPath;
      }

      public Operator operator() {
         return this.operator;
      }
   }

   public static enum Operator implements StringIdentifiable {
      REPLACE("replace") {
         public void merge(NbtElement itemNbt, NbtPathArgumentType.NbtPath targetPath, List sourceNbts) throws CommandSyntaxException {
            targetPath.put(itemNbt, (NbtElement)Iterables.getLast(sourceNbts));
         }
      },
      APPEND("append") {
         public void merge(NbtElement itemNbt, NbtPathArgumentType.NbtPath targetPath, List sourceNbts) throws CommandSyntaxException {
            List list = targetPath.getOrInit(itemNbt, NbtList::new);
            list.forEach((foundNbt) -> {
               if (foundNbt instanceof NbtList) {
                  sourceNbts.forEach((sourceNbt) -> {
                     ((NbtList)foundNbt).add(sourceNbt.copy());
                  });
               }

            });
         }
      },
      MERGE("merge") {
         public void merge(NbtElement itemNbt, NbtPathArgumentType.NbtPath targetPath, List sourceNbts) throws CommandSyntaxException {
            List list = targetPath.getOrInit(itemNbt, NbtCompound::new);
            list.forEach((foundNbt) -> {
               if (foundNbt instanceof NbtCompound) {
                  sourceNbts.forEach((sourceNbt) -> {
                     if (sourceNbt instanceof NbtCompound) {
                        ((NbtCompound)foundNbt).copyFrom((NbtCompound)sourceNbt);
                     }

                  });
               }

            });
         }
      };

      public static final Codec CODEC = StringIdentifiable.createCodec(Operator::values);
      private final String name;

      public abstract void merge(NbtElement itemNbt, NbtPathArgumentType.NbtPath targetPath, List sourceNbts) throws CommandSyntaxException;

      Operator(final String name) {
         this.name = name;
      }

      public String asString() {
         return this.name;
      }

      // $FF: synthetic method
      private static Operator[] method_36795() {
         return new Operator[]{REPLACE, APPEND, MERGE};
      }
   }
}
