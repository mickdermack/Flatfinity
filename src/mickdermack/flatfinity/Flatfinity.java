/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mickdermack.flatfinity;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

/**
 *
 * @author mickdermack
 */
public class Flatfinity extends JavaPlugin {
    private static final Logger log = Logger.getLogger("Minecraft");
    private ChunkGenerator gen;
    private Properties props = new Properties();
    private Configuration conf;
    private PluginManager pm;
    private final FlatfinityWeatherListener weatherListener = new FlatfinityWeatherListener(this);
    private boolean noWeather = false;
    private byte height;

    private void logSevere(String msg) {
        log.severe("[Flatfinity] "+msg+" Aborting.");
    }

    public void onEnable() {
        boolean dontrun = false;
        File ymlFile = new File("plugins"+File.separator+"Flatfinity"+File.separator+"flatfinity.yml");
        conf = new Configuration(ymlFile);
        File dir = new File("plugins"+File.separator+"Flatfinity");
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                logSevere("Failed to create folder.");
                return;
            }
        }

        if (!ymlFile.exists()) {
            conf.setProperty("layers.1.material", 7);
            conf.setProperty("layers.1.height", 1);
            conf.setProperty("layers.2.material", 3);
            conf.setProperty("layers.2.height", 5);
            conf.setProperty("layers.3.material", 2);
            conf.setProperty("layers.3.height", 1);

            try {
                ymlFile.createNewFile();
                if (!conf.save())
                    throw new IOException("Configuration.save() failed");
            } catch (Exception e) {
                logSevere("Failed to create config file \""+e.getMessage()+"\".");
                return;
            }
        }

        conf.load();

        if (conf.getNode("layers")==null) {
            logSevere("Configuration node \"layers\" not found or empty. Is flatfinity.yml valid?");
            return;
        }

        Map<String, ConfigurationNode> layers = conf.getNodes("layers");
        height = 0;
        for (int i=1;i<layers.size();i++) {
            if (!layers.containsKey(Integer.toString(i))) {
                logSevere("Invalid config file. layers contains non-numeric nodes or nodes lower or equal to zero.");
                return;
            }
            if (layers.get(Integer.toString(i)).getProperty("height")==null||layers.get(Integer.toString(i)).getProperty("material")==null) {
                logSevere("Definition of layer "+i+" is missing height or material.");
                return;
            }
            int check = layers.get(Integer.toString(i)).getInt("height", 0);
            if (check<=0||check>=128) {
                logSevere("Layer "+i+" is too high (>=128) or too low (<=0).");
                return;
            }
            height += check;
        }

        if (dontrun)
            return;

//        byte[] defaultmats = {7,3,2};
//        byte[] defaultheights = {1,5,1};
//        if (usingdef) {
//            log.info("[Flatfinity] Using default values");
//            mats = defaultmats;
//            heights = defaultheights;
//        }

        gen = new FlatfinityGenerator(layers);
        World w = getServer().createWorld("flatfinity", World.Environment.NORMAL, gen);
        w.setSpawnLocation(0, height, 0);
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
