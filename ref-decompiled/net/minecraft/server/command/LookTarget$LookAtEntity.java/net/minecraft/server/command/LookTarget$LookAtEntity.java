/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.command;

import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.LookTarget;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public record LookTarget.LookAtEntity(Entity entity, EntityAnchorArgumentType.EntityAnchor anchor) implements LookTarget
{
    @Override
    public void look(ServerCommandSource source, Entity entity) {
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
            serverPlayerEntity.lookAtEntity(source.getEntityAnchor(), this.entity, this.anchor);
        } else {
            entity.lookAt(source.getEntityAnchor(), this.anchor.positionAt(this.entity));
        }
    }
}
