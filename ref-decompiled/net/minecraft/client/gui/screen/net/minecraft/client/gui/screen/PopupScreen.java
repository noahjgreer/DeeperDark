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
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.IconWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PopupScreen
extends Screen {
    private static final Identifier BACKGROUND_TEXTURE = Identifier.ofVanilla("popup/background");
    private static final int VERTICAL_SPACING = 12;
    private static final int MARGIN_WIDTH = 18;
    private static final int BUTTON_HORIZONTAL_SPACING = 6;
    private static final int IMAGE_WIDTH = 130;
    private static final int IMAGE_HEIGHT = 64;
    private static final int DEFAULT_WIDTH = 250;
    private final Screen backgroundScreen;
    private final @Nullable Identifier image;
    private final Text message;
    private final List<Button> buttons;
    private final @Nullable Runnable onClosed;
    private final int innerWidth;
    private final DirectionalLayoutWidget layout = DirectionalLayoutWidget.vertical();

    PopupScreen(Screen backgroundScreen, int width, @Nullable Identifier image, Text title, Text message, List<Button> buttons, @Nullable Runnable onClosed) {
        super(title);
        this.backgroundScreen = backgroundScreen;
        this.image = image;
        this.message = message;
        this.buttons = buttons;
        this.onClosed = onClosed;
        this.innerWidth = width - 36;
    }

    @Override
    public void onDisplayed() {
        super.onDisplayed();
        this.backgroundScreen.blur();
    }

    @Override
    protected void init() {
        this.backgroundScreen.init(this.width, this.height);
        this.layout.spacing(12).getMainPositioner().alignHorizontalCenter();
        this.layout.add(new MultilineTextWidget(this.title.copy().formatted(Formatting.BOLD), this.textRenderer).setMaxWidth(this.innerWidth).setCentered(true));
        if (this.image != null) {
            this.layout.add(IconWidget.create(130, 64, this.image, 130, 64));
        }
        this.layout.add(new MultilineTextWidget(this.message, this.textRenderer).setMaxWidth(this.innerWidth).setCentered(true));
        this.layout.add(this.createButtonLayout());
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    private DirectionalLayoutWidget createButtonLayout() {
        int i = 6 * (this.buttons.size() - 1);
        int j = Math.min((this.innerWidth - i) / this.buttons.size(), 150);
        DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal();
        directionalLayoutWidget.spacing(6);
        for (Button button2 : this.buttons) {
            directionalLayoutWidget.add(ButtonWidget.builder(button2.message(), button -> button2.action().accept(this)).width(j).build());
        }
        return directionalLayoutWidget;
    }

    @Override
    protected void refreshWidgetPositions() {
        this.backgroundScreen.resize(this.width, this.height);
        this.layout.refreshPositions();
        SimplePositioningWidget.setPos(this.layout, this.getNavigationFocus());
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.backgroundScreen.renderBackground(context, mouseX, mouseY, deltaTicks);
        context.createNewRootLayer();
        this.backgroundScreen.render(context, -1, -1, deltaTicks);
        context.createNewRootLayer();
        this.renderInGameBackground(context);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, this.layout.getX() - 18, this.layout.getY() - 18, this.layout.getWidth() + 36, this.layout.getHeight() + 36);
    }

    @Override
    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences(this.title, this.message);
    }

    @Override
    public void close() {
        if (this.onClosed != null) {
            this.onClosed.run();
        }
        this.client.setScreen(this.backgroundScreen);
    }

    @Environment(value=EnvType.CLIENT)
    record Button(Text message, Consumer<PopupScreen> action) {
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        private final Screen backgroundScreen;
        private final Text title;
        private Text message = ScreenTexts.EMPTY;
        private int width = 250;
        private @Nullable Identifier image;
        private final List<Button> buttons = new ArrayList<Button>();
        private @Nullable Runnable onClosed = null;

        public Builder(Screen backgroundScreen, Text title) {
            this.backgroundScreen = backgroundScreen;
            this.title = title;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder image(Identifier image) {
            this.image = image;
            return this;
        }

        public Builder message(Text message) {
            this.message = message;
            return this;
        }

        public Builder button(Text message, Consumer<PopupScreen> action) {
            this.buttons.add(new Button(message, action));
            return this;
        }

        public Builder onClosed(Runnable onClosed) {
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
}
