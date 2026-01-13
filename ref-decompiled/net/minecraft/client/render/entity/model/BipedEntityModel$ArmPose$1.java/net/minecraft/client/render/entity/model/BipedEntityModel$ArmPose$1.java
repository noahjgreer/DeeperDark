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
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.Lancing;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;

@Environment(value=EnvType.CLIENT)
final class BipedEntityModel.ArmPose.1
extends BipedEntityModel.ArmPose {
    BipedEntityModel.ArmPose.1(boolean bl, boolean bl2) {
    }

    @Override
    public <S extends ArmedEntityRenderState> void method_75382(S armedEntityRenderState, MatrixStack matrixStack, float f, Arm arm, ItemStack itemStack) {
        Lancing.method_75392(armedEntityRenderState, matrixStack, f, arm, itemStack);
    }
}
