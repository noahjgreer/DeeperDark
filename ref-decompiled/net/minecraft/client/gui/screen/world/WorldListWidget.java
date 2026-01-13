/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.FatalErrorScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.gui.screen.world.CreateWorldScreen
 *  net.minecraft.client.gui.screen.world.WorldListWidget
 *  net.minecraft.client.gui.screen.world.WorldListWidget$EmptyListEntry
 *  net.minecraft.client.gui.screen.world.WorldListWidget$Entry
 *  net.minecraft.client.gui.screen.world.WorldListWidget$LoadingEntry
 *  net.minecraft.client.gui.screen.world.WorldListWidget$WorldEntry
 *  net.minecraft.client.gui.screen.world.WorldListWidget$WorldListType
 *  net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
 *  net.minecraft.client.gui.widget.EntryListWidget$Entry
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Util
 *  net.minecraft.util.crash.CrashReport
 *  net.minecraft.world.level.storage.LevelStorage$LevelList
 *  net.minecraft.world.level.storage.LevelStorageException
 *  net.minecraft.world.level.storage.LevelSummary
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.world;

import com.mojang.logging.LogUtils;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.FatalErrorScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class WorldListWidget
extends AlwaysSelectedEntryListWidget<Entry> {
    public static final DateTimeFormatter DATE_FORMAT = Util.getDefaultLocaleFormatter((FormatStyle)FormatStyle.SHORT);
    static final Identifier ERROR_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla((String)"world_list/error_highlighted");
    static final Identifier ERROR_TEXTURE = Identifier.ofVanilla((String)"world_list/error");
    static final Identifier MARKED_JOIN_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla((String)"world_list/marked_join_highlighted");
    static final Identifier MARKED_JOIN_TEXTURE = Identifier.ofVanilla((String)"world_list/marked_join");
    static final Identifier WARNING_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla((String)"world_list/warning_highlighted");
    static final Identifier WARNING_TEXTURE = Identifier.ofVanilla((String)"world_list/warning");
    static final Identifier JOIN_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla((String)"world_list/join_highlighted");
    static final Identifier JOIN_TEXTURE = Identifier.ofVanilla((String)"world_list/join");
    static final Logger LOGGER = LogUtils.getLogger();
    static final Text FROM_NEWER_VERSION_FIRST_LINE = Text.translatable((String)"selectWorld.tooltip.fromNewerVersion1").formatted(Formatting.RED);
    static final Text FROM_NEWER_VERSION_SECOND_LINE = Text.translatable((String)"selectWorld.tooltip.fromNewerVersion2").formatted(Formatting.RED);
    static final Text SNAPSHOT_FIRST_LINE = Text.translatable((String)"selectWorld.tooltip.snapshot1").formatted(Formatting.GOLD);
    static final Text SNAPSHOT_SECOND_LINE = Text.translatable((String)"selectWorld.tooltip.snapshot2").formatted(Formatting.GOLD);
    static final Text LOCKED_TEXT = Text.translatable((String)"selectWorld.locked").formatted(Formatting.RED);
    static final Text CONVERSION_TOOLTIP = Text.translatable((String)"selectWorld.conversion.tooltip").formatted(Formatting.RED);
    static final Text INCOMPATIBLE_TOOLTIP = Text.translatable((String)"selectWorld.incompatible.tooltip").formatted(Formatting.RED);
    static final Text EXPERIMENTAL_TEXT = Text.translatable((String)"selectWorld.experimental");
    private final Screen parent;
    private CompletableFuture<List<LevelSummary>> levelsFuture;
    private @Nullable List<LevelSummary> levels;
    private final LoadingEntry loadingEntry;
    final WorldListType worldListType;
    private String search;
    private boolean failedToGetLevels;
    private final @Nullable Consumer<LevelSummary> selectionCallback;
    final @Nullable Consumer<// Could not load outer class - annotation placement on inner may be incorrect
    WorldEntry> confirmationCallback;

    WorldListWidget(Screen parent, MinecraftClient client, int width, int height, String search, @Nullable WorldListWidget predecessor, @Nullable Consumer<LevelSummary> selectionCallback, @Nullable Consumer<// Could not load outer class - annotation placement on inner may be incorrect
    WorldEntry> confirmationCallback, WorldListType worldListType) {
        super(client, width, height, 0, 36);
        this.parent = parent;
        this.loadingEntry = new LoadingEntry(client);
        this.search = search;
        this.selectionCallback = selectionCallback;
        this.confirmationCallback = confirmationCallback;
        this.worldListType = worldListType;
        this.levelsFuture = predecessor != null ? predecessor.levelsFuture : this.loadLevels();
        this.addEntry((EntryListWidget.Entry)this.loadingEntry);
        this.show(this.tryGet());
    }

    protected void clearEntries() {
        this.children().forEach(Entry::close);
        super.clearEntries();
    }

    private @Nullable List<LevelSummary> tryGet() {
        try {
            List<LevelSummary> list = this.levelsFuture.getNow(null);
            if (this.worldListType == WorldListType.UPLOAD_WORLD) {
                if (list != null && !this.failedToGetLevels) {
                    this.failedToGetLevels = true;
                    list = list.stream().filter(LevelSummary::isImmediatelyLoadable).toList();
                } else {
                    return null;
                }
            }
            return list;
        }
        catch (CancellationException | CompletionException runtimeException) {
            return null;
        }
    }

    public void load() {
        this.levelsFuture = this.loadLevels();
    }

    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        List list = this.tryGet();
        if (list != this.levels) {
            this.show(list);
        }
        super.renderWidget(context, mouseX, mouseY, deltaTicks);
    }

    private void show(@Nullable List<LevelSummary> summaries) {
        if (summaries == null) {
            return;
        }
        if (summaries.isEmpty()) {
            switch (this.worldListType.ordinal()) {
                case 0: {
                    CreateWorldScreen.show((MinecraftClient)this.client, () -> this.client.setScreen(null));
                    break;
                }
                case 1: {
                    this.clearEntries();
                    this.addEntry((EntryListWidget.Entry)new EmptyListEntry((Text)Text.translatable((String)"mco.upload.select.world.none"), this.parent.getTextRenderer()));
                }
            }
        } else {
            this.showSummaries(this.search, summaries);
            this.levels = summaries;
        }
    }

    public void setSearch(String search) {
        if (this.levels != null && !search.equals(this.search)) {
            this.showSummaries(search, this.levels);
        }
        this.search = search;
    }

    private CompletableFuture<List<LevelSummary>> loadLevels() {
        LevelStorage.LevelList levelList;
        try {
            levelList = this.client.getLevelStorage().getLevelList();
        }
        catch (LevelStorageException levelStorageException) {
            LOGGER.error("Couldn't load level list", (Throwable)levelStorageException);
            this.showUnableToLoadScreen(levelStorageException.getMessageText());
            return CompletableFuture.completedFuture(List.of());
        }
        return this.client.getLevelStorage().loadSummaries(levelList).exceptionally(throwable -> {
            this.client.setCrashReportSupplierAndAddDetails(CrashReport.create((Throwable)throwable, (String)"Couldn't load level list"));
            return List.of();
        });
    }

    private void showSummaries(String search, List<LevelSummary> summaries) {
        ArrayList<WorldEntry> list = new ArrayList<WorldEntry>();
        Optional optional = this.getSelectedAsOptional();
        WorldEntry worldEntry = null;
        for (LevelSummary levelSummary : summaries.stream().filter(summary -> this.shouldShow(search.toLowerCase(Locale.ROOT), summary)).toList()) {
            WorldEntry worldEntry2 = new WorldEntry(this, this, levelSummary);
            if (optional.isPresent() && ((WorldEntry)optional.get()).getLevel().getName().equals(worldEntry2.getLevel().getName())) {
                worldEntry = worldEntry2;
            }
            list.add(worldEntry2);
        }
        this.removeEntries(this.children().stream().filter(child -> !list.contains(child)).toList());
        list.forEach(entry -> {
            if (!this.children().contains(entry)) {
                this.addEntry((EntryListWidget.Entry)entry);
            }
        });
        this.setSelected(worldEntry);
        this.narrateScreenIfNarrationEnabled();
    }

    private boolean shouldShow(String search, LevelSummary summary) {
        return summary.getDisplayName().toLowerCase(Locale.ROOT).contains(search) || summary.getName().toLowerCase(Locale.ROOT).contains(search);
    }

    private void narrateScreenIfNarrationEnabled() {
        this.refreshScroll();
        this.parent.narrateScreenIfNarrationEnabled(true);
    }

    private void showUnableToLoadScreen(Text message) {
        this.client.setScreen((Screen)new FatalErrorScreen((Text)Text.translatable((String)"selectWorld.unable_to_load"), message));
    }

    public int getRowWidth() {
        return 270;
    }

    public void setSelected(// Could not load outer class - annotation placement on inner may be incorrect
    @Nullable WorldListWidget.Entry entry) {
        super.setSelected((EntryListWidget.Entry)entry);
        if (this.selectionCallback != null) {
            LevelSummary levelSummary;
            if (entry instanceof WorldEntry) {
                WorldEntry worldEntry = (WorldEntry)entry;
                levelSummary = worldEntry.level;
            } else {
                levelSummary = null;
            }
            this.selectionCallback.accept(levelSummary);
        }
    }

    public Optional<WorldEntry> getSelectedAsOptional() {
        Entry entry = (Entry)this.getSelectedOrNull();
        if (entry instanceof WorldEntry) {
            WorldEntry worldEntry = (WorldEntry)entry;
            return Optional.of(worldEntry);
        }
        return Optional.empty();
    }

    public void refresh() {
        this.load();
        this.client.setScreen(this.parent);
    }

    public Screen getParent() {
        return this.parent;
    }

    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        if (this.children().contains(this.loadingEntry)) {
            this.loadingEntry.appendNarrations(builder);
            return;
        }
        super.appendClickableNarrations(builder);
    }

    static /* synthetic */ MinecraftClient method_43452(WorldListWidget worldListWidget) {
        return worldListWidget.client;
    }

    static /* synthetic */ void method_76291(WorldListWidget worldListWidget, DrawContext drawContext) {
        worldListWidget.setCursor(drawContext);
    }

    static /* synthetic */ void method_76292(WorldListWidget worldListWidget, DrawContext drawContext) {
        worldListWidget.setCursor(drawContext);
    }
}

