/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.IllagerEntityRenderer
 *  net.minecraft.client.render.entity.IllusionerEntityRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.IllagerEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.IllagerEntityRenderState
 *  net.minecraft.client.render.entity.state.IllusionerEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.IllagerEntity
 *  net.minecraft.entity.mob.IllusionerEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package net.minecraft.client.render.entity;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.IllagerEntityRenderState;
import net.minecraft.client.render.entity.state.IllusionerEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class IllusionerEntityRenderer
extends IllagerEntityRenderer<IllusionerEntity, IllusionerEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/illager/illusioner.png");

    public IllusionerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new IllagerEntityModel(context.getPart(EntityModelLayers.ILLUSIONER)), 0.5f);
        this.addFeature((FeatureRenderer)new /* Unavailable Anonymous Inner Class!! */);
        ((IllagerEntityModel)this.model).getHat().visible = true;
    }

    public Identifier getTexture(IllusionerEntityRenderState illusionerEntityRenderState) {
        return TEXTURE;
    }

    public IllusionerEntityRenderState createRenderState() {
        return new IllusionerEntityRenderState();
    }

    public void updateRenderState(IllusionerEntity illusionerEntity, IllusionerEntityRenderState illusionerEntityRenderState, float f) {
        super.updateRenderState((IllagerEntity)illusionerEntity, (IllagerEntityRenderState)illusionerEntityRenderState, f);
        Vec3d[] vec3ds = illusionerEntity.getMirrorCopyOffsets(f);
        illusionerEntityRenderState.mirrorCopyOffsets = Arrays.copyOf(vec3ds, vec3ds.length);
        illusionerEntityRenderState.spellcasting = illusionerEntity.isSpellcasting();
    }

    public void render(IllusionerEntityRenderState illusionerEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (illusionerEntityRenderState.invisible) {
            Vec3d[] vec3ds = illusionerEntityRenderState.mirrorCopyOffsets;
            for (int i = 0; i < vec3ds.length; ++i) {
                matrixStack.push();
                matrixStack.translate(vec3ds[i].x + (double)MathHelper.cos((double)((float)i + illusionerEntityRenderState.age * 0.5f)) * 0.025, vec3ds[i].y + (double)MathHelper.cos((double)((float)i + illusionerEntityRenderState.age * 0.75f)) * 0.0125, vec3ds[i].z + (double)MathHelper.cos((double)((float)i + illusionerEntityRenderState.age * 0.7f)) * 0.025);
                super.render((LivingEntityRenderState)illusionerEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
                matrixStack.pop();
            }
        } else {
            super.render((LivingEntityRenderState)illusionerEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
        }
    }

    protected boolean isVisible(IllusionerEntityRenderState illusionerEntityRenderState) {
        return true;
    }

    protected Box getBoundingBox(IllusionerEntity illusionerEntity) {
        return super.getBoundingBox((LivingEntity)illusionerEntity).expand(3.0, 0.0, 3.0);
    }

    protected /* synthetic */ boolean isVisible(LivingEntityRenderState state) {
        return this.isVisible((IllusionerEntityRenderState)state);
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((IllusionerEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

