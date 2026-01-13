/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.mob.MobEntity
 *  net.minecraft.text.Text
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public abstract class MobEntityRenderer<T extends MobEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
extends LivingEntityRenderer<T, S, M> {
    public MobEntityRenderer(EntityRendererFactory.Context context, M entityModel, float f) {
        super(context, entityModel, f);
    }

    protected boolean hasLabel(T mobEntity, double d) {
        return super.hasLabel(mobEntity, d) && (mobEntity.shouldRenderName() || mobEntity.hasCustomName() && mobEntity == this.dispatcher.targetedEntity);
    }

    protected float getShadowRadius(S livingEntityRenderState) {
        return super.getShadowRadius(livingEntityRenderState) * ((LivingEntityRenderState)livingEntityRenderState).ageScale;
    }

    protected static boolean nameEquals(Entity entity, String name) {
        Text text = entity.getCustomName();
        return text != null && name.equals(text.getString());
    }

    protected /* synthetic */ float getShadowRadius(EntityRenderState state) {
        return this.getShadowRadius((LivingEntityRenderState)state);
    }
}

