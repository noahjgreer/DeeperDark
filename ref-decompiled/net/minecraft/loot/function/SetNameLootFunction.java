package net.minecraft.loot.function;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.Texts;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class SetNameLootFunction extends ConditionalLootFunction {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(instance.group(TextCodecs.CODEC.optionalFieldOf("name").forGetter((function) -> {
         return function.name;
      }), LootContext.EntityTarget.CODEC.optionalFieldOf("entity").forGetter((function) -> {
         return function.entity;
      }), SetNameLootFunction.Target.CODEC.optionalFieldOf("target", SetNameLootFunction.Target.CUSTOM_NAME).forGetter((function) -> {
         return function.target;
      }))).apply(instance, SetNameLootFunction::new);
   });
   private final Optional name;
   private final Optional entity;
   private final Target target;

   private SetNameLootFunction(List conditions, Optional name, Optional entity, Target target) {
      super(conditions);
      this.name = name;
      this.entity = entity;
      this.target = target;
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.SET_NAME;
   }

   public Set getAllowedParameters() {
      return (Set)this.entity.map((entity) -> {
         return Set.of(entity.getParameter());
      }).orElse(Set.of());
   }

   public static UnaryOperator applySourceEntity(LootContext context, @Nullable LootContext.EntityTarget sourceEntity) {
      if (sourceEntity != null) {
         Entity entity = (Entity)context.get(sourceEntity.getParameter());
         if (entity != null) {
            ServerCommandSource serverCommandSource = entity.getCommandSource(context.getWorld()).withLevel(2);
            return (textComponent) -> {
               try {
                  return Texts.parse(serverCommandSource, (Text)textComponent, entity, 0);
               } catch (CommandSyntaxException var4) {
                  LOGGER.warn("Failed to resolve text component", var4);
                  return textComponent;
               }
            };
         }
      }

      return (textComponent) -> {
         return textComponent;
      };
   }

   public ItemStack process(ItemStack stack, LootContext context) {
      this.name.ifPresent((name) -> {
         stack.set(this.target.getComponentType(), (Text)applySourceEntity(context, (LootContext.EntityTarget)this.entity.orElse((Object)null)).apply(name));
      });
      return stack;
   }

   public static ConditionalLootFunction.Builder builder(Text name, Target target) {
      return builder((conditions) -> {
         return new SetNameLootFunction(conditions, Optional.of(name), Optional.empty(), target);
      });
   }

   public static ConditionalLootFunction.Builder builder(Text name, Target target, LootContext.EntityTarget entity) {
      return builder((conditions) -> {
         return new SetNameLootFunction(conditions, Optional.of(name), Optional.of(entity), target);
      });
   }

   public static enum Target implements StringIdentifiable {
      CUSTOM_NAME("custom_name"),
      ITEM_NAME("item_name");

      public static final Codec CODEC = StringIdentifiable.createCodec(Target::values);
      private final String id;

      private Target(final String id) {
         this.id = id;
      }

      public String asString() {
         return this.id;
      }

      public ComponentType getComponentType() {
         ComponentType var10000;
         switch (this.ordinal()) {
            case 0:
               var10000 = DataComponentTypes.CUSTOM_NAME;
               break;
            case 1:
               var10000 = DataComponentTypes.ITEM_NAME;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      // $FF: synthetic method
      private static Target[] method_58735() {
         return new Target[]{CUSTOM_NAME, ITEM_NAME};
      }
   }
}
