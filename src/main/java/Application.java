/**
 * Created by Esteban_Lopez on 10/3/2018.
 */
public class Application
{

  public static void main(String ... args) {

    TransformFile1 transformFileWorker = new TransformFile1();
    Thread thread = new Thread( transformFileWorker );
    //thread.setDaemon( true );
    thread.start();

  }
}
