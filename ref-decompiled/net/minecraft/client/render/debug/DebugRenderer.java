/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.hud.debug.DebugHudEntries
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.debug.BeeDebugRenderer
 *  net.minecraft.client.render.debug.BlockOutlineDebugRenderer
 *  net.minecraft.client.render.debug.BrainDebugRenderer
 *  net.minecraft.client.render.debug.BreezeDebugRenderer
 *  net.minecraft.client.render.debug.ChunkBorderDebugRenderer
 *  net.minecraft.client.render.debug.ChunkDebugRenderer
 *  net.minecraft.client.render.debug.ChunkLoadingDebugRenderer
 *  net.minecraft.client.render.debug.CollisionDebugRenderer
 *  net.minecraft.client.render.debug.DebugRenderer
 *  net.minecraft.client.render.debug.DebugRenderer$Renderer
 *  net.minecraft.client.render.debug.EntityBlockIntersectionsDebugRenderer
 *  net.minecraft.client.render.debug.EntityHitboxDebugRenderer
 *  net.minecraft.client.render.debug.GameEventDebugRenderer
 *  net.minecraft.client.render.debug.GoalSelectorDebugRenderer
 *  net.minecraft.client.render.debug.HeightmapDebugRenderer
 *  net.minecraft.client.render.debug.LightDebugRenderer
 *  net.minecraft.client.render.debug.NeighborUpdateDebugRenderer
 *  net.minecraft.client.render.debug.OctreeDebugRenderer
 *  net.minecraft.client.render.debug.PathfindingDebugRenderer
 *  net.minecraft.client.render.debug.PoiDebugRenderer
 *  net.minecraft.client.render.debug.RaidCenterDebugRenderer
 *  net.minecraft.client.render.debug.RedstoneUpdateOrderDebugRenderer
 *  net.minecraft.client.render.debug.SkyLightDebugRenderer
 *  net.minecraft.client.render.debug.StructureDebugRenderer
 *  net.minecraft.client.render.debug.SupportingBlockDebugRenderer
 *  net.minecraft.client.render.debug.VillageSectionsDebugRenderer
 *  net.minecraft.client.render.debug.WaterDebugRenderer
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.projectile.ProjectileUtil
 *  net.minecraft.predicate.entity.EntityPredicates
 *  net.minecraft.util.hit.EntityHitResult
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.LightType
 *  net.minecraft.world.debug.DebugDataStore
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudEntries;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.BeeDebugRenderer;
import net.minecraft.client.render.debug.BlockOutlineDebugRenderer;
import net.minecraft.client.render.debug.BrainDebugRenderer;
import net.minecraft.client.render.debug.BreezeDebugRenderer;
import net.minecraft.client.render.debug.ChunkBorderDebugRenderer;
import net.minecraft.client.render.debug.ChunkDebugRenderer;
import net.minecraft.client.render.debug.ChunkLoadingDebugRenderer;
import net.minecraft.client.render.debug.CollisionDebugRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.debug.EntityBlockIntersectionsDebugRenderer;
import net.minecraft.client.render.debug.EntityHitboxDebugRenderer;
import net.minecraft.client.render.debug.GameEventDebugRenderer;
import net.minecraft.client.render.debug.GoalSelectorDebugRenderer;
import net.minecraft.client.render.debug.HeightmapDebugRenderer;
import net.minecraft.client.render.debug.LightDebugRenderer;
import net.minecraft.client.render.debug.NeighborUpdateDebugRenderer;
import net.minecraft.client.render.debug.OctreeDebugRenderer;
import net.minecraft.client.render.debug.PathfindingDebugRenderer;
import net.minecraft.client.render.debug.PoiDebugRenderer;
import net.minecraft.client.render.debug.RaidCenterDebugRenderer;
import net.minecraft.client.render.debug.RedstoneUpdateOrderDebugRenderer;
import net.minecraft.client.render.debug.SkyLightDebugRenderer;
import net.minecraft.client.render.debug.StructureDebugRenderer;
import net.minecraft.client.render.debug.SupportingBlockDebugRenderer;
import net.minecraft.client.render.debug.VillageSectionsDebugRenderer;
import net.minecraft.client.render.debug.WaterDebugRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.debug.DebugDataStore;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class DebugRenderer {
    private final List<Renderer> renderers = new ArrayList();
    private long currentVersion;

    public DebugRenderer() {
        this.initRenderers();
    }

    public void initRenderers() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        this.renderers.clear();
        if (minecraftClient.debugHudEntryList.isEntryVisible(DebugHudEntries.CHUNK_BORDERS)) {
            this.renderers.add(new ChunkBorderDebugRenderer(minecraftClient));
        }
        if (minecraftClient.debugHudEntryList.isEntryVisible(DebugHudEntries.CHUNK_SECTION_OCTREE)) {
            this.renderers.add(new OctreeDebugRenderer(minecraftClient));
        }
        if (SharedConstants.PATHFINDING) {
            this.renderers.add(new PathfindingDebugRenderer());
        }
        if (minecraftClient.debugHudEntryList.isEntryVisible(DebugHudEntries.VISUALIZE_WATER_LEVELS)) {
            this.renderers.add(new WaterDebugRenderer(minecraftClient));
        }
        if (minecraftClient.debugHudEntryList.isEntryVisible(DebugHudEntries.VISUALIZE_HEIGHTMAP)) {
            this.renderers.add(new HeightmapDebugRenderer(minecraftClient));
        }
        if (minecraftClient.debugHudEntryList.isEntryVisible(DebugHudEntries.VISUALIZE_COLLISION_BOXES)) {
            this.renderers.add(new CollisionDebugRenderer(minecraftClient));
        }
        if (minecraftClient.debugHudEntryList.isEntryVisible(DebugHudEntries.VISUALIZE_ENTITY_SUPPORTING_BLOCKS)) {
            this.renderers.add(new SupportingBlockDebugRenderer(minecraftClient));
        }
        if (SharedConstants.NEIGHBORSUPDATE) {
            this.renderers.add(new NeighborUpdateDebugRenderer());
        }
        if (SharedConstants.EXPERIMENTAL_REDSTONEWIRE_UPDATE_ORDER) {
            this.renderers.add(new RedstoneUpdateOrderDebugRenderer());
        }
        if (SharedConstants.STRUCTURES) {
            this.renderers.add(new StructureDebugRenderer());
        }
        if (minecraftClient.debugHudEntryList.isEntryVisible(DebugHudEntries.VISUALIZE_BLOCK_LIGHT_LEVELS) || minecraftClient.debugHudEntryList.isEntryVisible(DebugHudEntries.VISUALIZE_SKY_LIGHT_LEVELS)) {
            this.renderers.add(new SkyLightDebugRenderer(minecraftClient, minecraftClient.debugHudEntryList.isEntryVisible(DebugHudEntries.VISUALIZE_BLOCK_LIGHT_LEVELS), minecraftClient.debugHudEntryList.isEntryVisible(DebugHudEntries.VISUALIZE_SKY_LIGHT_LEVELS)));
        }
        if (minecraftClient.debugHudEntryList.isEntryVisible(DebugHudEntries.VISUALIZE_SOLID_FACES)) {
            this.renderers.add(new BlockOutlineDebugRenderer(minecraftClient));
        }
        if (SharedConstants.VILLAGE_SECTIONS) {
            this.renderers.add(new VillageSectionsDebugRenderer());
        }
        if (SharedConstants.BRAIN) {
            this.renderers.add(new BrainDebugRenderer(minecraftClient));
        }
        if (SharedConstants.POI) {
            this.renderers.add(new PoiDebugRenderer(new BrainDebugRenderer(minecraftClient)));
        }
        if (SharedConstants.BEES) {
            this.renderers.add(new BeeDebugRenderer(minecraftClient));
        }
        if (SharedConstants.RAIDS) {
            this.renderers.add(new RaidCenterDebugRenderer(minecraftClient));
        }
        if (SharedConstants.GOAL_SELECTOR) {
            this.renderers.add(new GoalSelectorDebugRenderer(minecraftClient));
        }
        if (minecraftClient.debugHudEntryList.isEntryVisible(DebugHudEntries.VISUALIZE_CHUNKS_ON_SERVER)) {
            this.renderers.add(new ChunkLoadingDebugRenderer(minecraftClient));
        }
        if (SharedConstants.GAME_EVENT_LISTENERS) {
            this.renderers.add(new GameEventDebugRenderer());
        }
        if (minecraftClient.debugHudEntryList.isEntryVisible(DebugHudEntries.VISUALIZE_SKY_LIGHT_SECTIONS)) {
            this.renderers.add(new LightDebugRenderer(minecraftClient, LightType.SKY));
        }
        if (SharedConstants.BREEZE_MOB) {
            this.renderers.add(new BreezeDebugRenderer(minecraftClient));
        }
        if (SharedConstants.ENTITY_BLOCK_INTERSECTION) {
            this.renderers.add(new EntityBlockIntersectionsDebugRenderer());
        }
        if (minecraftClient.debugHudEntryList.isEntryVisible(DebugHudEntries.ENTITY_HITBOXES)) {
            this.renderers.add(new EntityHitboxDebugRenderer(minecraftClient));
        }
        this.renderers.add(new ChunkDebugRenderer(minecraftClient));
    }

    public void render(Frustum frustum, double cameraX, double cameraY, double cameraZ, float tickProgress) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        DebugDataStore debugDataStore = minecraftClient.getNetworkHandler().getDebugDataStore();
        if (minecraftClient.debugHudEntryList.getVersion() != this.currentVersion) {
            this.currentVersion = minecraftClient.debugHudEntryList.getVersion();
            this.initRenderers();
        }
        for (Renderer renderer : this.renderers) {
            renderer.render(cameraX, cameraY, cameraZ, debugDataStore, frustum, tickProgress);
        }
    }

    public static Optional<Entity> getTargetedEntity(@Nullable Entity entity, int maxDistance) {
        int i;
        Box box;
        Vec3d vec3d2;
        Vec3d vec3d3;
        if (entity == null) {
            return Optional.empty();
        }
        Vec3d vec3d = entity.getEyePos();
        EntityHitResult entityHitResult = ProjectileUtil.raycast((Entity)entity, (Vec3d)vec3d, (Vec3d)(vec3d3 = vec3d.add(vec3d2 = entity.getRotationVec(1.0f).multiply((double)maxDistance))), (Box)(box = entity.getBoundingBox().stretch(vec3d2).expand(1.0)), (Predicate)EntityPredicates.CAN_HIT, (double)(i = maxDistance * maxDistance));
        if (entityHitResult == null) {
            return Optional.empty();
        }
        if (vec3d.squaredDistanceTo(entityHitResult.getPos()) > (double)i) {
            return Optional.empty();
        }
        return Optional.of(entityHitResult.getEntity());
    }

    private static Vec3d hueToRgb(float hue) {
        float f = 5.99999f;
        int i = (int)(MathHelper.clamp((float)hue, (float)0.0f, (float)1.0f) * 5.99999f);
        float g = hue * 5.99999f - (float)i;
        return switch (i) {
            case 0 -> new Vec3d(1.0, (double)g, 0.0);
            case 1 -> new Vec3d((double)(1.0f - g), 1.0, 0.0);
            case 2 -> new Vec3d(0.0, 1.0, (double)g);
            case 3 -> new Vec3d(0.0, 1.0 - (double)g, 1.0);
            case 4 -> new Vec3d((double)g, 0.0, 1.0);
            case 5 -> new Vec3d(1.0, 0.0, 1.0 - (double)g);
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };
    }

    private static Vec3d shiftHue(float r, float g, float b, float dHue) {
        Vec3d vec3d = DebugRenderer.hueToRgb((float)dHue).multiply((double)r);
        Vec3d vec3d2 = DebugRenderer.hueToRgb((float)((dHue + 0.33333334f) % 1.0f)).multiply((double)g);
        Vec3d vec3d3 = DebugRenderer.hueToRgb((float)((dHue + 0.6666667f) % 1.0f)).multiply((double)b);
        Vec3d vec3d4 = vec3d.add(vec3d2).add(vec3d3);
        double d = Math.max(Math.max(1.0, vec3d4.x), Math.max(vec3d4.y, vec3d4.z));
        return new Vec3d(vec3d4.x / d, vec3d4.y / d, vec3d4.z / d);
    }
}

