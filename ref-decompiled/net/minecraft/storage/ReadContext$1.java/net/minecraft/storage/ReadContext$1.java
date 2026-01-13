/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.storage;

import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;
import net.minecraft.storage.ReadContext;
import net.minecraft.storage.ReadView;

class ReadContext.1
implements ReadView.ListReadView {
    ReadContext.1(ReadContext readContext) {
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public Stream<ReadView> stream() {
        return Stream.empty();
    }

    @Override
    public Iterator<ReadView> iterator() {
        return Collections.emptyIterator();
    }
}
