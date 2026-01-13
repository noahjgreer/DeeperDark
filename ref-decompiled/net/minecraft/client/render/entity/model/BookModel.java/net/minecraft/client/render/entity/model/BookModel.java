/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
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
        modelPartData.addChild("left_lid", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0f, -5.0f, -0.005f, 6.0f, 10.0f, 0.005f), ModelTransform.origin(0.0f, 0.0f, -1.0f));
        modelPartData.addChild("right_lid", ModelPartBuilder.create().uv(16, 0).cuboid(0.0f, -5.0f, -0.005f, 6.0f, 10.0f, 0.005f), ModelTransform.origin(0.0f, 0.0f, 1.0f));
        modelPartData.addChild("seam", ModelPartBuilder.create().uv(12, 0).cuboid(-1.0f, -5.0f, 0.0f, 2.0f, 10.0f, 0.005f), ModelTransform.rotation(0.0f, 1.5707964f, 0.0f));
        modelPartData.addChild(LEFT_PAGES, ModelPartBuilder.create().uv(0, 10).cuboid(0.0f, -4.0f, -0.99f, 5.0f, 8.0f, 1.0f), ModelTransform.NONE);
        modelPartData.addChild(RIGHT_PAGES, ModelPartBuilder.create().uv(12, 10).cuboid(0.0f, -4.0f, -0.01f, 5.0f, 8.0f, 1.0f), ModelTransform.NONE);
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(24, 10).cuboid(0.0f, -4.0f, 0.0f, 5.0f, 8.0f, 0.005f);
        modelPartData.addChild(FLIP_PAGE1, modelPartBuilder, ModelTransform.NONE);
        modelPartData.addChild(FLIP_PAGE2, modelPartBuilder, ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(BookModelState bookModelState) {
        super.setAngles(bookModelState);
        float f = (MathHelper.sin(bookModelState.pageTurnAmount * 0.02f) * 0.1f + 1.25f) * bookModelState.pageTurnSpeed;
        this.leftCover.yaw = (float)Math.PI + f;
        this.rightCover.yaw = -f;
        this.leftPages.yaw = f;
        this.rightPages.yaw = -f;
        this.leftFlippingPage.yaw = f - f * 2.0f * bookModelState.leftFlipAmount;
        this.rightFlippingPage.yaw = f - f * 2.0f * bookModelState.rightFlipAmount;
        this.leftPages.originX = MathHelper.sin(f);
        this.rightPages.originX = MathHelper.sin(f);
        this.leftFlippingPage.originX = MathHelper.sin(f);
        this.rightFlippingPage.originX = MathHelper.sin(f);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class BookModelState
    extends Record {
        final float pageTurnAmount;
        final float leftFlipAmount;
        final float rightFlipAmount;
        final float pageTurnSpeed;

        public BookModelState(float pageTurnAmount, float leftFlipAmount, float rightFlipAmount, float pageTurnSpeed) {
            this.pageTurnAmount = pageTurnAmount;
            this.leftFlipAmount = leftFlipAmount;
            this.rightFlipAmount = rightFlipAmount;
            this.pageTurnSpeed = pageTurnSpeed;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{BookModelState.class, "animationPos;pageFlip1;pageFlip2;open", "pageTurnAmount", "leftFlipAmount", "rightFlipAmount", "pageTurnSpeed"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BookModelState.class, "animationPos;pageFlip1;pageFlip2;open", "pageTurnAmount", "leftFlipAmount", "rightFlipAmount", "pageTurnSpeed"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BookModelState.class, "animationPos;pageFlip1;pageFlip2;open", "pageTurnAmount", "leftFlipAmount", "rightFlipAmount", "pageTurnSpeed"}, this, object);
        }

        public float pageTurnAmount() {
            return this.pageTurnAmount;
        }

        public float leftFlipAmount() {
            return this.leftFlipAmount;
        }

        public float rightFlipAmount() {
            return this.rightFlipAmount;
        }

        public float pageTurnSpeed() {
            return this.pageTurnSpeed;
        }
    }
}
