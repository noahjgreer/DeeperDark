/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockState
 *  net.minecraft.client.render.model.BlockModelPart
 *  net.minecraft.client.render.model.BlockStateModel
 *  net.minecraft.client.render.model.MultipartBlockStateModel
 *  net.minecraft.client.render.model.MultipartBlockStateModel$MultipartBakedModel
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.util.math.random.Random
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.model;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.MultipartBlockStateModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MultipartBlockStateModel
implements BlockStateModel {
    private final MultipartBakedModel bakedModels;
    private final BlockState state;
    private @Nullable List<BlockStateModel> models;

    MultipartBlockStateModel(MultipartBakedModel bakedModels, BlockState state) {
        this.bakedModels = bakedModels;
        this.state = state;
    }

    public Sprite particleSprite() {
        return this.bakedModels.particleSprite;
    }

    public void addParts(Random random, List<BlockModelPart> parts) {
        if (this.models == null) {
            this.models = this.bakedModels.build(this.state);
        }
        long l = random.nextLong();
        for (BlockStateModel blockStateModel : this.models) {
            random.setSeed(l);
            blockStateModel.addParts(random, parts);
        }
    }
}

