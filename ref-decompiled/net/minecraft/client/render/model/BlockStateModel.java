/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.renderer.v1.model.FabricBlockStateModel
 *  net.minecraft.client.render.model.BlockModelPart
 *  net.minecraft.client.render.model.BlockStateModel
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.util.math.random.Random
 */
package net.minecraft.client.render.model;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBlockStateModel;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public interface BlockStateModel
extends FabricBlockStateModel {
    public void addParts(Random var1, List<BlockModelPart> var2);

    default public List<BlockModelPart> getParts(Random random) {
        ObjectArrayList list = new ObjectArrayList();
        this.addParts(random, (List)list);
        return list;
    }

    public Sprite particleSprite();
}

