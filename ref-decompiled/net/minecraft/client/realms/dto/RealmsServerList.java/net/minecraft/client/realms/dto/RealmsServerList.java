/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.CheckedGson;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.dto.RealmsServer;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public record RealmsServerList(@SerializedName(value="servers") List<RealmsServer> servers) implements RealmsSerializable
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public static RealmsServerList parse(CheckedGson gson, String json) {
        try {
            RealmsServerList realmsServerList = gson.fromJson(json, RealmsServerList.class);
            if (realmsServerList != null) {
                realmsServerList.servers.forEach(RealmsServer::replaceNullsWithDefaults);
                return realmsServerList;
            }
            LOGGER.error("Could not parse McoServerList: {}", (Object)json);
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse McoServerList", (Throwable)exception);
        }
        return new RealmsServerList(List.of());
    }
}
