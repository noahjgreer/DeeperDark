/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsAvailability;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsClientIncompatibleScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsParentalConsentScreen;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record RealmsAvailability.Info(RealmsAvailability.Type type, @Nullable RealmsServiceException exception) {
    public RealmsAvailability.Info(RealmsAvailability.Type type) {
        this(type, null);
    }

    public RealmsAvailability.Info(RealmsServiceException exception) {
        this(RealmsAvailability.Type.UNEXPECTED_ERROR, exception);
    }

    public @Nullable Screen createScreen(Screen parent) {
        return switch (this.type.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> null;
            case 1 -> new RealmsClientIncompatibleScreen(parent);
            case 2 -> new RealmsParentalConsentScreen(parent);
            case 3 -> new RealmsGenericErrorScreen(Text.translatable("mco.error.invalid.session.title"), Text.translatable("mco.error.invalid.session.message"), parent);
            case 4 -> new RealmsGenericErrorScreen(Objects.requireNonNull(this.exception), parent);
        };
    }
}
