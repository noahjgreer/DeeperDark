package net.minecraft.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockRotation;

public class Batches {
   private static final int BATCH_SIZE = 50;
   public static final Decorator DEFAULT_DECORATOR = (instance, world) -> {
      return Stream.of(new GameTestState(instance, BlockRotation.NONE, world, TestAttemptConfig.once()));
   };

   public static List batch(Collection instances, Decorator decorator, ServerWorld world) {
      Map map = (Map)instances.stream().flatMap((instance) -> {
         return decorator.decorate(instance, world);
      }).collect(Collectors.groupingBy((state) -> {
         return state.getInstance().getEnvironment();
      }));
      return map.entrySet().stream().flatMap((entry) -> {
         RegistryEntry registryEntry = (RegistryEntry)entry.getKey();
         List list = (List)entry.getValue();
         return Streams.mapWithIndex(Lists.partition(list, 50).stream(), (states, index) -> {
            return create(states, registryEntry, (int)index);
         });
      }).toList();
   }

   public static TestRunContext.Batcher defaultBatcher() {
      return batcher(50);
   }

   public static TestRunContext.Batcher batcher(int batchSize) {
      return (states) -> {
         Map map = (Map)states.stream().filter(Objects::nonNull).collect(Collectors.groupingBy((state) -> {
            return state.getInstance().getEnvironment();
         }));
         return map.entrySet().stream().flatMap((entry) -> {
            RegistryEntry registryEntry = (RegistryEntry)entry.getKey();
            List list = (List)entry.getValue();
            return Streams.mapWithIndex(Lists.partition(list, batchSize).stream(), (states, index) -> {
               return create(List.copyOf(states), registryEntry, (int)index);
            });
         }).toList();
      };
   }

   public static GameTestBatch create(Collection states, RegistryEntry environment, int index) {
      return new GameTestBatch(index, states, environment);
   }

   @FunctionalInterface
   public interface Decorator {
      Stream decorate(RegistryEntry.Reference instance, ServerWorld world);
   }
}
