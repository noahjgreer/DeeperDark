package net.noahsarch.deeperdark.autoupdate;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.noahsarch.deeperdark.DeeperDarkConfig;

@Environment(EnvType.CLIENT)
public class AutoUpdaterScreen extends Screen {

    private enum State {
        CHECKING,
        UPDATE_AVAILABLE,
        DOWNLOADING,
        SUCCESS,
        FAILED,
        ERROR
    }

    private volatile State state = State.CHECKING;
    private State prevState = null;

    private volatile String errorMessage = null;
    private volatile AutoUpdater.UpdateInfo updateInfo = null;
    private volatile long downloadedBytes = 0;
    private volatile long totalBytes = 1;

    private boolean checkStarted = false;
    private int tickCounter = 0;

    public AutoUpdaterScreen() {
        super(Component.translatable("autoUpdater.title"));
    }

    // ===== Lifecycle =====

    @Override
    protected void init() {
        addWidgetsForState();
        if (!checkStarted) {
            checkStarted = true;
            beginUpdateCheck();
        }
    }

    @Override
    public void tick() {
        super.tick();
        tickCounter++;
        // Detect state changes from background threads; rebuild widgets on the main thread
        if (state != prevState) {
            prevState = state;
            clearWidgets();
            addWidgetsForState();
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    // ===== Widget construction =====

    private void addWidgetsForState() {
        DeeperDarkConfig.AutoUpdaterConfig cfg = getConfig();
        int cx = this.width / 2;
        int cy = this.height / 2;

        switch (state) {
            case UPDATE_AVAILABLE -> {
                Button installBtn = Button.builder(
                    Component.translatable("autoUpdater.update.confirm"),
                    btn -> startDownload()
                ).bounds(cx - 205, cy + 44, 200, 20).build();
                this.addRenderableWidget(installBtn);

                Button declineBtn = Button.builder(
                    Component.translatable("autoUpdater.update.decline"),
                    btn -> goToTitleScreen()
                ).bounds(cx + 5, cy + 44, 200, 20).build();
                declineBtn.active = cfg.canDenyUpdate;
                this.addRenderableWidget(declineBtn);
            }

            case SUCCESS -> {
                Button restartBtn = Button.builder(
                    Component.translatable("autoUpdater.install.option.restart"),
                    btn -> this.minecraft.stop()
                ).bounds(cx - 205, cy + 50, 200, 20).build();
                this.addRenderableWidget(restartBtn);

                Button laterBtn = Button.builder(
                    Component.translatable("autoUpdater.install.option.restartLater"),
                    btn -> goToTitleScreen()
                ).bounds(cx + 5, cy + 50, 200, 20).build();
                laterBtn.active = cfg.canRestartLater;
                this.addRenderableWidget(laterBtn);
            }

            case FAILED -> {
                Button continueBtn = Button.builder(
                    Component.translatable("autoUpdater.install.option.fail.continue"),
                    btn -> goToTitleScreen()
                ).bounds(cx - 205, cy + 50, 200, 20).build();
                this.addRenderableWidget(continueBtn);

                Button exitBtn = Button.builder(
                    Component.translatable("autoUpdater.install.option.fail.exit"),
                    btn -> this.minecraft.stop()
                ).bounds(cx + 5, cy + 50, 200, 20).build();
                this.addRenderableWidget(exitBtn);
            }

            case ERROR -> {
                Button continueBtn = Button.builder(
                    Component.translatable("autoUpdater.install.option.fail.continue"),
                    btn -> goToTitleScreen()
                ).bounds(cx - 100, cy + 40, 200, 20).build();
                this.addRenderableWidget(continueBtn);
            }

            default -> { /* CHECKING, DOWNLOADING — no buttons */ }
        }
    }

    // ===== Rendering =====

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        this.extractMenuBackground(g);

        int cx = this.width / 2;
        int cy = this.height / 2;

        // Title
        g.centeredText(this.font, this.title, cx, cy - 75, 0xFFFFFFFF);

        // Separator line
        g.fill(cx - 150, cy - 62, cx + 150, cy - 61, 0xFF555555);

        switch (state) {
            case CHECKING      -> renderChecking(g, cx, cy);
            case UPDATE_AVAILABLE -> renderUpdateAvailable(g, cx, cy);
            case DOWNLOADING   -> renderDownloading(g, cx, cy);
            case SUCCESS       -> renderMultiline(g, cx, cy,
                                    Component.translatable("autoUpdater.install.success").getString(),
                                    0xFF55FF55);
            case FAILED        -> renderMultiline(g, cx, cy,
                                    Component.translatable("autoUpdater.install.fail").getString(),
                                    0xFFFF5555);
            case ERROR         -> renderError(g, cx, cy);
        }

        // Render buttons / children
        super.extractRenderState(g, mouseX, mouseY, partialTick);
    }

    private void renderChecking(GuiGraphicsExtractor g, int cx, int cy) {
        int dots = (tickCounter / 12) % 4;
        String text = Component.translatable("autoUpdater.update.check").getString() + ".".repeat(dots);
        g.centeredText(this.font, text, cx, cy - 4, 0xFFAAAAAA);
    }

    private void renderUpdateAvailable(GuiGraphicsExtractor g, int cx, int cy) {
        String current = AutoUpdater.getCurrentVersion();
        String latest  = updateInfo != null ? updateInfo.latestVersion() : "?";

        g.centeredText(this.font,
            Component.translatable("autoUpdater.update.available.l1"), cx, cy - 28, 0xFFFFFFFF);
        g.centeredText(this.font,
            Component.translatable("autoUpdater.update.available.l2", current), cx, cy - 12, 0xFFAAAAAA);
        g.centeredText(this.font,
            Component.translatable("autoUpdater.update.available.l3", latest), cx, cy + 4, 0xFF55FF55);
        g.centeredText(this.font,
            Component.translatable("autoUpdater.update.available.l4"), cx, cy + 20, 0xFFFFFFFF);
    }

    private void renderDownloading(GuiGraphicsExtractor g, int cx, int cy) {
        g.centeredText(this.font,
            Component.translatable("autoUpdater.install.splash"), cx, cy - 28, 0xFFFFFFFF);

        // Progress bar
        int barW = 300, barH = 14;
        int barX = cx - barW / 2;
        int barY = cy - 6;
        float progress = totalBytes > 0 ? Math.min(1f, (float) downloadedBytes / totalBytes) : 0f;

        g.fill(barX - 1, barY - 1, barX + barW + 1, barY + barH + 1, 0xFF666666); // border
        g.fill(barX, barY, barX + barW, barY + barH, 0xFF1A1A1A);                  // background
        g.fill(barX, barY, barX + (int)(barW * progress), barY + barH, 0xFF00AA00); // fill

        String mbText = String.format("%.2f MB / %.2f MB",
            downloadedBytes / 1_048_576.0,
            Math.max(totalBytes, downloadedBytes) / 1_048_576.0);
        g.centeredText(this.font, mbText, cx, cy + 16, 0xFFAAAAAA);
    }

    private void renderMultiline(GuiGraphicsExtractor g, int cx, int cy, String message, int color) {
        String[] lines = message.split("\n");
        int nonBlank = 0;
        for (String l : lines) if (!l.isBlank()) nonBlank++;
        int y = cy - (nonBlank * 12) / 2;
        for (String line : lines) {
            if (!line.isBlank()) {
                g.centeredText(this.font, line, cx, y, color);
                y += 12;
            }
        }
    }

    private void renderError(GuiGraphicsExtractor g, int cx, int cy) {
        g.centeredText(this.font,
            Component.translatable("autoUpdater.error"), cx, cy - 20, 0xFFFF5555);
        if (errorMessage != null) {
            String err = errorMessage.length() > 64
                ? errorMessage.substring(0, 61) + "..."
                : errorMessage;
            g.centeredText(this.font, err, cx, cy - 4, 0xFFAAAAAA);
        }
    }

    // ===== Background workers =====

    private void beginUpdateCheck() {
        String repoUrl = getConfig().repoURL;
        Thread.ofVirtual().name("deeperdark-updatecheck").start(() -> {
            try {
                java.util.Optional<AutoUpdater.UpdateInfo> result = AutoUpdater.checkForUpdate(repoUrl);
                if (result.isPresent()) {
                    updateInfo = result.get();
                    state = State.UPDATE_AVAILABLE;
                } else {
                    // Already up to date — go straight to title screen
                    if (minecraft != null) minecraft.execute(this::goToTitleScreen);
                }
            } catch (Exception e) {
                errorMessage = e.getMessage();
                state = State.ERROR;
            }
        });
    }

    private void startDownload() {
        state = State.DOWNLOADING;
        AutoUpdater.UpdateInfo info = updateInfo;
        Thread.ofVirtual().name("deeperdark-download").start(() -> {
            try {
                AutoUpdater.downloadUpdate(info, (dl, total) -> {
                    downloadedBytes = dl;
                    totalBytes = total;
                });
                state = State.SUCCESS;
            } catch (Exception e) {
                errorMessage = e.getMessage();
                state = State.FAILED;
            }
        });
    }

    // ===== Helpers =====

    private void goToTitleScreen() {
        this.minecraft.setScreen(new TitleScreen());
    }

    private static DeeperDarkConfig.AutoUpdaterConfig getConfig() {
        DeeperDarkConfig.AutoUpdaterConfig cfg = DeeperDarkConfig.get().autoUpdater;
        return cfg != null ? cfg : new DeeperDarkConfig.AutoUpdaterConfig();
    }
}
