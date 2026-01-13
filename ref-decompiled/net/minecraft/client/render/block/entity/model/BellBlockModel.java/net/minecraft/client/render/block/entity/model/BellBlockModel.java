/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity.model;

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
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BellBlockModel
extends Model<BellModelState> {
    private static final String BELL_BODY = "bell_body";
    private final ModelPart bellBody;

    public BellBlockModel(ModelPart root) {
        super(root, RenderLayers::entitySolid);
        this.bellBody = root.getChild(BELL_BODY);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(BELL_BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-3.0f, -6.0f, -3.0f, 6.0f, 7.0f, 6.0f), ModelTransform.origin(8.0f, 12.0f, 8.0f));
        modelPartData2.addChild("bell_base", ModelPartBuilder.create().uv(0, 13).cuboid(4.0f, 4.0f, 4.0f, 8.0f, 2.0f, 8.0f), ModelTransform.origin(-8.0f, -12.0f, -8.0f));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(BellModelState bellModelState) {
        super.setAngles(bellModelState);
        float f = 0.0f;
        float g = 0.0f;
        if (bellModelState.shakeDirection != null) {
            float h = MathHelper.sin(bellModelState.ticks / (float)Math.PI) / (4.0f + bellModelState.ticks / 3.0f);
            switch (bellModelState.shakeDirection) {
                case NORTH: {
                    f = -h;
                    break;
                }
                case SOUTH: {
                    f = h;
                    break;
                }
                case EAST: {
                    g = -h;
                    break;
                }
                case WEST: {
                    g = h;
                }
            }
        }
        this.bellBody.pitch = f;
        this.bellBody.roll = g;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class BellModelState
    extends Record {
        final float ticks;
        final @Nullable Direction shakeDirection;

        public BellModelState(float ticks, @Nullable Direction shakeDirection) {
            this.ticks = ticks;
            this.shakeDirection = shakeDirection;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{BellModelState.class, "ticks;shakeDirection", "ticks", "shakeDirection"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BellModelState.class, "ticks;shakeDirection", "ticks", "shakeDirection"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BellModelState.class, "ticks;shakeDirection", "ticks", "shakeDirection"}, this, object);
        }

        public float ticks() {
            return this.ticks;
        }

        public @Nullable Direction shakeDirection() {
            return this.shakeDirection;
        }
    }
}
