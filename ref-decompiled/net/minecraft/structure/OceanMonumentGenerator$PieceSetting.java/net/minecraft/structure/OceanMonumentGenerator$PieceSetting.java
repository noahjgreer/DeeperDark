/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.util.math.Direction;

static class OceanMonumentGenerator.PieceSetting {
    final int roomIndex;
    final OceanMonumentGenerator.PieceSetting[] neighbors = new OceanMonumentGenerator.PieceSetting[6];
    final boolean[] neighborPresences = new boolean[6];
    boolean used;
    boolean entrance;
    private int entranceDistance;

    public OceanMonumentGenerator.PieceSetting(int index) {
        this.roomIndex = index;
    }

    public void setNeighbor(Direction orientation, OceanMonumentGenerator.PieceSetting setting) {
        this.neighbors[orientation.getIndex()] = setting;
        setting.neighbors[orientation.getOpposite().getIndex()] = this;
    }

    public void checkNeighborStates() {
        for (int i = 0; i < 6; ++i) {
            this.neighborPresences[i] = this.neighbors[i] != null;
        }
    }

    public boolean hasEntranceConnection(int distance) {
        if (this.entrance) {
            return true;
        }
        this.entranceDistance = distance;
        for (int i = 0; i < 6; ++i) {
            if (this.neighbors[i] == null || !this.neighborPresences[i] || this.neighbors[i].entranceDistance == distance || !this.neighbors[i].hasEntranceConnection(distance)) continue;
            return true;
        }
        return false;
    }

    public boolean isAboveLevelThree() {
        return this.roomIndex >= 75;
    }

    public int countNeighbors() {
        int i = 0;
        for (int j = 0; j < 6; ++j) {
            if (!this.neighborPresences[j]) continue;
            ++i;
        }
        return i;
    }
}
