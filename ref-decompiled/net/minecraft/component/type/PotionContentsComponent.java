package net.minecraft.component.type;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.potion.Potion;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.World;

public record PotionContentsComponent(Optional potion, Optional customColor, List customEffects, Optional customName) implements Consumable, TooltipAppender {
   public static final PotionContentsComponent DEFAULT = new PotionContentsComponent(Optional.empty(), Optional.empty(), List.of(), Optional.empty());
   private static final Text NONE_TEXT;
   public static final int EFFECTLESS_COLOR = -13083194;
   private static final Codec BASE_CODEC;
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;

   public PotionContentsComponent(RegistryEntry potion) {
      this(Optional.of(potion), Optional.empty(), List.of(), Optional.empty());
   }

   public PotionContentsComponent(Optional optional, Optional optional2, List list, Optional optional3) {
      this.potion = optional;
      this.customColor = optional2;
      this.customEffects = list;
      this.customName = optional3;
   }

   public static ItemStack createStack(Item item, RegistryEntry potion) {
      ItemStack itemStack = new ItemStack(item);
      itemStack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(potion));
      return itemStack;
   }

   public boolean matches(RegistryEntry potion) {
      return this.potion.isPresent() && ((RegistryEntry)this.potion.get()).matches(potion) && this.customEffects.isEmpty();
   }

   public Iterable getEffects() {
      if (this.potion.isEmpty()) {
         return this.customEffects;
      } else {
         return (Iterable)(this.customEffects.isEmpty() ? ((Potion)((RegistryEntry)this.potion.get()).value()).getEffects() : Iterables.concat(((Potion)((RegistryEntry)this.potion.get()).value()).getEffects(), this.customEffects));
      }
   }

   public void forEachEffect(Consumer effectConsumer, float durationMultiplier) {
      Iterator var3;
      StatusEffectInstance statusEffectInstance;
      if (this.potion.isPresent()) {
         var3 = ((Potion)((RegistryEntry)this.potion.get()).value()).getEffects().iterator();

         while(var3.hasNext()) {
            statusEffectInstance = (StatusEffectInstance)var3.next();
            effectConsumer.accept(statusEffectInstance.withScaledDuration(durationMultiplier));
         }
      }

      var3 = this.customEffects.iterator();

      while(var3.hasNext()) {
         statusEffectInstance = (StatusEffectInstance)var3.next();
         effectConsumer.accept(statusEffectInstance.withScaledDuration(durationMultiplier));
      }

   }

   public PotionContentsComponent with(RegistryEntry potion) {
      return new PotionContentsComponent(Optional.of(potion), this.customColor, this.customEffects, this.customName);
   }

   public PotionContentsComponent with(StatusEffectInstance customEffect) {
      return new PotionContentsComponent(this.potion, this.customColor, Util.withAppended(this.customEffects, customEffect), this.customName);
   }

   public int getColor() {
      return this.getColor(-13083194);
   }

   public int getColor(int defaultColor) {
      return this.customColor.isPresent() ? (Integer)this.customColor.get() : mixColors(this.getEffects()).orElse(defaultColor);
   }

   public Text getName(String prefix) {
      String string = (String)this.customName.or(() -> {
         return this.potion.map((potionEntry) -> {
            return ((Potion)potionEntry.value()).getBaseName();
         });
      }).orElse("empty");
      return Text.translatable(prefix + string);
   }

   public static OptionalInt mixColors(Iterable effects) {
      int i = 0;
      int j = 0;
      int k = 0;
      int l = 0;
      Iterator var5 = effects.iterator();

      while(var5.hasNext()) {
         StatusEffectInstance statusEffectInstance = (StatusEffectInstance)var5.next();
         if (statusEffectInstance.shouldShowParticles()) {
            int m = ((StatusEffect)statusEffectInstance.getEffectType().value()).getColor();
            int n = statusEffectInstance.getAmplifier() + 1;
            i += n * ColorHelper.getRed(m);
            j += n * ColorHelper.getGreen(m);
            k += n * ColorHelper.getBlue(m);
            l += n;
         }
      }

      if (l == 0) {
         return OptionalInt.empty();
      } else {
         return OptionalInt.of(ColorHelper.getArgb(i / l, j / l, k / l));
      }
   }

   public boolean hasEffects() {
      if (!this.customEffects.isEmpty()) {
         return true;
      } else {
         return this.potion.isPresent() && !((Potion)((RegistryEntry)this.potion.get()).value()).getEffects().isEmpty();
      }
   }

   public List customEffects() {
      return Lists.transform(this.customEffects, StatusEffectInstance::new);
   }

   public void apply(LivingEntity user, float durationMultiplier) {
      World var4 = user.getWorld();
      if (var4 instanceof ServerWorld serverWorld) {
         PlayerEntity var10000;
         if (user instanceof PlayerEntity playerEntity) {
            var10000 = playerEntity;
         } else {
            var10000 = null;
         }

         PlayerEntity playerEntity2 = var10000;
         this.forEachEffect((effect) -> {
            if (((StatusEffect)effect.getEffectType().value()).isInstant()) {
               ((StatusEffect)effect.getEffectType().value()).applyInstantEffect(serverWorld, playerEntity2, playerEntity2, user, effect.getAmplifier(), 1.0);
            } else {
               user.addStatusEffect(effect);
            }

         }, durationMultiplier);
      }
   }

   public static void buildTooltip(Iterable effects, Consumer textConsumer, float durationMultiplier, float tickRate) {
      List list = Lists.newArrayList();
      boolean bl = true;

      Iterator var6;
      RegistryEntry registryEntry;
      MutableText mutableText;
      for(var6 = effects.iterator(); var6.hasNext(); textConsumer.accept(mutableText.formatted(((StatusEffect)registryEntry.value()).getCategory().getFormatting()))) {
         StatusEffectInstance statusEffectInstance = (StatusEffectInstance)var6.next();
         bl = false;
         registryEntry = statusEffectInstance.getEffectType();
         int i = statusEffectInstance.getAmplifier();
         ((StatusEffect)registryEntry.value()).forEachAttributeModifier(i, (attribute, modifier) -> {
            list.add(new Pair(attribute, modifier));
         });
         mutableText = getEffectText(registryEntry, i);
         if (!statusEffectInstance.isDurationBelow(20)) {
            mutableText = Text.translatable("potion.withDuration", mutableText, StatusEffectUtil.getDurationText(statusEffectInstance, durationMultiplier, tickRate));
         }
      }

      if (bl) {
         textConsumer.accept(NONE_TEXT);
      }

      if (!list.isEmpty()) {
         textConsumer.accept(ScreenTexts.EMPTY);
         textConsumer.accept(Text.translatable("potion.whenDrank").formatted(Formatting.DARK_PURPLE));
         var6 = list.iterator();

         while(var6.hasNext()) {
            Pair pair = (Pair)var6.next();
            EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)pair.getSecond();
            double d = entityAttributeModifier.value();
            double e;
            if (entityAttributeModifier.operation() != EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE && entityAttributeModifier.operation() != EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
               e = entityAttributeModifier.value();
            } else {
               e = entityAttributeModifier.value() * 100.0;
            }

            if (d > 0.0) {
               textConsumer.accept(Text.translatable("attribute.modifier.plus." + entityAttributeModifier.operation().getId(), AttributeModifiersComponent.DECIMAL_FORMAT.format(e), Text.translatable(((EntityAttribute)((RegistryEntry)pair.getFirst()).value()).getTranslationKey())).formatted(Formatting.BLUE));
            } else if (d < 0.0) {
               e *= -1.0;
               textConsumer.accept(Text.translatable("attribute.modifier.take." + entityAttributeModifier.operation().getId(), AttributeModifiersComponent.DECIMAL_FORMAT.format(e), Text.translatable(((EntityAttribute)((RegistryEntry)pair.getFirst()).value()).getTranslationKey())).formatted(Formatting.RED));
            }
         }
      }

   }

   public static MutableText getEffectText(RegistryEntry effect, int amplifier) {
      MutableText mutableText = Text.translatable(((StatusEffect)effect.value()).getTranslationKey());
      return amplifier > 0 ? Text.translatable("potion.withAmplifier", mutableText, Text.translatable("potion.potency." + amplifier)) : mutableText;
   }

   public void onConsume(World world, LivingEntity user, ItemStack stack, ConsumableComponent consumable) {
      this.apply(user, (Float)stack.getOrDefault(DataComponentTypes.POTION_DURATION_SCALE, 1.0F));
   }

   public void appendTooltip(Item.TooltipContext context, Consumer textConsumer, TooltipType type, ComponentsAccess components) {
      buildTooltip(this.getEffects(), textConsumer, (Float)components.getOrDefault(DataComponentTypes.POTION_DURATION_SCALE, 1.0F), context.getUpdateTickRate());
   }

   public Optional potion() {
      return this.potion;
   }

   public Optional customColor() {
      return this.customColor;
   }

   public Optional customName() {
      return this.customName;
   }

   static {
      NONE_TEXT = Text.translatable("effect.none").formatted(Formatting.GRAY);
      BASE_CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Potion.CODEC.optionalFieldOf("potion").forGetter(PotionContentsComponent::potion), Codec.INT.optionalFieldOf("custom_color").forGetter(PotionContentsComponent::customColor), StatusEffectInstance.CODEC.listOf().optionalFieldOf("custom_effects", List.of()).forGetter(PotionContentsComponent::customEffects), Codec.STRING.optionalFieldOf("custom_name").forGetter(PotionContentsComponent::customName)).apply(instance, PotionContentsComponent::new);
      });
      CODEC = Codec.withAlternative(BASE_CODEC, Potion.CODEC, PotionContentsComponent::new);
      PACKET_CODEC = PacketCodec.tuple(Potion.PACKET_CODEC.collect(PacketCodecs::optional), PotionContentsComponent::potion, PacketCodecs.INTEGER.collect(PacketCodecs::optional), PotionContentsComponent::customColor, StatusEffectInstance.PACKET_CODEC.collect(PacketCodecs.toList()), PotionContentsComponent::customEffects, PacketCodecs.STRING.collect(PacketCodecs::optional), PotionContentsComponent::customName, PotionContentsComponent::new);
   }
}
