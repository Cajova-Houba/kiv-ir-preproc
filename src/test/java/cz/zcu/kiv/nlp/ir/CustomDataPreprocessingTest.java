package cz.zcu.kiv.nlp.ir;

import me.champeau.ld.EuroparlDetector;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class CustomDataPreprocessingTest {

    /**
     * File with source data (comments from reddit).
     */
    public static final String SOURCE_FILE_NAME = "comments_21057_textonly_v1.txt";
    public static final String STOPWORDS_EN_FILENAME = "stopwords-en.txt";

    /**
     * Comment delimiter.
     */
    public static final String COMMENT_DELIMITER = "\n;\n";

    static Preprocessing preprocessing;

    private static String wholeText;
    private static List<String> documents;

    @BeforeClass
    public static void setUpBeforeClass() throws IOException {

        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        createNewInstance();

        wholeText = loadWholeText();
        documents = loadAllDocuments();
    }

    private static void createNewInstance() throws IOException {
        List<String> stopWords = getEnStopWords();
        preprocessing = new BasicPreprocessing(
                new EnglishStemmer(), new AdvancedTokenizer(), new HashSet<>(stopWords), false, true, true
        );
    }

    private static List<String> getEnStopWords() throws IOException {
        ClassLoader classLoader = CustomDataPreprocessingTest.class.getClassLoader();
        File file = new File(classLoader.getResource(STOPWORDS_EN_FILENAME).getFile());
        return Files.readAllLines(file.toPath());
    }

    @Test
    public void testDetectLanguage() throws IOException {
        String expectedLanguage = "en";

        EuroparlDetector detector = EuroparlDetector.getInstance();
        String language = detector.detectLang(wholeText);

        assertEquals("Wrong language!", expectedLanguage, language);
    }

    @Test
    public void testNoLinks() throws IOException {
        createNewInstance();
        for(String document : documents) {
            preprocessing.index(document);
        }
        final Map<String, Integer> wordFrequencies = preprocessing.getWordFrequencies();
        assertFalse(wordFrequencies.containsKey("http"));
        assertFalse(wordFrequencies.containsKey("https"));
        assertTrue(wordFrequencies.containsKey("https://www.politico.com/magazine/story/2017/04/the-happy-go-lucky-jewish-group-that-connects-trump-and-putin-215007"));
    }


    @Test
    public void testStopWords() throws Exception {
        createNewInstance();
        for(String document : documents) {
            preprocessing.index(document);
        }
        final Map<String, Integer> wordFrequencies = preprocessing.getWordFrequencies();

        assertFalse(wordFrequencies.containsKey(preprocessing.getProcessedForm("we")));
        assertFalse(wordFrequencies.containsKey(preprocessing.getProcessedForm("him")));
        assertFalse(wordFrequencies.containsKey(preprocessing.getProcessedForm("i")));
        assertFalse(wordFrequencies.containsKey(preprocessing.getProcessedForm("themselves")));
        assertFalse(wordFrequencies.containsKey(preprocessing.getProcessedForm("because")));

        // 'hes' gets processed to 'he'
//        assertFalse(wordFrequencies.containsKey(preprocessing.getProcessedForm("he")));

        // 'his' gets processed to 'hi'
//        assertFalse(wordFrequencies.containsKey(preprocessing.getProcessedForm("his")));

    }

    /**
     * Loads whole file as a text (without delimiters).
     * @return
     */
    private static String loadWholeText() throws IOException {
        ClassLoader classLoader = CustomDataPreprocessingTest.class.getClassLoader();
        File file = new File(classLoader.getResource(SOURCE_FILE_NAME).getFile());
        FileReader fr=new FileReader(file);   //reads the file
        BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream
        StringBuilder sb = new StringBuilder();    //constructs a string buffer with no characters
        String line;
        while((line=br.readLine())!=null)
        {
            if (line.startsWith(";") || line.startsWith("\n") || line.isEmpty()) {
                continue;
            }
            sb.append(line);      //appends line to string buffer
        }
        fr.close();    //closes the stream and release the resources

        return sb.toString();
    }

    private static List<String> loadAllDocuments() throws IOException {
        ClassLoader classLoader = CustomDataPreprocessingTest.class.getClassLoader();
        File file = new File(classLoader.getResource(SOURCE_FILE_NAME).getFile());
        FileReader fr=new FileReader(file);   //reads the file
        List<String> documents = new ArrayList<>();
        BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream
        String line;
        while((line=br.readLine())!=null)
        {
            if (line.startsWith(";") || line.startsWith("\n") || line.isEmpty()) {
                continue;
            }
            documents.add(line);
        }
        fr.close();    //closes the stream and release the resources

        return documents;
    }


}
