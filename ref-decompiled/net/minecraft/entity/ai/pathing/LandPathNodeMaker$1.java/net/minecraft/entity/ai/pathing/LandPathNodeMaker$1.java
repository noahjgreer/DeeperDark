/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.ai.pathing.PathNodeType;

static class LandPathNodeMaker.1 {
    static final /* synthetic */ int[] field_47414;

    static {
        field_47414 = new int[PathNodeType.values().length];
        try {
            LandPathNodeMaker.1.field_47414[PathNodeType.OPEN.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            LandPathNodeMaker.1.field_47414[PathNodeType.WATER.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            LandPathNodeMaker.1.field_47414[PathNodeType.LAVA.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            LandPathNodeMaker.1.field_47414[PathNodeType.WALKABLE.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            LandPathNodeMaker.1.field_47414[PathNodeType.DAMAGE_FIRE.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            LandPathNodeMaker.1.field_47414[PathNodeType.DAMAGE_OTHER.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            LandPathNodeMaker.1.field_47414[PathNodeType.STICKY_HONEY.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            LandPathNodeMaker.1.field_47414[PathNodeType.POWDER_SNOW.ordinal()] = 8;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            LandPathNodeMaker.1.field_47414[PathNodeType.DAMAGE_CAUTIOUS.ordinal()] = 9;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            LandPathNodeMaker.1.field_47414[PathNodeType.TRAPDOOR.ordinal()] = 10;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
