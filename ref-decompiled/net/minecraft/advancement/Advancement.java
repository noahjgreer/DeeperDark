/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.advancement.Advancement
 *  net.minecraft.advancement.AdvancementCriterion
 *  net.minecraft.advancement.AdvancementDisplay
 *  net.minecraft.advancement.AdvancementEntry
 *  net.minecraft.advancement.AdvancementRequirements
 *  net.minecraft.advancement.AdvancementRewards
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.network.RegistryByteBuf
 *  net.minecraft.network.codec.PacketCodec
 *  net.minecraft.network.codec.PacketCodecs
 *  net.minecraft.predicate.entity.LootContextPredicateValidator
 *  net.minecraft.registry.RegistryEntryLookup$RegistryLookup
 *  net.minecraft.text.HoverEvent
 *  net.minecraft.text.HoverEvent$ShowText
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  net.minecraft.text.Texts
 *  net.minecraft.util.ErrorReporter
 *  net.minecraft.util.ErrorReporter$Context
 *  net.minecraft.util.ErrorReporter$CriterionContext
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Identifier
 */
package net.minecraft.advancement;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public record Advancement(Optional<Identifier> parent, Optional<AdvancementDisplay> display, AdvancementRewards rewards, Map<String, AdvancementCriterion<?>> criteria, AdvancementRequirements requirements, boolean sendsTelemetryEvent, Optional<Text> name) {
    private final Optional<Identifier> parent;
    private final Optional<AdvancementDisplay> display;
    private final AdvancementRewards rewards;
    private final Map<String, AdvancementCriterion<?>> criteria;
    private final AdvancementRequirements requirements;
    private final boolean sendsTelemetryEvent;
    private final Optional<Text> name;
    private static final Codec<Map<String, AdvancementCriterion<?>>> CRITERIA_CODEC = Codec.unboundedMap((Codec)Codec.STRING, (Codec)AdvancementCriterion.CODEC).validate((T criteria) -> criteria.isEmpty() ? DataResult.error(() -> "Advancement criteria cannot be empty") : DataResult.success((Object)criteria));
    public static final Codec<Advancement> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Identifier.CODEC.optionalFieldOf("parent").forGetter(Advancement::parent), (App)AdvancementDisplay.CODEC.optionalFieldOf("display").forGetter(Advancement::display), (App)AdvancementRewards.CODEC.optionalFieldOf("rewards", (Object)AdvancementRewards.NONE).forGetter(Advancement::rewards), (App)CRITERIA_CODEC.fieldOf("criteria").forGetter(Advancement::criteria), (App)AdvancementRequirements.CODEC.optionalFieldOf("requirements").forGetter(advancement -> Optional.of(advancement.requirements())), (App)Codec.BOOL.optionalFieldOf("sends_telemetry_event", (Object)false).forGetter(Advancement::sendsTelemetryEvent)).apply((Applicative)instance, (parent, display, rewards, criteria, requirements, sendsTelemetryEvent) -> {
        AdvancementRequirements advancementRequirements = requirements.orElseGet(() -> AdvancementRequirements.allOf(criteria.keySet()));
        return new Advancement(parent, display, rewards, criteria, advancementRequirements, sendsTelemetryEvent.booleanValue());
    })).validate(Advancement::validate);
    public static final PacketCodec<RegistryByteBuf, Advancement> PACKET_CODEC = PacketCodec.of(Advancement::write, Advancement::read);

    public Advancement(Optional<Identifier> parent, Optional<AdvancementDisplay> display, AdvancementRewards rewards, Map<String, AdvancementCriterion<?>> criteria, AdvancementRequirements requirements, boolean sendsTelemetryEvent) {
        this(parent, display, rewards, Map.copyOf(criteria), requirements, sendsTelemetryEvent, display.map(Advancement::createNameFromDisplay));
    }

    public Advancement(Optional<Identifier> parent, Optional<AdvancementDisplay> display, AdvancementRewards rewards, Map<String, AdvancementCriterion<?>> criteria, AdvancementRequirements requirements, boolean sendsTelemetryEvent, Optional<Text> name) {
        this.parent = parent;
        this.display = display;
        this.rewards = rewards;
        this.criteria = criteria;
        this.requirements = requirements;
        this.sendsTelemetryEvent = sendsTelemetryEvent;
        this.name = name;
    }

    private static DataResult<Advancement> validate(Advancement advancement) {
        return advancement.requirements().validate(advancement.criteria().keySet()).map(validated -> advancement);
    }

    private static Text createNameFromDisplay(AdvancementDisplay display) {
        Text text = display.getTitle();
        Formatting formatting = display.getFrame().getTitleFormat();
        MutableText text2 = Texts.setStyleIfAbsent((MutableText)text.copy(), (Style)Style.EMPTY.withColor(formatting)).append("\n").append(display.getDescription());
        MutableText text3 = text.copy().styled(arg_0 -> Advancement.method_53629((Text)text2, arg_0));
        return Texts.bracketed((Text)text3).formatted(formatting);
    }

    public static Text getNameFromIdentity(AdvancementEntry identifiedAdvancement) {
        return identifiedAdvancement.value().name().orElseGet(() -> Text.literal((String)identifiedAdvancement.id().toString()));
    }

    private void write(RegistryByteBuf buf) {
        buf.writeOptional(this.parent, PacketByteBuf::writeIdentifier);
        AdvancementDisplay.PACKET_CODEC.collect(PacketCodecs::optional).encode((Object)buf, (Object)this.display);
        this.requirements.writeRequirements((PacketByteBuf)buf);
        buf.writeBoolean(this.sendsTelemetryEvent);
    }

    private static Advancement read(RegistryByteBuf buf) {
        return new Advancement(buf.readOptional(PacketByteBuf::readIdentifier), (Optional)AdvancementDisplay.PACKET_CODEC.collect(PacketCodecs::optional).decode((Object)buf), AdvancementRewards.NONE, Map.of(), new AdvancementRequirements((PacketByteBuf)buf), buf.readBoolean());
    }

    public boolean isRoot() {
        return this.parent.isEmpty();
    }

    public void validate(ErrorReporter errorReporter, RegistryEntryLookup.RegistryLookup lookup) {
        this.criteria.forEach((name, criterion) -> {
            LootContextPredicateValidator lootContextPredicateValidator = new LootContextPredicateValidator(errorReporter.makeChild((ErrorReporter.Context)new ErrorReporter.CriterionContext(name)), lookup);
            criterion.conditions().validate(lootContextPredicateValidator);
        });
    }

    public Optional<Identifier> parent() {
        return this.parent;
    }

    public Optional<AdvancementDisplay> display() {
        return this.display;
    }

    public AdvancementRewards rewards() {
        return this.rewards;
    }

    public Map<String, AdvancementCriterion<?>> criteria() {
        return this.criteria;
    }

    public AdvancementRequirements requirements() {
        return this.requirements;
    }

    public boolean sendsTelemetryEvent() {
        return this.sendsTelemetryEvent;
    }

    public Optional<Text> name() {
        return this.name;
    }

    private static /* synthetic */ Style method_53629(Text text, Style style) {
        return style.withHoverEvent((HoverEvent)new HoverEvent.ShowText(text));
    }
}

