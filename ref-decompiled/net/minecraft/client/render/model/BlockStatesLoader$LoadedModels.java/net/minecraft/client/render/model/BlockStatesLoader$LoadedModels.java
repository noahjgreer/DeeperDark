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
import net.minecraft.client.render.model.BlockStateModel;

@Environment(value=EnvType.CLIENT)
public record BlockStatesLoader.LoadedModels(Map<BlockState, BlockStateModel.UnbakedGrouped> models) {
}
