package io.alegd.materialtouch;

import com.jfoenix.controls.JFXCheckBox;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.util.Callback;

/**
 * Class to hold configuration for checkboxes in TreeTableCell.
 */
public class SelectionValueFactory<T> implements Callback<T, ObservableValue<Boolean>> {

    private DataList dataList;

    public SelectionValueFactory(DataList dataList) {
        this.dataList = dataList;
    }

    @Override
    public ObservableValue<Boolean> call(T param) {
        Selectable item = (Selectable) param;
        BooleanProperty observable = new SimpleBooleanProperty();
        CheckBox checkBox = new JFXCheckBox();
//        checkBox.selectedProperty().bindBidirectional(item.selectedProperty());
        observable.addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                dataList.getSelectedItems().add(item);
            } else {
                dataList.getSelectedItems().remove(item);
            }
        });
        return observable;
    }
}
