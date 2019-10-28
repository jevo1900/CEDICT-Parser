
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class CEDict {

    private final HashMap<String, CEWord> TRADITIONAL_DICT = new HashMap<>();

    private final HashMap<String, CEWord> SIMPLIFIED_DICT = new HashMap<>();

    File file;

    public CEDict(String filePath) {
        this.file = new File(filePath);
        try {
            Path path = Paths.get(filePath);
            Charset charset = StandardCharsets.UTF_8;

            String content = new String(Files.readAllBytes(path), charset);
            String[] lines = content.split("\n");

            for (String line : lines) {
                System.out.println(line);
                if (!line.startsWith("#")) {
                    CEWord w = new CEWord(line);
                    TRADITIONAL_DICT.put(w.TRADITIONAL, w);
                    SIMPLIFIED_DICT.put(w.SIMPLIFIED, w);
                }
            }

        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
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
    
    
}
