/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.DebugSubscriptionTypes;
import net.minecraft.world.debug.data.BrainDebugData;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BrainDebugRenderer
implements DebugRenderer.Renderer {
    private static final boolean field_32874 = true;
    private static final boolean field_32875 = false;
    private static final boolean field_32876 = false;
    private static final boolean field_32877 = false;
    private static final boolean field_32878 = false;
    private static final boolean field_32879 = false;
    private static final boolean field_32881 = false;
    private static final boolean field_32882 = true;
    private static final boolean field_38346 = false;
    private static final boolean field_32883 = true;
    private static final boolean field_32884 = true;
    private static final boolean field_32885 = true;
    private static final boolean field_32886 = true;
    private static final boolean field_32887 = true;
    private static final boolean field_32888 = true;
    private static final boolean field_32889 = true;
    private static final boolean field_32891 = true;
    private static final boolean field_32892 = true;
    private static final boolean field_38347 = true;
    private static final int POI_RANGE = 30;
    private static final int TARGET_ENTITY_RANGE = 8;
    private static final float DEFAULT_DRAWN_STRING_SIZE = 0.32f;
    private static final int AQUA = -16711681;
    private static final int GRAY = -3355444;
    private static final int PINK = -98404;
    private static final int ORANGE = -23296;
    private final MinecraftClient client;
    private @Nullable UUID targetedEntity;

    public BrainDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        this.draw(store);
        if (!this.client.player.isSpectator()) {
            this.updateTargetedEntity();
        }
    }

    private void draw(DebugDataStore store) {
        store.forEachEntityData(DebugSubscriptionTypes.BRAINS, (entity, brain) -> {
            if (this.client.player.isInRange((Entity)entity, 30.0)) {
                this.drawBrain((Entity)entity, (BrainDebugData)brain);
            }
        });
    }

    private void drawBrain(Entity entity, BrainDebugData brain) {
        boolean bl = this.isTargeted(entity);
        int i = 0;
        GizmoDrawing.entityLabel(entity, i, brain.name(), -1, 0.48f);
        ++i;
        if (bl) {
            GizmoDrawing.entityLabel(entity, i, brain.profession() + " " + brain.xp() + " xp", -1, 0.32f);
            ++i;
        }
        if (bl) {
            int j = brain.health() < brain.maxHealth() ? -23296 : -1;
            GizmoDrawing.entityLabel(entity, i, "health: " + String.format(Locale.ROOT, "%.1f", Float.valueOf(brain.health())) + " / " + String.format(Locale.ROOT, "%.1f", Float.valueOf(brain.maxHealth())), j, 0.32f);
            ++i;
        }
        if (bl && !brain.inventory().equals("")) {
            GizmoDrawing.entityLabel(entity, i, brain.inventory(), -98404, 0.32f);
            ++i;
        }
        if (bl) {
            for (String string : brain.behaviors()) {
                GizmoDrawing.entityLabel(entity, i, string, -16711681, 0.32f);
                ++i;
            }
        }
        if (bl) {
            for (String string : brain.activities()) {
                GizmoDrawing.entityLabel(entity, i, string, -16711936, 0.32f);
                ++i;
            }
        }
        if (brain.wantsGolem()) {
            GizmoDrawing.entityLabel(entity, i, "Wants Golem", -23296, 0.32f);
            ++i;
        }
        if (bl && brain.angerLevel() != -1) {
            GizmoDrawing.entityLabel(entity, i, "Anger Level: " + brain.angerLevel(), -98404, 0.32f);
            ++i;
        }
        if (bl) {
            for (String string : brain.gossips()) {
                if (string.startsWith(brain.name())) {
                    GizmoDrawing.entityLabel(entity, i, string, -1, 0.32f);
                } else {
                    GizmoDrawing.entityLabel(entity, i, string, -23296, 0.32f);
                }
                ++i;
            }
        }
        if (bl) {
            for (String string : Lists.reverse(brain.memories())) {
                GizmoDrawing.entityLabel(entity, i, string, -3355444, 0.32f);
                ++i;
            }
        }
    }

    private boolean isTargeted(Entity entity) {
        return Objects.equals(this.targetedEntity, entity.getUuid());
    }

    public Map<BlockPos, List<String>> getGhostPointsOfInterest(DebugDataStore store) {
        HashMap map = Maps.newHashMap();
        store.forEachEntityData(DebugSubscriptionTypes.BRAINS, (entity, data) -> {
            for (BlockPos blockPos : Iterables.concat(data.pois(), data.potentialPois())) {
                map.computeIfAbsent(blockPos, pos -> Lists.newArrayList()).add(data.name());
            }
        });
        return map;
    }

    private void updateTargetedEntity() {
        DebugRenderer.getTargetedEntity(this.client.getCameraEntity(), 8).ifPresent(entity -> {
            this.targetedEntity = entity.getUuid();
        });
    }
}
