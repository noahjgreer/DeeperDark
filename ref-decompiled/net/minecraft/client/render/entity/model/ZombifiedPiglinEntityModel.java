/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.render.entity.model.ArmPosing
 *  net.minecraft.client.render.entity.model.PiglinBaseEntityModel
 *  net.minecraft.client.render.entity.model.ZombifiedPiglinEntityModel
 *  net.minecraft.client.render.entity.state.BipedEntityRenderState
 *  net.minecraft.client.render.entity.state.LancerEntityRenderState
 *  net.minecraft.client.render.entity.state.ZombifiedPiglinEntityRenderState
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.ArmPosing;
import net.minecraft.client.render.entity.model.PiglinBaseEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.LancerEntityRenderState;
import net.minecraft.client.render.entity.state.ZombifiedPiglinEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class ZombifiedPiglinEntityModel
extends PiglinBaseEntityModel<ZombifiedPiglinEntityRenderState> {
    public ZombifiedPiglinEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public void setAngles(ZombifiedPiglinEntityRenderState zombifiedPiglinEntityRenderState) {
        super.setAngles((BipedEntityRenderState)zombifiedPiglinEntityRenderState);
        ArmPosing.zombieArms((ModelPart)this.leftArm, (ModelPart)this.rightArm, (boolean)zombifiedPiglinEntityRenderState.attacking, (LancerEntityRenderState)zombifiedPiglinEntityRenderState);
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        this.leftSleeve.visible = visible;
        this.rightSleeve.visible = visible;
        this.leftPants.visible = visible;
        this.rightPants.visible = visible;
        this.jacket.visible = visible;
    }
}

