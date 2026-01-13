/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity.model;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class EntityModel<T extends EntityRenderState>
extends Model<T> {
    public static final float field_52908 = -1.501f;

    protected EntityModel(ModelPart root) {
        this(root, RenderLayers::entityCutoutNoCull);
    }

    protected EntityModel(ModelPart modelPart, Function<Identifier, RenderLayer> function) {
        super(modelPart, function);
    }
}

