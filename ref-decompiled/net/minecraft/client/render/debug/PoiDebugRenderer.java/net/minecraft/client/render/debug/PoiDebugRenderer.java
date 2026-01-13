/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import java.util.ArrayList;
import java.util.List;
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

    @Override
    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        BlockPos blockPos = BlockPos.ofFloored(cameraX, cameraY, cameraZ);
        store.forEachBlockData(DebugSubscriptionTypes.POIS, (pos, data) -> {
            if (blockPos.isWithinDistance((Vec3i)pos, 30.0)) {
                PoiDebugRenderer.accentuatePoi(pos);
                this.drawPoiInfo((PoiDebugData)data, store);
            }
        });
        this.brainDebugRenderer.getGhostPointsOfInterest(store).forEach((pos, ghostPois) -> {
            if (store.getBlockData(DebugSubscriptionTypes.POIS, (BlockPos)pos) != null) {
                return;
            }
            if (blockPos.isWithinDistance((Vec3i)pos, 30.0)) {
                this.drawGhostPoi((BlockPos)pos, (List<String>)ghostPois);
            }
        });
    }

    private static void accentuatePoi(BlockPos pos) {
        float f = 0.05f;
        GizmoDrawing.box(pos, 0.05f, DrawStyle.filled(ColorHelper.fromFloats(0.3f, 0.2f, 0.2f, 1.0f)));
    }

    private void drawGhostPoi(BlockPos pos, List<String> ghostPois) {
        float f = 0.05f;
        GizmoDrawing.box(pos, 0.05f, DrawStyle.filled(ColorHelper.fromFloats(0.3f, 0.2f, 0.2f, 1.0f)));
        GizmoDrawing.blockLabel(ghostPois.toString(), pos, 0, -256, 0.32f);
        GizmoDrawing.blockLabel("Ghost POI", pos, 1, -65536, 0.32f);
    }

    private void drawPoiInfo(PoiDebugData data, DebugDataStore store) {
        int i = 0;
        if (SharedConstants.BRAIN) {
            List<String> list = this.getTicketHolders(data, false, store);
            if (list.size() < 4) {
                PoiDebugRenderer.drawTextOverPoi("Owners: " + String.valueOf(list), data, i, -256);
            } else {
                PoiDebugRenderer.drawTextOverPoi(list.size() + " ticket holders", data, i, -256);
            }
            ++i;
            List<String> list2 = this.getTicketHolders(data, true, store);
            if (list2.size() < 4) {
                PoiDebugRenderer.drawTextOverPoi("Candidates: " + String.valueOf(list2), data, i, -23296);
            } else {
                PoiDebugRenderer.drawTextOverPoi(list2.size() + " potential owners", data, i, -23296);
            }
            ++i;
        }
        PoiDebugRenderer.drawTextOverPoi("Free tickets: " + data.freeTicketCount(), data, i, -256);
        PoiDebugRenderer.drawTextOverPoi(data.poiType().getIdAsString(), data, ++i, -1);
    }

    private static void drawTextOverPoi(String string, PoiDebugData data, int yOffset, int color) {
        GizmoDrawing.blockLabel(string, data.pos(), yOffset, color, 0.32f);
    }

    private List<String> getTicketHolders(PoiDebugData poiData, boolean potential, DebugDataStore store) {
        ArrayList<String> list = new ArrayList<String>();
        store.forEachEntityData(DebugSubscriptionTypes.BRAINS, (entity, grainData) -> {
            boolean bl2;
            boolean bl3 = bl2 = potential ? grainData.potentialPoiContains(poiData.pos()) : grainData.poiContains(poiData.pos());
            if (bl2) {
                list.add(NameGenerator.name(entity.getUuid()));
            }
        });
        return list;
    }
}
