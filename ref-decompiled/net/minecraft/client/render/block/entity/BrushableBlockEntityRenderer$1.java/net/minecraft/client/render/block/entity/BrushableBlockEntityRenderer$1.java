/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
static class BrushableBlockEntityRenderer.1 {
    static final /* synthetic */ int[] field_42929;

    static {
        field_42929 = new int[Direction.values().length];
        try {
            BrushableBlockEntityRenderer.1.field_42929[Direction.EAST.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BrushableBlockEntityRenderer.1.field_42929[Direction.WEST.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BrushableBlockEntityRenderer.1.field_42929[Direction.UP.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BrushableBlockEntityRenderer.1.field_42929[Direction.DOWN.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BrushableBlockEntityRenderer.1.field_42929[Direction.NORTH.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BrushableBlockEntityRenderer.1.field_42929[Direction.SOUTH.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
