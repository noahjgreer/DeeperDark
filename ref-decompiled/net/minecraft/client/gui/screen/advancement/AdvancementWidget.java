/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.advancement.AdvancementDisplay
 *  net.minecraft.advancement.AdvancementProgress
 *  net.minecraft.advancement.PlacedAdvancement
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextHandler
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.advancement.AdvancementObtainedStatus
 *  net.minecraft.client.gui.screen.advancement.AdvancementTab
 *  net.minecraft.client.gui.screen.advancement.AdvancementWidget
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.OrderedText
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  net.minecraft.text.Texts
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Language
 *  net.minecraft.util.math.MathHelper
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.advancement;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.advancement.AdvancementObtainedStatus;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class AdvancementWidget {
    private static final Identifier TITLE_BOX_TEXTURE = Identifier.ofVanilla((String)"advancements/title_box");
    private static final int field_32286 = 26;
    private static final int field_32287 = 0;
    private static final int field_32288 = 200;
    private static final int field_32289 = 26;
    private static final int ICON_OFFSET_X = 8;
    private static final int ICON_OFFSET_Y = 5;
    private static final int ICON_SIZE = 26;
    private static final int field_32293 = 3;
    private static final int field_32294 = 5;
    private static final int TITLE_OFFSET_X = 32;
    private static final int TITLE_OFFSET_Y = 9;
    private static final int field_55103 = 8;
    private static final int TITLE_MAX_WIDTH = 163;
    private static final int field_55104 = 80;
    private static final int[] SPLIT_OFFSET_CANDIDATES = new int[]{0, 10, -10, 25, -25};
    private final AdvancementTab tab;
    private final PlacedAdvancement advancement;
    private final AdvancementDisplay display;
    private final List<OrderedText> title;
    private final int width;
    private final List<OrderedText> description;
    private final MinecraftClient client;
    private @Nullable AdvancementWidget parent;
    private final List<AdvancementWidget> children = Lists.newArrayList();
    private @Nullable AdvancementProgress progress;
    private final int x;
    private final int y;

    public AdvancementWidget(AdvancementTab tab, MinecraftClient client, PlacedAdvancement advancement, AdvancementDisplay display) {
        this.tab = tab;
        this.advancement = advancement;
        this.display = display;
        this.client = client;
        this.title = client.textRenderer.wrapLines((StringVisitable)display.getTitle(), 163);
        this.x = MathHelper.floor((float)(display.getX() * 28.0f));
        this.y = MathHelper.floor((float)(display.getY() * 27.0f));
        int i = Math.max(this.title.stream().mapToInt(arg_0 -> ((TextRenderer)client.textRenderer).getWidth(arg_0)).max().orElse(0), 80);
        int j = this.getProgressWidth();
        int k = 29 + i + j;
        this.description = Language.getInstance().reorder(this.wrapDescription(Texts.withStyle((Text)display.getDescription(), (Style)Style.EMPTY.withColor(display.getFrame().getTitleFormat())), k));
        for (OrderedText orderedText : this.description) {
            k = Math.max(k, client.textRenderer.getWidth(orderedText));
        }
        this.width = k + 3 + 5;
    }

    private int getProgressWidth() {
        int i = this.advancement.getAdvancement().requirements().getLength();
        if (i <= 1) {
            return 0;
        }
        int j = 8;
        MutableText text = Text.translatable((String)"advancements.progress", (Object[])new Object[]{i, i});
        return this.client.textRenderer.getWidth((StringVisitable)text) + 8;
    }

    private static float getMaxWidth(TextHandler textHandler, List<StringVisitable> lines) {
        return (float)lines.stream().mapToDouble(arg_0 -> ((TextHandler)textHandler).getWidth(arg_0)).max().orElse(0.0);
    }

    private List<StringVisitable> wrapDescription(Text text, int width) {
        TextHandler textHandler = this.client.textRenderer.getTextHandler();
        List list = null;
        float f = Float.MAX_VALUE;
        for (int i : SPLIT_OFFSET_CANDIDATES) {
            List list2 = textHandler.wrapLines((StringVisitable)text, width - i, Style.EMPTY);
            float g = Math.abs(AdvancementWidget.getMaxWidth((TextHandler)textHandler, (List)list2) - (float)width);
            if (g <= 10.0f) {
                return list2;
            }
            if (!(g < f)) continue;
            f = g;
            list = list2;
        }
        return list;
    }

    private @Nullable AdvancementWidget getParent(PlacedAdvancement advancement) {
        while ((advancement = advancement.getParent()) != null && advancement.getAdvancement().display().isEmpty()) {
        }
        if (advancement == null || advancement.getAdvancement().display().isEmpty()) {
            return null;
        }
        return this.tab.getWidget(advancement.getAdvancementEntry());
    }

    public void renderLines(DrawContext context, int x, int y, boolean border) {
        if (this.parent != null) {
            int n;
            int i = x + this.parent.x + 13;
            int j = x + this.parent.x + 26 + 4;
            int k = y + this.parent.y + 13;
            int l = x + this.x + 13;
            int m = y + this.y + 13;
            int n2 = n = border ? -16777216 : -1;
            if (border) {
                context.drawHorizontalLine(j, i, k - 1, n);
                context.drawHorizontalLine(j + 1, i, k, n);
                context.drawHorizontalLine(j, i, k + 1, n);
                context.drawHorizontalLine(l, j - 1, m - 1, n);
                context.drawHorizontalLine(l, j - 1, m, n);
                context.drawHorizontalLine(l, j - 1, m + 1, n);
                context.drawVerticalLine(j - 1, m, k, n);
                context.drawVerticalLine(j + 1, m, k, n);
            } else {
                context.drawHorizontalLine(j, i, k, n);
                context.drawHorizontalLine(l, j, m, n);
                context.drawVerticalLine(j, m, k, n);
            }
        }
        for (AdvancementWidget advancementWidget : this.children) {
            advancementWidget.renderLines(context, x, y, border);
        }
    }

    public void renderWidgets(DrawContext context, int x, int y) {
        if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
            float f = this.progress == null ? 0.0f : this.progress.getProgressBarPercentage();
            AdvancementObtainedStatus advancementObtainedStatus = f >= 1.0f ? AdvancementObtainedStatus.OBTAINED : AdvancementObtainedStatus.UNOBTAINED;
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, advancementObtainedStatus.getFrameTexture(this.display.getFrame()), x + this.x + 3, y + this.y, 26, 26);
            context.drawItemWithoutEntity(this.display.getIcon(), x + this.x + 8, y + this.y + 5);
        }
        for (AdvancementWidget advancementWidget : this.children) {
            advancementWidget.renderWidgets(context, x, y);
        }
    }

    public int getWidth() {
        return this.width;
    }

    public void setProgress(AdvancementProgress progress) {
        this.progress = progress;
    }

    public void addChild(AdvancementWidget widget) {
        this.children.add(widget);
    }

    public void drawTooltip(DrawContext context, int originX, int originY, float alpha, int x, int y) {
        AdvancementObtainedStatus advancementObtainedStatus3;
        AdvancementObtainedStatus advancementObtainedStatus2;
        AdvancementObtainedStatus advancementObtainedStatus;
        TextRenderer textRenderer = this.client.textRenderer;
        Objects.requireNonNull(textRenderer);
        int i = 9 * this.title.size() + 9 + 8;
        int j = originY + this.y + (26 - i) / 2;
        int k = j + i;
        int n = this.description.size();
        Objects.requireNonNull(textRenderer);
        int l = n * 9;
        int m = 6 + l;
        boolean bl = x + originX + this.x + this.width + 26 >= this.tab.getScreen().width;
        Text text = this.progress == null ? null : this.progress.getProgressBarFraction();
        int n2 = text == null ? 0 : textRenderer.getWidth((StringVisitable)text);
        boolean bl2 = k + m >= 113;
        float f = this.progress == null ? 0.0f : this.progress.getProgressBarPercentage();
        int o = MathHelper.floor((float)(f * (float)this.width));
        if (f >= 1.0f) {
            o = this.width / 2;
            advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus2 = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus3 = AdvancementObtainedStatus.OBTAINED;
        } else if (o < 2) {
            o = this.width / 2;
            advancementObtainedStatus = AdvancementObtainedStatus.UNOBTAINED;
            advancementObtainedStatus2 = AdvancementObtainedStatus.UNOBTAINED;
            advancementObtainedStatus3 = AdvancementObtainedStatus.UNOBTAINED;
        } else if (o > this.width - 2) {
            o = this.width / 2;
            advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus2 = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus3 = AdvancementObtainedStatus.UNOBTAINED;
        } else {
            advancementObtainedStatus = AdvancementObtainedStatus.OBTAINED;
            advancementObtainedStatus2 = AdvancementObtainedStatus.UNOBTAINED;
            advancementObtainedStatus3 = AdvancementObtainedStatus.UNOBTAINED;
        }
        int p = this.width - o;
        int q = bl ? originX + this.x - this.width + 26 + 6 : originX + this.x;
        int r = i + m;
        if (!this.description.isEmpty()) {
            if (bl2) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TITLE_BOX_TEXTURE, q, k - r, this.width, r);
            } else {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TITLE_BOX_TEXTURE, q, j, this.width, r);
            }
        }
        if (advancementObtainedStatus != advancementObtainedStatus2) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, advancementObtainedStatus.getBoxTexture(), 200, i, 0, 0, q, j, o, i);
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, advancementObtainedStatus2.getBoxTexture(), 200, i, 200 - p, 0, q + o, j, p, i);
        } else {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, advancementObtainedStatus.getBoxTexture(), q, j, this.width, i);
        }
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, advancementObtainedStatus3.getFrameTexture(this.display.getFrame()), originX + this.x + 3, originY + this.y, 26, 26);
        int s = q + 5;
        if (bl) {
            this.drawText(context, this.title, s, j + 9, -1);
            if (text != null) {
                context.drawTextWithShadow(textRenderer, text, originX + this.x - n2, j + 9, -1);
            }
        } else {
            this.drawText(context, this.title, originX + this.x + 32, j + 9, -1);
            if (text != null) {
                context.drawTextWithShadow(textRenderer, text, originX + this.x + this.width - n2 - 5, j + 9, -1);
            }
        }
        if (bl2) {
            this.drawText(context, this.description, s, j - l + 1, -16711936);
        } else {
            this.drawText(context, this.description, s, k, -16711936);
        }
        context.drawItemWithoutEntity(this.display.getIcon(), originX + this.x + 8, originY + this.y + 5);
    }

    private void drawText(DrawContext context, List<OrderedText> text, int x, int y, int color) {
        TextRenderer textRenderer = this.client.textRenderer;
        for (int i = 0; i < text.size(); ++i) {
            Objects.requireNonNull(textRenderer);
            context.drawTextWithShadow(textRenderer, text.get(i), x, y + i * 9, color);
        }
    }

    public boolean shouldRender(int originX, int originY, int mouseX, int mouseY) {
        if (this.display.isHidden() && (this.progress == null || !this.progress.isDone())) {
            return false;
        }
        int i = originX + this.x;
        int j = i + 26;
        int k = originY + this.y;
        int l = k + 26;
        return mouseX >= i && mouseX <= j && mouseY >= k && mouseY <= l;
    }

    public void addToTree() {
        if (this.parent == null && this.advancement.getParent() != null) {
            this.parent = this.getParent(this.advancement);
            if (this.parent != null) {
                this.parent.addChild(this);
            }
        }
    }

    public int getY() {
        return this.y;
    }

    public int getX() {
        return this.x;
    }
}

