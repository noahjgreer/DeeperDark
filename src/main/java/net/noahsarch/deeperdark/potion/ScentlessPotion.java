package net.noahsarch.deeperdark.potion;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;

public class ScentlessPotion {

    // Helper method to check if a potion is our custom scentless potion
    public static boolean isScentlessPotion(ItemStack stack) {
        if (!(stack.getItem() instanceof net.minecraft.world.item.PotionItem)) {
            return false;
        }

        // Check if it has our custom name
        Component customName = stack.get(DataComponents.CUSTOM_NAME);
        if (customName != null && customName.getContents() instanceof TranslatableContents translatable) {
            String key = translatable.getKey();
            return key.equals("potion.deeperdark.scentless.name") ||
                   key.equals("potion.deeperdark.long_scentless.name");
        }

        return false;
    }
}
