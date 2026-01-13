/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.recipebook.RecipeResultCollection
 *  net.minecraft.client.recipebook.ClientRecipeBook
 *  net.minecraft.client.search.IdentifierSearchProvider
 *  net.minecraft.client.search.SearchManager
 *  net.minecraft.client.search.SearchManager$Key
 *  net.minecraft.client.search.SearchProvider
 *  net.minecraft.client.search.TextSearchProvider
 *  net.minecraft.item.Item$TooltipContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.tooltip.TooltipType
 *  net.minecraft.item.tooltip.TooltipType$Default
 *  net.minecraft.recipe.display.SlotDisplayContexts
 *  net.minecraft.registry.DynamicRegistryManager
 *  net.minecraft.registry.Registry
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.registry.RegistryWrapper$WrapperLookup
 *  net.minecraft.registry.tag.TagKey
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Util
 *  net.minecraft.util.context.ContextParameterMap
 *  net.minecraft.world.World
 */
package net.minecraft.client.search;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.search.IdentifierSearchProvider;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchProvider;
import net.minecraft.client.search.TextSearchProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class SearchManager {
    private static final Key RECIPE_OUTPUT = new Key();
    private static final Key ITEM_TOOLTIP = new Key();
    private static final Key ITEM_TAG = new Key();
    private CompletableFuture<SearchProvider<ItemStack>> itemTooltipReloadFuture = CompletableFuture.completedFuture(SearchProvider.empty());
    private CompletableFuture<SearchProvider<ItemStack>> itemTagReloadFuture = CompletableFuture.completedFuture(SearchProvider.empty());
    private CompletableFuture<SearchProvider<RecipeResultCollection>> recipeOutputReloadFuture = CompletableFuture.completedFuture(SearchProvider.empty());
    private final Map<Key, Runnable> reloaders = new IdentityHashMap();

    private void addReloader(Key key, Runnable reloader) {
        reloader.run();
        this.reloaders.put(key, reloader);
    }

    public void refresh() {
        for (Runnable runnable : this.reloaders.values()) {
            runnable.run();
        }
    }

    private static Stream<String> collectItemTooltips(Stream<ItemStack> stacks, Item.TooltipContext context, TooltipType type) {
        return stacks.flatMap(stack -> stack.getTooltip(context, null, type).stream()).map(tooltip -> Formatting.strip((String)tooltip.getString()).trim()).filter(string -> !string.isEmpty());
    }

    public void addRecipeOutputReloader(ClientRecipeBook recipeBook, World world) {
        this.addReloader(RECIPE_OUTPUT, () -> {
            List list = recipeBook.getOrderedResults();
            DynamicRegistryManager dynamicRegistryManager = world.getRegistryManager();
            Registry registry = dynamicRegistryManager.getOrThrow(RegistryKeys.ITEM);
            Item.TooltipContext tooltipContext = Item.TooltipContext.create((RegistryWrapper.WrapperLookup)dynamicRegistryManager);
            ContextParameterMap contextParameterMap = SlotDisplayContexts.createParameters((World)world);
            TooltipType.Default tooltipType = TooltipType.Default.BASIC;
            CompletableFuture completableFuture = this.recipeOutputReloadFuture;
            this.recipeOutputReloadFuture = CompletableFuture.supplyAsync(() -> SearchManager.method_60361(contextParameterMap, tooltipContext, (TooltipType)tooltipType, registry, list), (Executor)Util.getMainWorkerExecutor());
            completableFuture.cancel(true);
        });
    }

    public SearchProvider<RecipeResultCollection> getRecipeOutputReloadFuture() {
        return (SearchProvider)this.recipeOutputReloadFuture.join();
    }

    public void addItemTagReloader(List<ItemStack> stacks) {
        this.addReloader(ITEM_TAG, () -> {
            CompletableFuture completableFuture = this.itemTagReloadFuture;
            this.itemTagReloadFuture = CompletableFuture.supplyAsync(() -> new IdentifierSearchProvider(stack -> stack.streamTags().map(TagKey::id), stacks), (Executor)Util.getMainWorkerExecutor());
            completableFuture.cancel(true);
        });
    }

    public SearchProvider<ItemStack> getItemTagReloadFuture() {
        return (SearchProvider)this.itemTagReloadFuture.join();
    }

    public void addItemTooltipReloader(RegistryWrapper.WrapperLookup registries, List<ItemStack> stacks) {
        this.addReloader(ITEM_TOOLTIP, () -> {
            Item.TooltipContext tooltipContext = Item.TooltipContext.create((RegistryWrapper.WrapperLookup)registries);
            TooltipType.Default tooltipType = TooltipType.Default.BASIC.withCreative();
            CompletableFuture completableFuture = this.itemTooltipReloadFuture;
            this.itemTooltipReloadFuture = CompletableFuture.supplyAsync(() -> SearchManager.method_60350(tooltipContext, (TooltipType)tooltipType, stacks), (Executor)Util.getMainWorkerExecutor());
            completableFuture.cancel(true);
        });
    }

    public SearchProvider<ItemStack> getItemTooltipReloadFuture() {
        return (SearchProvider)this.itemTooltipReloadFuture.join();
    }

    private static /* synthetic */ SearchProvider method_60350(Item.TooltipContext tooltipContext, TooltipType tooltipType, List list) {
        return new TextSearchProvider(stack -> SearchManager.collectItemTooltips(Stream.of(stack), (Item.TooltipContext)tooltipContext, (TooltipType)tooltipType), stack -> stack.getRegistryEntry().getKey().map(RegistryKey::getValue).stream(), list);
    }

    private static /* synthetic */ SearchProvider method_60361(ContextParameterMap contextParameterMap, Item.TooltipContext tooltipContext, TooltipType tooltipType, Registry registry, List list) {
        return new TextSearchProvider(resultCollection -> SearchManager.collectItemTooltips(resultCollection.getAllRecipes().stream().flatMap(display -> display.getStacks(contextParameterMap).stream()), (Item.TooltipContext)tooltipContext, (TooltipType)tooltipType), resultCollection -> resultCollection.getAllRecipes().stream().flatMap(display -> display.getStacks(contextParameterMap).stream()).map(stack -> registry.getId((Object)stack.getItem())), list);
    }
}

