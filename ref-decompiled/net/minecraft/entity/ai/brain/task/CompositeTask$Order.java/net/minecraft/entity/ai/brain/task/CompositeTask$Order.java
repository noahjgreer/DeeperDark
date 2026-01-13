/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;

import java.util.function.Consumer;
import net.minecraft.util.collection.WeightedList;

public static final class CompositeTask.Order
extends Enum<CompositeTask.Order> {
    public static final /* enum */ CompositeTask.Order ORDERED = new CompositeTask.Order(list -> {});
    public static final /* enum */ CompositeTask.Order SHUFFLED = new CompositeTask.Order(WeightedList::shuffle);
    private final Consumer<WeightedList<?>> listModifier;
    private static final /* synthetic */ CompositeTask.Order[] field_18351;

    public static CompositeTask.Order[] values() {
        return (CompositeTask.Order[])field_18351.clone();
    }

    public static CompositeTask.Order valueOf(String string) {
        return Enum.valueOf(CompositeTask.Order.class, string);
    }

    private CompositeTask.Order(Consumer<WeightedList<?>> listModifier) {
        this.listModifier = listModifier;
    }

    public void apply(WeightedList<?> list) {
        this.listModifier.accept(list);
    }

    private static /* synthetic */ CompositeTask.Order[] method_36617() {
        return new CompositeTask.Order[]{ORDERED, SHUFFLED};
    }

    static {
        field_18351 = CompositeTask.Order.method_36617();
    }
}
