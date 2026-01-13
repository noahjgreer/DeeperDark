/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.entity.StructureBlockBlockEntity
 *  net.minecraft.block.entity.StructureBlockBlockEntity$Action
 *  net.minecraft.block.enums.StructureBlockMode
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.StructureBlockScreen
 *  net.minecraft.client.gui.screen.ingame.StructureBlockScreen$2
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.CyclingButtonWidget
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.UpdateStructureBlockC2SPacket
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.ImmutableList;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.UpdateStructureBlockC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

@Environment(value=EnvType.CLIENT)
public class StructureBlockScreen
extends Screen {
    private static final Text STRUCTURE_NAME_TEXT = Text.translatable((String)"structure_block.structure_name");
    private static final Text POSITION_TEXT = Text.translatable((String)"structure_block.position");
    private static final Text SIZE_TEXT = Text.translatable((String)"structure_block.size");
    private static final Text INTEGRITY_TEXT = Text.translatable((String)"structure_block.integrity");
    private static final Text CUSTOM_DATA_TEXT = Text.translatable((String)"structure_block.custom_data");
    private static final Text INCLUDE_ENTITIES_TEXT = Text.translatable((String)"structure_block.include_entities");
    private static final Text STRICT_TEXT = Text.translatable((String)"structure_block.strict");
    private static final Text DETECT_SIZE_TEXT = Text.translatable((String)"structure_block.detect_size");
    private static final Text SHOW_AIR_TEXT = Text.translatable((String)"structure_block.show_air");
    private static final Text SHOW_BOUNDING_BOX_TEXT = Text.translatable((String)"structure_block.show_boundingbox");
    private static final ImmutableList<StructureBlockMode> MODES = ImmutableList.copyOf((Object[])StructureBlockMode.values());
    private static final ImmutableList<StructureBlockMode> MODES_EXCEPT_DATA = (ImmutableList)MODES.stream().filter(mode -> mode != StructureBlockMode.DATA).collect(ImmutableList.toImmutableList());
    private final StructureBlockBlockEntity structureBlock;
    private BlockMirror mirror = BlockMirror.NONE;
    private BlockRotation rotation = BlockRotation.NONE;
    private StructureBlockMode mode = StructureBlockMode.DATA;
    private boolean ignoreEntities;
    private boolean strict;
    private boolean showAir;
    private boolean showBoundingBox;
    private TextFieldWidget inputName;
    private TextFieldWidget inputPosX;
    private TextFieldWidget inputPosY;
    private TextFieldWidget inputPosZ;
    private TextFieldWidget inputSizeX;
    private TextFieldWidget inputSizeY;
    private TextFieldWidget inputSizeZ;
    private TextFieldWidget inputIntegrity;
    private TextFieldWidget inputSeed;
    private TextFieldWidget inputMetadata;
    private ButtonWidget buttonSave;
    private ButtonWidget buttonLoad;
    private ButtonWidget buttonRotate0;
    private ButtonWidget buttonRotate90;
    private ButtonWidget buttonRotate180;
    private ButtonWidget buttonRotate270;
    private ButtonWidget buttonDetect;
    private CyclingButtonWidget<Boolean> ignoreEntitiesButton;
    private CyclingButtonWidget<Boolean> strictButton;
    private CyclingButtonWidget<BlockMirror> mirrorButton;
    private CyclingButtonWidget<Boolean> showAirButton;
    private CyclingButtonWidget<Boolean> showBoundingBoxButton;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.0###", DecimalFormatSymbols.getInstance(Locale.ROOT));

    public StructureBlockScreen(StructureBlockBlockEntity structureBlock) {
        super((Text)Text.translatable((String)Blocks.STRUCTURE_BLOCK.getTranslationKey()));
        this.structureBlock = structureBlock;
    }

    private void done() {
        if (this.updateStructureBlock(StructureBlockBlockEntity.Action.UPDATE_DATA)) {
            this.client.setScreen(null);
        }
    }

    private void cancel() {
        this.structureBlock.setMirror(this.mirror);
        this.structureBlock.setRotation(this.rotation);
        this.structureBlock.setMode(this.mode);
        this.structureBlock.setIgnoreEntities(this.ignoreEntities);
        this.structureBlock.setStrict(this.strict);
        this.structureBlock.setShowAir(this.showAir);
        this.structureBlock.setShowBoundingBox(this.showBoundingBox);
        this.client.setScreen(null);
    }

    protected void init() {
        this.addDrawableChild((Element)ButtonWidget.builder((Text)ScreenTexts.DONE, button -> this.done()).dimensions(this.width / 2 - 4 - 150, 210, 150, 20).build());
        this.addDrawableChild((Element)ButtonWidget.builder((Text)ScreenTexts.CANCEL, button -> this.cancel()).dimensions(this.width / 2 + 4, 210, 150, 20).build());
        this.mirror = this.structureBlock.getMirror();
        this.rotation = this.structureBlock.getRotation();
        this.mode = this.structureBlock.getMode();
        this.ignoreEntities = this.structureBlock.shouldIgnoreEntities();
        this.strict = this.structureBlock.isStrict();
        this.showAir = this.structureBlock.shouldShowAir();
        this.showBoundingBox = this.structureBlock.shouldShowBoundingBox();
        this.buttonSave = (ButtonWidget)this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.translatable((String)"structure_block.button.save"), button -> {
            if (this.structureBlock.getMode() == StructureBlockMode.SAVE) {
                this.updateStructureBlock(StructureBlockBlockEntity.Action.SAVE_AREA);
                this.client.setScreen(null);
            }
        }).dimensions(this.width / 2 + 4 + 100, 185, 50, 20).build());
        this.buttonLoad = (ButtonWidget)this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.translatable((String)"structure_block.button.load"), button -> {
            if (this.structureBlock.getMode() == StructureBlockMode.LOAD) {
                this.updateStructureBlock(StructureBlockBlockEntity.Action.LOAD_AREA);
                this.client.setScreen(null);
            }
        }).dimensions(this.width / 2 + 4 + 100, 185, 50, 20).build());
        this.addDrawableChild((Element)CyclingButtonWidget.builder(value -> Text.translatable((String)("structure_block.mode." + value.asString())), (Object)this.mode).values((List)MODES_EXCEPT_DATA, (List)MODES).omitKeyText().build(this.width / 2 - 4 - 150, 185, 50, 20, (Text)Text.literal((String)"MODE"), (button, mode) -> {
            this.structureBlock.setMode(mode);
            this.updateWidgets(mode);
        }));
        this.buttonDetect = (ButtonWidget)this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.translatable((String)"structure_block.button.detect_size"), button -> {
            if (this.structureBlock.getMode() == StructureBlockMode.SAVE) {
                this.updateStructureBlock(StructureBlockBlockEntity.Action.SCAN_AREA);
                this.client.setScreen(null);
            }
        }).dimensions(this.width / 2 + 4 + 100, 120, 50, 20).build());
        this.ignoreEntitiesButton = (CyclingButtonWidget)this.addDrawableChild((Element)CyclingButtonWidget.onOffBuilder((!this.structureBlock.shouldIgnoreEntities() ? 1 : 0) != 0).omitKeyText().build(this.width / 2 + 4 + 100, 160, 50, 20, INCLUDE_ENTITIES_TEXT, (button, includeEntities) -> this.structureBlock.setIgnoreEntities(includeEntities == false)));
        this.strictButton = (CyclingButtonWidget)this.addDrawableChild((Element)CyclingButtonWidget.onOffBuilder((boolean)this.structureBlock.isStrict()).omitKeyText().build(this.width / 2 + 4 + 100, 120, 50, 20, STRICT_TEXT, (button, strict) -> this.structureBlock.setStrict(strict.booleanValue())));
        this.mirrorButton = (CyclingButtonWidget)this.addDrawableChild((Element)CyclingButtonWidget.builder(BlockMirror::getName, (Object)this.mirror).values((Object[])BlockMirror.values()).omitKeyText().build(this.width / 2 - 20, 185, 40, 20, (Text)Text.literal((String)"MIRROR"), (button, mirror) -> this.structureBlock.setMirror(mirror)));
        this.showAirButton = (CyclingButtonWidget)this.addDrawableChild((Element)CyclingButtonWidget.onOffBuilder((boolean)this.structureBlock.shouldShowAir()).omitKeyText().build(this.width / 2 + 4 + 100, 80, 50, 20, SHOW_AIR_TEXT, (button, showAir) -> this.structureBlock.setShowAir(showAir.booleanValue())));
        this.showBoundingBoxButton = (CyclingButtonWidget)this.addDrawableChild((Element)CyclingButtonWidget.onOffBuilder((boolean)this.structureBlock.shouldShowBoundingBox()).omitKeyText().build(this.width / 2 + 4 + 100, 80, 50, 20, SHOW_BOUNDING_BOX_TEXT, (button, showBoundingBox) -> this.structureBlock.setShowBoundingBox(showBoundingBox.booleanValue())));
        this.buttonRotate0 = (ButtonWidget)this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.literal((String)"0"), button -> {
            this.structureBlock.setRotation(BlockRotation.NONE);
            this.updateRotationButton();
        }).dimensions(this.width / 2 - 1 - 40 - 1 - 40 - 20, 185, 40, 20).build());
        this.buttonRotate90 = (ButtonWidget)this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.literal((String)"90"), button -> {
            this.structureBlock.setRotation(BlockRotation.CLOCKWISE_90);
            this.updateRotationButton();
        }).dimensions(this.width / 2 - 1 - 40 - 20, 185, 40, 20).build());
        this.buttonRotate180 = (ButtonWidget)this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.literal((String)"180"), button -> {
            this.structureBlock.setRotation(BlockRotation.CLOCKWISE_180);
            this.updateRotationButton();
        }).dimensions(this.width / 2 + 1 + 20, 185, 40, 20).build());
        this.buttonRotate270 = (ButtonWidget)this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.literal((String)"270"), button -> {
            this.structureBlock.setRotation(BlockRotation.COUNTERCLOCKWISE_90);
            this.updateRotationButton();
        }).dimensions(this.width / 2 + 1 + 40 + 1 + 20, 185, 40, 20).build());
        this.inputName = new /* Unavailable Anonymous Inner Class!! */;
        this.inputName.setMaxLength(128);
        this.inputName.setText(this.structureBlock.getTemplateName());
        this.addSelectableChild((Element)this.inputName);
        BlockPos blockPos = this.structureBlock.getOffset();
        this.inputPosX = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 80, 80, 20, (Text)Text.translatable((String)"structure_block.position.x"));
        this.inputPosX.setMaxLength(15);
        this.inputPosX.setText(Integer.toString(blockPos.getX()));
        this.addSelectableChild((Element)this.inputPosX);
        this.inputPosY = new TextFieldWidget(this.textRenderer, this.width / 2 - 72, 80, 80, 20, (Text)Text.translatable((String)"structure_block.position.y"));
        this.inputPosY.setMaxLength(15);
        this.inputPosY.setText(Integer.toString(blockPos.getY()));
        this.addSelectableChild((Element)this.inputPosY);
        this.inputPosZ = new TextFieldWidget(this.textRenderer, this.width / 2 + 8, 80, 80, 20, (Text)Text.translatable((String)"structure_block.position.z"));
        this.inputPosZ.setMaxLength(15);
        this.inputPosZ.setText(Integer.toString(blockPos.getZ()));
        this.addSelectableChild((Element)this.inputPosZ);
        Vec3i vec3i = this.structureBlock.getSize();
        this.inputSizeX = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 120, 80, 20, (Text)Text.translatable((String)"structure_block.size.x"));
        this.inputSizeX.setMaxLength(15);
        this.inputSizeX.setText(Integer.toString(vec3i.getX()));
        this.addSelectableChild((Element)this.inputSizeX);
        this.inputSizeY = new TextFieldWidget(this.textRenderer, this.width / 2 - 72, 120, 80, 20, (Text)Text.translatable((String)"structure_block.size.y"));
        this.inputSizeY.setMaxLength(15);
        this.inputSizeY.setText(Integer.toString(vec3i.getY()));
        this.addSelectableChild((Element)this.inputSizeY);
        this.inputSizeZ = new TextFieldWidget(this.textRenderer, this.width / 2 + 8, 120, 80, 20, (Text)Text.translatable((String)"structure_block.size.z"));
        this.inputSizeZ.setMaxLength(15);
        this.inputSizeZ.setText(Integer.toString(vec3i.getZ()));
        this.addSelectableChild((Element)this.inputSizeZ);
        this.inputIntegrity = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 120, 80, 20, (Text)Text.translatable((String)"structure_block.integrity.integrity"));
        this.inputIntegrity.setMaxLength(15);
        this.inputIntegrity.setText(this.decimalFormat.format(this.structureBlock.getIntegrity()));
        this.addSelectableChild((Element)this.inputIntegrity);
        this.inputSeed = new TextFieldWidget(this.textRenderer, this.width / 2 - 72, 120, 80, 20, (Text)Text.translatable((String)"structure_block.integrity.seed"));
        this.inputSeed.setMaxLength(31);
        this.inputSeed.setText(Long.toString(this.structureBlock.getSeed()));
        this.addSelectableChild((Element)this.inputSeed);
        this.inputMetadata = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 120, 240, 20, (Text)Text.translatable((String)"structure_block.custom_data"));
        this.inputMetadata.setMaxLength(128);
        this.inputMetadata.setText(this.structureBlock.getMetadata());
        this.addSelectableChild((Element)this.inputMetadata);
        this.updateRotationButton();
        this.updateWidgets(this.mode);
    }

    protected void setInitialFocus() {
        this.setInitialFocus((Element)this.inputName);
    }

    public void resize(int width, int height) {
        String string = this.inputName.getText();
        String string2 = this.inputPosX.getText();
        String string3 = this.inputPosY.getText();
        String string4 = this.inputPosZ.getText();
        String string5 = this.inputSizeX.getText();
        String string6 = this.inputSizeY.getText();
        String string7 = this.inputSizeZ.getText();
        String string8 = this.inputIntegrity.getText();
        String string9 = this.inputSeed.getText();
        String string10 = this.inputMetadata.getText();
        this.init(width, height);
        this.inputName.setText(string);
        this.inputPosX.setText(string2);
        this.inputPosY.setText(string3);
        this.inputPosZ.setText(string4);
        this.inputSizeX.setText(string5);
        this.inputSizeY.setText(string6);
        this.inputSizeZ.setText(string7);
        this.inputIntegrity.setText(string8);
        this.inputSeed.setText(string9);
        this.inputMetadata.setText(string10);
    }

    private void updateRotationButton() {
        this.buttonRotate0.active = true;
        this.buttonRotate90.active = true;
        this.buttonRotate180.active = true;
        this.buttonRotate270.active = true;
        switch (2.field_3025[this.structureBlock.getRotation().ordinal()]) {
            case 1: {
                this.buttonRotate0.active = false;
                break;
            }
            case 2: {
                this.buttonRotate180.active = false;
                break;
            }
            case 3: {
                this.buttonRotate270.active = false;
                break;
            }
            case 4: {
                this.buttonRotate90.active = false;
            }
        }
    }

    private void updateWidgets(StructureBlockMode mode) {
        this.inputName.setVisible(false);
        this.inputPosX.setVisible(false);
        this.inputPosY.setVisible(false);
        this.inputPosZ.setVisible(false);
        this.inputSizeX.setVisible(false);
        this.inputSizeY.setVisible(false);
        this.inputSizeZ.setVisible(false);
        this.inputIntegrity.setVisible(false);
        this.inputSeed.setVisible(false);
        this.inputMetadata.setVisible(false);
        this.buttonSave.visible = false;
        this.buttonLoad.visible = false;
        this.buttonDetect.visible = false;
        this.ignoreEntitiesButton.visible = false;
        this.strictButton.visible = false;
        this.mirrorButton.visible = false;
        this.buttonRotate0.visible = false;
        this.buttonRotate90.visible = false;
        this.buttonRotate180.visible = false;
        this.buttonRotate270.visible = false;
        this.showAirButton.visible = false;
        this.showBoundingBoxButton.visible = false;
        switch (2.field_3024[mode.ordinal()]) {
            case 1: {
                this.inputName.setVisible(true);
                this.inputPosX.setVisible(true);
                this.inputPosY.setVisible(true);
                this.inputPosZ.setVisible(true);
                this.inputSizeX.setVisible(true);
                this.inputSizeY.setVisible(true);
                this.inputSizeZ.setVisible(true);
                this.buttonSave.visible = true;
                this.buttonDetect.visible = true;
                this.ignoreEntitiesButton.visible = true;
                this.strictButton.visible = false;
                this.showAirButton.visible = true;
                break;
            }
            case 2: {
                this.inputName.setVisible(true);
                this.inputPosX.setVisible(true);
                this.inputPosY.setVisible(true);
                this.inputPosZ.setVisible(true);
                this.inputIntegrity.setVisible(true);
                this.inputSeed.setVisible(true);
                this.buttonLoad.visible = true;
                this.ignoreEntitiesButton.visible = true;
                this.strictButton.visible = true;
                this.mirrorButton.visible = true;
                this.buttonRotate0.visible = true;
                this.buttonRotate90.visible = true;
                this.buttonRotate180.visible = true;
                this.buttonRotate270.visible = true;
                this.showBoundingBoxButton.visible = true;
                this.updateRotationButton();
                break;
            }
            case 3: {
                this.inputName.setVisible(true);
                break;
            }
            case 4: {
                this.inputMetadata.setVisible(true);
            }
        }
    }

    private boolean updateStructureBlock(StructureBlockBlockEntity.Action action) {
        BlockPos blockPos = new BlockPos(this.parseInt(this.inputPosX.getText()), this.parseInt(this.inputPosY.getText()), this.parseInt(this.inputPosZ.getText()));
        Vec3i vec3i = new Vec3i(this.parseInt(this.inputSizeX.getText()), this.parseInt(this.inputSizeY.getText()), this.parseInt(this.inputSizeZ.getText()));
        float f = this.parseFloat(this.inputIntegrity.getText());
        long l = this.parseLong(this.inputSeed.getText());
        this.client.getNetworkHandler().sendPacket((Packet)new UpdateStructureBlockC2SPacket(this.structureBlock.getPos(), action, this.structureBlock.getMode(), this.inputName.getText(), blockPos, vec3i, this.structureBlock.getMirror(), this.structureBlock.getRotation(), this.inputMetadata.getText(), this.structureBlock.shouldIgnoreEntities(), this.structureBlock.isStrict(), this.structureBlock.shouldShowAir(), this.structureBlock.shouldShowBoundingBox(), f, l));
        return true;
    }

    private long parseLong(String string) {
        try {
            return Long.valueOf(string);
        }
        catch (NumberFormatException numberFormatException) {
            return 0L;
        }
    }

    private float parseFloat(String string) {
        try {
            return Float.valueOf(string).floatValue();
        }
        catch (NumberFormatException numberFormatException) {
            return 1.0f;
        }
    }

    private int parseInt(String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException numberFormatException) {
            return 0;
        }
    }

    public void close() {
        this.cancel();
    }

    public boolean keyPressed(KeyInput input) {
        if (super.keyPressed(input)) {
            return true;
        }
        if (input.isEnter()) {
            this.done();
            return true;
        }
        return false;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        StructureBlockMode structureBlockMode = this.structureBlock.getMode();
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, -1);
        if (structureBlockMode != StructureBlockMode.DATA) {
            context.drawTextWithShadow(this.textRenderer, STRUCTURE_NAME_TEXT, this.width / 2 - 153, 30, -6250336);
            this.inputName.render(context, mouseX, mouseY, deltaTicks);
        }
        if (structureBlockMode == StructureBlockMode.LOAD || structureBlockMode == StructureBlockMode.SAVE) {
            context.drawTextWithShadow(this.textRenderer, POSITION_TEXT, this.width / 2 - 153, 70, -6250336);
            this.inputPosX.render(context, mouseX, mouseY, deltaTicks);
            this.inputPosY.render(context, mouseX, mouseY, deltaTicks);
            this.inputPosZ.render(context, mouseX, mouseY, deltaTicks);
            context.drawTextWithShadow(this.textRenderer, INCLUDE_ENTITIES_TEXT, this.width / 2 + 154 - this.textRenderer.getWidth((StringVisitable)INCLUDE_ENTITIES_TEXT), 150, -6250336);
        }
        if (structureBlockMode == StructureBlockMode.SAVE) {
            context.drawTextWithShadow(this.textRenderer, SIZE_TEXT, this.width / 2 - 153, 110, -6250336);
            this.inputSizeX.render(context, mouseX, mouseY, deltaTicks);
            this.inputSizeY.render(context, mouseX, mouseY, deltaTicks);
            this.inputSizeZ.render(context, mouseX, mouseY, deltaTicks);
            context.drawTextWithShadow(this.textRenderer, DETECT_SIZE_TEXT, this.width / 2 + 154 - this.textRenderer.getWidth((StringVisitable)DETECT_SIZE_TEXT), 110, -6250336);
            context.drawTextWithShadow(this.textRenderer, SHOW_AIR_TEXT, this.width / 2 + 154 - this.textRenderer.getWidth((StringVisitable)SHOW_AIR_TEXT), 70, -6250336);
        }
        if (structureBlockMode == StructureBlockMode.LOAD) {
            context.drawTextWithShadow(this.textRenderer, INTEGRITY_TEXT, this.width / 2 - 153, 110, -6250336);
            this.inputIntegrity.render(context, mouseX, mouseY, deltaTicks);
            this.inputSeed.render(context, mouseX, mouseY, deltaTicks);
            context.drawTextWithShadow(this.textRenderer, STRICT_TEXT, this.width / 2 + 154 - this.textRenderer.getWidth((StringVisitable)STRICT_TEXT), 110, -6250336);
            context.drawTextWithShadow(this.textRenderer, SHOW_BOUNDING_BOX_TEXT, this.width / 2 + 154 - this.textRenderer.getWidth((StringVisitable)SHOW_BOUNDING_BOX_TEXT), 70, -6250336);
        }
        if (structureBlockMode == StructureBlockMode.DATA) {
            context.drawTextWithShadow(this.textRenderer, CUSTOM_DATA_TEXT, this.width / 2 - 153, 110, -6250336);
            this.inputMetadata.render(context, mouseX, mouseY, deltaTicks);
        }
        context.drawTextWithShadow(this.textRenderer, structureBlockMode.asText(), this.width / 2 - 153, 174, -6250336);
    }

    public boolean shouldPause() {
        return false;
    }

    public boolean deferSubtitles() {
        return true;
    }

    static /* synthetic */ boolean method_16017(StructureBlockScreen structureBlockScreen, String string, int i, int j) {
        return structureBlockScreen.isValidCharacterForName(string, i, j);
    }
}

