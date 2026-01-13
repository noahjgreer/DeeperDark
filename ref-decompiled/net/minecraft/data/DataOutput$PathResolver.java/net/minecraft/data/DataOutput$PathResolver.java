/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data;

import java.nio.file.Path;
import net.minecraft.data.DataOutput;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public static class DataOutput.PathResolver {
    private final Path rootPath;
    private final String directoryName;

    DataOutput.PathResolver(DataOutput dataGenerator, DataOutput.OutputType outputType, String directoryName) {
        this.rootPath = dataGenerator.resolvePath(outputType);
        this.directoryName = directoryName;
    }

    public Path resolve(Identifier id, String fileExtension) {
        return this.rootPath.resolve(id.getNamespace()).resolve(this.directoryName).resolve(id.getPath() + "." + fileExtension);
    }

    public Path resolveJson(Identifier id) {
        return this.rootPath.resolve(id.getNamespace()).resolve(this.directoryName).resolve(id.getPath() + ".json");
    }

    public Path resolveJson(RegistryKey<?> key) {
        return this.rootPath.resolve(key.getValue().getNamespace()).resolve(this.directoryName).resolve(key.getValue().getPath() + ".json");
    }
}
