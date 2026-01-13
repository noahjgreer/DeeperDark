/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.UniformType
 */
package net.minecraft.client.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public final class UniformType
extends Enum<UniformType> {
    public static final /* enum */ UniformType UNIFORM_BUFFER = new UniformType("UNIFORM_BUFFER", 0, "ubo");
    public static final /* enum */ UniformType TEXEL_BUFFER = new UniformType("TEXEL_BUFFER", 1, "utb");
    final String name;
    private static final /* synthetic */ UniformType[] field_56751;

    public static UniformType[] values() {
        return (UniformType[])field_56751.clone();
    }

    public static UniformType valueOf(String string) {
        return Enum.valueOf(UniformType.class, string);
    }

    private UniformType(String name) {
        this.name = name;
    }

    private static /* synthetic */ UniformType[] method_67774() {
        return new UniformType[]{UNIFORM_BUFFER, TEXEL_BUFFER};
    }

    static {
        field_56751 = UniformType.method_67774();
    }
}

