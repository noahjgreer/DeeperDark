/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.ingame;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record BookScreen.Contents(List<Text> pages) {
    public int getPageCount() {
        return this.pages.size();
    }

    public Text getPage(int index) {
        if (index >= 0 && index < this.getPageCount()) {
            return this.pages.get(index);
        }
        return ScreenTexts.EMPTY;
    }

    public static @Nullable BookScreen.Contents create(ItemStack stack) {
        boolean bl = MinecraftClient.getInstance().shouldFilterText();
        WrittenBookContentComponent writtenBookContentComponent = stack.get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
        if (writtenBookContentComponent != null) {
            return new BookScreen.Contents(writtenBookContentComponent.getPages(bl));
        }
        WritableBookContentComponent writableBookContentComponent = stack.get(DataComponentTypes.WRITABLE_BOOK_CONTENT);
        if (writableBookContentComponent != null) {
            return new BookScreen.Contents(writableBookContentComponent.stream(bl).map(Text::literal).toList());
        }
        return null;
    }
}
