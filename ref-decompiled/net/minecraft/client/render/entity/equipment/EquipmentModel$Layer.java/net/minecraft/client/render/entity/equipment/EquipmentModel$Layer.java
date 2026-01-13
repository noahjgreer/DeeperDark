/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.equipment;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record EquipmentModel.Layer(Identifier textureId, Optional<EquipmentModel.Dyeable> dyeable, boolean usePlayerTexture) {
    public static final Codec<EquipmentModel.Layer> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Identifier.CODEC.fieldOf("texture").forGetter(EquipmentModel.Layer::textureId), (App)EquipmentModel.Dyeable.CODEC.optionalFieldOf("dyeable").forGetter(EquipmentModel.Layer::dyeable), (App)Codec.BOOL.optionalFieldOf("use_player_texture", (Object)false).forGetter(EquipmentModel.Layer::usePlayerTexture)).apply((Applicative)instance, EquipmentModel.Layer::new));

    public EquipmentModel.Layer(Identifier textureId) {
        this(textureId, Optional.empty(), false);
    }

    public static EquipmentModel.Layer createWithLeatherColor(Identifier textureId, boolean dyeable) {
        return new EquipmentModel.Layer(textureId, dyeable ? Optional.of(new EquipmentModel.Dyeable(Optional.of(-6265536))) : Optional.empty(), false);
    }

    public static EquipmentModel.Layer create(Identifier textureId, boolean dyeable) {
        return new EquipmentModel.Layer(textureId, dyeable ? Optional.of(new EquipmentModel.Dyeable(Optional.empty())) : Optional.empty(), false);
    }

    public Identifier getFullTextureId(EquipmentModel.LayerType layerType) {
        return this.textureId.withPath(textureName -> "textures/entity/equipment/" + layerType.asString() + "/" + textureName + ".png");
    }
}
