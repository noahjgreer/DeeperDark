/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.animation.Transformation;

@Environment(value=EnvType.CLIENT)
public static class Transformation.Targets {
    public static final Transformation.Target MOVE_ORIGIN = ModelPart::moveOrigin;
    public static final Transformation.Target ROTATE = ModelPart::rotate;
    public static final Transformation.Target SCALE = ModelPart::scale;
}
