/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.option.FontOptionsScreen
 *  net.minecraft.client.gui.screen.option.GameOptionsScreen
 *  net.minecraft.client.option.GameOptions
 *  net.minecraft.client.option.SimpleOption
 *  net.minecraft.text.Text
 */
package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class FontOptionsScreen
extends GameOptionsScreen {
    private static SimpleOption<?>[] getOptions(GameOptions gameOptions) {
        return new SimpleOption[]{gameOptions.getForceUnicodeFont(), gameOptions.getJapaneseGlyphVariants()};
    }

    public FontOptionsScreen(Screen parent, GameOptions gameOptions) {
        super(parent, gameOptions, (Text)Text.translatable((String)"options.font.title"));
    }

    protected void addOptions() {
        this.body.addAll(FontOptionsScreen.getOptions((GameOptions)this.gameOptions));
    }
}

