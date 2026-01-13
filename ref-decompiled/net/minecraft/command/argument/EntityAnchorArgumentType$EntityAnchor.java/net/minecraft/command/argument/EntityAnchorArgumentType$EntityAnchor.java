/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.command.argument;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import org.jspecify.annotations.Nullable;

public static final class EntityAnchorArgumentType.EntityAnchor
extends Enum<EntityAnchorArgumentType.EntityAnchor> {
    public static final /* enum */ EntityAnchorArgumentType.EntityAnchor FEET = new EntityAnchorArgumentType.EntityAnchor("feet", (pos, entity) -> pos);
    public static final /* enum */ EntityAnchorArgumentType.EntityAnchor EYES = new EntityAnchorArgumentType.EntityAnchor("eyes", (pos, entity) -> new Vec3d(pos.x, pos.y + (double)entity.getStandingEyeHeight(), pos.z));
    static final Map<String, EntityAnchorArgumentType.EntityAnchor> ANCHORS;
    private final String id;
    private final BiFunction<Vec3d, Entity, Vec3d> offset;
    private static final /* synthetic */ EntityAnchorArgumentType.EntityAnchor[] field_9850;

    public static EntityAnchorArgumentType.EntityAnchor[] values() {
        return (EntityAnchorArgumentType.EntityAnchor[])field_9850.clone();
    }

    public static EntityAnchorArgumentType.EntityAnchor valueOf(String string) {
        return Enum.valueOf(EntityAnchorArgumentType.EntityAnchor.class, string);
    }

    private EntityAnchorArgumentType.EntityAnchor(String id, BiFunction<Vec3d, Entity, Vec3d> offset) {
        this.id = id;
        this.offset = offset;
    }

    public static @Nullable EntityAnchorArgumentType.EntityAnchor fromId(String id) {
        return ANCHORS.get(id);
    }

    public Vec3d positionAt(Entity entity) {
        return this.offset.apply(entity.getEntityPos(), entity);
    }

    public Vec3d positionAt(ServerCommandSource source) {
        Entity entity = source.getEntity();
        if (entity == null) {
            return source.getPosition();
        }
        return this.offset.apply(source.getPosition(), entity);
    }

    private static /* synthetic */ EntityAnchorArgumentType.EntityAnchor[] method_36814() {
        return new EntityAnchorArgumentType.EntityAnchor[]{FEET, EYES};
    }

    static {
        field_9850 = EntityAnchorArgumentType.EntityAnchor.method_36814();
        ANCHORS = Util.make(Maps.newHashMap(), map -> {
            for (EntityAnchorArgumentType.EntityAnchor entityAnchor : EntityAnchorArgumentType.EntityAnchor.values()) {
                map.put(entityAnchor.id, entityAnchor);
            }
        });
    }
}
