/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource.fs;

import java.util.Map;
import net.minecraft.resource.fs.ResourceFile;
import net.minecraft.resource.fs.ResourcePath;

public record ResourceFile.Directory(Map<String, ResourcePath> children) implements ResourceFile
{
}
