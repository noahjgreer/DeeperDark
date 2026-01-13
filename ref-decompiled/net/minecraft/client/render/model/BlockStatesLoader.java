/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.client.render.model.BlockStateManagers
 *  net.minecraft.client.render.model.BlockStatesLoader
 *  net.minecraft.client.render.model.BlockStatesLoader$LoadedBlockStateDefinition
 *  net.minecraft.client.render.model.BlockStatesLoader$LoadedModels
 *  net.minecraft.client.render.model.json.BlockModelDefinition
 *  net.minecraft.resource.Resource
 *  net.minecraft.resource.ResourceFinder
 *  net.minecraft.resource.ResourceManager
 *  net.minecraft.state.StateManager
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.StrictJsonParser
 *  net.minecraft.util.Util
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.render.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BlockStateManagers;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.client.render.model.json.BlockModelDefinition;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class BlockStatesLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceFinder FINDER = ResourceFinder.json((String)"blockstates");

    /*
     * Issues handling annotations - annotations may be inaccurate
     */
    public static CompletableFuture<LoadedModels> load(ResourceManager resourceManager, Executor prepareExecutor) {
        @Nullable Function function = BlockStateManagers.createIdToManagerMapper();
        return CompletableFuture.supplyAsync(() -> FINDER.findAllResources(resourceManager), prepareExecutor).thenCompose(resourceMap -> {
            ArrayList<CompletableFuture<// Could not load outer class - annotation placement on inner may be incorrect
            @Nullable BlockStatesLoader.LoadedModels>> list = new ArrayList<CompletableFuture<LoadedModels>>(resourceMap.size());
            for (Map.Entry entry : resourceMap.entrySet()) {
                list.add(CompletableFuture.supplyAsync(() -> {
                    @Nullable Identifier identifier = FINDER.toResourceId((Identifier)entry.getKey());
                    @Nullable StateManager stateManager = (StateManager)function.apply(identifier);
                    if (stateManager == null) {
                        LOGGER.debug("Discovered unknown block state definition {}, ignoring", (Object)identifier);
                        return null;
                    }
                    List list = (List)entry.getValue();
                    ArrayList<LoadedBlockStateDefinition> list2 = new ArrayList<LoadedBlockStateDefinition>(list.size());
                    for (Resource resource : list) {
                        try {
                            BufferedReader reader = resource.getReader();
                            try {
                                JsonElement jsonElement = StrictJsonParser.parse((Reader)reader);
                                BlockModelDefinition blockModelDefinition = (BlockModelDefinition)BlockModelDefinition.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)jsonElement).getOrThrow(JsonParseException::new);
                                list2.add(new LoadedBlockStateDefinition(resource.getPackId(), blockModelDefinition));
                            }
                            finally {
                                if (reader == null) continue;
                                ((Reader)reader).close();
                            }
                        }
                        catch (Exception exception) {
                            LOGGER.error("Failed to load blockstate definition {} from pack {}", new Object[]{identifier, resource.getPackId(), exception});
                        }
                    }
                    try {
                        return BlockStatesLoader.combine((Identifier)identifier, (StateManager)stateManager, list2);
                    }
                    catch (Exception exception2) {
                        LOGGER.error("Failed to load blockstate definition {}", (Object)identifier, (Object)exception2);
                        return null;
                    }
                }, prepareExecutor));
            }
            return Util.combineSafe(list).thenApply(definitions -> {
                IdentityHashMap map = new IdentityHashMap();
                for (LoadedModels loadedModels : definitions) {
                    if (loadedModels == null) continue;
                    map.putAll(loadedModels.models());
                }
                return new LoadedModels(map);
            });
        });
    }

    private static LoadedModels combine(Identifier id, StateManager<Block, BlockState> stateManager, List<LoadedBlockStateDefinition> definitions) {
        IdentityHashMap map = new IdentityHashMap();
        for (LoadedBlockStateDefinition loadedBlockStateDefinition : definitions) {
            map.putAll(loadedBlockStateDefinition.contents.load(stateManager, () -> String.valueOf(id) + "/" + loadedBlockStateDefinition.source));
        }
        return new LoadedModels(map);
    }
}

