/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.network.message;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.network.message.FilterMask;
import net.minecraft.util.StringIdentifiable;

static final class FilterMask.FilterStatus
extends Enum<FilterMask.FilterStatus>
implements StringIdentifiable {
    public static final /* enum */ FilterMask.FilterStatus PASS_THROUGH = new FilterMask.FilterStatus("pass_through", () -> PASS_THROUGH_CODEC);
    public static final /* enum */ FilterMask.FilterStatus FULLY_FILTERED = new FilterMask.FilterStatus("fully_filtered", () -> FULLY_FILTERED_CODEC);
    public static final /* enum */ FilterMask.FilterStatus PARTIALLY_FILTERED = new FilterMask.FilterStatus("partially_filtered", () -> PARTIALLY_FILTERED_CODEC);
    private final String id;
    private final Supplier<MapCodec<FilterMask>> codecSupplier;
    private static final /* synthetic */ FilterMask.FilterStatus[] field_39950;

    public static FilterMask.FilterStatus[] values() {
        return (FilterMask.FilterStatus[])field_39950.clone();
    }

    public static FilterMask.FilterStatus valueOf(String string) {
        return Enum.valueOf(FilterMask.FilterStatus.class, string);
    }

    private FilterMask.FilterStatus(String id, Supplier<MapCodec<FilterMask>> codecSupplier) {
        this.id = id;
        this.codecSupplier = codecSupplier;
    }

    @Override
    public String asString() {
        return this.id;
    }

    private MapCodec<FilterMask> getCodec() {
        return this.codecSupplier.get();
    }

    private static /* synthetic */ FilterMask.FilterStatus[] method_45094() {
        return new FilterMask.FilterStatus[]{PASS_THROUGH, FULLY_FILTERED, PARTIALLY_FILTERED};
    }

    static {
        field_39950 = FilterMask.FilterStatus.method_45094();
    }
}
