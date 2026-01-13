/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.NoticeScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.world.CreateWorldScreen
 *  net.minecraft.client.realms.dto.RealmsServer
 *  net.minecraft.client.realms.dto.RealmsSettingDto
 *  net.minecraft.client.realms.dto.RealmsSlot
 *  net.minecraft.client.realms.dto.RealmsWorldOptions
 *  net.minecraft.client.realms.exception.upload.CancelledRealmsUploadException
 *  net.minecraft.client.realms.exception.upload.FailedRealmsUploadException
 *  net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen
 *  net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen
 *  net.minecraft.client.realms.gui.screen.RealmsMainScreen
 *  net.minecraft.client.realms.gui.screen.RealmsWorldCreating
 *  net.minecraft.client.realms.task.WorldCreationTask
 *  net.minecraft.client.realms.util.RealmsUploader
 *  net.minecraft.client.realms.util.UploadProgressTracker
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.nbt.NbtIo
 *  net.minecraft.registry.CombinedDynamicRegistries
 *  net.minecraft.registry.DynamicRegistryManager
 *  net.minecraft.registry.ServerDynamicRegistryType
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.world.level.LevelInfo
 *  net.minecraft.world.level.LevelProperties
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsSettingDto;
import net.minecraft.client.realms.dto.RealmsSlot;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.exception.upload.CancelledRealmsUploadException;
import net.minecraft.client.realms.exception.upload.FailedRealmsUploadException;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.task.WorldCreationTask;
import net.minecraft.client.realms.util.RealmsUploader;
import net.minecraft.client.realms.util.UploadProgressTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class RealmsWorldCreating {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void showCreateWorldScreen(MinecraftClient client, Screen parent, Screen realmsScreen, int slotId, RealmsServer server, @Nullable WorldCreationTask creationTask) {
        CreateWorldScreen.show((MinecraftClient)client, () -> client.setScreen(parent), (screen, dynamicRegistries, levelProperties, dataPackTempDir) -> {
            Path path;
            try {
                path = RealmsWorldCreating.saveTempWorld((CombinedDynamicRegistries)dynamicRegistries, (LevelProperties)levelProperties, (Path)dataPackTempDir);
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to create temporary world folder.");
                client.setScreen((Screen)new RealmsGenericErrorScreen((Text)Text.translatable((String)"mco.create.world.failed"), realmsScreen));
                return true;
            }
            RealmsWorldOptions realmsWorldOptions = RealmsWorldOptions.create((LevelInfo)levelProperties.getLevelInfo(), (String)SharedConstants.getGameVersion().name());
            RealmsSlot realmsSlot = new RealmsSlot(slotId, realmsWorldOptions, List.of(RealmsSettingDto.ofHardcore((boolean)levelProperties.getLevelInfo().isHardcore())));
            RealmsUploader realmsUploader = new RealmsUploader(path, realmsSlot, client.getSession(), realmsServer.id, UploadProgressTracker.create());
            client.setScreenAndRender((Screen)new NoticeScreen(() -> ((RealmsUploader)realmsUploader).cancel(), (Text)Text.translatable((String)"mco.create.world.reset.title"), (Text)Text.empty(), ScreenTexts.CANCEL, false));
            if (creationTask != null) {
                creationTask.run();
            }
            realmsUploader.upload().handleAsync((v, throwable) -> {
                if (throwable != null) {
                    if (throwable instanceof CompletionException) {
                        CompletionException completionException = (CompletionException)throwable;
                        throwable = completionException.getCause();
                    }
                    if (throwable instanceof CancelledRealmsUploadException) {
                        client.setScreenAndRender(realmsScreen);
                    } else {
                        if (throwable instanceof FailedRealmsUploadException) {
                            FailedRealmsUploadException failedRealmsUploadException = (FailedRealmsUploadException)throwable;
                            LOGGER.warn("Failed to create realms world {}", (Object)failedRealmsUploadException.getStatus());
                        } else {
                            LOGGER.warn("Failed to create realms world {}", (Object)throwable.getMessage());
                        }
                        client.setScreenAndRender((Screen)new RealmsGenericErrorScreen((Text)Text.translatable((String)"mco.create.world.failed"), realmsScreen));
                    }
                } else {
                    if (parent instanceof RealmsConfigureWorldScreen) {
                        RealmsConfigureWorldScreen realmsConfigureWorldScreen = (RealmsConfigureWorldScreen)parent;
                        realmsConfigureWorldScreen.fetchServerData(realmsServer.id);
                    }
                    if (creationTask != null) {
                        RealmsMainScreen.play((RealmsServer)server, (Screen)parent, (boolean)true);
                    } else {
                        client.setScreenAndRender(parent);
                    }
                    RealmsMainScreen.resetServerList();
                }
                return null;
            }, (Executor)client);
            return true;
        });
    }

    private static Path saveTempWorld(CombinedDynamicRegistries<ServerDynamicRegistryType> dynamicRegistries, LevelProperties levelProperties, @Nullable Path dataPackTempDir) throws IOException {
        Path path = Files.createTempDirectory("minecraft_realms_world_upload", new FileAttribute[0]);
        if (dataPackTempDir != null) {
            Files.move(dataPackTempDir, path.resolve("datapacks"), new CopyOption[0]);
        }
        NbtCompound nbtCompound = levelProperties.cloneWorldNbt((DynamicRegistryManager)dynamicRegistries.getCombinedRegistryManager(), null);
        NbtCompound nbtCompound2 = new NbtCompound();
        nbtCompound2.put("Data", (NbtElement)nbtCompound);
        Path path2 = Files.createFile(path.resolve("level.dat"), new FileAttribute[0]);
        NbtIo.writeCompressed((NbtCompound)nbtCompound2, (Path)path2);
        return path;
    }
}

