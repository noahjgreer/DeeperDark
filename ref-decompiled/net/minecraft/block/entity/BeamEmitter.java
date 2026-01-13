/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.entity.BeamEmitter
 *  net.minecraft.block.entity.BeamEmitter$BeamSegment
 */
package net.minecraft.block.entity;

import java.util.List;
import net.minecraft.block.entity.BeamEmitter;

public interface BeamEmitter {
    public List<BeamSegment> getBeamSegments();
}

