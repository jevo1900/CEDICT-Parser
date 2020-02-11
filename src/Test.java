/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jvazqoter
 */
public class Test {
    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
               int code = (i * 16 + j);
                System.out.printf("\u001B["+code+"m %-4d",code);
            }
            System.out.println("");
        }
    }
}
