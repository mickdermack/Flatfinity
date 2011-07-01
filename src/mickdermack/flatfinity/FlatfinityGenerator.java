/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mickdermack.flatfinity;

import java.util.Random;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

/**
 *
 * @author User
 */
public class FlatfinityGenerator extends ChunkGenerator {
    private byte[] maxs;
    private byte[] mats;

    public FlatfinityGenerator(byte[] maxs, byte[] mats) {
        this.maxs = maxs;
        this.mats = mats;
    }

    public byte[] generate(World world, Random random, int x, int z) {
        byte[] result = new byte[32768];
        for (int bx = 0; bx < 16; bx++) {
            for (int bz = 0; bz < 16; bz++) {
                for (int by = 0; by < 128; by++) {
                    if (by<=maxs[0])
                        result[(bx * 16 + bz) * 128 + by] = mats[0];
                    else if (by<=maxs[1])
                        result[(bx * 16 + bz) * 128 + by] = mats[1];
                    else if (by<=maxs[2])
                        result[(bx * 16 + bz) * 128 + by] = mats[2];
                }
            }
        }
        return result;
    }

    @Override
    public boolean canSpawn(World w, int x, int z) {
        return true;
    }
}
