/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource.fs;

import java.nio.file.Path;
import net.minecraft.resource.fs.ResourceFile;

public record ResourceFile.File(Path contents) implements ResourceFile
{
}
