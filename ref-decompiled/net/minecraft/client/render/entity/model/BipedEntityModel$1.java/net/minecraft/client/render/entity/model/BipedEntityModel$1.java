/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.SwingAnimationType;

@Environment(value=EnvType.CLIENT)
static class BipedEntityModel.1 {
    static final /* synthetic */ int[] field_63541;

    static {
        field_63541 = new int[SwingAnimationType.values().length];
        try {
            BipedEntityModel.1.field_63541[SwingAnimationType.WHACK.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BipedEntityModel.1.field_63541[SwingAnimationType.NONE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            BipedEntityModel.1.field_63541[SwingAnimationType.STAB.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
