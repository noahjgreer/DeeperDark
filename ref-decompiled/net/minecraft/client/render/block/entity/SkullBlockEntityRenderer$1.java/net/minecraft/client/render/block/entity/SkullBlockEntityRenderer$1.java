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
import net.minecraft.block.SkullBlock;

@Environment(value=EnvType.CLIENT)
static class SkullBlockEntityRenderer.1 {
    static final /* synthetic */ int[] field_55285;

    static {
        field_55285 = new int[SkullBlock.Type.values().length];
        try {
            SkullBlockEntityRenderer.1.field_55285[SkullBlock.Type.SKELETON.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SkullBlockEntityRenderer.1.field_55285[SkullBlock.Type.WITHER_SKELETON.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SkullBlockEntityRenderer.1.field_55285[SkullBlock.Type.PLAYER.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SkullBlockEntityRenderer.1.field_55285[SkullBlock.Type.ZOMBIE.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SkullBlockEntityRenderer.1.field_55285[SkullBlock.Type.CREEPER.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SkullBlockEntityRenderer.1.field_55285[SkullBlock.Type.DRAGON.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            SkullBlockEntityRenderer.1.field_55285[SkullBlock.Type.PIGLIN.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
