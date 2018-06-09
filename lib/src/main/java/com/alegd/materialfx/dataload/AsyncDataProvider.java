package com.alegd.materialfx.dataload;

/**
 * @author J. Alejandro Guerra Denis
 */
public interface AsyncDataProvider extends DataProvider {

    void onRunning();

    void onSucceeded();

    void onFailed();
}
