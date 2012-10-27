
import java.util.logging.Level;
import java.util.logging.Logger;
import os.Handler;
import os.Looper;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jatin
 */
public class Test2 {
public static void main(String args[]) throws Exception    
{
    new Test2().go2();
}
int i=0;int j=0;

void go2() throws Exception
{
    final Looper looper = new Looper();
    final Handler h = new Handler(looper);
    //Thread.currentThread().interrupt();
   
    h.post(new Runnable(){
        public void run()
        {
                try {
                    System.out.println(System.currentTimeMillis());
                     Thread.currentThread().sleep(1);
                    System.out.println("sdf");
                } catch (InterruptedException ex) {
                    Logger.getLogger(Test2.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
    });  
    new Thread(new Runnable(){
       public void run()
       {
                try {
                    h.shutDownNowImmediately();
                    System.out.println(System.currentTimeMillis());
                } catch (Exception ex) {
                    Logger.getLogger(Test2.class.getName()).log(Level.SEVERE, null, ex);
                }
       }
    }).start();
    

      
    h.post(new Runnable(){
        public void run()
        {
            System.out.println(System.currentTimeMillis());
            System.out.println("sdf");
        }
    });
         h.shutDown();
         looper.loop();
}



void go() throws InterruptedException
{
    Looper looper = new Looper();
    final Handler h = new Handler(looper);
    h.post(new Runnable(){
        public void run()
        {
            System.out.println("sdf");
        }
    });
    h.shutDown();
    h.post(new Runnable(){
        public void run()
        {
            System.out.println("sdf");
        }
    });
    h.shutDown();
    looper.loop();
    
}
class A{
    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        //System.out.println(" finalized   "+i);
        
    }
}

}

