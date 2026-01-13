/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.ResolvableModel;

@Environment(value=EnvType.CLIENT)
public static interface BlockStateModel.UnbakedGrouped
extends ResolvableModel {
    public BlockStateModel bake(BlockState var1, Baker var2);

    public Object getEqualityGroup(BlockState var1);
}
