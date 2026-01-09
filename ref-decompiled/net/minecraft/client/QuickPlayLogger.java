package net.minecraft.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class QuickPlayLogger {
   private static final QuickPlayLogger NOOP = new QuickPlayLogger("") {
      public void save(MinecraftClient client) {
      }

      public void setWorld(WorldType worldType, String id, String name) {
      }
   };
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = (new GsonBuilder()).create();
   private final Path path;
   @Nullable
   private QuickPlayWorld world;

   QuickPlayLogger(String relativePath) {
      this.path = MinecraftClient.getInstance().runDirectory.toPath().resolve(relativePath);
   }

   public static QuickPlayLogger create(@Nullable String relativePath) {
      return relativePath == null ? NOOP : new QuickPlayLogger(relativePath);
   }

   public void setWorld(WorldType worldType, String id, String name) {
      this.world = new QuickPlayWorld(worldType, id, name);
   }

   public void save(MinecraftClient client) {
      if (client.interactionManager != null && this.world != null) {
         Util.getIoWorkerExecutor().execute(() -> {
            try {
               Files.deleteIfExists(this.path);
            } catch (IOException var3) {
               LOGGER.error("Failed to delete quickplay log file {}", this.path, var3);
            }

            Log log = new Log(this.world, Instant.now(), client.interactionManager.getCurrentGameMode());
            DataResult var10000 = Codec.list(QuickPlayLogger.Log.CODEC).encodeStart(JsonOps.INSTANCE, List.of(log));
            Logger var10002 = LOGGER;
            Objects.requireNonNull(var10002);
            var10000.resultOrPartial(Util.addPrefix("Quick Play: ", var10002::error)).ifPresent((json) -> {
               try {
                  Files.createDirectories(this.path.getParent());
                  Files.writeString(this.path, GSON.toJson(json));
               } catch (IOException var3) {
                  LOGGER.error("Failed to write to quickplay log file {}", this.path, var3);
               }

            });
         });
      } else {
         LOGGER.error("Failed to log session for quickplay. Missing world data or gamemode");
      }
   }

   @Environment(EnvType.CLIENT)
   private static record QuickPlayWorld(WorldType type, String id, String name) {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(QuickPlayLogger.WorldType.CODEC.fieldOf("type").forGetter(QuickPlayWorld::type), Codecs.ESCAPED_STRING.fieldOf("id").forGetter(QuickPlayWorld::id), Codec.STRING.fieldOf("name").forGetter(QuickPlayWorld::name)).apply(instance, QuickPlayWorld::new);
      });

      QuickPlayWorld(WorldType worldType, String string, String string2) {
         this.type = worldType;
         this.id = string;
         this.name = string2;
      }

      public WorldType type() {
         return this.type;
      }

      public String id() {
         return this.id;
      }

      public String name() {
         return this.name;
      }
   }

   @Environment(EnvType.CLIENT)
   public static enum WorldType implements StringIdentifiable {
      SINGLEPLAYER("singleplayer"),
      MULTIPLAYER("multiplayer"),
      REALMS("realms");

      static final Codec CODEC = StringIdentifiable.createCodec(WorldType::values);
      private final String id;

      private WorldType(final String id) {
         this.id = id;
      }

      public String asString() {
         return this.id;
      }

      // $FF: synthetic method
      private static WorldType[] method_51271() {
         return new WorldType[]{SINGLEPLAYER, MULTIPLAYER, REALMS};
      }
   }

   @Environment(EnvType.CLIENT)
   private static record Log(QuickPlayWorld quickPlayWorld, Instant lastPlayedTime, GameMode gameMode) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(QuickPlayLogger.QuickPlayWorld.CODEC.forGetter(Log::quickPlayWorld), Codecs.INSTANT.fieldOf("lastPlayedTime").forGetter(Log::lastPlayedTime), GameMode.CODEC.fieldOf("gamemode").forGetter(Log::gameMode)).apply(instance, Log::new);
      });

      Log(QuickPlayWorld quickPlayWorld, Instant instant, GameMode gameMode) {
         this.quickPlayWorld = quickPlayWorld;
         this.lastPlayedTime = instant;
         this.gameMode = gameMode;
      }

      public QuickPlayWorld quickPlayWorld() {
         return this.quickPlayWorld;
      }

      public Instant lastPlayedTime() {
         return this.lastPlayedTime;
      }

      public GameMode gameMode() {
         return this.gameMode;
      }
   }
}
