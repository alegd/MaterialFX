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

    private DataList dataTable;

    public SelectionValueFactory(DataList dataTable) {
        this.dataTable = dataTable;
    }

    @Override
    public ListCell<T> call(ListView<T> param) {
        Selectable item = (Selectable) param.getItems();
        CheckBox checkBox = new JFXCheckBox();
        checkBox.selectedProperty().bindBidirectional(item.selectedProperty());
        checkBox.setOnAction(event -> {
            if (item.isSelected()) {
                dataTable.getSelectedItems().add(item);
            } else {
                dataTable.getSelectedItems().remove(item);
//                dataTable.getSelectionHeader().selectedProperty().set(false);
            }

//            dataTable.setToolbar(dataTable.getSelectedItems().size());
        });
        return new ListCell<>();
    }
}
