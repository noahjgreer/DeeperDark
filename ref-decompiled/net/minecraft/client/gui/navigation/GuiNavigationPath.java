/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.ParentElement
 *  net.minecraft.client.gui.navigation.GuiNavigationPath
 *  net.minecraft.client.gui.navigation.GuiNavigationPath$IntermediaryNode
 *  net.minecraft.client.gui.navigation.GuiNavigationPath$Leaf
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.navigation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
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
        GuiNavigationPath guiNavigationPath = GuiNavigationPath.of((Element)leaf);
        for (ParentElement parentElement : elements) {
            guiNavigationPath = GuiNavigationPath.of((ParentElement)parentElement, (GuiNavigationPath)guiNavigationPath);
        }
        return guiNavigationPath;
    }

    public Element component();

    public void setFocused(boolean var1);
}

