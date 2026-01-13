/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

import java.util.List;
import net.minecraft.util.math.Vec3d;

public static interface EntityAttachmentType.Point {
    public static final List<Vec3d> NONE = List.of(Vec3d.ZERO);
    public static final EntityAttachmentType.Point ZERO = (width, height) -> NONE;
    public static final EntityAttachmentType.Point AT_HEIGHT = (width, height) -> List.of(new Vec3d(0.0, height, 0.0));
    public static final EntityAttachmentType.Point WARDEN_CHEST = (width, height) -> List.of(new Vec3d(0.0, (double)height / 2.0, 0.0));

    public List<Vec3d> create(float var1, float var2);
}
