package io.github.lucaargolo.seasons.utils;

import io.github.lucaargolo.seasons.FabricSeasons;
import net.minecraft.ChatFormatting;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum Season implements StringRepresentable {
    SPRING(2, ChatFormatting.GREEN, ChatFormatting.DARK_GREEN),
    SUMMER(3, ChatFormatting.GOLD, ChatFormatting.GOLD),
    FALL(1, ChatFormatting.RED, ChatFormatting.RED),
    WINTER(0, ChatFormatting.AQUA, ChatFormatting.DARK_AQUA);

    private final int temperature;
    private final ChatFormatting formatting;
    private final ChatFormatting darkFormatting;

    Season(int temperature, ChatFormatting formatting, ChatFormatting darkFormatting) {
        this.temperature = temperature;
        this.formatting = formatting;
        this.darkFormatting = darkFormatting;
    }

    public int getTemperature() {
        return temperature;
    }

    public ChatFormatting getFormatting() {
        return formatting;
    }

    public ChatFormatting getDarkFormatting() {
        return darkFormatting;
    }

    public String getTranslationKey() {
        return "tooltip.seasons." + name().toLowerCase(Locale.ROOT);
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public Season getNext() {
        return switch (this) {
            case SUMMER -> Season.FALL;
            case FALL -> Season.WINTER;
            case WINTER -> Season.SPRING;
            default -> Season.SUMMER;
        };
    }

    public int getSeasonLength() {
        return switch (this) {
            case SUMMER -> FabricSeasons.CONFIG.getSummerLength();
            case FALL -> FabricSeasons.CONFIG.getFallLength();
            case WINTER -> FabricSeasons.CONFIG.getWinterLength();
            default -> FabricSeasons.CONFIG.getSpringLength();
        };
    }
}
