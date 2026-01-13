/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.world.OptimizeWorldScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.registry.DynamicRegistryManager
 *  net.minecraft.registry.DynamicRegistryManager$Immutable
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.resource.ResourcePackManager
 *  net.minecraft.resource.VanillaDataPackProvider
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.server.SaveLoader
 *  net.minecraft.server.integrated.IntegratedServerLoader
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.SaveProperties
 *  net.minecraft.world.World
 *  net.minecraft.world.level.storage.LevelStorage$Session
 *  net.minecraft.world.updater.WorldUpdater
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.world;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.Objects;
import java.util.function.ToIntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.integrated.IntegratedServerLoader;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.updater.WorldUpdater;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class OptimizeWorldScreen
extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ToIntFunction<RegistryKey<World>> DIMENSION_COLORS = (ToIntFunction)Util.make((Object)new Reference2IntOpenHashMap(), map -> {
        map.put((Object)World.OVERWORLD, -13408734);
        map.put((Object)World.NETHER, -10075085);
        map.put((Object)World.END, -8943531);
        map.defaultReturnValue(-2236963);
    });
    private final BooleanConsumer callback;
    private final WorldUpdater updater;

    public static @Nullable OptimizeWorldScreen create(MinecraftClient client, BooleanConsumer callback, DataFixer dataFixer, LevelStorage.Session storageSession, boolean eraseCache) {
        OptimizeWorldScreen optimizeWorldScreen;
        block8: {
            IntegratedServerLoader integratedServerLoader = client.createIntegratedServerLoader();
            ResourcePackManager resourcePackManager = VanillaDataPackProvider.createManager((LevelStorage.Session)storageSession);
            SaveLoader saveLoader = integratedServerLoader.load(storageSession.readLevelProperties(), false, resourcePackManager);
            try {
                SaveProperties saveProperties = saveLoader.saveProperties();
                DynamicRegistryManager.Immutable immutable = saveLoader.combinedDynamicRegistries().getCombinedRegistryManager();
                storageSession.backupLevelDataFile((DynamicRegistryManager)immutable, saveProperties);
                optimizeWorldScreen = new OptimizeWorldScreen(callback, dataFixer, storageSession, saveProperties, eraseCache, (DynamicRegistryManager)immutable);
                if (saveLoader == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (saveLoader != null) {
                        try {
                            saveLoader.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Exception exception) {
                    LOGGER.warn("Failed to load datapacks, can't optimize world", (Throwable)exception);
                    return null;
                }
            }
            saveLoader.close();
        }
        return optimizeWorldScreen;
    }

    private OptimizeWorldScreen(BooleanConsumer callback, DataFixer dataFixer, LevelStorage.Session storageSession, SaveProperties saveProperties, boolean eraseCache, DynamicRegistryManager registries) {
        super((Text)Text.translatable((String)"optimizeWorld.title", (Object[])new Object[]{saveProperties.getLevelInfo().getLevelName()}));
        this.callback = callback;
        this.updater = new WorldUpdater(storageSession, dataFixer, saveProperties, registries, eraseCache, false);
    }

    protected void init() {
        super.init();
        this.addDrawableChild((Element)ButtonWidget.builder((Text)ScreenTexts.CANCEL, button -> {
            this.updater.cancel();
            this.callback.accept(false);
        }).dimensions(this.width / 2 - 100, this.height / 4 + 150, 200, 20).build());
    }

    public void tick() {
        if (this.updater.isDone()) {
            this.callback.accept(true);
        }
    }

    public void close() {
        this.callback.accept(false);
    }

    public void removed() {
        this.updater.cancel();
        this.updater.close();
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, -1);
        int i = this.width / 2 - 150;
        int j = this.width / 2 + 150;
        int k = this.height / 4 + 100;
        int l = k + 10;
        Text text = this.updater.getStatus();
        int n = this.width / 2;
        Objects.requireNonNull(this.textRenderer);
        context.drawCenteredTextWithShadow(this.textRenderer, text, n, k - 9 - 2, -6250336);
        if (this.updater.getTotalChunkCount() > 0) {
            context.fill(i - 1, k - 1, j + 1, l + 1, -16777216);
            context.drawTextWithShadow(this.textRenderer, (Text)Text.translatable((String)"optimizeWorld.info.converted", (Object[])new Object[]{this.updater.getUpgradedChunkCount()}), i, 40, -6250336);
            MutableText mutableText = Text.translatable((String)"optimizeWorld.info.skipped", (Object[])new Object[]{this.updater.getSkippedChunkCount()});
            Objects.requireNonNull(this.textRenderer);
            context.drawTextWithShadow(this.textRenderer, (Text)mutableText, i, 40 + 9 + 3, -6250336);
            MutableText mutableText2 = Text.translatable((String)"optimizeWorld.info.total", (Object[])new Object[]{this.updater.getTotalChunkCount()});
            Objects.requireNonNull(this.textRenderer);
            context.drawTextWithShadow(this.textRenderer, (Text)mutableText2, i, 40 + (9 + 3) * 2, -6250336);
            int m = 0;
            for (RegistryKey registryKey : this.updater.getWorlds()) {
                int n2 = MathHelper.floor((float)(this.updater.getProgress(registryKey) * (float)(j - i)));
                context.fill(i + m, k, i + m + n2, l, DIMENSION_COLORS.applyAsInt(registryKey));
                m += n2;
            }
            int o = this.updater.getUpgradedChunkCount() + this.updater.getSkippedChunkCount();
            MutableText text2 = Text.translatable((String)"optimizeWorld.progress.counter", (Object[])new Object[]{o, this.updater.getTotalChunkCount()});
            MutableText text22 = Text.translatable((String)"optimizeWorld.progress.percentage", (Object[])new Object[]{MathHelper.floor((float)(this.updater.getProgress() * 100.0f))});
            int n3 = this.width / 2;
            Objects.requireNonNull(this.textRenderer);
            context.drawCenteredTextWithShadow(this.textRenderer, (Text)text2, n3, k + 2 * 9 + 2, -6250336);
            int n4 = this.width / 2;
            int n5 = k + (l - k) / 2;
            Objects.requireNonNull(this.textRenderer);
            context.drawCenteredTextWithShadow(this.textRenderer, (Text)text22, n4, n5 - 9 / 2, -6250336);
        }
    }
}

