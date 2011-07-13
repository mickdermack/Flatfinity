/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mickdermack.flatfinity;

import java.util.Map;
import java.util.Random;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.config.ConfigurationNode;

/**
 *
 * @author User
 */
public class FlatfinityGenerator extends ChunkGenerator {
    private byte[] heights;
    private byte[] mats;
    private byte[] chunk;


    public FlatfinityGenerator(Map<String, ConfigurationNode> layers) {
        heights = new byte[layers.size()];
        mats = new byte[layers.size()];

        for (int i=1;i<=layers.size();i++) {
            heights[i-1] = (byte)layers.get(Integer.toString(i)).getInt("height", 0);
            mats[i-1] = (byte)layers.get(Integer.toString(i)).getInt("material", 0);
        }

        chunk = new byte[32768];
        for (int bx = 0; bx < 16; bx++) {
            for (int bz = 0; bz < 16; bz++) {
                for (int by = 0; by < 128; by++) {
                    for (int i=0;i<heights.length&&i<mats.length;i++) {
                        if (by<Utils.byteArraySumUntil(heights, i)) {
                            chunk[(bx * 16 + bz) * 128 + by] = mats[i];
                            break;
                        }
                    }

                }
            }
        }
    }

    public byte[] generate(World world, Random random, int x, int z) {
        return chunk.clone(); // Forgetting the .clone() has interesting effects
    }

    @Override
    public boolean canSpawn(World w, int x, int z) {
        return true;
    }
}
