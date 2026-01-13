/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.AccessibilityOnboardingButtons
 *  net.minecraft.client.gui.widget.ButtonWidget$PressAction
 *  net.minecraft.client.gui.widget.TextIconButtonWidget
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class AccessibilityOnboardingButtons {
    public static TextIconButtonWidget createLanguageButton(int width, ButtonWidget.PressAction onPress, boolean hideText) {
        return TextIconButtonWidget.builder((Text)Text.translatable((String)"options.language"), (ButtonWidget.PressAction)onPress, (boolean)hideText).width(width).texture(Identifier.ofVanilla((String)"icon/language"), 15, 15).build();
    }

    public static TextIconButtonWidget createAccessibilityButton(int width, ButtonWidget.PressAction onPress, boolean hideText) {
        MutableText text = hideText ? Text.translatable((String)"options.accessibility") : Text.translatable((String)"accessibility.onboarding.accessibility.button");
        return TextIconButtonWidget.builder((Text)text, (ButtonWidget.PressAction)onPress, (boolean)hideText).width(width).texture(Identifier.ofVanilla((String)"icon/accessibility"), 15, 15).build();
    }
}

