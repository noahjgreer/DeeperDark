/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.model;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;

@Environment(value=EnvType.CLIENT)
public static class Model.SinglePartModel
extends Model<Unit> {
    public Model.SinglePartModel(ModelPart part, Function<Identifier, RenderLayer> layerFactory) {
        super(part, layerFactory);
    }

    @Override
    public void setAngles(Unit unit) {
    }
}
