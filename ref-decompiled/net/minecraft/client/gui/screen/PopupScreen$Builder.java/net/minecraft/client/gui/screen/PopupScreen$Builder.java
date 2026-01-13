/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.PopupScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class PopupScreen.Builder {
    private final Screen backgroundScreen;
    private final Text title;
    private Text message = ScreenTexts.EMPTY;
    private int width = 250;
    private @Nullable Identifier image;
    private final List<PopupScreen.Button> buttons = new ArrayList<PopupScreen.Button>();
    private @Nullable Runnable onClosed = null;

    public PopupScreen.Builder(Screen backgroundScreen, Text title) {
        this.backgroundScreen = backgroundScreen;
        this.title = title;
    }

    public PopupScreen.Builder width(int width) {
        this.width = width;
        return this;
    }

    public PopupScreen.Builder image(Identifier image) {
        this.image = image;
        return this;
    }

    public PopupScreen.Builder message(Text message) {
        this.message = message;
        return this;
    }

    public PopupScreen.Builder button(Text message, Consumer<PopupScreen> action) {
        this.buttons.add(new PopupScreen.Button(message, action));
        return this;
    }

    public PopupScreen.Builder onClosed(Runnable onClosed) {
        this.onClosed = onClosed;
        return this;
    }

    public PopupScreen build() {
        if (this.buttons.isEmpty()) {
            throw new IllegalStateException("Popup must have at least one button");
        }
        return new PopupScreen(this.backgroundScreen, this.width, this.image, this.title, this.message, List.copyOf(this.buttons), this.onClosed);
    }
}
