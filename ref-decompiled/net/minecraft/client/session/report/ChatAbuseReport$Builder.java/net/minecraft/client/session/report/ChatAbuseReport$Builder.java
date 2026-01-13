/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.minecraft.report.AbuseReport
 *  com.mojang.authlib.minecraft.report.AbuseReportLimits
 *  com.mojang.authlib.minecraft.report.ReportChatMessage
 *  com.mojang.authlib.minecraft.report.ReportEvidence
 *  com.mojang.authlib.minecraft.report.ReportedEntity
 *  com.mojang.datafixers.util.Either
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.session.report;

import com.google.common.collect.Lists;
import com.mojang.authlib.minecraft.report.AbuseReport;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.minecraft.report.ReportChatMessage;
import com.mojang.authlib.minecraft.report.ReportEvidence;
import com.mojang.authlib.minecraft.report.ReportedEntity;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.report.AbuseReport;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.AbuseReportType;
import net.minecraft.client.session.report.ChatAbuseReport;
import net.minecraft.client.session.report.ContextMessageCollector;
import net.minecraft.client.session.report.log.ReceivedMessage;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageLink;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.util.Nullables;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class ChatAbuseReport.Builder
extends AbuseReport.Builder<ChatAbuseReport> {
    public ChatAbuseReport.Builder(ChatAbuseReport report, AbuseReportLimits limits) {
        super(report, limits);
    }

    public ChatAbuseReport.Builder(UUID reportedPlayerUuid, AbuseReportLimits limits) {
        super(new ChatAbuseReport(UUID.randomUUID(), Instant.now(), reportedPlayerUuid), limits);
    }

    public IntSet getSelectedMessages() {
        return ((ChatAbuseReport)this.report).selectedMessages;
    }

    public void toggleMessageSelection(int index) {
        ((ChatAbuseReport)this.report).toggleMessageSelection(index, this.limits);
    }

    public boolean isMessageSelected(int index) {
        return ((ChatAbuseReport)this.report).selectedMessages.contains(index);
    }

    @Override
    public boolean hasEnoughInfo() {
        return StringUtils.isNotEmpty((CharSequence)this.getOpinionComments()) || !this.getSelectedMessages().isEmpty() || this.getReason() != null;
    }

    @Override
    public  @Nullable AbuseReport.ValidationError validate() {
        if (((ChatAbuseReport)this.report).selectedMessages.isEmpty()) {
            return AbuseReport.ValidationError.NO_REPORTED_MESSAGES;
        }
        if (((ChatAbuseReport)this.report).selectedMessages.size() > this.limits.maxReportedMessageCount()) {
            return AbuseReport.ValidationError.TOO_MANY_MESSAGES;
        }
        if (((ChatAbuseReport)this.report).reason == null) {
            return AbuseReport.ValidationError.NO_REASON;
        }
        if (((ChatAbuseReport)this.report).opinionComments.length() > this.limits.maxOpinionCommentsLength()) {
            return AbuseReport.ValidationError.COMMENTS_TOO_LONG;
        }
        return super.validate();
    }

    @Override
    public Either<AbuseReport.ReportWithId, AbuseReport.ValidationError> build(AbuseReportContext context) {
        AbuseReport.ValidationError validationError = this.validate();
        if (validationError != null) {
            return Either.right((Object)validationError);
        }
        String string = Objects.requireNonNull(((ChatAbuseReport)this.report).reason).getId();
        ReportEvidence reportEvidence = this.collectEvidences(context);
        ReportedEntity reportedEntity = new ReportedEntity(((ChatAbuseReport)this.report).reportedPlayerUuid);
        AbuseReport abuseReport = AbuseReport.chat((String)((ChatAbuseReport)this.report).opinionComments, (String)string, (ReportEvidence)reportEvidence, (ReportedEntity)reportedEntity, (Instant)((ChatAbuseReport)this.report).currentTime);
        return Either.left((Object)new AbuseReport.ReportWithId(((ChatAbuseReport)this.report).reportId, AbuseReportType.CHAT, abuseReport));
    }

    private ReportEvidence collectEvidences(AbuseReportContext context) {
        ArrayList list = new ArrayList();
        ContextMessageCollector contextMessageCollector = new ContextMessageCollector(this.limits.leadingContextMessageCount());
        contextMessageCollector.add(context.getChatLog(), (IntCollection)((ChatAbuseReport)this.report).selectedMessages, (index, message) -> list.add(this.toReportChatMessage(message, this.isMessageSelected(index))));
        return new ReportEvidence(Lists.reverse(list));
    }

    private ReportChatMessage toReportChatMessage(ReceivedMessage.ChatMessage message, boolean selected) {
        MessageLink messageLink = message.message().link();
        MessageBody messageBody = message.message().signedBody();
        List<ByteBuffer> list = messageBody.lastSeenMessages().entries().stream().map(MessageSignatureData::toByteBuffer).toList();
        ByteBuffer byteBuffer = Nullables.map(message.message().signature(), MessageSignatureData::toByteBuffer);
        return new ReportChatMessage(messageLink.index(), messageLink.sender(), messageLink.sessionId(), messageBody.timestamp(), messageBody.salt(), list, messageBody.content(), byteBuffer, selected);
    }

    public ChatAbuseReport.Builder copy() {
        return new ChatAbuseReport.Builder(((ChatAbuseReport)this.report).copy(), this.limits);
    }
}
