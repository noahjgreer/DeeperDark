/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.TropicalFishEntity;

@Environment(value=EnvType.CLIENT)
static class TropicalFishColorFeatureRenderer.1 {
    static final /* synthetic */ int[] field_41658;
    static final /* synthetic */ int[] field_41659;

    static {
        field_41659 = new int[TropicalFishEntity.Pattern.values().length];
        try {
            TropicalFishColorFeatureRenderer.1.field_41659[TropicalFishEntity.Pattern.KOB.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TropicalFishColorFeatureRenderer.1.field_41659[TropicalFishEntity.Pattern.SUNSTREAK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TropicalFishColorFeatureRenderer.1.field_41659[TropicalFishEntity.Pattern.SNOOPER.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TropicalFishColorFeatureRenderer.1.field_41659[TropicalFishEntity.Pattern.DASHER.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TropicalFishColorFeatureRenderer.1.field_41659[TropicalFishEntity.Pattern.BRINELY.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TropicalFishColorFeatureRenderer.1.field_41659[TropicalFishEntity.Pattern.SPOTTY.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TropicalFishColorFeatureRenderer.1.field_41659[TropicalFishEntity.Pattern.FLOPPER.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TropicalFishColorFeatureRenderer.1.field_41659[TropicalFishEntity.Pattern.STRIPEY.ordinal()] = 8;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TropicalFishColorFeatureRenderer.1.field_41659[TropicalFishEntity.Pattern.GLITTER.ordinal()] = 9;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TropicalFishColorFeatureRenderer.1.field_41659[TropicalFishEntity.Pattern.BLOCKFISH.ordinal()] = 10;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TropicalFishColorFeatureRenderer.1.field_41659[TropicalFishEntity.Pattern.BETTY.ordinal()] = 11;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TropicalFishColorFeatureRenderer.1.field_41659[TropicalFishEntity.Pattern.CLAYFISH.ordinal()] = 12;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_41658 = new int[TropicalFishEntity.Size.values().length];
        try {
            TropicalFishColorFeatureRenderer.1.field_41658[TropicalFishEntity.Size.SMALL.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TropicalFishColorFeatureRenderer.1.field_41658[TropicalFishEntity.Size.LARGE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
