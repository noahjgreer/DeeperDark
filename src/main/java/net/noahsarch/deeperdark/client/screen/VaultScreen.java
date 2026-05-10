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

    private static final int COLOR_WHITE      = 0xFFFFFFFF;
    private static final int COLOR_LIGHT_GRAY = 0xFFAAAAAA;

    private final Identifier texture;
    private final int maxTypes;

    public VaultScreen(VaultMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title,
            VaultMenu.getImageWidth(menu.getMaxTypes()),
            VaultMenu.getImageHeight(menu.getMaxTypes()));
        this.maxTypes    = menu.getMaxTypes();
        this.titleLabelY = -1000;
        if (maxTypes == 9) {
            this.inventoryLabelY = -1000;
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

        // Vault name — white
        graphics.text(font, title, leftPos + 7, topPos + 6, COLOR_WHITE, false);

        // "Contents" subheader — white; position per wiki
        int contentsX = switch (maxTypes) {
            case 1  -> 25;
            case 3  -> 14;
            default -> 16;
        };
        int contentsY = switch (maxTypes) {
            case 1  -> 23;
            case 3  -> 24;
            default -> 26;
        };
        graphics.text(font,
            Component.translatable("container.deeperdark.vault_contents"),
            leftPos + contentsX, topPos + contentsY, COLOR_WHITE, false);

        // Stats — light gray, left-aligned, "# x Name", shifted right to leave room for icon
        for (int i = 0; i < maxTypes; i++) {
            ItemStack item = menu.getVaultDisplayItem(i);
            if (!item.isEmpty()) {
                int count = menu.getStoredCount(i);
                String line = String.format("%,d x %s", count, item.getHoverName().getString());
                graphics.text(font, Component.literal(line),
                    leftPos + 20, topPos + 40 + i * 11, COLOR_LIGHT_GRAY, false);
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
        // Simulate Y-axis rotation by squishing X with abs(cos(time))
        float angle     = (float)((System.currentTimeMillis() % 4000L) / 4000.0 * Math.PI * 2.0);
        float spinScale = Math.abs((float) Math.cos(angle));

        var pose = graphics.pose(); // Matrix3x2fStack

        for (int i = 0; i < maxTypes; i++) {
            ItemStack item = menu.getVaultDisplayItem(i);
            if (item.isEmpty()) continue;

            // 10 px icon centred in the 11 px stat line
            float cx = leftPos + 7 + 5f;
            float cy = topPos  + 38 + i * 11 + 5f;

            pose.pushMatrix();
            pose.translate(cx, cy);
            pose.scale(spinScale * 0.625f, 0.625f);
            pose.translate(-8f, -8f); // align renderItem origin with icon centre
            graphics.item(item.copyWithCount(1), 0, 0);
            pose.popMatrix();
        }
    }
}
