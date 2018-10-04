import java.io.*;

public class TransformFile1 extends DirectoryWatcher {

  public TransformFile1() {
    super();
  }

  public boolean processWorkRequestfile(File controlFile) throws FileNotFoundException
  {
    boolean processed = false;
    FileInputStream fis = new FileInputStream(controlFile);
    DataInputStream dis = new DataInputStream(fis);
    InputStreamReader isr = new InputStreamReader(dis);
    int lineNumber =0;
    try(BufferedReader br = new BufferedReader( isr )) {
      String line;
      while ((line = br.readLine()) != null){
        lineNumber++;
        processed = true;
      }
    } catch(IOException ioe) {
      ioe.printStackTrace();

    }

    System.out.println(controlFile.getName()   +
      " File Processed with " +
      lineNumber + " Lines");

    return processed;
  }

}
