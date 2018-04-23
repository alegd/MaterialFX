package io.alegd.materialtouch;

import com.jfoenix.controls.JFXCheckBox;
import javafx.beans.property.SimpleObjectProperty;
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
        CheckBox checkBox = new JFXCheckBox();
        checkBox.selectedProperty().bindBidirectional(item.selectedProperty());
        checkBox.setOnAction(event -> {
            if (item.isSelected()) {
                dataList.getSelectedItems().add(item);
            } else {
                dataList.getSelectedItems().remove(item);
//                dataList.getSelectionHeader().selectedProperty().set(false);
            }

            dataList.setToolbar(dataList.getSelectedItems().size());
//            if (dataList.getSelectedItems().size() == dataList.getData().size())
//                dataList.getSelectionHeader().setSelected(true);
        });
        return new SimpleObjectProperty(checkBox);
    }
}
