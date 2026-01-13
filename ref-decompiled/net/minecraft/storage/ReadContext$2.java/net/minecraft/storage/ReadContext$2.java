/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.storage;

import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;
import net.minecraft.storage.ReadContext;
import net.minecraft.storage.ReadView;

class ReadContext.2
implements ReadView.TypedListReadView<Object> {
    ReadContext.2(ReadContext readContext) {
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public Stream<Object> stream() {
        return Stream.empty();
    }

    @Override
    public Iterator<Object> iterator() {
        return Collections.emptyIterator();
    }
}
