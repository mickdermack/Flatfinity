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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author mickdermack
 */
public class Flatfinity extends JavaPlugin {
    static final Logger log = Logger.getLogger("Minecraft");
    private ChunkGenerator gen;
    private Properties props = new Properties();
    private byte[] mats = new byte[3];
    private byte[] heights = new byte[3];
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
                props.setProperty("layer1-height", "1");
                props.setProperty("layer2-mat", "3");
                props.setProperty("layer2-height", "5");
                props.setProperty("layer3-mat", "2");
                props.setProperty("layer3-height", "1");
                props.setProperty("noweather", "false");
                props.store(out, "layer<x>-mat is the Minecraft block ID\nlayer<x>-height is the height of the layer\nlayer1 is the lowest, layer3 the highest\nIf you don't follow that or specify values above 127, behaviour is undefined, most likely you will spawn below the surface, too high and/or get Exceptions\nnoweather disables weather in the Flatfinity map\nChanges in this files only apply to newly generated chunks");
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
        int[] defaultheights = {1,5,1};
        for (int i=1;i<=3;i++) {
            try {
                mats[i-1] = Byte.parseByte(props.getProperty("layer"+i+"-mat", Integer.toString(defaultmats[i-1])));
            } catch (NumberFormatException e) {
                log.log(Level.WARNING, "[Flatfinity] Failed to parse the properties file: Not a number or too high: {0}", props.getProperty("layer" + i + "-mat"));
                e.printStackTrace();
                usingdef = true;
            }
        }
        for (int i=1;i<=3;i++) {
            try {
                heights[i-1] = Byte.parseByte(props.getProperty("layer"+i+"-height", Integer.toString(defaultheights[i-1])));
            } catch (NumberFormatException e) {
                log.log(Level.WARNING, "[Flatfinity] Failed to parse the properties file: Not a number or too high: {0}", props.getProperty("layer" + i + "-max"));
                e.printStackTrace();
                usingdef = true;
            }
        }
        gen = new FlatfinityGenerator(heights, mats);
        World w = getServer().createWorld("flatfinity", World.Environment.NORMAL, gen);
        w.setSpawnLocation(0, heights[0]+heights[1]+heights[2], 0);
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
