/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mickdermack.flatfinity;

import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.weather.WeatherListener;

/**
 *
 * @author User
 */
class FlatfinityWeatherListener extends WeatherListener {
    private Flatfinity plugin;

    public FlatfinityWeatherListener(Flatfinity instance) {
        plugin = instance;
    }

    @Override
    public void onWeatherChange(WeatherChangeEvent evt) {
        if (evt.toWeatherState())
            evt.setCancelled(true);
    }

}
