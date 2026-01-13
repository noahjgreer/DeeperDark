/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import com.google.common.collect.Lists;
import java.util.AbstractList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.EntryListWidget;

@Environment(value=EnvType.CLIENT)
class EntryListWidget.Entries
extends AbstractList<E> {
    private final List<E> entries = Lists.newArrayList();

    EntryListWidget.Entries() {
    }

    @Override
    public E get(int i) {
        return (EntryListWidget.Entry)this.entries.get(i);
    }

    @Override
    public int size() {
        return this.entries.size();
    }

    @Override
    public E set(int i, E entry) {
        EntryListWidget.Entry entry2 = (EntryListWidget.Entry)this.entries.set(i, entry);
        EntryListWidget.this.setEntryParentList(entry);
        return entry2;
    }

    @Override
    public void add(int i, E entry) {
        this.entries.add(i, entry);
        EntryListWidget.this.setEntryParentList(entry);
    }

    @Override
    public E remove(int i) {
        return (EntryListWidget.Entry)this.entries.remove(i);
    }

    @Override
    public /* synthetic */ Object remove(int index) {
        return this.remove(index);
    }

    @Override
    public /* synthetic */ void add(int index, Object entry) {
        this.add(index, (E)((EntryListWidget.Entry)entry));
    }

    @Override
    public /* synthetic */ Object set(int index, Object entry) {
        return this.set(index, (E)((EntryListWidget.Entry)entry));
    }

    @Override
    public /* synthetic */ Object get(int index) {
        return this.get(index);
    }
}
