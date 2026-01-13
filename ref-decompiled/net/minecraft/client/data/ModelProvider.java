/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.data.BlockStateModelGenerator
 *  net.minecraft.client.data.ItemModelGenerator
 *  net.minecraft.client.data.ItemModelOutput
 *  net.minecraft.client.data.ModelProvider
 *  net.minecraft.client.data.ModelProvider$BlockStateSuppliers
 *  net.minecraft.client.data.ModelProvider$ItemAssets
 *  net.minecraft.client.data.ModelProvider$ModelSuppliers
 *  net.minecraft.data.DataOutput
 *  net.minecraft.data.DataOutput$OutputType
 *  net.minecraft.data.DataOutput$PathResolver
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.DataWriter
 */
package net.minecraft.client.data;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.ItemModelOutput;
import net.minecraft.client.data.ModelProvider;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;

@Environment(value=EnvType.CLIENT)
public class ModelProvider
implements DataProvider {
    private final DataOutput.PathResolver blockstatesPathResolver;
    private final DataOutput.PathResolver itemsPathResolver;
    private final DataOutput.PathResolver modelsPathResolver;

    public ModelProvider(DataOutput output) {
        this.blockstatesPathResolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "blockstates");
        this.itemsPathResolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "items");
        this.modelsPathResolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "models");
    }

    public CompletableFuture<?> run(DataWriter writer) {
        ItemAssets itemAssets = new ItemAssets();
        BlockStateSuppliers blockStateSuppliers = new BlockStateSuppliers();
        ModelSuppliers modelSuppliers = new ModelSuppliers();
        new BlockStateModelGenerator((Consumer)blockStateSuppliers, (ItemModelOutput)itemAssets, (BiConsumer)modelSuppliers).register();
        new ItemModelGenerator((ItemModelOutput)itemAssets, (BiConsumer)modelSuppliers).register();
        blockStateSuppliers.validate();
        itemAssets.resolveAndValidate();
        return CompletableFuture.allOf(blockStateSuppliers.writeAllToPath(writer, this.blockstatesPathResolver), modelSuppliers.writeAllToPath(writer, this.modelsPathResolver), itemAssets.writeAllToPath(writer, this.itemsPathResolver));
    }

    public String getName() {
        return "Model Definitions";
    }
}

