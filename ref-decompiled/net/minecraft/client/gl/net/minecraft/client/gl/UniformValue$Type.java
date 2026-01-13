/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.UniformValue;
import net.minecraft.util.StringIdentifiable;

@Environment(value=EnvType.CLIENT)
public static final class UniformValue.Type
extends Enum<UniformValue.Type>
implements StringIdentifiable {
    public static final /* enum */ UniformValue.Type INT = new UniformValue.Type("int", UniformValue.IntValue.CODEC);
    public static final /* enum */ UniformValue.Type IVEC3 = new UniformValue.Type("ivec3", UniformValue.Vec3iValue.CODEC);
    public static final /* enum */ UniformValue.Type FLOAT = new UniformValue.Type("float", UniformValue.FloatValue.CODEC);
    public static final /* enum */ UniformValue.Type VEC2 = new UniformValue.Type("vec2", UniformValue.Vec2fValue.CODEC);
    public static final /* enum */ UniformValue.Type VEC3 = new UniformValue.Type("vec3", UniformValue.Vec3fValue.CODEC);
    public static final /* enum */ UniformValue.Type VEC4 = new UniformValue.Type("vec4", UniformValue.Vec4fValue.CODEC);
    public static final /* enum */ UniformValue.Type MATRIX4X4 = new UniformValue.Type("matrix4x4", UniformValue.Matrix4fValue.CODEC);
    public static final StringIdentifiable.EnumCodec<UniformValue.Type> CODEC;
    private final String name;
    final MapCodec<? extends UniformValue> mapCodec;
    private static final /* synthetic */ UniformValue.Type[] field_60145;

    public static UniformValue.Type[] values() {
        return (UniformValue.Type[])field_60145.clone();
    }

    public static UniformValue.Type valueOf(String string) {
        return Enum.valueOf(UniformValue.Type.class, string);
    }

    private UniformValue.Type(String name, Codec<? extends UniformValue> codec) {
        this.name = name;
        this.mapCodec = codec.fieldOf("value");
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static /* synthetic */ UniformValue.Type[] method_71134() {
        return new UniformValue.Type[]{INT, IVEC3, FLOAT, VEC2, VEC3, VEC4, MATRIX4X4};
    }

    static {
        field_60145 = UniformValue.Type.method_71134();
        CODEC = StringIdentifiable.createCodec(UniformValue.Type::values);
    }
}
