/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.block.entity.TestInstanceBlockEntity
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.TestInstanceBlockScreen
 *  net.minecraft.client.gui.screen.ingame.TestInstanceBlockScreen$1
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.CyclingButtonWidget
 *  net.minecraft.client.gui.widget.ScrollableTextWidget
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.TestInstanceBlockActionC2SPacket
 *  net.minecraft.network.packet.c2s.play.TestInstanceBlockActionC2SPacket$Action
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3i
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.ingame;

import java.util.Objects;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.block.entity.TestInstanceBlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.TestInstanceBlockScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.ScrollableTextWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.TestInstanceBlockActionC2SPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class TestInstanceBlockScreen
extends Screen {
    private static final Text TEST_ID_TEXT = Text.translatable((String)"test_instance_block.test_id");
    private static final Text SIZE_TEXT = Text.translatable((String)"test_instance_block.size");
    private static final Text ENTITIES_TEXT = Text.translatable((String)"test_instance_block.entities");
    private static final Text ROTATION_TEXT = Text.translatable((String)"test_instance_block.rotation");
    private static final int field_56052 = 8;
    private static final int field_56053 = 316;
    private final TestInstanceBlockEntity testInstanceBlockEntity;
    private @Nullable TextFieldWidget testIdTextField;
    private @Nullable TextFieldWidget sizeXField;
    private @Nullable TextFieldWidget sizeYField;
    private @Nullable TextFieldWidget sizeZField;
    private @Nullable ScrollableTextWidget statusWidget;
    private @Nullable ButtonWidget saveButton;
    private @Nullable ButtonWidget exportButton;
    private @Nullable CyclingButtonWidget<Boolean> entitiesButton;
    private @Nullable CyclingButtonWidget<BlockRotation> rotationButton;

    public TestInstanceBlockScreen(TestInstanceBlockEntity testInstanceBlockEntity) {
        super((Text)testInstanceBlockEntity.getCachedState().getBlock().getName());
        this.testInstanceBlockEntity = testInstanceBlockEntity;
    }

    protected void init() {
        int i = this.width / 2 - 158;
        boolean bl = SharedConstants.isDevelopment;
        int j = bl ? 3 : 2;
        int k = TestInstanceBlockScreen.getRoundedWidth((int)j);
        this.testIdTextField = new TextFieldWidget(this.textRenderer, i, 40, 316, 20, (Text)Text.translatable((String)"test_instance_block.test_id"));
        this.testIdTextField.setMaxLength(128);
        Optional optional = this.testInstanceBlockEntity.getTestKey();
        if (optional.isPresent()) {
            this.testIdTextField.setText(((RegistryKey)optional.get()).getValue().toString());
        }
        this.testIdTextField.setChangedListener(value -> this.refresh(false));
        this.addDrawableChild((Element)this.testIdTextField);
        Objects.requireNonNull(this.textRenderer);
        this.statusWidget = new ScrollableTextWidget(i, 70, 316, 8 * 9, (Text)Text.literal((String)""), this.textRenderer);
        this.addDrawableChild((Element)this.statusWidget);
        Vec3i vec3i = this.testInstanceBlockEntity.getSize();
        int l = 0;
        this.sizeXField = new TextFieldWidget(this.textRenderer, this.getX(l++, 5), 160, TestInstanceBlockScreen.getRoundedWidth((int)5), 20, (Text)Text.translatable((String)"structure_block.size.x"));
        this.sizeXField.setMaxLength(15);
        this.addDrawableChild((Element)this.sizeXField);
        this.sizeYField = new TextFieldWidget(this.textRenderer, this.getX(l++, 5), 160, TestInstanceBlockScreen.getRoundedWidth((int)5), 20, (Text)Text.translatable((String)"structure_block.size.y"));
        this.sizeYField.setMaxLength(15);
        this.addDrawableChild((Element)this.sizeYField);
        this.sizeZField = new TextFieldWidget(this.textRenderer, this.getX(l++, 5), 160, TestInstanceBlockScreen.getRoundedWidth((int)5), 20, (Text)Text.translatable((String)"structure_block.size.z"));
        this.sizeZField.setMaxLength(15);
        this.addDrawableChild((Element)this.sizeZField);
        this.setSize(vec3i);
        this.rotationButton = (CyclingButtonWidget)this.addDrawableChild((Element)CyclingButtonWidget.builder(TestInstanceBlockScreen::rotationAsText, (Object)this.testInstanceBlockEntity.getRotation()).values((Object[])BlockRotation.values()).omitKeyText().build(this.getX(l++, 5), 160, TestInstanceBlockScreen.getRoundedWidth((int)5), 20, ROTATION_TEXT, (button, rotation) -> this.refresh()));
        this.entitiesButton = (CyclingButtonWidget)this.addDrawableChild((Element)CyclingButtonWidget.onOffBuilder((!this.testInstanceBlockEntity.shouldIgnoreEntities() ? 1 : 0) != 0).omitKeyText().build(this.getX(l++, 5), 160, TestInstanceBlockScreen.getRoundedWidth((int)5), 20, ENTITIES_TEXT));
        l = 0;
        this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.translatable((String)"test_instance.action.reset"), button -> {
            this.executeAction(TestInstanceBlockActionC2SPacket.Action.RESET);
            this.client.setScreen(null);
        }).dimensions(this.getX(l++, j), 185, k, 20).build());
        this.saveButton = (ButtonWidget)this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.translatable((String)"test_instance.action.save"), button -> {
            this.executeAction(TestInstanceBlockActionC2SPacket.Action.SAVE);
            this.client.setScreen(null);
        }).dimensions(this.getX(l++, j), 185, k, 20).build());
        if (bl) {
            this.exportButton = (ButtonWidget)this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.literal((String)"Export Structure"), button -> {
                this.executeAction(TestInstanceBlockActionC2SPacket.Action.EXPORT);
                this.client.setScreen(null);
            }).dimensions(this.getX(l++, j), 185, k, 20).build());
        }
        this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.translatable((String)"test_instance.action.run"), button -> {
            this.executeAction(TestInstanceBlockActionC2SPacket.Action.RUN);
            this.client.setScreen(null);
        }).dimensions(this.getX(0, 3), 210, TestInstanceBlockScreen.getRoundedWidth((int)3), 20).build());
        this.addDrawableChild((Element)ButtonWidget.builder((Text)ScreenTexts.DONE, button -> this.onDone()).dimensions(this.getX(1, 3), 210, TestInstanceBlockScreen.getRoundedWidth((int)3), 20).build());
        this.addDrawableChild((Element)ButtonWidget.builder((Text)ScreenTexts.CANCEL, button -> this.onCancel()).dimensions(this.getX(2, 3), 210, TestInstanceBlockScreen.getRoundedWidth((int)3), 20).build());
        this.refresh(true);
    }

    private void refresh() {
        boolean bl;
        this.saveButton.active = bl = this.rotationButton.getValue() == BlockRotation.NONE && Identifier.tryParse((String)this.testIdTextField.getText()) != null;
        if (this.exportButton != null) {
            this.exportButton.active = bl;
        }
    }

    private static Text rotationAsText(BlockRotation rotation) {
        return Text.literal((String)(switch (1.field_56059[rotation.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> "0";
            case 2 -> "90";
            case 3 -> "180";
            case 4 -> "270";
        }));
    }

    private void setSize(Vec3i vec) {
        this.sizeXField.setText(Integer.toString(vec.getX()));
        this.sizeYField.setText(Integer.toString(vec.getY()));
        this.sizeZField.setText(Integer.toString(vec.getZ()));
    }

    private int getX(int index, int total) {
        int i = this.width / 2 - 158;
        float f = TestInstanceBlockScreen.getWidth((int)total);
        return (int)((float)i + (float)index * (8.0f + f));
    }

    private static int getRoundedWidth(int total) {
        return (int)TestInstanceBlockScreen.getWidth((int)total);
    }

    private static float getWidth(int total) {
        return (float)(316 - (total - 1) * 8) / (float)total;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        int i = this.width / 2 - 158;
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, -1);
        context.drawTextWithShadow(this.textRenderer, TEST_ID_TEXT, i, 30, -6250336);
        context.drawTextWithShadow(this.textRenderer, SIZE_TEXT, i, 150, -6250336);
        context.drawTextWithShadow(this.textRenderer, ROTATION_TEXT, this.rotationButton.getX(), 150, -6250336);
        context.drawTextWithShadow(this.textRenderer, ENTITIES_TEXT, this.entitiesButton.getX(), 150, -6250336);
    }

    private void refresh(boolean initial) {
        boolean bl = this.executeAction(initial ? TestInstanceBlockActionC2SPacket.Action.INIT : TestInstanceBlockActionC2SPacket.Action.QUERY);
        if (!bl) {
            this.statusWidget.setMessage((Text)Text.translatable((String)"test_instance.description.invalid_id").formatted(Formatting.RED));
        }
        this.refresh();
    }

    private void onDone() {
        this.executeAction(TestInstanceBlockActionC2SPacket.Action.SET);
        this.close();
    }

    private boolean executeAction(TestInstanceBlockActionC2SPacket.Action action) {
        Optional<Identifier> optional = Optional.ofNullable(Identifier.tryParse((String)this.testIdTextField.getText()));
        Optional<RegistryKey> optional2 = optional.map(testId -> RegistryKey.of((RegistryKey)RegistryKeys.TEST_INSTANCE, (Identifier)testId));
        Vec3i vec3i = new Vec3i(TestInstanceBlockScreen.parse((String)this.sizeXField.getText()), TestInstanceBlockScreen.parse((String)this.sizeYField.getText()), TestInstanceBlockScreen.parse((String)this.sizeZField.getText()));
        boolean bl = (Boolean)this.entitiesButton.getValue() == false;
        this.client.getNetworkHandler().sendPacket((Packet)new TestInstanceBlockActionC2SPacket(this.testInstanceBlockEntity.getPos(), action, optional2, vec3i, (BlockRotation)this.rotationButton.getValue(), bl));
        return optional.isPresent();
    }

    public void handleStatus(Text status, Optional<Vec3i> size) {
        MutableText mutableText = Text.empty();
        this.testInstanceBlockEntity.getErrorMessage().ifPresent(message -> mutableText.append((Text)Text.translatable((String)"test_instance.description.failed", (Object[])new Object[]{Text.empty().formatted(Formatting.RED).append(message)})).append("\n\n"));
        mutableText.append(status);
        this.statusWidget.setMessage((Text)mutableText);
        size.ifPresent(arg_0 -> this.setSize(arg_0));
    }

    private void onCancel() {
        this.close();
    }

    private static int parse(String value) {
        try {
            return MathHelper.clamp((int)Integer.parseInt(value), (int)1, (int)48);
        }
        catch (NumberFormatException numberFormatException) {
            return 1;
        }
    }

    public boolean deferSubtitles() {
        return true;
    }
}

