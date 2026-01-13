/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.ModelSupplier;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
static class ModelProvider.ModelSuppliers
implements BiConsumer<Identifier, ModelSupplier> {
    private final Map<Identifier, ModelSupplier> modelSuppliers = new HashMap<Identifier, ModelSupplier>();

    ModelProvider.ModelSuppliers() {
    }

    @Override
    public void accept(Identifier identifier, ModelSupplier modelSupplier) {
        Supplier supplier = this.modelSuppliers.put(identifier, modelSupplier);
        if (supplier != null) {
            throw new IllegalStateException("Duplicate model definition for " + String.valueOf(identifier));
        }
    }

    public CompletableFuture<?> writeAllToPath(DataWriter writer, DataOutput.PathResolver pathResolver) {
        return DataProvider.writeAllToPath(writer, Supplier::get, pathResolver::resolveJson, this.modelSuppliers);
    }

    @Override
    public /* synthetic */ void accept(Object id, Object modelSupplier) {
        this.accept((Identifier)id, (ModelSupplier)modelSupplier);
    }
}
