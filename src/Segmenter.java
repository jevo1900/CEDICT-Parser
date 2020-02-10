import java.lang.*;
import java.io.*;
import java.util.*;

/* Written by Erik Peterson
   erik AT mandarintools.com
   Last modified Jan. 13, 2004
*/

public class Segmenter {
    private TreeMap zhwords;
    private TreeSet csurname, cforeign, cnumbers, cnotname;
    private String debugencoding;

    private boolean debug;

    // Char form
    public final static int TRAD = 0;
    public final static int SIMP = 1;
    public final static int BOTH = 2;

    // Charform is TRAD, SIMP or BOTH
    public Segmenter(int charform, boolean loadwordfile) {
	debug = false;
	debugencoding = "UTF-8";

	int count = 0;

	//int treelevel;

	csurname = new TreeSet();
	cforeign = new TreeSet();
	cnumbers = new TreeSet();
	cnotname = new TreeSet();

	if (charform == SIMP) {
	    loadset(cnumbers, "data/snumbers_u8.txt");
	    loadset(cforeign, "data/sforeign_u8.txt");
	    loadset(csurname, "data/ssurname_u8.txt");
	    loadset(cnotname, "data/snotname_u8.txt");
	} else if (charform == TRAD) {
	    loadset(cnumbers, "data/tnumbers_u8.txt");
	    loadset(cforeign, "data/tforeign_u8.txt");
	    loadset(csurname, "data/tsurname_u8.txt");
	    loadset(cnotname, "data/tnotname_u8.txt");
	} else {  // BOTH
	    loadset(cnumbers, "data/snumbers_u8.txt");
	    loadset(cforeign, "data/sforeign_u8.txt");
	    loadset(csurname, "data/ssurname_u8.txt");
	    loadset(cnotname, "data/snotname_u8.txt");
	    loadset(cnumbers, "data/tnumbers_u8.txt");
	    loadset(cforeign, "data/tforeign_u8.txt");
	    loadset(csurname, "data/tsurname_u8.txt");
	    loadset(cnotname, "data/tnotname_u8.txt");
	}

	//zhwords = new Hashtable(120000);
	zhwords = new TreeMap();
	
	if (!loadwordfile) {
	    return;
	}

	String newword = null;
	try {
	    InputStream worddata = null;
	    if (charform == SIMP) {
		worddata = getClass().getResourceAsStream("dictionary/simplexu8.txt");
	    } else if (charform == TRAD) {
		worddata = getClass().getResourceAsStream("dictionary/tradlexu8.txt");
	    } else if (charform == BOTH) {
		worddata = getClass().getResourceAsStream("dictionary/bothlexu8.txt");
	    }
	    BufferedReader in = new BufferedReader(new InputStreamReader(worddata, "UTF8"));
	    while ((newword = in.readLine()) != null) {
		if (newword.indexOf("#") == -1) {
		    addword(newword);
		    if (debug && count++ % 20000 == 0) { System.err.println(count); }
		}
	    } 
	    in.close();

	}
	catch (IOException e) {
	    System.err.println("IOException: "+e);
	}

    }


    /** Load a set of character data */
    private void loadset(TreeSet targetset, String sourcefile) {
	String dataline;
	try {
	    InputStream setdata = getClass().getResourceAsStream(sourcefile);
	    BufferedReader in = new BufferedReader(new InputStreamReader(setdata, "UTF-8"));
	    while ((dataline = in.readLine()) != null) {
		if ((dataline.indexOf("#") > -1) || (dataline.length() == 0)) {
		    continue;
		}
		targetset.add(dataline);
	    }
	    in.close();
	}
	catch (Exception e) {
	    System.err.println("Exception loading data file " + sourcefile + " " + e);
	}

    }

    public boolean isNumber(String testword) {
	boolean result = true;
	for (int i = 0; i < testword.length(); i++) {
	    if (cnumbers.contains(testword.substring(i, i+1)) == false) {
		result = false;
		break;
	    }
	}

	if (debug) {
	    printDebug(testword + " " + result); 
	}

	return result;
    }

    public boolean isAllForeign(String testword) {
	boolean result = true;
	for (int i = 0; i < testword.length(); i++) {
	    if (cforeign.contains(testword.substring(i, i+1)) == false) {
		result = false;
		break;
	    }
	}

	return result;
    }

    public boolean isNotCJK(String testword) {
	boolean result = true;
	for (int i = 0; i < testword.length(); i++) {
	    if (Character.UnicodeBlock.of(testword.charAt(i)) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
		result = false;
		break;
	    }
	}

	return result;
    }



    public String stemWord(String word) {
	String[] prefix = new String[] {"\u7b2c", "\u526f", "\u4e0d"};
	String[] suffix = new String[] {"\u4e86", "\u7684", "\u5730", "\u4e0b", "\u4e0a", "\u4e2d", "\u91cc", 
					"\u5230", "\u5185", "\u5916", "\u4eec"};
	String[] infix  = new String[] {"\u5f97", "\u4e0d"};
	int i;
	
	StringBuffer unstemmed = new StringBuffer(word);

	for (i = 0; i < prefix.length; i++) {
	    if (unstemmed.substring(0, 1).equals(prefix[i]) == true && 
		(zhwords.get(unstemmed.substring(1, unstemmed.length())) != null ||
		 unstemmed.length() == 2)) {
		//System.out.println("Stemmed prefix");
		//try {System.out.println(new String(unstemmed.toString().getBytes(debugencoding)));} catch (Exception a) { };
		unstemmed.deleteCharAt(0);
		return unstemmed.toString();
	    }
	}


	for (i = 0; i < suffix.length; i++) {
	    if (unstemmed.substring(unstemmed.length()-1, unstemmed.length()).equals(suffix[i]) == true && 
		(zhwords.get(unstemmed.substring(0, unstemmed.length()-1)) != null ||
		 unstemmed.length() == 2)) {
		System.out.println("Stemmed suffix");
		try {System.out.println(new String(unstemmed.toString().getBytes(debugencoding)));} catch (Exception a) { };
		unstemmed.deleteCharAt(unstemmed.length()-1);
		return unstemmed.toString();
	    }
	}
    
	for (i = 0; i < infix.length; i++) {
	    if (unstemmed.length() == 3 && unstemmed.substring(1, 2).equals(infix[i]) == true &&
		zhwords.get(new String(unstemmed.substring(0, 1) + unstemmed.substring(2, 3))) != null) {
		System.out.println("Stemmed infix");
		unstemmed.deleteCharAt(1);
		return unstemmed.toString();
	    }
	}

	return unstemmed.toString();
    }



    // Takes a Chinese string, returns string with separator inserted between each Chinese word
    public String segmentLine(String cline, String separator) {
	int[] boundaries = segmentLineOffsets(cline);
	StringBuffer clinebuffer = new StringBuffer(cline);
	int i, seplen = separator.length();

	if (boundaries.length == 0) { return cline; }

	for (i = boundaries.length-2; i >= 0; i--) {
	    if (boundaries[i] > 0 &&
		i+boundaries[i] != cline.length() &&
		cline.substring(i, i+seplen).equals(separator) == false &&
		cline.substring(i+boundaries[i], i+boundaries[i]+seplen).equals(separator) == false) {
		clinebuffer.insert(i+boundaries[i], separator);
	    }
	}
	return clinebuffer.toString();
    }


    public LinkedList segmentLine(String cline) {
	int[] boundaries = segmentLineOffsets(cline);
	LinkedList offsets = new LinkedList();
	int i;
	
	for (i = 0; i < boundaries.length; i++) {
	    if (boundaries[i] > 0) {
		offsets.add(new Integer(i));
	    }
	}

	return offsets;
    }




    public int[] segmentLineOffsets(String cline) {
	int i, j, tmpoffset;
	int clength = cline.length();
	int[] offsets = new int[clength];

	if (debug) System.out.println("Line length " + clength);

	// Handle Chinese & non-Chinese text; Group spaces, letters, punctuation, numbers
	if (debug) System.out.println("Grouping Chinese, letters, digits and spaces");
	i = 0;
	while (i < clength) {
	    if (debug) System.out.println("i " + i);
	    
	    if (Character.UnicodeBlock.of(cline.charAt(i)) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
		j = 8;
		if (i+j > clength) { j = clength - i; }
		for (; i+j <= clength && j > 1; j--) {
		    if (zhwords.containsKey(cline.substring(i, i+j))) {
			break;
		    }
		}
		offsets[i] = j;
		i += j;
		
	    } else if (Character.isWhitespace(cline.charAt(i))) {
		j=1;
		while (i+j < clength && Character.isWhitespace(cline.charAt(i+j))) {
		    j++;
		}
		offsets[i] = j;
		i += j;
	    } else if (Character.isLetter(cline.charAt(i))) {
		j=1;
		while (i+j < clength && Character.isLetter(cline.charAt(i+j))) {
		    j++;
		}
		offsets[i] = j;
		i += j;
	    } else if (Character.isDigit(cline.charAt(i))) {
		j=1;
		while (i+j < clength && Character.isDigit(cline.charAt(i+j))) {
		    j++;
		}
		offsets[i] = j;
		i += j;

	    } else {
		offsets[i] = 1;
		i++;
		
	    }
	    
	}
	
	if (debug) System.out.println("Grouping foreign transliterations");
	// Add in foreign transliterations
	i = 0;
	while (i < clength) {
	    if (offsets[i] > 0 ) {
		// Possibly a transliteration of a foreign name
		while (i+offsets[i] < clength &&
		       i+offsets[i]+offsets[i+offsets[i]] < clength &&
		       isAllForeign(cline.substring(i, i+offsets[i]+offsets[i+offsets[i]])) 
		       ) {
		    tmpoffset = offsets[i+offsets[i]];
		    offsets[i+offsets[i]] = 0;
		    offsets[i] = offsets[i] + tmpoffset;
		}
	    }
	    i++;
	}
	
	if (debug) System.out.println("Grouping numbers");
	// Concatenate numbers
	i = 0;
	while (i < clength) {
	    if (offsets[i] > 0)   {
		// Add in numbers
		while (i+offsets[i] < clength && 
		       i+offsets[i]+offsets[i+offsets[i]] < clength &&
		       isNumber(cline.substring(i, i+offsets[i]+offsets[i+offsets[i]]))) {
		    tmpoffset = offsets[i+offsets[i]];
		    offsets[i+offsets[i]] = 0;
		    offsets[i] = offsets[i] + tmpoffset;
		}
	    }
	    i++;
	}

	/*
	if (debug) System.out.println("Grouping Chinese names");
	// Group possible Chinese names together
	i = 0;
	while (i < clength) {
	    if (offsets[i] == 1 &&
		dictdata.isChineseSurname(cline.substring(i, i+offsets[i]))) {
		// Check for two syllable given name
		if (i+offsets[i] < clength && offsets[i+offsets[i]] == 1 &&
		    dictdata.isChinese(cline.substring(i, i+offsets[i])) &&
		    i+offsets[i]+offsets[i+offsets[i]] < clength && 
		    offsets[i+offsets[i] + offsets[i+offsets[i]]] == 1 &&
		    dictdata.isChinese(cline.substring(i+offsets[i], i+offsets[i]+offsets[i+offsets[i]]))) {

		    offsets[i+offsets[i]] = 0;
		    offsets[i+offsets[i] + offsets[i+offsets[i]]] = 0;
		    offsets[i] = 3;
		    
		}
		// Check for one syllable given name
		else if (i+offsets[i] < clength && offsets[i+offsets[i]] == 1 &&
			 dictdata.isChinese(cline.substring(i, i+offsets[i]))) {

		    offsets[i+offsets[i]] = 0;
		    offsets[i] = 2;
		}

	    }
	    i++;
	}
	*/

	

	return offsets;
    }


    

    public void addword(String newword) {
	int i;
	zhwords.put(newword, "1");
    }


    public void segmentFile(String inputfile, String encoding) {
	byte[] gbbytes;
	String outfile = inputfile + ".seg";
	String segstring;
	boolean debug = false;

	try {
	    String dataline;
	    InputStream srcdata = new FileInputStream(inputfile);
	    BufferedReader in = new BufferedReader(new InputStreamReader(srcdata, encoding));
	    BufferedWriter outbuffer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile), encoding));

	    
	    while ((dataline = in.readLine()) != null) {
		segstring = segmentLine(dataline, " ");
		if (debug) {
		    gbbytes = segstring.getBytes(encoding);
		    System.err.println("Output: " + new String(gbbytes));
		} 
		outbuffer.write(segstring);
		outbuffer.newLine();
	    }

	    in.close();
	    outbuffer.close();
	}
	catch (Exception e) {
	    System.err.println("Exception " + e.toString());
	}

    }

    public void printDebug(String debuginfo) {
	try {
	    System.out.println(new String(debuginfo.getBytes(debugencoding)));
	} 
	catch (Exception a) { a.printStackTrace(); }
    }


    public static void printHelp() {
	System.out.println("Usage:\njava -jar segmenter.jar [-b|-g|-8|-s|-t] inputfile.txt");
	System.out.println("\t-b Big5, -g GB2312, -8 UTF-8, -s simp. chars, -t trad. chars");
	System.out.println("  Segmented text will be saved to inputfile.txt.seg");
	System.exit(0);
    }

    public static void main(String[] argv) {
	Vector inputfiles = new Vector() ;
	String encoding = "BIG5";
	int charform = Segmenter.TRAD;
	boolean debug = false;
	int i, j;

	for (i = 0; i < argv.length; i++) {
	    if (argv[i].equals("-b")) {
		if (debug) System.out.println("Setting to Big5, TRAD");
		encoding = "BIG5";
		charform = Segmenter.TRAD;
	    } else if (argv[i].equals("-g")) {
		if (debug) System.out.println("Setting to GB, SIMP");
		encoding = "GBK";
		charform = Segmenter.SIMP;
	    } else if (argv[i].equals("-8")) {
		encoding = "UTF8";
		charform = Segmenter.BOTH;
	    } else if (argv[i].equals("-s")) {
		if (debug) System.out.println("Setting to UTF-8 SIMP");
		encoding = "UTF8";
		charform = Segmenter.SIMP;
	    } else if (argv[i].equals("-t")) {
		if (debug) System.out.println("Setting to UTF-8 TRAD");
		encoding = "UTF8";
		charform = Segmenter.TRAD;
	    } else if (argv[i].equals("-h")) {
		printHelp();
	    } else if (argv[i].equals("-d")) {
		debug = true;
	    } else {
		inputfiles.add(argv[i]);
	    }
	}

	if (inputfiles.size() == 0) {
	    System.out.println("ERROR: Please specify name of Chinese text file to segment.\n");
	    printHelp();
	} 


	System.err.println("Loading segmenter word list.  One moment please.");
	Segmenter mainsegmenter = new Segmenter(charform, true);

	System.err.println("Total keys " + mainsegmenter.zhwords.size());

	File tmpfile;
	String dirfiles[];
	for (i = 0; i < inputfiles.size(); i++) {
	    tmpfile = new File((String)inputfiles.get(i));
	    if (tmpfile.exists() == false) {
		System.out.println("ERROR: Source file " + (String)inputfiles.get(i) + 
				   " does not exist.\n");
		continue;
	    }
	    if (tmpfile.isDirectory() == true) {
		dirfiles = tmpfile.list();
		if (dirfiles != null) {
		    for (j = 0; j < dirfiles.length; j++) {
			inputfiles.add((String)inputfiles.get(i) + File.separator +
				       dirfiles[j]);
		    }
		}
		continue;
	    }
	    System.err.println("Segmenting " + inputfiles.get(i) + 
			       " with encoding " + encoding);
	    mainsegmenter.segmentFile((String)inputfiles.get(i), encoding);
	}
    }

}
