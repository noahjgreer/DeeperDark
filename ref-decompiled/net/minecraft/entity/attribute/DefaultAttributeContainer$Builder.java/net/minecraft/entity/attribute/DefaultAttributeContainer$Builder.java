/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package net.minecraft.entity.attribute;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.registry.entry.RegistryEntry;

public static class DefaultAttributeContainer.Builder {
    private final ImmutableMap.Builder<RegistryEntry<EntityAttribute>, EntityAttributeInstance> instances = ImmutableMap.builder();
    private boolean unmodifiable;

    private EntityAttributeInstance checkedAdd(RegistryEntry<EntityAttribute> attribute) {
        EntityAttributeInstance entityAttributeInstance = new EntityAttributeInstance(attribute, attributex -> {
            if (this.unmodifiable) {
                throw new UnsupportedOperationException("Tried to change value for default attribute instance: " + attribute.getIdAsString());
            }
        });
        this.instances.put(attribute, (Object)entityAttributeInstance);
        return entityAttributeInstance;
    }

    public DefaultAttributeContainer.Builder add(RegistryEntry<EntityAttribute> attribute) {
        this.checkedAdd(attribute);
        return this;
    }

    public DefaultAttributeContainer.Builder add(RegistryEntry<EntityAttribute> attribute, double baseValue) {
        EntityAttributeInstance entityAttributeInstance = this.checkedAdd(attribute);
        entityAttributeInstance.setBaseValue(baseValue);
        return this;
    }

    public DefaultAttributeContainer build() {
        this.unmodifiable = true;
        return new DefaultAttributeContainer((Map<RegistryEntry<EntityAttribute>, EntityAttributeInstance>)this.instances.buildKeepingLast());
    }
}
