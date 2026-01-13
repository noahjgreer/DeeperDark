/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.option.GameOptionsScreen
 *  net.minecraft.client.gui.screen.option.SoundOptionsScreen
 *  net.minecraft.client.option.GameOptions
 *  net.minecraft.client.option.SimpleOption
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.text.Text
 */
package net.minecraft.client.gui.screen.option;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class SoundOptionsScreen
extends GameOptionsScreen {
    private static final Text TITLE_TEXT = Text.translatable((String)"options.sounds.title");

    public SoundOptionsScreen(Screen parent, GameOptions options) {
        super(parent, options, TITLE_TEXT);
    }

    protected void addOptions() {
        this.body.addSingleOptionEntry(this.gameOptions.getSoundVolumeOption(SoundCategory.MASTER));
        this.body.addAll(this.getVolumeOptions());
        this.body.addSingleOptionEntry(this.gameOptions.getSoundDevice());
        this.body.addAll(new SimpleOption[]{this.gameOptions.getShowSubtitles(), this.gameOptions.getDirectionalAudio()});
        this.body.addAll(new SimpleOption[]{this.gameOptions.getMusicFrequency(), this.gameOptions.getMusicToast()});
    }

    private SimpleOption<?>[] getVolumeOptions() {
        return (SimpleOption[])Arrays.stream(SoundCategory.values()).filter(category -> category != SoundCategory.MASTER).map(arg_0 -> ((GameOptions)this.gameOptions).getSoundVolumeOption(arg_0)).toArray(SimpleOption[]::new);
    }
}

