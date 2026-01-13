/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWCharModsCallbackI
 *  org.lwjgl.glfw.GLFWCursorPosCallbackI
 *  org.lwjgl.glfw.GLFWDropCallbackI
 *  org.lwjgl.glfw.GLFWKeyCallbackI
 *  org.lwjgl.glfw.GLFWMouseButtonCallbackI
 *  org.lwjgl.glfw.GLFWScrollCallbackI
 */
package net.minecraft.client.util;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWDropCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;

@Environment(value=EnvType.CLIENT)
public class InputUtil {
    private static final @Nullable MethodHandle GLFW_RAW_MOUSE_MOTION_SUPPORTED_HANDLE;
    private static final int GLFW_RAW_MOUSE_MOTION;
    public static final int GLFW_KEY_0 = 48;
    public static final int GLFW_KEY_1 = 49;
    public static final int GLFW_KEY_2 = 50;
    public static final int GLFW_KEY_3 = 51;
    public static final int GLFW_KEY_4 = 52;
    public static final int GLFW_KEY_5 = 53;
    public static final int GLFW_KEY_6 = 54;
    public static final int GLFW_KEY_7 = 55;
    public static final int GLFW_KEY_8 = 56;
    public static final int GLFW_KEY_9 = 57;
    public static final int GLFW_KEY_A = 65;
    public static final int GLFW_KEY_B = 66;
    public static final int GLFW_KEY_C = 67;
    public static final int GLFW_KEY_D = 68;
    public static final int GLFW_KEY_E = 69;
    public static final int GLFW_KEY_F = 70;
    public static final int GLFW_KEY_G = 71;
    public static final int GLFW_KEY_H = 72;
    public static final int GLFW_KEY_I = 73;
    public static final int GLFW_KEY_J = 74;
    public static final int GLFW_KEY_K = 75;
    public static final int GLFW_KEY_L = 76;
    public static final int GLFW_KEY_M = 77;
    public static final int GLFW_KEY_N = 78;
    public static final int GLFW_KEY_O = 79;
    public static final int GLFW_KEY_P = 80;
    public static final int GLFW_KEY_Q = 81;
    public static final int GLFW_KEY_R = 82;
    public static final int GLFW_KEY_S = 83;
    public static final int GLFW_KEY_T = 84;
    public static final int GLFW_KEY_U = 85;
    public static final int GLFW_KEY_V = 86;
    public static final int GLFW_KEY_W = 87;
    public static final int GLFW_KEY_X = 88;
    public static final int GLFW_KEY_Y = 89;
    public static final int GLFW_KEY_Z = 90;
    public static final int GLFW_KEY_F1 = 290;
    public static final int GLFW_KEY_F2 = 291;
    public static final int GLFW_KEY_F3 = 292;
    public static final int GLFW_KEY_F4 = 293;
    public static final int GLFW_KEY_F5 = 294;
    public static final int GLFW_KEY_F6 = 295;
    public static final int GLFW_KEY_F7 = 296;
    public static final int GLFW_KEY_F8 = 297;
    public static final int GLFW_KEY_F9 = 298;
    public static final int GLFW_KEY_F10 = 299;
    public static final int GLFW_KEY_F11 = 300;
    public static final int GLFW_KEY_F12 = 301;
    public static final int GLFW_KEY_F13 = 302;
    public static final int GLFW_KEY_F14 = 303;
    public static final int GLFW_KEY_F15 = 304;
    public static final int GLFW_KEY_F16 = 305;
    public static final int GLFW_KEY_F17 = 306;
    public static final int GLFW_KEY_F18 = 307;
    public static final int GLFW_KEY_F19 = 308;
    public static final int GLFW_KEY_F20 = 309;
    public static final int GLFW_KEY_F21 = 310;
    public static final int GLFW_KEY_F22 = 311;
    public static final int GLFW_KEY_F23 = 312;
    public static final int GLFW_KEY_F24 = 313;
    public static final int GLFW_KEY_F25 = 314;
    public static final int GLFW_KEY_NUM_LOCK = 282;
    public static final int GLFW_KEY_KP_0 = 320;
    public static final int GLFW_KEY_KP_1 = 321;
    public static final int GLFW_KEY_KP_2 = 322;
    public static final int GLFW_KEY_KP_3 = 323;
    public static final int GLFW_KEY_KP_4 = 324;
    public static final int GLFW_KEY_KP_5 = 325;
    public static final int GLFW_KEY_KP_6 = 326;
    public static final int GLFW_KEY_KP_7 = 327;
    public static final int GLFW_KEY_KP_8 = 328;
    public static final int GLFW_KEY_KP_9 = 329;
    public static final int GLFW_KEY_KP_DECIMAL = 330;
    public static final int GLFW_KEY_KP_ENTER = 335;
    public static final int GLFW_KEY_KP_EQUAL = 336;
    public static final int GLFW_KEY_DOWN = 264;
    public static final int GLFW_KEY_LEFT = 263;
    public static final int GLFW_KEY_RIGHT = 262;
    public static final int GLFW_KEY_UP = 265;
    public static final int GLFW_KEY_KP_ADD = 334;
    public static final int GLFW_KEY_APOSTROPHE = 39;
    public static final int GLFW_KEY_BACKSLASH = 92;
    public static final int GLFW_KEY_COMMA = 44;
    public static final int GLFW_KEY_EQUAL = 61;
    public static final int GLFW_KEY_GRAVE_ACCENT = 96;
    public static final int GLFW_KEY_LEFT_BRACKET = 91;
    public static final int GLFW_KEY_MINUS = 45;
    public static final int GLFW_KEY_KP_MULTIPLY = 332;
    public static final int GLFW_KEY_PERIOD = 46;
    public static final int GLFW_KEY_RIGHT_BRACKET = 93;
    public static final int GLFW_KEY_SEMICOLON = 59;
    public static final int GLFW_KEY_SLASH = 47;
    public static final int GLFW_KEY_SPACE = 32;
    public static final int GLFW_KEY_TAB = 258;
    public static final int GLFW_KEY_LEFT_ALT = 342;
    public static final int GLFW_KEY_LEFT_CONTROL = 341;
    public static final int GLFW_KEY_LEFT_SHIFT = 340;
    public static final int GLFW_KEY_LEFT_SUPER = 343;
    public static final int GLFW_KEY_RIGHT_ALT = 346;
    public static final int GLFW_KEY_RIGHT_CONTROL = 345;
    public static final int GLFW_KEY_RIGHT_SHIFT = 344;
    public static final int GLFW_KEY_RIGHT_SUPER = 347;
    public static final int GLFW_KEY_ENTER = 257;
    public static final int GLFW_KEY_ESCAPE = 256;
    public static final int GLFW_KEY_BACKSPACE = 259;
    public static final int GLFW_KEY_DELETE = 261;
    public static final int GLFW_KEY_END = 269;
    public static final int GLFW_KEY_HOME = 268;
    public static final int GLFW_KEY_INSERT = 260;
    public static final int GLFW_KEY_PAGE_DOWN = 267;
    public static final int GLFW_KEY_PAGE_UP = 266;
    public static final int GLFW_KEY_CAPS_LOCK = 280;
    public static final int GLFW_KEY_PAUSE = 284;
    public static final int GLFW_KEY_SCROLL_LOCK = 281;
    public static final int GLFW_KEY_PRINT_SCREEN = 283;
    public static final int GLFW_PRESS = 1;
    public static final int GLFW_RELEASE = 0;
    public static final int GLFW_REPEAT = 2;
    public static final int GLFW_MOUSE_BUTTON_LEFT = 0;
    public static final int GLFW_MOUSE_BUTTON_RIGHT = 1;
    public static final int GLFW_MOUSE_BUTTON_MIDDLE = 2;
    public static final int field_63448 = 3;
    public static final int field_63449 = 4;
    public static final int field_63450 = 5;
    public static final int field_63451 = 6;
    public static final int field_63452 = 0;
    public static final int GLFW_MOD_SHIFT = 1;
    public static final int GLFW_MOD_CONTROL = 2;
    public static final int GLFW_MOD_ALT = 4;
    public static final int GLFW_MOD_SUPER = 8;
    public static final int GLFW_MOD_CAPS_LOCK = 16;
    public static final int GLFW_MOD_NUM_LOCK = 32;
    public static final int GLFW_CURSOR = 208897;
    public static final int GLFW_CURSOR_DISABLED = 212995;
    public static final int GLFW_CURSOR_NORMAL = 212993;
    public static final Key UNKNOWN_KEY;

    public static Key fromKeyCode(KeyInput key) {
        if (key.key() == -1) {
            return Type.SCANCODE.createFromCode(key.scancode());
        }
        return Type.KEYSYM.createFromCode(key.key());
    }

    public static Key fromTranslationKey(String translationKey) {
        if (Key.KEYS.containsKey(translationKey)) {
            return Key.KEYS.get(translationKey);
        }
        for (Type type : Type.values()) {
            if (!translationKey.startsWith(type.name)) continue;
            String string = translationKey.substring(type.name.length() + 1);
            int i = Integer.parseInt(string);
            if (type == Type.MOUSE) {
                --i;
            }
            return type.createFromCode(i);
        }
        throw new IllegalArgumentException("Unknown key name: " + translationKey);
    }

    public static boolean isKeyPressed(Window window, int code) {
        return GLFW.glfwGetKey((long)window.getHandle(), (int)code) == 1;
    }

    public static void setKeyboardCallbacks(Window window, GLFWKeyCallbackI keyCallback, GLFWCharModsCallbackI charModsCallback) {
        GLFW.glfwSetKeyCallback((long)window.getHandle(), (GLFWKeyCallbackI)keyCallback);
        GLFW.glfwSetCharModsCallback((long)window.getHandle(), (GLFWCharModsCallbackI)charModsCallback);
    }

    public static void setMouseCallbacks(Window window, GLFWCursorPosCallbackI cursorPosCallback, GLFWMouseButtonCallbackI mouseButtonCallback, GLFWScrollCallbackI scrollCallback, GLFWDropCallbackI dropCallback) {
        GLFW.glfwSetCursorPosCallback((long)window.getHandle(), (GLFWCursorPosCallbackI)cursorPosCallback);
        GLFW.glfwSetMouseButtonCallback((long)window.getHandle(), (GLFWMouseButtonCallbackI)mouseButtonCallback);
        GLFW.glfwSetScrollCallback((long)window.getHandle(), (GLFWScrollCallbackI)scrollCallback);
        GLFW.glfwSetDropCallback((long)window.getHandle(), (GLFWDropCallbackI)dropCallback);
    }

    public static void setCursorParameters(Window window, int inputModeValue, double x, double y) {
        GLFW.glfwSetCursorPos((long)window.getHandle(), (double)x, (double)y);
        GLFW.glfwSetInputMode((long)window.getHandle(), (int)208897, (int)inputModeValue);
    }

    public static boolean isRawMouseMotionSupported() {
        try {
            return GLFW_RAW_MOUSE_MOTION_SUPPORTED_HANDLE != null && GLFW_RAW_MOUSE_MOTION_SUPPORTED_HANDLE.invokeExact();
        }
        catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public static void setRawMouseMotionMode(Window window, boolean value) {
        if (InputUtil.isRawMouseMotionSupported()) {
            GLFW.glfwSetInputMode((long)window.getHandle(), (int)GLFW_RAW_MOUSE_MOTION, (int)(value ? 1 : 0));
        }
    }

    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodType methodType = MethodType.methodType(Boolean.TYPE);
        MethodHandle methodHandle = null;
        int i = 0;
        try {
            methodHandle = lookup.findStatic(GLFW.class, "glfwRawMouseMotionSupported", methodType);
            MethodHandle methodHandle2 = lookup.findStaticGetter(GLFW.class, "GLFW_RAW_MOUSE_MOTION", Integer.TYPE);
            i = methodHandle2.invokeExact();
        }
        catch (NoSuchFieldException | NoSuchMethodException methodHandle2) {
        }
        catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
        GLFW_RAW_MOUSE_MOTION_SUPPORTED_HANDLE = methodHandle;
        GLFW_RAW_MOUSE_MOTION = i;
        UNKNOWN_KEY = Type.KEYSYM.createFromCode(-1);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type KEYSYM = new Type("key.keyboard", (keyCode, translationKey) -> {
            if (UNKNOWN_TRANSLATION_KEY.equals(translationKey)) {
                return Text.translatable(translationKey);
            }
            String string = GLFW.glfwGetKeyName((int)keyCode, (int)-1);
            return string != null ? Text.literal(string.toUpperCase(Locale.ROOT)) : Text.translatable(translationKey);
        });
        public static final /* enum */ Type SCANCODE = new Type("scancode", (scanCode, translationKey) -> {
            String string = GLFW.glfwGetKeyName((int)-1, (int)scanCode);
            return string != null ? Text.literal(string) : Text.translatable(translationKey);
        });
        public static final /* enum */ Type MOUSE = new Type("key.mouse", (buttonCode, translationKey) -> Language.getInstance().hasTranslation((String)translationKey) ? Text.translatable(translationKey) : Text.translatable("key.mouse", buttonCode + 1));
        private static final String UNKNOWN_TRANSLATION_KEY = "key.keyboard.unknown";
        private final Int2ObjectMap<Key> map = new Int2ObjectOpenHashMap();
        final String name;
        final BiFunction<Integer, String, Text> textTranslator;
        private static final /* synthetic */ Type[] field_1670;

        public static Type[] values() {
            return (Type[])field_1670.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private static void mapKey(Type type, String translationKey, int keyCode) {
            Key key = new Key(translationKey, type, keyCode);
            type.map.put(keyCode, (Object)key);
        }

        private Type(String name, BiFunction<Integer, String, Text> textTranslator) {
            this.name = name;
            this.textTranslator = textTranslator;
        }

        public Key createFromCode(int code2) {
            return (Key)this.map.computeIfAbsent(code2, code -> {
                int i = code;
                if (this == MOUSE) {
                    ++i;
                }
                String string = this.name + "." + i;
                return new Key(string, this, code);
            });
        }

        private static /* synthetic */ Type[] method_36810() {
            return new Type[]{KEYSYM, SCANCODE, MOUSE};
        }

        static {
            field_1670 = Type.method_36810();
            Type.mapKey(KEYSYM, UNKNOWN_TRANSLATION_KEY, -1);
            Type.mapKey(MOUSE, "key.mouse.left", 0);
            Type.mapKey(MOUSE, "key.mouse.right", 1);
            Type.mapKey(MOUSE, "key.mouse.middle", 2);
            Type.mapKey(MOUSE, "key.mouse.4", 3);
            Type.mapKey(MOUSE, "key.mouse.5", 4);
            Type.mapKey(MOUSE, "key.mouse.6", 5);
            Type.mapKey(MOUSE, "key.mouse.7", 6);
            Type.mapKey(MOUSE, "key.mouse.8", 7);
            Type.mapKey(KEYSYM, "key.keyboard.0", 48);
            Type.mapKey(KEYSYM, "key.keyboard.1", 49);
            Type.mapKey(KEYSYM, "key.keyboard.2", 50);
            Type.mapKey(KEYSYM, "key.keyboard.3", 51);
            Type.mapKey(KEYSYM, "key.keyboard.4", 52);
            Type.mapKey(KEYSYM, "key.keyboard.5", 53);
            Type.mapKey(KEYSYM, "key.keyboard.6", 54);
            Type.mapKey(KEYSYM, "key.keyboard.7", 55);
            Type.mapKey(KEYSYM, "key.keyboard.8", 56);
            Type.mapKey(KEYSYM, "key.keyboard.9", 57);
            Type.mapKey(KEYSYM, "key.keyboard.a", 65);
            Type.mapKey(KEYSYM, "key.keyboard.b", 66);
            Type.mapKey(KEYSYM, "key.keyboard.c", 67);
            Type.mapKey(KEYSYM, "key.keyboard.d", 68);
            Type.mapKey(KEYSYM, "key.keyboard.e", 69);
            Type.mapKey(KEYSYM, "key.keyboard.f", 70);
            Type.mapKey(KEYSYM, "key.keyboard.g", 71);
            Type.mapKey(KEYSYM, "key.keyboard.h", 72);
            Type.mapKey(KEYSYM, "key.keyboard.i", 73);
            Type.mapKey(KEYSYM, "key.keyboard.j", 74);
            Type.mapKey(KEYSYM, "key.keyboard.k", 75);
            Type.mapKey(KEYSYM, "key.keyboard.l", 76);
            Type.mapKey(KEYSYM, "key.keyboard.m", 77);
            Type.mapKey(KEYSYM, "key.keyboard.n", 78);
            Type.mapKey(KEYSYM, "key.keyboard.o", 79);
            Type.mapKey(KEYSYM, "key.keyboard.p", 80);
            Type.mapKey(KEYSYM, "key.keyboard.q", 81);
            Type.mapKey(KEYSYM, "key.keyboard.r", 82);
            Type.mapKey(KEYSYM, "key.keyboard.s", 83);
            Type.mapKey(KEYSYM, "key.keyboard.t", 84);
            Type.mapKey(KEYSYM, "key.keyboard.u", 85);
            Type.mapKey(KEYSYM, "key.keyboard.v", 86);
            Type.mapKey(KEYSYM, "key.keyboard.w", 87);
            Type.mapKey(KEYSYM, "key.keyboard.x", 88);
            Type.mapKey(KEYSYM, "key.keyboard.y", 89);
            Type.mapKey(KEYSYM, "key.keyboard.z", 90);
            Type.mapKey(KEYSYM, "key.keyboard.f1", 290);
            Type.mapKey(KEYSYM, "key.keyboard.f2", 291);
            Type.mapKey(KEYSYM, "key.keyboard.f3", 292);
            Type.mapKey(KEYSYM, "key.keyboard.f4", 293);
            Type.mapKey(KEYSYM, "key.keyboard.f5", 294);
            Type.mapKey(KEYSYM, "key.keyboard.f6", 295);
            Type.mapKey(KEYSYM, "key.keyboard.f7", 296);
            Type.mapKey(KEYSYM, "key.keyboard.f8", 297);
            Type.mapKey(KEYSYM, "key.keyboard.f9", 298);
            Type.mapKey(KEYSYM, "key.keyboard.f10", 299);
            Type.mapKey(KEYSYM, "key.keyboard.f11", 300);
            Type.mapKey(KEYSYM, "key.keyboard.f12", 301);
            Type.mapKey(KEYSYM, "key.keyboard.f13", 302);
            Type.mapKey(KEYSYM, "key.keyboard.f14", 303);
            Type.mapKey(KEYSYM, "key.keyboard.f15", 304);
            Type.mapKey(KEYSYM, "key.keyboard.f16", 305);
            Type.mapKey(KEYSYM, "key.keyboard.f17", 306);
            Type.mapKey(KEYSYM, "key.keyboard.f18", 307);
            Type.mapKey(KEYSYM, "key.keyboard.f19", 308);
            Type.mapKey(KEYSYM, "key.keyboard.f20", 309);
            Type.mapKey(KEYSYM, "key.keyboard.f21", 310);
            Type.mapKey(KEYSYM, "key.keyboard.f22", 311);
            Type.mapKey(KEYSYM, "key.keyboard.f23", 312);
            Type.mapKey(KEYSYM, "key.keyboard.f24", 313);
            Type.mapKey(KEYSYM, "key.keyboard.f25", 314);
            Type.mapKey(KEYSYM, "key.keyboard.num.lock", 282);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.0", 320);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.1", 321);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.2", 322);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.3", 323);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.4", 324);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.5", 325);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.6", 326);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.7", 327);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.8", 328);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.9", 329);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.add", 334);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.decimal", 330);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.enter", 335);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.equal", 336);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.multiply", 332);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.divide", 331);
            Type.mapKey(KEYSYM, "key.keyboard.keypad.subtract", 333);
            Type.mapKey(KEYSYM, "key.keyboard.down", 264);
            Type.mapKey(KEYSYM, "key.keyboard.left", 263);
            Type.mapKey(KEYSYM, "key.keyboard.right", 262);
            Type.mapKey(KEYSYM, "key.keyboard.up", 265);
            Type.mapKey(KEYSYM, "key.keyboard.apostrophe", 39);
            Type.mapKey(KEYSYM, "key.keyboard.backslash", 92);
            Type.mapKey(KEYSYM, "key.keyboard.comma", 44);
            Type.mapKey(KEYSYM, "key.keyboard.equal", 61);
            Type.mapKey(KEYSYM, "key.keyboard.grave.accent", 96);
            Type.mapKey(KEYSYM, "key.keyboard.left.bracket", 91);
            Type.mapKey(KEYSYM, "key.keyboard.minus", 45);
            Type.mapKey(KEYSYM, "key.keyboard.period", 46);
            Type.mapKey(KEYSYM, "key.keyboard.right.bracket", 93);
            Type.mapKey(KEYSYM, "key.keyboard.semicolon", 59);
            Type.mapKey(KEYSYM, "key.keyboard.slash", 47);
            Type.mapKey(KEYSYM, "key.keyboard.space", 32);
            Type.mapKey(KEYSYM, "key.keyboard.tab", 258);
            Type.mapKey(KEYSYM, "key.keyboard.left.alt", 342);
            Type.mapKey(KEYSYM, "key.keyboard.left.control", 341);
            Type.mapKey(KEYSYM, "key.keyboard.left.shift", 340);
            Type.mapKey(KEYSYM, "key.keyboard.left.win", 343);
            Type.mapKey(KEYSYM, "key.keyboard.right.alt", 346);
            Type.mapKey(KEYSYM, "key.keyboard.right.control", 345);
            Type.mapKey(KEYSYM, "key.keyboard.right.shift", 344);
            Type.mapKey(KEYSYM, "key.keyboard.right.win", 347);
            Type.mapKey(KEYSYM, "key.keyboard.enter", 257);
            Type.mapKey(KEYSYM, "key.keyboard.escape", 256);
            Type.mapKey(KEYSYM, "key.keyboard.backspace", 259);
            Type.mapKey(KEYSYM, "key.keyboard.delete", 261);
            Type.mapKey(KEYSYM, "key.keyboard.end", 269);
            Type.mapKey(KEYSYM, "key.keyboard.home", 268);
            Type.mapKey(KEYSYM, "key.keyboard.insert", 260);
            Type.mapKey(KEYSYM, "key.keyboard.page.down", 267);
            Type.mapKey(KEYSYM, "key.keyboard.page.up", 266);
            Type.mapKey(KEYSYM, "key.keyboard.caps.lock", 280);
            Type.mapKey(KEYSYM, "key.keyboard.pause", 284);
            Type.mapKey(KEYSYM, "key.keyboard.scroll.lock", 281);
            Type.mapKey(KEYSYM, "key.keyboard.menu", 348);
            Type.mapKey(KEYSYM, "key.keyboard.print.screen", 283);
            Type.mapKey(KEYSYM, "key.keyboard.world.1", 161);
            Type.mapKey(KEYSYM, "key.keyboard.world.2", 162);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Key {
        private final String translationKey;
        private final Type type;
        private final int code;
        private final Supplier<Text> localizedText;
        static final Map<String, Key> KEYS = Maps.newHashMap();

        Key(String translationKey, Type type, int code) {
            this.translationKey = translationKey;
            this.type = type;
            this.code = code;
            this.localizedText = Suppliers.memoize(() -> type.textTranslator.apply(code, translationKey));
            KEYS.put(translationKey, this);
        }

        public Type getCategory() {
            return this.type;
        }

        public int getCode() {
            return this.code;
        }

        public String getTranslationKey() {
            return this.translationKey;
        }

        public Text getLocalizedText() {
            return this.localizedText.get();
        }

        public OptionalInt toInt() {
            if (this.code >= 48 && this.code <= 57) {
                return OptionalInt.of(this.code - 48);
            }
            if (this.code >= 320 && this.code <= 329) {
                return OptionalInt.of(this.code - 320);
            }
            return OptionalInt.empty();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Key key = (Key)o;
            return this.code == key.code && this.type == key.type;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.type, this.code});
        }

        public String toString() {
            return this.translationKey;
        }
    }

    @Retention(value=RetentionPolicy.CLASS)
    @Target(value={ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.TYPE_USE})
    @Environment(value=EnvType.CLIENT)
    public static @interface Keycode {
    }
}
