/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.model.BlockModelPart
 *  net.minecraft.client.render.model.BlockStateModel
 *  net.minecraft.client.render.model.WeightedBlockStateModel
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.util.collection.Pool
 *  net.minecraft.util.collection.Weighted
 *  net.minecraft.util.math.random.Random
 */
package net.minecraft.client.render.model;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class WeightedBlockStateModel
implements BlockStateModel {
    private final Pool<BlockStateModel> models;
    private final Sprite particleSprite;

    public WeightedBlockStateModel(Pool<BlockStateModel> models) {
        this.models = models;
        BlockStateModel blockStateModel = (BlockStateModel)((Weighted)models.getEntries().getFirst()).value();
        this.particleSprite = blockStateModel.particleSprite();
    }

    public Sprite particleSprite() {
        return this.particleSprite;
    }

    public void addParts(Random random, List<BlockModelPart> parts) {
        ((BlockStateModel)this.models.get(random)).addParts(random, parts);
    }
}

