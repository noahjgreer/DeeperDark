/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.pack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.SquareWidgetEntry;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

@Environment(value=EnvType.CLIENT)
public class PackListWidget.ResourcePackEntry
extends PackListWidget.Entry
implements SquareWidgetEntry {
    private static final int field_32403 = 157;
    public static final int field_64204 = 32;
    private final PackListWidget widget;
    protected final MinecraftClient client;
    private final ResourcePackOrganizer.Pack pack;
    private final TextWidget nameWidget;
    private final MultilineTextWidget descriptionWidget;

    public PackListWidget.ResourcePackEntry(MinecraftClient client, PackListWidget widget, ResourcePackOrganizer.Pack pack) {
        super(PackListWidget.this);
        this.client = client;
        this.pack = pack;
        this.widget = widget;
        this.nameWidget = new TextWidget(pack.getDisplayName(), client.textRenderer);
        this.descriptionWidget = new MultilineTextWidget(Texts.withStyle(pack.getDecoratedDescription(), Style.EMPTY.withColor(-8355712)), client.textRenderer);
        this.descriptionWidget.setMaxRows(2);
    }

    @Override
    public Text getNarration() {
        return Text.translatable("narrator.select", this.pack.getDisplayName());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        int j;
        int i;
        ResourcePackCompatibility resourcePackCompatibility = this.pack.getCompatibility();
        if (!resourcePackCompatibility.isCompatible()) {
            i = this.getContentX() - 1;
            j = this.getContentY() - 1;
            int k = this.getContentRightEnd() + 1;
            int l = this.getContentBottomEnd() + 1;
            context.fill(i, j, k, l, -8978432);
        }
        context.drawTexture(RenderPipelines.GUI_TEXTURED, this.pack.getIconId(), this.getContentX(), this.getContentY(), 0.0f, 0.0f, 32, 32, 32, 32);
        if (!this.nameWidget.getMessage().equals(this.pack.getDisplayName())) {
            this.nameWidget.setMessage(this.pack.getDisplayName());
        }
        if (!this.descriptionWidget.getMessage().getContent().equals(this.pack.getDecoratedDescription().getContent())) {
            this.descriptionWidget.setMessage(Texts.withStyle(this.pack.getDecoratedDescription(), Style.EMPTY.withColor(-8355712)));
        }
        if (this.isSelectable() && (this.client.options.getTouchscreen().getValue().booleanValue() || hovered || this.widget.getSelectedOrNull() == this && this.widget.isFocused())) {
            context.fill(this.getContentX(), this.getContentY(), this.getContentX() + 32, this.getContentY() + 32, -1601138544);
            i = mouseX - this.getContentX();
            j = mouseY - this.getContentY();
            if (!this.pack.getCompatibility().isCompatible()) {
                this.nameWidget.setMessage(INCOMPATIBLE);
                this.descriptionWidget.setMessage(this.pack.getCompatibility().getNotification());
            }
            if (this.pack.canBeEnabled()) {
                if (this.isInside(i, j, 32)) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SELECT_HIGHLIGHTED_TEXTURE, this.getContentX(), this.getContentY(), 32, 32);
                    PackListWidget.this.setCursor(context);
                } else {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SELECT_TEXTURE, this.getContentX(), this.getContentY(), 32, 32);
                }
            } else {
                if (this.pack.canBeDisabled()) {
                    if (this.isLeft(i, j, 32)) {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, UNSELECT_HIGHLIGHTED_TEXTURE, this.getContentX(), this.getContentY(), 32, 32);
                        PackListWidget.this.setCursor(context);
                    } else {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, UNSELECT_TEXTURE, this.getContentX(), this.getContentY(), 32, 32);
                    }
                }
                if (this.pack.canMoveTowardStart()) {
                    if (this.isBottomRight(i, j, 32)) {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MOVE_UP_HIGHLIGHTED_TEXTURE, this.getContentX(), this.getContentY(), 32, 32);
                        PackListWidget.this.setCursor(context);
                    } else {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MOVE_UP_TEXTURE, this.getContentX(), this.getContentY(), 32, 32);
                    }
                }
                if (this.pack.canMoveTowardEnd()) {
                    if (this.isTopRight(i, j, 32)) {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MOVE_DOWN_HIGHLIGHTED_TEXTURE, this.getContentX(), this.getContentY(), 32, 32);
                        PackListWidget.this.setCursor(context);
                    } else {
                        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MOVE_DOWN_TEXTURE, this.getContentX(), this.getContentY(), 32, 32);
                    }
                }
            }
        }
        this.nameWidget.setMaxWidth(157 - (PackListWidget.this.overflows() ? 6 : 0));
        this.nameWidget.setPosition(this.getContentX() + 32 + 2, this.getContentY() + 1);
        this.nameWidget.render(context, mouseX, mouseY, deltaTicks);
        this.descriptionWidget.setMaxWidth(157 - (PackListWidget.this.overflows() ? 6 : 0));
        this.descriptionWidget.setPosition(this.getContentX() + 32 + 2, this.getContentY() + 12);
        this.descriptionWidget.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (this.isSelectable()) {
            int i = (int)click.x() - this.getContentX();
            int j = (int)click.y() - this.getContentY();
            if (this.pack.canBeEnabled() && this.isInside(i, j, 32)) {
                this.enable();
                return true;
            }
            if (this.pack.canBeDisabled() && this.isLeft(i, j, 32)) {
                this.pack.disable();
                return true;
            }
            if (this.pack.canMoveTowardStart() && this.isBottomRight(i, j, 32)) {
                this.pack.moveTowardStart();
                return true;
            }
            if (this.pack.canMoveTowardEnd() && this.isTopRight(i, j, 32)) {
                this.pack.moveTowardEnd();
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.isEnter()) {
            this.toggle();
            return true;
        }
        if (input.hasShift()) {
            if (input.isUp()) {
                this.moveTowardStart();
                return true;
            }
            if (input.isDown()) {
                this.moveTowardEnd();
                return true;
            }
        }
        return super.keyPressed(input);
    }

    private boolean isSelectable() {
        return !this.pack.isPinned() || !this.pack.isAlwaysEnabled();
    }

    public void toggle() {
        if (this.pack.canBeEnabled()) {
            this.enable();
        } else if (this.pack.canBeDisabled()) {
            this.pack.disable();
        }
    }

    private void moveTowardStart() {
        if (this.pack.canMoveTowardStart()) {
            this.pack.moveTowardStart();
        }
    }

    private void moveTowardEnd() {
        if (this.pack.canMoveTowardEnd()) {
            this.pack.moveTowardEnd();
        }
    }

    private void enable() {
        if (this.pack.getCompatibility().isCompatible()) {
            this.pack.enable();
        } else {
            Text text = this.pack.getCompatibility().getConfirmMessage();
            this.client.setScreen(new ConfirmScreen(confirmed -> {
                this.client.setScreen(this.widget.screen);
                if (confirmed) {
                    this.pack.enable();
                }
            }, INCOMPATIBLE_CONFIRM, text));
        }
    }

    @Override
    public String getName() {
        return this.pack.getName();
    }

    @Override
    public boolean isClickable() {
        return PackListWidget.this.children().stream().anyMatch(entry -> entry.getName().equals(this.getName()));
    }
}
