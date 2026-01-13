/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsError;
import net.minecraft.client.realms.exception.RealmsHttpException;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record RealmsError.SimpleHttpError(int httpCode, @Nullable Text payload) implements RealmsError
{
    public static final RealmsError.SimpleHttpError SERVICE_BUSY = new RealmsError.SimpleHttpError(429, Text.translatable("mco.errorMessage.serviceBusy"));
    public static final Text RETRY_TEXT = Text.translatable("mco.errorMessage.retry");
    public static final String BODY_HTML_OPENING_TAG = "<body>";
    public static final String BODY_HTML_CLOSING_TAG = "</body>";

    public static RealmsError.SimpleHttpError unknownCompatibility(String response) {
        return new RealmsError.SimpleHttpError(500, Text.translatable("mco.errorMessage.realmsService.unknownCompatibility", response));
    }

    public static RealmsError.SimpleHttpError configurationError() {
        return new RealmsError.SimpleHttpError(500, Text.translatable("mco.errorMessage.realmsService.configurationError"));
    }

    public static RealmsError.SimpleHttpError connectivity(RealmsHttpException exception) {
        return new RealmsError.SimpleHttpError(500, Text.translatable("mco.errorMessage.realmsService.connectivity", exception.getMessage()));
    }

    public static RealmsError.SimpleHttpError retryable(int statusCode) {
        return new RealmsError.SimpleHttpError(statusCode, RETRY_TEXT);
    }

    public static RealmsError.SimpleHttpError statusCodeOnly(int statusCode) {
        return new RealmsError.SimpleHttpError(statusCode, null);
    }

    public static RealmsError.SimpleHttpError unreadableHtmlBody(int statusCode, String html) {
        int i = html.indexOf(BODY_HTML_OPENING_TAG);
        int j = html.indexOf(BODY_HTML_CLOSING_TAG);
        if (i >= 0 && j > i) {
            return new RealmsError.SimpleHttpError(statusCode, Text.literal(html.substring(i + BODY_HTML_OPENING_TAG.length(), j).trim()));
        }
        LOGGER.error("Got an error with an unreadable html body {}", (Object)html);
        return new RealmsError.SimpleHttpError(statusCode, null);
    }

    @Override
    public int getErrorCode() {
        return this.httpCode;
    }

    @Override
    public Text getText() {
        return this.payload != null ? this.payload : NO_DETAILS_TEXT;
    }

    @Override
    public String getErrorMessage() {
        if (this.payload != null) {
            return String.format(Locale.ROOT, "Realms service error (%d) with message '%s'", this.httpCode, this.payload.getString());
        }
        return String.format(Locale.ROOT, "Realms service error (%d) with no payload", this.httpCode);
    }
}
