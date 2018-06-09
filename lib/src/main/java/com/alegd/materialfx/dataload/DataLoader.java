package com.alegd.materialfx.dataload;

import com.alegd.materialfx.DataContainer;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Class to manage asynchronous data loading.
 *
 * @author J. Alejandro Guerra Denis
 */
public class DataLoader {

    /**
     * The service that handles the data loading.
     */
    private Service loadDataService;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private DataContainer dataContainer;

    private AsyncDataProvider asyncDataProvider;

    /**
     * This class constructor handle a service creation. Once the service is created,
     * a task inside it is created as well overriding {@link Service#createTask()} method,
     * and from this new task the method to actually load the data is called. The next few
     * steps after this are to set the different states of a service that you can manipulate.
     * These states are:
     * 1. RUNNING: While service is running you can show a loading text, a progress bar, etc.
     * 2. SUCCEEDED: When the service is done and finished successfully you should normally
     * hide the component shown while the service was running and show the component where
     * data is displayed.
     * 3. FAILED: If for some reason the service execution failed an exception is thrown and
     * printed to the console. This is only for development purposes.
     *
     * @param asyncDataProvider The class implementing AsyncDataProvider interface.
     */
    public DataLoader(DataContainer dataContainer, AsyncDataProvider asyncDataProvider) {
        this.dataContainer = dataContainer;
        this.asyncDataProvider = asyncDataProvider;
    }

    /**
     * Start the service once is in READY state. If the service state is any other different
     * than READY, the service is restarted instead. This is important, especially when users
     * trigger an action that leads to load data (e.i run the task of the service) but the
     * service is still running (just to mention an example).
     */
    public synchronized void start() {
        loadDataService = new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Void call() throws Exception {
                        dataContainer.loadData();
                        System.out.println("LOADING...");
                        return null;
                    }
                };
            }
        };

        loadDataService.setOnRunning(service -> asyncDataProvider.onRunning());
        // If everything it's OK :)
        loadDataService.setOnSucceeded(service -> {
            if (dataContainer.havePages())
                dataContainer.paginate();
            asyncDataProvider.onSucceeded();
            System.out.println("SUCCESS");
        });
        // If something goes wrong :\
        loadDataService.setOnFailed(service -> {
            asyncDataProvider.onFailed();
            loadDataService.getException().printStackTrace();
            System.out.println("FAIL");
        });

        if (loadDataService.getState() == Worker.State.READY)
            loadDataService.start();
        else
            loadDataService.restart();
    }

    /**
     * Start the service once is in READY state. If the service state is any other different
     * than READY, the service is restarted instead. This is important, especially when users
     * trigger an action that leads to load data (e.i run the task of the service) but the
     * service is still running (just to mention an example).
     */
    public synchronized void startExecutor() {
        if (executor.isShutdown())
            executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> dataContainer.loadData());

        try {
            System.out.println("attempt to shutdown executor");
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("tasks interrupted");
        } finally {
            if (!executor.isTerminated()) {
                System.err.println("cancel non-finished tasks");
            }

            executor.shutdownNow();
            if (dataContainer.havePages())
                dataContainer.paginate();
            asyncDataProvider.onSucceeded();
            System.out.println("shutdown finished");
        }
    }
}
