/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Ordering
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.hud.InGameHud
 *  net.minecraft.client.gui.screen.ingame.HandledScreen
 *  net.minecraft.client.gui.screen.ingame.StatusEffectsDisplay
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.entity.effect.StatusEffect
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.effect.StatusEffectUtil
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.OrderedText
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Ordering;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class StatusEffectsDisplay {
    private static final Identifier BACKGROUND_TEXTURE = Identifier.ofVanilla((String)"container/inventory/effect_background");
    private static final Identifier AMBIENT_BACKGROUND_TEXTURE = Identifier.ofVanilla((String)"container/inventory/effect_background_ambient");
    private static final int field_63534 = 18;
    public static final int field_63530 = 7;
    private static final int field_63535 = 32;
    public static final int field_63531 = 32;
    private final HandledScreen<?> parent;
    private final MinecraftClient client;

    public StatusEffectsDisplay(HandledScreen<?> parent) {
        this.parent = parent;
        this.client = MinecraftClient.getInstance();
    }

    public boolean shouldHideStatusEffectHud() {
        int i = this.parent.x + this.parent.backgroundWidth + 2;
        int j = this.parent.width - i;
        return j >= 32;
    }

    public void render(DrawContext context, int mouseX, int mouseY) {
        int i = this.parent.x + this.parent.backgroundWidth + 2;
        int j = this.parent.width - i;
        Collection collection = this.client.player.getStatusEffects();
        if (collection.isEmpty() || j < 32) {
            return;
        }
        int k = j >= 120 ? j - 7 : 32;
        int l = 33;
        if (collection.size() > 5) {
            l = 132 / (collection.size() - 1);
        }
        this.drawStatusEffects(context, collection, i, l, mouseX, mouseY, k);
    }

    private void drawStatusEffects(DrawContext context, Collection<StatusEffectInstance> effects, int x, int height, int mouseX, int mouseY, int width) {
        List iterable = Ordering.natural().sortedCopy(effects);
        int i = this.parent.y;
        TextRenderer textRenderer = this.parent.getTextRenderer();
        for (StatusEffectInstance statusEffectInstance : iterable) {
            boolean bl = statusEffectInstance.isAmbient();
            Text text = this.getStatusEffectDescription(statusEffectInstance);
            Text text2 = StatusEffectUtil.getDurationText((StatusEffectInstance)statusEffectInstance, (float)1.0f, (float)this.client.world.getTickManager().getTickRate());
            int j = this.drawStatusEffectBackgrounds(context, textRenderer, text, text2, x, i, bl, width);
            this.drawTexts(context, text, text2, textRenderer, x, i, j, height, mouseX, mouseY);
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.getEffectTexture((RegistryEntry)statusEffectInstance.getEffectType()), x + 7, i + 7, 18, 18);
            i += height;
        }
    }

    private int drawStatusEffectBackgrounds(DrawContext context, TextRenderer textRenderer, Text description, Text duration, int x, int y, boolean ambient, int width) {
        int i = 32 + textRenderer.getWidth((StringVisitable)description) + 7;
        int j = 32 + textRenderer.getWidth((StringVisitable)duration) + 7;
        int k = Math.min(width, Math.max(i, j));
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ambient ? AMBIENT_BACKGROUND_TEXTURE : BACKGROUND_TEXTURE, x, y, k, 32);
        return k;
    }

    private void drawTexts(DrawContext context, Text description, Text duration, TextRenderer textRenderer, int x, int y, int width, int height, int mouseX, int mouseY) {
        boolean bl2;
        int i = x + 32;
        int j = y + 7;
        int k = width - 32 - 7;
        if (k > 0) {
            boolean bl = textRenderer.getWidth((StringVisitable)description) > k;
            OrderedText orderedText = bl ? TextWidget.trim((Text)description, (TextRenderer)textRenderer, (int)k) : description.asOrderedText();
            context.drawTextWithShadow(textRenderer, orderedText, i, j, -1);
            Objects.requireNonNull(textRenderer);
            context.drawTextWithShadow(textRenderer, duration, i, j + 9, -8355712);
            bl2 = bl;
        } else {
            bl2 = true;
        }
        if (bl2 && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            context.drawTooltip(this.parent.getTextRenderer(), List.of(description, duration), Optional.empty(), mouseX, mouseY);
        }
    }

    private Text getStatusEffectDescription(StatusEffectInstance statusEffect) {
        MutableText mutableText = ((StatusEffect)statusEffect.getEffectType().value()).getName().copy();
        if (statusEffect.getAmplifier() >= 1 && statusEffect.getAmplifier() <= 9) {
            mutableText.append(ScreenTexts.SPACE).append((Text)Text.translatable((String)("enchantment.level." + (statusEffect.getAmplifier() + 1))));
        }
        return mutableText;
    }
}

