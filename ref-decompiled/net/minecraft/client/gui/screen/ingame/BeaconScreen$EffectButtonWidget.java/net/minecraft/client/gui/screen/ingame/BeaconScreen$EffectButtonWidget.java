/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
class BeaconScreen.EffectButtonWidget
extends BeaconScreen.BaseButtonWidget {
    private final boolean primary;
    protected final int level;
    private RegistryEntry<StatusEffect> effect;
    private Identifier sprite;

    public BeaconScreen.EffectButtonWidget(int x, int y, RegistryEntry<StatusEffect> effect, boolean primary, int level) {
        super(x, y);
        this.primary = primary;
        this.level = level;
        this.init(effect);
    }

    protected void init(RegistryEntry<StatusEffect> effect) {
        this.effect = effect;
        this.sprite = InGameHud.getEffectTexture(effect);
        this.setTooltip(Tooltip.of(this.getEffectName(effect), null));
    }

    protected MutableText getEffectName(RegistryEntry<StatusEffect> effect) {
        return Text.translatable(effect.value().getTranslationKey());
    }

    @Override
    public void onPress(AbstractInput input) {
        if (this.isDisabled()) {
            return;
        }
        if (this.primary) {
            BeaconScreen.this.primaryEffect = this.effect;
        } else {
            BeaconScreen.this.secondaryEffect = this.effect;
        }
        BeaconScreen.this.tickButtons();
    }

    @Override
    protected void renderExtra(DrawContext context) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.sprite, this.getX() + 2, this.getY() + 2, 18, 18);
    }

    @Override
    public void tick(int level) {
        this.active = this.level < level;
        this.setDisabled(this.effect.equals(this.primary ? BeaconScreen.this.primaryEffect : BeaconScreen.this.secondaryEffect));
    }

    @Override
    protected MutableText getNarrationMessage() {
        return this.getEffectName(this.effect);
    }
}
