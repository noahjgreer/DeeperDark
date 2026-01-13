/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.Mouse
 *  net.minecraft.client.Mouse$MouseClickTime
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.navigation.GuiNavigationType
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.input.MouseInput
 *  net.minecraft.client.input.MouseInput$ButtonCode
 *  net.minecraft.client.input.MouseInput$MouseAction
 *  net.minecraft.client.input.Scroller
 *  net.minecraft.client.input.SystemKeycodes
 *  net.minecraft.client.option.KeyBinding
 *  net.minecraft.client.toast.SystemToast
 *  net.minecraft.client.util.GlfwUtil
 *  net.minecraft.client.util.InputUtil
 *  net.minecraft.client.util.InputUtil$Key
 *  net.minecraft.client.util.InputUtil$Type
 *  net.minecraft.client.util.Window
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.util.Util
 *  net.minecraft.util.crash.CrashException
 *  net.minecraft.util.crash.CrashReport
 *  net.minecraft.util.crash.CrashReportSection
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Smoother
 *  org.joml.Vector2i
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.glfw.GLFWDropCallback
 *  org.slf4j.Logger
 */
package net.minecraft.client;

import com.mojang.logging.LogUtils;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.navigation.GuiNavigationType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.input.Scroller;
import net.minecraft.client.input.SystemKeycodes;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Smoother;
import org.joml.Vector2i;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFWDropCallback;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class Mouse {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final long field_61505 = 250L;
    private final MinecraftClient client;
    private boolean leftButtonClicked;
    private boolean middleButtonClicked;
    private boolean rightButtonClicked;
    private double x;
    private double y;
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable Mouse.MouseClickTime lastMouseClick;
    @MouseInput.ButtonCode
    protected int lastMouseButton;
    private int controlLeftClicks;
    private @Nullable MouseInput activeButton = null;
    private boolean hasResolutionChanged = true;
    private int touchHoldTime;
    private double glfwTime;
    private final Smoother cursorXSmoother = new Smoother();
    private final Smoother cursorYSmoother = new Smoother();
    private double cursorDeltaX;
    private double cursorDeltaY;
    private final Scroller scroller;
    private double lastTickTime = Double.MIN_VALUE;
    private boolean cursorLocked;

    public Mouse(MinecraftClient client) {
        this.client = client;
        this.scroller = new Scroller();
    }

    private void onMouseButton(long window, MouseInput input, @MouseInput.MouseAction int action) {
        MouseInput mouseInput;
        boolean bl;
        block25: {
            Window window2 = this.client.getWindow();
            if (window != window2.getHandle()) {
                return;
            }
            this.client.getInactivityFpsLimiter().onInput();
            if (this.client.currentScreen != null) {
                this.client.setNavigationType(GuiNavigationType.MOUSE);
            }
            bl = action == 1;
            mouseInput = this.modifyMouseInput(input, bl);
            if (bl) {
                if (((Boolean)this.client.options.getTouchscreen().getValue()).booleanValue() && this.touchHoldTime++ > 0) {
                    return;
                }
                this.activeButton = mouseInput;
                this.glfwTime = GlfwUtil.getTime();
            } else if (this.activeButton != null) {
                if (((Boolean)this.client.options.getTouchscreen().getValue()).booleanValue() && --this.touchHoldTime > 0) {
                    return;
                }
                this.activeButton = null;
            }
            if (this.client.getOverlay() == null) {
                if (this.client.currentScreen == null) {
                    if (!this.cursorLocked && bl) {
                        this.lockCursor();
                    }
                } else {
                    double d = this.getScaledX(window2);
                    double e = this.getScaledY(window2);
                    Screen screen = this.client.currentScreen;
                    Click click = new Click(d, e, mouseInput);
                    if (bl) {
                        screen.applyMousePressScrollNarratorDelay();
                        try {
                            boolean bl2;
                            long l = Util.getMeasuringTimeMs();
                            boolean bl3 = bl2 = this.lastMouseClick != null && l - this.lastMouseClick.time() < 250L && this.lastMouseClick.screen() == screen && this.lastMouseButton == click.button();
                            if (screen.mouseClicked(click, bl2)) {
                                this.lastMouseClick = new MouseClickTime(l, screen);
                                this.lastMouseButton = mouseInput.button();
                                return;
                            }
                            break block25;
                        }
                        catch (Throwable throwable) {
                            CrashReport crashReport = CrashReport.create((Throwable)throwable, (String)"mouseClicked event handler");
                            screen.addCrashReportSection(crashReport);
                            CrashReportSection crashReportSection = crashReport.addElement("Mouse");
                            this.addCrashReportSection(crashReportSection, window2);
                            crashReportSection.add("Button", (Object)click.button());
                            throw new CrashException(crashReport);
                        }
                    }
                    try {
                        if (screen.mouseReleased(click)) {
                            return;
                        }
                    }
                    catch (Throwable throwable) {
                        CrashReport crashReport = CrashReport.create((Throwable)throwable, (String)"mouseReleased event handler");
                        screen.addCrashReportSection(crashReport);
                        CrashReportSection crashReportSection = crashReport.addElement("Mouse");
                        this.addCrashReportSection(crashReportSection, window2);
                        crashReportSection.add("Button", (Object)click.button());
                        throw new CrashException(crashReport);
                    }
                }
            }
        }
        if (this.client.currentScreen == null && this.client.getOverlay() == null) {
            if (mouseInput.button() == 0) {
                this.leftButtonClicked = bl;
            } else if (mouseInput.button() == 2) {
                this.middleButtonClicked = bl;
            } else if (mouseInput.button() == 1) {
                this.rightButtonClicked = bl;
            }
            InputUtil.Key key = InputUtil.Type.MOUSE.createFromCode(mouseInput.button());
            KeyBinding.setKeyPressed((InputUtil.Key)key, (boolean)bl);
            if (bl) {
                KeyBinding.onKeyPressed((InputUtil.Key)key);
            }
        }
    }

    private MouseInput modifyMouseInput(MouseInput input, boolean pressed) {
        if (SystemKeycodes.USE_LONG_LEFT_PRESS && input.button() == 0) {
            if (pressed) {
                if ((input.modifiers() & 2) == 2) {
                    ++this.controlLeftClicks;
                    return new MouseInput(1, input.modifiers());
                }
            } else if (this.controlLeftClicks > 0) {
                --this.controlLeftClicks;
                return new MouseInput(1, input.modifiers());
            }
        }
        return input;
    }

    public void addCrashReportSection(CrashReportSection section, Window window) {
        section.add("Mouse location", () -> String.format(Locale.ROOT, "Scaled: (%f, %f). Absolute: (%f, %f)", Mouse.scaleX((Window)window, (double)this.x), Mouse.scaleY((Window)window, (double)this.y), this.x, this.y));
        section.add("Screen size", () -> String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d", window.getScaledWidth(), window.getScaledHeight(), window.getFramebufferWidth(), window.getFramebufferHeight(), window.getScaleFactor()));
    }

    private void onMouseScroll(long window, double horizontal, double vertical) {
        if (window == this.client.getWindow().getHandle()) {
            this.client.getInactivityFpsLimiter().onInput();
            boolean bl = (Boolean)this.client.options.getDiscreteMouseScroll().getValue();
            double d = (Double)this.client.options.getMouseWheelSensitivity().getValue();
            double e = (bl ? Math.signum(horizontal) : horizontal) * d;
            double f = (bl ? Math.signum(vertical) : vertical) * d;
            if (this.client.getOverlay() == null) {
                if (this.client.currentScreen != null) {
                    double g = this.getScaledX(this.client.getWindow());
                    double h = this.getScaledY(this.client.getWindow());
                    this.client.currentScreen.mouseScrolled(g, h, e, f);
                    this.client.currentScreen.applyMousePressScrollNarratorDelay();
                } else if (this.client.player != null) {
                    int i;
                    Vector2i vector2i = this.scroller.update(e, f);
                    if (vector2i.x == 0 && vector2i.y == 0) {
                        return;
                    }
                    int n = i = vector2i.y == 0 ? -vector2i.x : vector2i.y;
                    if (this.client.player.isSpectator()) {
                        if (this.client.inGameHud.getSpectatorHud().isOpen()) {
                            this.client.inGameHud.getSpectatorHud().cycleSlot(-i);
                        } else {
                            float j = MathHelper.clamp((float)(this.client.player.getAbilities().getFlySpeed() + (float)vector2i.y * 0.005f), (float)0.0f, (float)0.2f);
                            this.client.player.getAbilities().setFlySpeed(j);
                        }
                    } else {
                        PlayerInventory playerInventory = this.client.player.getInventory();
                        playerInventory.setSelectedSlot(Scroller.scrollCycling((double)i, (int)playerInventory.getSelectedSlot(), (int)PlayerInventory.getHotbarSize()));
                    }
                }
            }
        }
    }

    private void onFilesDropped(long window, List<Path> paths, int invalidFilesCount) {
        this.client.getInactivityFpsLimiter().onInput();
        if (this.client.currentScreen != null) {
            this.client.currentScreen.onFilesDropped(paths);
        }
        if (invalidFilesCount > 0) {
            SystemToast.addFileDropFailure((MinecraftClient)this.client, (int)invalidFilesCount);
        }
    }

    public void setup(Window window2) {
        InputUtil.setMouseCallbacks((Window)window2, (window, x, y) -> this.client.execute(() -> this.onCursorPos(window, x, y)), (window, button, action, modifiers) -> {
            MouseInput mouseInput = new MouseInput(button, modifiers);
            this.client.execute(() -> this.onMouseButton(window, mouseInput, action));
        }, (window, offsetX, offsetY) -> this.client.execute(() -> this.onMouseScroll(window, offsetX, offsetY)), (window, count, names) -> {
            int j;
            ArrayList<Path> list = new ArrayList<Path>(count);
            int i = 0;
            for (j = 0; j < count; ++j) {
                String string = GLFWDropCallback.getName((long)names, (int)j);
                try {
                    list.add(Paths.get(string, new String[0]));
                    continue;
                }
                catch (InvalidPathException invalidPathException) {
                    ++i;
                    LOGGER.error("Failed to parse path '{}'", (Object)string, (Object)invalidPathException);
                }
            }
            if (!list.isEmpty()) {
                j = i;
                this.client.execute(() -> this.onFilesDropped(window, list, j));
            }
        });
    }

    private void onCursorPos(long window, double x, double y) {
        if (window != this.client.getWindow().getHandle()) {
            return;
        }
        if (this.hasResolutionChanged) {
            this.x = x;
            this.y = y;
            this.hasResolutionChanged = false;
            return;
        }
        if (this.client.isWindowFocused()) {
            this.cursorDeltaX += x - this.x;
            this.cursorDeltaY += y - this.y;
        }
        this.x = x;
        this.y = y;
    }

    public void tick() {
        double d = GlfwUtil.getTime();
        double e = d - this.lastTickTime;
        this.lastTickTime = d;
        if (this.client.isWindowFocused()) {
            boolean bl;
            Screen screen = this.client.currentScreen;
            boolean bl2 = bl = this.cursorDeltaX != 0.0 || this.cursorDeltaY != 0.0;
            if (bl) {
                this.client.getInactivityFpsLimiter().onInput();
            }
            if (screen != null && this.client.getOverlay() == null && bl) {
                Window window = this.client.getWindow();
                double f = this.getScaledX(window);
                double g = this.getScaledY(window);
                try {
                    screen.mouseMoved(f, g);
                }
                catch (Throwable throwable) {
                    CrashReport crashReport = CrashReport.create((Throwable)throwable, (String)"mouseMoved event handler");
                    screen.addCrashReportSection(crashReport);
                    CrashReportSection crashReportSection = crashReport.addElement("Mouse");
                    this.addCrashReportSection(crashReportSection, window);
                    throw new CrashException(crashReport);
                }
                if (this.activeButton != null && this.glfwTime > 0.0) {
                    double h = Mouse.scaleX((Window)window, (double)this.cursorDeltaX);
                    double i = Mouse.scaleY((Window)window, (double)this.cursorDeltaY);
                    try {
                        screen.mouseDragged(new Click(f, g, this.activeButton), h, i);
                    }
                    catch (Throwable throwable2) {
                        CrashReport crashReport2 = CrashReport.create((Throwable)throwable2, (String)"mouseDragged event handler");
                        screen.addCrashReportSection(crashReport2);
                        CrashReportSection crashReportSection2 = crashReport2.addElement("Mouse");
                        this.addCrashReportSection(crashReportSection2, window);
                        throw new CrashException(crashReport2);
                    }
                }
                screen.applyMouseMoveNarratorDelay();
            }
            if (this.isCursorLocked() && this.client.player != null) {
                this.updateMouse(e);
            }
        }
        this.cursorDeltaX = 0.0;
        this.cursorDeltaY = 0.0;
    }

    public static double scaleX(Window window, double x) {
        return x * (double)window.getScaledWidth() / (double)window.getWidth();
    }

    public double getScaledX(Window window) {
        return Mouse.scaleX((Window)window, (double)this.x);
    }

    public static double scaleY(Window window, double y) {
        return y * (double)window.getScaledHeight() / (double)window.getHeight();
    }

    public double getScaledY(Window window) {
        return Mouse.scaleY((Window)window, (double)this.y);
    }

    private void updateMouse(double timeDelta) {
        double j;
        double i;
        double d = (Double)this.client.options.getMouseSensitivity().getValue() * (double)0.6f + (double)0.2f;
        double e = d * d * d;
        double f = e * 8.0;
        if (this.client.options.smoothCameraEnabled) {
            double g = this.cursorXSmoother.smooth(this.cursorDeltaX * f, timeDelta * f);
            double h = this.cursorYSmoother.smooth(this.cursorDeltaY * f, timeDelta * f);
            i = g;
            j = h;
        } else if (this.client.options.getPerspective().isFirstPerson() && this.client.player.isUsingSpyglass()) {
            this.cursorXSmoother.clear();
            this.cursorYSmoother.clear();
            i = this.cursorDeltaX * e;
            j = this.cursorDeltaY * e;
        } else {
            this.cursorXSmoother.clear();
            this.cursorYSmoother.clear();
            i = this.cursorDeltaX * f;
            j = this.cursorDeltaY * f;
        }
        this.client.getTutorialManager().onUpdateMouse(i, j);
        if (this.client.player != null) {
            this.client.player.changeLookDirection((Boolean)this.client.options.getInvertMouseX().getValue() != false ? -i : i, (Boolean)this.client.options.getInvertMouseY().getValue() != false ? -j : j);
        }
    }

    public boolean wasLeftButtonClicked() {
        return this.leftButtonClicked;
    }

    public boolean wasMiddleButtonClicked() {
        return this.middleButtonClicked;
    }

    public boolean wasRightButtonClicked() {
        return this.rightButtonClicked;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void onResolutionChanged() {
        this.hasResolutionChanged = true;
    }

    public boolean isCursorLocked() {
        return this.cursorLocked;
    }

    public void lockCursor() {
        if (!this.client.isWindowFocused()) {
            return;
        }
        if (this.cursorLocked) {
            return;
        }
        if (SystemKeycodes.UPDATE_PRESSED_STATE_ON_MOUSE_GRAB) {
            KeyBinding.updatePressedStates();
        }
        this.cursorLocked = true;
        this.x = this.client.getWindow().getWidth() / 2;
        this.y = this.client.getWindow().getHeight() / 2;
        InputUtil.setCursorParameters((Window)this.client.getWindow(), (int)212995, (double)this.x, (double)this.y);
        this.client.setScreen(null);
        this.client.attackCooldown = 10000;
        this.hasResolutionChanged = true;
    }

    public void unlockCursor() {
        if (!this.cursorLocked) {
            return;
        }
        this.cursorLocked = false;
        this.x = this.client.getWindow().getWidth() / 2;
        this.y = this.client.getWindow().getHeight() / 2;
        InputUtil.setCursorParameters((Window)this.client.getWindow(), (int)212993, (double)this.x, (double)this.y);
    }

    public void setResolutionChanged() {
        this.hasResolutionChanged = true;
    }

    public void drawScaledPos(TextRenderer textRenderer, DrawContext context) {
        Window window = this.client.getWindow();
        double d = this.getScaledX(window);
        double e = this.getScaledY(window) - 8.0;
        String string = String.format(Locale.ROOT, "%.0f,%.0f", d, e);
        context.drawTextWithShadow(textRenderer, string, (int)d, (int)e, -1);
    }
}

