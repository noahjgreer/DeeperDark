/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.option;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class KeyBinding
implements Comparable<KeyBinding> {
    private static final Map<String, KeyBinding> KEYS_BY_ID = Maps.newHashMap();
    private static final Map<InputUtil.Key, List<KeyBinding>> KEY_TO_BINDINGS = Maps.newHashMap();
    private final String id;
    private final InputUtil.Key defaultKey;
    private final Category category;
    protected InputUtil.Key boundKey;
    private boolean pressed;
    private int timesPressed;
    private final int field_63464;

    public static void onKeyPressed(InputUtil.Key key2) {
        KeyBinding.forAllKeyBinds(key2, key -> ++key.timesPressed);
    }

    public static void setKeyPressed(InputUtil.Key key2, boolean pressed) {
        KeyBinding.forAllKeyBinds(key2, key -> key.setPressed(pressed));
    }

    private static void forAllKeyBinds(InputUtil.Key key, Consumer<KeyBinding> keyConsumer) {
        List<KeyBinding> list = KEY_TO_BINDINGS.get(key);
        if (list != null && !list.isEmpty()) {
            for (KeyBinding keyBinding : list) {
                keyConsumer.accept(keyBinding);
            }
        }
    }

    public static void updatePressedStates() {
        Window window = MinecraftClient.getInstance().getWindow();
        for (KeyBinding keyBinding : KEYS_BY_ID.values()) {
            if (!keyBinding.shouldSetOnGameFocus()) continue;
            keyBinding.setPressed(InputUtil.isKeyPressed(window, keyBinding.boundKey.getCode()));
        }
    }

    public static void unpressAll() {
        for (KeyBinding keyBinding : KEYS_BY_ID.values()) {
            keyBinding.reset();
        }
    }

    public static void restoreToggleStates() {
        for (KeyBinding keyBinding : KEYS_BY_ID.values()) {
            StickyKeyBinding stickyKeyBinding;
            if (!(keyBinding instanceof StickyKeyBinding) || !(stickyKeyBinding = (StickyKeyBinding)keyBinding).shouldRestoreOnScreenClose()) continue;
            stickyKeyBinding.setPressed(true);
        }
    }

    public static void untoggleStickyKeys() {
        for (KeyBinding keyBinding : KEYS_BY_ID.values()) {
            if (!(keyBinding instanceof StickyKeyBinding)) continue;
            StickyKeyBinding stickyKeyBinding = (StickyKeyBinding)keyBinding;
            stickyKeyBinding.untoggle();
        }
    }

    public static void updateKeysByCode() {
        KEY_TO_BINDINGS.clear();
        for (KeyBinding keyBinding : KEYS_BY_ID.values()) {
            keyBinding.registerBinding(keyBinding.boundKey);
        }
    }

    public KeyBinding(String id, int code, Category category) {
        this(id, InputUtil.Type.KEYSYM, code, category);
    }

    public KeyBinding(String string, InputUtil.Type type, int i, Category category) {
        this(string, type, i, category, 0);
    }

    public KeyBinding(String id, InputUtil.Type type, int code, Category category, int i) {
        this.id = id;
        this.defaultKey = this.boundKey = type.createFromCode(code);
        this.category = category;
        this.field_63464 = i;
        KEYS_BY_ID.put(id, this);
        this.registerBinding(this.boundKey);
    }

    public boolean isPressed() {
        return this.pressed;
    }

    public Category getCategory() {
        return this.category;
    }

    public boolean wasPressed() {
        if (this.timesPressed == 0) {
            return false;
        }
        --this.timesPressed;
        return true;
    }

    protected void reset() {
        this.timesPressed = 0;
        this.setPressed(false);
    }

    protected boolean shouldSetOnGameFocus() {
        return this.boundKey.getCategory() == InputUtil.Type.KEYSYM && this.boundKey.getCode() != InputUtil.UNKNOWN_KEY.getCode();
    }

    public String getId() {
        return this.id;
    }

    public InputUtil.Key getDefaultKey() {
        return this.defaultKey;
    }

    public void setBoundKey(InputUtil.Key boundKey) {
        this.boundKey = boundKey;
    }

    @Override
    public int compareTo(KeyBinding keyBinding) {
        if (this.category == keyBinding.category) {
            if (this.field_63464 == keyBinding.field_63464) {
                return I18n.translate(this.id, new Object[0]).compareTo(I18n.translate(keyBinding.id, new Object[0]));
            }
            return Integer.compare(this.field_63464, keyBinding.field_63464);
        }
        return Integer.compare(Category.CATEGORIES.indexOf(this.category), Category.CATEGORIES.indexOf(keyBinding.category));
    }

    public static Supplier<Text> getLocalizedName(String id) {
        KeyBinding keyBinding = KEYS_BY_ID.get(id);
        if (keyBinding == null) {
            return () -> Text.translatable(id);
        }
        return keyBinding::getBoundKeyLocalizedText;
    }

    public boolean equals(KeyBinding other) {
        return this.boundKey.equals(other.boundKey);
    }

    public boolean isUnbound() {
        return this.boundKey.equals(InputUtil.UNKNOWN_KEY);
    }

    public boolean matchesKey(KeyInput key) {
        if (key.key() == InputUtil.UNKNOWN_KEY.getCode()) {
            return this.boundKey.getCategory() == InputUtil.Type.SCANCODE && this.boundKey.getCode() == key.scancode();
        }
        return this.boundKey.getCategory() == InputUtil.Type.KEYSYM && this.boundKey.getCode() == key.key();
    }

    public boolean matchesMouse(Click click) {
        return this.boundKey.getCategory() == InputUtil.Type.MOUSE && this.boundKey.getCode() == click.button();
    }

    public Text getBoundKeyLocalizedText() {
        return this.boundKey.getLocalizedText();
    }

    public boolean isDefault() {
        return this.boundKey.equals(this.defaultKey);
    }

    public String getBoundKeyTranslationKey() {
        return this.boundKey.getTranslationKey();
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    private void registerBinding(InputUtil.Key key) {
        KEY_TO_BINDINGS.computeIfAbsent(key, keyx -> new ArrayList()).add(this);
    }

    public static @Nullable KeyBinding byId(String id) {
        return KEYS_BY_ID.get(id);
    }

    @Override
    public /* synthetic */ int compareTo(Object other) {
        return this.compareTo((KeyBinding)other);
    }

    @Environment(value=EnvType.CLIENT)
    public record Category(Identifier id) {
        static final List<Category> CATEGORIES = new ArrayList<Category>();
        public static final Category MOVEMENT = Category.create("movement");
        public static final Category MISC = Category.create("misc");
        public static final Category MULTIPLAYER = Category.create("multiplayer");
        public static final Category GAMEPLAY = Category.create("gameplay");
        public static final Category INVENTORY = Category.create("inventory");
        public static final Category CREATIVE = Category.create("creative");
        public static final Category SPECTATOR = Category.create("spectator");
        public static final Category DEBUG = Category.create("debug");

        private static Category create(String name) {
            return Category.create(Identifier.ofVanilla(name));
        }

        public static Category create(Identifier id) {
            Category category = new Category(id);
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
}
