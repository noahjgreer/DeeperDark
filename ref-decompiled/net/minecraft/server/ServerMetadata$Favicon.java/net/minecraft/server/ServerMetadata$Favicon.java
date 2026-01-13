/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 */
package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public record ServerMetadata.Favicon(byte[] iconBytes) {
    private static final String DATA_URI_PREFIX = "data:image/png;base64,";
    public static final Codec<ServerMetadata.Favicon> CODEC = Codec.STRING.comapFlatMap(uri -> {
        if (!uri.startsWith(DATA_URI_PREFIX)) {
            return DataResult.error(() -> "Unknown format");
        }
        try {
            String string = uri.substring(DATA_URI_PREFIX.length()).replaceAll("\n", "");
            byte[] bs = Base64.getDecoder().decode(string.getBytes(StandardCharsets.UTF_8));
            return DataResult.success((Object)new ServerMetadata.Favicon(bs));
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return DataResult.error(() -> "Malformed base64 server icon");
        }
    }, iconBytes -> DATA_URI_PREFIX + new String(Base64.getEncoder().encode(iconBytes.iconBytes), StandardCharsets.UTF_8));
}
