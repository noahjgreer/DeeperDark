/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
class RealmsSlotOptionsScreen.SettingsSlider
extends SliderWidget {
    private final double min;
    private final double max;

    public RealmsSlotOptionsScreen.SettingsSlider(int x, int y, int width, int value, float min, float max) {
        super(x, y, width, 20, ScreenTexts.EMPTY, 0.0);
        this.min = min;
        this.max = max;
        this.value = (MathHelper.clamp((float)value, min, max) - min) / (max - min);
        this.updateMessage();
    }

    @Override
    public void applyValue() {
        if (!RealmsSlotOptionsScreen.this.spawnProtectionButton.active) {
            return;
        }
        RealmsSlotOptionsScreen.this.spawnProtection = (int)MathHelper.lerp(MathHelper.clamp(this.value, 0.0, 1.0), this.min, this.max);
    }

    @Override
    protected void updateMessage() {
        this.setMessage(ScreenTexts.composeGenericOptionText(SPAWN_PROTECTION, RealmsSlotOptionsScreen.this.spawnProtection == 0 ? ScreenTexts.OFF : Text.literal(String.valueOf(RealmsSlotOptionsScreen.this.spawnProtection))));
    }
}
