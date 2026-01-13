/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.navigation.GuiNavigation
 *  net.minecraft.client.gui.navigation.GuiNavigationPath
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.IconWidget
 *  net.minecraft.client.gui.widget.IconWidget$Simple
 *  net.minecraft.client.gui.widget.IconWidget$Texture
 *  net.minecraft.client.sound.SoundManager
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.util.Identifier
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.IconWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class IconWidget
extends ClickableWidget {
    IconWidget(int x, int y, int width, int height) {
        super(x, y, width, height, ScreenTexts.EMPTY);
    }

    public static IconWidget create(int width, int height, Identifier texture, int textureWidth, int textureHeight) {
        return new Texture(0, 0, width, height, texture, textureWidth, textureHeight);
    }

    public static IconWidget create(int width, int height, Identifier texture) {
        return new Simple(0, 0, width, height, texture);
    }

    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }

    public void playDownSound(SoundManager soundManager) {
    }

    public boolean isInteractable() {
        return false;
    }

    public abstract void setTexture(Identifier var1);

    public @Nullable GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        return null;
    }
}

