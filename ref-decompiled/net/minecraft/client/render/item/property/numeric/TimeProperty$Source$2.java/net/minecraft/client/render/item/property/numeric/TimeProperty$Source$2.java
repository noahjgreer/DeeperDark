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
import net.minecraft.world.attribute.EnvironmentAttributes;

@Environment(value=EnvType.CLIENT)
final class TimeProperty.Source.2
extends TimeProperty.Source {
    TimeProperty.Source.2(String string2) {
    }

    @Override
    public float getAngle(ClientWorld world, ItemStack stack, HeldItemContext heldItemContext, Random random) {
        return world.getEnvironmentAttributes().getAttributeValue(EnvironmentAttributes.SUN_ANGLE_VISUAL, heldItemContext.getEntityPos()).floatValue() / 360.0f;
    }
}
