/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix3x2f
 *  org.joml.Matrix3x2fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.font;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record DrawnTextConsumer.Transformation(Matrix3x2fc pose, float opacity, @Nullable ScreenRect scissor) {
    public DrawnTextConsumer.Transformation(Matrix3x2fc pose) {
        this(pose, 1.0f, null);
    }

    public DrawnTextConsumer.Transformation withPose(Matrix3x2fc pose) {
        return new DrawnTextConsumer.Transformation(pose, this.opacity, this.scissor);
    }

    public DrawnTextConsumer.Transformation scaled(float scale) {
        return this.withPose((Matrix3x2fc)this.pose.scale(scale, scale, new Matrix3x2f()));
    }

    public DrawnTextConsumer.Transformation withOpacity(float opacity) {
        if (this.opacity == opacity) {
            return this;
        }
        return new DrawnTextConsumer.Transformation(this.pose, opacity, this.scissor);
    }

    public DrawnTextConsumer.Transformation withScissor(ScreenRect scissor) {
        if (scissor.equals(this.scissor)) {
            return this;
        }
        return new DrawnTextConsumer.Transformation(this.pose, this.opacity, scissor);
    }

    public DrawnTextConsumer.Transformation withScissor(int left, int right, int top, int bottom) {
        ScreenRect screenRect = new ScreenRect(left, top, right - left, bottom - top).transform(this.pose);
        if (this.scissor != null) {
            screenRect = Objects.requireNonNullElse(this.scissor.intersection(screenRect), ScreenRect.empty());
        }
        return this.withScissor(screenRect);
    }
}
