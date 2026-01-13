/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.glfw.GLFW
 */
package net.minecraft.client.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Locale;
import java.util.function.BiFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.lwjgl.glfw.GLFW;

@Environment(value=EnvType.CLIENT)
public static final class InputUtil.Type
extends Enum<InputUtil.Type> {
    public static final /* enum */ InputUtil.Type KEYSYM = new InputUtil.Type("key.keyboard", (keyCode, translationKey) -> {
        if (UNKNOWN_TRANSLATION_KEY.equals(translationKey)) {
            return Text.translatable(translationKey);
        }
        String string = GLFW.glfwGetKeyName((int)keyCode, (int)-1);
        return string != null ? Text.literal(string.toUpperCase(Locale.ROOT)) : Text.translatable(translationKey);
    });
    public static final /* enum */ InputUtil.Type SCANCODE = new InputUtil.Type("scancode", (scanCode, translationKey) -> {
        String string = GLFW.glfwGetKeyName((int)-1, (int)scanCode);
        return string != null ? Text.literal(string) : Text.translatable(translationKey);
    });
    public static final /* enum */ InputUtil.Type MOUSE = new InputUtil.Type("key.mouse", (buttonCode, translationKey) -> Language.getInstance().hasTranslation((String)translationKey) ? Text.translatable(translationKey) : Text.translatable("key.mouse", buttonCode + 1));
    private static final String UNKNOWN_TRANSLATION_KEY = "key.keyboard.unknown";
    private final Int2ObjectMap<InputUtil.Key> map = new Int2ObjectOpenHashMap();
    final String name;
    final BiFunction<Integer, String, Text> textTranslator;
    private static final /* synthetic */ InputUtil.Type[] field_1670;

    public static InputUtil.Type[] values() {
        return (InputUtil.Type[])field_1670.clone();
    }

    public static InputUtil.Type valueOf(String string) {
        return Enum.valueOf(InputUtil.Type.class, string);
    }

    private static void mapKey(InputUtil.Type type, String translationKey, int keyCode) {
        InputUtil.Key key = new InputUtil.Key(translationKey, type, keyCode);
        type.map.put(keyCode, (Object)key);
    }

    private InputUtil.Type(String name, BiFunction<Integer, String, Text> textTranslator) {
        this.name = name;
        this.textTranslator = textTranslator;
    }

    public InputUtil.Key createFromCode(int code2) {
        return (InputUtil.Key)this.map.computeIfAbsent(code2, code -> {
            int i = code;
            if (this == MOUSE) {
                ++i;
            }
            String string = this.name + "." + i;
            return new InputUtil.Key(string, this, code);
        });
    }

    private static /* synthetic */ InputUtil.Type[] method_36810() {
        return new InputUtil.Type[]{KEYSYM, SCANCODE, MOUSE};
    }

    static {
        field_1670 = InputUtil.Type.method_36810();
        InputUtil.Type.mapKey(KEYSYM, UNKNOWN_TRANSLATION_KEY, -1);
        InputUtil.Type.mapKey(MOUSE, "key.mouse.left", 0);
        InputUtil.Type.mapKey(MOUSE, "key.mouse.right", 1);
        InputUtil.Type.mapKey(MOUSE, "key.mouse.middle", 2);
        InputUtil.Type.mapKey(MOUSE, "key.mouse.4", 3);
        InputUtil.Type.mapKey(MOUSE, "key.mouse.5", 4);
        InputUtil.Type.mapKey(MOUSE, "key.mouse.6", 5);
        InputUtil.Type.mapKey(MOUSE, "key.mouse.7", 6);
        InputUtil.Type.mapKey(MOUSE, "key.mouse.8", 7);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.0", 48);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.1", 49);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.2", 50);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.3", 51);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.4", 52);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.5", 53);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.6", 54);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.7", 55);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.8", 56);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.9", 57);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.a", 65);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.b", 66);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.c", 67);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.d", 68);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.e", 69);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f", 70);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.g", 71);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.h", 72);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.i", 73);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.j", 74);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.k", 75);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.l", 76);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.m", 77);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.n", 78);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.o", 79);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.p", 80);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.q", 81);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.r", 82);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.s", 83);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.t", 84);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.u", 85);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.v", 86);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.w", 87);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.x", 88);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.y", 89);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.z", 90);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f1", 290);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f2", 291);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f3", 292);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f4", 293);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f5", 294);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f6", 295);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f7", 296);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f8", 297);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f9", 298);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f10", 299);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f11", 300);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f12", 301);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f13", 302);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f14", 303);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f15", 304);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f16", 305);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f17", 306);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f18", 307);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f19", 308);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f20", 309);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f21", 310);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f22", 311);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f23", 312);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f24", 313);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.f25", 314);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.num.lock", 282);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.keypad.0", 320);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.keypad.1", 321);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.keypad.2", 322);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.keypad.3", 323);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.keypad.4", 324);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.keypad.5", 325);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.keypad.6", 326);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.keypad.7", 327);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.keypad.8", 328);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.keypad.9", 329);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.keypad.add", 334);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.keypad.decimal", 330);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.keypad.enter", 335);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.keypad.equal", 336);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.keypad.multiply", 332);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.keypad.divide", 331);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.keypad.subtract", 333);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.down", 264);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.left", 263);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.right", 262);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.up", 265);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.apostrophe", 39);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.backslash", 92);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.comma", 44);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.equal", 61);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.grave.accent", 96);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.left.bracket", 91);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.minus", 45);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.period", 46);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.right.bracket", 93);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.semicolon", 59);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.slash", 47);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.space", 32);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.tab", 258);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.left.alt", 342);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.left.control", 341);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.left.shift", 340);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.left.win", 343);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.right.alt", 346);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.right.control", 345);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.right.shift", 344);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.right.win", 347);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.enter", 257);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.escape", 256);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.backspace", 259);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.delete", 261);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.end", 269);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.home", 268);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.insert", 260);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.page.down", 267);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.page.up", 266);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.caps.lock", 280);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.pause", 284);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.scroll.lock", 281);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.menu", 348);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.print.screen", 283);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.world.1", 161);
        InputUtil.Type.mapKey(KEYSYM, "key.keyboard.world.2", 162);
    }
}
