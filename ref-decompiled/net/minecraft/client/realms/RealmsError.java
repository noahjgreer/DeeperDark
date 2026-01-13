/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.RealmsError
 *  net.minecraft.client.realms.RealmsError$DetailedHttpError
 *  net.minecraft.client.realms.RealmsError$RawHttpPayloadError
 *  net.minecraft.client.realms.RealmsError$SimpleHttpError
 *  net.minecraft.text.Text
 *  net.minecraft.util.JsonHelper
 *  net.minecraft.util.LenientJsonParser
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsError;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public interface RealmsError {
    public static final Text NO_DETAILS_TEXT = Text.translatable((String)"mco.errorMessage.noDetails");
    public static final Logger LOGGER = LogUtils.getLogger();

    public int getErrorCode();

    public Text getText();

    public String getErrorMessage();

    public static RealmsError ofHttp(int statusCode, String response) {
        if (statusCode == 429) {
            return SimpleHttpError.SERVICE_BUSY;
        }
        if (Strings.isNullOrEmpty((String)response)) {
            return SimpleHttpError.statusCodeOnly((int)statusCode);
        }
        try {
            JsonObject jsonObject = LenientJsonParser.parse((String)response).getAsJsonObject();
            String string = JsonHelper.getString((JsonObject)jsonObject, (String)"reason", null);
            String string2 = JsonHelper.getString((JsonObject)jsonObject, (String)"errorMsg", null);
            int i = JsonHelper.getInt((JsonObject)jsonObject, (String)"errorCode", (int)-1);
            if (string2 != null || string != null || i != -1) {
                return new DetailedHttpError(statusCode, i != -1 ? i : statusCode, string, string2);
            }
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse RealmsError", (Throwable)exception);
        }
        return new RawHttpPayloadError(statusCode, response);
    }
}

