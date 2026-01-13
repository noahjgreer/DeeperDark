/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.DebugSubscriptionTypes;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import net.minecraft.world.debug.gizmo.TextGizmo;

@Environment(value=EnvType.CLIENT)
public class NeighborUpdateDebugRenderer
implements DebugRenderer.Renderer {
    @Override
    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        Update update;
        BlockPos blockPos;
        int i = DebugSubscriptionTypes.NEIGHBOR_UPDATES.getExpiry();
        double d = 1.0 / (double)(i * 2);
        HashMap map = new HashMap();
        store.forEachEvent(DebugSubscriptionTypes.NEIGHBOR_UPDATES, (pos, remainingTime, expiry) -> {
            long l = expiry - remainingTime;
            Update update = map.getOrDefault(pos, Update.EMPTY);
            map.put(pos, update.withAge((int)l));
        });
        for (Map.Entry entry : map.entrySet()) {
            blockPos = (BlockPos)entry.getKey();
            update = (Update)entry.getValue();
            Box box = new Box(blockPos).expand(0.002).contract(d * (double)update.age);
            GizmoDrawing.box(box, DrawStyle.stroked(-1));
        }
        for (Map.Entry entry : map.entrySet()) {
            blockPos = (BlockPos)entry.getKey();
            update = (Update)entry.getValue();
            GizmoDrawing.text(String.valueOf(update.count), Vec3d.ofCenter(blockPos), TextGizmo.Style.left());
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class Update
    extends Record {
        final int count;
        final int age;
        static final Update EMPTY = new Update(0, Integer.MAX_VALUE);

        private Update(int count, int age) {
            this.count = count;
            this.age = age;
        }

        public Update withAge(int age) {
            if (age == this.age) {
                return new Update(this.count + 1, age);
            }
            if (age < this.age) {
                return new Update(1, age);
            }
            return this;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Update.class, "count;age", "count", "age"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Update.class, "count;age", "count", "age"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Update.class, "count;age", "count", "age"}, this, object);
        }

        public int count() {
            return this.count;
        }

        public int age() {
            return this.age;
        }
    }
}
