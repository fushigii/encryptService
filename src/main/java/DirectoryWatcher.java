import java.io.*;
import java.nio.file.Files;
import java.nio.file.*;

public abstract class DirectoryWatcher implements Runnable, RequestFileProcessorInterface {

  private WatchService watcher;
  private boolean m_terminate = false;
  private static final String pathToWatch = "C:\\data\\temp\\";
  private static final String target_path = "C:\\data\\temp\\archive\\";
  private Path target = FileSystems.getDefault().getPath("C:\\data\\temp\\archive\\");
  private Path error = FileSystems.getDefault().getPath("C:\\data\\temp\\error\\");

  public void stopProcessing() {
    m_terminate = true;
  }

  public DirectoryWatcher() {

    try {
      this.watcher = FileSystems.getDefault().newWatchService();
      Path directory = Paths.get(pathToWatch);
      directory.register(this.watcher, StandardWatchEventKinds.ENTRY_CREATE);
    } catch(IOException ioe) {
      ioe.printStackTrace();
    }

  }

  @Override
  public void run() {

    for(;;){
      WatchKey key = null;
      try{
        key = this.watcher.take();
        processNewFileEvents(key);
      } catch(InterruptedException ie){
        return;
      } finally {
        //reset the key
       boolean valid = key.reset();
        //If the key is no longer valid, the directory is inaccessible so exit the loop
        //A watch key is valid if
        //It has not been canceled
        //The WatchService is not yet closed

        if (!valid) {
          break;
       }
      }
    }
  }


  public void processNewFileEvents(final WatchKey key) {
    key.pollEvents().stream().filter((event)-> isNotOverFlowEvent(event) && m_terminate==false)
      .map(event -> (WatchEvent<Path>) event)
      .map( watchEvent -> watchEvent.context())
      .map(fileEvents -> fileEvents.getFileName())
      .forEach(fileEvent ->{
        String absolutePath =  pathToWatch + fileEvent.getFileName().toString();
        File file = new File(absolutePath);
        Path movefrom = FileSystems.getDefault().getPath(absolutePath);
        Path target = FileSystems.getDefault().getPath(target_path +fileEvent.toString() );
        try {
          if ( processWorkRequestfile( file ) ) {
           try {
             Files.move(
               movefrom, target, StandardCopyOption.REPLACE_EXISTING );
           } catch( IOException e ) {
             System.err.println( e );
           }
         } else {
            Path error = FileSystems.getDefault().getPath(target_path +fileEvent.toString() );
            try {
              Files.move( movefrom, error, StandardCopyOption.REPLACE_EXISTING );
           } catch( IOException e ) {
             System.err.println( e );
           }
         }
       } catch( FileNotFoundException fne ) {
         fne.printStackTrace();
       }
      });
  }

  public boolean isNotOverFlowEvent(WatchEvent event) {
    return !(event.kind() == StandardWatchEventKinds.OVERFLOW);
  }
}
