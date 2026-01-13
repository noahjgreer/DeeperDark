/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.UserApiService
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.ConfirmScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.session.report.AbuseReport
 *  net.minecraft.client.session.report.AbuseReportContext
 *  net.minecraft.client.session.report.AbuseReportSender
 *  net.minecraft.client.session.report.ReporterEnvironment
 *  net.minecraft.client.session.report.log.ChatLog
 *  net.minecraft.text.Text
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.session.report;

import com.mojang.authlib.minecraft.UserApiService;
import java.util.Objects;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.session.report.AbuseReport;
import net.minecraft.client.session.report.AbuseReportSender;
import net.minecraft.client.session.report.ReporterEnvironment;
import net.minecraft.client.session.report.log.ChatLog;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public final class AbuseReportContext {
    private static final int MAX_LOGS = 1024;
    private final AbuseReportSender sender;
    private final ReporterEnvironment environment;
    private final ChatLog chatLog;
    private @Nullable AbuseReport draft;

    public AbuseReportContext(AbuseReportSender sender, ReporterEnvironment environment, ChatLog chatLog) {
        this.sender = sender;
        this.environment = environment;
        this.chatLog = chatLog;
    }

    public static AbuseReportContext create(ReporterEnvironment environment, UserApiService userApiService) {
        ChatLog chatLog = new ChatLog(1024);
        AbuseReportSender abuseReportSender = AbuseReportSender.create((ReporterEnvironment)environment, (UserApiService)userApiService);
        return new AbuseReportContext(abuseReportSender, environment, chatLog);
    }

    public void tryShowDraftScreen(MinecraftClient client, Screen parent, Runnable callback, boolean quit) {
        if (this.draft != null) {
            AbuseReport abuseReport = this.draft.copy();
            client.setScreen((Screen)new ConfirmScreen(confirmed -> {
                this.setDraft(null);
                if (confirmed) {
                    client.setScreen(abuseReport.createReportScreen(parent, this));
                } else {
                    callback.run();
                }
            }, (Text)Text.translatable((String)(quit ? "gui.abuseReport.draft.quittotitle.title" : "gui.abuseReport.draft.title")), (Text)Text.translatable((String)(quit ? "gui.abuseReport.draft.quittotitle.content" : "gui.abuseReport.draft.content")), (Text)Text.translatable((String)"gui.abuseReport.draft.edit"), (Text)Text.translatable((String)"gui.abuseReport.draft.discard")));
        } else {
            callback.run();
        }
    }

    public AbuseReportSender getSender() {
        return this.sender;
    }

    public ChatLog getChatLog() {
        return this.chatLog;
    }

    public boolean environmentEquals(ReporterEnvironment environment) {
        return Objects.equals(this.environment, environment);
    }

    public void setDraft(@Nullable AbuseReport draft) {
        this.draft = draft;
    }

    public boolean hasDraft() {
        return this.draft != null;
    }

    public boolean draftPlayerUuidEquals(UUID uuid) {
        return this.hasDraft() && this.draft.playerUuidEquals(uuid);
    }
}

