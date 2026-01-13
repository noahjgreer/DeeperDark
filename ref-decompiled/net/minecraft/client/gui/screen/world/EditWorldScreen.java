/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.world.BackupPromptScreen
 *  net.minecraft.client.gui.screen.world.EditWorldScreen
 *  net.minecraft.client.gui.screen.world.OptimizeWorldScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ButtonWidget$PressAction
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.EmptyWidget
 *  net.minecraft.client.gui.widget.SimplePositioningWidget
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.client.toast.SystemToast
 *  net.minecraft.client.toast.SystemToast$Type
 *  net.minecraft.client.toast.Toast
 *  net.minecraft.nbt.NbtCrashException
 *  net.minecraft.nbt.NbtException
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.StringHelper
 *  net.minecraft.util.Util
 *  net.minecraft.util.WorldSavePath
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.path.PathUtil
 *  net.minecraft.world.level.storage.LevelStorage
 *  net.minecraft.world.level.storage.LevelStorage$Session
 *  net.minecraft.world.level.storage.LevelSummary
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.world;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.LambdaMetafactory;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.BackupPromptScreen;
import net.minecraft.client.gui.screen.world.OptimizeWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.nbt.NbtCrashException;
import net.minecraft.nbt.NbtException;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.path.PathUtil;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class EditWorldScreen
extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Text ENTER_NAME_TEXT = Text.translatable((String)"selectWorld.enterName").formatted(Formatting.GRAY);
    private static final Text RESET_ICON_TEXT = Text.translatable((String)"selectWorld.edit.resetIcon");
    private static final Text OPEN_FOLDER_TEXT = Text.translatable((String)"selectWorld.edit.openFolder");
    private static final Text BACKUP_TEXT = Text.translatable((String)"selectWorld.edit.backup");
    private static final Text BACKUP_FOLDER_TEXT = Text.translatable((String)"selectWorld.edit.backupFolder");
    private static final Text OPTIMIZE_TEXT = Text.translatable((String)"selectWorld.edit.optimize");
    private static final Text CONFIRM_TITLE_TEXT = Text.translatable((String)"optimizeWorld.confirm.title");
    private static final Text CONFIRM_DESCRIPTION_TEXT = Text.translatable((String)"optimizeWorld.confirm.description");
    private static final Text CONFIRM_PROCEED_TEXT = Text.translatable((String)"optimizeWorld.confirm.proceed");
    private static final Text SAVE_TEXT = Text.translatable((String)"selectWorld.edit.save");
    private static final int field_46893 = 200;
    private static final int field_46894 = 4;
    private static final int field_46895 = 98;
    private final DirectionalLayoutWidget layout = DirectionalLayoutWidget.vertical().spacing(5);
    private final BooleanConsumer callback;
    private final LevelStorage.Session storageSession;
    private final TextFieldWidget nameFieldWidget;

    public static EditWorldScreen create(MinecraftClient client, LevelStorage.Session session, BooleanConsumer callback) throws IOException {
        LevelSummary levelSummary = session.getLevelSummary(session.readLevelProperties());
        return new EditWorldScreen(client, session, levelSummary.getDisplayName(), callback);
    }

    private EditWorldScreen(MinecraftClient client, LevelStorage.Session session, String levelName, BooleanConsumer callback) {
        super((Text)Text.translatable((String)"selectWorld.edit.title"));
        this.callback = callback;
        this.storageSession = session;
        TextRenderer textRenderer = client.textRenderer;
        this.layout.add((Widget)new EmptyWidget(200, 20));
        this.layout.add((Widget)new TextWidget(ENTER_NAME_TEXT, textRenderer));
        this.nameFieldWidget = (TextFieldWidget)this.layout.add((Widget)new TextFieldWidget(textRenderer, 200, 20, ENTER_NAME_TEXT));
        this.nameFieldWidget.setText(levelName);
        DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(4);
        ButtonWidget buttonWidget = (ButtonWidget)directionalLayoutWidget.add((Widget)ButtonWidget.builder((Text)SAVE_TEXT, button -> this.commit(this.nameFieldWidget.getText())).width(98).build());
        directionalLayoutWidget.add((Widget)ButtonWidget.builder((Text)ScreenTexts.CANCEL, button -> this.close()).width(98).build());
        this.nameFieldWidget.setChangedListener(name -> {
            buttonWidget.active = !StringHelper.isBlank((String)name);
        });
        ((ButtonWidget)this.layout.add((Widget)ButtonWidget.builder((Text)EditWorldScreen.RESET_ICON_TEXT, (ButtonWidget.PressAction)(ButtonWidget.PressAction)LambdaMetafactory.metafactory(null, null, null, (Lnet/minecraft/client/gui/widget/ButtonWidget;)V, method_54608(net.minecraft.world.level.storage.LevelStorage$Session net.minecraft.client.gui.widget.ButtonWidget ), (Lnet/minecraft/client/gui/widget/ButtonWidget;)V)((LevelStorage.Session)session)).width((int)200).build())).active = session.getIconFile().filter(iconFile -> Files.isRegularFile(iconFile, new LinkOption[0])).isPresent();
        this.layout.add((Widget)ButtonWidget.builder((Text)OPEN_FOLDER_TEXT, button -> Util.getOperatingSystem().open(session.getDirectory(WorldSavePath.ROOT))).width(200).build());
        this.layout.add((Widget)ButtonWidget.builder((Text)BACKUP_TEXT, button -> {
            boolean bl = EditWorldScreen.backupLevel((LevelStorage.Session)session);
            this.callback.accept(!bl);
        }).width(200).build());
        this.layout.add((Widget)ButtonWidget.builder((Text)BACKUP_FOLDER_TEXT, button -> {
            LevelStorage levelStorage = client.getLevelStorage();
            Path path = levelStorage.getBackupsDirectory();
            try {
                PathUtil.createDirectories((Path)path);
            }
            catch (IOException iOException) {
                throw new RuntimeException(iOException);
            }
            Util.getOperatingSystem().open(path);
        }).width(200).build());
        this.layout.add((Widget)ButtonWidget.builder((Text)OPTIMIZE_TEXT, button -> client.setScreen((Screen)new BackupPromptScreen(() -> client.setScreen((Screen)this), (backup, eraseCache) -> {
            if (backup) {
                EditWorldScreen.backupLevel((LevelStorage.Session)session);
            }
            client.setScreen((Screen)OptimizeWorldScreen.create((MinecraftClient)client, (BooleanConsumer)this.callback, (DataFixer)client.getDataFixer(), (LevelStorage.Session)session, (boolean)eraseCache));
        }, CONFIRM_TITLE_TEXT, CONFIRM_DESCRIPTION_TEXT, CONFIRM_PROCEED_TEXT, true))).width(200).build());
        this.layout.add((Widget)new EmptyWidget(200, 20));
        this.layout.add((Widget)directionalLayoutWidget);
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
    }

    protected void setInitialFocus() {
        this.setInitialFocus((Element)this.nameFieldWidget);
    }

    protected void init() {
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        SimplePositioningWidget.setPos((Widget)this.layout, (ScreenRect)this.getNavigationFocus());
    }

    public boolean keyPressed(KeyInput input) {
        if (this.nameFieldWidget.isFocused() && input.isEnter()) {
            this.commit(this.nameFieldWidget.getText());
            this.close();
            return true;
        }
        return super.keyPressed(input);
    }

    public void close() {
        this.callback.accept(false);
    }

    private void commit(String levelName) {
        try {
            this.storageSession.save(levelName);
        }
        catch (IOException | NbtCrashException | NbtException exception) {
            LOGGER.error("Failed to access world '{}'", (Object)this.storageSession.getDirectoryName(), (Object)exception);
            SystemToast.addWorldAccessFailureToast((MinecraftClient)this.client, (String)this.storageSession.getDirectoryName());
        }
        this.callback.accept(true);
    }

    public static boolean backupLevel(LevelStorage.Session storageSession) {
        long l = 0L;
        IOException iOException = null;
        try {
            l = storageSession.createBackup();
        }
        catch (IOException iOException2) {
            iOException = iOException2;
        }
        if (iOException != null) {
            MutableText text = Text.translatable((String)"selectWorld.edit.backupFailed");
            MutableText text2 = Text.literal((String)iOException.getMessage());
            MinecraftClient.getInstance().getToastManager().add((Toast)new SystemToast(SystemToast.Type.WORLD_BACKUP, (Text)text, (Text)text2));
            return false;
        }
        MutableText text = Text.translatable((String)"selectWorld.edit.backupCreated", (Object[])new Object[]{storageSession.getDirectoryName()});
        MutableText text2 = Text.translatable((String)"selectWorld.edit.backupSize", (Object[])new Object[]{MathHelper.ceil((double)((double)l / 1048576.0))});
        MinecraftClient.getInstance().getToastManager().add((Toast)new SystemToast(SystemToast.Type.WORLD_BACKUP, (Text)text, (Text)text2));
        return true;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, -1);
    }

    private static /* synthetic */ void method_54608(LevelStorage.Session session, ButtonWidget button) {
        session.getIconFile().ifPresent(iconFile -> FileUtils.deleteQuietly((File)iconFile.toFile()));
        button.active = false;
    }
}

