/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsError;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
static final class RealmsGenericErrorScreen.ErrorMessages
extends Record {
    final Text title;
    final Text detail;

    RealmsGenericErrorScreen.ErrorMessages(Text title, Text detail) {
        this.title = title;
        this.detail = detail;
    }

    static RealmsGenericErrorScreen.ErrorMessages of(RealmsServiceException exception) {
        RealmsError realmsError = exception.error;
        return new RealmsGenericErrorScreen.ErrorMessages(Text.translatable("mco.errorMessage.realmsService.realmsError", realmsError.getErrorCode()), realmsError.getText());
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{RealmsGenericErrorScreen.ErrorMessages.class, "title;detail", "title", "detail"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RealmsGenericErrorScreen.ErrorMessages.class, "title;detail", "title", "detail"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RealmsGenericErrorScreen.ErrorMessages.class, "title;detail", "title", "detail"}, this, object);
    }

    public Text title() {
        return this.title;
    }

    public Text detail() {
        return this.detail;
    }
}
