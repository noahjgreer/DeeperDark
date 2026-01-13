/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;

@Environment(value=EnvType.CLIENT)
public static final class VillagerResourceMetadata.HatType
extends Enum<VillagerResourceMetadata.HatType>
implements StringIdentifiable {
    public static final /* enum */ VillagerResourceMetadata.HatType NONE = new VillagerResourceMetadata.HatType("none");
    public static final /* enum */ VillagerResourceMetadata.HatType PARTIAL = new VillagerResourceMetadata.HatType("partial");
    public static final /* enum */ VillagerResourceMetadata.HatType FULL = new VillagerResourceMetadata.HatType("full");
    public static final Codec<VillagerResourceMetadata.HatType> CODEC;
    private final String name;
    private static final /* synthetic */ VillagerResourceMetadata.HatType[] field_17165;

    public static VillagerResourceMetadata.HatType[] values() {
        return (VillagerResourceMetadata.HatType[])field_17165.clone();
    }

    public static VillagerResourceMetadata.HatType valueOf(String string) {
        return Enum.valueOf(VillagerResourceMetadata.HatType.class, string);
    }

    private VillagerResourceMetadata.HatType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static /* synthetic */ VillagerResourceMetadata.HatType[] method_36924() {
        return new VillagerResourceMetadata.HatType[]{NONE, PARTIAL, FULL};
    }

    static {
        field_17165 = VillagerResourceMetadata.HatType.method_36924();
        CODEC = StringIdentifiable.createCodec(VillagerResourceMetadata.HatType::values);
    }
}
