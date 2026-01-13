/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.model.BlockModelPart
 *  net.minecraft.client.render.model.BlockStateModel
 *  net.minecraft.client.render.model.SimpleBlockStateModel
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.util.math.random.Random
 */
package net.minecraft.client.render.model;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class SimpleBlockStateModel
implements BlockStateModel {
    private final BlockModelPart part;

    public SimpleBlockStateModel(BlockModelPart part) {
        this.part = part;
    }

    public void addParts(Random random, List<BlockModelPart> parts) {
        parts.add(this.part);
    }

    public Sprite particleSprite() {
        return this.part.particleSprite();
    }
}

