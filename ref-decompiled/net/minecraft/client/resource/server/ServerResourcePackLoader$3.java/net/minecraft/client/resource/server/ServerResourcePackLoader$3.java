/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.resource.server;

import java.util.OptionalLong;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.SizeUnit;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.NetworkUtils;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class ServerResourcePackLoader.3
implements NetworkUtils.DownloadListener {
    private final SystemToast.Type toastType = new SystemToast.Type();
    private Text toastTitle = Text.empty();
    private @Nullable Text toastDescription = null;
    private int current;
    private int failureCount;
    private OptionalLong contentLength = OptionalLong.empty();
    final /* synthetic */ int field_47602;

    ServerResourcePackLoader.3() {
        this.field_47602 = i;
    }

    private void showToast() {
        ServerResourcePackLoader.this.client.execute(() -> SystemToast.show(ServerResourcePackLoader.this.client.getToastManager(), this.toastType, this.toastTitle, this.toastDescription));
    }

    private void showProgress(long writtenBytes) {
        this.toastDescription = this.contentLength.isPresent() ? Text.translatable("download.pack.progress.percent", writtenBytes * 100L / this.contentLength.getAsLong()) : Text.translatable("download.pack.progress.bytes", SizeUnit.getUserFriendlyString(writtenBytes));
        this.showToast();
    }

    @Override
    public void onStart() {
        ++this.current;
        this.toastTitle = Text.translatable("download.pack.title", this.current, this.field_47602);
        this.showToast();
        LOGGER.debug("Starting pack {}/{} download", (Object)this.current, (Object)this.field_47602);
    }

    @Override
    public void onContentLength(OptionalLong contentLength) {
        LOGGER.debug("File size = {} bytes", (Object)contentLength);
        this.contentLength = contentLength;
        this.showProgress(0L);
    }

    @Override
    public void onProgress(long writtenBytes) {
        LOGGER.debug("Progress for pack {}: {} bytes", (Object)this.current, (Object)writtenBytes);
        this.showProgress(writtenBytes);
    }

    @Override
    public void onFinish(boolean success) {
        if (!success) {
            LOGGER.info("Pack {} failed to download", (Object)this.current);
            ++this.failureCount;
        } else {
            LOGGER.debug("Download ended for pack {}", (Object)this.current);
        }
        if (this.current == this.field_47602) {
            if (this.failureCount > 0) {
                this.toastTitle = Text.translatable("download.pack.failed", this.failureCount, this.field_47602);
                this.toastDescription = null;
                this.showToast();
            } else {
                SystemToast.hide(ServerResourcePackLoader.this.client.getToastManager(), this.toastType);
            }
        }
    }
}
