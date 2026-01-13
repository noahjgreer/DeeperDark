/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import java.util.List;
import java.util.function.Function;
import net.minecraft.resource.ResourcePackPosition;

public static final class ResourcePackProfile.InsertionPosition
extends Enum<ResourcePackProfile.InsertionPosition> {
    public static final /* enum */ ResourcePackProfile.InsertionPosition TOP = new ResourcePackProfile.InsertionPosition();
    public static final /* enum */ ResourcePackProfile.InsertionPosition BOTTOM = new ResourcePackProfile.InsertionPosition();
    private static final /* synthetic */ ResourcePackProfile.InsertionPosition[] field_14282;

    public static ResourcePackProfile.InsertionPosition[] values() {
        return (ResourcePackProfile.InsertionPosition[])field_14282.clone();
    }

    public static ResourcePackProfile.InsertionPosition valueOf(String string) {
        return Enum.valueOf(ResourcePackProfile.InsertionPosition.class, string);
    }

    public <T> int insert(List<T> items, T item, Function<T, ResourcePackPosition> profileGetter, boolean listInverted) {
        ResourcePackPosition resourcePackPosition;
        int i;
        ResourcePackProfile.InsertionPosition insertionPosition;
        ResourcePackProfile.InsertionPosition insertionPosition2 = insertionPosition = listInverted ? this.inverse() : this;
        if (insertionPosition == BOTTOM) {
            ResourcePackPosition resourcePackPosition2;
            int i2;
            for (i2 = 0; i2 < items.size() && (resourcePackPosition2 = profileGetter.apply(items.get(i2))).fixedPosition() && resourcePackPosition2.defaultPosition() == this; ++i2) {
            }
            items.add(i2, item);
            return i2;
        }
        for (i = items.size() - 1; i >= 0 && (resourcePackPosition = profileGetter.apply(items.get(i))).fixedPosition() && resourcePackPosition.defaultPosition() == this; --i) {
        }
        items.add(i + 1, item);
        return i + 1;
    }

    public ResourcePackProfile.InsertionPosition inverse() {
        return this == TOP ? BOTTOM : TOP;
    }

    private static /* synthetic */ ResourcePackProfile.InsertionPosition[] method_36583() {
        return new ResourcePackProfile.InsertionPosition[]{TOP, BOTTOM};
    }

    static {
        field_14282 = ResourcePackProfile.InsertionPosition.method_36583();
    }
}
