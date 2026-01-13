/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.dto.RealmsNotification
 *  net.minecraft.client.realms.dto.RealmsNotification$InfoPopup
 *  net.minecraft.client.realms.dto.RealmsNotification$VisitUrl
 *  net.minecraft.client.realms.util.JsonUtils
 *  net.minecraft.text.Text
 *  net.minecraft.util.LenientJsonParser
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.RealmsNotification;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.text.Text;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class RealmsNotification {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final String NOTIFICATION_UUID_KEY = "notificationUuid";
    private static final String DISMISSABLE_KEY = "dismissable";
    private static final String SEEN_KEY = "seen";
    private static final String TYPE_KEY = "type";
    private static final String VISIT_URL_TYPE = "visitUrl";
    private static final String INFO_POPUP_TYPE = "infoPopup";
    static final Text OPEN_LINK_TEXT = Text.translatable((String)"mco.notification.visitUrl.buttonText.default");
    final UUID uuid;
    final boolean dismissable;
    final boolean seen;
    final String type;

    RealmsNotification(UUID uuid, boolean dismissable, boolean seen, String type) {
        this.uuid = uuid;
        this.dismissable = dismissable;
        this.seen = seen;
        this.type = type;
    }

    public boolean isSeen() {
        return this.seen;
    }

    public boolean isDismissable() {
        return this.dismissable;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public static List<RealmsNotification> parse(String json) {
        ArrayList<RealmsNotification> list = new ArrayList<RealmsNotification>();
        try {
            JsonArray jsonArray = LenientJsonParser.parse((String)json).getAsJsonObject().get("notifications").getAsJsonArray();
            for (JsonElement jsonElement : jsonArray) {
                list.add(RealmsNotification.fromJson((JsonObject)jsonElement.getAsJsonObject()));
            }
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse list of RealmsNotifications", (Throwable)exception);
        }
        return list;
    }

    private static RealmsNotification fromJson(JsonObject json) {
        UUID uUID = JsonUtils.getUuidOr((String)"notificationUuid", (JsonObject)json, null);
        if (uUID == null) {
            throw new IllegalStateException("Missing required property notificationUuid");
        }
        boolean bl = JsonUtils.getBooleanOr((String)"dismissable", (JsonObject)json, (boolean)true);
        boolean bl2 = JsonUtils.getBooleanOr((String)"seen", (JsonObject)json, (boolean)false);
        String string = JsonUtils.getString((String)"type", (JsonObject)json);
        RealmsNotification realmsNotification = new RealmsNotification(uUID, bl, bl2, string);
        return switch (string) {
            case "visitUrl" -> VisitUrl.fromJson((RealmsNotification)realmsNotification, (JsonObject)json);
            case "infoPopup" -> InfoPopup.fromJson((RealmsNotification)realmsNotification, (JsonObject)json);
            default -> realmsNotification;
        };
    }
}

