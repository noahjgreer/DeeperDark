/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArraySet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.IOUtils
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gl;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlImportProcessor;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.path.PathUtil;
import org.apache.commons.io.IOUtils;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static class ShaderLoader.1
extends GlImportProcessor {
    private final Set<Identifier> processed = new ObjectArraySet();
    final /* synthetic */ Identifier field_53945;
    final /* synthetic */ Map field_53946;

    ShaderLoader.1(Identifier identifier, Map map) {
        this.field_53945 = identifier;
        this.field_53946 = map;
    }

    @Override
    public @Nullable String loadImport(boolean inline, String name) {
        String string;
        block11: {
            Identifier identifier;
            try {
                identifier = inline ? this.field_53945.withPath(path -> PathUtil.normalizeToPosix(path + name)) : Identifier.of(name).withPrefixedPath(ShaderLoader.INCLUDE_PATH);
            }
            catch (InvalidIdentifierException invalidIdentifierException) {
                LOGGER.error("Malformed GLSL import {}: {}", (Object)name, (Object)invalidIdentifierException.getMessage());
                return "#error " + invalidIdentifierException.getMessage();
            }
            if (!this.processed.add(identifier)) {
                return null;
            }
            BufferedReader reader = ((Resource)this.field_53946.get(identifier)).getReader();
            try {
                string = IOUtils.toString((Reader)reader);
                if (reader == null) break block11;
            }
            catch (Throwable throwable) {
                try {
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
                catch (IOException iOException) {
                    LOGGER.error("Could not open GLSL import {}: {}", (Object)identifier, (Object)iOException.getMessage());
                    return "#error " + iOException.getMessage();
                }
            }
            ((Reader)reader).close();
        }
        return string;
    }
}
