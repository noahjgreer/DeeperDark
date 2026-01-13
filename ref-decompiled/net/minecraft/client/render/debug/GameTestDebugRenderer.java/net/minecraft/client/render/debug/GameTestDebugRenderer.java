/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import net.minecraft.world.debug.gizmo.TextGizmo;

@Environment(value=EnvType.CLIENT)
public class GameTestDebugRenderer {
    private static final int MARKER_LIFESPAN_MS = 10000;
    private static final float MARKER_BOX_SIZE = 0.02f;
    private final Map<BlockPos, Marker> markers = Maps.newHashMap();

    public void addMarker(BlockPos absolutePos, BlockPos relativePos) {
        String string = relativePos.toShortString();
        this.markers.put(absolutePos, new Marker(0x6000FF00, string, Util.getMeasuringTimeMs() + 10000L));
    }

    public void clear() {
        this.markers.clear();
    }

    public void render() {
        long l = Util.getMeasuringTimeMs();
        this.markers.entrySet().removeIf(marker -> l > ((Marker)marker.getValue()).removalTime);
        this.markers.forEach((pos, marker) -> this.render((BlockPos)pos, (Marker)marker));
    }

    private void render(BlockPos blockPos, Marker marker) {
        GizmoDrawing.box(blockPos, 0.02f, DrawStyle.filled(marker.color()));
        if (!marker.message.isEmpty()) {
            GizmoDrawing.text(marker.message, Vec3d.add(blockPos, 0.5, 1.2, 0.5), TextGizmo.Style.left().scaled(0.16f)).ignoreOcclusion();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class Marker
    extends Record {
        private final int color;
        final String message;
        final long removalTime;

        Marker(int color, String message, long removalTime) {
            this.color = color;
            this.message = message;
            this.removalTime = removalTime;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Marker.class, "color;text;removeAtTime", "color", "message", "removalTime"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Marker.class, "color;text;removeAtTime", "color", "message", "removalTime"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Marker.class, "color;text;removeAtTime", "color", "message", "removalTime"}, this, object);
        }

        public int color() {
            return this.color;
        }

        public String message() {
            return this.message;
        }

        public long removalTime() {
            return this.removalTime;
        }
    }
}
