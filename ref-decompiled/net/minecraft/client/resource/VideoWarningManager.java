/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.resource.VideoWarningManager
 *  net.minecraft.client.resource.VideoWarningManager$WarningPatternLoader
 *  net.minecraft.resource.ResourceManager
 *  net.minecraft.resource.SinglePreparationResourceReloader
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.StrictJsonParser
 *  net.minecraft.util.profiler.Profiler
 *  net.minecraft.util.profiler.ScopedProfiler
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.resource;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.VideoWarningManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.ScopedProfiler;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class VideoWarningManager
extends SinglePreparationResourceReloader<WarningPatternLoader> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Identifier GPU_WARNLIST_ID = Identifier.ofVanilla((String)"gpu_warnlist.json");
    private ImmutableMap<String, String> warnings = ImmutableMap.of();
    private boolean warningScheduled;
    private boolean warned;

    public boolean hasWarning() {
        return !this.warnings.isEmpty();
    }

    public boolean canWarn() {
        return this.hasWarning() && !this.warned;
    }

    public void scheduleWarning() {
        this.warningScheduled = true;
    }

    public void acceptAfterWarnings() {
        this.warned = true;
    }

    public boolean shouldWarn() {
        return this.warningScheduled && !this.warned;
    }

    public void reset() {
        this.warningScheduled = false;
        this.warned = false;
    }

    public @Nullable String getRendererWarning() {
        return (String)this.warnings.get((Object)"renderer");
    }

    public @Nullable String getVersionWarning() {
        return (String)this.warnings.get((Object)"version");
    }

    public @Nullable String getVendorWarning() {
        return (String)this.warnings.get((Object)"vendor");
    }

    public @Nullable String getWarningsAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        this.warnings.forEach((key, value) -> stringBuilder.append((String)key).append(": ").append((String)value));
        return stringBuilder.isEmpty() ? null : stringBuilder.toString();
    }

    protected WarningPatternLoader prepare(ResourceManager resourceManager, Profiler profiler) {
        ArrayList list = Lists.newArrayList();
        ArrayList list2 = Lists.newArrayList();
        ArrayList list3 = Lists.newArrayList();
        JsonObject jsonObject = VideoWarningManager.loadWarnlist((ResourceManager)resourceManager, (Profiler)profiler);
        if (jsonObject != null) {
            try (ScopedProfiler scopedProfiler = profiler.scoped("compile_regex");){
                VideoWarningManager.compilePatterns((JsonArray)jsonObject.getAsJsonArray("renderer"), (List)list);
                VideoWarningManager.compilePatterns((JsonArray)jsonObject.getAsJsonArray("version"), (List)list2);
                VideoWarningManager.compilePatterns((JsonArray)jsonObject.getAsJsonArray("vendor"), (List)list3);
            }
        }
        return new WarningPatternLoader((List)list, (List)list2, (List)list3);
    }

    protected void apply(WarningPatternLoader warningPatternLoader, ResourceManager resourceManager, Profiler profiler) {
        this.warnings = warningPatternLoader.buildWarnings();
    }

    private static void compilePatterns(JsonArray array, List<Pattern> patterns) {
        array.forEach(json -> patterns.add(Pattern.compile(json.getAsString(), 2)));
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private static @Nullable JsonObject loadWarnlist(ResourceManager resourceManager, Profiler profiler) {
        try (ScopedProfiler scopedProfiler = profiler.scoped("parse_json");){
            JsonObject jsonObject;
            block14: {
                BufferedReader reader = resourceManager.openAsReader(GPU_WARNLIST_ID);
                try {
                    jsonObject = StrictJsonParser.parse((Reader)reader).getAsJsonObject();
                    if (reader == null) break block14;
                }
                catch (Throwable throwable) {
                    if (reader != null) {
                        try {
                            ((Reader)reader).close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                ((Reader)reader).close();
            }
            return jsonObject;
        }
        catch (JsonSyntaxException | IOException exception) {
            LOGGER.warn("Failed to load GPU warnlist", exception);
            return null;
        }
    }

    protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
        return this.prepare(manager, profiler);
    }
}

