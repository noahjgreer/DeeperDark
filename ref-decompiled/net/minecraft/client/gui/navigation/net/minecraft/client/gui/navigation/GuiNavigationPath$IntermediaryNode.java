/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.navigation;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.navigation.GuiNavigationPath;

@Environment(value=EnvType.CLIENT)
public static final class GuiNavigationPath.IntermediaryNode
extends Record
implements GuiNavigationPath {
    private final ParentElement component;
    private final GuiNavigationPath childPath;

    public GuiNavigationPath.IntermediaryNode(ParentElement component, GuiNavigationPath childPath) {
        this.component = component;
        this.childPath = childPath;
    }

    @Override
    public void setFocused(boolean focused) {
        if (!focused) {
            this.component.setFocused(null);
        } else {
            this.component.setFocused(this.childPath.component());
        }
        this.childPath.setFocused(focused);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{GuiNavigationPath.IntermediaryNode.class, "component;childPath", "component", "childPath"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GuiNavigationPath.IntermediaryNode.class, "component;childPath", "component", "childPath"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GuiNavigationPath.IntermediaryNode.class, "component;childPath", "component", "childPath"}, this, object);
    }

    @Override
    public ParentElement component() {
        return this.component;
    }

    public GuiNavigationPath childPath() {
        return this.childPath;
    }

    @Override
    public /* synthetic */ Element component() {
        return this.component();
    }
}
