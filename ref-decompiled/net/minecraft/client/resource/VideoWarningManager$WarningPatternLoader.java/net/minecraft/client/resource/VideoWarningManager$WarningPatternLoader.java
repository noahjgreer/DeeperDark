/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
protected static final class VideoWarningManager.WarningPatternLoader {
    private final List<Pattern> rendererPatterns;
    private final List<Pattern> versionPatterns;
    private final List<Pattern> vendorPatterns;

    VideoWarningManager.WarningPatternLoader(List<Pattern> rendererPatterns, List<Pattern> versionPatterns, List<Pattern> vendorPatterns) {
        this.rendererPatterns = rendererPatterns;
        this.versionPatterns = versionPatterns;
        this.vendorPatterns = vendorPatterns;
    }

    private static String buildWarning(List<Pattern> warningPattern, String info) {
        ArrayList list = Lists.newArrayList();
        for (Pattern pattern : warningPattern) {
            Matcher matcher = pattern.matcher(info);
            while (matcher.find()) {
                list.add(matcher.group());
            }
        }
        return String.join((CharSequence)", ", list);
    }

    ImmutableMap<String, String> buildWarnings() {
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        GpuDevice gpuDevice = RenderSystem.getDevice();
        if (gpuDevice.getBackendName().equals("OpenGL")) {
            String string3;
            String string2;
            String string = VideoWarningManager.WarningPatternLoader.buildWarning(this.rendererPatterns, gpuDevice.getRenderer());
            if (!string.isEmpty()) {
                builder.put((Object)"renderer", (Object)string);
            }
            if (!(string2 = VideoWarningManager.WarningPatternLoader.buildWarning(this.versionPatterns, gpuDevice.getVersion())).isEmpty()) {
                builder.put((Object)"version", (Object)string2);
            }
            if (!(string3 = VideoWarningManager.WarningPatternLoader.buildWarning(this.vendorPatterns, gpuDevice.getVendor())).isEmpty()) {
                builder.put((Object)"vendor", (Object)string3);
            }
        }
        return builder.build();
    }
}
