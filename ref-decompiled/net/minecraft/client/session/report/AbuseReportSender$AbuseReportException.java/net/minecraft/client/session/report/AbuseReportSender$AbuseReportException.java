/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.report;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.TextifiedException;

@Environment(value=EnvType.CLIENT)
public static class AbuseReportSender.AbuseReportException
extends TextifiedException {
    public AbuseReportSender.AbuseReportException(Text text, Throwable throwable) {
        super(text, throwable);
    }
}
