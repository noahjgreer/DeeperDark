/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

public static sealed class Util.OperatingSystem
extends Enum<Util.OperatingSystem> {
    public static final /* enum */ Util.OperatingSystem LINUX = new Util.OperatingSystem("linux");
    public static final /* enum */ Util.OperatingSystem SOLARIS = new Util.OperatingSystem("solaris");
    public static final /* enum */ Util.OperatingSystem WINDOWS = new Util.OperatingSystem("windows"){

        @Override
        protected String[] getURIOpenCommand(URI uri) {
            return new String[]{"rundll32", "url.dll,FileProtocolHandler", uri.toString()};
        }
    };
    public static final /* enum */ Util.OperatingSystem OSX = new Util.OperatingSystem("mac"){

        @Override
        protected String[] getURIOpenCommand(URI uri) {
            return new String[]{"open", uri.toString()};
        }
    };
    public static final /* enum */ Util.OperatingSystem UNKNOWN = new Util.OperatingSystem("unknown");
    private final String name;
    private static final /* synthetic */ Util.OperatingSystem[] field_1136;

    public static Util.OperatingSystem[] values() {
        return (Util.OperatingSystem[])field_1136.clone();
    }

    public static Util.OperatingSystem valueOf(String string) {
        return Enum.valueOf(Util.OperatingSystem.class, string);
    }

    Util.OperatingSystem(String name) {
        this.name = name;
    }

    public void open(URI uri) {
        try {
            Process process = Runtime.getRuntime().exec(this.getURIOpenCommand(uri));
            process.getInputStream().close();
            process.getErrorStream().close();
            process.getOutputStream().close();
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't open location '{}'", (Object)uri, (Object)iOException);
        }
    }

    public void open(File file) {
        this.open(file.toURI());
    }

    public void open(Path path) {
        this.open(path.toUri());
    }

    protected String[] getURIOpenCommand(URI uri) {
        String string = uri.toString();
        if ("file".equals(uri.getScheme())) {
            string = string.replace("file:", "file://");
        }
        return new String[]{"xdg-open", string};
    }

    public void open(String uri) {
        try {
            this.open(new URI(uri));
        }
        catch (IllegalArgumentException | URISyntaxException exception) {
            LOGGER.error("Couldn't open uri '{}'", (Object)uri, (Object)exception);
        }
    }

    public String getName() {
        return this.name;
    }

    private static /* synthetic */ Util.OperatingSystem[] method_36579() {
        return new Util.OperatingSystem[]{LINUX, SOLARIS, WINDOWS, OSX, UNKNOWN};
    }

    static {
        field_1136 = Util.OperatingSystem.method_36579();
    }
}
