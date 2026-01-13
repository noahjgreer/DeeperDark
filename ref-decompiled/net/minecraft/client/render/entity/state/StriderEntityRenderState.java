/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.StriderEntityRenderState
 *  net.minecraft.item.ItemStack
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
public class StriderEntityRenderState
extends LivingEntityRenderState {
    public ItemStack saddleStack = ItemStack.EMPTY;
    public boolean cold;
    public boolean hasPassengers;
}

