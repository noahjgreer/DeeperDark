/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.world;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
record WorldScreenOptionGrid.Option(CyclingButtonWidget<Boolean> button, BooleanSupplier getter, @Nullable BooleanSupplier toggleable) {
    public void refresh() {
        this.button.setValue(this.getter.getAsBoolean());
        if (this.toggleable != null) {
            this.button.active = this.toggleable.getAsBoolean();
        }
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{WorldScreenOptionGrid.Option.class, "button;stateSupplier;isActiveCondition", "button", "getter", "toggleable"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{WorldScreenOptionGrid.Option.class, "button;stateSupplier;isActiveCondition", "button", "getter", "toggleable"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{WorldScreenOptionGrid.Option.class, "button;stateSupplier;isActiveCondition", "button", "getter", "toggleable"}, this, object);
    }
}
