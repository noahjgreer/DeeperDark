/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 */
package net.minecraft.server.command;

import java.io.PrintWriter;
import java.io.Writer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.function.Tracer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

static class DebugCommand.Tracer
implements CommandOutput,
Tracer {
    public static final int MARGIN = 1;
    private final PrintWriter writer;
    private int lastIndentWidth;
    private boolean expectsCommandResult;

    DebugCommand.Tracer(PrintWriter writer) {
        this.writer = writer;
    }

    private void writeIndent(int width) {
        this.writeIndentWithoutRememberingWidth(width);
        this.lastIndentWidth = width;
    }

    private void writeIndentWithoutRememberingWidth(int width) {
        for (int i = 0; i < width + 1; ++i) {
            this.writer.write("    ");
        }
    }

    private void writeNewLine() {
        if (this.expectsCommandResult) {
            this.writer.println();
            this.expectsCommandResult = false;
        }
    }

    @Override
    public void traceCommandStart(int depth, String command) {
        this.writeNewLine();
        this.writeIndent(depth);
        this.writer.print("[C] ");
        this.writer.print(command);
        this.expectsCommandResult = true;
    }

    @Override
    public void traceCommandEnd(int depth, String command, int result) {
        if (this.expectsCommandResult) {
            this.writer.print(" -> ");
            this.writer.println(result);
            this.expectsCommandResult = false;
        } else {
            this.writeIndent(depth);
            this.writer.print("[R = ");
            this.writer.print(result);
            this.writer.print("] ");
            this.writer.println(command);
        }
    }

    @Override
    public void traceFunctionCall(int depth, Identifier function, int size) {
        this.writeNewLine();
        this.writeIndent(depth);
        this.writer.print("[F] ");
        this.writer.print(function);
        this.writer.print(" size=");
        this.writer.println(size);
    }

    @Override
    public void traceError(String message) {
        this.writeNewLine();
        this.writeIndent(this.lastIndentWidth + 1);
        this.writer.print("[E] ");
        this.writer.print(message);
    }

    @Override
    public void sendMessage(Text message) {
        this.writeNewLine();
        this.writeIndentWithoutRememberingWidth(this.lastIndentWidth + 1);
        this.writer.print("[M] ");
        this.writer.println(message.getString());
    }

    @Override
    public boolean shouldReceiveFeedback() {
        return true;
    }

    @Override
    public boolean shouldTrackOutput() {
        return true;
    }

    @Override
    public boolean shouldBroadcastConsoleToOps() {
        return false;
    }

    @Override
    public boolean cannotBeSilenced() {
        return true;
    }

    @Override
    public void close() {
        IOUtils.closeQuietly((Writer)this.writer);
    }
}
