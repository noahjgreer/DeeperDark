/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.BipedEntityRenderState
 *  net.minecraft.client.render.entity.state.LancerEntityRenderState
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.Arm
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;

@Environment(value=EnvType.CLIENT)
public class LancerEntityRenderState
extends BipedEntityRenderState {
    public ItemStack getItemStackForArm(Arm arm) {
        return this.getMainHandItemStack();
    }
}

