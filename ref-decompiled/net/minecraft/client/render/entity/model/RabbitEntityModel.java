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
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.ModelTransformer
 *  net.minecraft.client.render.entity.model.RabbitEntityModel
 *  net.minecraft.client.render.entity.state.RabbitEntityRenderState
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
import net.minecraft.client.render.entity.state.RabbitEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class RabbitEntityModel
extends EntityModel<RabbitEntityRenderState> {
    private static final float HAUNCH_JUMP_PITCH_MULTIPLIER = 50.0f;
    private static final float FRONT_LEGS_JUMP_PITCH_MULTIPLIER = -40.0f;
    private static final float SCALE = 0.6f;
    private static final ModelTransformer ADULT_TRANSFORMER = ModelTransformer.scaling((float)0.6f);
    private static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 22.0f, 2.0f, 2.65f, 2.5f, 36.0f, Set.of("head", "left_ear", "right_ear", "nose"));
    private static final String LEFT_HAUNCH = "left_haunch";
    private static final String RIGHT_HAUNCH = "right_haunch";
    private final ModelPart leftHaunch;
    private final ModelPart rightHaunch;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart head;

    public RabbitEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.leftHaunch = modelPart.getChild(LEFT_HAUNCH);
        this.rightHaunch = modelPart.getChild(RIGHT_HAUNCH);
        this.leftFrontLeg = modelPart.getChild("left_front_leg");
        this.rightFrontLeg = modelPart.getChild("right_front_leg");
        this.head = modelPart.getChild("head");
    }

    public static TexturedModelData getTexturedModelData(boolean baby) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(LEFT_HAUNCH, ModelPartBuilder.create().uv(30, 15).cuboid(-1.0f, 0.0f, 0.0f, 2.0f, 4.0f, 5.0f), ModelTransform.of((float)3.0f, (float)17.5f, (float)3.7f, (float)-0.36651915f, (float)0.0f, (float)0.0f));
        ModelPartData modelPartData3 = modelPartData.addChild(RIGHT_HAUNCH, ModelPartBuilder.create().uv(16, 15).cuboid(-1.0f, 0.0f, 0.0f, 2.0f, 4.0f, 5.0f), ModelTransform.of((float)-3.0f, (float)17.5f, (float)3.7f, (float)-0.36651915f, (float)0.0f, (float)0.0f));
        modelPartData2.addChild("left_hind_foot", ModelPartBuilder.create().uv(26, 24).cuboid(-1.0f, 5.5f, -3.7f, 2.0f, 1.0f, 7.0f), ModelTransform.rotation((float)0.36651915f, (float)0.0f, (float)0.0f));
        modelPartData3.addChild("right_hind_foot", ModelPartBuilder.create().uv(8, 24).cuboid(-1.0f, 5.5f, -3.7f, 2.0f, 1.0f, 7.0f), ModelTransform.rotation((float)0.36651915f, (float)0.0f, (float)0.0f));
        modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0f, -2.0f, -10.0f, 6.0f, 5.0f, 10.0f), ModelTransform.of((float)0.0f, (float)19.0f, (float)8.0f, (float)-0.34906584f, (float)0.0f, (float)0.0f));
        modelPartData.addChild("left_front_leg", ModelPartBuilder.create().uv(8, 15).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 7.0f, 2.0f), ModelTransform.of((float)3.0f, (float)17.0f, (float)-1.0f, (float)-0.19198622f, (float)0.0f, (float)0.0f));
        modelPartData.addChild("right_front_leg", ModelPartBuilder.create().uv(0, 15).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 7.0f, 2.0f), ModelTransform.of((float)-3.0f, (float)17.0f, (float)-1.0f, (float)-0.19198622f, (float)0.0f, (float)0.0f));
        ModelPartData modelPartData4 = modelPartData.addChild("head", ModelPartBuilder.create().uv(32, 0).cuboid(-2.5f, -4.0f, -5.0f, 5.0f, 4.0f, 5.0f), ModelTransform.origin((float)0.0f, (float)16.0f, (float)-1.0f));
        modelPartData4.addChild("right_ear", ModelPartBuilder.create().uv(52, 0).cuboid(-2.5f, -9.0f, -1.0f, 2.0f, 5.0f, 1.0f), ModelTransform.of((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f, (float)-0.2617994f, (float)0.0f));
        modelPartData4.addChild("left_ear", ModelPartBuilder.create().uv(58, 0).cuboid(0.5f, -9.0f, -1.0f, 2.0f, 5.0f, 1.0f), ModelTransform.of((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f, (float)0.2617994f, (float)0.0f));
        modelPartData.addChild("tail", ModelPartBuilder.create().uv(52, 6).cuboid(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 2.0f), ModelTransform.of((float)0.0f, (float)20.0f, (float)7.0f, (float)-0.3490659f, (float)0.0f, (float)0.0f));
        modelPartData4.addChild("nose", ModelPartBuilder.create().uv(32, 9).cuboid(-0.5f, -2.5f, -5.5f, 1.0f, 1.0f, 1.0f), ModelTransform.NONE);
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)32).transform(baby ? BABY_TRANSFORMER : ADULT_TRANSFORMER);
    }

    public void setAngles(RabbitEntityRenderState rabbitEntityRenderState) {
        super.setAngles((Object)rabbitEntityRenderState);
        this.head.pitch = rabbitEntityRenderState.pitch * ((float)Math.PI / 180);
        this.head.yaw = rabbitEntityRenderState.relativeHeadYaw * ((float)Math.PI / 180);
        float f = MathHelper.sin((double)(rabbitEntityRenderState.jumpProgress * (float)Math.PI));
        this.leftHaunch.pitch += f * 50.0f * ((float)Math.PI / 180);
        this.rightHaunch.pitch += f * 50.0f * ((float)Math.PI / 180);
        this.leftFrontLeg.pitch += f * -40.0f * ((float)Math.PI / 180);
        this.rightFrontLeg.pitch += f * -40.0f * ((float)Math.PI / 180);
    }
}

