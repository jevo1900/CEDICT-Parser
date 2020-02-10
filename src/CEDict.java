
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;

public class CEDict {

    private final HashMap<String, CEWord> TRADITIONAL_DICT = new HashMap<>();

    private final HashMap<String, CEWord> SIMPLIFIED_DICT = new HashMap<>();

    File file;

    public CEDict(String filePath) {
        Charset charset = StandardCharsets.UTF_8;
        String dataline;
        try {
            InputStream setdata = getClass().getResourceAsStream(filePath);
            try (BufferedReader in = new BufferedReader(new InputStreamReader(setdata, charset))) {
                while ((dataline = in.readLine()) != null) {
                    if ((dataline.startsWith("#")) || (dataline.length() == 0)) {
                        continue;
                    }
                    CEWord w = new CEWord(dataline);
                    TRADITIONAL_DICT.put(w.TRADITIONAL, w);
                    SIMPLIFIED_DICT.put(w.SIMPLIFIED, w);
                }
            }
        } catch (IOException e) {
            System.err.println("Exception loading data file" + filePath + " " + e);
        }

    }

    public final CEWord getTRADITIONAL(String key) {
        if (this.TRADITIONAL_DICT.containsKey(key)) {
            return this.TRADITIONAL_DICT.get(key);
        }
        return null;
    }

    public final CEWord getSIMPLIFIED(String key) {
        if (this.SIMPLIFIED_DICT.containsKey(key)) {
            return this.SIMPLIFIED_DICT.get(key);
        }
        return null;
    }

    public final Set<String> getDictTRADITIONAL() {
        return TRADITIONAL_DICT.keySet();
    }

    public final Set<String> getDictSIMPLIFIED() {
        return SIMPLIFIED_DICT.keySet();
    }

}
