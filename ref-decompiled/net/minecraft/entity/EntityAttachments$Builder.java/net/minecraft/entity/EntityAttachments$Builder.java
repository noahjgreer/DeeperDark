/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.EntityAttachments;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;

public static class EntityAttachments.Builder {
    private final Map<EntityAttachmentType, List<Vec3d>> points = new EnumMap<EntityAttachmentType, List<Vec3d>>(EntityAttachmentType.class);

    EntityAttachments.Builder() {
    }

    public EntityAttachments.Builder add(EntityAttachmentType type, float x, float y, float z) {
        return this.add(type, new Vec3d(x, y, z));
    }

    public EntityAttachments.Builder add(EntityAttachmentType type, Vec3d point) {
        this.points.computeIfAbsent(type, list -> new ArrayList(1)).add(point);
        return this;
    }

    public EntityAttachments build(float width, float height) {
        Map<EntityAttachmentType, List<Vec3d>> map = Util.mapEnum(EntityAttachmentType.class, type -> {
            List<Vec3d> list = this.points.get(type);
            return list == null ? type.createPoint(width, height) : List.copyOf(list);
        });
        return new EntityAttachments(map);
    }
}
