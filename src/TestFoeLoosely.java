
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
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
public class TestFoeLoosely {

    public static void main(String args[]) throws InterruptedException {
        new TestFoeLoosely().go();
    }
    Handler l;

    void go() throws InterruptedException {
        new Thread(new Ch()).start();
        final CyclicBarrier barr = new CyclicBarrier(1000);
        for (int i = 0; i < 1000; i++) {

            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        //System.out.println(barr.getNumberWaiting());
                        barr.await();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                        // Logger.getLogger(TestFoeLoosely.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (BrokenBarrierException ex) {
                        ex.printStackTrace();
                        //Logger.getLogger(TestFoeLoosely.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    int count=0;
                    for (int k = 0; k < 800; k++) {
                        if (k > 400) {
                            Future<Object> executeLoosely = l.execute(new Runnable() {

                                public void run() {
                                    //String h = new String("LOOSE");
                                    //System.out.println(h);
                                    long hD =System.currentTimeMillis();
                                }
                            }, new Object());
                            count++;

                        }
                        else {
                            Future<Object> executeLoosely = l.execute(new Runnable() {

                                public void run() {
                                    //String h = new String("EXECUTE");
                                    //System.out.println(h);
                                    long hD =System.currentTimeMillis();
                                }
                            }, new Object());
                            count++;
                        }
                    }
                    //System.out.println(Thread.currentThread().toString()+"  "+count);
                }
            }).start();
        }
        Thread.currentThread().sleep(20000);
        l.shutDown();


    }

    class Ch implements Runnable {

        public void run() {
            try {
                Looper looper = new Looper();
                l = new Handler(looper);
//Thread.currentThread().sleep(2000);
                looper.loop();
            } catch (Exception ex) {
                Logger.getLogger(TestFoeLoosely.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
