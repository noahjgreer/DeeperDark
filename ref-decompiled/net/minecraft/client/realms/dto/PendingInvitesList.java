/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.dto.PendingInvite
 *  net.minecraft.client.realms.dto.PendingInvitesList
 *  net.minecraft.util.LenientJsonParser
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.PendingInvite;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public record PendingInvitesList(List<PendingInvite> pendingInvites) {
    private final List<PendingInvite> pendingInvites;
    private static final Logger LOGGER = LogUtils.getLogger();

    public PendingInvitesList(List<PendingInvite> pendingInvites) {
        this.pendingInvites = pendingInvites;
    }

    public static PendingInvitesList parse(String json) {
        ArrayList<PendingInvite> list = new ArrayList<PendingInvite>();
        try {
            JsonObject jsonObject = LenientJsonParser.parse((String)json).getAsJsonObject();
            if (jsonObject.get("invites").isJsonArray()) {
                for (JsonElement jsonElement : jsonObject.get("invites").getAsJsonArray()) {
                    PendingInvite pendingInvite = PendingInvite.parse((JsonObject)jsonElement.getAsJsonObject());
                    if (pendingInvite == null) continue;
                    list.add(pendingInvite);
                }
            }
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse PendingInvitesList", (Throwable)exception);
        }
        return new PendingInvitesList(list);
    }

    public List<PendingInvite> pendingInvites() {
        return this.pendingInvites;
    }
}

