/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.navigation;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface GuiNavigationPath {
    public static GuiNavigationPath of(Element leaf) {
        return new Leaf(leaf);
    }

    public static @Nullable GuiNavigationPath of(ParentElement element, @Nullable GuiNavigationPath childPath) {
        if (childPath == null) {
            return null;
        }
        return new IntermediaryNode(element, childPath);
    }

    public static GuiNavigationPath of(Element leaf, ParentElement ... elements) {
        GuiNavigationPath guiNavigationPath = GuiNavigationPath.of(leaf);
        for (ParentElement parentElement : elements) {
            guiNavigationPath = GuiNavigationPath.of(parentElement, guiNavigationPath);
        }
        return guiNavigationPath;
    }

    public Element component();

    public void setFocused(boolean var1);

    @Environment(value=EnvType.CLIENT)
    public record Leaf(Element component) implements GuiNavigationPath
    {
        @Override
        public void setFocused(boolean focused) {
            this.component.setFocused(focused);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class IntermediaryNode
    extends Record
    implements GuiNavigationPath {
        private final ParentElement component;
        private final GuiNavigationPath childPath;

        public IntermediaryNode(ParentElement component, GuiNavigationPath childPath) {
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
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{IntermediaryNode.class, "component;childPath", "component", "childPath"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{IntermediaryNode.class, "component;childPath", "component", "childPath"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{IntermediaryNode.class, "component;childPath", "component", "childPath"}, this, object);
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
}
