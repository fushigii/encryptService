import java.io.File;
import java.io.FileNotFoundException;

public interface RequestFileProcessorInterface {
  public boolean processWorkRequestfile(File controlFile)
    throws FileNotFoundException;
}
