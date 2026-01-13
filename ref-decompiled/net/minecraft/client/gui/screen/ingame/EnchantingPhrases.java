/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.screen.ingame.EnchantingPhrases
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Style
 *  net.minecraft.text.StyleSpriteSource
 *  net.minecraft.text.StyleSpriteSource$Font
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.random.Random
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class EnchantingPhrases {
    private static final StyleSpriteSource FONT_ID = new StyleSpriteSource.Font(Identifier.ofVanilla((String)"alt"));
    private static final Style STYLE = Style.EMPTY.withFont(FONT_ID);
    private static final EnchantingPhrases INSTANCE = new EnchantingPhrases();
    private final Random random = Random.create();
    private final String[] phrases = new String[]{"the", "elder", "scrolls", "klaatu", "berata", "niktu", "xyzzy", "bless", "curse", "light", "darkness", "fire", "air", "earth", "water", "hot", "dry", "cold", "wet", "ignite", "snuff", "embiggen", "twist", "shorten", "stretch", "fiddle", "destroy", "imbue", "galvanize", "enchant", "free", "limited", "range", "of", "towards", "inside", "sphere", "cube", "self", "other", "ball", "mental", "physical", "grow", "shrink", "demon", "elemental", "spirit", "animal", "creature", "beast", "humanoid", "undead", "fresh", "stale", "phnglui", "mglwnafh", "cthulhu", "rlyeh", "wgahnagl", "fhtagn", "baguette"};

    private EnchantingPhrases() {
    }

    public static EnchantingPhrases getInstance() {
        return INSTANCE;
    }

    public StringVisitable generatePhrase(TextRenderer textRenderer, int width) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = this.random.nextInt(2) + 3;
        for (int j = 0; j < i; ++j) {
            if (j != 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append((String)Util.getRandom((Object[])this.phrases, (Random)this.random));
        }
        return textRenderer.getTextHandler().trimToWidth((StringVisitable)Text.literal((String)stringBuilder.toString()).fillStyle(STYLE), width, Style.EMPTY);
    }

    public void setSeed(long seed) {
        this.random.setSeed(seed);
    }
}

