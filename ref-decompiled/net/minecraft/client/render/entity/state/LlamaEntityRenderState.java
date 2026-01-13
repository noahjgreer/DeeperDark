/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.LlamaEntityRenderState
 *  net.minecraft.entity.passive.LlamaEntity$Variant
 *  net.minecraft.item.ItemStack
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
public class LlamaEntityRenderState
extends LivingEntityRenderState {
    public LlamaEntity.Variant variant = LlamaEntity.Variant.DEFAULT;
    public boolean hasChest;
    public ItemStack bodyArmor = ItemStack.EMPTY;
    public boolean trader;
}

