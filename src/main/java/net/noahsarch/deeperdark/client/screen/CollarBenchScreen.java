package net.noahsarch.deeperdark.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.noahsarch.deeperdark.item.CollarTier;
import net.noahsarch.deeperdark.menu.CollarBenchMenu;

@Environment(EnvType.CLIENT)
public class CollarBenchScreen extends AbstractContainerScreen<CollarBenchMenu> {

    private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(
        "deeperdark", "textures/gui/container/collarbench.png");

    // Overlay textures per tier (kept in gui/container alongside the background for direct blit access)
    private static final Identifier[] TIER_OVERLAYS = new Identifier[CollarTier.values().length];
    static {
        for (CollarTier tier : CollarTier.values()) {
            TIER_OVERLAYS[tier.ordinal()] = Identifier.fromNamespaceAndPath(
                "deeperdark", "textures/gui/container/collarbench/" + tier.overlaySpriteName() + ".png");
        }
    }

    public CollarBenchScreen(CollarBenchMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        // Push labels off-screen until the GUI texture is sized to fit them
        this.titleLabelY      = -1000;
        this.inventoryLabelY  = -1000;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractBackground(graphics, mouseX, mouseY, delta);

        // Draw main background
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND,
            this.leftPos, this.topPos, 0f, 0f,
            this.imageWidth, this.imageHeight, 256, 256);

        // Draw tier overlay if a collar is present
        int tier = this.menu.getTier();
        if (tier >= 0 && tier < TIER_OVERLAYS.length) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, TIER_OVERLAYS[tier],
                this.leftPos, this.topPos, 0f, 0f,
                this.imageWidth, this.imageHeight, 256, 256);
        }

        // Draw fuel bars
        if (tier >= 0) {
            drawFuelBars(graphics);
        }

        // Draw XP cost text (GuiGraphicsExtractor inherits drawString from its parent)
        int cost = this.menu.getCost();
        if (cost > 0) {
            boolean canAfford = this.minecraft != null && this.minecraft.player != null
                && (this.minecraft.player.hasInfiniteMaterials() || this.minecraft.player.experienceLevel >= cost);
            Component costText = Component.translatable("container.deeperdark.collarbench.cost", cost)
                .withStyle(canAfford ? ChatFormatting.GREEN : ChatFormatting.RED);
            graphics.text(this.font, costText, this.leftPos + 138, this.topPos + 55,
                canAfford ? 0xFF80FF20 : 0xFFFF5555, true);
        }
    }

    private void drawFuelBars(GuiGraphicsExtractor graphics) {
        int maxFire  = this.menu.getMaxFire();
        int maxWater = this.menu.getMaxWater();
        int curFire  = this.menu.getCurFire();
        int curWater = this.menu.getCurWater();
        int addFire  = this.menu.getAddFire();
        int addWater = this.menu.getAddWater();

        // Fire bar at (140, 65), Water bar at (140, 72)
        // Each bar is 3px tall, up to 16px wide
        if (maxFire > 0) {
            int fireBarWidth     = (int) Math.round(23.0 * curFire / maxFire);
            int newFireBarWidth  = (int) Math.round(23.0 * Math.min(curFire + addFire, maxFire) / maxFire);
            // new_length indicator (behind main bar)
            if (addFire > 0) {
                graphics.fill(this.leftPos + 140, this.topPos + 65,
                    this.leftPos + 140 + newFireBarWidth, this.topPos + 68, 0xFFFFAA00);
            }
            // Current fire bar
            graphics.fill(this.leftPos + 140, this.topPos + 65,
                this.leftPos + 140 + fireBarWidth, this.topPos + 68, 0xFFFF4400);
        }

        if (maxWater > 0) {
            int waterBarWidth    = (int) Math.round(23.0 * curWater / maxWater);
            int newWaterBarWidth = (int) Math.round(23.0 * Math.min(curWater + addWater, maxWater) / maxWater);
            // new_length indicator (behind main bar)
            if (addWater > 0) {
                graphics.fill(this.leftPos + 140, this.topPos + 72,
                    this.leftPos + 140 + newWaterBarWidth, this.topPos + 75, 0xFF00AAFF);
            }
            // Current water bar
            graphics.fill(this.leftPos + 140, this.topPos + 72,
                this.leftPos + 140 + waterBarWidth, this.topPos + 75, 0xFF0044FF);
        }
    }

}

