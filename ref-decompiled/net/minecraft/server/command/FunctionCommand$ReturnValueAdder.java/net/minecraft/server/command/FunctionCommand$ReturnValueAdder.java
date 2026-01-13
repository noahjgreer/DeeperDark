/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.command;

class FunctionCommand.ReturnValueAdder {
    boolean successful;
    int returnValue;

    FunctionCommand.ReturnValueAdder() {
    }

    public void onSuccess(int returnValue) {
        this.successful = true;
        this.returnValue += returnValue;
    }
}
