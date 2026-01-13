/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.ReconfiguringScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.WarningScreen
 *  net.minecraft.client.gui.screen.multiplayer.CodeOfConductScreen
 *  net.minecraft.client.gui.screen.multiplayer.ConnectScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.LayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.network.ServerInfo
 *  net.minecraft.client.option.ServerList
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.multiplayer;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ReconfiguringScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CodeOfConductScreen
extends WarningScreen {
    private static final Text TITLE_TEXT = Text.translatable((String)"multiplayer.codeOfConduct.title").formatted(Formatting.BOLD);
    private static final Text CHECK_TEXT = Text.translatable((String)"multiplayer.codeOfConduct.check");
    private final @Nullable ServerInfo serverInfo;
    private final String rawCodeOfConduct;
    private final BooleanConsumer callback;
    private final Screen field_62585;

    private CodeOfConductScreen(@Nullable ServerInfo serverInfo, Screen screen, Text text, String string, BooleanConsumer booleanConsumer) {
        super(TITLE_TEXT, text, CHECK_TEXT, (Text)TITLE_TEXT.copy().append("\n").append(text));
        this.serverInfo = serverInfo;
        this.field_62585 = screen;
        this.rawCodeOfConduct = string;
        this.callback = booleanConsumer;
    }

    public CodeOfConductScreen(@Nullable ServerInfo serverInfo, Screen screen, String string, BooleanConsumer booleanConsumer) {
        this(serverInfo, screen, (Text)Text.literal((String)string), string, booleanConsumer);
    }

    protected LayoutWidget getLayout() {
        DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(8);
        directionalLayoutWidget.add((Widget)ButtonWidget.builder((Text)ScreenTexts.ACKNOWLEDGE, button -> this.onAnswer(true)).build());
        directionalLayoutWidget.add((Widget)ButtonWidget.builder((Text)ScreenTexts.DISCONNECT, button -> this.onAnswer(false)).build());
        return directionalLayoutWidget;
    }

    private void onAnswer(boolean acknowledged) {
        this.callback.accept(acknowledged);
        if (this.serverInfo != null) {
            if (acknowledged && this.checkbox.isChecked()) {
                this.serverInfo.setAcceptedCodeOfConduct(this.rawCodeOfConduct);
            } else {
                this.serverInfo.resetAcceptedCodeOfConduct();
            }
            ServerList.updateServerListEntry((ServerInfo)this.serverInfo);
        }
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    public void tick() {
        super.tick();
        if (this.field_62585 instanceof ConnectScreen || this.field_62585 instanceof ReconfiguringScreen) {
            this.field_62585.tick();
        }
    }
}

