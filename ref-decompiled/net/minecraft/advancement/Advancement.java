package net.minecraft.advancement;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record Advancement(Optional parent, Optional display, AdvancementRewards rewards, Map criteria, AdvancementRequirements requirements, boolean sendsTelemetryEvent, Optional name) {
   private static final Codec CRITERIA_CODEC;
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;

   public Advancement(Optional parent, Optional display, AdvancementRewards rewards, Map criteria, AdvancementRequirements requirements, boolean sendsTelemetryEvent) {
      this(parent, display, rewards, Map.copyOf(criteria), requirements, sendsTelemetryEvent, display.map(Advancement::createNameFromDisplay));
   }

   public Advancement(Optional optional, Optional optional2, AdvancementRewards advancementRewards, Map map, AdvancementRequirements advancementRequirements, boolean bl, Optional optional3) {
      this.parent = optional;
      this.display = optional2;
      this.rewards = advancementRewards;
      this.criteria = map;
      this.requirements = advancementRequirements;
      this.sendsTelemetryEvent = bl;
      this.name = optional3;
   }

   private static DataResult validate(Advancement advancement) {
      return advancement.requirements().validate(advancement.criteria().keySet()).map((validated) -> {
         return advancement;
      });
   }

   private static Text createNameFromDisplay(AdvancementDisplay display) {
      Text text = display.getTitle();
      Formatting formatting = display.getFrame().getTitleFormat();
      Text text2 = Texts.setStyleIfAbsent(text.copy(), Style.EMPTY.withColor(formatting)).append("\n").append(display.getDescription());
      Text text3 = text.copy().styled((style) -> {
         return style.withHoverEvent(new HoverEvent.ShowText(text2));
      });
      return Texts.bracketed(text3).formatted(formatting);
   }

   public static Text getNameFromIdentity(AdvancementEntry identifiedAdvancement) {
      return (Text)identifiedAdvancement.value().name().orElseGet(() -> {
         return Text.literal(identifiedAdvancement.id().toString());
      });
   }

   private void write(RegistryByteBuf buf) {
      buf.writeOptional(this.parent, PacketByteBuf::writeIdentifier);
      AdvancementDisplay.PACKET_CODEC.collect(PacketCodecs::optional).encode(buf, this.display);
      this.requirements.writeRequirements(buf);
      buf.writeBoolean(this.sendsTelemetryEvent);
   }

   private static Advancement read(RegistryByteBuf buf) {
      return new Advancement(buf.readOptional(PacketByteBuf::readIdentifier), (Optional)AdvancementDisplay.PACKET_CODEC.collect(PacketCodecs::optional).decode(buf), AdvancementRewards.NONE, Map.of(), new AdvancementRequirements(buf), buf.readBoolean());
   }

   public boolean isRoot() {
      return this.parent.isEmpty();
   }

   public void validate(ErrorReporter errorReporter, RegistryEntryLookup.RegistryLookup lookup) {
      this.criteria.forEach((name, criterion) -> {
         LootContextPredicateValidator lootContextPredicateValidator = new LootContextPredicateValidator(errorReporter.makeChild(new ErrorReporter.CriterionContext(name)), lookup);
         criterion.conditions().validate(lootContextPredicateValidator);
      });
   }

   public Optional parent() {
      return this.parent;
   }

   public Optional display() {
      return this.display;
   }

   public AdvancementRewards rewards() {
      return this.rewards;
   }

   public Map criteria() {
      return this.criteria;
   }

   public AdvancementRequirements requirements() {
      return this.requirements;
   }

   public boolean sendsTelemetryEvent() {
      return this.sendsTelemetryEvent;
   }

   public Optional name() {
      return this.name;
   }

   static {
      CRITERIA_CODEC = Codec.unboundedMap(Codec.STRING, AdvancementCriterion.CODEC).validate((criteria) -> {
         return criteria.isEmpty() ? DataResult.error(() -> {
            return "Advancement criteria cannot be empty";
         }) : DataResult.success(criteria);
      });
      CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Identifier.CODEC.optionalFieldOf("parent").forGetter(Advancement::parent), AdvancementDisplay.CODEC.optionalFieldOf("display").forGetter(Advancement::display), AdvancementRewards.CODEC.optionalFieldOf("rewards", AdvancementRewards.NONE).forGetter(Advancement::rewards), CRITERIA_CODEC.fieldOf("criteria").forGetter(Advancement::criteria), AdvancementRequirements.CODEC.optionalFieldOf("requirements").forGetter((advancement) -> {
            return Optional.of(advancement.requirements());
         }), Codec.BOOL.optionalFieldOf("sends_telemetry_event", false).forGetter(Advancement::sendsTelemetryEvent)).apply(instance, (parent, display, rewards, criteria, requirements, sendsTelemetryEvent) -> {
            AdvancementRequirements advancementRequirements = (AdvancementRequirements)requirements.orElseGet(() -> {
               return AdvancementRequirements.allOf(criteria.keySet());
            });
            return new Advancement(parent, display, rewards, criteria, advancementRequirements, sendsTelemetryEvent);
         });
      }).validate(Advancement::validate);
      PACKET_CODEC = PacketCodec.of(Advancement::write, Advancement::read);
   }

   public static class Builder {
      private Optional parentObj = Optional.empty();
      private Optional display = Optional.empty();
      private AdvancementRewards rewards;
      private final ImmutableMap.Builder criteria;
      private Optional requirements;
      private AdvancementRequirements.CriterionMerger merger;
      private boolean sendsTelemetryEvent;

      public Builder() {
         this.rewards = AdvancementRewards.NONE;
         this.criteria = ImmutableMap.builder();
         this.requirements = Optional.empty();
         this.merger = AdvancementRequirements.CriterionMerger.AND;
      }

      public static Builder create() {
         return (new Builder()).sendsTelemetryEvent();
      }

      public static Builder createUntelemetered() {
         return new Builder();
      }

      public Builder parent(AdvancementEntry parent) {
         this.parentObj = Optional.of(parent.id());
         return this;
      }

      /** @deprecated */
      @Deprecated(
         forRemoval = true
      )
      public Builder parent(Identifier parentId) {
         this.parentObj = Optional.of(parentId);
         return this;
      }

      public Builder display(ItemStack icon, Text title, Text description, @Nullable Identifier background, AdvancementFrame frame, boolean showToast, boolean announceToChat, boolean hidden) {
         return this.display(new AdvancementDisplay(icon, title, description, Optional.ofNullable(background).map(AssetInfo::new), frame, showToast, announceToChat, hidden));
      }

      public Builder display(ItemConvertible icon, Text title, Text description, @Nullable Identifier background, AdvancementFrame frame, boolean showToast, boolean announceToChat, boolean hidden) {
         return this.display(new AdvancementDisplay(new ItemStack(icon.asItem()), title, description, Optional.ofNullable(background).map(AssetInfo::new), frame, showToast, announceToChat, hidden));
      }

      public Builder display(AdvancementDisplay display) {
         this.display = Optional.of(display);
         return this;
      }

      public Builder rewards(AdvancementRewards.Builder builder) {
         return this.rewards(builder.build());
      }

      public Builder rewards(AdvancementRewards rewards) {
         this.rewards = rewards;
         return this;
      }

      public Builder criterion(String name, AdvancementCriterion criterion) {
         this.criteria.put(name, criterion);
         return this;
      }

      public Builder criteriaMerger(AdvancementRequirements.CriterionMerger merger) {
         this.merger = merger;
         return this;
      }

      public Builder requirements(AdvancementRequirements requirements) {
         this.requirements = Optional.of(requirements);
         return this;
      }

      public Builder sendsTelemetryEvent() {
         this.sendsTelemetryEvent = true;
         return this;
      }

      public AdvancementEntry build(Identifier id) {
         Map map = this.criteria.buildOrThrow();
         AdvancementRequirements advancementRequirements = (AdvancementRequirements)this.requirements.orElseGet(() -> {
            return this.merger.create(map.keySet());
         });
         return new AdvancementEntry(id, new Advancement(this.parentObj, this.display, this.rewards, map, advancementRequirements, this.sendsTelemetryEvent));
      }

      public AdvancementEntry build(Consumer exporter, String id) {
         AdvancementEntry advancementEntry = this.build(Identifier.of(id));
         exporter.accept(advancementEntry);
         return advancementEntry;
      }
   }
}
