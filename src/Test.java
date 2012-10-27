
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import os.Handler;
import os.Looper;

/**
 *  Testing file. It is not not in the right order.
 * Proper test framework needed. JUnit should be fine
 * 
 * @author Jatin
 */
public class Test {
public static void main(String args[]) throws InterruptedException    
{
    Thread.currentThread().setPriority(6);    
    new Test().go3();      
        
        //Thread.currentThread().sleep(4000);
        
    
}
AtomicInteger k = new AtomicInteger(0);
private int getId()
{
    return k.addAndGet(1);
}

private LinkedBlockingQueue<String> list3 = new LinkedBlockingQueue<String>();
void go3()
{
        try {
                long st = System.currentTimeMillis();
            for(int j=0;j<90;j++)
            {
            
                for(int i=0;i<4;i++)
                {
                            new Thread(new Check2()).start();
                }
                          //  barr.await();
                            //barr.await();
                            //barr.await();
                            Thread.currentThread().yield();
                            
                            for(Handler h: list)
                            h.shutDown();
                            //System.out.println(j);

              }
            Thread.currentThread().sleep(10000);
                            for(Handler h: list)
                            h.shutDown();
                       
                        //System.out.println(list3);
                        //String[] p = (String[])list3.toArray();                        
//                        ArrayList<String> temp = new ArrayList<String>();
//                        temp.addAll(list3);
                        //String[] r = new String[list3.size()];
//                        for(int a=0;a<list3.size();a++)
//                        {
//                            r[a] = list3.poll();
//                            //System.out.println(r[a]);
//                        }
                        System.out.println(" --------------- "+(System.currentTimeMillis()-st));
                           //Collections.sort(temp);  
//                        System.out.println(r[r.length-1]);
//                        QuickSortWithObjects<String> t = new QuickSortWithObjects<String>(r);
//                        //t.setToFind((String[])temp.toArray());
//                        t.quickSort();
//                        t.print();
                           
//                           for(String k:temp)
//                           {
//                               System.out.println(k);
//                           }
                        

        } catch (InterruptedException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
}
//private CyclicBarrier barr = new CyclicBarrier(21);
private LinkedBlockingQueue<Handler> list = new LinkedBlockingQueue<Handler>();
private LinkedBlockingQueue<Handler> list2 = new LinkedBlockingQueue<Handler>();

class Check2 implements Runnable
{
    public void run()
    {
            try {
                Looper looper = new Looper();
                
                
                //barr.await();
                list.add(new Handler(looper));
                final int h = getId();
                //barr.await();
                int i=0;
                for(Handler hannex : list)
                {
                    i++;                    
//                    if(i>900)
//                    {
//                        //Thread.currentThread().interrupt();
//                        hannex.shutDown();                              
//                        
//                    }
                    
                    for(int j=0;j<100;j++)
                    {
                        try{
                        Object r = new Object();
                            hannex.execute(new Runnable(){
                                                        
                            public void run() 
                            {
                                list3.add(Thread.currentThread().toString()+"  to  "+h);
                        
                            }
                        },r); }
                        catch(Exception ex){}
                    }
                    
                    
                    
                    
                
                }                
                //barr.await();
                looper.loop();
               char t ='a';
            } catch (InterruptedException ex) {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            }
        
    }
}



Handler h;
Handler h2;

void go2() throws InterruptedException
{
    Looper looper = new Looper();
    h = new Handler(looper);
    boolean[] ans = new boolean[7];
    new Thread(new Check()).start();
    Thread.currentThread().sleep(100);
    h2.post(new Runnable(){public void run(){System.out.println(Thread.currentThread()+" "+2);}});
    h2.shutDown();
    h2.post(new Runnable(){public void run(){System.out.println(Thread.currentThread()+" "+2);}});
    looper.loop();
    //System.out.println(ans[0]+" "+ans[1]+" "+ans[5]+" "+ans[6] );
    
}




private class Check implements Runnable
{
      public void run() {
            try {
                Looper looper = new Looper();
                h2 = new Handler(looper);
                h.post(new Runnable(){public void run(){System.out.println(Thread.currentThread()+" "+1);}});
                h.shutDown();
                h.post(new Runnable(){public void run(){System.out.println(Thread.currentThread()+" "+1);}});
                looper.loop();
            } catch (InterruptedException ex) {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            }
        
    }
}
}
