package net.minecraft.client.session.telemetry;

import com.mojang.authlib.minecraft.TelemetryPropertyContainer;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Uuids;
import net.minecraft.util.dynamic.Codecs;

@Environment(EnvType.CLIENT)
public record TelemetryEventProperty(String id, String exportKey, Codec codec, PropertyExporter exporter) {
   private static final DateTimeFormatter DATE_TIME_FORMATTER;
   public static final TelemetryEventProperty USER_ID;
   public static final TelemetryEventProperty CLIENT_ID;
   public static final TelemetryEventProperty MINECRAFT_SESSION_ID;
   public static final TelemetryEventProperty GAME_VERSION;
   public static final TelemetryEventProperty OPERATING_SYSTEM;
   public static final TelemetryEventProperty PLATFORM;
   public static final TelemetryEventProperty CLIENT_MODDED;
   public static final TelemetryEventProperty LAUNCHER_NAME;
   public static final TelemetryEventProperty WORLD_SESSION_ID;
   public static final TelemetryEventProperty SERVER_MODDED;
   public static final TelemetryEventProperty SERVER_TYPE;
   public static final TelemetryEventProperty OPT_IN;
   public static final TelemetryEventProperty EVENT_TIMESTAMP_UTC;
   public static final TelemetryEventProperty GAME_MODE;
   public static final TelemetryEventProperty REALMS_MAP_CONTENT;
   public static final TelemetryEventProperty SECONDS_SINCE_LOAD;
   public static final TelemetryEventProperty TICKS_SINCE_LOAD;
   public static final TelemetryEventProperty FRAME_RATE_SAMPLES;
   public static final TelemetryEventProperty RENDER_TIME_SAMPLES;
   public static final TelemetryEventProperty USED_MEMORY_SAMPLES;
   public static final TelemetryEventProperty NUMBER_OF_SAMPLES;
   public static final TelemetryEventProperty RENDER_DISTANCE;
   public static final TelemetryEventProperty DEDICATED_MEMORY_KB;
   public static final TelemetryEventProperty WORLD_LOAD_TIME_MS;
   public static final TelemetryEventProperty NEW_WORLD;
   public static final TelemetryEventProperty LOAD_TIME_TOTAL_TIME_MS;
   public static final TelemetryEventProperty LOAD_TIME_PRE_WINDOW_MS;
   public static final TelemetryEventProperty LOAD_TIME_BOOTSTRAP_MS;
   public static final TelemetryEventProperty LOAD_TIME_LOADING_OVERLAY_MS;
   public static final TelemetryEventProperty ADVANCEMENT_ID;
   public static final TelemetryEventProperty ADVANCEMENT_GAME_TIME;

   public TelemetryEventProperty(String string, String string2, Codec codec, PropertyExporter propertyExporter) {
      this.id = string;
      this.exportKey = string2;
      this.codec = codec;
      this.exporter = propertyExporter;
   }

   public static TelemetryEventProperty of(String id, String exportKey, Codec codec, PropertyExporter exporter) {
      return new TelemetryEventProperty(id, exportKey, codec, exporter);
   }

   public static TelemetryEventProperty ofBoolean(String id, String exportKey) {
      return of(id, exportKey, Codec.BOOL, TelemetryPropertyContainer::addProperty);
   }

   public static TelemetryEventProperty ofString(String id, String exportKey) {
      return of(id, exportKey, Codec.STRING, TelemetryPropertyContainer::addProperty);
   }

   public static TelemetryEventProperty ofInteger(String id, String exportKey) {
      return of(id, exportKey, Codec.INT, TelemetryPropertyContainer::addProperty);
   }

   public static TelemetryEventProperty ofLong(String id, String exportKey) {
      return of(id, exportKey, Codec.LONG, TelemetryPropertyContainer::addProperty);
   }

   public static TelemetryEventProperty ofUuid(String id, String exportKey) {
      return of(id, exportKey, Uuids.STRING_CODEC, (container, key, value) -> {
         container.addProperty(key, value.toString());
      });
   }

   public static TelemetryEventProperty ofTimeMeasurement(String id, String exportKey) {
      return of(id, exportKey, GameLoadTimeEvent.Measurement.CODEC, (container, key, value) -> {
         container.addProperty(key, value.millis());
      });
   }

   public static TelemetryEventProperty ofLongList(String id, String exportKey) {
      return of(id, exportKey, Codec.LONG.listOf().xmap(LongArrayList::new, Function.identity()), (container, key, value) -> {
         container.addProperty(key, (String)value.longStream().mapToObj(String::valueOf).collect(Collectors.joining(";")));
      });
   }

   public void addTo(PropertyMap map, TelemetryPropertyContainer container) {
      Object object = map.get(this);
      if (object != null) {
         this.exporter.apply(container, this.exportKey, object);
      } else {
         container.addNullProperty(this.exportKey);
      }

   }

   public MutableText getTitle() {
      return Text.translatable("telemetry.property." + this.id + ".title");
   }

   public String toString() {
      return "TelemetryProperty[" + this.id + "]";
   }

   public String id() {
      return this.id;
   }

   public String exportKey() {
      return this.exportKey;
   }

   public Codec codec() {
      return this.codec;
   }

   public PropertyExporter exporter() {
      return this.exporter;
   }

   static {
      DATE_TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));
      USER_ID = ofString("user_id", "userId");
      CLIENT_ID = ofString("client_id", "clientId");
      MINECRAFT_SESSION_ID = ofUuid("minecraft_session_id", "deviceSessionId");
      GAME_VERSION = ofString("game_version", "buildDisplayName");
      OPERATING_SYSTEM = ofString("operating_system", "buildPlatform");
      PLATFORM = ofString("platform", "platform");
      CLIENT_MODDED = ofBoolean("client_modded", "clientModded");
      LAUNCHER_NAME = ofString("launcher_name", "launcherName");
      WORLD_SESSION_ID = ofUuid("world_session_id", "worldSessionId");
      SERVER_MODDED = ofBoolean("server_modded", "serverModded");
      SERVER_TYPE = of("server_type", "serverType", TelemetryEventProperty.ServerType.CODEC, (container, exportKey, value) -> {
         container.addProperty(exportKey, value.asString());
      });
      OPT_IN = ofBoolean("opt_in", "isOptional");
      EVENT_TIMESTAMP_UTC = of("event_timestamp_utc", "eventTimestampUtc", Codecs.INSTANT, (container, exportKey, value) -> {
         container.addProperty(exportKey, DATE_TIME_FORMATTER.format(value));
      });
      GAME_MODE = of("game_mode", "playerGameMode", TelemetryEventProperty.GameMode.CODEC, (container, exportKey, value) -> {
         container.addProperty(exportKey, value.getRawId());
      });
      REALMS_MAP_CONTENT = ofString("realms_map_content", "realmsMapContent");
      SECONDS_SINCE_LOAD = ofInteger("seconds_since_load", "secondsSinceLoad");
      TICKS_SINCE_LOAD = ofInteger("ticks_since_load", "ticksSinceLoad");
      FRAME_RATE_SAMPLES = ofLongList("frame_rate_samples", "serializedFpsSamples");
      RENDER_TIME_SAMPLES = ofLongList("render_time_samples", "serializedRenderTimeSamples");
      USED_MEMORY_SAMPLES = ofLongList("used_memory_samples", "serializedUsedMemoryKbSamples");
      NUMBER_OF_SAMPLES = ofInteger("number_of_samples", "numSamples");
      RENDER_DISTANCE = ofInteger("render_distance", "renderDistance");
      DEDICATED_MEMORY_KB = ofInteger("dedicated_memory_kb", "dedicatedMemoryKb");
      WORLD_LOAD_TIME_MS = ofInteger("world_load_time_ms", "worldLoadTimeMs");
      NEW_WORLD = ofBoolean("new_world", "newWorld");
      LOAD_TIME_TOTAL_TIME_MS = ofTimeMeasurement("load_time_total_time_ms", "loadTimeTotalTimeMs");
      LOAD_TIME_PRE_WINDOW_MS = ofTimeMeasurement("load_time_pre_window_ms", "loadTimePreWindowMs");
      LOAD_TIME_BOOTSTRAP_MS = ofTimeMeasurement("load_time_bootstrap_ms", "loadTimeBootstrapMs");
      LOAD_TIME_LOADING_OVERLAY_MS = ofTimeMeasurement("load_time_loading_overlay_ms", "loadTimeLoadingOverlayMs");
      ADVANCEMENT_ID = ofString("advancement_id", "advancementId");
      ADVANCEMENT_GAME_TIME = ofLong("advancement_game_time", "advancementGameTime");
   }

   @Environment(EnvType.CLIENT)
   public interface PropertyExporter {
      void apply(TelemetryPropertyContainer container, String key, Object value);
   }

   @Environment(EnvType.CLIENT)
   public static enum GameMode implements StringIdentifiable {
      SURVIVAL("survival", 0),
      CREATIVE("creative", 1),
      ADVENTURE("adventure", 2),
      SPECTATOR("spectator", 6),
      HARDCORE("hardcore", 99);

      public static final Codec CODEC = StringIdentifiable.createCodec(GameMode::values);
      private final String id;
      private final int rawId;

      private GameMode(final String id, final int rawId) {
         this.id = id;
         this.rawId = rawId;
      }

      public int getRawId() {
         return this.rawId;
      }

      public String asString() {
         return this.id;
      }

      // $FF: synthetic method
      private static GameMode[] method_47757() {
         return new GameMode[]{SURVIVAL, CREATIVE, ADVENTURE, SPECTATOR, HARDCORE};
      }
   }

   @Environment(EnvType.CLIENT)
   public static enum ServerType implements StringIdentifiable {
      REALM("realm"),
      LOCAL("local"),
      OTHER("server");

      public static final Codec CODEC = StringIdentifiable.createCodec(ServerType::values);
      private final String id;

      private ServerType(final String id) {
         this.id = id;
      }

      public String asString() {
         return this.id;
      }

      // $FF: synthetic method
      private static ServerType[] method_47758() {
         return new ServerType[]{REALM, LOCAL, OTHER};
      }
   }
}
