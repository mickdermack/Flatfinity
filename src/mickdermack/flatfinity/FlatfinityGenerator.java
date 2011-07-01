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
    private byte[] heights;
    private byte[] mats;

    public FlatfinityGenerator(byte[] maxs, byte[] mats) {
        this.heights = maxs;
        this.mats = mats;
    }

    public byte[] generate(World world, Random random, int x, int z) {
        byte[] result = new byte[32768];
        for (int bx = 0; bx < 16; bx++) {
            for (int bz = 0; bz < 16; bz++) {
                for (int by = 0; by < 128; by++) {
                    for (int i=0;i<heights.length&&i<mats.length;i++) {
                        if (by<Utils.byteArraySumUntil(heights, i)) {
//                            Flatfinity.log.info(Integer.toString(Utils.byteArraySumUntil(heights, i)));
                            result[(bx * 16 + bz) * 128 + by] = mats[i];
//                            Flatfinity.log.info(mats[i]+" at "+bx+" "+by+" "+bz);
//                            Flatfinity.log.info("Block y "+by+" is lower than "+(Utils.byteArraySumUntil(heights, i)-1)+" ("+i+""+mats[i]+")");
                            break;
//                        } else {
//                            Flatfinity.log.info("Block y "+by+" is higher than or equal "+(Utils.byteArraySumUntil(heights, i)-1)+" ("+i+")");
                        }
                    }
//                    else if (by<heights[1])
//                        result[(bx * 16 + bz) * 128 + by] = mats[1];
//                    else if (by<=heights[2])
//                        result[(bx * 16 + bz) * 128 + by] = mats[2];
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
