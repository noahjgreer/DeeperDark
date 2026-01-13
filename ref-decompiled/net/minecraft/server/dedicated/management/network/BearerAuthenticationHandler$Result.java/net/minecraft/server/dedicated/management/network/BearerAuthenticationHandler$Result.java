/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.dedicated.management.network;

static class BearerAuthenticationHandler.Result {
    private final boolean successful;
    private final String message;
    private final boolean mustReturnProtocol;

    private BearerAuthenticationHandler.Result(boolean successful, String message, boolean mustReturnProtocol) {
        this.successful = successful;
        this.message = message;
        this.mustReturnProtocol = mustReturnProtocol;
    }

    public static BearerAuthenticationHandler.Result success() {
        return new BearerAuthenticationHandler.Result(true, null, false);
    }

    public static BearerAuthenticationHandler.Result success(boolean mustReturnProtocol) {
        return new BearerAuthenticationHandler.Result(true, null, mustReturnProtocol);
    }

    public static BearerAuthenticationHandler.Result failure(String message) {
        return new BearerAuthenticationHandler.Result(false, message, false);
    }

    public boolean isSuccessful() {
        return this.successful;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean mustReturnProtocol() {
        return this.mustReturnProtocol;
    }
}
