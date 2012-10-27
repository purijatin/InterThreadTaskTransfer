package os;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableFuture;

/** 
 *<p> Class used to run a message loop for a thread. 
 * Threads by default do not have a Runnable/Callable loop associated with them; to create one, 
 * call prepare() in the thread that is to run the loop, and then loop() to have it process messages until 
 * the loop is stopped.</p>
 * <p>Most interaction with a message loop is through the Handler class.</p>
 * <p>This is a typical example of the implementation of a Looper thread, 
 * using the separation of prepare() and loop() to create an initial Handler to communicate with the Looper. </p>
 *  <p>If the runnable throws an exception, the Looper shall stop executing the current
 *  runnable and will execute the next one.</p>
 * 
 * <p>Only the thread which has instantiated this class, can use Looper</p>
 * All the methods in this class are Thread-Safe.
 * @author Jatin
 * @version 1.0
 */
public class Looper {

    private final BlockingQueue<Runnable> queue;
    private volatile boolean shut = false;
    private volatile boolean shutNow = false;
    /**
     * 
     *<p> The Shutdown policy is that, Once the respective handler#shutDown() is called, no more elements will be accepted
     * to add in the queue. It shall return a false, when the adding the element was unsuccessful.</p>     * 
     */
    /**
     * This is used so that Only one thread which instantiated this class can call Looper#loop
     */
    private final ThreadLocal<Boolean> tlocal = new ThreadLocal<Boolean>() {

        @Override
        protected Boolean initialValue() {
            return null;
        }
    };
    private final Thread initializerThread;
    
    /*
     * This is used as a poison pill, ie once this is added to any queue,
     * and when looper in loop comes till the POISON_PILL, then no more runnables
     * are executed.
     */
    private static final Runnable POISON_PILL = new Runnable() {

        @Override
        public void run() {
        }
    };

    public Looper() {
        tlocal.set(true);
        queue = new LinkedBlockingQueue<Runnable>();
        initializerThread = Thread.currentThread();
    }

    public Looper(BlockingQueue<? extends Runnable> queue) {
        tlocal.set(true);
        this.queue = (BlockingQueue<Runnable>) queue;
        initializerThread = Thread.currentThread();
    }

    /**
     * 
     * @param id The id of the handler. Needed to put the runnable in the respective queue.
     * @param runnable The Runnable which needs to be added to the respective queue.
     * @return If adding the runnable to queue was successful. It might not be a success
     * if shutdown was called.
     */
    //@Guarded-By : this
    protected synchronized boolean post(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException("Runnable is null");
        }
        //see execute(Double id,Callable<T> call

        if (shut) {
            return false;
        }

        queue.add(runnable);//System.out.println("added");
        return true;
    }

    protected synchronized <T> Future<T> execute(Callable<T> call) {
        if (call == null) {
            throw new NullPointerException("Callable is null");
        }
        /*
         * The reason of synchroning the whole block below is that, if suppose I synchronized till only
         * then end of 'if', then what might happen is that in between the end of 'if' and starting
         * RunnableFuture<T> ftask.... the shutdown is called, then the actual system for the queue
         * might be shutdown and still we are adding a task, hence the whole block need to be synchronized.
         * Not doing the whole block, wont create any trouble, but queue might accaept a new Runnable
         * when it was still closed. And what if there are many threads waiting for the syncronized to get over?,
         * in that case many elements would have been added.
         * 
         * 
         */
        if (shut) {
            throw new RejectedExecutionException();
        }

        //-------------------------Master Piece--------------------------------//

        RunnableFuture<T> ftask = new FutureTask<T>(call);
        queue.add(ftask);//System.out.println("added");
        return ftask;

        //------------------------Master Piece---------------------------------//
    }

    protected synchronized <T> Future<T> execute(Runnable runnable, T result) {
        if (runnable == null) {
            throw new NullPointerException("Callable is null");
        }
        //see execute(Double id,Callable<T> call

        if (shut) {
            throw new RejectedExecutionException();
        }

        RunnableFuture<T> ftask = new FutureTask<T>(runnable, result);
        queue.add(ftask);//System.out.println("added");
        return ftask;
    }

    /**
     * One needs to clear the interrupt status of the Thread before calling this method,
     * if not then it will never process the runnable's.
     * @throws InterruptedException 
     * @return Returns the number of Runnables processed
     */
    /*
     * The policy followed here is that:
     * If Runnable throws an exception, then that Runnable is no longer executed,
     * and the next one in the queue is executed next.
     * 
     * Thread- safe through thread confinement.
     */
    public int loop() throws InterruptedException {
        //check if is it the thread which instantiated this class
        if (tlocal.get() == null) {
            throw new RejectedExecutionException("This thread cannot call loop() "
                    + "as it is not the one which instantiated it");
        }
        int i = 0;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if(shutNow)
                    break;
                Runnable runnable = queue.take();
                if (runnable == POISON_PILL) {
                    //queue.clear();
                    break;
                }
                i++;
                if (!(runnable instanceof RunnableFuture)) {
                    try {
                        runnable.run();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    //FutureTask will make sure that the runnable is run well, if exception is thrown
                    //then FutureTask will register it, so that when user calls Future#get
                    //then exception is returned.
                    runnable.run();//System.out.println("ha");
                }

                runnable = null;//so that it is ready for gc, or else had the runnable consumed
                //a lot of resources, then for one loop it will remain in invisible state,
                //see http://java.sun.com/docs/books/performance/1st_edition/html/JPAppGC.fm.html#997426 for more info                                        

                if (shut) {
                    if (queue.isEmpty()) {
                        break;
                    }
                }

            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();//let the stack know, that interrupt was thrown.
                throw new InterruptedException();
            }
        }
        return i;
    }

    /**
     * Returns the number of Runnable's left in the Looper
     * @return The number of Runnable's left in the Looper
     */
    public int queueSize() {
        return queue.size();
    }

    /**
     * Returns the number of additional elements that this Looper can ideally
     * (in the absence of memory or resource constraints) accept without
     * blocking, or <tt>Integer.MAX_VALUE</tt> if there is no intrinsic
     * limit.
     *
     * <p>Note that you <em>cannot</em> always tell if an attempt to insert
     * an element will succeed by inspecting <tt>remainingCapacity</tt>
     * because it may be the case that another thread is about to
     * insert or remove an element.
     *
     * @return the remaining capacity
     */
    public int queueRemainingCapacity() {
        return queue.remainingCapacity();
    }

    protected synchronized void shutDown() {
        /**
         * The policy to shutdown is : To put a boolean, put the poison pill here,
         * @Guarded-By the id of the Handler which calls it
         */
        if (shut) //check if shutdown was already called. if so, then do nothing
        {
            return;
        }
        post(POISON_PILL);// this should be called before mapBoolean(id,!shut)
        //reason: post will never accept it if called the other wise and ultimately
        //thread will never shutdown
        shut = false;
    }

    /**
     * Shuts down the Looper Service of the thread to which the Handler points to.
     * Shuts down immediately after the current runnable is processed.
     */
    protected synchronized void shutDownNow() {
        shutNow = true;
        shutDown();
    }

    /**
     * Never ever use this method unless it is urgently important to do so.
     * It immediately stops the the thread from continuing the loop() and returns immediately.
     * It does so by throwing an interrupt.
     * Reason not recommended is that the there might be some other thread which
     * owns the thread running loop(), and hence should never indulge in anything
     * related to interrupts, as it might have its own interruption policy.
     * 
     */
    protected void shutDonwNowImmediately() {
        initializerThread.interrupt();
        shutDownNow();
    }
}
