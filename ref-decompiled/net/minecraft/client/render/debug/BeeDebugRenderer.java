/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.render.DrawStyle
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.debug.BeeDebugRenderer
 *  net.minecraft.client.render.debug.DebugRenderer
 *  net.minecraft.client.render.debug.DebugRenderer$Renderer
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.NameGenerator
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.debug.DebugDataStore
 *  net.minecraft.world.debug.DebugSubscriptionTypes
 *  net.minecraft.world.debug.data.BeeDebugData
 *  net.minecraft.world.debug.data.BeeHiveDebugData
 *  net.minecraft.world.debug.data.GoalSelectorDebugData
 *  net.minecraft.world.debug.data.GoalSelectorDebugData$Goal
 *  net.minecraft.world.debug.gizmo.GizmoDrawing
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.NameGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.DebugSubscriptionTypes;
import net.minecraft.world.debug.data.BeeDebugData;
import net.minecraft.world.debug.data.BeeHiveDebugData;
import net.minecraft.world.debug.data.GoalSelectorDebugData;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class BeeDebugRenderer
implements DebugRenderer.Renderer {
    private static final boolean field_32841 = true;
    private static final boolean field_32842 = true;
    private static final boolean field_32843 = true;
    private static final boolean field_32844 = true;
    private static final boolean field_32845 = true;
    private static final boolean field_32847 = true;
    private static final boolean field_32848 = true;
    private static final boolean field_32849 = true;
    private static final boolean field_32850 = true;
    private static final boolean field_32851 = true;
    private static final boolean field_32853 = true;
    private static final boolean field_32854 = true;
    private static final int HIVE_RANGE = 30;
    private static final int BEE_RANGE = 30;
    private static final int TARGET_ENTITY_RANGE = 8;
    private static final float DEFAULT_DRAWN_STRING_SIZE = 0.32f;
    private static final int ORANGE = -23296;
    private static final int GRAY = -3355444;
    private static final int PINK = -98404;
    private final MinecraftClient client;
    private @Nullable UUID targetedEntity;

    public BeeDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        this.render(store);
        if (!this.client.player.isSpectator()) {
            this.updateTargetedEntity();
        }
    }

    private void render(DebugDataStore store) {
        BlockPos blockPos = this.getCameraPos().getBlockPos();
        store.forEachEntityData(DebugSubscriptionTypes.BEES, (bee, debugData) -> {
            if (this.client.player.isInRange(bee, 30.0)) {
                GoalSelectorDebugData goalSelectorDebugData = (GoalSelectorDebugData)store.getEntityData(DebugSubscriptionTypes.GOAL_SELECTORS, bee);
                this.drawBee(bee, debugData, goalSelectorDebugData);
            }
        });
        this.drawFlowers(store);
        Map map = this.getBlacklistingBees(store);
        store.forEachBlockData(DebugSubscriptionTypes.BEE_HIVES, (hivePos, debugData) -> {
            if (blockPos.isWithinDistance((Vec3i)hivePos, 30.0)) {
                BeeDebugRenderer.drawHive((BlockPos)hivePos);
                Set set = map.getOrDefault(hivePos, Set.of());
                this.drawHiveInfo(hivePos, debugData, set, store);
            }
        });
        this.getBeesByHive(store).forEach((hivePos, names) -> {
            if (blockPos.isWithinDistance((Vec3i)hivePos, 30.0)) {
                this.drawHiveBees(hivePos, names);
            }
        });
    }

    private Map<BlockPos, Set<UUID>> getBlacklistingBees(DebugDataStore dataStore) {
        HashMap<BlockPos, Set<UUID>> map = new HashMap<BlockPos, Set<UUID>>();
        dataStore.forEachEntityData(DebugSubscriptionTypes.BEES, (entity, data) -> {
            for (BlockPos blockPos : data.blacklistedHives()) {
                map.computeIfAbsent(blockPos, pos -> new HashSet()).add(entity.getUuid());
            }
        });
        return map;
    }

    private void drawFlowers(DebugDataStore store) {
        HashMap<BlockPos, Set> map = new HashMap<BlockPos, Set>();
        store.forEachEntityData(DebugSubscriptionTypes.BEES, (entity, data) -> {
            if (data.flowerPos().isPresent()) {
                map.computeIfAbsent((BlockPos)data.flowerPos().get(), flower -> new HashSet()).add(entity.getUuid());
            }
        });
        map.forEach((flowerPos, uuids) -> {
            Set set = uuids.stream().map(NameGenerator::name).collect(Collectors.toSet());
            int i = 1;
            GizmoDrawing.blockLabel((String)set.toString(), (BlockPos)flowerPos, (int)i++, (int)-256, (float)0.32f);
            GizmoDrawing.blockLabel((String)"Flower", (BlockPos)flowerPos, (int)i++, (int)-1, (float)0.32f);
            GizmoDrawing.box((BlockPos)flowerPos, (float)0.05f, (DrawStyle)DrawStyle.filled((int)ColorHelper.fromFloats((float)0.3f, (float)0.8f, (float)0.8f, (float)0.0f)));
        });
    }

    private static String toString(Collection<UUID> bees) {
        if (bees.isEmpty()) {
            return "-";
        }
        if (bees.size() > 3) {
            return bees.size() + " bees";
        }
        return bees.stream().map(NameGenerator::name).collect(Collectors.toSet()).toString();
    }

    private static void drawHive(BlockPos hivePos) {
        float f = 0.05f;
        GizmoDrawing.box((BlockPos)hivePos, (float)0.05f, (DrawStyle)DrawStyle.filled((int)ColorHelper.fromFloats((float)0.3f, (float)0.2f, (float)0.2f, (float)1.0f)));
    }

    private void drawHiveBees(BlockPos hivePos, List<String> names) {
        float f = 0.05f;
        GizmoDrawing.box((BlockPos)hivePos, (float)0.05f, (DrawStyle)DrawStyle.filled((int)ColorHelper.fromFloats((float)0.3f, (float)0.2f, (float)0.2f, (float)1.0f)));
        GizmoDrawing.blockLabel((String)names.toString(), (BlockPos)hivePos, (int)0, (int)-256, (float)0.32f);
        GizmoDrawing.blockLabel((String)"Ghost Hive", (BlockPos)hivePos, (int)1, (int)-65536, (float)0.32f);
    }

    private void drawHiveInfo(BlockPos blockPos, BeeHiveDebugData debugData, Collection<UUID> blacklist, DebugDataStore store) {
        int i = 0;
        if (!blacklist.isEmpty()) {
            BeeDebugRenderer.drawString((String)("Blacklisted by " + BeeDebugRenderer.toString(blacklist)), (BlockPos)blockPos, (int)i++, (int)-65536);
        }
        BeeDebugRenderer.drawString((String)("Out: " + BeeDebugRenderer.toString((Collection)this.getBeesForHive(blockPos, store))), (BlockPos)blockPos, (int)i++, (int)-3355444);
        if (debugData.occupantCount() == 0) {
            BeeDebugRenderer.drawString((String)"In: -", (BlockPos)blockPos, (int)i++, (int)-256);
        } else if (debugData.occupantCount() == 1) {
            BeeDebugRenderer.drawString((String)"In: 1 bee", (BlockPos)blockPos, (int)i++, (int)-256);
        } else {
            BeeDebugRenderer.drawString((String)("In: " + debugData.occupantCount() + " bees"), (BlockPos)blockPos, (int)i++, (int)-256);
        }
        BeeDebugRenderer.drawString((String)("Honey: " + debugData.honeyLevel()), (BlockPos)blockPos, (int)i++, (int)-23296);
        BeeDebugRenderer.drawString((String)(debugData.type().getName().getString() + (debugData.sedated() ? " (sedated)" : "")), (BlockPos)blockPos, (int)i++, (int)-1);
    }

    private void drawBee(Entity bee, BeeDebugData debugData, @Nullable GoalSelectorDebugData goalSelectorDebugData) {
        boolean bl = this.isTargeted(bee);
        int i = 0;
        GizmoDrawing.entityLabel((Entity)bee, (int)i++, (String)debugData.toString(), (int)-1, (float)0.48f);
        if (debugData.hivePos().isEmpty()) {
            GizmoDrawing.entityLabel((Entity)bee, (int)i++, (String)"No hive", (int)-98404, (float)0.32f);
        } else {
            GizmoDrawing.entityLabel((Entity)bee, (int)i++, (String)("Hive: " + this.getPositionString(bee, (BlockPos)debugData.hivePos().get())), (int)-256, (float)0.32f);
        }
        if (debugData.flowerPos().isEmpty()) {
            GizmoDrawing.entityLabel((Entity)bee, (int)i++, (String)"No flower", (int)-98404, (float)0.32f);
        } else {
            GizmoDrawing.entityLabel((Entity)bee, (int)i++, (String)("Flower: " + this.getPositionString(bee, (BlockPos)debugData.flowerPos().get())), (int)-256, (float)0.32f);
        }
        if (goalSelectorDebugData != null) {
            for (GoalSelectorDebugData.Goal goal : goalSelectorDebugData.goals()) {
                if (!goal.isRunning()) continue;
                GizmoDrawing.entityLabel((Entity)bee, (int)i++, (String)goal.name(), (int)-16711936, (float)0.32f);
            }
        }
        if (debugData.travelTicks() > 0) {
            int j = debugData.travelTicks() < 2400 ? -3355444 : -23296;
            GizmoDrawing.entityLabel((Entity)bee, (int)i++, (String)("Travelling: " + debugData.travelTicks() + " ticks"), (int)j, (float)0.32f);
        }
    }

    private static void drawString(String string, BlockPos blockPos, int yOffset, int color) {
        GizmoDrawing.blockLabel((String)string, (BlockPos)blockPos, (int)yOffset, (int)color, (float)0.32f);
    }

    private Camera getCameraPos() {
        return this.client.gameRenderer.getCamera();
    }

    private String getPositionString(Entity bee, BlockPos pos) {
        double d = pos.getSquaredDistance((Position)bee.getEntityPos());
        double e = (double)Math.round(d * 10.0) / 10.0;
        return pos.toShortString() + " (dist " + e + ")";
    }

    private boolean isTargeted(Entity bee) {
        return Objects.equals(this.targetedEntity, bee.getUuid());
    }

    private Collection<UUID> getBeesForHive(BlockPos pos, DebugDataStore dataStore) {
        HashSet<UUID> set = new HashSet<UUID>();
        dataStore.forEachEntityData(DebugSubscriptionTypes.BEES, (entity, data) -> {
            if (data.hivePosEquals(pos)) {
                set.add(entity.getUuid());
            }
        });
        return set;
    }

    private Map<BlockPos, List<String>> getBeesByHive(DebugDataStore dataStore) {
        HashMap<BlockPos, List<String>> map = new HashMap<BlockPos, List<String>>();
        dataStore.forEachEntityData(DebugSubscriptionTypes.BEES, (entity, data) -> {
            if (data.hivePos().isPresent() && dataStore.getBlockData(DebugSubscriptionTypes.BEE_HIVES, (BlockPos)data.hivePos().get()) == null) {
                map.computeIfAbsent((BlockPos)data.hivePos().get(), hive -> Lists.newArrayList()).add(NameGenerator.name((Entity)entity));
            }
        });
        return map;
    }

    private void updateTargetedEntity() {
        DebugRenderer.getTargetedEntity((Entity)this.client.getCameraEntity(), (int)8).ifPresent(entity -> {
            this.targetedEntity = entity.getUuid();
        });
    }
}

