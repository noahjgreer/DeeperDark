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
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
class BeaconScreen.LevelTwoEffectButtonWidget
extends BeaconScreen.EffectButtonWidget {
    public BeaconScreen.LevelTwoEffectButtonWidget(int x, int y, RegistryEntry<StatusEffect> effect) {
        super(BeaconScreen.this, x, y, effect, false, 3);
    }

    @Override
    protected MutableText getEffectName(RegistryEntry<StatusEffect> effect) {
        return Text.translatable(effect.value().getTranslationKey()).append(" II");
    }

    @Override
    public void tick(int level) {
        if (BeaconScreen.this.primaryEffect != null) {
            this.visible = true;
            this.init(BeaconScreen.this.primaryEffect);
            super.tick(level);
        } else {
            this.visible = false;
        }
    }
}
