/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.hud.bar.Bar
 *  net.minecraft.client.gui.hud.bar.LocatorBar
 *  net.minecraft.client.render.RenderTickCounter
 *  net.minecraft.client.resource.waypoint.WaypointStyleAsset
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 *  net.minecraft.world.tick.TickManager
 *  net.minecraft.world.waypoint.EntityTickProgress
 *  net.minecraft.world.waypoint.TrackedWaypoint$Pitch
 *  net.minecraft.world.waypoint.TrackedWaypoint$PitchProvider
 *  net.minecraft.world.waypoint.TrackedWaypoint$YawProvider
 *  net.minecraft.world.waypoint.Waypoint$Config
 */
package net.minecraft.client.gui.hud.bar;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.Bar;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.resource.waypoint.WaypointStyleAsset;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickManager;
import net.minecraft.world.waypoint.EntityTickProgress;
import net.minecraft.world.waypoint.TrackedWaypoint;
import net.minecraft.world.waypoint.Waypoint;

@Environment(value=EnvType.CLIENT)
public class LocatorBar
implements Bar {
    private static final Identifier BACKGROUND = Identifier.ofVanilla((String)"hud/locator_bar_background");
    private static final Identifier ARROW_UP = Identifier.ofVanilla((String)"hud/locator_bar_arrow_up");
    private static final Identifier ARROW_DOWN = Identifier.ofVanilla((String)"hud/locator_bar_arrow_down");
    private static final int field_59852 = 9;
    private static final int field_59854 = 60;
    private static final int field_59855 = 7;
    private static final int field_59856 = 5;
    private static final int field_60309 = 1;
    private static final int field_60453 = 1;
    private final MinecraftClient client;

    public LocatorBar(MinecraftClient client) {
        this.client = client;
    }

    public void renderBar(DrawContext context, RenderTickCounter tickCounter) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.getCenterX(this.client.getWindow()), this.getCenterY(this.client.getWindow()), 182, 5);
    }

    public void renderAddons(DrawContext context, RenderTickCounter tickCounter) {
        int i = this.getCenterY(this.client.getWindow());
        Entity entity2 = this.client.getCameraEntity();
        if (entity2 == null) {
            return;
        }
        World world = entity2.getEntityWorld();
        TickManager tickManager = world.getTickManager();
        EntityTickProgress entityTickProgress = entity -> tickCounter.getTickProgress(!tickManager.shouldSkipTick(entity));
        this.client.player.networkHandler.getWaypointHandler().forEachWaypoint(entity2, waypoint -> {
            if (waypoint.getSource().left().map(uuid -> uuid.equals(entity2.getUuid())).orElse(false).booleanValue()) {
                return;
            }
            double d = waypoint.getRelativeYaw(world, (TrackedWaypoint.YawProvider)this.client.gameRenderer.getCamera(), entityTickProgress);
            if (d <= -60.0 || d > 60.0) {
                return;
            }
            int j = MathHelper.ceil((float)((float)(context.getScaledWindowWidth() - 9) / 2.0f));
            Waypoint.Config config = waypoint.getConfig();
            WaypointStyleAsset waypointStyleAsset = this.client.getWaypointStyleAssetManager().get(config.style);
            float f = MathHelper.sqrt((float)((float)waypoint.squaredDistanceTo(entity2)));
            Identifier identifier = waypointStyleAsset.getSpriteForDistance(f);
            int k = config.color.orElseGet(() -> (Integer)waypoint.getSource().map(uuid -> ColorHelper.withBrightness((int)ColorHelper.withAlpha((int)255, (int)uuid.hashCode()), (float)0.9f), name -> ColorHelper.withBrightness((int)ColorHelper.withAlpha((int)255, (int)name.hashCode()), (float)0.9f)));
            int l = MathHelper.floor((double)(d * 173.0 / 2.0 / 60.0));
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, j + l, i - 2, 9, 9, k);
            TrackedWaypoint.Pitch pitch = waypoint.getPitch(world, (TrackedWaypoint.PitchProvider)this.client.gameRenderer, entityTickProgress);
            if (pitch != TrackedWaypoint.Pitch.NONE) {
                Identifier identifier2;
                int m;
                if (pitch == TrackedWaypoint.Pitch.DOWN) {
                    m = 6;
                    identifier2 = ARROW_DOWN;
                } else {
                    m = -6;
                    identifier2 = ARROW_UP;
                }
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier2, j + l + 1, i + m, 7, 5);
            }
        });
    }
}

