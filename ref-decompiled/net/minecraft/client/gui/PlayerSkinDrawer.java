/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.PlayerSkinDrawer
 *  net.minecraft.entity.player.SkinTextures
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.Identifier;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class PlayerSkinDrawer {
    public static final int FACE_WIDTH = 8;
    public static final int FACE_HEIGHT = 8;
    public static final int FACE_X = 8;
    public static final int FACE_Y = 8;
    public static final int FACE_OVERLAY_X = 40;
    public static final int FACE_OVERLAY_Y = 8;
    public static final int field_39531 = 8;
    public static final int field_39532 = 8;
    public static final int SKIN_TEXTURE_WIDTH = 64;
    public static final int SKIN_TEXTURE_HEIGHT = 64;

    public static void draw(DrawContext context, SkinTextures textures, int x, int y, int size) {
        PlayerSkinDrawer.draw((DrawContext)context, (SkinTextures)textures, (int)x, (int)y, (int)size, (int)-1);
    }

    public static void draw(DrawContext context, SkinTextures textures, int x, int y, int size, int color) {
        PlayerSkinDrawer.draw((DrawContext)context, (Identifier)textures.body().texturePath(), (int)x, (int)y, (int)size, (boolean)true, (boolean)false, (int)color);
    }

    public static void draw(DrawContext context, Identifier texture, int x, int y, int size, boolean hatVisible, boolean upsideDown, int color) {
        int i = 8 + (upsideDown ? 8 : 0);
        int j = 8 * (upsideDown ? -1 : 1);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, 8.0f, (float)i, size, size, 8, j, 64, 64, color);
        if (hatVisible) {
            PlayerSkinDrawer.drawHat((DrawContext)context, (Identifier)texture, (int)x, (int)y, (int)size, (boolean)upsideDown, (int)color);
        }
    }

    private static void drawHat(DrawContext context, Identifier texture, int x, int y, int size, boolean upsideDown, int color) {
        int i = 8 + (upsideDown ? 8 : 0);
        int j = 8 * (upsideDown ? -1 : 1);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, 40.0f, (float)i, size, size, 8, j, 64, 64, color);
    }
}

