package com.ankoki.skjade;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import com.ankoki.skjade.api.NMS;
import com.ankoki.skjade.commands.SkJadeCmd;
import com.ankoki.skjade.hooks.elementals.EleClassInfo;
import com.ankoki.skjade.hooks.holograms.HoloClassInfo;
import com.ankoki.skjade.utils.Utils;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Logger;

//TODO make a seraliser for holograms so they are persistant over restart c:
public class SkJade extends JavaPlugin {

    private static boolean beta;
    private static SkJade instance;
    private static String version;
    private PluginManager pluginManager;
    private SkriptAddon addon;
    private Logger logger;
    private final int pluginId = 10131;
    private Metrics metrics;
    private static NMS nmsHandler;
    private static boolean nmsEnabled = false;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;
        pluginManager = this.getServer().getPluginManager();
        logger = this.getLogger();
        version = this.getDescription().getVersion();
        if (!isSkriptEnabled()) {
            logger.info("Skript wasn't found. Are you sure it's installed and up to date?");
            pluginManager.disablePlugin(this);
            return;
        }
        loadNMS();
        addon = Skript.registerAddon(this);
        this.loadElements();

        if (isPluginEnabled("ProtocolLib")) {
            logger.info("ProtocolLib was found! Enabling support");
            this.loadProtocolElements();
        }
        if (isPluginEnabled("HolographicDisplays")) {
            logger.info("HolographicDisplays was found! Enabling support");
            this.loadHDElements();
        }
        if (isPluginEnabled("Elementals")) {
            Plugin elementals = pluginManager.getPlugin("Elementals");
            assert elementals != null;
            if (Utils.checkPluginVersion(elementals, 1, 4)) {
                logger.info("Elementals was found! Enabling support");
                this.loadElementalsElements();
            } else {
                logger.info("Elementals was found! However it is an early version! Please upgrade to atleast 1.4.");
            }
        }
        if (version.endsWith("-beta")) {
            logger.warning("You are running on an unstable release and SkJade could potentionally not " +
                    "work correctly!");
            logger.warning("I recommend switching to a non-beta version of SkJade, especially if you're " +
                    "runninng on a production server, as data might be lost!");
        }
        metrics = new Metrics(this, pluginId);
        this.registerCommand();
        logger.info(String.format("SkJade v%s has been successfully enabled in %.2f seconds (%sms)",
                version, (float) System.currentTimeMillis() - start, System.currentTimeMillis() - start));
        /*
        //This isn't on github just yet so this will cause errors.
        UpdateChecker checker = new UpdateChecker("Ankoki-Dev", "SkJade");
        if (checker.isOutdated) {
            logger.info("You are not running the latest version of SkJade! Please update here:");
            logger.info("https://www.github.com/Ankoki-Dev/SkJade/releases/latest");
        }
         */
    }

    private void loadNMS() {
        String packageName = this.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);
        try {
            final Class<?> clazz = Class.forName("com.ankoki.skjade.nms." + version + ".NMSHandler");
            if (NMS.class.isAssignableFrom(clazz)) {
                nmsHandler = (NMS) clazz.getConstructor().newInstance();
                nmsEnabled = true;
                logger.info("NMS Support for " + version + " loaded!");
            } else {
                logger.severe("Could not find any NMS support for this version! Please note SkJade only supports the latest sub-version of each version.");
                logger.info("SkJade will remain enabled, however anything using NMS will not function!");
            }
        } catch (Exception ex) {
            logger.severe("Could not find any NMS support for this version! Please note SkJade only supports the latest sub-version of each version.");
            logger.info("SkJade will remain enabled, however anything using NMS will not function!");
        }
    }

    private boolean isSkriptEnabled() {
        Plugin skript = pluginManager.getPlugin("Skript");
        if (skript == null) return false;
        if (!skript.isEnabled()) return false;
        return Skript.isAcceptRegistrations();
    }

    private boolean isPluginEnabled(String pluginName) {
        Plugin plugin = pluginManager.getPlugin(pluginName);
        if (plugin == null) return false;
        return plugin.isEnabled();
    }

    private boolean loadElements() {
        try {
            addon.loadClasses("com.ankoki.skjade.elements"/*,
                    "expressions",
                    "effects",
                    "events",
                    "conditions"*/);
        } catch (IOException ex) {
            logger.info("Something went horribly wrong!");
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean loadHDElements() {
        try {
            addon.loadClasses("com.ankoki.skjade.hooks.holograms"/*,
                    "expressions",
                    "effects",
                    "conditions",
                    "events"*/);
            new HoloClassInfo();
        } catch (IOException ex) {
            logger.info("Something went horribly wrong!");
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean loadElementalsElements() {
        try {
            addon.loadClasses("com.ankoki.skjade.hooks.elementals"/*,
                    "expressions",
                    "effects",
                    "events",
                    "conditions"*/);
            new EleClassInfo();
        } catch (IOException ex) {
            logger.info("Something went horribly wrong!");
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean loadProtocolElements() {
        try {
            addon.loadClasses("com.ankoki.skjade.hooks.protocollib"/*,
                    "expressions",
                    "effects",
                    "events",
                    "conditions"*/);
        } catch (IOException ex) {
            logger.info("Something went horribly wrong!");
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    private void registerCommand() {
        this.getServer().getPluginCommand("skjade").setExecutor(new SkJadeCmd());
    }

    public static boolean isBeta() {
        return beta;
    }

    public static String getVersion() {
        return version;
    }

    public static SkJade getInstance() {
        return instance;
    }

    public static NMS getNMS() {
        return nmsHandler;
    }

    public static boolean isNmsEnabled() {
        return nmsEnabled;
    }
}
