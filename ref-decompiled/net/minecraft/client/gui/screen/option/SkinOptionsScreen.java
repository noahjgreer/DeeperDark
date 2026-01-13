/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.option.GameOptionsScreen
 *  net.minecraft.client.gui.screen.option.SkinOptionsScreen
 *  net.minecraft.client.gui.widget.CyclingButtonWidget
 *  net.minecraft.client.option.GameOptions
 *  net.minecraft.entity.player.PlayerModelPart
 *  net.minecraft.text.Text
 */
package net.minecraft.client.gui.screen.option;

import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class SkinOptionsScreen
extends GameOptionsScreen {
    private static final Text TITLE_TEXT = Text.translatable((String)"options.skinCustomisation.title");

    public SkinOptionsScreen(Screen parent, GameOptions gameOptions) {
        super(parent, gameOptions, TITLE_TEXT);
    }

    protected void addOptions() {
        ArrayList<Object> list = new ArrayList<Object>();
        for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
            list.add(CyclingButtonWidget.onOffBuilder((boolean)this.gameOptions.isPlayerModelPartEnabled(playerModelPart)).build(playerModelPart.getOptionName(), (button, enabled) -> this.gameOptions.setPlayerModelPart(playerModelPart, enabled.booleanValue())));
        }
        list.add(this.gameOptions.getMainArm().createWidget(this.gameOptions));
        this.body.addAll(list);
    }
}

