/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public static final class UndeadHorseEntityRenderer.Type
extends Enum<UndeadHorseEntityRenderer.Type> {
    public static final /* enum */ UndeadHorseEntityRenderer.Type SKELETON = new UndeadHorseEntityRenderer.Type(Identifier.ofVanilla("textures/entity/horse/horse_skeleton.png"), EntityModelLayers.SKELETON_HORSE, EntityModelLayers.SKELETON_HORSE_BABY, EquipmentModel.LayerType.SKELETON_HORSE_SADDLE, EntityModelLayers.SKELETON_HORSE_SADDLE, EntityModelLayers.SKELETON_HORSE_BABY_SADDLE);
    public static final /* enum */ UndeadHorseEntityRenderer.Type ZOMBIE = new UndeadHorseEntityRenderer.Type(Identifier.ofVanilla("textures/entity/horse/horse_zombie.png"), EntityModelLayers.ZOMBIE_HORSE, EntityModelLayers.ZOMBIE_HORSE_BABY, EquipmentModel.LayerType.ZOMBIE_HORSE_SADDLE, EntityModelLayers.ZOMBIE_HORSE_SADDLE, EntityModelLayers.ZOMBIE_HORSE_BABY_SADDLE);
    final Identifier texture;
    final EntityModelLayer modelLayer;
    final EntityModelLayer babyModelLayer;
    final EquipmentModel.LayerType saddleLayerType;
    final EntityModelLayer saddleModelLayer;
    final EntityModelLayer babySaddleModelLayer;
    private static final /* synthetic */ UndeadHorseEntityRenderer.Type[] field_56112;

    public static UndeadHorseEntityRenderer.Type[] values() {
        return (UndeadHorseEntityRenderer.Type[])field_56112.clone();
    }

    public static UndeadHorseEntityRenderer.Type valueOf(String string) {
        return Enum.valueOf(UndeadHorseEntityRenderer.Type.class, string);
    }

    private UndeadHorseEntityRenderer.Type(Identifier texture, EntityModelLayer modelLayer, EntityModelLayer babyModelLayer, EquipmentModel.LayerType saddleLayerType, EntityModelLayer saddleModelLayer, EntityModelLayer babySaddleModelLayer) {
        this.texture = texture;
        this.modelLayer = modelLayer;
        this.babyModelLayer = babyModelLayer;
        this.saddleLayerType = saddleLayerType;
        this.saddleModelLayer = saddleModelLayer;
        this.babySaddleModelLayer = babySaddleModelLayer;
    }

    private static /* synthetic */ UndeadHorseEntityRenderer.Type[] method_66855() {
        return new UndeadHorseEntityRenderer.Type[]{SKELETON, ZOMBIE};
    }

    static {
        field_56112 = UndeadHorseEntityRenderer.Type.method_66855();
    }
}
