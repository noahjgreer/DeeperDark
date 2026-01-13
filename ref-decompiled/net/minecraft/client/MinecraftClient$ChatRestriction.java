/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(value=EnvType.CLIENT)
public static abstract sealed class MinecraftClient.ChatRestriction
extends Enum<MinecraftClient.ChatRestriction> {
    public static final /* enum */ MinecraftClient.ChatRestriction ENABLED = new MinecraftClient.ChatRestriction(ScreenTexts.EMPTY){

        @Override
        public boolean allowsChat(boolean singlePlayer) {
            return true;
        }
    };
    public static final /* enum */ MinecraftClient.ChatRestriction DISABLED_BY_OPTIONS = new MinecraftClient.ChatRestriction(Text.translatable("chat.disabled.options").formatted(Formatting.RED)){

        @Override
        public boolean allowsChat(boolean singlePlayer) {
            return false;
        }
    };
    public static final /* enum */ MinecraftClient.ChatRestriction DISABLED_BY_LAUNCHER = new MinecraftClient.ChatRestriction(Text.translatable("chat.disabled.launcher").formatted(Formatting.RED)){

        @Override
        public boolean allowsChat(boolean singlePlayer) {
            return singlePlayer;
        }
    };
    public static final /* enum */ MinecraftClient.ChatRestriction DISABLED_BY_PROFILE = new MinecraftClient.ChatRestriction(Text.translatable("chat.disabled.profile", Text.keybind(MinecraftClient.instance.options.chatKey.getId())).formatted(Formatting.RED)){

        @Override
        public boolean allowsChat(boolean singlePlayer) {
            return singlePlayer;
        }
    };
    static final Text MORE_INFO_TEXT;
    private final Text description;
    private static final /* synthetic */ MinecraftClient.ChatRestriction[] field_28945;

    public static MinecraftClient.ChatRestriction[] values() {
        return (MinecraftClient.ChatRestriction[])field_28945.clone();
    }

    public static MinecraftClient.ChatRestriction valueOf(String string) {
        return Enum.valueOf(MinecraftClient.ChatRestriction.class, string);
    }

    MinecraftClient.ChatRestriction(Text description) {
        this.description = description;
    }

    public Text getDescription() {
        return this.description;
    }

    public abstract boolean allowsChat(boolean var1);

    private static /* synthetic */ MinecraftClient.ChatRestriction[] method_36862() {
        return new MinecraftClient.ChatRestriction[]{ENABLED, DISABLED_BY_OPTIONS, DISABLED_BY_LAUNCHER, DISABLED_BY_PROFILE};
    }

    static {
        field_28945 = MinecraftClient.ChatRestriction.method_36862();
        MORE_INFO_TEXT = Text.translatable("chat.disabled.profile.moreInfo");
    }
}
