/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import com.mojang.datafixers.util.Either;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.WorldTemplate;
import net.minecraft.client.realms.dto.WorldTemplatePaginatedList;
import net.minecraft.client.realms.util.TextRenderingUtils;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Urls;

@Environment(value=EnvType.CLIENT)
class RealmsSelectWorldTemplateScreen.1
extends Thread {
    final /* synthetic */ WorldTemplatePaginatedList field_20091;

    RealmsSelectWorldTemplateScreen.1(String string, WorldTemplatePaginatedList worldTemplatePaginatedList) {
        this.field_20091 = worldTemplatePaginatedList;
        super(string);
    }

    @Override
    public void run() {
        WorldTemplatePaginatedList worldTemplatePaginatedList = this.field_20091;
        RealmsClient realmsClient = RealmsClient.create();
        while (worldTemplatePaginatedList != null) {
            Either<WorldTemplatePaginatedList, Exception> either = RealmsSelectWorldTemplateScreen.this.fetchWorldTemplates(worldTemplatePaginatedList, realmsClient);
            worldTemplatePaginatedList = RealmsSelectWorldTemplateScreen.this.client.submit(() -> {
                if (either.right().isPresent()) {
                    LOGGER.error("Couldn't fetch templates", (Throwable)either.right().get());
                    if (RealmsSelectWorldTemplateScreen.this.templateList.isEmpty()) {
                        RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.decompose(I18n.translate("mco.template.select.failure", new Object[0]), new TextRenderingUtils.LineSegment[0]);
                    }
                    return null;
                }
                WorldTemplatePaginatedList worldTemplatePaginatedList = (WorldTemplatePaginatedList)either.left().get();
                for (WorldTemplate worldTemplate : worldTemplatePaginatedList.templates()) {
                    RealmsSelectWorldTemplateScreen.this.templateList.addEntry(worldTemplate);
                }
                if (worldTemplatePaginatedList.templates().isEmpty()) {
                    if (RealmsSelectWorldTemplateScreen.this.templateList.isEmpty()) {
                        String string = I18n.translate("mco.template.select.none", "%link");
                        TextRenderingUtils.LineSegment lineSegment = TextRenderingUtils.LineSegment.link(I18n.translate("mco.template.select.none.linkTitle", new Object[0]), Urls.REALMS_CONTENT_CREATOR.toString());
                        RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.decompose(string, lineSegment);
                    }
                    return null;
                }
                return worldTemplatePaginatedList;
            }).join();
        }
    }
}
