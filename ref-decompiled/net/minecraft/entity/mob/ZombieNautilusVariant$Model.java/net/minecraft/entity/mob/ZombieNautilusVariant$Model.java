/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.entity.mob;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public static final class ZombieNautilusVariant.Model
extends Enum<ZombieNautilusVariant.Model>
implements StringIdentifiable {
    public static final /* enum */ ZombieNautilusVariant.Model NORMAL = new ZombieNautilusVariant.Model("normal");
    public static final /* enum */ ZombieNautilusVariant.Model WARM = new ZombieNautilusVariant.Model("warm");
    public static final Codec<ZombieNautilusVariant.Model> CODEC;
    private final String id;
    private static final /* synthetic */ ZombieNautilusVariant.Model[] field_64369;

    public static ZombieNautilusVariant.Model[] values() {
        return (ZombieNautilusVariant.Model[])field_64369.clone();
    }

    public static ZombieNautilusVariant.Model valueOf(String string) {
        return Enum.valueOf(ZombieNautilusVariant.Model.class, string);
    }

    private ZombieNautilusVariant.Model(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static /* synthetic */ ZombieNautilusVariant.Model[] method_76447() {
        return new ZombieNautilusVariant.Model[]{NORMAL, WARM};
    }

    static {
        field_64369 = ZombieNautilusVariant.Model.method_76447();
        CODEC = StringIdentifiable.createCodec(ZombieNautilusVariant.Model::values);
    }
}
