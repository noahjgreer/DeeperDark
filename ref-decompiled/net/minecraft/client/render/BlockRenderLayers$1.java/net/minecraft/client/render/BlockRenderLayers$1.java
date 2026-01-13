/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BlockRenderLayer;

@Environment(value=EnvType.CLIENT)
static class BlockRenderLayers.1 {
    static final /* synthetic */ int[] field_60922;

    static {
        field_60922 = new int[BlockRenderLayer.values().length];
        try {
            BlockRenderLayers.1.field_60922[BlockRenderLayer.SOLID.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockRenderLayers.1.field_60922[BlockRenderLayer.CUTOUT.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockRenderLayers.1.field_60922[BlockRenderLayer.TRANSLUCENT.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BlockRenderLayers.1.field_60922[BlockRenderLayer.TRIPWIRE.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
