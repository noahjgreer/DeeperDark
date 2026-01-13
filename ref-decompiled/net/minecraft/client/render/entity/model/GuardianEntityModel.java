/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.ModelData
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.model.ModelPartBuilder
 *  net.minecraft.client.model.ModelPartData
 *  net.minecraft.client.model.ModelTransform
 *  net.minecraft.client.model.TexturedModelData
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.GuardianEntityModel
 *  net.minecraft.client.render.entity.model.ModelTransformer
 *  net.minecraft.client.render.entity.state.GuardianEntityRenderState
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.GuardianEntityRenderState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class GuardianEntityModel
extends EntityModel<GuardianEntityRenderState> {
    public static final ModelTransformer ELDER_TRANSFORMER = ModelTransformer.scaling((float)2.35f);
    private static final float[] SPIKE_PITCHES = new float[]{1.75f, 0.25f, 0.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 1.25f, 0.75f, 0.0f, 0.0f};
    private static final float[] SPIKE_YAWS = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.25f, 1.75f, 1.25f, 0.75f, 0.0f, 0.0f, 0.0f, 0.0f};
    private static final float[] SPIKE_ROLLS = new float[]{0.0f, 0.0f, 0.25f, 1.75f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.75f, 1.25f};
    private static final float[] SPIKE_PIVOTS_X = new float[]{0.0f, 0.0f, 8.0f, -8.0f, -8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f, 8.0f, -8.0f};
    private static final float[] SPIKE_PIVOTS_Y = new float[]{-8.0f, -8.0f, -8.0f, -8.0f, 0.0f, 0.0f, 0.0f, 0.0f, 8.0f, 8.0f, 8.0f, 8.0f};
    private static final float[] SPIKE_PIVOTS_Z = new float[]{8.0f, -8.0f, 0.0f, 0.0f, -8.0f, -8.0f, 8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f};
    private static final String EYE = "eye";
    private static final String TAIL0 = "tail0";
    private static final String TAIL1 = "tail1";
    private static final String TAIL2 = "tail2";
    private final ModelPart head;
    private final ModelPart eye;
    private final ModelPart[] spikes = new ModelPart[12];
    private final ModelPart[] tail;

    public GuardianEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.head = modelPart.getChild("head");
        for (int i = 0; i < this.spikes.length; ++i) {
            this.spikes[i] = this.head.getChild(GuardianEntityModel.getSpikeName((int)i));
        }
        this.eye = this.head.getChild("eye");
        this.tail = new ModelPart[3];
        this.tail[0] = this.head.getChild("tail0");
        this.tail[1] = this.tail[0].getChild("tail1");
        this.tail[2] = this.tail[1].getChild("tail2");
    }

    private static String getSpikeName(int index) {
        return "spike" + index;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0f, 10.0f, -8.0f, 12.0f, 12.0f, 16.0f).uv(0, 28).cuboid(-8.0f, 10.0f, -6.0f, 2.0f, 12.0f, 12.0f).uv(0, 28).cuboid(6.0f, 10.0f, -6.0f, 2.0f, 12.0f, 12.0f, true).uv(16, 40).cuboid(-6.0f, 8.0f, -6.0f, 12.0f, 2.0f, 12.0f).uv(16, 40).cuboid(-6.0f, 22.0f, -6.0f, 12.0f, 2.0f, 12.0f), ModelTransform.NONE);
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, -4.5f, -1.0f, 2.0f, 9.0f, 2.0f);
        for (int i = 0; i < 12; ++i) {
            float f = GuardianEntityModel.getSpikePivotX((int)i, (float)0.0f, (float)0.0f);
            float g = GuardianEntityModel.getSpikePivotY((int)i, (float)0.0f, (float)0.0f);
            float h = GuardianEntityModel.getSpikePivotZ((int)i, (float)0.0f, (float)0.0f);
            float j = (float)Math.PI * SPIKE_PITCHES[i];
            float k = (float)Math.PI * SPIKE_YAWS[i];
            float l = (float)Math.PI * SPIKE_ROLLS[i];
            modelPartData2.addChild(GuardianEntityModel.getSpikeName((int)i), modelPartBuilder, ModelTransform.of((float)f, (float)g, (float)h, (float)j, (float)k, (float)l));
        }
        modelPartData2.addChild("eye", ModelPartBuilder.create().uv(8, 0).cuboid(-1.0f, 15.0f, 0.0f, 2.0f, 2.0f, 1.0f), ModelTransform.origin((float)0.0f, (float)0.0f, (float)-8.25f));
        ModelPartData modelPartData3 = modelPartData2.addChild("tail0", ModelPartBuilder.create().uv(40, 0).cuboid(-2.0f, 14.0f, 7.0f, 4.0f, 4.0f, 8.0f), ModelTransform.NONE);
        ModelPartData modelPartData4 = modelPartData3.addChild("tail1", ModelPartBuilder.create().uv(0, 54).cuboid(0.0f, 14.0f, 0.0f, 3.0f, 3.0f, 7.0f), ModelTransform.origin((float)-1.5f, (float)0.5f, (float)14.0f));
        modelPartData4.addChild("tail2", ModelPartBuilder.create().uv(41, 32).cuboid(0.0f, 14.0f, 0.0f, 2.0f, 2.0f, 6.0f).uv(25, 19).cuboid(1.0f, 10.5f, 3.0f, 1.0f, 9.0f, 9.0f), ModelTransform.origin((float)0.5f, (float)0.5f, (float)6.0f));
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)64);
    }

    public static TexturedModelData getElderTexturedModelData() {
        return GuardianEntityModel.getTexturedModelData().transform(ELDER_TRANSFORMER);
    }

    public void setAngles(GuardianEntityRenderState guardianEntityRenderState) {
        super.setAngles((Object)guardianEntityRenderState);
        this.head.yaw = guardianEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        this.head.pitch = guardianEntityRenderState.pitch * ((float)Math.PI / 180);
        float f = (1.0f - guardianEntityRenderState.spikesExtension) * 0.55f;
        this.updateSpikeExtensions(guardianEntityRenderState.age, f);
        if (guardianEntityRenderState.lookAtPos != null && guardianEntityRenderState.rotationVec != null) {
            double d = guardianEntityRenderState.lookAtPos.y - guardianEntityRenderState.cameraPosVec.y;
            this.eye.originY = d > 0.0 ? 0.0f : 1.0f;
            Vec3d vec3d = guardianEntityRenderState.rotationVec;
            vec3d = new Vec3d(vec3d.x, 0.0, vec3d.z);
            Vec3d vec3d2 = new Vec3d(guardianEntityRenderState.cameraPosVec.x - guardianEntityRenderState.lookAtPos.x, 0.0, guardianEntityRenderState.cameraPosVec.z - guardianEntityRenderState.lookAtPos.z).normalize().rotateY(1.5707964f);
            double e = vec3d.dotProduct(vec3d2);
            this.eye.originX = MathHelper.sqrt((float)((float)Math.abs(e))) * 2.0f * (float)Math.signum(e);
        }
        this.eye.visible = true;
        float g = guardianEntityRenderState.tailAngle;
        this.tail[0].yaw = MathHelper.sin((double)g) * (float)Math.PI * 0.05f;
        this.tail[1].yaw = MathHelper.sin((double)g) * (float)Math.PI * 0.1f;
        this.tail[2].yaw = MathHelper.sin((double)g) * (float)Math.PI * 0.15f;
    }

    private void updateSpikeExtensions(float animationProgress, float extension) {
        for (int i = 0; i < 12; ++i) {
            this.spikes[i].originX = GuardianEntityModel.getSpikePivotX((int)i, (float)animationProgress, (float)extension);
            this.spikes[i].originY = GuardianEntityModel.getSpikePivotY((int)i, (float)animationProgress, (float)extension);
            this.spikes[i].originZ = GuardianEntityModel.getSpikePivotZ((int)i, (float)animationProgress, (float)extension);
        }
    }

    private static float getAngle(int index, float animationProgress, float magnitude) {
        return 1.0f + MathHelper.cos((double)(animationProgress * 1.5f + (float)index)) * 0.01f - magnitude;
    }

    private static float getSpikePivotX(int index, float animationProgress, float extension) {
        return SPIKE_PIVOTS_X[index] * GuardianEntityModel.getAngle((int)index, (float)animationProgress, (float)extension);
    }

    private static float getSpikePivotY(int index, float animationProgress, float extension) {
        return 16.0f + SPIKE_PIVOTS_Y[index] * GuardianEntityModel.getAngle((int)index, (float)animationProgress, (float)extension);
    }

    private static float getSpikePivotZ(int index, float animationProgress, float extension) {
        return SPIKE_PIVOTS_Z[index] * GuardianEntityModel.getAngle((int)index, (float)animationProgress, (float)extension);
    }
}

