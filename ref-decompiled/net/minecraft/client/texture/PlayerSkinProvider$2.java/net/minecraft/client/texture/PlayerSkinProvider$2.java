/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static class PlayerSkinProvider.2 {
    static final /* synthetic */ int[] field_39908;

    static {
        field_39908 = new int[MinecraftProfileTexture.Type.values().length];
        try {
            PlayerSkinProvider.2.field_39908[MinecraftProfileTexture.Type.SKIN.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PlayerSkinProvider.2.field_39908[MinecraftProfileTexture.Type.CAPE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            PlayerSkinProvider.2.field_39908[MinecraftProfileTexture.Type.ELYTRA.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
