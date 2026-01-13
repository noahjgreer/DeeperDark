/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.RealmsClientConfig
 *  net.minecraft.client.realms.Request
 *  net.minecraft.client.realms.Request$Delete
 *  net.minecraft.client.realms.Request$Get
 *  net.minecraft.client.realms.Request$Post
 *  net.minecraft.client.realms.Request$Put
 *  net.minecraft.client.realms.exception.RealmsHttpException
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsClientConfig;
import net.minecraft.client.realms.Request;
import net.minecraft.client.realms.exception.RealmsHttpException;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public abstract class Request<T extends Request<T>> {
    protected HttpURLConnection connection;
    private boolean connected;
    protected String url;
    private static final int READ_TIMEOUT = 60000;
    private static final int CONNECT_TIMEOUT = 5000;
    private static final String IS_PRERELEASE_HEADER = "Is-Prerelease";
    private static final String COOKIE_HEADER = "Cookie";

    public Request(String url, int connectTimeout, int readTimeout) {
        try {
            this.url = url;
            Proxy proxy = RealmsClientConfig.getProxy();
            this.connection = proxy != null ? (HttpURLConnection)new URL(url).openConnection(proxy) : (HttpURLConnection)new URL(url).openConnection();
            this.connection.setConnectTimeout(connectTimeout);
            this.connection.setReadTimeout(readTimeout);
        }
        catch (MalformedURLException malformedURLException) {
            throw new RealmsHttpException(malformedURLException.getMessage(), (Exception)malformedURLException);
        }
        catch (IOException iOException) {
            throw new RealmsHttpException(iOException.getMessage(), (Exception)iOException);
        }
    }

    public void cookie(String key, String value) {
        Request.cookie((HttpURLConnection)this.connection, (String)key, (String)value);
    }

    public static void cookie(HttpURLConnection connection, String key, String value) {
        String string = connection.getRequestProperty("Cookie");
        if (string == null) {
            connection.setRequestProperty("Cookie", key + "=" + value);
        } else {
            connection.setRequestProperty("Cookie", string + ";" + key + "=" + value);
        }
    }

    public void prerelease(boolean prerelease) {
        this.connection.addRequestProperty("Is-Prerelease", String.valueOf(prerelease));
    }

    public int getRetryAfterHeader() {
        return Request.getRetryAfterHeader((HttpURLConnection)this.connection);
    }

    public static int getRetryAfterHeader(HttpURLConnection connection) {
        String string = connection.getHeaderField("Retry-After");
        try {
            return Integer.valueOf(string);
        }
        catch (Exception exception) {
            return 5;
        }
    }

    public int responseCode() {
        try {
            this.connect();
            return this.connection.getResponseCode();
        }
        catch (Exception exception) {
            throw new RealmsHttpException(exception.getMessage(), exception);
        }
    }

    public String text() {
        try {
            this.connect();
            String string = this.responseCode() >= 400 ? this.read(this.connection.getErrorStream()) : this.read(this.connection.getInputStream());
            this.dispose();
            return string;
        }
        catch (IOException iOException) {
            throw new RealmsHttpException(iOException.getMessage(), (Exception)iOException);
        }
    }

    private String read(@Nullable InputStream in) throws IOException {
        if (in == null) {
            return "";
        }
        InputStreamReader inputStreamReader = new InputStreamReader(in, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        int i = inputStreamReader.read();
        while (i != -1) {
            stringBuilder.append((char)i);
            i = inputStreamReader.read();
        }
        return stringBuilder.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void dispose() {
        byte[] bs = new byte[1024];
        try {
            InputStream inputStream = this.connection.getInputStream();
            while (inputStream.read(bs) > 0) {
            }
            inputStream.close();
        }
        catch (Exception exception) {
            InputStream inputStream2;
            block13: {
                inputStream2 = this.connection.getErrorStream();
                if (inputStream2 != null) break block13;
                return;
            }
            try {
                while (inputStream2.read(bs) > 0) {
                }
                inputStream2.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        finally {
            if (this.connection != null) {
                this.connection.disconnect();
            }
        }
    }

    protected T connect() {
        if (this.connected) {
            return (T)this;
        }
        Request request = this.doConnect();
        this.connected = true;
        return (T)request;
    }

    protected abstract T doConnect();

    public static Request<?> get(String url) {
        return new Get(url, 5000, 60000);
    }

    public static Request<?> get(String url, int connectTimeoutMillis, int readTimeoutMillis) {
        return new Get(url, connectTimeoutMillis, readTimeoutMillis);
    }

    public static Request<?> post(String uri, String content) {
        return new Post(uri, content, 5000, 60000);
    }

    public static Request<?> post(String uri, String content, int connectTimeoutMillis, int readTimeoutMillis) {
        return new Post(uri, content, connectTimeoutMillis, readTimeoutMillis);
    }

    public static Request<?> delete(String url) {
        return new Delete(url, 5000, 60000);
    }

    public static Request<?> put(String url, String content) {
        return new Put(url, content, 5000, 60000);
    }

    public static Request<?> put(String url, String content, int connectTimeoutMillis, int readTimeoutMillis) {
        return new Put(url, content, connectTimeoutMillis, readTimeoutMillis);
    }

    public String getHeader(String header) {
        return Request.getHeader((HttpURLConnection)this.connection, (String)header);
    }

    public static String getHeader(HttpURLConnection connection, String header) {
        try {
            return connection.getHeaderField(header);
        }
        catch (Exception exception) {
            return "";
        }
    }
}

