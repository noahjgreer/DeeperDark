/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.DrawnTextConsumer
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Drawable
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.Selectable
 *  net.minecraft.client.gui.Selectable$SelectionType
 *  net.minecraft.client.gui.cursor.StandardCursors
 *  net.minecraft.client.gui.navigation.GuiNavigation
 *  net.minecraft.client.gui.navigation.GuiNavigationPath
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.gui.screen.narration.NarrationPart
 *  net.minecraft.client.gui.tooltip.Tooltip
 *  net.minecraft.client.gui.tooltip.TooltipState
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.input.MouseInput
 *  net.minecraft.client.sound.PositionedSoundInstance
 *  net.minecraft.client.sound.SoundInstance
 *  net.minecraft.client.sound.SoundManager
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import java.time.Duration;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.cursor.StandardCursors;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.tooltip.TooltipState;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public abstract class ClickableWidget
implements Drawable,
Element,
Widget,
Selectable {
    protected int width;
    protected int height;
    private int x;
    private int y;
    protected Text message;
    protected boolean hovered;
    public boolean active = true;
    public boolean visible = true;
    protected float alpha = 1.0f;
    private int navigationOrder;
    private boolean focused;
    private final TooltipState tooltip = new TooltipState();

    public ClickableWidget(int x, int y, int width, int height, Text message) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.message = message;
    }

    public int getHeight() {
        return this.height;
    }

    public final void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (!this.visible) {
            return;
        }
        this.hovered = context.scissorContains(mouseX, mouseY) && this.isInBounds((double)mouseX, (double)mouseY);
        this.renderWidget(context, mouseX, mouseY, deltaTicks);
        this.tooltip.render(context, mouseX, mouseY, this.isHovered(), this.isFocused(), this.getNavigationFocus());
    }

    protected void setCursor(DrawContext context) {
        if (this.isHovered()) {
            context.setCursor(this.isInteractable() ? StandardCursors.POINTING_HAND : StandardCursors.NOT_ALLOWED);
        }
    }

    public void setTooltip(@Nullable Tooltip tooltip) {
        this.tooltip.setTooltip(tooltip);
    }

    public void setTooltipDelay(Duration tooltipDelay) {
        this.tooltip.setDelay(tooltipDelay);
    }

    protected MutableText getNarrationMessage() {
        return ClickableWidget.getNarrationMessage((Text)this.getMessage());
    }

    public static MutableText getNarrationMessage(Text message) {
        return Text.translatable((String)"gui.narrate.button", (Object[])new Object[]{message});
    }

    protected abstract void renderWidget(DrawContext var1, int var2, int var3, float var4);

    protected void drawTextWithMargin(DrawnTextConsumer drawer, Text text, int marginX) {
        int i = this.getX() + marginX;
        int j = this.getX() + this.getWidth() - marginX;
        int k = this.getY();
        int l = this.getY() + this.getHeight();
        drawer.text(text, i, j, k, l);
    }

    public void onClick(Click click, boolean doubled) {
    }

    public void onRelease(Click click) {
    }

    protected void onDrag(Click click, double offsetX, double offsetY) {
    }

    public boolean mouseClicked(Click click, boolean doubled) {
        boolean bl;
        if (!this.isInteractable()) {
            return false;
        }
        if (this.isValidClickButton(click.buttonInfo()) && (bl = this.isMouseOver(click.x(), click.y()))) {
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            this.onClick(click, doubled);
            return true;
        }
        return false;
    }

    public boolean mouseReleased(Click click) {
        if (this.isValidClickButton(click.buttonInfo())) {
            this.onRelease(click);
            return true;
        }
        return false;
    }

    protected boolean isValidClickButton(MouseInput input) {
        return input.button() == 0;
    }

    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if (this.isValidClickButton(click.buttonInfo())) {
            this.onDrag(click, offsetX, offsetY);
            return true;
        }
        return false;
    }

    public @Nullable GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        if (!this.isInteractable()) {
            return null;
        }
        if (!this.isFocused()) {
            return GuiNavigationPath.of((Element)this);
        }
        return null;
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.isInteractable() && this.isInBounds(mouseX, mouseY);
    }

    public void playDownSound(SoundManager soundManager) {
        ClickableWidget.playClickSound((SoundManager)soundManager);
    }

    public static void playClickSound(SoundManager soundManager) {
        soundManager.play((SoundInstance)PositionedSoundInstance.ui((RegistryEntry)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public void setMessage(Text message) {
        this.message = message;
    }

    public Text getMessage() {
        return this.message;
    }

    public boolean isFocused() {
        return this.focused;
    }

    public boolean isHovered() {
        return this.hovered;
    }

    public boolean isSelected() {
        return this.isHovered() || this.isFocused();
    }

    public boolean isInteractable() {
        return this.visible && this.active;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public Selectable.SelectionType getType() {
        if (this.isFocused()) {
            return Selectable.SelectionType.FOCUSED;
        }
        if (this.hovered) {
            return Selectable.SelectionType.HOVERED;
        }
        return Selectable.SelectionType.NONE;
    }

    public final void appendNarrations(NarrationMessageBuilder builder) {
        this.appendClickableNarrations(builder);
        this.tooltip.appendNarrations(builder);
    }

    protected abstract void appendClickableNarrations(NarrationMessageBuilder var1);

    protected void appendDefaultNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, (Text)this.getNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                builder.put(NarrationPart.USAGE, (Text)Text.translatable((String)"narration.button.usage.focused"));
            } else {
                builder.put(NarrationPart.USAGE, (Text)Text.translatable((String)"narration.button.usage.hovered"));
            }
        }
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getRight() {
        return this.getX() + this.getWidth();
    }

    public int getBottom() {
        return this.getY() + this.getHeight();
    }

    public void forEachChild(Consumer<ClickableWidget> consumer) {
        consumer.accept(this);
    }

    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public ScreenRect getNavigationFocus() {
        return super.getNavigationFocus();
    }

    private boolean isInBounds(double x, double y) {
        return x >= (double)this.getX() && y >= (double)this.getY() && x < (double)this.getRight() && y < (double)this.getBottom();
    }

    public void setDimensionsAndPosition(int width, int height, int x, int y) {
        this.setDimensions(width, height);
        this.setPosition(x, y);
    }

    public int getNavigationOrder() {
        return this.navigationOrder;
    }

    public void setNavigationOrder(int navigationOrder) {
        this.navigationOrder = navigationOrder;
    }
}

