/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.model.BakedSimpleModel
 *  net.minecraft.client.render.model.Baker
 *  net.minecraft.client.render.model.Baker$ResolvableCacheKey
 *  net.minecraft.client.render.model.Baker$Vec3fInterner
 *  net.minecraft.client.render.model.BlockModelPart
 *  net.minecraft.client.render.model.ErrorCollectingSpriteGetter
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedSimpleModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.ErrorCollectingSpriteGetter;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public interface Baker {
    public BakedSimpleModel getModel(Identifier var1);

    public BlockModelPart getBlockPart();

    public ErrorCollectingSpriteGetter getSpriteGetter();

    public Vec3fInterner getVec3fInterner();

    public <T> T compute(ResolvableCacheKey<T> var1);
}

