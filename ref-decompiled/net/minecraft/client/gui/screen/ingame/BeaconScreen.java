/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.entity.BeaconBlockEntity
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.ingame.BeaconScreen
 *  net.minecraft.client.gui.screen.ingame.BeaconScreen$BeaconButtonWidget
 *  net.minecraft.client.gui.screen.ingame.BeaconScreen$CancelButtonWidget
 *  net.minecraft.client.gui.screen.ingame.BeaconScreen$DoneButtonWidget
 *  net.minecraft.client.gui.screen.ingame.BeaconScreen$EffectButtonWidget
 *  net.minecraft.client.gui.screen.ingame.BeaconScreen$LevelTwoEffectButtonWidget
 *  net.minecraft.client.gui.screen.ingame.HandledScreen
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.entity.effect.StatusEffect
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.screen.BeaconScreenHandler
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.screen.ScreenHandlerListener
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BeaconScreen
extends HandledScreen<BeaconScreenHandler> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/gui/container/beacon.png");
    static final Identifier BUTTON_DISABLED_TEXTURE = Identifier.ofVanilla((String)"container/beacon/button_disabled");
    static final Identifier BUTTON_SELECTED_TEXTURE = Identifier.ofVanilla((String)"container/beacon/button_selected");
    static final Identifier BUTTON_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla((String)"container/beacon/button_highlighted");
    static final Identifier BUTTON_TEXTURE = Identifier.ofVanilla((String)"container/beacon/button");
    static final Identifier CONFIRM_TEXTURE = Identifier.ofVanilla((String)"container/beacon/confirm");
    static final Identifier CANCEL_TEXTURE = Identifier.ofVanilla((String)"container/beacon/cancel");
    private static final Text PRIMARY_POWER_TEXT = Text.translatable((String)"block.minecraft.beacon.primary");
    private static final Text SECONDARY_POWER_TEXT = Text.translatable((String)"block.minecraft.beacon.secondary");
    private final List<BeaconButtonWidget> buttons = Lists.newArrayList();
    @Nullable RegistryEntry<StatusEffect> primaryEffect;
    @Nullable RegistryEntry<StatusEffect> secondaryEffect;

    public BeaconScreen(BeaconScreenHandler handler, PlayerInventory inventory, Text title) {
        super((ScreenHandler)handler, inventory, title);
        this.backgroundWidth = 230;
        this.backgroundHeight = 219;
        handler.addListener((ScreenHandlerListener)new /* Unavailable Anonymous Inner Class!! */);
    }

    private <T extends ClickableWidget> void addButton(T button) {
        this.addDrawableChild(button);
        this.buttons.add((BeaconButtonWidget)button);
    }

    protected void init() {
        EffectButtonWidget effectButtonWidget;
        RegistryEntry registryEntry;
        int l;
        int k;
        int j;
        int i;
        super.init();
        this.buttons.clear();
        for (i = 0; i <= 2; ++i) {
            j = ((List)BeaconBlockEntity.EFFECTS_BY_LEVEL.get(i)).size();
            k = j * 22 + (j - 1) * 2;
            for (l = 0; l < j; ++l) {
                registryEntry = (RegistryEntry)((List)BeaconBlockEntity.EFFECTS_BY_LEVEL.get(i)).get(l);
                effectButtonWidget = new EffectButtonWidget(this, this.x + 76 + l * 24 - k / 2, this.y + 22 + i * 25, registryEntry, true, i);
                effectButtonWidget.active = false;
                this.addButton((ClickableWidget)effectButtonWidget);
            }
        }
        i = 3;
        j = ((List)BeaconBlockEntity.EFFECTS_BY_LEVEL.get(3)).size() + 1;
        k = j * 22 + (j - 1) * 2;
        for (l = 0; l < j - 1; ++l) {
            registryEntry = (RegistryEntry)((List)BeaconBlockEntity.EFFECTS_BY_LEVEL.get(3)).get(l);
            effectButtonWidget = new EffectButtonWidget(this, this.x + 167 + l * 24 - k / 2, this.y + 47, registryEntry, false, 3);
            effectButtonWidget.active = false;
            this.addButton((ClickableWidget)effectButtonWidget);
        }
        RegistryEntry registryEntry2 = (RegistryEntry)((List)BeaconBlockEntity.EFFECTS_BY_LEVEL.get(0)).get(0);
        LevelTwoEffectButtonWidget effectButtonWidget2 = new LevelTwoEffectButtonWidget(this, this.x + 167 + (j - 1) * 24 - k / 2, this.y + 47, registryEntry2);
        effectButtonWidget2.visible = false;
        this.addButton((ClickableWidget)effectButtonWidget2);
        this.addButton((ClickableWidget)new DoneButtonWidget(this, this.x + 164, this.y + 107));
        this.addButton((ClickableWidget)new CancelButtonWidget(this, this.x + 190, this.y + 107));
    }

    public void handledScreenTick() {
        super.handledScreenTick();
        this.tickButtons();
    }

    void tickButtons() {
        int i = ((BeaconScreenHandler)this.handler).getProperties();
        this.buttons.forEach(button -> button.tick(i));
    }

    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawCenteredTextWithShadow(this.textRenderer, PRIMARY_POWER_TEXT, 62, 10, -2039584);
        context.drawCenteredTextWithShadow(this.textRenderer, SECONDARY_POWER_TEXT, 169, 10, -2039584);
    }

    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0f, 0.0f, this.backgroundWidth, this.backgroundHeight, 256, 256);
        context.drawItem(new ItemStack((ItemConvertible)Items.NETHERITE_INGOT), i + 20, j + 109);
        context.drawItem(new ItemStack((ItemConvertible)Items.EMERALD), i + 41, j + 109);
        context.drawItem(new ItemStack((ItemConvertible)Items.DIAMOND), i + 41 + 22, j + 109);
        context.drawItem(new ItemStack((ItemConvertible)Items.GOLD_INGOT), i + 42 + 44, j + 109);
        context.drawItem(new ItemStack((ItemConvertible)Items.IRON_INGOT), i + 42 + 66, j + 109);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    static /* synthetic */ MinecraftClient method_47418(BeaconScreen beaconScreen) {
        return beaconScreen.client;
    }

    static /* synthetic */ MinecraftClient method_2394(BeaconScreen beaconScreen) {
        return beaconScreen.client;
    }

    static /* synthetic */ MinecraftClient method_2393(BeaconScreen beaconScreen) {
        return beaconScreen.client;
    }
}

