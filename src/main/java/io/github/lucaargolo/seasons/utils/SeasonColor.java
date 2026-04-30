package io.github.lucaargolo.seasons.utils;

import com.google.gson.JsonElement;

public class SeasonColor {
    private final int springColor;
    private final int summerColor;
    private final int fallColor;
    private final int winterColor;

    public SeasonColor(int springColor, int summerColor, int fallColor, int winterColor) {
        this.springColor = springColor;
        this.summerColor = summerColor;
        this.fallColor = fallColor;
        this.winterColor = winterColor;
    }

    public SeasonColor(JsonElement json) {
        this.springColor = getStringColor(json.getAsJsonObject().get("spring").getAsString());
        this.summerColor = getStringColor(json.getAsJsonObject().get("summer").getAsString());
        this.fallColor = getStringColor(json.getAsJsonObject().get("fall").getAsString());
        this.winterColor = getStringColor(json.getAsJsonObject().get("winter").getAsString());
    }

    private int getStringColor(String color) {
        if (color.startsWith("0x")) {
            return Integer.parseInt(color.replace("0x", ""), 16);
        } else if (color.startsWith("#")) {
            return Integer.parseInt(color.replace("#", ""), 16);
        } else {
            return Integer.parseInt(color);
        }
    }

    public int getColor(Season season) {
        return switch (season) {
            case SPRING -> springColor;
            case SUMMER -> summerColor;
            case FALL -> fallColor;
            case WINTER -> winterColor;
        };
    }

    public int getBlendedColor(Season current, Season next, float t) {
        return lerpColor(getColor(current), getColor(next), t);
    }

    public static int lerpColor(int a, int b, float t) {
        int b0 = Math.round(( a        & 0xFF) * (1 - t) + ( b        & 0xFF) * t);
        int b1 = Math.round(((a >>  8) & 0xFF) * (1 - t) + ((b >>  8) & 0xFF) * t);
        int b2 = Math.round(((a >> 16) & 0xFF) * (1 - t) + ((b >> 16) & 0xFF) * t);
        int b3 = Math.round(((a >> 24) & 0xFF) * (1 - t) + ((b >> 24) & 0xFF) * t);
        return (b3 << 24) | (b2 << 16) | (b1 << 8) | b0;
    }
}
