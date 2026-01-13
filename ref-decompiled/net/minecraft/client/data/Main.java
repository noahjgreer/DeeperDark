/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  joptsimple.AbstractOptionSpec
 *  joptsimple.ArgumentAcceptingOptionSpec
 *  joptsimple.OptionParser
 *  joptsimple.OptionSet
 *  joptsimple.OptionSpec
 *  joptsimple.OptionSpecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.Bootstrap
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.ClientBootstrap
 *  net.minecraft.client.data.AtlasDefinitionProvider
 *  net.minecraft.client.data.EquipmentAssetProvider
 *  net.minecraft.client.data.Main
 *  net.minecraft.client.data.ModelProvider
 *  net.minecraft.client.data.WaypointStyleProvider
 *  net.minecraft.data.DataGenerator
 *  net.minecraft.data.DataGenerator$Pack
 *  net.minecraft.obfuscate.DontObfuscate
 *  net.minecraft.util.Util
 *  net.minecraft.util.annotation.SuppressLinter
 */
package net.minecraft.client.data;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBootstrap;
import net.minecraft.client.data.AtlasDefinitionProvider;
import net.minecraft.client.data.EquipmentAssetProvider;
import net.minecraft.client.data.ModelProvider;
import net.minecraft.client.data.WaypointStyleProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.SuppressLinter;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class Main {
    @DontObfuscate
    @SuppressLinter(reason="System.out needed before bootstrap")
    public static void main(String[] args) throws IOException {
        SharedConstants.createGameVersion();
        OptionParser optionParser = new OptionParser();
        AbstractOptionSpec optionSpec = optionParser.accepts("help", "Show the help menu").forHelp();
        OptionSpecBuilder optionSpec2 = optionParser.accepts("client", "Include client generators");
        OptionSpecBuilder optionSpec3 = optionParser.accepts("all", "Include all generators");
        ArgumentAcceptingOptionSpec optionSpec4 = optionParser.accepts("output", "Output folder").withRequiredArg().defaultsTo((Object)"generated", (Object[])new String[0]);
        OptionSet optionSet = optionParser.parse(args);
        if (optionSet.has((OptionSpec)optionSpec) || !optionSet.hasOptions()) {
            optionParser.printHelpOn((OutputStream)System.out);
            return;
        }
        Path path = Paths.get((String)optionSpec4.value(optionSet), new String[0]);
        boolean bl = optionSet.has((OptionSpec)optionSpec3);
        boolean bl2 = bl || optionSet.has((OptionSpec)optionSpec2);
        Bootstrap.initialize();
        ClientBootstrap.initialize();
        DataGenerator dataGenerator = new DataGenerator(path, SharedConstants.getGameVersion(), true);
        Main.create((DataGenerator)dataGenerator, (boolean)bl2);
        dataGenerator.run();
        Util.shutdownExecutors();
    }

    public static void create(DataGenerator dataGenerator, boolean includeClient) {
        DataGenerator.Pack pack = dataGenerator.createVanillaPack(includeClient);
        pack.addProvider(ModelProvider::new);
        pack.addProvider(EquipmentAssetProvider::new);
        pack.addProvider(WaypointStyleProvider::new);
        pack.addProvider(AtlasDefinitionProvider::new);
    }
}

