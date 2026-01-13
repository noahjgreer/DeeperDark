/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.ItemModelOutput;
import net.minecraft.client.data.ItemModels;
import net.minecraft.client.data.ModelIds;
import net.minecraft.client.item.ItemAsset;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
static class ModelProvider.ItemAssets
implements ItemModelOutput {
    private final Map<Item, ItemAsset> itemAssets = new HashMap<Item, ItemAsset>();
    private final Map<Item, Item> aliasedAssets = new HashMap<Item, Item>();

    ModelProvider.ItemAssets() {
    }

    @Override
    public void accept(Item item, ItemModel.Unbaked model, ItemAsset.Properties properties) {
        this.accept(item, new ItemAsset(model, properties));
    }

    private void accept(Item item, ItemAsset asset) {
        ItemAsset itemAsset = this.itemAssets.put(item, asset);
        if (itemAsset != null) {
            throw new IllegalStateException("Duplicate item model definition for " + String.valueOf(item));
        }
    }

    @Override
    public void acceptAlias(Item base, Item alias) {
        this.aliasedAssets.put(alias, base);
    }

    public void resolveAndValidate() {
        Registries.ITEM.forEach(item -> {
            BlockItem blockItem;
            if (this.aliasedAssets.containsKey(item)) {
                return;
            }
            if (item instanceof BlockItem && !this.itemAssets.containsKey(blockItem = (BlockItem)item)) {
                Identifier identifier = ModelIds.getBlockModelId(blockItem.getBlock());
                this.accept((Item)blockItem, ItemModels.basic(identifier));
            }
        });
        this.aliasedAssets.forEach((base, alias) -> {
            ItemAsset itemAsset = this.itemAssets.get(alias);
            if (itemAsset == null) {
                throw new IllegalStateException("Missing donor: " + String.valueOf(alias) + " -> " + String.valueOf(base));
            }
            this.accept((Item)base, itemAsset);
        });
        List<Identifier> list = Registries.ITEM.streamEntries().filter(entry -> !this.itemAssets.containsKey(entry.value())).map(entryx -> entryx.registryKey().getValue()).toList();
        if (!list.isEmpty()) {
            throw new IllegalStateException("Missing item model definitions for: " + String.valueOf(list));
        }
    }

    public CompletableFuture<?> writeAllToPath(DataWriter writer, DataOutput.PathResolver pathResolver) {
        return DataProvider.writeAllToPath(writer, ItemAsset.CODEC, item -> pathResolver.resolveJson(item.getRegistryEntry().registryKey().getValue()), this.itemAssets);
    }
}
