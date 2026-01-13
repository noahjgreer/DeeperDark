/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.model.ModelData
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.model.ModelPartBuilder
 *  net.minecraft.client.model.ModelPartData
 *  net.minecraft.client.model.ModelTransform
 *  net.minecraft.client.model.TexturedModelData
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.entity.model.BookModel
 *  net.minecraft.client.render.entity.model.BookModel$BookModelState
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BookModel
extends Model<BookModelState> {
    private static final String LEFT_PAGES = "left_pages";
    private static final String RIGHT_PAGES = "right_pages";
    private static final String FLIP_PAGE1 = "flip_page1";
    private static final String FLIP_PAGE2 = "flip_page2";
    private final ModelPart leftCover;
    private final ModelPart rightCover;
    private final ModelPart leftPages;
    private final ModelPart rightPages;
    private final ModelPart leftFlippingPage;
    private final ModelPart rightFlippingPage;

    public BookModel(ModelPart root) {
        super(root, RenderLayers::entitySolid);
        this.leftCover = root.getChild("left_lid");
        this.rightCover = root.getChild("right_lid");
        this.leftPages = root.getChild(LEFT_PAGES);
        this.rightPages = root.getChild(RIGHT_PAGES);
        this.leftFlippingPage = root.getChild(FLIP_PAGE1);
        this.rightFlippingPage = root.getChild(FLIP_PAGE2);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("left_lid", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0f, -5.0f, -0.005f, 6.0f, 10.0f, 0.005f), ModelTransform.origin((float)0.0f, (float)0.0f, (float)-1.0f));
        modelPartData.addChild("right_lid", ModelPartBuilder.create().uv(16, 0).cuboid(0.0f, -5.0f, -0.005f, 6.0f, 10.0f, 0.005f), ModelTransform.origin((float)0.0f, (float)0.0f, (float)1.0f));
        modelPartData.addChild("seam", ModelPartBuilder.create().uv(12, 0).cuboid(-1.0f, -5.0f, 0.0f, 2.0f, 10.0f, 0.005f), ModelTransform.rotation((float)0.0f, (float)1.5707964f, (float)0.0f));
        modelPartData.addChild(LEFT_PAGES, ModelPartBuilder.create().uv(0, 10).cuboid(0.0f, -4.0f, -0.99f, 5.0f, 8.0f, 1.0f), ModelTransform.NONE);
        modelPartData.addChild(RIGHT_PAGES, ModelPartBuilder.create().uv(12, 10).cuboid(0.0f, -4.0f, -0.01f, 5.0f, 8.0f, 1.0f), ModelTransform.NONE);
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(24, 10).cuboid(0.0f, -4.0f, 0.0f, 5.0f, 8.0f, 0.005f);
        modelPartData.addChild(FLIP_PAGE1, modelPartBuilder, ModelTransform.NONE);
        modelPartData.addChild(FLIP_PAGE2, modelPartBuilder, ModelTransform.NONE);
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)32);
    }

    public void setAngles(BookModelState bookModelState) {
        super.setAngles((Object)bookModelState);
        float f = (MathHelper.sin((double)(bookModelState.pageTurnAmount * 0.02f)) * 0.1f + 1.25f) * bookModelState.pageTurnSpeed;
        this.leftCover.yaw = (float)Math.PI + f;
        this.rightCover.yaw = -f;
        this.leftPages.yaw = f;
        this.rightPages.yaw = -f;
        this.leftFlippingPage.yaw = f - f * 2.0f * bookModelState.leftFlipAmount;
        this.rightFlippingPage.yaw = f - f * 2.0f * bookModelState.rightFlipAmount;
        this.leftPages.originX = MathHelper.sin((double)f);
        this.rightPages.originX = MathHelper.sin((double)f);
        this.leftFlippingPage.originX = MathHelper.sin((double)f);
        this.rightFlippingPage.originX = MathHelper.sin((double)f);
    }
}

