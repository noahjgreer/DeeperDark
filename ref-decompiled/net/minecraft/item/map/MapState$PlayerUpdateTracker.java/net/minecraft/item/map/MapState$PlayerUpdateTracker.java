/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.item.map;

import java.util.Collection;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.map.MapDecoration;
import net.minecraft.item.map.MapState;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import org.jspecify.annotations.Nullable;

public class MapState.PlayerUpdateTracker {
    public final PlayerEntity player;
    private boolean dirty = true;
    private int startX;
    private int startZ;
    private int endX = 127;
    private int endZ = 127;
    private boolean decorationsDirty = true;
    private int emptyPacketsRequested;
    public int field_131;

    MapState.PlayerUpdateTracker(PlayerEntity player) {
        this.player = player;
    }

    private MapState.UpdateData getMapUpdateData() {
        int i = this.startX;
        int j = this.startZ;
        int k = this.endX + 1 - this.startX;
        int l = this.endZ + 1 - this.startZ;
        byte[] bs = new byte[k * l];
        for (int m = 0; m < k; ++m) {
            for (int n = 0; n < l; ++n) {
                bs[m + n * k] = MapState.this.colors[i + m + (j + n) * 128];
            }
        }
        return new MapState.UpdateData(i, j, k, l, bs);
    }

    @Nullable Packet<?> getPacket(MapIdComponent mapId) {
        Collection<MapDecoration> collection;
        MapState.UpdateData updateData;
        if (this.dirty) {
            this.dirty = false;
            updateData = this.getMapUpdateData();
        } else {
            updateData = null;
        }
        if (this.decorationsDirty && this.emptyPacketsRequested++ % 5 == 0) {
            this.decorationsDirty = false;
            collection = MapState.this.decorations.values();
        } else {
            collection = null;
        }
        if (collection != null || updateData != null) {
            return new MapUpdateS2CPacket(mapId, MapState.this.scale, MapState.this.locked, collection, updateData);
        }
        return null;
    }

    void markDirty(int startX, int startZ) {
        if (this.dirty) {
            this.startX = Math.min(this.startX, startX);
            this.startZ = Math.min(this.startZ, startZ);
            this.endX = Math.max(this.endX, startX);
            this.endZ = Math.max(this.endZ, startZ);
        } else {
            this.dirty = true;
            this.startX = startX;
            this.startZ = startZ;
            this.endX = startX;
            this.endZ = startZ;
        }
    }

    private void markDecorationsDirty() {
        this.decorationsDirty = true;
    }
}
