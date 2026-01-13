/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.TelemetryPropertyContainer
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.session.telemetry.GameLoadTimeEvent$Measurement
 *  net.minecraft.client.session.telemetry.PropertyMap
 *  net.minecraft.client.session.telemetry.TelemetryEventProperty
 *  net.minecraft.client.session.telemetry.TelemetryEventProperty$GameMode
 *  net.minecraft.client.session.telemetry.TelemetryEventProperty$PropertyExporter
 *  net.minecraft.client.session.telemetry.TelemetryEventProperty$ServerType
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Uuids
 *  net.minecraft.util.dynamic.Codecs
 */
package net.minecraft.client.session.telemetry;

import com.mojang.authlib.minecraft.TelemetryPropertyContainer;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.telemetry.GameLoadTimeEvent;
import net.minecraft.client.session.telemetry.PropertyMap;
import net.minecraft.client.session.telemetry.TelemetryEventProperty;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Uuids;
import net.minecraft.util.dynamic.Codecs;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public record TelemetryEventProperty<T>(String id, String exportKey, Codec<T> codec, PropertyExporter<T> exporter) {
    private final String id;
    private final String exportKey;
    private final Codec<T> codec;
    private final PropertyExporter<T> exporter;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));
    public static final TelemetryEventProperty<String> USER_ID = TelemetryEventProperty.ofString((String)"user_id", (String)"userId");
    public static final TelemetryEventProperty<String> CLIENT_ID = TelemetryEventProperty.ofString((String)"client_id", (String)"clientId");
    public static final TelemetryEventProperty<UUID> MINECRAFT_SESSION_ID = TelemetryEventProperty.ofUuid((String)"minecraft_session_id", (String)"deviceSessionId");
    public static final TelemetryEventProperty<String> GAME_VERSION = TelemetryEventProperty.ofString((String)"game_version", (String)"buildDisplayName");
    public static final TelemetryEventProperty<String> OPERATING_SYSTEM = TelemetryEventProperty.ofString((String)"operating_system", (String)"buildPlatform");
    public static final TelemetryEventProperty<String> PLATFORM = TelemetryEventProperty.ofString((String)"platform", (String)"platform");
    public static final TelemetryEventProperty<Boolean> CLIENT_MODDED = TelemetryEventProperty.ofBoolean((String)"client_modded", (String)"clientModded");
    public static final TelemetryEventProperty<String> LAUNCHER_NAME = TelemetryEventProperty.ofString((String)"launcher_name", (String)"launcherName");
    public static final TelemetryEventProperty<UUID> WORLD_SESSION_ID = TelemetryEventProperty.ofUuid((String)"world_session_id", (String)"worldSessionId");
    public static final TelemetryEventProperty<Boolean> SERVER_MODDED = TelemetryEventProperty.ofBoolean((String)"server_modded", (String)"serverModded");
    public static final TelemetryEventProperty<ServerType> SERVER_TYPE = TelemetryEventProperty.of((String)"server_type", (String)"serverType", (Codec)ServerType.CODEC, (container, exportKey, value) -> container.addProperty(exportKey, value.asString()));
    public static final TelemetryEventProperty<Boolean> OPT_IN = TelemetryEventProperty.ofBoolean((String)"opt_in", (String)"isOptional");
    public static final TelemetryEventProperty<Instant> EVENT_TIMESTAMP_UTC = TelemetryEventProperty.of((String)"event_timestamp_utc", (String)"eventTimestampUtc", (Codec)Codecs.INSTANT, (container, exportKey, value) -> container.addProperty(exportKey, DATE_TIME_FORMATTER.format((TemporalAccessor)value)));
    public static final TelemetryEventProperty<GameMode> GAME_MODE = TelemetryEventProperty.of((String)"game_mode", (String)"playerGameMode", (Codec)GameMode.CODEC, (container, exportKey, value) -> container.addProperty(exportKey, value.getRawId()));
    public static final TelemetryEventProperty<String> REALMS_MAP_CONTENT = TelemetryEventProperty.ofString((String)"realms_map_content", (String)"realmsMapContent");
    public static final TelemetryEventProperty<Integer> SECONDS_SINCE_LOAD = TelemetryEventProperty.ofInteger((String)"seconds_since_load", (String)"secondsSinceLoad");
    public static final TelemetryEventProperty<Integer> TICKS_SINCE_LOAD = TelemetryEventProperty.ofInteger((String)"ticks_since_load", (String)"ticksSinceLoad");
    public static final TelemetryEventProperty<LongList> FRAME_RATE_SAMPLES = TelemetryEventProperty.ofLongList((String)"frame_rate_samples", (String)"serializedFpsSamples");
    public static final TelemetryEventProperty<LongList> RENDER_TIME_SAMPLES = TelemetryEventProperty.ofLongList((String)"render_time_samples", (String)"serializedRenderTimeSamples");
    public static final TelemetryEventProperty<LongList> USED_MEMORY_SAMPLES = TelemetryEventProperty.ofLongList((String)"used_memory_samples", (String)"serializedUsedMemoryKbSamples");
    public static final TelemetryEventProperty<Integer> NUMBER_OF_SAMPLES = TelemetryEventProperty.ofInteger((String)"number_of_samples", (String)"numSamples");
    public static final TelemetryEventProperty<Integer> RENDER_DISTANCE = TelemetryEventProperty.ofInteger((String)"render_distance", (String)"renderDistance");
    public static final TelemetryEventProperty<Integer> DEDICATED_MEMORY_KB = TelemetryEventProperty.ofInteger((String)"dedicated_memory_kb", (String)"dedicatedMemoryKb");
    public static final TelemetryEventProperty<Integer> WORLD_LOAD_TIME_MS = TelemetryEventProperty.ofInteger((String)"world_load_time_ms", (String)"worldLoadTimeMs");
    public static final TelemetryEventProperty<Boolean> NEW_WORLD = TelemetryEventProperty.ofBoolean((String)"new_world", (String)"newWorld");
    public static final TelemetryEventProperty<GameLoadTimeEvent.Measurement> LOAD_TIME_TOTAL_TIME_MS = TelemetryEventProperty.ofTimeMeasurement((String)"load_time_total_time_ms", (String)"loadTimeTotalTimeMs");
    public static final TelemetryEventProperty<GameLoadTimeEvent.Measurement> LOAD_TIME_PRE_WINDOW_MS = TelemetryEventProperty.ofTimeMeasurement((String)"load_time_pre_window_ms", (String)"loadTimePreWindowMs");
    public static final TelemetryEventProperty<GameLoadTimeEvent.Measurement> LOAD_TIME_BOOTSTRAP_MS = TelemetryEventProperty.ofTimeMeasurement((String)"load_time_bootstrap_ms", (String)"loadTimeBootstrapMs");
    public static final TelemetryEventProperty<GameLoadTimeEvent.Measurement> LOAD_TIME_LOADING_OVERLAY_MS = TelemetryEventProperty.ofTimeMeasurement((String)"load_time_loading_overlay_ms", (String)"loadTimeLoadingOverlayMs");
    public static final TelemetryEventProperty<String> ADVANCEMENT_ID = TelemetryEventProperty.ofString((String)"advancement_id", (String)"advancementId");
    public static final TelemetryEventProperty<Long> ADVANCEMENT_GAME_TIME = TelemetryEventProperty.ofLong((String)"advancement_game_time", (String)"advancementGameTime");

    public TelemetryEventProperty(String id, String exportKey, Codec<T> codec, PropertyExporter<T> exporter) {
        this.id = id;
        this.exportKey = exportKey;
        this.codec = codec;
        this.exporter = exporter;
    }

    public static <T> TelemetryEventProperty<T> of(String id, String exportKey, Codec<T> codec, PropertyExporter<T> exporter) {
        return new TelemetryEventProperty(id, exportKey, codec, exporter);
    }

    public static TelemetryEventProperty<Boolean> ofBoolean(String id, String exportKey) {
        return TelemetryEventProperty.of((String)id, (String)exportKey, (Codec)Codec.BOOL, TelemetryPropertyContainer::addProperty);
    }

    public static TelemetryEventProperty<String> ofString(String id, String exportKey) {
        return TelemetryEventProperty.of((String)id, (String)exportKey, (Codec)Codec.STRING, TelemetryPropertyContainer::addProperty);
    }

    public static TelemetryEventProperty<Integer> ofInteger(String id, String exportKey) {
        return TelemetryEventProperty.of((String)id, (String)exportKey, (Codec)Codec.INT, TelemetryPropertyContainer::addProperty);
    }

    public static TelemetryEventProperty<Long> ofLong(String id, String exportKey) {
        return TelemetryEventProperty.of((String)id, (String)exportKey, (Codec)Codec.LONG, TelemetryPropertyContainer::addProperty);
    }

    public static TelemetryEventProperty<UUID> ofUuid(String id, String exportKey) {
        return TelemetryEventProperty.of((String)id, (String)exportKey, (Codec)Uuids.STRING_CODEC, (container, key, value) -> container.addProperty(key, value.toString()));
    }

    public static TelemetryEventProperty<GameLoadTimeEvent.Measurement> ofTimeMeasurement(String id, String exportKey) {
        return TelemetryEventProperty.of((String)id, (String)exportKey, (Codec)GameLoadTimeEvent.Measurement.CODEC, (container, key, value) -> container.addProperty(key, value.millis()));
    }

    public static TelemetryEventProperty<LongList> ofLongList(String id, String exportKey) {
        return TelemetryEventProperty.of((String)id, (String)exportKey, (Codec)Codec.LONG.listOf().xmap(LongArrayList::new, Function.identity()), (container, key, value) -> container.addProperty(key, value.longStream().mapToObj(String::valueOf).collect(Collectors.joining(";"))));
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
        return Text.translatable((String)("telemetry.property." + this.id + ".title"));
    }

    @Override
    public String toString() {
        return "TelemetryProperty[" + this.id + "]";
    }

    public String id() {
        return this.id;
    }

    public String exportKey() {
        return this.exportKey;
    }

    public Codec<T> codec() {
        return this.codec;
    }

    public PropertyExporter<T> exporter() {
        return this.exporter;
    }
}

