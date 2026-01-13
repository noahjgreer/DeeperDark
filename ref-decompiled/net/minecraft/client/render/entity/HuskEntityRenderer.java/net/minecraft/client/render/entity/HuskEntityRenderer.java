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
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class HuskEntityRenderer
extends ZombieEntityRenderer {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/zombie/husk.png");

    public HuskEntityRenderer(EntityRendererFactory.Context context) {
        super(context, EntityModelLayers.HUSK, EntityModelLayers.HUSK_BABY, EntityModelLayers.HUSK_EQUIPMENT, EntityModelLayers.HUSK_BABY_EQUIPMENT);
    }

    @Override
    public Identifier getTexture(ZombieEntityRenderState zombieEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((ZombieEntityRenderState)state);
    }
}
