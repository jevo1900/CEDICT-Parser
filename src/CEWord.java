
public class CEWord {

    public final String TRADITIONAL;
    public final String SIMPLIFIED;
    public final String PINYIN;
    public final String[] ENGLISH;

    public CEWord(String lines) {
        String caracteres = lines.substring(0, lines.indexOf("["));
        String pinyin_eng = lines.substring(lines.indexOf("["));
        String[] ar = pinyin_eng.split(" /");
        String[] chars=caracteres.split(" ");
        this.TRADITIONAL = chars[0];
        this.SIMPLIFIED = chars[1];
        this.PINYIN = ar[0];
        this.ENGLISH = ar[1].split("/");
    }

    public void print() {
        System.out.println("Chino Tradicional: "+TRADITIONAL);
        System.out.println("Chino Simplificado: "+SIMPLIFIED);
        System.out.println("Chino Pinyin: "+PINYIN);
        System.out.println("Ingles: ");
        for (String string : ENGLISH) {
            System.out.println(string);
        }
    }
//
//    public String getTRADITIONAL() {
//        return TRADITIONAL;
//    }
//
//    public String getSIMPLIFIED() {
//        return SIMPLIFIED;
//    }
//
//    public String getPINYIN() {
//        return PINYIN;
//    }
//
//    public String[] getENGLISH() {
//        return ENGLISH;
//    }
    
    
    
}
