/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonSyntaxException
 *  io.netty.buffer.ByteBuf
 *  io.netty.handler.codec.DecoderException
 */
package net.minecraft.network.codec;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.StringEncoding;
import net.minecraft.util.LenientJsonParser;

static class PacketCodecs.28
implements PacketCodec<ByteBuf, JsonElement> {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    final /* synthetic */ int field_60513;

    PacketCodecs.28(int i) {
        this.field_60513 = i;
    }

    @Override
    public JsonElement decode(ByteBuf byteBuf) {
        String string = StringEncoding.decode(byteBuf, this.field_60513);
        try {
            return LenientJsonParser.parse(string);
        }
        catch (JsonSyntaxException jsonSyntaxException) {
            throw new DecoderException("Failed to parse JSON", (Throwable)jsonSyntaxException);
        }
    }

    @Override
    public void encode(ByteBuf byteBuf, JsonElement jsonElement) {
        String string = GSON.toJson(jsonElement);
        StringEncoding.encode(byteBuf, string, this.field_60513);
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((ByteBuf)object, (JsonElement)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((ByteBuf)object);
    }
}
