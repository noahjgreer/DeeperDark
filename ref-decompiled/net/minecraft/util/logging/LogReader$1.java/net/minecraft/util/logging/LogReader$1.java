/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonParser
 *  com.google.gson.stream.JsonReader
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util.logging;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.EOFException;
import java.io.IOException;
import net.minecraft.util.logging.LogReader;
import org.jspecify.annotations.Nullable;

static class LogReader.1
implements LogReader<T> {
    final /* synthetic */ JsonReader field_41302;
    final /* synthetic */ Codec field_41303;

    LogReader.1(JsonReader jsonReader, Codec codec) {
        this.field_41302 = jsonReader;
        this.field_41303 = codec;
    }

    @Override
    public @Nullable T read() throws IOException {
        try {
            if (!this.field_41302.hasNext()) {
                return null;
            }
            JsonElement jsonElement = JsonParser.parseReader((JsonReader)this.field_41302);
            return this.field_41303.parse((DynamicOps)JsonOps.INSTANCE, (Object)jsonElement).getOrThrow(IOException::new);
        }
        catch (JsonParseException jsonParseException) {
            throw new IOException(jsonParseException);
        }
        catch (EOFException eOFException) {
            return null;
        }
    }

    @Override
    public void close() throws IOException {
        this.field_41302.close();
    }
}
