/**
 * Created by Aamir on 11/12/16.
 */
import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;

/**
 * Class to handle the input file
 */
public class FileProcessor {
    private  File file;
    private  int programCounter;

    /**
     * Constructor for this class
     * @param fileIn
     * @param programCounter_In
     */
    public FileProcessor(File fileIn, int programCounter_In){
        file=fileIn;
        programCounter = programCounter_In;
    }

    /**
     * Method to return arraylist of instructions
     * from input file.
     * Instructions start from index 4000.
     * @return arraylist of instructions
     */
    public  ArrayList<String> OpenFile(){
        ArrayList<String> textdata = new ArrayList<>();
        BufferedReader textReader = null;
        int i;
        try {
            FileReader fr = new FileReader(file);
            textReader = new BufferedReader(fr);
            String instruction;
            for (i = 0; i <programCounter; i++) {
                textdata.add(i,null);
            }
            while((instruction=textReader.readLine())!=null){
                textdata.add(programCounter,instruction);
                programCounter++;
            }
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                textReader.close();
            } catch (IOException  | NullPointerException e1){
                e1.printStackTrace();
                System.exit(1);
            }
        }
        return textdata;
    }
}
