/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.IllusionerEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class IllusionerEntityRenderer
extends IllagerEntityRenderer<IllusionerEntity, IllusionerEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/illager/illusioner.png");

    public IllusionerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new IllagerEntityModel(context.getPart(EntityModelLayers.ILLUSIONER)), 0.5f);
        this.addFeature(new HeldItemFeatureRenderer<IllusionerEntityRenderState, IllagerEntityModel<IllusionerEntityRenderState>>(this, (FeatureRendererContext)this){

            @Override
            public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, IllusionerEntityRenderState illusionerEntityRenderState, float f, float g) {
                if (illusionerEntityRenderState.spellcasting || illusionerEntityRenderState.attacking) {
                    super.render(matrixStack, orderedRenderCommandQueue, i, illusionerEntityRenderState, f, g);
                }
            }
        });
        ((IllagerEntityModel)this.model).getHat().visible = true;
    }

    @Override
    public Identifier getTexture(IllusionerEntityRenderState illusionerEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public IllusionerEntityRenderState createRenderState() {
        return new IllusionerEntityRenderState();
    }

    @Override
    public void updateRenderState(IllusionerEntity illusionerEntity, IllusionerEntityRenderState illusionerEntityRenderState, float f) {
        super.updateRenderState(illusionerEntity, illusionerEntityRenderState, f);
        Vec3d[] vec3ds = illusionerEntity.getMirrorCopyOffsets(f);
        illusionerEntityRenderState.mirrorCopyOffsets = Arrays.copyOf(vec3ds, vec3ds.length);
        illusionerEntityRenderState.spellcasting = illusionerEntity.isSpellcasting();
    }

    @Override
    public void render(IllusionerEntityRenderState illusionerEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (illusionerEntityRenderState.invisible) {
            Vec3d[] vec3ds = illusionerEntityRenderState.mirrorCopyOffsets;
            for (int i = 0; i < vec3ds.length; ++i) {
                matrixStack.push();
                matrixStack.translate(vec3ds[i].x + (double)MathHelper.cos((float)i + illusionerEntityRenderState.age * 0.5f) * 0.025, vec3ds[i].y + (double)MathHelper.cos((float)i + illusionerEntityRenderState.age * 0.75f) * 0.0125, vec3ds[i].z + (double)MathHelper.cos((float)i + illusionerEntityRenderState.age * 0.7f) * 0.025);
                super.render(illusionerEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
                matrixStack.pop();
            }
        } else {
            super.render(illusionerEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
        }
    }

    @Override
    protected boolean isVisible(IllusionerEntityRenderState illusionerEntityRenderState) {
        return true;
    }

    @Override
    protected Box getBoundingBox(IllusionerEntity illusionerEntity) {
        return super.getBoundingBox(illusionerEntity).expand(3.0, 0.0, 3.0);
    }

    @Override
    protected /* synthetic */ boolean isVisible(LivingEntityRenderState state) {
        return this.isVisible((IllusionerEntityRenderState)state);
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((IllusionerEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
