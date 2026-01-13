/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.command;

import net.minecraft.entity.Entity;
import net.minecraft.server.command.LookTarget;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec3d;

public record LookTarget.LookAtPosition(Vec3d position) implements LookTarget
{
    @Override
    public void look(ServerCommandSource source, Entity entity) {
        entity.lookAt(source.getEntityAnchor(), this.position);
    }
}
