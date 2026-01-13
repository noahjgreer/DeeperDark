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
public record RealmsError.AuthenticationError(String message) implements RealmsError
{
    public static final int ERROR_CODE = 401;

    @Override
    public int getErrorCode() {
        return 401;
    }

    @Override
    public Text getText() {
        return Text.literal(this.message);
    }

    @Override
    public String getErrorMessage() {
        return String.format(Locale.ROOT, "Realms authentication error with message '%s'", this.message);
    }
}
