/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.util;

import com.google.gson.annotations.SerializedName;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class RealmsPersistence.RealmsPersistenceData
implements RealmsSerializable {
    @SerializedName(value="newsLink")
    public @Nullable String newsLink;
    @SerializedName(value="hasUnreadNews")
    public boolean hasUnreadNews;
}
