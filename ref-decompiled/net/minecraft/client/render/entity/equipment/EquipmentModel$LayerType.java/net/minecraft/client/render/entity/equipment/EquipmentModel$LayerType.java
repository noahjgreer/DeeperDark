/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.equipment;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;

@Environment(value=EnvType.CLIENT)
public static final class EquipmentModel.LayerType
extends Enum<EquipmentModel.LayerType>
implements StringIdentifiable {
    public static final /* enum */ EquipmentModel.LayerType HUMANOID = new EquipmentModel.LayerType("humanoid");
    public static final /* enum */ EquipmentModel.LayerType HUMANOID_LEGGINGS = new EquipmentModel.LayerType("humanoid_leggings");
    public static final /* enum */ EquipmentModel.LayerType WINGS = new EquipmentModel.LayerType("wings");
    public static final /* enum */ EquipmentModel.LayerType WOLF_BODY = new EquipmentModel.LayerType("wolf_body");
    public static final /* enum */ EquipmentModel.LayerType HORSE_BODY = new EquipmentModel.LayerType("horse_body");
    public static final /* enum */ EquipmentModel.LayerType LLAMA_BODY = new EquipmentModel.LayerType("llama_body");
    public static final /* enum */ EquipmentModel.LayerType PIG_SADDLE = new EquipmentModel.LayerType("pig_saddle");
    public static final /* enum */ EquipmentModel.LayerType STRIDER_SADDLE = new EquipmentModel.LayerType("strider_saddle");
    public static final /* enum */ EquipmentModel.LayerType CAMEL_SADDLE = new EquipmentModel.LayerType("camel_saddle");
    public static final /* enum */ EquipmentModel.LayerType CAMEL_HUSK_SADDLE = new EquipmentModel.LayerType("camel_husk_saddle");
    public static final /* enum */ EquipmentModel.LayerType HORSE_SADDLE = new EquipmentModel.LayerType("horse_saddle");
    public static final /* enum */ EquipmentModel.LayerType DONKEY_SADDLE = new EquipmentModel.LayerType("donkey_saddle");
    public static final /* enum */ EquipmentModel.LayerType MULE_SADDLE = new EquipmentModel.LayerType("mule_saddle");
    public static final /* enum */ EquipmentModel.LayerType ZOMBIE_HORSE_SADDLE = new EquipmentModel.LayerType("zombie_horse_saddle");
    public static final /* enum */ EquipmentModel.LayerType SKELETON_HORSE_SADDLE = new EquipmentModel.LayerType("skeleton_horse_saddle");
    public static final /* enum */ EquipmentModel.LayerType HAPPY_GHAST_BODY = new EquipmentModel.LayerType("happy_ghast_body");
    public static final /* enum */ EquipmentModel.LayerType NAUTILUS_SADDLE = new EquipmentModel.LayerType("nautilus_saddle");
    public static final /* enum */ EquipmentModel.LayerType NAUTILUS_BODY = new EquipmentModel.LayerType("nautilus_body");
    public static final Codec<EquipmentModel.LayerType> CODEC;
    private final String name;
    private static final /* synthetic */ EquipmentModel.LayerType[] field_54133;

    public static EquipmentModel.LayerType[] values() {
        return (EquipmentModel.LayerType[])field_54133.clone();
    }

    public static EquipmentModel.LayerType valueOf(String string) {
        return Enum.valueOf(EquipmentModel.LayerType.class, string);
    }

    private EquipmentModel.LayerType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public String getTrimsDirectory() {
        return "trims/entity/" + this.name;
    }

    private static /* synthetic */ EquipmentModel.LayerType[] method_64010() {
        return new EquipmentModel.LayerType[]{HUMANOID, HUMANOID_LEGGINGS, WINGS, WOLF_BODY, HORSE_BODY, LLAMA_BODY, PIG_SADDLE, STRIDER_SADDLE, CAMEL_SADDLE, CAMEL_HUSK_SADDLE, HORSE_SADDLE, DONKEY_SADDLE, MULE_SADDLE, ZOMBIE_HORSE_SADDLE, SKELETON_HORSE_SADDLE, HAPPY_GHAST_BODY, NAUTILUS_SADDLE, NAUTILUS_BODY};
    }

    static {
        field_54133 = EquipmentModel.LayerType.method_64010();
        CODEC = StringIdentifiable.createCodec(EquipmentModel.LayerType::values);
    }
}
