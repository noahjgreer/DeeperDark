/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.model.special;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.CopperGolemStatueBlock;

@Environment(value=EnvType.CLIENT)
static class CopperGolemStatueModelRenderer.1 {
    static final /* synthetic */ int[] field_64690;

    static {
        field_64690 = new int[CopperGolemStatueBlock.Pose.values().length];
        try {
            CopperGolemStatueModelRenderer.1.field_64690[CopperGolemStatueBlock.Pose.STANDING.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CopperGolemStatueModelRenderer.1.field_64690[CopperGolemStatueBlock.Pose.SITTING.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CopperGolemStatueModelRenderer.1.field_64690[CopperGolemStatueBlock.Pose.STAR.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CopperGolemStatueModelRenderer.1.field_64690[CopperGolemStatueBlock.Pose.RUNNING.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
