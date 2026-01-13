/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.render.entity.model.AbstractBoatEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.state.BoatEntityRenderState
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.BoatEntityRenderState;
import net.minecraft.util.math.MathHelper;

/*
 * Exception performing whole class analysis ignored.
 */
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

    public void setAngles(BoatEntityRenderState boatEntityRenderState) {
        super.setAngles((Object)boatEntityRenderState);
        AbstractBoatEntityModel.setPaddleAngles((float)boatEntityRenderState.leftPaddleAngle, (int)0, (ModelPart)this.leftPaddle);
        AbstractBoatEntityModel.setPaddleAngles((float)boatEntityRenderState.rightPaddleAngle, (int)1, (ModelPart)this.rightPaddle);
    }

    private static void setPaddleAngles(float angle, int paddle, ModelPart modelPart) {
        modelPart.pitch = MathHelper.clampedLerp((float)((MathHelper.sin((double)(-angle)) + 1.0f) / 2.0f), (float)-1.0471976f, (float)-0.2617994f);
        modelPart.yaw = MathHelper.clampedLerp((float)((MathHelper.sin((double)(-angle + 1.0f)) + 1.0f) / 2.0f), (float)-0.7853982f, (float)0.7853982f);
        if (paddle == 1) {
            modelPart.yaw = (float)Math.PI - modelPart.yaw;
        }
    }
}

