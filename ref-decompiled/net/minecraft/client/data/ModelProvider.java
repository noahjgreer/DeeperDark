package net.minecraft.client.data;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.item.ItemAsset;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.model.json.BlockModelDefinition;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ModelProvider implements DataProvider {
   private final DataOutput.PathResolver blockstatesPathResolver;
   private final DataOutput.PathResolver itemsPathResolver;
   private final DataOutput.PathResolver modelsPathResolver;

   public ModelProvider(DataOutput output) {
      this.blockstatesPathResolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "blockstates");
      this.itemsPathResolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "items");
      this.modelsPathResolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "models");
   }

   public CompletableFuture run(DataWriter writer) {
      ItemAssets itemAssets = new ItemAssets();
      BlockStateSuppliers blockStateSuppliers = new BlockStateSuppliers();
      ModelSuppliers modelSuppliers = new ModelSuppliers();
      (new BlockStateModelGenerator(blockStateSuppliers, itemAssets, modelSuppliers)).register();
      (new ItemModelGenerator(itemAssets, modelSuppliers)).register();
      blockStateSuppliers.validate();
      itemAssets.resolveAndValidate();
      return CompletableFuture.allOf(blockStateSuppliers.writeAllToPath(writer, this.blockstatesPathResolver), modelSuppliers.writeAllToPath(writer, this.modelsPathResolver), itemAssets.writeAllToPath(writer, this.itemsPathResolver));
   }

   public String getName() {
      return "Model Definitions";
   }

   @Environment(EnvType.CLIENT)
   private static class ItemAssets implements ItemModelOutput {
      private final Map itemAssets = new HashMap();
      private final Map aliasedAssets = new HashMap();

      ItemAssets() {
      }

      public void accept(Item item, ItemModel.Unbaked model) {
         this.accept(item, new ItemAsset(model, ItemAsset.Properties.DEFAULT));
      }

      private void accept(Item item, ItemAsset asset) {
         ItemAsset itemAsset = (ItemAsset)this.itemAssets.put(item, asset);
         if (itemAsset != null) {
            throw new IllegalStateException("Duplicate item model definition for " + String.valueOf(item));
         }
      }

      public void acceptAlias(Item base, Item alias) {
         this.aliasedAssets.put(alias, base);
      }

      public void resolveAndValidate() {
         Registries.ITEM.forEach((item) -> {
            if (!this.aliasedAssets.containsKey(item)) {
               if (item instanceof BlockItem) {
                  BlockItem blockItem = (BlockItem)item;
                  if (!this.itemAssets.containsKey(blockItem)) {
                     Identifier identifier = ModelIds.getBlockModelId(blockItem.getBlock());
                     this.accept(blockItem, (ItemModel.Unbaked)ItemModels.basic(identifier));
                  }
               }

            }
         });
         this.aliasedAssets.forEach((base, alias) -> {
            ItemAsset itemAsset = (ItemAsset)this.itemAssets.get(alias);
            if (itemAsset == null) {
               String var10002 = String.valueOf(alias);
               throw new IllegalStateException("Missing donor: " + var10002 + " -> " + String.valueOf(base));
            } else {
               this.accept(base, itemAsset);
            }
         });
         List list = Registries.ITEM.streamEntries().filter((entry) -> {
            return !this.itemAssets.containsKey(entry.value());
         }).map((entryx) -> {
            return entryx.registryKey().getValue();
         }).toList();
         if (!list.isEmpty()) {
            throw new IllegalStateException("Missing item model definitions for: " + String.valueOf(list));
         }
      }

      public CompletableFuture writeAllToPath(DataWriter writer, DataOutput.PathResolver pathResolver) {
         return DataProvider.writeAllToPath(writer, ItemAsset.CODEC, (item) -> {
            return pathResolver.resolveJson(item.getRegistryEntry().registryKey().getValue());
         }, this.itemAssets);
      }
   }

   @Environment(EnvType.CLIENT)
   private static class BlockStateSuppliers implements Consumer {
      private final Map blockStateSuppliers = new HashMap();

      BlockStateSuppliers() {
      }

      public void accept(BlockModelDefinitionCreator blockModelDefinitionCreator) {
         Block block = blockModelDefinitionCreator.getBlock();
         BlockModelDefinitionCreator blockModelDefinitionCreator2 = (BlockModelDefinitionCreator)this.blockStateSuppliers.put(block, blockModelDefinitionCreator);
         if (blockModelDefinitionCreator2 != null) {
            throw new IllegalStateException("Duplicate blockstate definition for " + String.valueOf(block));
         }
      }

      public void validate() {
         Stream stream = Registries.BLOCK.streamEntries().filter((entry) -> {
            return true;
         });
         List list = stream.filter((entry) -> {
            return !this.blockStateSuppliers.containsKey(entry.value());
         }).map((entryx) -> {
            return entryx.registryKey().getValue();
         }).toList();
         if (!list.isEmpty()) {
            throw new IllegalStateException("Missing blockstate definitions for: " + String.valueOf(list));
         }
      }

      public CompletableFuture writeAllToPath(DataWriter writer, DataOutput.PathResolver pathResolver) {
         Map map = Maps.transformValues(this.blockStateSuppliers, BlockModelDefinitionCreator::createBlockModelDefinition);
         Function function = (block) -> {
            return pathResolver.resolveJson(block.getRegistryEntry().registryKey().getValue());
         };
         return DataProvider.writeAllToPath(writer, BlockModelDefinition.CODEC, function, map);
      }

      // $FF: synthetic method
      public void accept(final Object blockStateSupplier) {
         this.accept((BlockModelDefinitionCreator)blockStateSupplier);
      }
   }

   @Environment(EnvType.CLIENT)
   private static class ModelSuppliers implements BiConsumer {
      private final Map modelSuppliers = new HashMap();

      ModelSuppliers() {
      }

      public void accept(Identifier identifier, ModelSupplier modelSupplier) {
         Supplier supplier = (Supplier)this.modelSuppliers.put(identifier, modelSupplier);
         if (supplier != null) {
            throw new IllegalStateException("Duplicate model definition for " + String.valueOf(identifier));
         }
      }

      public CompletableFuture writeAllToPath(DataWriter writer, DataOutput.PathResolver pathResolver) {
         Function var10001 = Supplier::get;
         Objects.requireNonNull(pathResolver);
         return DataProvider.writeAllToPath(writer, var10001, pathResolver::resolveJson, this.modelSuppliers);
      }

      // $FF: synthetic method
      public void accept(final Object id, final Object modelSupplier) {
         this.accept((Identifier)id, (ModelSupplier)modelSupplier);
      }
   }
}
