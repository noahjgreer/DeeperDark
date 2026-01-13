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
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record RealmsError.DetailedHttpError(int httpCode, int code, @Nullable String reason, @Nullable String message) implements RealmsError
{
    @Override
    public int getErrorCode() {
        return this.code;
    }

    @Override
    public Text getText() {
        String string2;
        String string = "mco.errorMessage." + this.code;
        if (I18n.hasTranslation(string)) {
            return Text.translatable(string);
        }
        if (this.reason != null && I18n.hasTranslation(string2 = "mco.errorReason." + this.reason)) {
            return Text.translatable(string2);
        }
        return this.message != null ? Text.literal(this.message) : NO_DETAILS_TEXT;
    }

    @Override
    public String getErrorMessage() {
        return String.format(Locale.ROOT, "Realms service error (%d/%d/%s) with message '%s'", this.httpCode, this.code, this.reason, this.message);
    }
}
