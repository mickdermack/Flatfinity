/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mickdermack.flatfinity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author mickdermack
 */
public class Flatfinity extends JavaPlugin {
    private static final Logger log = Logger.getLogger("Minecraft");
    private ChunkGenerator gen;
    private Properties props = new Properties();
    private byte[] mats = new byte[3];
    private byte[] maxs = new byte[3];
    private PluginManager pm;
    private final FlatfinityWeatherListener weatherListener = new FlatfinityWeatherListener(this);
    private boolean noWeather = false;

    public void onEnable(){
        File propFile = new File("plugins"+File.separator+"Flatfinity"+File.separator+"flatfinity.properties");
        boolean usingdef = false;
        File dir = new File("plugins"+File.separator+"Flatfinity");
        boolean mkdirfailed = false;
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                log.warning("[Flatfinity] Failed to create folder");
                mkdirfailed = true;
                usingdef = true;
            }
        }
        if (!propFile.exists()&&!mkdirfailed) {
            try {
                log.warning("[Flatfinity] Properties file not present");
                propFile.createNewFile();
                FileWriter out = new FileWriter(propFile);
                props.setProperty("layer1-mat", "7");
                props.setProperty("layer1-max", "0");
                props.setProperty("layer2-mat", "3");
                props.setProperty("layer2-max", "5");
                props.setProperty("layer3-mat", "2");
                props.setProperty("layer3-max", "6");
                props.setProperty("noweather", "false");
                props.store(out, "layer<x>-mat is the Minecraft block ID\nlayer<x>-max is the maximum height of the layer\nlayer1 is the lowest, layer3 the highest\nIf you don't follow that or specify values above 127, behaviour is undefined, most likely you will spawn below the surface, too high and/or get Exceptions\nnoweather disables weather in the Flatfinity map\nChanges in this files only apply to newly generated chunks");
                log.info("[Flatfinity] Created properties file");
            } catch (IOException e) {
                log.warning("[Flatfinity] Failed to create the configuration file");
                e.printStackTrace();
            }
            usingdef=true;
        } else if (!propFile.canRead()) {
            log.warning("[Flatfinity] Cannot read the properties file: No permission");
            usingdef=true;
        } else if (!propFile.isFile()) {
            log.warning("[Flatfinity] Cannot read the properties file: flatfinity.properties is not a file");
            usingdef=true;
        } else {
            try {
                Reader is = new FileReader(propFile);
                props.load(is);
            } catch (Exception e) {
                log.warning("[Flatfinity] Failed to load properties");
                e.printStackTrace();
                usingdef = true;
            }
        }
        if (usingdef)
            log.info("[Flatfinity] Using default values");
        int[] defaultmats = {7,3,2};
        int[] defaultmaxs = {0,5,6};
        for (int i=1;i<=3;i++) {
            try {
                mats[i-1] = Byte.parseByte(props.getProperty("layer"+i+"-mat", Integer.toString(defaultmats[i-1])));
            } catch (NumberFormatException e) {
                log.log(Level.SEVERE, "[Flatfinity] Failed to parse the properties file: Not a number or too high: {0}", props.getProperty("layer" + i + "-mat"));
                return;
            }
        }
        for (int i=1;i<=3;i++) {
            try {
                maxs[i-1] = Byte.parseByte(props.getProperty("layer"+i+"-max", Integer.toString(defaultmaxs[i-1])));
            } catch (NumberFormatException e) {
                log.log(Level.SEVERE, "[Flatfinity] Failed to parse the properties file: Not a number or too high: {0}", props.getProperty("layer" + i + "-max"));
                return;
            }
        }
        gen = new FlatfinityGenerator(maxs, mats);
        World w = getServer().createWorld("flatfinity", World.Environment.NORMAL, gen);
        w.setSpawnLocation(0, maxs[2], 0);
        pm = this.getServer().getPluginManager();
        noWeather = Boolean.parseBoolean(props.getProperty("noweather","false"));
        if (noWeather) {
            pm.registerEvent(Event.Type.WEATHER_CHANGE, weatherListener, Event.Priority.Normal, this);
            w.setStorm(false);
        }
        log.info("Flatfinity has been enabled.");
    }



    public void onDisable(){
        log.info("Flatfinity has been disabled.");
    }
}
