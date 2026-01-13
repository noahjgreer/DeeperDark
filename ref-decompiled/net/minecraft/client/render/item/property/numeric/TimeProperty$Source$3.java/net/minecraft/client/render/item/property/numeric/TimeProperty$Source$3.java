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
import net.minecraft.world.MoonPhase;
import net.minecraft.world.attribute.EnvironmentAttributes;

@Environment(value=EnvType.CLIENT)
final class TimeProperty.Source.3
extends TimeProperty.Source {
    TimeProperty.Source.3(String string2) {
    }

    @Override
    public float getAngle(ClientWorld world, ItemStack stack, HeldItemContext heldItemContext, Random random) {
        return (float)world.getEnvironmentAttributes().getAttributeValue(EnvironmentAttributes.MOON_PHASE_VISUAL, heldItemContext.getEntityPos()).getIndex() / (float)MoonPhase.COUNT;
    }
}
