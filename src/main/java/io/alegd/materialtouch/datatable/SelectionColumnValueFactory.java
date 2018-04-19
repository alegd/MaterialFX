package io.alegd.materialtouch.datatable;

import com.jfoenix.controls.JFXCheckBox;
import io.alegd.materialtouch.Selectable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

/**
 * Class to hold configuration for checkboxes in TreeTableCell.
 */
public class SelectionColumnValueFactory<T> implements
        Callback<TreeTableColumn.CellDataFeatures<T, Boolean>, ObservableValue<Boolean>> {

    private DataTable dataTable;

    public SelectionColumnValueFactory(DataTable dataTable) {
        this.dataTable = dataTable;
    }

    @Override
    public ObservableValue<Boolean> call(TreeTableColumn.CellDataFeatures<T, Boolean> param) {
        Selectable item = (Selectable) param.getValue().getValue();
        CheckBox checkBox = new JFXCheckBox();
        checkBox.selectedProperty().bindBidirectional(item.selectedProperty());
        checkBox.setOnAction(event -> {
            if (item.isSelected()) {
                dataTable.getSelectedItems().add(item);
            } else {
                dataTable.getSelectedItems().remove(item);
                dataTable.getSelectionHeader().selectedProperty().set(false);
            }

            dataTable.setToolbar(dataTable.getSelectedItems().size());
            if (dataTable.getSelectedItems().size() == dataTable.getData().size())
                dataTable.getSelectionHeader().setSelected(true);
        });
        return new SimpleObjectProperty(checkBox);
    }
}
