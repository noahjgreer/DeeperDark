/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.property.numeric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.numeric.TimeProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
final class TimeProperty.Source.1
extends TimeProperty.Source {
    TimeProperty.Source.1(String string2) {
    }

    @Override
    public float getAngle(ClientWorld world, ItemStack stack, HeldItemContext heldItemContext, Random random) {
        return random.nextFloat();
    }
}
