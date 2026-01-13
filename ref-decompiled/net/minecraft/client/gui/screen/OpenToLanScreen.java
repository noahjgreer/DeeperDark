/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.OpenToLanScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.tooltip.Tooltip
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.CyclingButtonWidget
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.server.command.PublishCommand
 *  net.minecraft.server.integrated.IntegratedServer
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.NetworkUtils
 *  net.minecraft.world.GameMode
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.command.PublishCommand;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.NetworkUtils;
import net.minecraft.world.GameMode;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class OpenToLanScreen
extends Screen {
    private static final int MIN_PORT = 1024;
    private static final int MAX_PORT = 65535;
    private static final Text ALLOW_COMMANDS_TEXT = Text.translatable((String)"selectWorld.allowCommands");
    private static final Text GAME_MODE_TEXT = Text.translatable((String)"selectWorld.gameMode");
    private static final Text OTHER_PLAYERS_TEXT = Text.translatable((String)"lanServer.otherPlayers");
    private static final Text PORT_TEXT = Text.translatable((String)"lanServer.port");
    private static final Text UNAVAILABLE_PORT_TEXT = Text.translatable((String)"lanServer.port.unavailable", (Object[])new Object[]{1024, 65535});
    private static final Text INVALID_PORT_TEXT = Text.translatable((String)"lanServer.port.invalid", (Object[])new Object[]{1024, 65535});
    private final Screen parent;
    private GameMode gameMode = GameMode.SURVIVAL;
    private boolean allowCommands;
    private int port = NetworkUtils.findLocalPort();
    private @Nullable TextFieldWidget portField;

    public OpenToLanScreen(Screen screen) {
        super((Text)Text.translatable((String)"lanServer.title"));
        this.parent = screen;
    }

    protected void init() {
        IntegratedServer integratedServer = this.client.getServer();
        this.gameMode = integratedServer.getDefaultGameMode();
        this.allowCommands = integratedServer.getSaveProperties().areCommandsAllowed();
        this.addDrawableChild((Element)CyclingButtonWidget.builder(GameMode::getSimpleTranslatableName, (Object)this.gameMode).values((Object[])new GameMode[]{GameMode.SURVIVAL, GameMode.SPECTATOR, GameMode.CREATIVE, GameMode.ADVENTURE}).build(this.width / 2 - 155, 100, 150, 20, GAME_MODE_TEXT, (button, gameMode) -> {
            this.gameMode = gameMode;
        }));
        this.addDrawableChild((Element)CyclingButtonWidget.onOffBuilder((boolean)this.allowCommands).build(this.width / 2 + 5, 100, 150, 20, ALLOW_COMMANDS_TEXT, (button, allowCommands) -> {
            this.allowCommands = allowCommands;
        }));
        ButtonWidget buttonWidget = ButtonWidget.builder((Text)Text.translatable((String)"lanServer.start"), button -> {
            this.client.setScreen(null);
            MutableText text = integratedServer.openToLan(this.gameMode, this.allowCommands, this.port) ? PublishCommand.getStartedText((int)this.port) : Text.translatable((String)"commands.publish.failed");
            this.client.inGameHud.getChatHud().addMessage((Text)text);
            this.client.getNarratorManager().narrateSystemMessage((Text)text);
            this.client.updateWindowTitle();
        }).dimensions(this.width / 2 - 155, this.height - 28, 150, 20).build();
        this.portField = new TextFieldWidget(this.textRenderer, this.width / 2 - 75, 160, 150, 20, (Text)Text.translatable((String)"lanServer.port"));
        this.portField.setChangedListener(portText -> {
            Text text = this.updatePort(portText);
            this.portField.setPlaceholder((Text)Text.literal((String)("" + this.port)));
            if (text == null) {
                this.portField.setEditableColor(-2039584);
                this.portField.setTooltip(null);
                buttonWidget.active = true;
            } else {
                this.portField.setEditableColor(-2142128);
                this.portField.setTooltip(Tooltip.of((Text)text));
                buttonWidget.active = false;
            }
        });
        this.portField.setPlaceholder((Text)Text.literal((String)("" + this.port)));
        this.addDrawableChild((Element)this.portField);
        this.addDrawableChild((Element)buttonWidget);
        this.addDrawableChild((Element)ButtonWidget.builder((Text)ScreenTexts.CANCEL, button -> this.close()).dimensions(this.width / 2 + 5, this.height - 28, 150, 20).build());
    }

    public void close() {
        this.client.setScreen(this.parent);
    }

    private @Nullable Text updatePort(String portText) {
        if (portText.isBlank()) {
            this.port = NetworkUtils.findLocalPort();
            return null;
        }
        try {
            this.port = Integer.parseInt(portText);
            if (this.port < 1024 || this.port > 65535) {
                return INVALID_PORT_TEXT;
            }
            if (!NetworkUtils.isPortAvailable((int)this.port)) {
                return UNAVAILABLE_PORT_TEXT;
            }
            return null;
        }
        catch (NumberFormatException numberFormatException) {
            this.port = NetworkUtils.findLocalPort();
            return INVALID_PORT_TEXT;
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 50, -1);
        context.drawCenteredTextWithShadow(this.textRenderer, OTHER_PLAYERS_TEXT, this.width / 2, 82, -1);
        context.drawCenteredTextWithShadow(this.textRenderer, PORT_TEXT, this.width / 2, 142, -1);
    }
}

