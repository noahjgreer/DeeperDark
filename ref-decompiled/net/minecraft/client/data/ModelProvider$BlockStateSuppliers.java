/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import com.google.common.collect.Maps;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockModelDefinitionCreator;
import net.minecraft.client.render.model.json.BlockModelDefinition;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
static class ModelProvider.BlockStateSuppliers
implements Consumer<BlockModelDefinitionCreator> {
    private final Map<Block, BlockModelDefinitionCreator> blockStateSuppliers = new HashMap<Block, BlockModelDefinitionCreator>();

    ModelProvider.BlockStateSuppliers() {
    }

    @Override
    public void accept(BlockModelDefinitionCreator blockModelDefinitionCreator) {
        Block block = blockModelDefinitionCreator.getBlock();
        BlockModelDefinitionCreator blockModelDefinitionCreator2 = this.blockStateSuppliers.put(block, blockModelDefinitionCreator);
        if (blockModelDefinitionCreator2 != null) {
            throw new IllegalStateException("Duplicate blockstate definition for " + String.valueOf(block));
        }
    }

    public void validate() {
        Stream<RegistryEntry.Reference> stream = Registries.BLOCK.streamEntries().filter(entry -> true);
        List<Identifier> list = stream.filter(entry -> !this.blockStateSuppliers.containsKey(entry.value())).map(entryx -> entryx.registryKey().getValue()).toList();
        if (!list.isEmpty()) {
            throw new IllegalStateException("Missing blockstate definitions for: " + String.valueOf(list));
        }
    }

    public CompletableFuture<?> writeAllToPath(DataWriter writer, DataOutput.PathResolver pathResolver) {
        Map map = Maps.transformValues(this.blockStateSuppliers, BlockModelDefinitionCreator::createBlockModelDefinition);
        Function<Block, Path> function = block -> pathResolver.resolveJson(block.getRegistryEntry().registryKey().getValue());
        return DataProvider.writeAllToPath(writer, BlockModelDefinition.CODEC, function, map);
    }

    @Override
    public /* synthetic */ void accept(Object blockStateSupplier) {
        this.accept((BlockModelDefinitionCreator)blockStateSupplier);
    }
}
