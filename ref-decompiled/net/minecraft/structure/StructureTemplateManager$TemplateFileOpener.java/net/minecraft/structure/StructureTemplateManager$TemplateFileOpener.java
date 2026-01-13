/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
static interface StructureTemplateManager.TemplateFileOpener {
    public InputStream open() throws IOException;
}
