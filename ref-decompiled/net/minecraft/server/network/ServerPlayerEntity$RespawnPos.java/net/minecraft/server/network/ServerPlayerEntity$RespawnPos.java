/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.network;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

record ServerPlayerEntity.RespawnPos(Vec3d pos, float yaw, float pitch) {
    public static ServerPlayerEntity.RespawnPos fromCurrentPos(Vec3d respawnPos, BlockPos currentPos, float f) {
        return new ServerPlayerEntity.RespawnPos(respawnPos, ServerPlayerEntity.RespawnPos.getYaw(respawnPos, currentPos), f);
    }

    private static float getYaw(Vec3d respawnPos, BlockPos currentPos) {
        Vec3d vec3d = Vec3d.ofBottomCenter(currentPos).subtract(respawnPos).normalize();
        return (float)MathHelper.wrapDegrees(MathHelper.atan2(vec3d.z, vec3d.x) * 57.2957763671875 - 90.0);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ServerPlayerEntity.RespawnPos.class, "position;yaw;pitch", "pos", "yaw", "pitch"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ServerPlayerEntity.RespawnPos.class, "position;yaw;pitch", "pos", "yaw", "pitch"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ServerPlayerEntity.RespawnPos.class, "position;yaw;pitch", "pos", "yaw", "pitch"}, this, object);
    }
}
