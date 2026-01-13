/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.function;

import net.minecraft.component.ComponentsAccess;
import net.minecraft.loot.context.LootEntityValueSource;
import net.minecraft.util.context.ContextParameter;

record CopyComponentsLootFunction.ComponentAccessSource<T extends ComponentsAccess>(ContextParameter<? extends T> contextParam) implements LootEntityValueSource.ContextComponentBased<T, ComponentsAccess>
{
    @Override
    public ComponentsAccess get(T componentsAccess) {
        return componentsAccess;
    }
}
