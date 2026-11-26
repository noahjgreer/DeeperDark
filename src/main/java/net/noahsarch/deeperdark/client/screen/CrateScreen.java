package net.noahsarch.deeperdark.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.noahsarch.deeperdark.Deeperdark;
import net.noahsarch.deeperdark.menu.CrateMenu;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CrateScreen extends AbstractContainerScreen<CrateMenu> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
        Deeperdark.MOD_ID, "textures/gui/container/crate.png");
    private static final Identifier SCROLLER = Identifier.withDefaultNamespace("container/creative_inventory/scroller");
    private static final Identifier SCROLLER_DISABLED = Identifier.withDefaultNamespace("container/creative_inventory/scroller_disabled");
    private static final Identifier BUTTON = Identifier.withDefaultNamespace("widget/button");
    private static final Identifier BUTTON_HIGHLIGHTED = Identifier.withDefaultNamespace("widget/button_highlighted");

    private static final int SCROLL_X = 175, SCROLL_Y = 18, SCROLL_HEIGHT = 108, SCROLLER_WIDTH = 12, SCROLLER_HEIGHT = 15;
    private static final int TOGGLE_X = 174, TOGGLE_Y = 139, TOGGLE_SIZE = 16;
    private static final int TOOLTIP_WIDTH = 170;

    private int row;
    private boolean scrolling;

    public CrateScreen(CrateMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 195, 222);
        this.inventoryLabelY = imageHeight - 94;
    }

    private int maxRow() {
        return CrateMenu.maxScrollRow(menu.getEntryCount());
    }

    private void scrollTo(int newRow) {
        newRow = Mth.clamp(newRow, 0, maxRow());
        if (newRow == row) return;
        row = newRow;
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, CrateMenu.SCROLL_BUTTON + row);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractBackground(graphics, mouseX, mouseY, delta);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0f, 0f, imageWidth, imageHeight, 256, 256);

        // The server clamps its row the same way when entries leave.
        row = Math.min(row, maxRow());
        int max = maxRow();
        int scrollerY = max == 0 ? 0 : (SCROLL_HEIGHT - SCROLLER_HEIGHT) * row / max;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, max == 0 ? SCROLLER_DISABLED : SCROLLER,
            leftPos + SCROLL_X, topPos + SCROLL_Y + scrollerY, SCROLLER_WIDTH, SCROLLER_HEIGHT);

        boolean hovered = isHovering(TOGGLE_X, TOGGLE_Y, TOGGLE_SIZE, TOGGLE_SIZE, mouseX, mouseY);
        int x = leftPos + TOGGLE_X, y = topPos + TOGGLE_Y;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, hovered ? BUTTON_HIGHLIGHTED : BUTTON, x, y, TOGGLE_SIZE, TOGGLE_SIZE);
        var pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(x + 2, y + 2);
        pose.scale(0.75f, 0.75f);
        graphics.item(new ItemStack(Items.GRINDSTONE), 0, 0);
        pose.popMatrix();
        if (!menu.isAutoMerge()) graphics.fill(x + 1, y + 1, x + TOGGLE_SIZE - 1, y + TOGGLE_SIZE - 1, 0xA0303030);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractContents(graphics, mouseX, mouseY, delta);
        if (isHovering(TOGGLE_X, TOGGLE_Y, TOGGLE_SIZE, TOGGLE_SIZE, mouseX, mouseY)) {
            List<FormattedCharSequence> lines = new ArrayList<>();
            lines.add(Component.translatable(menu.isAutoMerge()
                ? "container.deeperdark.crate.auto_merge.on" : "container.deeperdark.crate.auto_merge.off").getVisualOrderText());
            lines.addAll(font.split(Component.translatable("container.deeperdark.crate.auto_merge.desc")
                .withStyle(ChatFormatting.GRAY), TOOLTIP_WIDTH));
            graphics.setTooltipForNextFrame(lines, mouseX, mouseY);
        }
    }

    /** Crate slots hold whole entries, so their counts can run past what vanilla's label fits. */
    @Override
    protected void extractSlot(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY) {
        if (slot.index >= CrateMenu.VISIBLE_SLOTS || !slot.hasItem()) {
            super.extractSlot(graphics, slot, mouseX, mouseY);
            return;
        }
        ItemStack stack = slot.getItem();
        int count = stack.getCount();
        graphics.item(stack, slot.x, slot.y, slot.x + slot.y * imageWidth);
        graphics.itemDecorations(font, stack, slot.x, slot.y,
            count < 1000 ? null : count < 1_000_000 ? count / 1000 + "k" : count / 1_000_000 + "M");
    }

    @Override
    protected List<Component> getTooltipFromContainerItem(ItemStack stack) {
        List<Component> lines = super.getTooltipFromContainerItem(stack);
        if (hoveredSlot == null || hoveredSlot.index >= CrateMenu.VISIBLE_SLOTS || stack.getCount() < 2) return lines;
        lines = new ArrayList<>(lines);
        lines.add(Component.translatable("container.deeperdark.crate.stored", String.format("%,d", stack.getCount()))
            .withStyle(ChatFormatting.GRAY));
        return lines;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (super.mouseScrolled(x, y, scrollX, scrollY)) return true;
        if (maxRow() == 0) return false;
        scrollTo(row - (int) Math.signum(scrollY));
        return true;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0 && isHovering(SCROLL_X, SCROLL_Y, SCROLLER_WIDTH, SCROLL_HEIGHT, event.x(), event.y())) {
            scrolling = true;
            dragScroller(event.y());
            return true;
        }
        if (event.button() == 0 && isHovering(TOGGLE_X, TOGGLE_Y, TOGGLE_SIZE, TOGGLE_SIZE, event.x(), event.y())) {
            AbstractWidget.playButtonClickSound(minecraft.getSoundManager());
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, CrateMenu.TOGGLE_AUTO_MERGE_BUTTON);
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (!scrolling) return super.mouseDragged(event, dx, dy);
        dragScroller(event.y());
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0) scrolling = false;
        return super.mouseReleased(event);
    }

    private void dragScroller(double mouseY) {
        float position = (float) (mouseY - topPos - SCROLL_Y - SCROLLER_HEIGHT / 2.0) / (SCROLL_HEIGHT - SCROLLER_HEIGHT);
        scrollTo(Math.round(Mth.clamp(position, 0f, 1f) * maxRow()));
    }
}
