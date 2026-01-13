/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.QuickPlayLogger
 *  net.minecraft.client.QuickPlayLogger$Log
 *  net.minecraft.client.QuickPlayLogger$QuickPlayWorld
 *  net.minecraft.client.QuickPlayLogger$WorldType
 *  net.minecraft.util.Util
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.time.Instant;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.QuickPlayLogger;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class QuickPlayLogger {
    private static final QuickPlayLogger NOOP = new /* Unavailable Anonymous Inner Class!! */;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().create();
    private final Path path;
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable QuickPlayLogger.QuickPlayWorld world;

    QuickPlayLogger(String relativePath) {
        this.path = MinecraftClient.getInstance().runDirectory.toPath().resolve(relativePath);
    }

    public static QuickPlayLogger create(@Nullable String relativePath) {
        if (relativePath == null) {
            return NOOP;
        }
        return new QuickPlayLogger(relativePath);
    }

    public void setWorld(WorldType worldType, String id, String name) {
        this.world = new QuickPlayWorld(worldType, id, name);
    }

    public void save(MinecraftClient client) {
        if (client.interactionManager == null || this.world == null) {
            LOGGER.error("Failed to log session for quickplay. Missing world data or gamemode");
            return;
        }
        Util.getIoWorkerExecutor().execute(() -> {
            try {
                Files.deleteIfExists(this.path);
            }
            catch (IOException iOException) {
                LOGGER.error("Failed to delete quickplay log file {}", (Object)this.path, (Object)iOException);
            }
            Log log = new Log(this.world, Instant.now(), minecraftClient.interactionManager.getCurrentGameMode());
            Codec.list((Codec)Log.CODEC).encodeStart((DynamicOps)JsonOps.INSTANCE, List.of(log)).resultOrPartial(Util.addPrefix((String)"Quick Play: ", arg_0 -> ((Logger)LOGGER).error(arg_0))).ifPresent(json -> {
                try {
                    Files.createDirectories(this.path.getParent(), new FileAttribute[0]);
                    Files.writeString(this.path, (CharSequence)GSON.toJson(json), new OpenOption[0]);
                }
                catch (IOException iOException) {
                    LOGGER.error("Failed to write to quickplay log file {}", (Object)this.path, (Object)iOException);
                }
            });
        });
    }
}

