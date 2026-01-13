/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.text;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.message.MessageType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public static final class Decoration.Parameter
extends Enum<Decoration.Parameter>
implements StringIdentifiable {
    public static final /* enum */ Decoration.Parameter SENDER = new Decoration.Parameter(0, "sender", (content, params) -> params.name());
    public static final /* enum */ Decoration.Parameter TARGET = new Decoration.Parameter(1, "target", (content, params) -> params.targetName().orElse(ScreenTexts.EMPTY));
    public static final /* enum */ Decoration.Parameter CONTENT = new Decoration.Parameter(2, "content", (content, params) -> content);
    private static final IntFunction<Decoration.Parameter> BY_ID;
    public static final Codec<Decoration.Parameter> CODEC;
    public static final PacketCodec<ByteBuf, Decoration.Parameter> PACKET_CODEC;
    private final int id;
    private final String name;
    private final Selector selector;
    private static final /* synthetic */ Decoration.Parameter[] field_39226;

    public static Decoration.Parameter[] values() {
        return (Decoration.Parameter[])field_39226.clone();
    }

    public static Decoration.Parameter valueOf(String string) {
        return Enum.valueOf(Decoration.Parameter.class, string);
    }

    private Decoration.Parameter(int id, String name, Selector selector) {
        this.id = id;
        this.name = name;
        this.selector = selector;
    }

    public Text apply(Text content, MessageType.Parameters params) {
        return this.selector.select(content, params);
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static /* synthetic */ Decoration.Parameter[] method_43836() {
        return new Decoration.Parameter[]{SENDER, TARGET, CONTENT};
    }

    static {
        field_39226 = Decoration.Parameter.method_43836();
        BY_ID = ValueLists.createIndexToValueFunction(parameter -> parameter.id, Decoration.Parameter.values(), ValueLists.OutOfBoundsHandling.ZERO);
        CODEC = StringIdentifiable.createCodec(Decoration.Parameter::values);
        PACKET_CODEC = PacketCodecs.indexed(BY_ID, parameter -> parameter.id);
    }

    public static interface Selector {
        public Text select(Text var1, MessageType.Parameters var2);
    }
}
