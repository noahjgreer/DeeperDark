/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client;

import java.io.File;
import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.ResourceIndex;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class RunArgs.Directories {
    public final File runDir;
    public final File resourcePackDir;
    public final File assetDir;
    public final @Nullable String assetIndex;

    public RunArgs.Directories(File runDir, File resPackDir, File assetDir, @Nullable String assetIndex) {
        this.runDir = runDir;
        this.resourcePackDir = resPackDir;
        this.assetDir = assetDir;
        this.assetIndex = assetIndex;
    }

    public Path getAssetDir() {
        return this.assetIndex == null ? this.assetDir.toPath() : ResourceIndex.buildFileSystem(this.assetDir.toPath(), this.assetIndex);
    }
}
