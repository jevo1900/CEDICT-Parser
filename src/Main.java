
import java.util.Set;

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

    static Segmenter s = new Segmenter(Segmenter.SIMP, true);
    static CEDict ced = new CEDict("dictionary/cedict_ts.u8");

    public static void main(String[] args) {

        String str = "我不能学魔法，但是我有农场，你敢攻打我？我放虫子吃光你的军粮，吃光你的庄稼，把你们的水源都下上杀虫剂，除草剂，给你的地上都种上杂草，我看你还敢来攻打我。";

        proc(str);

    }

    public static void proc(String str) {
        String segmentado = s.segmentLine(str, " ");
        for (String value : segmentado.split(" ")) {
            CEWord word = ced.getSIMPLIFIED(value);
            try {
                word.print();
                System.out.println("--------------------");
            } catch (NullPointerException ex) {
                altProc(value);
            }
        }
    }

    public static void altProc(String str) {
        Set<String> dictSIMPLIFIED = ced.getDictSIMPLIFIED();
        String replace = null;
        for (String string : dictSIMPLIFIED) {
            if ((str).startsWith(string)) {
                String firstChar = string;
                CEWord word = ced.getSIMPLIFIED(firstChar);
                try {
                    word.print();
                    System.out.println("--------------------");
                } catch (NullPointerException ex) {
                    altProc(firstChar);
                }
                String lastChar = (str).replace(string, "");
                word = ced.getSIMPLIFIED(lastChar);
                try {
                    word.print();
                    System.out.println("--------------------");
                } catch (NullPointerException ex) {
                    altProc(lastChar);
                }
            }
        }
    }

}
