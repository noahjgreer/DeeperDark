/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.BoatEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractBoatEntityModel
extends EntityModel<BoatEntityRenderState> {
    private final ModelPart leftPaddle;
    private final ModelPart rightPaddle;

    public AbstractBoatEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.leftPaddle = modelPart.getChild("left_paddle");
        this.rightPaddle = modelPart.getChild("right_paddle");
    }

    @Override
    public void setAngles(BoatEntityRenderState boatEntityRenderState) {
        super.setAngles(boatEntityRenderState);
        AbstractBoatEntityModel.setPaddleAngles(boatEntityRenderState.leftPaddleAngle, 0, this.leftPaddle);
        AbstractBoatEntityModel.setPaddleAngles(boatEntityRenderState.rightPaddleAngle, 1, this.rightPaddle);
    }

    private static void setPaddleAngles(float angle, int paddle, ModelPart modelPart) {
        modelPart.pitch = MathHelper.clampedLerp((MathHelper.sin(-angle) + 1.0f) / 2.0f, -1.0471976f, -0.2617994f);
        modelPart.yaw = MathHelper.clampedLerp((MathHelper.sin(-angle + 1.0f) + 1.0f) / 2.0f, -0.7853982f, 0.7853982f);
        if (paddle == 1) {
            modelPart.yaw = (float)Math.PI - modelPart.yaw;
        }
    }
}
