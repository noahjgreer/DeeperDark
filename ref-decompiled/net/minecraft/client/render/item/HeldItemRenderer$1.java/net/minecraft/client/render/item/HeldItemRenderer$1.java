/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.SwingAnimationType;

@Environment(value=EnvType.CLIENT)
static class HeldItemRenderer.1 {
    static final /* synthetic */ int[] field_4054;
    static final /* synthetic */ int[] field_63573;

    static {
        field_63573 = new int[SwingAnimationType.values().length];
        try {
            HeldItemRenderer.1.field_63573[SwingAnimationType.NONE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            HeldItemRenderer.1.field_63573[SwingAnimationType.WHACK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            HeldItemRenderer.1.field_63573[SwingAnimationType.STAB.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_4054 = new int[UseAction.values().length];
        try {
            HeldItemRenderer.1.field_4054[UseAction.NONE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            HeldItemRenderer.1.field_4054[UseAction.EAT.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            HeldItemRenderer.1.field_4054[UseAction.DRINK.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            HeldItemRenderer.1.field_4054[UseAction.BLOCK.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            HeldItemRenderer.1.field_4054[UseAction.BOW.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            HeldItemRenderer.1.field_4054[UseAction.TRIDENT.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            HeldItemRenderer.1.field_4054[UseAction.BRUSH.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            HeldItemRenderer.1.field_4054[UseAction.BUNDLE.ordinal()] = 8;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            HeldItemRenderer.1.field_4054[UseAction.SPEAR.ordinal()] = 9;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
