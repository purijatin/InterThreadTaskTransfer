
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
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
public class TestForFuture {
    public static void main(String args[])
    {
        new TestForFuture().go();
    }
    void go()
    {
        Double a = Math.random() * Double.MAX_VALUE * (Math.random() > 0.5 ? -1 : 1);
        System.out.println(a);
        try {
            System.out.println(Thread.currentThread().toString());
            Looper looper = new Looper();
            final Handler h = new Handler(looper);
            final Future<String> l = h.execute(new Callable<String>(){
                
                @Override
                public String call() throws Exception {
                    //Thread.currentThread().sleep(10000);
                    int y= 90/0;
                    return (Thread.currentThread().toString()+"fg");
                }
            
        });
            h.post(new Runnable(){
                public void run()
                {
                    int y = 90/0;
                }
            });
            new Thread(new Runnable(){
               public void run()
               {
            
                        //System.out.println("Waiting    new THread");
                        //System.out.println(l.get()+"    new Thread");
                         h.post(new Runnable(){
                       public void run()
                       {
                            try {
                                Thread.currentThread().sleep(10);
                                h.shutDown();
                                System.out.println("After shudown");
                                 h.post(new Runnable(){
                public void run()
                {
                    System.out.println("72");
                }
            });
                            } catch (InterruptedException ex) {
                                Logger.getLogger(TestForFuture.class.getName()).log(Level.SEVERE, null, ex);
                            }
                       }
                   });
                               
            
               }
            }).start();
           
          
          looper.loop();
        } catch (Exception ex) {
            Logger.getLogger(TestForFuture.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
