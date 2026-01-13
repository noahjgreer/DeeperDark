/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState$LeashData
 *  net.minecraft.client.render.entity.state.EntityRenderState$ShadowPiece
 *  net.minecraft.entity.EntityType
 *  net.minecraft.text.Text
 *  net.minecraft.util.crash.CrashReportSection
 *  net.minecraft.util.math.Vec3d
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.entity.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.EntityType;
import net.minecraft.text.Text;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Vec3d;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class EntityRenderState
implements FabricRenderState {
    public static final int NO_OUTLINE = 0;
    public EntityType<?> entityType;
    public double x;
    public double y;
    public double z;
    public float age;
    public float width;
    public float height;
    public float standingEyeHeight;
    public double squaredDistanceToCamera;
    public boolean invisible;
    public boolean sneaking;
    public boolean onFire;
    public int light = 0xF000F0;
    public int outlineColor = 0;
    public @Nullable Vec3d positionOffset;
    public @Nullable Text displayName;
    public @Nullable Vec3d nameLabelPos;
    public @Nullable List<// Could not load outer class - annotation placement on inner may be incorrect
    LeashData> leashDatas;
    public float shadowRadius;
    public final List<ShadowPiece> shadowPieces = new ArrayList();

    public boolean hasOutline() {
        return this.outlineColor != 0;
    }

    public void addCrashReportDetails(CrashReportSection crashReportSection) {
        crashReportSection.add("EntityRenderState", (Object)this.getClass().getCanonicalName());
        crashReportSection.add("Entity's Exact location", (Object)String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.x, this.y, this.z));
    }
}

