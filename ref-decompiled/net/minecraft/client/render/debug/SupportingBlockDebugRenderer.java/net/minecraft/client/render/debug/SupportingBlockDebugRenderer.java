/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

@Environment(value=EnvType.CLIENT)
public class SupportingBlockDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;
    private double lastEntityCheckTime = Double.MIN_VALUE;
    private List<Entity> entities = Collections.emptyList();

    public SupportingBlockDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        ClientPlayerEntity playerEntity;
        double d = Util.getMeasuringTimeNano();
        if (d - this.lastEntityCheckTime > 1.0E8) {
            this.lastEntityCheckTime = d;
            Entity entity = this.client.gameRenderer.getCamera().getFocusedEntity();
            this.entities = ImmutableList.copyOf(entity.getEntityWorld().getOtherEntities(entity, entity.getBoundingBox().expand(16.0)));
        }
        if ((playerEntity = this.client.player) != null && playerEntity.supportingBlockPos.isPresent()) {
            this.renderBlockHighlights(playerEntity, () -> 0.0, -65536);
        }
        for (Entity entity2 : this.entities) {
            if (entity2 == playerEntity) continue;
            this.renderBlockHighlights(entity2, () -> this.getAdditionalDilation(entity2), -16711936);
        }
    }

    private void renderBlockHighlights(Entity entity, DoubleSupplier dilationSupplier, int colr) {
        entity.supportingBlockPos.ifPresent(pos -> {
            double d = dilationSupplier.getAsDouble();
            BlockPos blockPos = entity.getSteppingPos();
            this.renderBlockHighlight(blockPos, 0.02 + d, colr);
            BlockPos blockPos2 = entity.getLandingPos();
            if (!blockPos2.equals(blockPos)) {
                this.renderBlockHighlight(blockPos2, 0.04 + d, -16711681);
            }
        });
    }

    private double getAdditionalDilation(Entity entity) {
        return 0.02 * (double)(String.valueOf((double)entity.getId() + 0.132453657).hashCode() % 1000) / 1000.0;
    }

    private void renderBlockHighlight(BlockPos pos, double d, int i) {
        double e = (double)pos.getX() - 2.0 * d;
        double f = (double)pos.getY() - 2.0 * d;
        double g = (double)pos.getZ() - 2.0 * d;
        double h = e + 1.0 + 4.0 * d;
        double j = f + 1.0 + 4.0 * d;
        double k = g + 1.0 + 4.0 * d;
        GizmoDrawing.box(new Box(e, f, g, h, j, k), DrawStyle.stroked(ColorHelper.withAlpha(0.4f, i)));
        VoxelShape voxelShape = this.client.world.getBlockState(pos).getCollisionShape(this.client.world, pos, ShapeContext.absent()).offset(pos);
        DrawStyle drawStyle = DrawStyle.stroked(i);
        for (Box box : voxelShape.getBoundingBoxes()) {
            GizmoDrawing.box(box, drawStyle);
        }
    }
}
