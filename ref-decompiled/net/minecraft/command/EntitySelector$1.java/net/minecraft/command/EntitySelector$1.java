/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import net.minecraft.entity.Entity;
import net.minecraft.util.TypeFilter;

class EntitySelector.1
implements TypeFilter<Entity, Entity> {
    EntitySelector.1() {
    }

    @Override
    public Entity downcast(Entity entity) {
        return entity;
    }

    @Override
    public Class<? extends Entity> getBaseClass() {
        return Entity.class;
    }
}
