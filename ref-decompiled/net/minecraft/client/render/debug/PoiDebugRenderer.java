/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.render.DrawStyle
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.debug.BrainDebugRenderer
 *  net.minecraft.client.render.debug.DebugRenderer$Renderer
 *  net.minecraft.client.render.debug.PoiDebugRenderer
 *  net.minecraft.util.NameGenerator
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.debug.DebugDataStore
 *  net.minecraft.world.debug.DebugSubscriptionTypes
 *  net.minecraft.world.debug.data.PoiDebugData
 *  net.minecraft.world.debug.gizmo.GizmoDrawing
 */
package net.minecraft.client.render.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.BrainDebugRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.util.NameGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.DebugSubscriptionTypes;
import net.minecraft.world.debug.data.PoiDebugData;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class PoiDebugRenderer
implements DebugRenderer.Renderer {
    private static final int field_62976 = 30;
    private static final float field_62977 = 0.32f;
    private static final int ORANGE_COLOR = -23296;
    private final BrainDebugRenderer brainDebugRenderer;

    public PoiDebugRenderer(BrainDebugRenderer brainDebugRenderer) {
        this.brainDebugRenderer = brainDebugRenderer;
    }

    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        BlockPos blockPos = BlockPos.ofFloored((double)cameraX, (double)cameraY, (double)cameraZ);
        store.forEachBlockData(DebugSubscriptionTypes.POIS, (pos, data) -> {
            if (blockPos.isWithinDistance((Vec3i)pos, 30.0)) {
                PoiDebugRenderer.accentuatePoi((BlockPos)pos);
                this.drawPoiInfo(data, store);
            }
        });
        this.brainDebugRenderer.getGhostPointsOfInterest(store).forEach((pos, ghostPois) -> {
            if (store.getBlockData(DebugSubscriptionTypes.POIS, pos) != null) {
                return;
            }
            if (blockPos.isWithinDistance((Vec3i)pos, 30.0)) {
                this.drawGhostPoi(pos, ghostPois);
            }
        });
    }

    private static void accentuatePoi(BlockPos pos) {
        float f = 0.05f;
        GizmoDrawing.box((BlockPos)pos, (float)0.05f, (DrawStyle)DrawStyle.filled((int)ColorHelper.fromFloats((float)0.3f, (float)0.2f, (float)0.2f, (float)1.0f)));
    }

    private void drawGhostPoi(BlockPos pos, List<String> ghostPois) {
        float f = 0.05f;
        GizmoDrawing.box((BlockPos)pos, (float)0.05f, (DrawStyle)DrawStyle.filled((int)ColorHelper.fromFloats((float)0.3f, (float)0.2f, (float)0.2f, (float)1.0f)));
        GizmoDrawing.blockLabel((String)ghostPois.toString(), (BlockPos)pos, (int)0, (int)-256, (float)0.32f);
        GizmoDrawing.blockLabel((String)"Ghost POI", (BlockPos)pos, (int)1, (int)-65536, (float)0.32f);
    }

    private void drawPoiInfo(PoiDebugData data, DebugDataStore store) {
        int i = 0;
        if (SharedConstants.BRAIN) {
            List list = this.getTicketHolders(data, false, store);
            if (list.size() < 4) {
                PoiDebugRenderer.drawTextOverPoi((String)("Owners: " + String.valueOf(list)), (PoiDebugData)data, (int)i, (int)-256);
            } else {
                PoiDebugRenderer.drawTextOverPoi((String)(list.size() + " ticket holders"), (PoiDebugData)data, (int)i, (int)-256);
            }
            ++i;
            List list2 = this.getTicketHolders(data, true, store);
            if (list2.size() < 4) {
                PoiDebugRenderer.drawTextOverPoi((String)("Candidates: " + String.valueOf(list2)), (PoiDebugData)data, (int)i, (int)-23296);
            } else {
                PoiDebugRenderer.drawTextOverPoi((String)(list2.size() + " potential owners"), (PoiDebugData)data, (int)i, (int)-23296);
            }
            ++i;
        }
        PoiDebugRenderer.drawTextOverPoi((String)("Free tickets: " + data.freeTicketCount()), (PoiDebugData)data, (int)i, (int)-256);
        PoiDebugRenderer.drawTextOverPoi((String)data.poiType().getIdAsString(), (PoiDebugData)data, (int)(++i), (int)-1);
    }

    private static void drawTextOverPoi(String string, PoiDebugData data, int yOffset, int color) {
        GizmoDrawing.blockLabel((String)string, (BlockPos)data.pos(), (int)yOffset, (int)color, (float)0.32f);
    }

    private List<String> getTicketHolders(PoiDebugData poiData, boolean potential, DebugDataStore store) {
        ArrayList<String> list = new ArrayList<String>();
        store.forEachEntityData(DebugSubscriptionTypes.BRAINS, (entity, grainData) -> {
            boolean bl2;
            boolean bl3 = bl2 = potential ? grainData.potentialPoiContains(poiData.pos()) : grainData.poiContains(poiData.pos());
            if (bl2) {
                list.add(NameGenerator.name((UUID)entity.getUuid()));
            }
        });
        return list;
    }
}

