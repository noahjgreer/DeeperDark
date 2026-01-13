/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlDebug;

@Environment(value=EnvType.CLIENT)
static class GlDebug.DebugMessage {
    private final int id;
    private final int source;
    private final int type;
    private final int severity;
    private final String message;
    int count = 1;

    GlDebug.DebugMessage(int source, int type, int id, int severity, String message) {
        this.id = id;
        this.source = source;
        this.type = type;
        this.severity = severity;
        this.message = message;
    }

    boolean equals(int source, int type, int id, int severity, String message) {
        return type == this.type && source == this.source && id == this.id && severity == this.severity && message.equals(this.message);
    }

    public String toString() {
        return "id=" + this.id + ", source=" + GlDebug.getSource(this.source) + ", type=" + GlDebug.getType(this.type) + ", severity=" + GlDebug.getSeverity(this.severity) + ", message='" + this.message + "'";
    }
}
