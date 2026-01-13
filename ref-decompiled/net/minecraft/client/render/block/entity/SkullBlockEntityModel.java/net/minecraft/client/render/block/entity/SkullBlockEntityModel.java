/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayers;

@Environment(value=EnvType.CLIENT)
public abstract class SkullBlockEntityModel
extends Model<SkullModelState> {
    public SkullBlockEntityModel(ModelPart root) {
        super(root, RenderLayers::entityTranslucent);
    }

    @Environment(value=EnvType.CLIENT)
    public static class SkullModelState {
        public float poweredTicks;
        public float yaw;
        public float pitch;
    }
}
