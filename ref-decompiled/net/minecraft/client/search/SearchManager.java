package net.minecraft.client.search;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class SearchManager {
   private static final Key RECIPE_OUTPUT = new Key();
   private static final Key ITEM_TOOLTIP = new Key();
   private static final Key ITEM_TAG = new Key();
   private CompletableFuture itemTooltipReloadFuture = CompletableFuture.completedFuture(SearchProvider.empty());
   private CompletableFuture itemTagReloadFuture = CompletableFuture.completedFuture(SearchProvider.empty());
   private CompletableFuture recipeOutputReloadFuture = CompletableFuture.completedFuture(SearchProvider.empty());
   private final Map reloaders = new IdentityHashMap();

   private void addReloader(Key key, Runnable reloader) {
      reloader.run();
      this.reloaders.put(key, reloader);
   }

   public void refresh() {
      Iterator var1 = this.reloaders.values().iterator();

      while(var1.hasNext()) {
         Runnable runnable = (Runnable)var1.next();
         runnable.run();
      }

   }

   private static Stream collectItemTooltips(Stream stacks, Item.TooltipContext context, TooltipType type) {
      return stacks.flatMap((stack) -> {
         return stack.getTooltip(context, (PlayerEntity)null, type).stream();
      }).map((tooltip) -> {
         return Formatting.strip(tooltip.getString()).trim();
      }).filter((string) -> {
         return !string.isEmpty();
      });
   }

   public void addRecipeOutputReloader(ClientRecipeBook recipeBook, World world) {
      this.addReloader(RECIPE_OUTPUT, () -> {
         List list = recipeBook.getOrderedResults();
         DynamicRegistryManager dynamicRegistryManager = world.getRegistryManager();
         Registry registry = dynamicRegistryManager.getOrThrow(RegistryKeys.ITEM);
         Item.TooltipContext tooltipContext = Item.TooltipContext.create((RegistryWrapper.WrapperLookup)dynamicRegistryManager);
         ContextParameterMap contextParameterMap = SlotDisplayContexts.createParameters(world);
         TooltipType tooltipType = TooltipType.Default.BASIC;
         CompletableFuture completableFuture = this.recipeOutputReloadFuture;
         this.recipeOutputReloadFuture = CompletableFuture.supplyAsync(() -> {
            return new TextSearchProvider((resultCollection) -> {
               return collectItemTooltips(resultCollection.getAllRecipes().stream().flatMap((display) -> {
                  return display.getStacks(contextParameterMap).stream();
               }), tooltipContext, tooltipType);
            }, (resultCollection) -> {
               return resultCollection.getAllRecipes().stream().flatMap((display) -> {
                  return display.getStacks(contextParameterMap).stream();
               }).map((stack) -> {
                  return registry.getId(stack.getItem());
               });
            }, list);
         }, Util.getMainWorkerExecutor());
         completableFuture.cancel(true);
      });
   }

   public SearchProvider getRecipeOutputReloadFuture() {
      return (SearchProvider)this.recipeOutputReloadFuture.join();
   }

   public void addItemTagReloader(List stacks) {
      this.addReloader(ITEM_TAG, () -> {
         CompletableFuture completableFuture = this.itemTagReloadFuture;
         this.itemTagReloadFuture = CompletableFuture.supplyAsync(() -> {
            return new IdentifierSearchProvider((stack) -> {
               return stack.streamTags().map(TagKey::id);
            }, stacks);
         }, Util.getMainWorkerExecutor());
         completableFuture.cancel(true);
      });
   }

   public SearchProvider getItemTagReloadFuture() {
      return (SearchProvider)this.itemTagReloadFuture.join();
   }

   public void addItemTooltipReloader(RegistryWrapper.WrapperLookup registries, List stacks) {
      this.addReloader(ITEM_TOOLTIP, () -> {
         Item.TooltipContext tooltipContext = Item.TooltipContext.create(registries);
         TooltipType tooltipType = TooltipType.Default.BASIC.withCreative();
         CompletableFuture completableFuture = this.itemTooltipReloadFuture;
         this.itemTooltipReloadFuture = CompletableFuture.supplyAsync(() -> {
            return new TextSearchProvider((stack) -> {
               return collectItemTooltips(Stream.of(stack), tooltipContext, tooltipType);
            }, (stack) -> {
               return stack.getRegistryEntry().getKey().map(RegistryKey::getValue).stream();
            }, stacks);
         }, Util.getMainWorkerExecutor());
         completableFuture.cancel(true);
      });
   }

   public SearchProvider getItemTooltipReloadFuture() {
      return (SearchProvider)this.itemTooltipReloadFuture.join();
   }

   @Environment(EnvType.CLIENT)
   private static class Key {
      Key() {
      }
   }
}
