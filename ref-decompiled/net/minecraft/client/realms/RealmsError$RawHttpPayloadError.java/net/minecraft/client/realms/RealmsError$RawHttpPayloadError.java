/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsError;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public record RealmsError.RawHttpPayloadError(int httpCode, String payload) implements RealmsError
{
    @Override
    public int getErrorCode() {
        return this.httpCode;
    }

    @Override
    public Text getText() {
        return Text.literal(this.payload);
    }

    @Override
    public String getErrorMessage() {
        return String.format(Locale.ROOT, "Realms service error (%d) with raw payload '%s'", this.httpCode, this.payload);
    }
}
