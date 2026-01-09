package net.minecraft.client.render.model.json;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.MultipartBlockStateModel;
import net.minecraft.state.StateManager;
import net.minecraft.util.dynamic.Codecs;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public record BlockModelDefinition(Optional simpleModels, Optional multipartModel) {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(BlockModelDefinition.Variants.CODEC.optionalFieldOf("variants").forGetter(BlockModelDefinition::simpleModels), BlockModelDefinition.Multipart.CODEC.optionalFieldOf("multipart").forGetter(BlockModelDefinition::multipartModel)).apply(instance, BlockModelDefinition::new);
   }).validate((modelDefinition) -> {
      return modelDefinition.simpleModels().isEmpty() && modelDefinition.multipartModel().isEmpty() ? DataResult.error(() -> {
         return "Neither 'variants' nor 'multipart' found";
      }) : DataResult.success(modelDefinition);
   });

   public BlockModelDefinition(Optional optional, Optional optional2) {
      this.simpleModels = optional;
      this.multipartModel = optional2;
   }

   public Map load(StateManager stateManager, Supplier idSupplier) {
      Map map = new IdentityHashMap();
      this.simpleModels.ifPresent((simpleModels) -> {
         simpleModels.load(stateManager, idSupplier, (state, model) -> {
            BlockStateModel.UnbakedGrouped unbakedGrouped = (BlockStateModel.UnbakedGrouped)map.put(state, model);
            if (unbakedGrouped != null) {
               throw new IllegalArgumentException("Overlapping definition on state: " + String.valueOf(state));
            }
         });
      });
      this.multipartModel.ifPresent((multipartModel) -> {
         List list = stateManager.getStates();
         BlockStateModel.UnbakedGrouped unbakedGrouped = multipartModel.toModel(stateManager);
         Iterator var5 = list.iterator();

         while(var5.hasNext()) {
            BlockState blockState = (BlockState)var5.next();
            map.putIfAbsent(blockState, unbakedGrouped);
         }

      });
      return map;
   }

   public Optional simpleModels() {
      return this.simpleModels;
   }

   public Optional multipartModel() {
      return this.multipartModel;
   }

   @Environment(EnvType.CLIENT)
   public static record Multipart(List selectors) {
      public static final Codec CODEC;

      public Multipart(List list) {
         this.selectors = list;
      }

      public MultipartBlockStateModel.MultipartUnbaked toModel(StateManager stateManager) {
         ImmutableList.Builder builder = ImmutableList.builderWithExpectedSize(this.selectors.size());
         Iterator var3 = this.selectors.iterator();

         while(var3.hasNext()) {
            MultipartModelComponent multipartModelComponent = (MultipartModelComponent)var3.next();
            builder.add(new MultipartBlockStateModel.Selector(multipartModelComponent.init(stateManager), multipartModelComponent.model()));
         }

         return new MultipartBlockStateModel.MultipartUnbaked(builder.build());
      }

      public List selectors() {
         return this.selectors;
      }

      static {
         CODEC = Codecs.nonEmptyList(MultipartModelComponent.CODEC.listOf()).xmap(Multipart::new, Multipart::selectors);
      }
   }

   @Environment(EnvType.CLIENT)
   public static record Variants(Map models) {
      public static final Codec CODEC;

      public Variants(Map map) {
         this.models = map;
      }

      public void load(StateManager stateManager, Supplier idSupplier, BiConsumer callback) {
         this.models.forEach((predicate, model) -> {
            try {
               Predicate predicate2 = BlockPropertiesPredicate.parse(stateManager, predicate);
               BlockStateModel.UnbakedGrouped unbakedGrouped = model.cached();
               UnmodifiableIterator var7 = stateManager.getStates().iterator();

               while(var7.hasNext()) {
                  BlockState blockState = (BlockState)var7.next();
                  if (predicate2.test(blockState)) {
                     callback.accept(blockState, unbakedGrouped);
                  }
               }
            } catch (Exception var9) {
               BlockModelDefinition.LOGGER.warn("Exception loading blockstate definition: '{}' for variant: '{}': {}", new Object[]{idSupplier.get(), predicate, var9.getMessage()});
            }

         });
      }

      public Map models() {
         return this.models;
      }

      static {
         CODEC = Codecs.nonEmptyMap(Codec.unboundedMap(Codec.STRING, BlockStateModel.Unbaked.CODEC)).xmap(Variants::new, Variants::models);
      }
   }
}
