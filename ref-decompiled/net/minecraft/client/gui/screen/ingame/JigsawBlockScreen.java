/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.JigsawBlock
 *  net.minecraft.block.entity.JigsawBlockEntity
 *  net.minecraft.block.entity.JigsawBlockEntity$Joint
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.JigsawBlockScreen
 *  net.minecraft.client.gui.tooltip.Tooltip
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.CyclingButtonWidget
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.client.util.NarratorManager
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.JigsawGeneratingC2SPacket
 *  net.minecraft.network.packet.c2s.play.UpdateJigsawC2SPacket
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.JigsawGeneratingC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateJigsawC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class JigsawBlockScreen
extends Screen {
    private static final Text JOINT_LABEL_TEXT = Text.translatable((String)"jigsaw_block.joint_label");
    private static final Text POOL_TEXT = Text.translatable((String)"jigsaw_block.pool");
    private static final Text NAME_TEXT = Text.translatable((String)"jigsaw_block.name");
    private static final Text TARGET_TEXT = Text.translatable((String)"jigsaw_block.target");
    private static final Text FINAL_STATE_TEXT = Text.translatable((String)"jigsaw_block.final_state");
    private static final Text PLACEMENT_PRIORITY_TEXT = Text.translatable((String)"jigsaw_block.placement_priority");
    private static final Text PLACEMENT_PRIORITY_TOOLTIP = Text.translatable((String)"jigsaw_block.placement_priority.tooltip");
    private static final Text SELECTION_PRIORITY_TEXT = Text.translatable((String)"jigsaw_block.selection_priority");
    private static final Text SELECTION_PRIORITY_TOOLTIP = Text.translatable((String)"jigsaw_block.selection_priority.tooltip");
    private final JigsawBlockEntity jigsaw;
    private TextFieldWidget nameField;
    private TextFieldWidget targetField;
    private TextFieldWidget poolField;
    private TextFieldWidget finalStateField;
    private TextFieldWidget selectionPriorityField;
    private TextFieldWidget placementPriorityField;
    int generationDepth;
    private boolean keepJigsaws = true;
    private CyclingButtonWidget<JigsawBlockEntity.Joint> jointRotationButton;
    private ButtonWidget doneButton;
    private ButtonWidget generateButton;
    private JigsawBlockEntity.Joint joint;

    public JigsawBlockScreen(JigsawBlockEntity jigsaw) {
        super(NarratorManager.EMPTY);
        this.jigsaw = jigsaw;
    }

    private void onDone() {
        this.updateServer();
        this.client.setScreen(null);
    }

    private void onCancel() {
        this.client.setScreen(null);
    }

    private void updateServer() {
        this.client.getNetworkHandler().sendPacket((Packet)new UpdateJigsawC2SPacket(this.jigsaw.getPos(), Identifier.of((String)this.nameField.getText()), Identifier.of((String)this.targetField.getText()), Identifier.of((String)this.poolField.getText()), this.finalStateField.getText(), this.joint, this.parseInt(this.selectionPriorityField.getText()), this.parseInt(this.placementPriorityField.getText())));
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException numberFormatException) {
            return 0;
        }
    }

    private void generate() {
        this.client.getNetworkHandler().sendPacket((Packet)new JigsawGeneratingC2SPacket(this.jigsaw.getPos(), this.generationDepth, this.keepJigsaws));
    }

    public void close() {
        this.onCancel();
    }

    protected void init() {
        boolean bl;
        this.poolField = new TextFieldWidget(this.textRenderer, this.width / 2 - 153, 20, 300, 20, POOL_TEXT);
        this.poolField.setMaxLength(128);
        this.poolField.setText(this.jigsaw.getPool().getValue().toString());
        this.poolField.setChangedListener(pool -> this.updateDoneButtonState());
        this.addSelectableChild((Element)this.poolField);
        this.nameField = new TextFieldWidget(this.textRenderer, this.width / 2 - 153, 55, 300, 20, NAME_TEXT);
        this.nameField.setMaxLength(128);
        this.nameField.setText(this.jigsaw.getName().toString());
        this.nameField.setChangedListener(name -> this.updateDoneButtonState());
        this.addSelectableChild((Element)this.nameField);
        this.targetField = new TextFieldWidget(this.textRenderer, this.width / 2 - 153, 90, 300, 20, TARGET_TEXT);
        this.targetField.setMaxLength(128);
        this.targetField.setText(this.jigsaw.getTarget().toString());
        this.targetField.setChangedListener(target -> this.updateDoneButtonState());
        this.addSelectableChild((Element)this.targetField);
        this.finalStateField = new TextFieldWidget(this.textRenderer, this.width / 2 - 153, 125, 300, 20, FINAL_STATE_TEXT);
        this.finalStateField.setMaxLength(256);
        this.finalStateField.setText(this.jigsaw.getFinalState());
        this.addSelectableChild((Element)this.finalStateField);
        this.selectionPriorityField = new TextFieldWidget(this.textRenderer, this.width / 2 - 153, 160, 98, 20, SELECTION_PRIORITY_TEXT);
        this.selectionPriorityField.setMaxLength(3);
        this.selectionPriorityField.setText(Integer.toString(this.jigsaw.getSelectionPriority()));
        this.selectionPriorityField.setTooltip(Tooltip.of((Text)SELECTION_PRIORITY_TOOLTIP));
        this.addSelectableChild((Element)this.selectionPriorityField);
        this.placementPriorityField = new TextFieldWidget(this.textRenderer, this.width / 2 - 50, 160, 98, 20, PLACEMENT_PRIORITY_TEXT);
        this.placementPriorityField.setMaxLength(3);
        this.placementPriorityField.setText(Integer.toString(this.jigsaw.getPlacementPriority()));
        this.placementPriorityField.setTooltip(Tooltip.of((Text)PLACEMENT_PRIORITY_TOOLTIP));
        this.addSelectableChild((Element)this.placementPriorityField);
        this.joint = this.jigsaw.getJoint();
        this.jointRotationButton = (CyclingButtonWidget)this.addDrawableChild((Element)CyclingButtonWidget.builder(JigsawBlockEntity.Joint::asText, (Object)this.joint).values((Object[])JigsawBlockEntity.Joint.values()).omitKeyText().build(this.width / 2 + 54, 160, 100, 20, JOINT_LABEL_TEXT, (button, joint) -> {
            this.joint = joint;
        }));
        this.jointRotationButton.active = bl = JigsawBlock.getFacing((BlockState)this.jigsaw.getCachedState()).getAxis().isVertical();
        this.jointRotationButton.visible = bl;
        this.addDrawableChild((Element)new /* Unavailable Anonymous Inner Class!! */);
        this.addDrawableChild((Element)CyclingButtonWidget.onOffBuilder((boolean)this.keepJigsaws).build(this.width / 2 - 50, 185, 100, 20, (Text)Text.translatable((String)"jigsaw_block.keep_jigsaws"), (button, keepJigsaws) -> {
            this.keepJigsaws = keepJigsaws;
        }));
        this.generateButton = (ButtonWidget)this.addDrawableChild((Element)ButtonWidget.builder((Text)Text.translatable((String)"jigsaw_block.generate"), button -> {
            this.onDone();
            this.generate();
        }).dimensions(this.width / 2 + 54, 185, 100, 20).build());
        this.doneButton = (ButtonWidget)this.addDrawableChild((Element)ButtonWidget.builder((Text)ScreenTexts.DONE, button -> this.onDone()).dimensions(this.width / 2 - 4 - 150, 210, 150, 20).build());
        this.addDrawableChild((Element)ButtonWidget.builder((Text)ScreenTexts.CANCEL, button -> this.onCancel()).dimensions(this.width / 2 + 4, 210, 150, 20).build());
        this.updateDoneButtonState();
    }

    protected void setInitialFocus() {
        this.setInitialFocus((Element)this.poolField);
    }

    public static boolean isValidId(String id) {
        return Identifier.tryParse((String)id) != null;
    }

    private void updateDoneButtonState() {
        boolean bl;
        this.doneButton.active = bl = JigsawBlockScreen.isValidId((String)this.nameField.getText()) && JigsawBlockScreen.isValidId((String)this.targetField.getText()) && JigsawBlockScreen.isValidId((String)this.poolField.getText());
        this.generateButton.active = bl;
    }

    public boolean deferSubtitles() {
        return true;
    }

    public void resize(int width, int height) {
        String string = this.nameField.getText();
        String string2 = this.targetField.getText();
        String string3 = this.poolField.getText();
        String string4 = this.finalStateField.getText();
        String string5 = this.selectionPriorityField.getText();
        String string6 = this.placementPriorityField.getText();
        int i = this.generationDepth;
        JigsawBlockEntity.Joint joint = this.joint;
        this.init(width, height);
        this.nameField.setText(string);
        this.targetField.setText(string2);
        this.poolField.setText(string3);
        this.finalStateField.setText(string4);
        this.generationDepth = i;
        this.joint = joint;
        this.jointRotationButton.setValue((Object)joint);
        this.selectionPriorityField.setText(string5);
        this.placementPriorityField.setText(string6);
    }

    public boolean keyPressed(KeyInput input) {
        if (super.keyPressed(input)) {
            return true;
        }
        if (this.doneButton.active && input.isEnter()) {
            this.onDone();
            return true;
        }
        return false;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawTextWithShadow(this.textRenderer, POOL_TEXT, this.width / 2 - 153, 10, -6250336);
        this.poolField.render(context, mouseX, mouseY, deltaTicks);
        context.drawTextWithShadow(this.textRenderer, NAME_TEXT, this.width / 2 - 153, 45, -6250336);
        this.nameField.render(context, mouseX, mouseY, deltaTicks);
        context.drawTextWithShadow(this.textRenderer, TARGET_TEXT, this.width / 2 - 153, 80, -6250336);
        this.targetField.render(context, mouseX, mouseY, deltaTicks);
        context.drawTextWithShadow(this.textRenderer, FINAL_STATE_TEXT, this.width / 2 - 153, 115, -6250336);
        this.finalStateField.render(context, mouseX, mouseY, deltaTicks);
        context.drawTextWithShadow(this.textRenderer, SELECTION_PRIORITY_TEXT, this.width / 2 - 153, 150, -6250336);
        this.placementPriorityField.render(context, mouseX, mouseY, deltaTicks);
        context.drawTextWithShadow(this.textRenderer, PLACEMENT_PRIORITY_TEXT, this.width / 2 - 50, 150, -6250336);
        this.selectionPriorityField.render(context, mouseX, mouseY, deltaTicks);
        if (JigsawBlock.getFacing((BlockState)this.jigsaw.getCachedState()).getAxis().isVertical()) {
            context.drawTextWithShadow(this.textRenderer, JOINT_LABEL_TEXT, this.width / 2 + 53, 150, -6250336);
        }
    }
}

