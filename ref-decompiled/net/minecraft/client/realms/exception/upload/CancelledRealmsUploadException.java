/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.exception.RealmsUploadException
 *  net.minecraft.client.realms.exception.upload.CancelledRealmsUploadException
 *  net.minecraft.text.Text
 */
package net.minecraft.client.realms.exception.upload;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.exception.RealmsUploadException;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class CancelledRealmsUploadException
extends RealmsUploadException {
    private static final Text STATUS_TEXT = Text.translatable((String)"mco.upload.cancelled");

    public Text getStatus() {
        return STATUS_TEXT;
    }
}

