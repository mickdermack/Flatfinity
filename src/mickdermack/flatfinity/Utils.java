/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mickdermack.flatfinity;

/**
 *
 * @author User
 */
public class Utils {
    public static int byteArraySumUntil(byte[] array, int until) {
        if (until<0) return 0;
        int sum = 0;
        for (int i=0;i<=until;i++)
            sum+=array[i];
        return sum;
    }

    public static int byteArraySum(byte[] array) {
        int sum = 0;
        for (int i=0;i<array.length;i++)
            sum+=array[i];
        return sum;
    }
}
