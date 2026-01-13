/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.item.ItemAsset
 *  net.minecraft.client.item.ItemAssetsLoader
 *  net.minecraft.client.item.ItemAssetsLoader$Definition
 *  net.minecraft.client.item.ItemAssetsLoader$Result
 *  net.minecraft.client.network.ClientDynamicRegistryType
 *  net.minecraft.registry.ContextSwappableRegistryLookup
 *  net.minecraft.registry.DynamicRegistryManager$Immutable
 *  net.minecraft.registry.RegistryOps
 *  net.minecraft.registry.RegistryWrapper$WrapperLookup
 *  net.minecraft.resource.ResourceFinder
 *  net.minecraft.resource.ResourceManager
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.StrictJsonParser
 *  net.minecraft.util.Util
 *  org.slf4j.Logger
 */
package net.minecraft.client.item;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemAsset;
import net.minecraft.client.item.ItemAssetsLoader;
import net.minecraft.client.network.ClientDynamicRegistryType;
import net.minecraft.registry.ContextSwappableRegistryLookup;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.Util;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ItemAssetsLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceFinder FINDER = ResourceFinder.json((String)"items");

    public static CompletableFuture<Result> load(ResourceManager resourceManager, Executor executor) {
        DynamicRegistryManager.Immutable immutable = ClientDynamicRegistryType.createCombinedDynamicRegistries().getCombinedRegistryManager();
        return CompletableFuture.supplyAsync(() -> FINDER.findResources(resourceManager), executor).thenCompose(itemAssets -> {
            ArrayList list = new ArrayList(itemAssets.size());
            itemAssets.forEach((itemId, itemResource) -> list.add(CompletableFuture.supplyAsync(() -> {
                Definition definition;
                block8: {
                    Identifier identifier2 = FINDER.toResourceId(itemId);
                    BufferedReader reader = itemResource.getReader();
                    try {
                        ContextSwappableRegistryLookup contextSwappableRegistryLookup = new ContextSwappableRegistryLookup((RegistryWrapper.WrapperLookup)immutable);
                        RegistryOps dynamicOps = contextSwappableRegistryLookup.createRegistryOps((DynamicOps)JsonOps.INSTANCE);
                        ItemAsset itemAsset2 = ItemAsset.CODEC.parse((DynamicOps)dynamicOps, (Object)StrictJsonParser.parse((Reader)reader)).ifError(error -> LOGGER.error("Couldn't parse item model '{}' from pack '{}': {}", new Object[]{identifier2, itemResource.getPackId(), error.message()})).result().map(itemAsset -> {
                            if (contextSwappableRegistryLookup.hasEntries()) {
                                return itemAsset.withContextSwapper(contextSwappableRegistryLookup.createContextSwapper());
                            }
                            return itemAsset;
                        }).orElse(null);
                        definition = new Definition(identifier2, itemAsset2);
                        if (reader == null) break block8;
                    }
                    catch (Throwable throwable) {
                        try {
                            if (reader != null) {
                                try {
                                    ((Reader)reader).close();
                                }
                                catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        }
                        catch (Exception exception) {
                            LOGGER.error("Failed to open item model {} from pack '{}'", new Object[]{itemId, itemResource.getPackId(), exception});
                            return new Definition(identifier2, null);
                        }
                    }
                    ((Reader)reader).close();
                }
                return definition;
            }, executor)));
            return Util.combineSafe(list).thenApply(definitions -> {
                HashMap<Identifier, ItemAsset> map = new HashMap<Identifier, ItemAsset>();
                for (Definition definition : definitions) {
                    if (definition.clientItemInfo == null) continue;
                    map.put(definition.id, definition.clientItemInfo);
                }
                return new Result(map);
            });
        });
    }
}

