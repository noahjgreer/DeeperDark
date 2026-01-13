/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
static class FluidRenderer.1 {
    static final /* synthetic */ int[] field_36387;

    static {
        field_36387 = new int[Direction.values().length];
        try {
            FluidRenderer.1.field_36387[Direction.NORTH.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            FluidRenderer.1.field_36387[Direction.SOUTH.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            FluidRenderer.1.field_36387[Direction.WEST.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
