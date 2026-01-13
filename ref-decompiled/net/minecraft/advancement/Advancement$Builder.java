/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.advancement;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

public static class Advancement.Builder {
    private Optional<Identifier> parentObj = Optional.empty();
    private Optional<AdvancementDisplay> display = Optional.empty();
    private AdvancementRewards rewards = AdvancementRewards.NONE;
    private final ImmutableMap.Builder<String, AdvancementCriterion<?>> criteria = ImmutableMap.builder();
    private Optional<AdvancementRequirements> requirements = Optional.empty();
    private AdvancementRequirements.CriterionMerger merger = AdvancementRequirements.CriterionMerger.AND;
    private boolean sendsTelemetryEvent;

    public static Advancement.Builder create() {
        return new Advancement.Builder().sendsTelemetryEvent();
    }

    public static Advancement.Builder createUntelemetered() {
        return new Advancement.Builder();
    }

    public Advancement.Builder parent(AdvancementEntry parent) {
        this.parentObj = Optional.of(parent.id());
        return this;
    }

    @Deprecated(forRemoval=true)
    public Advancement.Builder parent(Identifier parentId) {
        this.parentObj = Optional.of(parentId);
        return this;
    }

    public Advancement.Builder display(ItemStack icon, Text title, Text description, @Nullable Identifier background, AdvancementFrame frame, boolean showToast, boolean announceToChat, boolean hidden) {
        return this.display(new AdvancementDisplay(icon, title, description, Optional.ofNullable(background).map(AssetInfo.TextureAssetInfo::new), frame, showToast, announceToChat, hidden));
    }

    public Advancement.Builder display(ItemConvertible icon, Text title, Text description, @Nullable Identifier background, AdvancementFrame frame, boolean showToast, boolean announceToChat, boolean hidden) {
        return this.display(new AdvancementDisplay(new ItemStack(icon.asItem()), title, description, Optional.ofNullable(background).map(AssetInfo.TextureAssetInfo::new), frame, showToast, announceToChat, hidden));
    }

    public Advancement.Builder display(AdvancementDisplay display) {
        this.display = Optional.of(display);
        return this;
    }

    public Advancement.Builder rewards(AdvancementRewards.Builder builder) {
        return this.rewards(builder.build());
    }

    public Advancement.Builder rewards(AdvancementRewards rewards) {
        this.rewards = rewards;
        return this;
    }

    public Advancement.Builder criterion(String name, AdvancementCriterion<?> criterion) {
        this.criteria.put((Object)name, criterion);
        return this;
    }

    public Advancement.Builder criteriaMerger(AdvancementRequirements.CriterionMerger merger) {
        this.merger = merger;
        return this;
    }

    public Advancement.Builder requirements(AdvancementRequirements requirements) {
        this.requirements = Optional.of(requirements);
        return this;
    }

    public Advancement.Builder sendsTelemetryEvent() {
        this.sendsTelemetryEvent = true;
        return this;
    }

    public AdvancementEntry build(Identifier id) {
        ImmutableMap map = this.criteria.buildOrThrow();
        AdvancementRequirements advancementRequirements = this.requirements.orElseGet(() -> this.method_53633((Map)map));
        return new AdvancementEntry(id, new Advancement(this.parentObj, this.display, this.rewards, (Map<String, AdvancementCriterion<?>>)map, advancementRequirements, this.sendsTelemetryEvent));
    }

    public AdvancementEntry build(Consumer<AdvancementEntry> exporter, String id) {
        AdvancementEntry advancementEntry = this.build(Identifier.of(id));
        exporter.accept(advancementEntry);
        return advancementEntry;
    }

    private /* synthetic */ AdvancementRequirements method_53633(Map map) {
        return this.merger.create(map.keySet());
    }
}
