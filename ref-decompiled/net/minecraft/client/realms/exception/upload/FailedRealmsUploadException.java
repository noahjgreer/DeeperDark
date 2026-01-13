/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.exception.RealmsUploadException
 *  net.minecraft.client.realms.exception.upload.FailedRealmsUploadException
 *  net.minecraft.text.Text
 */
package net.minecraft.client.realms.exception.upload;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.exception.RealmsUploadException;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class FailedRealmsUploadException
extends RealmsUploadException {
    private final Text errorMessage;

    public FailedRealmsUploadException(Text errorMessage) {
        this.errorMessage = errorMessage;
    }

    public FailedRealmsUploadException(String errorMessage) {
        this((Text)Text.literal((String)errorMessage));
    }

    public Text getStatus() {
        return Text.translatable((String)"mco.upload.failed", (Object[])new Object[]{this.errorMessage});
    }
}

