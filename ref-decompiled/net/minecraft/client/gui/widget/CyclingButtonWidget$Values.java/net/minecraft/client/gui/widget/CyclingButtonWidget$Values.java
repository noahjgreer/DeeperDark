/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static interface CyclingButtonWidget.Values<T> {
    public List<T> getCurrent();

    public List<T> getDefaults();

    public static <T> CyclingButtonWidget.Values<T> of(Collection<T> values) {
        ImmutableList list = ImmutableList.copyOf(values);
        return new CyclingButtonWidget.Values<T>((List)list){
            final /* synthetic */ List field_27979;
            {
                this.field_27979 = list;
            }

            @Override
            public List<T> getCurrent() {
                return this.field_27979;
            }

            @Override
            public List<T> getDefaults() {
                return this.field_27979;
            }
        };
    }

    public static <T> CyclingButtonWidget.Values<T> of(final BooleanSupplier alternativeToggle, List<T> defaults, List<T> alternatives) {
        ImmutableList list = ImmutableList.copyOf(defaults);
        ImmutableList list2 = ImmutableList.copyOf(alternatives);
        return new CyclingButtonWidget.Values<T>((List)list2, (List)list){
            final /* synthetic */ List field_27981;
            final /* synthetic */ List field_27982;
            {
                this.field_27981 = list;
                this.field_27982 = list2;
            }

            @Override
            public List<T> getCurrent() {
                return alternativeToggle.getAsBoolean() ? this.field_27981 : this.field_27982;
            }

            @Override
            public List<T> getDefaults() {
                return this.field_27982;
            }
        };
    }
}
