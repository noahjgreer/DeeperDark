/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util.collection;

import java.util.AbstractList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.SequencedCollection;
import net.minecraft.util.collection.ArrayListDeque;
import net.minecraft.util.collection.ListDeque;
import org.jspecify.annotations.Nullable;

class ArrayListDeque.ReversedWrapper
extends AbstractList<T>
implements ListDeque<T> {
    private final ArrayListDeque<T> original;

    public ArrayListDeque.ReversedWrapper(ArrayListDeque<T> original) {
        this.original = original;
    }

    @Override
    public ListDeque<T> reversed() {
        return this.original;
    }

    @Override
    public T getFirst() {
        return this.original.getLast();
    }

    @Override
    public T getLast() {
        return this.original.getFirst();
    }

    @Override
    public void addFirst(T object) {
        this.original.addLast(object);
    }

    @Override
    public void addLast(T object) {
        this.original.addFirst(object);
    }

    @Override
    public boolean offerFirst(T value) {
        return this.original.offerLast(value);
    }

    @Override
    public boolean offerLast(T value) {
        return this.original.offerFirst(value);
    }

    @Override
    public @Nullable T pollFirst() {
        return this.original.pollLast();
    }

    @Override
    public @Nullable T pollLast() {
        return this.original.pollFirst();
    }

    @Override
    public @Nullable T peekFirst() {
        return this.original.peekLast();
    }

    @Override
    public @Nullable T peekLast() {
        return this.original.peekFirst();
    }

    @Override
    public T removeFirst() {
        return this.original.removeLast();
    }

    @Override
    public T removeLast() {
        return this.original.removeFirst();
    }

    @Override
    public boolean removeFirstOccurrence(Object value) {
        return this.original.removeLastOccurrence(value);
    }

    @Override
    public boolean removeLastOccurrence(Object value) {
        return this.original.removeFirstOccurrence(value);
    }

    @Override
    public Iterator<T> descendingIterator() {
        return this.original.iterator();
    }

    @Override
    public int size() {
        return this.original.size();
    }

    @Override
    public boolean isEmpty() {
        return this.original.isEmpty();
    }

    @Override
    public boolean contains(Object value) {
        return this.original.contains(value);
    }

    @Override
    public T get(int index) {
        return this.original.get(this.getReversedIndex(index));
    }

    @Override
    public T set(int index, T value) {
        return this.original.set(this.getReversedIndex(index), value);
    }

    @Override
    public void add(int index, T value) {
        this.original.add(this.getReversedIndex(index) + 1, value);
    }

    @Override
    public T remove(int index) {
        return this.original.remove(this.getReversedIndex(index));
    }

    @Override
    public int indexOf(Object value) {
        return this.getReversedIndex(this.original.lastIndexOf(value));
    }

    @Override
    public int lastIndexOf(Object value) {
        return this.getReversedIndex(this.original.indexOf(value));
    }

    @Override
    public List<T> subList(int start, int end) {
        return this.original.subList(this.getReversedIndex(end) + 1, this.getReversedIndex(start) + 1).reversed();
    }

    @Override
    public Iterator<T> iterator() {
        return this.original.descendingIterator();
    }

    @Override
    public void clear() {
        this.original.clear();
    }

    private int getReversedIndex(int index) {
        return index == -1 ? -1 : this.original.size() - 1 - index;
    }

    @Override
    public /* synthetic */ List reversed() {
        return this.reversed();
    }

    @Override
    public /* synthetic */ SequencedCollection reversed() {
        return this.reversed();
    }

    @Override
    public /* synthetic */ Deque reversed() {
        return this.reversed();
    }
}
