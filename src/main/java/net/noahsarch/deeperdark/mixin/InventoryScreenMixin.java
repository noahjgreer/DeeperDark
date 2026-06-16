package net.noahsarch.deeperdark.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.noahsarch.deeperdark.duck.CraftingPanelHolder;
import net.noahsarch.deeperdark.payload.SyncCraftingPanelPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractContainerScreen<InventoryMenu> {

    private static final WidgetSprites CRAFTING_TRINKET_BUTTON_SPRITES = new WidgetSprites(
        Identifier.fromNamespaceAndPath("deeperdark", "crafting_trinket/button"),
        Identifier.fromNamespaceAndPath("deeperdark", "crafting_trinket/button_highlighted")
    );

    private static final Identifier CRAFTING_TRINKET_PANEL =
        Identifier.fromNamespaceAndPath("deeperdark", "textures/gui/crafting_trinket.png");

    // Panel dimensions as reported by the user
    private static final int PANEL_W = 68;
    private static final int PANEL_H = 130;

    @Unique private ImageButton deeperdark$craftingButton;

    protected InventoryScreenMixin(InventoryMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    // ── Button setup & visibility ─────────────────────────────────────────────

    @Inject(method = "init", at = @At("TAIL"))
    private void deeperdark$addCraftingTrinketButton(CallbackInfo ci) {
        if (this.minecraft == null || this.minecraft.player == null) return;

        // Place button 22 px to the right of the recipe book button (leftPos+104).
        int x = this.leftPos + 104 + 22;
        int y = this.height / 2 - 22;

        this.deeperdark$craftingButton = this.addRenderableWidget(
            new ImageButton(x, y, 20, 18, CRAFTING_TRINKET_BUTTON_SPRITES, btn -> {
                if (this.menu instanceof CraftingPanelHolder holder) {
                    boolean nowOpen = !holder.deeperdark$isPanelOpen();
                    holder.deeperdark$setPanelOpen(nowOpen);
                    ClientPlayNetworking.send(new SyncCraftingPanelPayload(nowOpen));
                }
            })
        );
        this.deeperdark$craftingButton.visible =
            CraftingPanelHolder.hasCraftingTrinket(this.minecraft.player);
    }

    /**
     * When the recipe book panel opens/closes leftPos is updated and the recipe book button
     * repositions itself. Mirror that shift so our button stays adjacent to it.
     */
    @Inject(method = "onRecipeBookButtonClick", at = @At("TAIL"))
    private void deeperdark$repositionOnRecipeBookToggle(CallbackInfo ci) {
        if (this.deeperdark$craftingButton != null) {
            this.deeperdark$craftingButton.setX(this.leftPos + 104 + 22);
        }
    }

    @Inject(method = "containerTick", at = @At("TAIL"))
    private void deeperdark$updateCraftingButtonVisibility(CallbackInfo ci) {
        if (this.deeperdark$craftingButton == null) return;
        if (this.minecraft == null || this.minecraft.player == null) return;
        boolean hasTrinket = CraftingPanelHolder.hasCraftingTrinket(this.minecraft.player);
        this.deeperdark$craftingButton.visible = hasTrinket;
        // If the trinket is removed while the panel is open, close the panel
        if (!hasTrinket && this.menu instanceof CraftingPanelHolder holder
                && holder.deeperdark$isPanelOpen()) {
            holder.deeperdark$setPanelOpen(false);
            ClientPlayNetworking.send(new SyncCraftingPanelPayload(false));
        }
    }

    // ── Panel rendering ───────────────────────────────────────────────────────

    @Inject(method = "extractBackground", at = @At("TAIL"))
    private void deeperdark$renderCraftingPanel(GuiGraphicsExtractor graphics,
                                                 int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (!(this.menu instanceof CraftingPanelHolder holder) || !holder.deeperdark$isPanelOpen()) return;

        int xo = this.leftPos + this.imageWidth;
        int yo = this.topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, CRAFTING_TRINKET_PANEL,
            xo, yo, 0.0F, 0.0F, PANEL_W, PANEL_H, 256, 256);
    }

    // ── Status effects shift ──────────────────────────────────────────────────
    // EffectsInInventory uses (screen.leftPos + screen.imageWidth + 2) as its x-origin.
    // The crafting panel occupies PANEL_W px starting at that same x, so we redirect the
    // two EffectsInInventory calls and temporarily shift leftPos right by PANEL_W so the
    // effects land beyond the panel. leftPos is non-final so no accessor tricks are needed.

    @Unique
    private boolean deeperdark$panelOpen() {
        return (this.menu instanceof CraftingPanelHolder h) && h.deeperdark$isPanelOpen();
    }

    @Redirect(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/EffectsInInventory;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V"))
    private void deeperdark$redirectEffectsExtract(EffectsInInventory effects,
                                                    GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        boolean open = deeperdark$panelOpen();
        if (open) this.leftPos += PANEL_W;
        effects.extractRenderState(graphics, mouseX, mouseY);
        if (open) this.leftPos -= PANEL_W;
    }

    @Redirect(method = "showsActiveEffects()Z",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/EffectsInInventory;canSeeEffects()Z"))
    private boolean deeperdark$redirectCanSeeEffects(EffectsInInventory effects) {
        boolean open = deeperdark$panelOpen();
        if (open) this.leftPos += PANEL_W;
        boolean result = effects.canSeeEffects();
        if (open) this.leftPos -= PANEL_W;
        return result;
    }

}
