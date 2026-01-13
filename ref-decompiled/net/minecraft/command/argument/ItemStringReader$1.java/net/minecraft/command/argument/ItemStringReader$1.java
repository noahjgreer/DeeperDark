/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package net.minecraft.command.argument;

import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;
import org.apache.commons.lang3.mutable.MutableObject;

class ItemStringReader.1
implements ItemStringReader.Callbacks {
    final /* synthetic */ MutableObject field_48956;
    final /* synthetic */ ComponentChanges.Builder field_49571;

    ItemStringReader.1() {
        this.field_48956 = mutableObject;
        this.field_49571 = builder;
    }

    @Override
    public void onItem(RegistryEntry<Item> item) {
        this.field_48956.setValue(item);
    }

    @Override
    public <T> void onComponentAdded(ComponentType<T> type, T value) {
        this.field_49571.add(type, value);
    }

    @Override
    public <T> void onComponentRemoved(ComponentType<T> type) {
        this.field_49571.remove(type);
    }
}
