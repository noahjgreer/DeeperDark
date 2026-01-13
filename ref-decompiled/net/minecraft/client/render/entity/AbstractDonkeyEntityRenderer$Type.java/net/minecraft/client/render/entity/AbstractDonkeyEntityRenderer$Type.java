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
public static final class AbstractDonkeyEntityRenderer.Type
extends Enum<AbstractDonkeyEntityRenderer.Type> {
    public static final /* enum */ AbstractDonkeyEntityRenderer.Type DONKEY = new AbstractDonkeyEntityRenderer.Type(Identifier.ofVanilla("textures/entity/horse/donkey.png"), EntityModelLayers.DONKEY, EntityModelLayers.DONKEY_BABY, EquipmentModel.LayerType.DONKEY_SADDLE, EntityModelLayers.DONKEY_SADDLE, EntityModelLayers.DONKEY_BABY_SADDLE);
    public static final /* enum */ AbstractDonkeyEntityRenderer.Type MULE = new AbstractDonkeyEntityRenderer.Type(Identifier.ofVanilla("textures/entity/horse/mule.png"), EntityModelLayers.MULE, EntityModelLayers.MULE_BABY, EquipmentModel.LayerType.MULE_SADDLE, EntityModelLayers.MULE_SADDLE, EntityModelLayers.MULE_BABY_SADDLE);
    final Identifier texture;
    final EntityModelLayer adultModelLayer;
    final EntityModelLayer babyModelLayer;
    final EquipmentModel.LayerType saddleLayerType;
    final EntityModelLayer adultSaddleModelLayer;
    final EntityModelLayer babySaddleModelLayer;
    private static final /* synthetic */ AbstractDonkeyEntityRenderer.Type[] field_56103;

    public static AbstractDonkeyEntityRenderer.Type[] values() {
        return (AbstractDonkeyEntityRenderer.Type[])field_56103.clone();
    }

    public static AbstractDonkeyEntityRenderer.Type valueOf(String string) {
        return Enum.valueOf(AbstractDonkeyEntityRenderer.Type.class, string);
    }

    private AbstractDonkeyEntityRenderer.Type(Identifier texture, EntityModelLayer adultModelLayer, EntityModelLayer babyModelLayer, EquipmentModel.LayerType saddleLayerType, EntityModelLayer adultSaddleModelLayer, EntityModelLayer babySaddleModelLayer) {
        this.texture = texture;
        this.adultModelLayer = adultModelLayer;
        this.babyModelLayer = babyModelLayer;
        this.saddleLayerType = saddleLayerType;
        this.adultSaddleModelLayer = adultSaddleModelLayer;
        this.babySaddleModelLayer = babySaddleModelLayer;
    }

    private static /* synthetic */ AbstractDonkeyEntityRenderer.Type[] method_66849() {
        return new AbstractDonkeyEntityRenderer.Type[]{DONKEY, MULE};
    }

    static {
        field_56103 = AbstractDonkeyEntityRenderer.Type.method_66849();
    }
}
