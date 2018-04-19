package io.alegd.materialtouch;

import com.jfoenix.controls.JFXCheckBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * Class to hold configuration for checkboxes in TreeTableCell.
 */
public class SelectionValueFactory<T> implements Callback<ListView<T>, ListCell<T>> {

    private DataList dataList;

    public SelectionValueFactory(DataList dataList) {
        this.dataList = dataList;
    }

    @Override
    public ListCell<T> call(ListView<T> param) {
        Selectable item = (Selectable) param.getItems();
        CheckBox checkBox = new JFXCheckBox();
        checkBox.selectedProperty().bindBidirectional(item.selectedProperty());
        checkBox.setOnAction(event -> {
            if (item.isSelected()) {
                dataList.getSelectedItems().add(item);
            } else {
                dataList.getSelectedItems().remove(item);
//                dataList.getSelectionHeader().selectedProperty().set(false);
            }

//            dataList.setToolbar(dataList.getSelectedItems().size());
        });
        return new ListCell<>();
    }
}
