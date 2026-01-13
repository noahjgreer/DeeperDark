/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockModelDefinitionCreator;
import net.minecraft.client.data.ModelSupplier;
import net.minecraft.client.data.TextureMap;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.util.Identifier;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
static interface BlockStateModelGenerator.StateFactory {
    public BlockModelDefinitionCreator create(Block var1, ModelVariant var2, TextureMap var3, BiConsumer<Identifier, ModelSupplier> var4);
}
