package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Ordering;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class StatusEffectsDisplay {
   private static final Identifier EFFECT_BACKGROUND_LARGE_TEXTURE = Identifier.ofVanilla("container/inventory/effect_background_large");
   private static final Identifier EFFECT_BACKGROUND_SMALL_TEXTURE = Identifier.ofVanilla("container/inventory/effect_background_small");
   private final HandledScreen parent;
   private final MinecraftClient client;
   @Nullable
   private StatusEffectInstance hoveredStatusEffect;

   public StatusEffectsDisplay(HandledScreen parent) {
      this.parent = parent;
      this.client = MinecraftClient.getInstance();
   }

   public boolean shouldHideStatusEffectHud() {
      int i = this.parent.x + this.parent.backgroundWidth + 2;
      int j = this.parent.width - i;
      return j >= 32;
   }

   public void drawStatusEffects(DrawContext context, int mouseX, int mouseY) {
      this.hoveredStatusEffect = null;
      int i = this.parent.x + this.parent.backgroundWidth + 2;
      int j = this.parent.width - i;
      Collection collection = this.client.player.getStatusEffects();
      if (!collection.isEmpty() && j >= 32) {
         boolean bl = j >= 120;
         int k = 33;
         if (collection.size() > 5) {
            k = 132 / (collection.size() - 1);
         }

         Iterable iterable = Ordering.natural().sortedCopy(collection);
         this.drawStatusEffectBackgrounds(context, i, k, iterable, bl);
         this.drawStatusEffectSprites(context, i, k, iterable, bl);
         if (bl) {
            this.drawStatusEffectDescriptions(context, i, k, iterable);
         } else if (mouseX >= i && mouseX <= i + 33) {
            int l = this.parent.y;

            for(Iterator var11 = iterable.iterator(); var11.hasNext(); l += k) {
               StatusEffectInstance statusEffectInstance = (StatusEffectInstance)var11.next();
               if (mouseY >= l && mouseY <= l + k) {
                  this.hoveredStatusEffect = statusEffectInstance;
               }
            }
         }

      }
   }

   public void drawStatusEffectTooltip(DrawContext context, int mouseX, int mouseY) {
      if (this.hoveredStatusEffect != null) {
         List list = List.of(this.getStatusEffectDescription(this.hoveredStatusEffect), StatusEffectUtil.getDurationText(this.hoveredStatusEffect, 1.0F, this.client.world.getTickManager().getTickRate()));
         context.drawTooltip(this.parent.getTextRenderer(), list, Optional.empty(), mouseX, mouseY);
      }

   }

   private void drawStatusEffectBackgrounds(DrawContext context, int x, int height, Iterable statusEffects, boolean wide) {
      int i = this.parent.y;

      for(Iterator var7 = statusEffects.iterator(); var7.hasNext(); i += height) {
         StatusEffectInstance statusEffectInstance = (StatusEffectInstance)var7.next();
         if (wide) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, EFFECT_BACKGROUND_LARGE_TEXTURE, x, i, 120, 32);
         } else {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, EFFECT_BACKGROUND_SMALL_TEXTURE, x, i, 32, 32);
         }
      }

   }

   private void drawStatusEffectSprites(DrawContext context, int x, int height, Iterable statusEffects, boolean wide) {
      int i = this.parent.y;

      for(Iterator var7 = statusEffects.iterator(); var7.hasNext(); i += height) {
         StatusEffectInstance statusEffectInstance = (StatusEffectInstance)var7.next();
         RegistryEntry registryEntry = statusEffectInstance.getEffectType();
         Identifier identifier = InGameHud.getEffectTexture(registryEntry);
         context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, x + (wide ? 6 : 7), i + 7, 18, 18);
      }

   }

   private void drawStatusEffectDescriptions(DrawContext context, int x, int height, Iterable statusEffects) {
      int i = this.parent.y;

      for(Iterator var6 = statusEffects.iterator(); var6.hasNext(); i += height) {
         StatusEffectInstance statusEffectInstance = (StatusEffectInstance)var6.next();
         Text text = this.getStatusEffectDescription(statusEffectInstance);
         context.drawTextWithShadow(this.parent.getTextRenderer(), (Text)text, x + 10 + 18, i + 6, -1);
         Text text2 = StatusEffectUtil.getDurationText(statusEffectInstance, 1.0F, this.client.world.getTickManager().getTickRate());
         context.drawTextWithShadow(this.parent.getTextRenderer(), text2, x + 10 + 18, i + 6 + 10, -8421505);
      }

   }

   private Text getStatusEffectDescription(StatusEffectInstance statusEffect) {
      MutableText mutableText = ((StatusEffect)statusEffect.getEffectType().value()).getName().copy();
      if (statusEffect.getAmplifier() >= 1 && statusEffect.getAmplifier() <= 9) {
         MutableText var10000 = mutableText.append(ScreenTexts.SPACE);
         int var10001 = statusEffect.getAmplifier();
         var10000.append((Text)Text.translatable("enchantment.level." + (var10001 + 1)));
      }

      return mutableText;
   }
}
