/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.Alignment
 *  net.minecraft.client.font.DrawnTextConsumer
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.BookEditScreen
 *  net.minecraft.client.gui.screen.ingame.BookScreen
 *  net.minecraft.client.gui.screen.ingame.BookSigningScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.EditBoxWidget
 *  net.minecraft.client.gui.widget.PageTurnWidget
 *  net.minecraft.client.input.AbstractInput
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.WritableBookContentComponent
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.RawFilteredPair
 *  net.minecraft.text.Text
 *  net.minecraft.util.Hand
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.screen.ingame.BookSigningScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

@Environment(value=EnvType.CLIENT)
public class BookEditScreen
extends Screen {
    public static final int MAX_TEXT_WIDTH = 114;
    public static final int MAX_TEXT_HEIGHT = 126;
    public static final int WIDTH = 192;
    public static final int HEIGHT = 192;
    public static final int field_52805 = 256;
    public static final int field_52806 = 256;
    private static final int field_63897 = 4;
    private static final int field_63898 = 98;
    private static final int field_63899 = 157;
    private static final int field_63900 = 43;
    private static final int field_63901 = 116;
    private static final int field_63902 = 16;
    private static final int field_63903 = 148;
    private static final Text TITLE_TEXT = Text.translatable((String)"book.edit.title");
    private static final Text SIGN_BUTTON_TEXT = Text.translatable((String)"book.signButton");
    private final PlayerEntity player;
    private final ItemStack stack;
    private final BookSigningScreen signingScreen;
    private int currentPage;
    private final List<String> pages = Lists.newArrayList();
    private PageTurnWidget nextPageButton;
    private PageTurnWidget previousPageButton;
    private final Hand hand;
    private Text pageIndicatorText = ScreenTexts.EMPTY;
    private EditBoxWidget editBox;

    public BookEditScreen(PlayerEntity player, ItemStack stack, Hand hand, WritableBookContentComponent writableBookContent) {
        super(TITLE_TEXT);
        this.player = player;
        this.stack = stack;
        this.hand = hand;
        writableBookContent.stream(MinecraftClient.getInstance().shouldFilterText()).forEach(this.pages::add);
        if (this.pages.isEmpty()) {
            this.pages.add("");
        }
        this.signingScreen = new BookSigningScreen(this, player, hand, this.pages);
    }

    private int countPages() {
        return this.pages.size();
    }

    protected void init() {
        int i = this.getLeft();
        int j = this.getTop();
        int k = 8;
        this.editBox = EditBoxWidget.builder().hasOverlay(false).textColor(-16777216).cursorColor(-16777216).hasBackground(false).textShadow(false).x((this.width - 114) / 2 - 8).y(28).build(this.textRenderer, 122, 134, ScreenTexts.EMPTY);
        this.editBox.setMaxLength(1024);
        Objects.requireNonNull(this.textRenderer);
        this.editBox.setMaxLines(126 / 9);
        this.editBox.setChangeListener(page -> this.pages.set(this.currentPage, page));
        this.addDrawableChild((Element)this.editBox);
        this.updatePage();
        this.pageIndicatorText = this.getPageIndicatorText();
        this.previousPageButton = (PageTurnWidget)this.addDrawableChild((Element)new PageTurnWidget(i + 43, j + 157, false, button -> this.openPreviousPage(), true));
        this.nextPageButton = (PageTurnWidget)this.addDrawableChild((Element)new PageTurnWidget(i + 116, j + 157, true, button -> this.openNextPage(), true));
        this.addDrawableChild((Element)ButtonWidget.builder((Text)SIGN_BUTTON_TEXT, button -> this.client.setScreen((Screen)this.signingScreen)).position(this.width / 2 - 98 - 2, this.getDoneButtonY()).width(98).build());
        this.addDrawableChild((Element)ButtonWidget.builder((Text)ScreenTexts.DONE, button -> {
            this.client.setScreen(null);
            this.finalizeBook();
        }).position(this.width / 2 + 2, this.getDoneButtonY()).width(98).build());
        this.updatePreviousPageButtonVisibility();
    }

    private int getLeft() {
        return (this.width - 192) / 2;
    }

    private int getTop() {
        return 2;
    }

    private int getDoneButtonY() {
        return this.getTop() + 192 + 2;
    }

    protected void setInitialFocus() {
        this.setInitialFocus((Element)this.editBox);
    }

    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences((Text[])new Text[]{super.getNarratedTitle(), this.getPageIndicatorText()});
    }

    private Text getPageIndicatorText() {
        return Text.translatable((String)"book.pageIndicator", (Object[])new Object[]{this.currentPage + 1, this.countPages()}).withColor(-16777216).withoutShadow();
    }

    private void openPreviousPage() {
        if (this.currentPage > 0) {
            --this.currentPage;
            this.updatePage();
        }
        this.updatePreviousPageButtonVisibility();
    }

    private void openNextPage() {
        if (this.currentPage < this.countPages() - 1) {
            ++this.currentPage;
        } else {
            this.appendNewPage();
            if (this.currentPage < this.countPages() - 1) {
                ++this.currentPage;
            }
        }
        this.updatePage();
        this.updatePreviousPageButtonVisibility();
    }

    private void updatePage() {
        this.editBox.setText((String)this.pages.get(this.currentPage), true);
        this.pageIndicatorText = this.getPageIndicatorText();
    }

    private void updatePreviousPageButtonVisibility() {
        this.previousPageButton.visible = this.currentPage > 0;
    }

    private void removeEmptyPages() {
        ListIterator listIterator = this.pages.listIterator(this.pages.size());
        while (listIterator.hasPrevious() && ((String)listIterator.previous()).isEmpty()) {
            listIterator.remove();
        }
    }

    private void finalizeBook() {
        this.removeEmptyPages();
        this.writeNbtData();
        int i = this.hand == Hand.MAIN_HAND ? this.player.getInventory().getSelectedSlot() : 40;
        this.client.getNetworkHandler().sendPacket((Packet)new BookUpdateC2SPacket(i, this.pages, Optional.empty()));
    }

    private void writeNbtData() {
        this.stack.set(DataComponentTypes.WRITABLE_BOOK_CONTENT, (Object)new WritableBookContentComponent(this.pages.stream().map(RawFilteredPair::of).toList()));
    }

    private void appendNewPage() {
        if (this.countPages() >= 100) {
            return;
        }
        this.pages.add("");
    }

    public boolean deferSubtitles() {
        return true;
    }

    public boolean keyPressed(KeyInput input) {
        switch (input.key()) {
            case 266: {
                this.previousPageButton.onPress((AbstractInput)input);
                return true;
            }
            case 267: {
                this.nextPageButton.onPress((AbstractInput)input);
                return true;
            }
        }
        return super.keyPressed(input);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.render(context.getTextConsumer());
    }

    private void render(DrawnTextConsumer textConsumer) {
        int i = this.getLeft();
        int j = this.getTop();
        textConsumer.text(Alignment.RIGHT, i + 148, j + 16, this.pageIndicatorText);
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderBackground(context, mouseX, mouseY, deltaTicks);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, BookScreen.BOOK_TEXTURE, this.getLeft(), this.getTop(), 0.0f, 0.0f, 192, 192, 256, 256);
    }
}

