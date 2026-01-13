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
 *  net.minecraft.client.render.entity.model.BabyModelTransformer
 *  net.minecraft.client.render.entity.model.ChickenEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.ModelTransformer
 *  net.minecraft.client.render.entity.state.ChickenEntityRenderState
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.render.entity.model;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.BabyModelTransformer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelTransformer;
import net.minecraft.client.render.entity.state.ChickenEntityRenderState;
import net.minecraft.util.math.MathHelper;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ChickenEntityModel
extends EntityModel<ChickenEntityRenderState> {
    public static final String RED_THING = "red_thing";
    public static final float field_56579 = 16.0f;
    public static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(false, 5.0f, 2.0f, 2.0f, 1.99f, 24.0f, Set.of("head", "beak", "red_thing"));
    private final ModelPart head;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart rightWing;
    private final ModelPart leftWing;

    public ChickenEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.head = modelPart.getChild("head");
        this.rightLeg = modelPart.getChild("right_leg");
        this.leftLeg = modelPart.getChild("left_leg");
        this.rightWing = modelPart.getChild("right_wing");
        this.leftWing = modelPart.getChild("left_wing");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = ChickenEntityModel.getModelData();
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)32);
    }

    protected static ModelData getModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0f, -6.0f, -2.0f, 4.0f, 6.0f, 3.0f), ModelTransform.origin((float)0.0f, (float)15.0f, (float)-4.0f));
        modelPartData2.addChild("beak", ModelPartBuilder.create().uv(14, 0).cuboid(-2.0f, -4.0f, -4.0f, 4.0f, 2.0f, 2.0f), ModelTransform.NONE);
        modelPartData2.addChild("red_thing", ModelPartBuilder.create().uv(14, 4).cuboid(-1.0f, -2.0f, -3.0f, 2.0f, 2.0f, 2.0f), ModelTransform.NONE);
        modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 9).cuboid(-3.0f, -4.0f, -3.0f, 6.0f, 8.0f, 6.0f), ModelTransform.of((float)0.0f, (float)16.0f, (float)0.0f, (float)1.5707964f, (float)0.0f, (float)0.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(26, 0).cuboid(-1.0f, 0.0f, -3.0f, 3.0f, 5.0f, 3.0f);
        modelPartData.addChild("right_leg", modelPartBuilder, ModelTransform.origin((float)-2.0f, (float)19.0f, (float)1.0f));
        modelPartData.addChild("left_leg", modelPartBuilder, ModelTransform.origin((float)1.0f, (float)19.0f, (float)1.0f));
        modelPartData.addChild("right_wing", ModelPartBuilder.create().uv(24, 13).cuboid(0.0f, 0.0f, -3.0f, 1.0f, 4.0f, 6.0f), ModelTransform.origin((float)-4.0f, (float)13.0f, (float)0.0f));
        modelPartData.addChild("left_wing", ModelPartBuilder.create().uv(24, 13).cuboid(-1.0f, 0.0f, -3.0f, 1.0f, 4.0f, 6.0f), ModelTransform.origin((float)4.0f, (float)13.0f, (float)0.0f));
        return modelData;
    }

    public void setAngles(ChickenEntityRenderState chickenEntityRenderState) {
        super.setAngles((Object)chickenEntityRenderState);
        float f = (MathHelper.sin((double)chickenEntityRenderState.flapProgress) + 1.0f) * chickenEntityRenderState.maxWingDeviation;
        this.head.pitch = chickenEntityRenderState.pitch * ((float)Math.PI / 180);
        this.head.yaw = chickenEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        float g = chickenEntityRenderState.limbSwingAmplitude;
        float h = chickenEntityRenderState.limbSwingAnimationProgress;
        this.rightLeg.pitch = MathHelper.cos((double)(h * 0.6662f)) * 1.4f * g;
        this.leftLeg.pitch = MathHelper.cos((double)(h * 0.6662f + (float)Math.PI)) * 1.4f * g;
        this.rightWing.roll = f;
        this.leftWing.roll = -f;
    }
}

