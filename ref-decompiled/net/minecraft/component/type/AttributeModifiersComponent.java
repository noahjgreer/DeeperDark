package net.minecraft.component.type;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.function.ValueLists;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.Nullable;

public record AttributeModifiersComponent(List modifiers) {
   public static final AttributeModifiersComponent DEFAULT = new AttributeModifiersComponent(List.of());
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;
   public static final DecimalFormat DECIMAL_FORMAT;

   public AttributeModifiersComponent(List list) {
      this.modifiers = list;
   }

   public static Builder builder() {
      return new Builder();
   }

   public AttributeModifiersComponent with(RegistryEntry attribute, EntityAttributeModifier modifier, AttributeModifierSlot slot) {
      ImmutableList.Builder builder = ImmutableList.builderWithExpectedSize(this.modifiers.size() + 1);
      Iterator var5 = this.modifiers.iterator();

      while(var5.hasNext()) {
         Entry entry = (Entry)var5.next();
         if (!entry.matches(attribute, modifier.id())) {
            builder.add(entry);
         }
      }

      builder.add(new Entry(attribute, modifier, slot));
      return new AttributeModifiersComponent(builder.build());
   }

   public void applyModifiers(AttributeModifierSlot slot, TriConsumer attributeConsumer) {
      Iterator var3 = this.modifiers.iterator();

      while(var3.hasNext()) {
         Entry entry = (Entry)var3.next();
         if (entry.slot.equals(slot)) {
            attributeConsumer.accept(entry.attribute, entry.modifier, entry.display);
         }
      }

   }

   public void applyModifiers(AttributeModifierSlot slot, BiConsumer attributeConsumer) {
      Iterator var3 = this.modifiers.iterator();

      while(var3.hasNext()) {
         Entry entry = (Entry)var3.next();
         if (entry.slot.equals(slot)) {
            attributeConsumer.accept(entry.attribute, entry.modifier);
         }
      }

   }

   public void applyModifiers(EquipmentSlot slot, BiConsumer attributeConsumer) {
      Iterator var3 = this.modifiers.iterator();

      while(var3.hasNext()) {
         Entry entry = (Entry)var3.next();
         if (entry.slot.matches(slot)) {
            attributeConsumer.accept(entry.attribute, entry.modifier);
         }
      }

   }

   public double applyOperations(double base, EquipmentSlot slot) {
      double d = base;
      Iterator var6 = this.modifiers.iterator();

      while(var6.hasNext()) {
         Entry entry = (Entry)var6.next();
         if (entry.slot.matches(slot)) {
            double e = entry.modifier.value();
            double var10001;
            switch (entry.modifier.operation()) {
               case ADD_VALUE:
                  var10001 = e;
                  break;
               case ADD_MULTIPLIED_BASE:
                  var10001 = e * base;
                  break;
               case ADD_MULTIPLIED_TOTAL:
                  var10001 = e * d;
                  break;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }

            d += var10001;
         }
      }

      return d;
   }

   public List modifiers() {
      return this.modifiers;
   }

   static {
      CODEC = AttributeModifiersComponent.Entry.CODEC.listOf().xmap(AttributeModifiersComponent::new, AttributeModifiersComponent::modifiers);
      PACKET_CODEC = PacketCodec.tuple(AttributeModifiersComponent.Entry.PACKET_CODEC.collect(PacketCodecs.toList()), AttributeModifiersComponent::modifiers, AttributeModifiersComponent::new);
      DECIMAL_FORMAT = (DecimalFormat)Util.make(new DecimalFormat("#.##"), (format) -> {
         format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
      });
   }

   public static class Builder {
      private final ImmutableList.Builder entries = ImmutableList.builder();

      Builder() {
      }

      public Builder add(RegistryEntry attribute, EntityAttributeModifier modifier, AttributeModifierSlot slot) {
         this.entries.add(new Entry(attribute, modifier, slot));
         return this;
      }

      public Builder add(RegistryEntry attribute, EntityAttributeModifier modifier, AttributeModifierSlot slot, Display display) {
         this.entries.add(new Entry(attribute, modifier, slot, display));
         return this;
      }

      public AttributeModifiersComponent build() {
         return new AttributeModifiersComponent(this.entries.build());
      }
   }

   public static record Entry(RegistryEntry attribute, EntityAttributeModifier modifier, AttributeModifierSlot slot, Display display) {
      final RegistryEntry attribute;
      final EntityAttributeModifier modifier;
      final AttributeModifierSlot slot;
      final Display display;
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityAttribute.CODEC.fieldOf("type").forGetter(Entry::attribute), EntityAttributeModifier.MAP_CODEC.forGetter(Entry::modifier), AttributeModifierSlot.CODEC.optionalFieldOf("slot", AttributeModifierSlot.ANY).forGetter(Entry::slot), AttributeModifiersComponent.Display.CODEC.optionalFieldOf("display", AttributeModifiersComponent.Display.Default.INSTANCE).forGetter(Entry::display)).apply(instance, Entry::new);
      });
      public static final PacketCodec PACKET_CODEC;

      public Entry(RegistryEntry attribute, EntityAttributeModifier modifier, AttributeModifierSlot slot) {
         this(attribute, modifier, slot, AttributeModifiersComponent.Display.getDefault());
      }

      public Entry(RegistryEntry registryEntry, EntityAttributeModifier entityAttributeModifier, AttributeModifierSlot attributeModifierSlot, Display display) {
         this.attribute = registryEntry;
         this.modifier = entityAttributeModifier;
         this.slot = attributeModifierSlot;
         this.display = display;
      }

      public boolean matches(RegistryEntry attribute, Identifier modifierId) {
         return attribute.equals(this.attribute) && this.modifier.idMatches(modifierId);
      }

      public RegistryEntry attribute() {
         return this.attribute;
      }

      public EntityAttributeModifier modifier() {
         return this.modifier;
      }

      public AttributeModifierSlot slot() {
         return this.slot;
      }

      public Display display() {
         return this.display;
      }

      static {
         PACKET_CODEC = PacketCodec.tuple(EntityAttribute.PACKET_CODEC, Entry::attribute, EntityAttributeModifier.PACKET_CODEC, Entry::modifier, AttributeModifierSlot.PACKET_CODEC, Entry::slot, AttributeModifiersComponent.Display.PACKET_CODEC, Entry::display, Entry::new);
      }
   }

   public interface Display {
      Codec CODEC = AttributeModifiersComponent.Display.Type.CODEC.dispatch("type", Display::getType, (type) -> {
         return type.codec;
      });
      PacketCodec PACKET_CODEC = AttributeModifiersComponent.Display.Type.PACKET_CODEC.cast().dispatch(Display::getType, Type::getPacketCodec);

      static Display getDefault() {
         return AttributeModifiersComponent.Display.Default.INSTANCE;
      }

      static Display getHidden() {
         return AttributeModifiersComponent.Display.Hidden.INSTANCE;
      }

      static Display createOverride(Text text) {
         return new Override(text);
      }

      Type getType();

      void addTooltip(Consumer textConsumer, @Nullable PlayerEntity player, RegistryEntry attribute, EntityAttributeModifier modifier);

      public static record Default() implements Display {
         static final Default INSTANCE = new Default();
         static final MapCodec CODEC;
         static final PacketCodec PACKET_CODEC;

         public Type getType() {
            return AttributeModifiersComponent.Display.Type.DEFAULT;
         }

         public void addTooltip(Consumer textConsumer, @Nullable PlayerEntity player, RegistryEntry attribute, EntityAttributeModifier modifier) {
            double d = modifier.value();
            boolean bl = false;
            if (player != null) {
               if (modifier.idMatches(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID)) {
                  d += player.getAttributeBaseValue(EntityAttributes.ATTACK_DAMAGE);
                  bl = true;
               } else if (modifier.idMatches(Item.BASE_ATTACK_SPEED_MODIFIER_ID)) {
                  d += player.getAttributeBaseValue(EntityAttributes.ATTACK_SPEED);
                  bl = true;
               }
            }

            double e;
            if (modifier.operation() != EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE && modifier.operation() != EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
               if (attribute.matches(EntityAttributes.KNOCKBACK_RESISTANCE)) {
                  e = d * 10.0;
               } else {
                  e = d;
               }
            } else {
               e = d * 100.0;
            }

            if (bl) {
               textConsumer.accept(ScreenTexts.space().append((Text)Text.translatable("attribute.modifier.equals." + modifier.operation().getId(), AttributeModifiersComponent.DECIMAL_FORMAT.format(e), Text.translatable(((EntityAttribute)attribute.value()).getTranslationKey()))).formatted(Formatting.DARK_GREEN));
            } else if (d > 0.0) {
               textConsumer.accept(Text.translatable("attribute.modifier.plus." + modifier.operation().getId(), AttributeModifiersComponent.DECIMAL_FORMAT.format(e), Text.translatable(((EntityAttribute)attribute.value()).getTranslationKey())).formatted(((EntityAttribute)attribute.value()).getFormatting(true)));
            } else if (d < 0.0) {
               textConsumer.accept(Text.translatable("attribute.modifier.take." + modifier.operation().getId(), AttributeModifiersComponent.DECIMAL_FORMAT.format(-e), Text.translatable(((EntityAttribute)attribute.value()).getTranslationKey())).formatted(((EntityAttribute)attribute.value()).getFormatting(false)));
            }

         }

         static {
            CODEC = MapCodec.unit(INSTANCE);
            PACKET_CODEC = PacketCodec.unit(INSTANCE);
         }
      }

      public static record Hidden() implements Display {
         static final Hidden INSTANCE = new Hidden();
         static final MapCodec CODEC;
         static final PacketCodec PACKET_CODEC;

         public Type getType() {
            return AttributeModifiersComponent.Display.Type.HIDDEN;
         }

         public void addTooltip(Consumer textConsumer, @Nullable PlayerEntity player, RegistryEntry attribute, EntityAttributeModifier modifier) {
         }

         static {
            CODEC = MapCodec.unit(INSTANCE);
            PACKET_CODEC = PacketCodec.unit(INSTANCE);
         }
      }

      public static record Override(Text value) implements Display {
         static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(TextCodecs.CODEC.fieldOf("value").forGetter(Override::value)).apply(instance, Override::new);
         });
         static final PacketCodec PACKET_CODEC;

         public Override(Text text) {
            this.value = text;
         }

         public Type getType() {
            return AttributeModifiersComponent.Display.Type.OVERRIDE;
         }

         public void addTooltip(Consumer textConsumer, @Nullable PlayerEntity player, RegistryEntry attribute, EntityAttributeModifier modifier) {
            textConsumer.accept(this.value);
         }

         public Text value() {
            return this.value;
         }

         static {
            PACKET_CODEC = PacketCodec.tuple(TextCodecs.REGISTRY_PACKET_CODEC, Override::value, Override::new);
         }
      }

      public static enum Type implements StringIdentifiable {
         DEFAULT("default", 0, AttributeModifiersComponent.Display.Default.CODEC, AttributeModifiersComponent.Display.Default.PACKET_CODEC),
         HIDDEN("hidden", 1, AttributeModifiersComponent.Display.Hidden.CODEC, AttributeModifiersComponent.Display.Hidden.PACKET_CODEC),
         OVERRIDE("override", 2, AttributeModifiersComponent.Display.Override.CODEC, AttributeModifiersComponent.Display.Override.PACKET_CODEC);

         static final Codec CODEC = StringIdentifiable.createCodec(Type::values);
         private static final IntFunction INDEX_MAPPER = ValueLists.createIndexToValueFunction(Type::getIndex, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.ZERO);
         static final PacketCodec PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, Type::getIndex);
         private final String id;
         private final int index;
         final MapCodec codec;
         private final PacketCodec packetCodec;

         private Type(final String id, final int index, final MapCodec codec, final PacketCodec packetCodec) {
            this.id = id;
            this.index = index;
            this.codec = codec;
            this.packetCodec = packetCodec;
         }

         public String asString() {
            return this.id;
         }

         private int getIndex() {
            return this.index;
         }

         private PacketCodec getPacketCodec() {
            return this.packetCodec;
         }

         // $FF: synthetic method
         private static Type[] method_70738() {
            return new Type[]{DEFAULT, HIDDEN, OVERRIDE};
         }
      }
   }
}
