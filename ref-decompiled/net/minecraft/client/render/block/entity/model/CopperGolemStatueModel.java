/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.block.entity.model.CopperGolemStatueModel
 *  net.minecraft.util.math.Direction
 */
package net.minecraft.client.render.block.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class CopperGolemStatueModel
extends Model<Direction> {
    public CopperGolemStatueModel(ModelPart root) {
        super(root, RenderLayers::entityCutoutNoCull);
    }

    public void setAngles(Direction direction) {
        this.root.originY = 0.0f;
        this.root.yaw = direction.getOpposite().getPositiveHorizontalDegrees() * ((float)Math.PI / 180);
        this.root.roll = (float)Math.PI;
    }
}

