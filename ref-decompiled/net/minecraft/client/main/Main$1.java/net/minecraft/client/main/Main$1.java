/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.main;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static class Main.1
extends Authenticator {
    final /* synthetic */ String field_12139;
    final /* synthetic */ String field_12140;

    Main.1(String string, String string2) {
        this.field_12139 = string;
        this.field_12140 = string2;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(this.field_12139, this.field_12140.toCharArray());
    }
}
