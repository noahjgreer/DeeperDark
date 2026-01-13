/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonIOException
 *  com.google.gson.JsonParseException
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.minecraft.advancement.Advancement
 *  net.minecraft.advancement.AdvancementCriterion
 *  net.minecraft.advancement.AdvancementDisplays
 *  net.minecraft.advancement.AdvancementEntry
 *  net.minecraft.advancement.AdvancementManager
 *  net.minecraft.advancement.AdvancementProgress
 *  net.minecraft.advancement.PlacedAdvancement
 *  net.minecraft.advancement.PlayerAdvancementTracker
 *  net.minecraft.advancement.PlayerAdvancementTracker$ProgressMap
 *  net.minecraft.advancement.criterion.Criterion
 *  net.minecraft.advancement.criterion.Criterion$ConditionsContainer
 *  net.minecraft.advancement.criterion.CriterionConditions
 *  net.minecraft.advancement.criterion.CriterionProgress
 *  net.minecraft.datafixer.DataFixTypes
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.play.SelectAdvancementTabS2CPacket
 *  net.minecraft.registry.Registries
 *  net.minecraft.server.PlayerManager
 *  net.minecraft.server.ServerAdvancementLoader
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.StrictJsonParser
 *  net.minecraft.util.path.PathUtil
 *  net.minecraft.world.rule.GameRules
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.advancement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementDisplays;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SelectAdvancementTabS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.path.PathUtil;
import net.minecraft.world.rule.GameRules;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class PlayerAdvancementTracker {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final PlayerManager playerManager;
    private final Path filePath;
    private AdvancementManager advancementManager;
    private final Map<AdvancementEntry, AdvancementProgress> progress = new LinkedHashMap();
    private final Set<AdvancementEntry> visibleAdvancements = new HashSet();
    private final Set<AdvancementEntry> progressUpdates = new HashSet();
    private final Set<PlacedAdvancement> updatedRoots = new HashSet();
    private ServerPlayerEntity owner;
    private @Nullable AdvancementEntry currentDisplayTab;
    private boolean dirty = true;
    private final Codec<ProgressMap> progressMapCodec;

    public PlayerAdvancementTracker(DataFixer dataFixer, PlayerManager playerManager, ServerAdvancementLoader advancementLoader, Path filePath, ServerPlayerEntity owner) {
        this.playerManager = playerManager;
        this.filePath = filePath;
        this.owner = owner;
        this.advancementManager = advancementLoader.getManager();
        int i = 1343;
        this.progressMapCodec = DataFixTypes.ADVANCEMENTS.createDataFixingCodec(ProgressMap.CODEC, dataFixer, 1343);
        this.load(advancementLoader);
    }

    public void setOwner(ServerPlayerEntity owner) {
        this.owner = owner;
    }

    public void clearCriteria() {
        for (Criterion criterion : Registries.CRITERION) {
            criterion.endTracking(this);
        }
    }

    public void reload(ServerAdvancementLoader advancementLoader) {
        this.clearCriteria();
        this.progress.clear();
        this.visibleAdvancements.clear();
        this.updatedRoots.clear();
        this.progressUpdates.clear();
        this.dirty = true;
        this.currentDisplayTab = null;
        this.advancementManager = advancementLoader.getManager();
        this.load(advancementLoader);
    }

    private void beginTrackingAllAdvancements(ServerAdvancementLoader advancementLoader) {
        for (AdvancementEntry advancementEntry : advancementLoader.getAdvancements()) {
            this.beginTracking(advancementEntry);
        }
    }

    private void rewardEmptyAdvancements(ServerAdvancementLoader advancementLoader) {
        for (AdvancementEntry advancementEntry : advancementLoader.getAdvancements()) {
            Advancement advancement = advancementEntry.value();
            if (!advancement.criteria().isEmpty()) continue;
            this.grantCriterion(advancementEntry, "");
            advancement.rewards().apply(this.owner);
        }
    }

    private void load(ServerAdvancementLoader advancementLoader) {
        if (Files.isRegularFile(this.filePath, new LinkOption[0])) {
            try (BufferedReader reader = Files.newBufferedReader(this.filePath, StandardCharsets.UTF_8);){
                JsonElement jsonElement = StrictJsonParser.parse((Reader)reader);
                ProgressMap progressMap = (ProgressMap)this.progressMapCodec.parse((DynamicOps)JsonOps.INSTANCE, (Object)jsonElement).getOrThrow(JsonParseException::new);
                this.loadProgressMap(advancementLoader, progressMap);
            }
            catch (JsonIOException | IOException exception) {
                LOGGER.error("Couldn't access player advancements in {}", (Object)this.filePath, (Object)exception);
            }
            catch (JsonParseException jsonParseException) {
                LOGGER.error("Couldn't parse player advancements in {}", (Object)this.filePath, (Object)jsonParseException);
            }
        }
        this.rewardEmptyAdvancements(advancementLoader);
        this.beginTrackingAllAdvancements(advancementLoader);
    }

    public void save() {
        JsonElement jsonElement = (JsonElement)this.progressMapCodec.encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)this.createProgressMap()).getOrThrow();
        try {
            PathUtil.createDirectories((Path)this.filePath.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(this.filePath, StandardCharsets.UTF_8, new OpenOption[0]);){
                GSON.toJson(jsonElement, GSON.newJsonWriter((Writer)writer));
            }
        }
        catch (JsonIOException | IOException exception) {
            LOGGER.error("Couldn't save player advancements to {}", (Object)this.filePath, (Object)exception);
        }
    }

    private void loadProgressMap(ServerAdvancementLoader loader, ProgressMap progressMap) {
        progressMap.forEach((id, progress) -> {
            AdvancementEntry advancementEntry = loader.get(id);
            if (advancementEntry == null) {
                LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", id, (Object)this.filePath);
                return;
            }
            this.initProgress(advancementEntry, progress);
            this.progressUpdates.add(advancementEntry);
            this.onStatusUpdate(advancementEntry);
        });
    }

    private ProgressMap createProgressMap() {
        LinkedHashMap map = new LinkedHashMap();
        this.progress.forEach((entry, progress) -> {
            if (progress.isAnyObtained()) {
                map.put(entry.id(), progress);
            }
        });
        return new ProgressMap(map);
    }

    public boolean grantCriterion(AdvancementEntry advancement, String criterionName) {
        boolean bl = false;
        AdvancementProgress advancementProgress = this.getProgress(advancement);
        boolean bl2 = advancementProgress.isDone();
        if (advancementProgress.obtain(criterionName)) {
            this.endTrackingCompleted(advancement);
            this.progressUpdates.add(advancement);
            bl = true;
            if (!bl2 && advancementProgress.isDone()) {
                advancement.value().rewards().apply(this.owner);
                advancement.value().display().ifPresent(display -> {
                    if (display.shouldAnnounceToChat() && ((Boolean)this.owner.getEntityWorld().getGameRules().getValue(GameRules.ANNOUNCE_ADVANCEMENTS)).booleanValue()) {
                        this.playerManager.broadcast((Text)display.getFrame().getChatAnnouncementText(advancement, this.owner), false);
                    }
                });
            }
        }
        if (!bl2 && advancementProgress.isDone()) {
            this.onStatusUpdate(advancement);
        }
        return bl;
    }

    public boolean revokeCriterion(AdvancementEntry advancement, String criterionName) {
        boolean bl = false;
        AdvancementProgress advancementProgress = this.getProgress(advancement);
        boolean bl2 = advancementProgress.isDone();
        if (advancementProgress.reset(criterionName)) {
            this.beginTracking(advancement);
            this.progressUpdates.add(advancement);
            bl = true;
        }
        if (bl2 && !advancementProgress.isDone()) {
            this.onStatusUpdate(advancement);
        }
        return bl;
    }

    private void onStatusUpdate(AdvancementEntry advancement) {
        PlacedAdvancement placedAdvancement = this.advancementManager.get(advancement);
        if (placedAdvancement != null) {
            this.updatedRoots.add(placedAdvancement.getRoot());
        }
    }

    private void beginTracking(AdvancementEntry advancement) {
        AdvancementProgress advancementProgress = this.getProgress(advancement);
        if (advancementProgress.isDone()) {
            return;
        }
        for (Map.Entry entry : advancement.value().criteria().entrySet()) {
            CriterionProgress criterionProgress = advancementProgress.getCriterionProgress((String)entry.getKey());
            if (criterionProgress == null || criterionProgress.isObtained()) continue;
            this.beginTracking(advancement, (String)entry.getKey(), (AdvancementCriterion)entry.getValue());
        }
    }

    private <T extends CriterionConditions> void beginTracking(AdvancementEntry advancement, String id, AdvancementCriterion<T> criterion) {
        criterion.trigger().beginTrackingCondition(this, new Criterion.ConditionsContainer(criterion.conditions(), advancement, id));
    }

    private void endTrackingCompleted(AdvancementEntry advancement) {
        AdvancementProgress advancementProgress = this.getProgress(advancement);
        for (Map.Entry entry : advancement.value().criteria().entrySet()) {
            CriterionProgress criterionProgress = advancementProgress.getCriterionProgress((String)entry.getKey());
            if (criterionProgress == null || !criterionProgress.isObtained() && !advancementProgress.isDone()) continue;
            this.endTrackingCompleted(advancement, (String)entry.getKey(), (AdvancementCriterion)entry.getValue());
        }
    }

    private <T extends CriterionConditions> void endTrackingCompleted(AdvancementEntry advancement, String id, AdvancementCriterion<T> criterion) {
        criterion.trigger().endTrackingCondition(this, new Criterion.ConditionsContainer(criterion.conditions(), advancement, id));
    }

    public void sendUpdate(ServerPlayerEntity player, boolean showToast) {
        if (this.dirty || !this.updatedRoots.isEmpty() || !this.progressUpdates.isEmpty()) {
            HashMap<Identifier, AdvancementProgress> map = new HashMap<Identifier, AdvancementProgress>();
            HashSet set = new HashSet();
            HashSet set2 = new HashSet();
            for (PlacedAdvancement placedAdvancement : this.updatedRoots) {
                this.calculateDisplay(placedAdvancement, set, set2);
            }
            this.updatedRoots.clear();
            for (AdvancementEntry advancementEntry : this.progressUpdates) {
                if (!this.visibleAdvancements.contains(advancementEntry)) continue;
                map.put(advancementEntry.id(), (AdvancementProgress)this.progress.get(advancementEntry));
            }
            this.progressUpdates.clear();
            if (!(map.isEmpty() && set.isEmpty() && set2.isEmpty())) {
                player.networkHandler.sendPacket((Packet)new AdvancementUpdateS2CPacket(this.dirty, set, set2, map, showToast));
            }
        }
        this.dirty = false;
    }

    public void setDisplayTab(@Nullable AdvancementEntry advancement) {
        AdvancementEntry advancementEntry = this.currentDisplayTab;
        this.currentDisplayTab = advancement != null && advancement.value().isRoot() && advancement.value().display().isPresent() ? advancement : null;
        if (advancementEntry != this.currentDisplayTab) {
            this.owner.networkHandler.sendPacket((Packet)new SelectAdvancementTabS2CPacket(this.currentDisplayTab == null ? null : this.currentDisplayTab.id()));
        }
    }

    public AdvancementProgress getProgress(AdvancementEntry advancement) {
        AdvancementProgress advancementProgress = (AdvancementProgress)this.progress.get(advancement);
        if (advancementProgress == null) {
            advancementProgress = new AdvancementProgress();
            this.initProgress(advancement, advancementProgress);
        }
        return advancementProgress;
    }

    private void initProgress(AdvancementEntry advancement, AdvancementProgress progress) {
        progress.init(advancement.value().requirements());
        this.progress.put(advancement, progress);
    }

    private void calculateDisplay(PlacedAdvancement root, Set<AdvancementEntry> added, Set<Identifier> removed) {
        AdvancementDisplays.calculateDisplay((PlacedAdvancement)root, advancement -> this.getProgress(advancement.getAdvancementEntry()).isDone(), (advancement, displayed) -> {
            AdvancementEntry advancementEntry = advancement.getAdvancementEntry();
            if (displayed) {
                if (this.visibleAdvancements.add(advancementEntry)) {
                    added.add(advancementEntry);
                    if (this.progress.containsKey(advancementEntry)) {
                        this.progressUpdates.add(advancementEntry);
                    }
                }
            } else if (this.visibleAdvancements.remove(advancementEntry)) {
                removed.add(advancementEntry.id());
            }
        });
    }
}

