/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public static final class FireworkExplosionComponent.Type
extends Enum<FireworkExplosionComponent.Type>
implements StringIdentifiable {
    public static final /* enum */ FireworkExplosionComponent.Type SMALL_BALL = new FireworkExplosionComponent.Type(0, "small_ball");
    public static final /* enum */ FireworkExplosionComponent.Type LARGE_BALL = new FireworkExplosionComponent.Type(1, "large_ball");
    public static final /* enum */ FireworkExplosionComponent.Type STAR = new FireworkExplosionComponent.Type(2, "star");
    public static final /* enum */ FireworkExplosionComponent.Type CREEPER = new FireworkExplosionComponent.Type(3, "creeper");
    public static final /* enum */ FireworkExplosionComponent.Type BURST = new FireworkExplosionComponent.Type(4, "burst");
    private static final IntFunction<FireworkExplosionComponent.Type> BY_ID;
    public static final PacketCodec<ByteBuf, FireworkExplosionComponent.Type> PACKET_CODEC;
    public static final Codec<FireworkExplosionComponent.Type> CODEC;
    private final int id;
    private final String name;
    private static final /* synthetic */ FireworkExplosionComponent.Type[] field_7978;

    public static FireworkExplosionComponent.Type[] values() {
        return (FireworkExplosionComponent.Type[])field_7978.clone();
    }

    public static FireworkExplosionComponent.Type valueOf(String string) {
        return Enum.valueOf(FireworkExplosionComponent.Type.class, string);
    }

    private FireworkExplosionComponent.Type(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public MutableText getName() {
        return Text.translatable("item.minecraft.firework_star.shape." + this.name);
    }

    public int getId() {
        return this.id;
    }

    public static FireworkExplosionComponent.Type byId(int id) {
        return BY_ID.apply(id);
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static /* synthetic */ FireworkExplosionComponent.Type[] method_36677() {
        return new FireworkExplosionComponent.Type[]{SMALL_BALL, LARGE_BALL, STAR, CREEPER, BURST};
    }

    static {
        field_7978 = FireworkExplosionComponent.Type.method_36677();
        BY_ID = ValueLists.createIndexToValueFunction(FireworkExplosionComponent.Type::getId, FireworkExplosionComponent.Type.values(), ValueLists.OutOfBoundsHandling.ZERO);
        PACKET_CODEC = PacketCodecs.indexed(BY_ID, FireworkExplosionComponent.Type::getId);
        CODEC = StringIdentifiable.createBasicCodec(FireworkExplosionComponent.Type::values);
    }
}
