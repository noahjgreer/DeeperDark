package net.minecraft.client.option;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class KeyBinding implements Comparable {
   private static final Map KEYS_BY_ID = Maps.newHashMap();
   private static final Map KEY_TO_BINDINGS = Maps.newHashMap();
   private static final Set KEY_CATEGORIES = Sets.newHashSet();
   public static final String MOVEMENT_CATEGORY = "key.categories.movement";
   public static final String MISC_CATEGORY = "key.categories.misc";
   public static final String MULTIPLAYER_CATEGORY = "key.categories.multiplayer";
   public static final String GAMEPLAY_CATEGORY = "key.categories.gameplay";
   public static final String INVENTORY_CATEGORY = "key.categories.inventory";
   public static final String UI_CATEGORY = "key.categories.ui";
   public static final String CREATIVE_CATEGORY = "key.categories.creative";
   private static final Map CATEGORY_ORDER_MAP = (Map)Util.make(Maps.newHashMap(), (map) -> {
      map.put("key.categories.movement", 1);
      map.put("key.categories.gameplay", 2);
      map.put("key.categories.inventory", 3);
      map.put("key.categories.creative", 4);
      map.put("key.categories.multiplayer", 5);
      map.put("key.categories.ui", 6);
      map.put("key.categories.misc", 7);
   });
   private final String translationKey;
   private final InputUtil.Key defaultKey;
   private final String category;
   private InputUtil.Key boundKey;
   private boolean pressed;
   private int timesPressed;

   public static void onKeyPressed(InputUtil.Key key) {
      KeyBinding keyBinding = (KeyBinding)KEY_TO_BINDINGS.get(key);
      if (keyBinding != null) {
         ++keyBinding.timesPressed;
      }

   }

   public static void setKeyPressed(InputUtil.Key key, boolean pressed) {
      KeyBinding keyBinding = (KeyBinding)KEY_TO_BINDINGS.get(key);
      if (keyBinding != null) {
         keyBinding.setPressed(pressed);
      }

   }

   public static void updatePressedStates() {
      Iterator var0 = KEYS_BY_ID.values().iterator();

      while(var0.hasNext()) {
         KeyBinding keyBinding = (KeyBinding)var0.next();
         if (keyBinding.boundKey.getCategory() == InputUtil.Type.KEYSYM && keyBinding.boundKey.getCode() != InputUtil.UNKNOWN_KEY.getCode()) {
            keyBinding.setPressed(InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), keyBinding.boundKey.getCode()));
         }
      }

   }

   public static void unpressAll() {
      Iterator var0 = KEYS_BY_ID.values().iterator();

      while(var0.hasNext()) {
         KeyBinding keyBinding = (KeyBinding)var0.next();
         keyBinding.reset();
      }

   }

   public static void untoggleStickyKeys() {
      Iterator var0 = KEYS_BY_ID.values().iterator();

      while(var0.hasNext()) {
         KeyBinding keyBinding = (KeyBinding)var0.next();
         if (keyBinding instanceof StickyKeyBinding stickyKeyBinding) {
            stickyKeyBinding.untoggle();
         }
      }

   }

   public static void updateKeysByCode() {
      KEY_TO_BINDINGS.clear();
      Iterator var0 = KEYS_BY_ID.values().iterator();

      while(var0.hasNext()) {
         KeyBinding keyBinding = (KeyBinding)var0.next();
         KEY_TO_BINDINGS.put(keyBinding.boundKey, keyBinding);
      }

   }

   public KeyBinding(String translationKey, int code, String category) {
      this(translationKey, InputUtil.Type.KEYSYM, code, category);
   }

   public KeyBinding(String translationKey, InputUtil.Type type, int code, String category) {
      this.translationKey = translationKey;
      this.boundKey = type.createFromCode(code);
      this.defaultKey = this.boundKey;
      this.category = category;
      KEYS_BY_ID.put(translationKey, this);
      KEY_TO_BINDINGS.put(this.boundKey, this);
      KEY_CATEGORIES.add(category);
   }

   public boolean isPressed() {
      return this.pressed;
   }

   public String getCategory() {
      return this.category;
   }

   public boolean wasPressed() {
      if (this.timesPressed == 0) {
         return false;
      } else {
         --this.timesPressed;
         return true;
      }
   }

   private void reset() {
      this.timesPressed = 0;
      this.setPressed(false);
   }

   public String getTranslationKey() {
      return this.translationKey;
   }

   public InputUtil.Key getDefaultKey() {
      return this.defaultKey;
   }

   public void setBoundKey(InputUtil.Key boundKey) {
      this.boundKey = boundKey;
   }

   public int compareTo(KeyBinding keyBinding) {
      return this.category.equals(keyBinding.category) ? I18n.translate(this.translationKey).compareTo(I18n.translate(keyBinding.translationKey)) : ((Integer)CATEGORY_ORDER_MAP.get(this.category)).compareTo((Integer)CATEGORY_ORDER_MAP.get(keyBinding.category));
   }

   public static Supplier getLocalizedName(String id) {
      KeyBinding keyBinding = (KeyBinding)KEYS_BY_ID.get(id);
      if (keyBinding == null) {
         return () -> {
            return Text.translatable(id);
         };
      } else {
         Objects.requireNonNull(keyBinding);
         return keyBinding::getBoundKeyLocalizedText;
      }
   }

   public boolean equals(KeyBinding other) {
      return this.boundKey.equals(other.boundKey);
   }

   public boolean isUnbound() {
      return this.boundKey.equals(InputUtil.UNKNOWN_KEY);
   }

   public boolean matchesKey(int keyCode, int scanCode) {
      if (keyCode == InputUtil.UNKNOWN_KEY.getCode()) {
         return this.boundKey.getCategory() == InputUtil.Type.SCANCODE && this.boundKey.getCode() == scanCode;
      } else {
         return this.boundKey.getCategory() == InputUtil.Type.KEYSYM && this.boundKey.getCode() == keyCode;
      }
   }

   public boolean matchesMouse(int code) {
      return this.boundKey.getCategory() == InputUtil.Type.MOUSE && this.boundKey.getCode() == code;
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

   @Nullable
   public static KeyBinding byId(String id) {
      return (KeyBinding)KEYS_BY_ID.get(id);
   }

   // $FF: synthetic method
   public int compareTo(final Object other) {
      return this.compareTo((KeyBinding)other);
   }
}
