/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GLX
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.hud.debug.DebugHudEntry
 *  net.minecraft.client.gui.hud.debug.DebugHudLines
 *  net.minecraft.client.gui.hud.debug.SystemSpecsDebugHudEntry
 *  net.minecraft.util.Identifier
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.WorldChunk
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.hud.debug;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SystemSpecsDebugHudEntry
implements DebugHudEntry {
    private static final Identifier SECTION_ID = Identifier.ofVanilla((String)"system");

    public void render(DebugHudLines lines, @Nullable World world, @Nullable WorldChunk clientChunk, @Nullable WorldChunk chunk) {
        GpuDevice gpuDevice = RenderSystem.getDevice();
        lines.addLinesToSection(SECTION_ID, List.of(String.format(Locale.ROOT, "Java: %s", System.getProperty("java.version")), String.format(Locale.ROOT, "CPU: %s", GLX._getCpuInfo()), String.format(Locale.ROOT, "Display: %dx%d (%s)", MinecraftClient.getInstance().getWindow().getFramebufferWidth(), MinecraftClient.getInstance().getWindow().getFramebufferHeight(), gpuDevice.getVendor()), gpuDevice.getRenderer(), String.format(Locale.ROOT, "%s %s", gpuDevice.getBackendName(), gpuDevice.getVersion())));
    }

    public boolean canShow(boolean reducedDebugInfo) {
        return true;
    }
}

