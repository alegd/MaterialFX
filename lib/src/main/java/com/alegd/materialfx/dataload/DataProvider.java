package com.alegd.materialfx.dataload;

import javafx.scene.input.MouseEvent;

/**
 * @author J. Alejandro Guerra Denis
 */
public interface DataProvider {

    /**
     *
     */
    void setupDataContainer();

    /**
     * @param event The event
     * @param item  The selected item
     */
    void onItemSelected(MouseEvent event, Object item);

    /**
     *
     */
    void loadData();
}
