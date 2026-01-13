/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.ItemAsset;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record ModelBaker.BakedModels(ModelBaker.BlockItemModels missingModels, Map<BlockState, BlockStateModel> blockStateModels, Map<Identifier, ItemModel> itemStackModels, Map<Identifier, ItemAsset.Properties> itemProperties) {
}
