/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.renderer.v1.model.FabricBlockModelPart
 *  net.minecraft.client.render.model.BakedQuad
 *  net.minecraft.client.render.model.BlockModelPart
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.util.math.Direction
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.model;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBlockModelPart;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface BlockModelPart
extends FabricBlockModelPart {
    public List<BakedQuad> getQuads(@Nullable Direction var1);

    public boolean useAmbientOcclusion();

    public Sprite particleSprite();
}

