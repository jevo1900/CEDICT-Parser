
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Usuario
 */
public class Main {

    public static void main(String[] args) {
        String str = "兰陵是某重点大学艺术系大三学生,今年19岁";

        segmenter s = new segmenter(segmenter.BOTH, false);
        s.segmentFile("file.txt","UTF-8");
        s.printDebug(str);
        segmenter.printHelp();
    }

}
