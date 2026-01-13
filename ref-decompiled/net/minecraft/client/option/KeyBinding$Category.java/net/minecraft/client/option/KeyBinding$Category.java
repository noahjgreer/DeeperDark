/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record KeyBinding.Category(Identifier id) {
    static final List<KeyBinding.Category> CATEGORIES = new ArrayList<KeyBinding.Category>();
    public static final KeyBinding.Category MOVEMENT = KeyBinding.Category.create("movement");
    public static final KeyBinding.Category MISC = KeyBinding.Category.create("misc");
    public static final KeyBinding.Category MULTIPLAYER = KeyBinding.Category.create("multiplayer");
    public static final KeyBinding.Category GAMEPLAY = KeyBinding.Category.create("gameplay");
    public static final KeyBinding.Category INVENTORY = KeyBinding.Category.create("inventory");
    public static final KeyBinding.Category CREATIVE = KeyBinding.Category.create("creative");
    public static final KeyBinding.Category SPECTATOR = KeyBinding.Category.create("spectator");
    public static final KeyBinding.Category DEBUG = KeyBinding.Category.create("debug");

    private static KeyBinding.Category create(String name) {
        return KeyBinding.Category.create(Identifier.ofVanilla(name));
    }

    public static KeyBinding.Category create(Identifier id) {
        KeyBinding.Category category = new KeyBinding.Category(id);
        if (CATEGORIES.contains(category)) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Category '%s' is already registered.", id));
        }
        CATEGORIES.add(category);
        return category;
    }

    public Text getLabel() {
        return Text.translatable(this.id.toTranslationKey("key.category"));
    }
}
