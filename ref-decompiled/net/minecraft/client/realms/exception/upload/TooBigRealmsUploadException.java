/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.SizeUnit
 *  net.minecraft.client.realms.exception.RealmsUploadException
 *  net.minecraft.client.realms.exception.upload.TooBigRealmsUploadException
 *  net.minecraft.text.Text
 */
package net.minecraft.client.realms.exception.upload;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.SizeUnit;
import net.minecraft.client.realms.exception.RealmsUploadException;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class TooBigRealmsUploadException
extends RealmsUploadException {
    final long maxSizeInBytes;

    public TooBigRealmsUploadException(long maxSizeInBytes) {
        this.maxSizeInBytes = maxSizeInBytes;
    }

    public Text[] getStatusTexts() {
        return new Text[]{Text.translatable((String)"mco.upload.failed.too_big.title"), Text.translatable((String)"mco.upload.failed.too_big.description", (Object[])new Object[]{SizeUnit.humanReadableSize((long)this.maxSizeInBytes, (SizeUnit)SizeUnit.getLargestUnit((long)this.maxSizeInBytes))})};
    }
}

