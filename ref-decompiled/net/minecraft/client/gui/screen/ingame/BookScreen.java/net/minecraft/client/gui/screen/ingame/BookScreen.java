/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.ingame;

import java.lang.runtime.SwitchBootstraps;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BookScreen
extends Screen {
    public static final int field_32328 = 16;
    public static final int field_32329 = 36;
    public static final int field_32330 = 30;
    private static final int field_52807 = 256;
    private static final int field_52808 = 256;
    private static final Text TITLE_TEXT = Text.translatable("book.view.title");
    private static final Style STYLE = Style.EMPTY.withoutShadow().withColor(-16777216);
    public static final Contents EMPTY_PROVIDER = new Contents(List.of());
    public static final Identifier BOOK_TEXTURE = Identifier.ofVanilla("textures/gui/book.png");
    protected static final int MAX_TEXT_WIDTH = 114;
    protected static final int MAX_TEXT_HEIGHT = 128;
    protected static final int WIDTH = 192;
    private static final int field_63904 = 148;
    protected static final int HEIGHT = 192;
    private static final int field_63905 = 157;
    private static final int field_63906 = 43;
    private static final int field_63907 = 116;
    private Contents contents;
    private int pageIndex;
    private List<OrderedText> cachedPage = Collections.emptyList();
    private int cachedPageIndex = -1;
    private Text pageIndexText = ScreenTexts.EMPTY;
    private PageTurnWidget nextPageButton;
    private PageTurnWidget previousPageButton;
    private final boolean pageTurnSound;

    public BookScreen(Contents pageProvider) {
        this(pageProvider, true);
    }

    public BookScreen() {
        this(EMPTY_PROVIDER, false);
    }

    private BookScreen(Contents contents, boolean playPageTurnSound) {
        super(TITLE_TEXT);
        this.contents = contents;
        this.pageTurnSound = playPageTurnSound;
    }

    public void setPageProvider(Contents pageProvider) {
        this.contents = pageProvider;
        this.pageIndex = MathHelper.clamp(this.pageIndex, 0, pageProvider.getPageCount());
        this.updatePageButtons();
        this.cachedPageIndex = -1;
    }

    public boolean setPage(int index) {
        int i = MathHelper.clamp(index, 0, this.contents.getPageCount() - 1);
        if (i != this.pageIndex) {
            this.pageIndex = i;
            this.updatePageButtons();
            this.cachedPageIndex = -1;
            return true;
        }
        return false;
    }

    protected boolean jumpToPage(int page) {
        return this.setPage(page);
    }

    @Override
    protected void init() {
        this.addCloseButton();
        this.addPageButtons();
    }

    @Override
    public Text getNarratedTitle() {
        return ScreenTexts.joinLines(super.getNarratedTitle(), this.getPageIndicatorText(), this.contents.getPage(this.pageIndex));
    }

    private Text getPageIndicatorText() {
        return Text.translatable("book.pageIndicator", this.pageIndex + 1, Math.max(this.getPageCount(), 1)).fillStyle(STYLE);
    }

    protected void addCloseButton() {
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).position((this.width - 200) / 2, this.getCloseButtonY()).width(200).build());
    }

    protected void addPageButtons() {
        int i = this.getLeft();
        int j = this.getTop();
        this.nextPageButton = this.addDrawableChild(new PageTurnWidget(i + 116, j + 157, true, button -> this.goToNextPage(), this.pageTurnSound));
        this.previousPageButton = this.addDrawableChild(new PageTurnWidget(i + 43, j + 157, false, button -> this.goToPreviousPage(), this.pageTurnSound));
        this.updatePageButtons();
    }

    private int getPageCount() {
        return this.contents.getPageCount();
    }

    protected void goToPreviousPage() {
        if (this.pageIndex > 0) {
            --this.pageIndex;
        }
        this.updatePageButtons();
    }

    protected void goToNextPage() {
        if (this.pageIndex < this.getPageCount() - 1) {
            ++this.pageIndex;
        }
        this.updatePageButtons();
    }

    private void updatePageButtons() {
        this.nextPageButton.visible = this.pageIndex < this.getPageCount() - 1;
        this.previousPageButton.visible = this.pageIndex > 0;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (super.keyPressed(input)) {
            return true;
        }
        return switch (input.key()) {
            case 266 -> {
                this.previousPageButton.onPress(input);
                yield true;
            }
            case 267 -> {
                this.nextPageButton.onPress(input);
                yield true;
            }
            default -> false;
        };
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.render(context.getTextConsumer(DrawContext.HoverType.TOOLTIP_AND_CURSOR), false);
    }

    private void render(DrawnTextConsumer drawer, boolean click) {
        if (this.cachedPageIndex != this.pageIndex) {
            Text stringVisitable = Texts.withStyle(this.contents.getPage(this.pageIndex), STYLE);
            this.cachedPage = this.textRenderer.wrapLines(stringVisitable, 114);
            this.pageIndexText = this.getPageIndicatorText();
            this.cachedPageIndex = this.pageIndex;
        }
        int i = this.getLeft();
        int j = this.getTop();
        if (!click) {
            drawer.text(Alignment.RIGHT, i + 148, j + 16, this.pageIndexText);
        }
        int k = Math.min(128 / this.textRenderer.fontHeight, this.cachedPage.size());
        for (int l = 0; l < k; ++l) {
            OrderedText orderedText = this.cachedPage.get(l);
            drawer.text(i + 36, j + 30 + l * this.textRenderer.fontHeight, orderedText);
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderBackground(context, mouseX, mouseY, deltaTicks);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, BOOK_TEXTURE, this.getLeft(), this.getTop(), 0.0f, 0.0f, 192, 192, 256, 256);
    }

    private int getLeft() {
        return (this.width - 192) / 2;
    }

    private int getTop() {
        return 2;
    }

    protected int getCloseButtonY() {
        return this.getTop() + 192 + 2;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() == 0) {
            DrawnTextConsumer.ClickHandler clickHandler = new DrawnTextConsumer.ClickHandler(this.textRenderer, (int)click.x(), (int)click.y());
            this.render(clickHandler, true);
            Style style = clickHandler.getStyle();
            if (style != null && this.handleClickEvent(style.getClickEvent())) {
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected boolean handleClickEvent(@Nullable ClickEvent clickEvent) {
        if (clickEvent == null) {
            return false;
        }
        ClientPlayerEntity clientPlayerEntity = Objects.requireNonNull(this.client.player, "Player not available");
        ClickEvent clickEvent2 = clickEvent;
        Objects.requireNonNull(clickEvent2);
        ClickEvent clickEvent3 = clickEvent2;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{ClickEvent.ChangePage.class, ClickEvent.RunCommand.class}, (Object)clickEvent3, n)) {
            case 0: {
                ClickEvent.ChangePage changePage = (ClickEvent.ChangePage)clickEvent3;
                try {
                    int n2;
                    int i = n2 = changePage.page();
                    this.jumpToPage(i - 1);
                    return true;
                }
                catch (Throwable throwable) {
                    throw new MatchException(throwable.toString(), throwable);
                }
            }
            case 1: {
                String string2;
                ClickEvent.RunCommand runCommand = (ClickEvent.RunCommand)clickEvent3;
                {
                    String string;
                    string2 = string = runCommand.command();
                    this.closeScreen();
                }
                BookScreen.handleRunCommand(clientPlayerEntity, string2, null);
                return true;
            }
        }
        BookScreen.handleClickEvent(clickEvent, this.client, this);
        return true;
    }

    protected void closeScreen() {
    }

    @Override
    public boolean deferSubtitles() {
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    public record Contents(List<Text> pages) {
        public int getPageCount() {
            return this.pages.size();
        }

        public Text getPage(int index) {
            if (index >= 0 && index < this.getPageCount()) {
                return this.pages.get(index);
            }
            return ScreenTexts.EMPTY;
        }

        public static @Nullable Contents create(ItemStack stack) {
            boolean bl = MinecraftClient.getInstance().shouldFilterText();
            WrittenBookContentComponent writtenBookContentComponent = stack.get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
            if (writtenBookContentComponent != null) {
                return new Contents(writtenBookContentComponent.getPages(bl));
            }
            WritableBookContentComponent writableBookContentComponent = stack.get(DataComponentTypes.WRITABLE_BOOK_CONTENT);
            if (writableBookContentComponent != null) {
                return new Contents(writableBookContentComponent.stream(bl).map(Text::literal).toList());
            }
            return null;
        }
    }
}
