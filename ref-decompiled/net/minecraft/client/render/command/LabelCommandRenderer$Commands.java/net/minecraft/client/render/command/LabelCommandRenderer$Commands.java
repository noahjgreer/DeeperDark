/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.command;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class LabelCommandRenderer.Commands {
    final List<OrderedRenderCommandQueueImpl.LabelCommand> seethroughLabels = new ArrayList<OrderedRenderCommandQueueImpl.LabelCommand>();
    final List<OrderedRenderCommandQueueImpl.LabelCommand> normalLabels = new ArrayList<OrderedRenderCommandQueueImpl.LabelCommand>();

    public void add(MatrixStack matrices, @Nullable Vec3d pos, int y, Text label, boolean notSneaking, int light, double squaredDistanceToCamera, CameraRenderState cameraState) {
        if (pos == null) {
            return;
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        matrices.push();
        matrices.translate(pos.x, pos.y + 0.5, pos.z);
        matrices.multiply((Quaternionfc)cameraState.orientation);
        matrices.scale(0.025f, -0.025f, 0.025f);
        Matrix4f matrix4f = new Matrix4f((Matrix4fc)matrices.peek().getPositionMatrix());
        float f = (float)(-minecraftClient.textRenderer.getWidth(label)) / 2.0f;
        int i = (int)(minecraftClient.options.getTextBackgroundOpacity(0.25f) * 255.0f) << 24;
        if (notSneaking) {
            this.normalLabels.add(new OrderedRenderCommandQueueImpl.LabelCommand(matrix4f, f, y, label, LightmapTextureManager.applyEmission(light, 2), -1, 0, squaredDistanceToCamera));
            this.seethroughLabels.add(new OrderedRenderCommandQueueImpl.LabelCommand(matrix4f, f, y, label, light, -2130706433, i, squaredDistanceToCamera));
        } else {
            this.normalLabels.add(new OrderedRenderCommandQueueImpl.LabelCommand(matrix4f, f, y, label, light, -2130706433, i, squaredDistanceToCamera));
        }
        matrices.pop();
    }

    public void clear() {
        this.normalLabels.clear();
        this.seethroughLabels.clear();
    }
}
