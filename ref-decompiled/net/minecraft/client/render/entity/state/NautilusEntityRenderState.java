/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.NautilusEntityRenderState
 *  net.minecraft.entity.mob.ZombieNautilusVariant
 *  net.minecraft.item.ItemStack
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.ZombieNautilusVariant;
import net.minecraft.item.ItemStack;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class NautilusEntityRenderState
extends LivingEntityRenderState {
    public ItemStack saddleStack = ItemStack.EMPTY;
    public ItemStack armorStack = ItemStack.EMPTY;
    public @Nullable ZombieNautilusVariant variant;
}

