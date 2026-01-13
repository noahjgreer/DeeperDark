/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.server.filter;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;

@FunctionalInterface
protected static interface AbstractTextFilterer.MessageEncoder {
    public JsonObject encode(GameProfile var1, String var2);
}
