/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.chunk.NormalizedRelativePos
 *  net.minecraft.util.math.ChunkSectionPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package net.minecraft.client.render.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public final class NormalizedRelativePos {
    private int x;
    private int y;
    private int z;

    public static NormalizedRelativePos of(Vec3d cameraPos, long sectionPos) {
        return new NormalizedRelativePos().with(cameraPos, sectionPos);
    }

    public NormalizedRelativePos with(Vec3d cameraPos, long sectionPos) {
        this.x = NormalizedRelativePos.normalize((double)cameraPos.getX(), (int)ChunkSectionPos.unpackX((long)sectionPos));
        this.y = NormalizedRelativePos.normalize((double)cameraPos.getY(), (int)ChunkSectionPos.unpackY((long)sectionPos));
        this.z = NormalizedRelativePos.normalize((double)cameraPos.getZ(), (int)ChunkSectionPos.unpackZ((long)sectionPos));
        return this;
    }

    private static int normalize(double cameraCoord, int sectionCoord) {
        int i = ChunkSectionPos.getSectionCoordFloored((double)cameraCoord) - sectionCoord;
        return MathHelper.clamp((int)i, (int)-1, (int)1);
    }

    public boolean isOnCameraAxis() {
        return this.x == 0 || this.y == 0 || this.z == 0;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof NormalizedRelativePos) {
            NormalizedRelativePos normalizedRelativePos = (NormalizedRelativePos)o;
            return this.x == normalizedRelativePos.x && this.y == normalizedRelativePos.y && this.z == normalizedRelativePos.z;
        }
        return false;
    }
}

