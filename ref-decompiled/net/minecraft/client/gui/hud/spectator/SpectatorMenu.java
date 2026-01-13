/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.hud.spectator.RootSpectatorCommandGroup
 *  net.minecraft.client.gui.hud.spectator.SpectatorMenu
 *  net.minecraft.client.gui.hud.spectator.SpectatorMenu$ChangePageSpectatorMenuCommand
 *  net.minecraft.client.gui.hud.spectator.SpectatorMenu$CloseSpectatorMenuCommand
 *  net.minecraft.client.gui.hud.spectator.SpectatorMenuCloseCallback
 *  net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand
 *  net.minecraft.client.gui.hud.spectator.SpectatorMenuCommandGroup
 *  net.minecraft.client.gui.hud.spectator.SpectatorMenuState
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.gui.hud.spectator;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.spectator.RootSpectatorCommandGroup;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCloseCallback;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommandGroup;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuState;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SpectatorMenu {
    static final Identifier CLOSE_TEXTURE = Identifier.ofVanilla((String)"spectator/close");
    static final Identifier SCROLL_LEFT_TEXTURE = Identifier.ofVanilla((String)"spectator/scroll_left");
    static final Identifier SCROLL_RIGHT_TEXTURE = Identifier.ofVanilla((String)"spectator/scroll_right");
    private static final SpectatorMenuCommand CLOSE_COMMAND = new CloseSpectatorMenuCommand();
    private static final SpectatorMenuCommand PREVIOUS_PAGE_COMMAND = new ChangePageSpectatorMenuCommand(-1, true);
    private static final SpectatorMenuCommand NEXT_PAGE_COMMAND = new ChangePageSpectatorMenuCommand(1, true);
    private static final SpectatorMenuCommand DISABLED_NEXT_PAGE_COMMAND = new ChangePageSpectatorMenuCommand(1, false);
    private static final int CLOSE_SLOT = 8;
    static final Text CLOSE_TEXT = Text.translatable((String)"spectatorMenu.close");
    static final Text PREVIOUS_PAGE_TEXT = Text.translatable((String)"spectatorMenu.previous_page");
    static final Text NEXT_PAGE_TEXT = Text.translatable((String)"spectatorMenu.next_page");
    public static final SpectatorMenuCommand BLANK_COMMAND = new /* Unavailable Anonymous Inner Class!! */;
    private final SpectatorMenuCloseCallback closeCallback;
    private SpectatorMenuCommandGroup currentGroup = new RootSpectatorCommandGroup();
    private int selectedSlot = -1;
    int page;

    public SpectatorMenu(SpectatorMenuCloseCallback closeCallback) {
        this.closeCallback = closeCallback;
    }

    public SpectatorMenuCommand getCommand(int slot) {
        int i = slot + this.page * 6;
        if (this.page > 0 && slot == 0) {
            return PREVIOUS_PAGE_COMMAND;
        }
        if (slot == 7) {
            if (i < this.currentGroup.getCommands().size()) {
                return NEXT_PAGE_COMMAND;
            }
            return DISABLED_NEXT_PAGE_COMMAND;
        }
        if (slot == 8) {
            return CLOSE_COMMAND;
        }
        if (i < 0 || i >= this.currentGroup.getCommands().size()) {
            return BLANK_COMMAND;
        }
        return (SpectatorMenuCommand)MoreObjects.firstNonNull((Object)((SpectatorMenuCommand)this.currentGroup.getCommands().get(i)), (Object)BLANK_COMMAND);
    }

    public List<SpectatorMenuCommand> getCommands() {
        ArrayList list = Lists.newArrayList();
        for (int i = 0; i <= 8; ++i) {
            list.add(this.getCommand(i));
        }
        return list;
    }

    public SpectatorMenuCommand getSelectedCommand() {
        return this.getCommand(this.selectedSlot);
    }

    public SpectatorMenuCommandGroup getCurrentGroup() {
        return this.currentGroup;
    }

    public void useCommand(int slot) {
        SpectatorMenuCommand spectatorMenuCommand = this.getCommand(slot);
        if (spectatorMenuCommand != BLANK_COMMAND) {
            if (this.selectedSlot == slot && spectatorMenuCommand.isEnabled()) {
                spectatorMenuCommand.use(this);
            } else {
                this.selectedSlot = slot;
            }
        }
    }

    public void close() {
        this.closeCallback.close(this);
    }

    public int getSelectedSlot() {
        return this.selectedSlot;
    }

    public void selectElement(SpectatorMenuCommandGroup group) {
        this.currentGroup = group;
        this.selectedSlot = -1;
        this.page = 0;
    }

    public SpectatorMenuState getCurrentState() {
        return new SpectatorMenuState(this.getCommands(), this.selectedSlot);
    }
}

