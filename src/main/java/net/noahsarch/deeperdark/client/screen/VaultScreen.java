package net.noahsarch.deeperdark.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.noahsarch.deeperdark.Deeperdark;
import net.noahsarch.deeperdark.menu.VaultMenu;

@Environment(EnvType.CLIENT)
public class VaultScreen extends AbstractContainerScreen<VaultMenu> {

    private static final Identifier VAULT_1 = Identifier.fromNamespaceAndPath(
        Deeperdark.MOD_ID, "textures/gui/container/vault_1.png");
    private static final Identifier VAULT_3 = Identifier.fromNamespaceAndPath(
        Deeperdark.MOD_ID, "textures/gui/container/vault_3.png");
    private static final Identifier VAULT_9 = Identifier.fromNamespaceAndPath(
        Deeperdark.MOD_ID, "textures/gui/container/vault_9.png");

    private static final int COLOR_DARK_GRAY  = 0xFF404040;
    private static final int COLOR_WHITE      = 0xFFFFFFFF;
    private static final int COLOR_LIGHT_GRAY = 0xFFAAAAAA;

    // Wiki-defined stats box X end (absolute panel pixel from leftPos)
    // Small: 24..117, Medium: 12..105, Large: 12..129
    private static final int[] STATS_BOX_X_END = {0, 117, 105, 0, 0, 0, 0, 0, 0, 129};

    private final Identifier texture;
    private final int maxTypes;
    private final int contentsX;
    private final int contentsY;
    private final int statsStartY;
    private final int maxTextWidth; // max pixel width for a stat text line

    public VaultScreen(VaultMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title,
            VaultMenu.getImageWidth(menu.getMaxTypes()),
            VaultMenu.getImageHeight(menu.getMaxTypes()));
        this.maxTypes  = menu.getMaxTypes();
        this.contentsX = switch (maxTypes) {
            case 1  -> 25;
            case 3  -> 14;
            default -> 16;
        };
        this.contentsY = switch (maxTypes) {
            case 1  -> 23;
            case 3  -> 24;
            default -> 26;
        };
        // Large vault hides the "Contents" header — stats start at its Y slot
        this.statsStartY = (maxTypes == 9) ? contentsY : 40;
        int xEnd = STATS_BOX_X_END[maxTypes];
        this.maxTextWidth = xEnd - (contentsX + 12);

        this.titleLabelY = -1000;
        if (maxTypes == 9) {
            this.inventoryLabelY = -1000;
        }
        if (maxTypes == 3) {
            this.inventoryLabelY += 2;
        }
        this.texture = switch (maxTypes) {
            case 1  -> VAULT_1;
            case 3  -> VAULT_3;
            default -> VAULT_9;
        };
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractBackground(graphics, mouseX, mouseY, delta);

        graphics.blit(RenderPipelines.GUI_TEXTURED, texture,
            leftPos, topPos, 0f, 0f, imageWidth, imageHeight, 256, 256);

        // Vault name — dark gray
        graphics.text(font, title, leftPos + 7, topPos + 6, COLOR_DARK_GRAY, false);

        // "Contents" subheader — hidden for large vault (9 types)
        if (maxTypes != 9) {
            graphics.text(font,
                Component.translatable("container.deeperdark.vault_contents"),
                leftPos + contentsX, topPos + contentsY, COLOR_WHITE, false);
        }

        // Stats — light gray, icon then "# x Name", left-aligned to contentsX
        for (int i = 0; i < maxTypes; i++) {
            ItemStack item = menu.getVaultDisplayItem(i);
            if (!item.isEmpty()) {
                int count = menu.getStoredCount(i);
                int yPos = topPos + statsStartY + i * 11;
                if (maxTypes == 9) {
                    yPos = (-3 + topPos) + statsStartY + i * 11;
                }
                String line = truncateWithEllipsis(
                    String.format("%,d x %s", count, item.getHoverName().getString()));
                graphics.text(font, Component.literal(line),
                    leftPos + contentsX + 12, yPos, COLOR_LIGHT_GRAY, false);
            }
        }
    }

    // Rendered after slots so icons sit on top of the stat-panel background
    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractContents(graphics, mouseX, mouseY, delta);
        renderStatItems(graphics);
    }

    private void renderStatItems(GuiGraphicsExtractor graphics) {
        var pose = graphics.pose(); // Matrix3x2fStack

        for (int i = 0; i < maxTypes; i++) {
            ItemStack item = menu.getVaultDisplayItem(i);
            if (item.isEmpty()) continue;
            // 10 px icon centred in the 11 px stat line, left edge at contentsX
            float cx = leftPos + contentsX + 5f;
            float cy = topPos  + statsStartY + i * 11 + 5f;
            if (maxTypes == 9) {
                cy = (-4 + topPos)  + statsStartY + i * 11 + 6f;
            }

            pose.pushMatrix();
            pose.translate(cx, cy);
            pose.scale(0.625f, 0.625f);
            pose.translate(-8f, -8f);
            graphics.item(item.copyWithCount(1), 0, 0);
            pose.popMatrix();
        }
    }

    private String truncateWithEllipsis(String text) {
        if (font.width(text) <= maxTextWidth) return text;
        String ellipsis = "...";
        int available = maxTextWidth - font.width(ellipsis);
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (font.width(sb.toString()) + font.width(String.valueOf(c)) > available) break;
            sb.append(c);
        }
        return sb + ellipsis;
    }
}
