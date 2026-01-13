/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
class RealmsBackupInfoScreen.BackupInfoListEntry
extends AlwaysSelectedEntryListWidget.Entry<RealmsBackupInfoScreen.BackupInfoListEntry> {
    private static final Text TEMPLATE_NAME_TEXT = Text.translatable("mco.backup.entry.templateName");
    private static final Text GAME_DIFFICULTY_TEXT = Text.translatable("mco.backup.entry.gameDifficulty");
    private static final Text NAME_TEXT = Text.translatable("mco.backup.entry.name");
    private static final Text GAME_SERVER_VERSION_TEXT = Text.translatable("mco.backup.entry.gameServerVersion");
    private static final Text UPLOADED_TEXT = Text.translatable("mco.backup.entry.uploaded");
    private static final Text ENABLED_PACK_TEXT = Text.translatable("mco.backup.entry.enabledPack");
    private static final Text DESCRIPTION_TEXT = Text.translatable("mco.backup.entry.description");
    private static final Text GAME_MODE_TEXT = Text.translatable("mco.backup.entry.gameMode");
    private static final Text SEED_TEXT = Text.translatable("mco.backup.entry.seed");
    private static final Text WORLD_TYPE_TEXT = Text.translatable("mco.backup.entry.worldType");
    private static final Text UNDEFINED_TEXT = Text.translatable("mco.backup.entry.undefined");
    private final String key;
    private final String value;
    private final Text keyLabelText;
    private final Text valueText;

    public RealmsBackupInfoScreen.BackupInfoListEntry(String key, String value) {
        this.key = key;
        this.value = value;
        this.keyLabelText = this.getKeyLabel(key);
        this.valueText = RealmsBackupInfoScreen.this.getValueText(key, value);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        context.drawTextWithShadow(RealmsBackupInfoScreen.this.textRenderer, this.keyLabelText, this.getContentX(), this.getContentY(), -6250336);
        context.drawTextWithShadow(RealmsBackupInfoScreen.this.textRenderer, this.valueText, this.getContentX(), this.getContentY() + 12, -1);
    }

    private Text getKeyLabel(String key) {
        return switch (key) {
            case "template_name" -> TEMPLATE_NAME_TEXT;
            case "game_difficulty" -> GAME_DIFFICULTY_TEXT;
            case "name" -> NAME_TEXT;
            case "game_server_version" -> GAME_SERVER_VERSION_TEXT;
            case "uploaded" -> UPLOADED_TEXT;
            case "enabled_packs" -> ENABLED_PACK_TEXT;
            case "description" -> DESCRIPTION_TEXT;
            case "game_mode" -> GAME_MODE_TEXT;
            case "seed" -> SEED_TEXT;
            case "world_type" -> WORLD_TYPE_TEXT;
            default -> UNDEFINED_TEXT;
        };
    }

    @Override
    public Text getNarration() {
        return Text.translatable("narrator.select", this.key + " " + this.value);
    }
}
