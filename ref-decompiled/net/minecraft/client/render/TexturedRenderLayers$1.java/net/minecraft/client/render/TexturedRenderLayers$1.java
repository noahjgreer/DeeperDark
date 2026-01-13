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
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.block.entity.state.ChestBlockEntityRenderState;

@Environment(value=EnvType.CLIENT)
static class TexturedRenderLayers.1 {
    static final /* synthetic */ int[] field_61761;
    static final /* synthetic */ int[] field_21482;

    static {
        field_21482 = new int[ChestType.values().length];
        try {
            TexturedRenderLayers.1.field_21482[ChestType.LEFT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TexturedRenderLayers.1.field_21482[ChestType.RIGHT.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TexturedRenderLayers.1.field_21482[ChestType.SINGLE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_61761 = new int[ChestBlockEntityRenderState.Variant.values().length];
        try {
            TexturedRenderLayers.1.field_61761[ChestBlockEntityRenderState.Variant.ENDER_CHEST.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TexturedRenderLayers.1.field_61761[ChestBlockEntityRenderState.Variant.CHRISTMAS.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TexturedRenderLayers.1.field_61761[ChestBlockEntityRenderState.Variant.TRAPPED.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TexturedRenderLayers.1.field_61761[ChestBlockEntityRenderState.Variant.COPPER_UNAFFECTED.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TexturedRenderLayers.1.field_61761[ChestBlockEntityRenderState.Variant.COPPER_EXPOSED.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TexturedRenderLayers.1.field_61761[ChestBlockEntityRenderState.Variant.COPPER_WEATHERED.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TexturedRenderLayers.1.field_61761[ChestBlockEntityRenderState.Variant.COPPER_OXIDIZED.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TexturedRenderLayers.1.field_61761[ChestBlockEntityRenderState.Variant.REGULAR.ordinal()] = 8;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
