/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.dto.UploadInfo
 *  net.minecraft.client.realms.util.JsonUtils
 *  net.minecraft.util.LenientJsonParser
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.util.LenientJsonParser;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public record UploadInfo(boolean worldClosed, @Nullable String token, URI uploadEndpoint) {
    private final boolean worldClosed;
    private final @Nullable String token;
    private final URI uploadEndpoint;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String HTTP_PROTOCOL = "http://";
    private static final int PORT = 8080;
    private static final Pattern PROTOCOL_PATTERN = Pattern.compile("^[a-zA-Z][-a-zA-Z0-9+.]+:");

    public UploadInfo(boolean worldClosed, @Nullable String token, URI uploadEndpoint) {
        this.worldClosed = worldClosed;
        this.token = token;
        this.uploadEndpoint = uploadEndpoint;
    }

    public static @Nullable UploadInfo parse(String json) {
        try {
            int i;
            URI uRI;
            JsonObject jsonObject = LenientJsonParser.parse((String)json).getAsJsonObject();
            String string = JsonUtils.getNullableStringOr((String)"uploadEndpoint", (JsonObject)jsonObject, null);
            if (string != null && (uRI = UploadInfo.getUrl((String)string, (int)(i = JsonUtils.getIntOr((String)"port", (JsonObject)jsonObject, (int)-1)))) != null) {
                boolean bl = JsonUtils.getBooleanOr((String)"worldClosed", (JsonObject)jsonObject, (boolean)false);
                String string2 = JsonUtils.getNullableStringOr((String)"token", (JsonObject)jsonObject, null);
                return new UploadInfo(bl, string2, uRI);
            }
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse UploadInfo", (Throwable)exception);
        }
        return null;
    }

    @VisibleForTesting
    public static @Nullable URI getUrl(String url, int port) {
        Matcher matcher = PROTOCOL_PATTERN.matcher(url);
        String string = UploadInfo.getUrlWithProtocol((String)url, (Matcher)matcher);
        try {
            URI uRI = new URI(string);
            int i = UploadInfo.getPort((int)port, (int)uRI.getPort());
            if (i != uRI.getPort()) {
                return new URI(uRI.getScheme(), uRI.getUserInfo(), uRI.getHost(), i, uRI.getPath(), uRI.getQuery(), uRI.getFragment());
            }
            return uRI;
        }
        catch (URISyntaxException uRISyntaxException) {
            LOGGER.warn("Failed to parse URI {}", (Object)string, (Object)uRISyntaxException);
            return null;
        }
    }

    private static int getPort(int port, int urlPort) {
        if (port != -1) {
            return port;
        }
        if (urlPort != -1) {
            return urlPort;
        }
        return 8080;
    }

    private static String getUrlWithProtocol(String url, Matcher matcher) {
        if (matcher.find()) {
            return url;
        }
        return "http://" + url;
    }

    public static String createRequestContent(@Nullable String token) {
        JsonObject jsonObject = new JsonObject();
        if (token != null) {
            jsonObject.addProperty("token", token);
        }
        return jsonObject.toString();
    }

    public boolean worldClosed() {
        return this.worldClosed;
    }

    public @Nullable String token() {
        return this.token;
    }

    public URI uploadEndpoint() {
        return this.uploadEndpoint;
    }
}

