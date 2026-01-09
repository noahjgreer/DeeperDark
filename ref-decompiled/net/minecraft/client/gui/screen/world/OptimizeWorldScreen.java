package net.minecraft.client.gui.screen.world;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.ToIntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
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
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class OptimizeWorldScreen extends Screen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ToIntFunction DIMENSION_COLORS = (ToIntFunction)Util.make(new Reference2IntOpenHashMap(), (map) -> {
      map.put(World.OVERWORLD, -13408734);
      map.put(World.NETHER, -10075085);
      map.put(World.END, -8943531);
      map.defaultReturnValue(-2236963);
   });
   private final BooleanConsumer callback;
   private final WorldUpdater updater;

   @Nullable
   public static OptimizeWorldScreen create(MinecraftClient client, BooleanConsumer callback, DataFixer dataFixer, LevelStorage.Session storageSession, boolean eraseCache) {
      try {
         IntegratedServerLoader integratedServerLoader = client.createIntegratedServerLoader();
         ResourcePackManager resourcePackManager = VanillaDataPackProvider.createManager(storageSession);
         SaveLoader saveLoader = integratedServerLoader.load(storageSession.readLevelProperties(), false, resourcePackManager);

         OptimizeWorldScreen var10;
         try {
            SaveProperties saveProperties = saveLoader.saveProperties();
            DynamicRegistryManager.Immutable immutable = saveLoader.combinedDynamicRegistries().getCombinedRegistryManager();
            storageSession.backupLevelDataFile(immutable, saveProperties);
            var10 = new OptimizeWorldScreen(callback, dataFixer, storageSession, saveProperties, eraseCache, immutable);
         } catch (Throwable var12) {
            if (saveLoader != null) {
               try {
                  saveLoader.close();
               } catch (Throwable var11) {
                  var12.addSuppressed(var11);
               }
            }

            throw var12;
         }

         if (saveLoader != null) {
            saveLoader.close();
         }

         return var10;
      } catch (Exception var13) {
         LOGGER.warn("Failed to load datapacks, can't optimize world", var13);
         return null;
      }
   }

   private OptimizeWorldScreen(BooleanConsumer callback, DataFixer dataFixer, LevelStorage.Session storageSession, SaveProperties saveProperties, boolean eraseCache, DynamicRegistryManager registries) {
      super(Text.translatable("optimizeWorld.title", saveProperties.getLevelInfo().getLevelName()));
      this.callback = callback;
      this.updater = new WorldUpdater(storageSession, dataFixer, saveProperties, registries, eraseCache, false);
   }

   protected void init() {
      super.init();
      this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, (button) -> {
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
      context.drawCenteredTextWithShadow(this.textRenderer, (Text)this.title, this.width / 2, 20, -1);
      int i = this.width / 2 - 150;
      int j = this.width / 2 + 150;
      int k = this.height / 4 + 100;
      int l = k + 10;
      TextRenderer var10001 = this.textRenderer;
      Text var10002 = this.updater.getStatus();
      int var10003 = this.width / 2;
      Objects.requireNonNull(this.textRenderer);
      context.drawCenteredTextWithShadow(var10001, var10002, var10003, k - 9 - 2, -6250336);
      if (this.updater.getTotalChunkCount() > 0) {
         context.fill(i - 1, k - 1, j + 1, l + 1, -16777216);
         context.drawTextWithShadow(this.textRenderer, (Text)Text.translatable("optimizeWorld.info.converted", this.updater.getUpgradedChunkCount()), i, 40, -6250336);
         var10001 = this.textRenderer;
         MutableText var16 = Text.translatable("optimizeWorld.info.skipped", this.updater.getSkippedChunkCount());
         Objects.requireNonNull(this.textRenderer);
         context.drawTextWithShadow(var10001, (Text)var16, i, 40 + 9 + 3, -6250336);
         var10001 = this.textRenderer;
         var16 = Text.translatable("optimizeWorld.info.total", this.updater.getTotalChunkCount());
         Objects.requireNonNull(this.textRenderer);
         context.drawTextWithShadow(var10001, (Text)var16, i, 40 + (9 + 3) * 2, -6250336);
         int m = 0;

         int n;
         for(Iterator var10 = this.updater.getWorlds().iterator(); var10.hasNext(); m += n) {
            RegistryKey registryKey = (RegistryKey)var10.next();
            n = MathHelper.floor(this.updater.getProgress(registryKey) * (float)(j - i));
            context.fill(i + m, k, i + m + n, l, DIMENSION_COLORS.applyAsInt(registryKey));
         }

         int o = this.updater.getUpgradedChunkCount() + this.updater.getSkippedChunkCount();
         Text text = Text.translatable("optimizeWorld.progress.counter", o, this.updater.getTotalChunkCount());
         Text text2 = Text.translatable("optimizeWorld.progress.percentage", MathHelper.floor(this.updater.getProgress() * 100.0F));
         var10001 = this.textRenderer;
         var10003 = this.width / 2;
         Objects.requireNonNull(this.textRenderer);
         context.drawCenteredTextWithShadow(var10001, (Text)text, var10003, k + 2 * 9 + 2, -6250336);
         var10001 = this.textRenderer;
         var10003 = this.width / 2;
         int var10004 = k + (l - k) / 2;
         Objects.requireNonNull(this.textRenderer);
         context.drawCenteredTextWithShadow(var10001, (Text)text2, var10003, var10004 - 9 / 2, -6250336);
      }

   }
}
