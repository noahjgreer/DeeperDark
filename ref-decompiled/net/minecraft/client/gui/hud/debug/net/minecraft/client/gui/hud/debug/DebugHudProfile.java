/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.FileUtils
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.hud.debug;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.File;
import java.io.IOException;
import java.lang.runtime.SwitchBootstraps;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudEntries;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudEntryVisibility;
import net.minecraft.client.gui.hud.debug.DebugProfileType;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.StrictJsonParser;
import org.apache.commons.io.FileUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class DebugHudProfile {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int field_63526 = 4649;
    private Map<Identifier, DebugHudEntryVisibility> visibilityMap;
    private final List<Identifier> visibleEntries = new ArrayList<Identifier>();
    private boolean f3Enabled = false;
    private @Nullable DebugProfileType type;
    private final File file;
    private long version;
    private final Codec<Serialization> codec;

    public DebugHudProfile(File file) {
        this.file = new File(file, "debug-profile.json");
        this.codec = DataFixTypes.DEBUG_PROFILE.createDataFixingCodec(Serialization.CODEC, MinecraftClient.getInstance().getDataFixer(), 4649);
        this.readProfileFile();
    }

    public void readProfileFile() {
        try {
            if (!this.file.isFile()) {
                this.setToDefault();
                this.updateVisibleEntries();
                return;
            }
            Dynamic dynamic = new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)StrictJsonParser.parse(FileUtils.readFileToString((File)this.file, (Charset)StandardCharsets.UTF_8)));
            Serialization serialization = (Serialization)this.codec.parse(dynamic).getOrThrow(error -> new IOException("Could not parse debug profile JSON: " + error));
            if (serialization.profile().isPresent()) {
                this.setProfileType(serialization.profile().get());
            } else {
                this.visibilityMap = new HashMap<Identifier, DebugHudEntryVisibility>();
                if (serialization.custom().isPresent()) {
                    this.visibilityMap.putAll(serialization.custom().get());
                }
                this.type = null;
            }
        }
        catch (JsonSyntaxException | IOException exception) {
            LOGGER.error("Couldn't read debug profile file {}, resetting to default", (Object)this.file, (Object)exception);
            this.setToDefault();
            this.saveProfileFile();
        }
        this.updateVisibleEntries();
    }

    public void setProfileType(DebugProfileType type) {
        this.type = type;
        Map<Identifier, DebugHudEntryVisibility> map = DebugHudEntries.PROFILES.get(type);
        this.visibilityMap = new HashMap<Identifier, DebugHudEntryVisibility>(map);
        this.updateVisibleEntries();
    }

    private void setToDefault() {
        this.type = DebugProfileType.DEFAULT;
        this.visibilityMap = new HashMap<Identifier, DebugHudEntryVisibility>(DebugHudEntries.PROFILES.get(DebugProfileType.DEFAULT));
    }

    public DebugHudEntryVisibility getVisibility(Identifier entryId) {
        DebugHudEntryVisibility debugHudEntryVisibility = this.visibilityMap.get(entryId);
        if (debugHudEntryVisibility == null) {
            return DebugHudEntryVisibility.NEVER;
        }
        return debugHudEntryVisibility;
    }

    public boolean isEntryVisible(Identifier entryId) {
        return this.visibleEntries.contains(entryId);
    }

    public void setEntryVisibility(Identifier entryId, DebugHudEntryVisibility visibility) {
        this.type = null;
        this.visibilityMap.put(entryId, visibility);
        this.updateVisibleEntries();
        this.saveProfileFile();
    }

    public boolean toggleVisibility(Identifier entryId) {
        DebugHudEntryVisibility debugHudEntryVisibility;
        DebugHudEntryVisibility debugHudEntryVisibility2 = debugHudEntryVisibility = this.visibilityMap.get(entryId);
        int n = 0;
        switch (SwitchBootstraps.enumSwitch("enumSwitch", new Object[]{"ALWAYS_ON", "IN_OVERLAY", "NEVER"}, (DebugHudEntryVisibility)debugHudEntryVisibility2, n)) {
            case 0: {
                this.setEntryVisibility(entryId, DebugHudEntryVisibility.NEVER);
                return false;
            }
            case 1: {
                if (this.f3Enabled) {
                    this.setEntryVisibility(entryId, DebugHudEntryVisibility.NEVER);
                    return false;
                }
                this.setEntryVisibility(entryId, DebugHudEntryVisibility.ALWAYS_ON);
                return true;
            }
            case 2: {
                if (this.f3Enabled) {
                    this.setEntryVisibility(entryId, DebugHudEntryVisibility.IN_OVERLAY);
                } else {
                    this.setEntryVisibility(entryId, DebugHudEntryVisibility.ALWAYS_ON);
                }
                return true;
            }
        }
        this.setEntryVisibility(entryId, DebugHudEntryVisibility.ALWAYS_ON);
        return true;
    }

    public Collection<Identifier> getVisibleEntries() {
        return this.visibleEntries;
    }

    public void toggleF3Enabled() {
        this.setF3Enabled(!this.f3Enabled);
    }

    public void setF3Enabled(boolean f3Enabled) {
        if (this.f3Enabled != f3Enabled) {
            this.f3Enabled = f3Enabled;
            this.updateVisibleEntries();
        }
    }

    public boolean isF3Enabled() {
        return this.f3Enabled;
    }

    public void updateVisibleEntries() {
        this.visibleEntries.clear();
        boolean bl = MinecraftClient.getInstance().hasReducedDebugInfo();
        for (Map.Entry<Identifier, DebugHudEntryVisibility> entry : this.visibilityMap.entrySet()) {
            DebugHudEntry debugHudEntry;
            if (entry.getValue() != DebugHudEntryVisibility.ALWAYS_ON && (!this.f3Enabled || entry.getValue() != DebugHudEntryVisibility.IN_OVERLAY) || (debugHudEntry = DebugHudEntries.get(entry.getKey())) == null || !debugHudEntry.canShow(bl)) continue;
            this.visibleEntries.add(entry.getKey());
        }
        this.visibleEntries.sort(Identifier::compareTo);
        ++this.version;
    }

    public long getVersion() {
        return this.version;
    }

    public boolean profileTypeMatches(DebugProfileType type) {
        return this.type == type;
    }

    public void saveProfileFile() {
        Serialization serialization = new Serialization(Optional.ofNullable(this.type), this.type == null ? Optional.of(this.visibilityMap) : Optional.empty());
        try {
            FileUtils.writeStringToFile((File)this.file, (String)((JsonElement)this.codec.encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)serialization).getOrThrow()).toString(), (Charset)StandardCharsets.UTF_8);
        }
        catch (IOException iOException) {
            LOGGER.error("Failed to save debug profile file {}", (Object)this.file, (Object)iOException);
        }
    }

    @Environment(value=EnvType.CLIENT)
    record Serialization(Optional<DebugProfileType> profile, Optional<Map<Identifier, DebugHudEntryVisibility>> custom) {
        private static final Codec<Map<Identifier, DebugHudEntryVisibility>> VISIBILITY_MAP_CODEC = Codec.unboundedMap(Identifier.CODEC, DebugHudEntryVisibility.CODEC);
        public static final Codec<Serialization> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)DebugProfileType.CODEC.optionalFieldOf("profile").forGetter(Serialization::profile), (App)VISIBILITY_MAP_CODEC.optionalFieldOf("custom").forGetter(Serialization::custom)).apply((Applicative)instance, Serialization::new));
    }
}
