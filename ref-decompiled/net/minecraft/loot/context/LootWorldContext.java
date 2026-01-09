package net.minecraft.loot.context;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.context.ContextType;
import org.jetbrains.annotations.Nullable;

public class LootWorldContext {
   private final ServerWorld world;
   private final ContextParameterMap parameters;
   private final Map dynamicDrops;
   private final float luck;

   public LootWorldContext(ServerWorld world, ContextParameterMap parameters, Map dynamicDrops, float luck) {
      this.world = world;
      this.parameters = parameters;
      this.dynamicDrops = dynamicDrops;
      this.luck = luck;
   }

   public ServerWorld getWorld() {
      return this.world;
   }

   public ContextParameterMap getParameters() {
      return this.parameters;
   }

   public void addDynamicDrops(Identifier id, Consumer lootConsumer) {
      DynamicDrop dynamicDrop = (DynamicDrop)this.dynamicDrops.get(id);
      if (dynamicDrop != null) {
         dynamicDrop.add(lootConsumer);
      }

   }

   public float getLuck() {
      return this.luck;
   }

   @FunctionalInterface
   public interface DynamicDrop {
      void add(Consumer lootConsumer);
   }

   public static class Builder {
      private final ServerWorld world;
      private final ContextParameterMap.Builder parameters = new ContextParameterMap.Builder();
      private final Map dynamicDrops = Maps.newHashMap();
      private float luck;

      public Builder(ServerWorld world) {
         this.world = world;
      }

      public ServerWorld getWorld() {
         return this.world;
      }

      public Builder add(ContextParameter parameter, Object value) {
         this.parameters.add(parameter, value);
         return this;
      }

      public Builder addOptional(ContextParameter parameter, @Nullable Object value) {
         this.parameters.addNullable(parameter, value);
         return this;
      }

      public Object get(ContextParameter parameter) {
         return this.parameters.getOrThrow(parameter);
      }

      @Nullable
      public Object getOptional(ContextParameter parameter) {
         return this.parameters.getNullable(parameter);
      }

      public Builder addDynamicDrop(Identifier id, DynamicDrop dynamicDrop) {
         DynamicDrop dynamicDrop2 = (DynamicDrop)this.dynamicDrops.put(id, dynamicDrop);
         if (dynamicDrop2 != null) {
            throw new IllegalStateException("Duplicated dynamic drop '" + String.valueOf(this.dynamicDrops) + "'");
         } else {
            return this;
         }
      }

      public Builder luck(float luck) {
         this.luck = luck;
         return this;
      }

      public LootWorldContext build(ContextType contextType) {
         ContextParameterMap contextParameterMap = this.parameters.build(contextType);
         return new LootWorldContext(this.world, contextParameterMap, this.dynamicDrops, this.luck);
      }
   }
}
