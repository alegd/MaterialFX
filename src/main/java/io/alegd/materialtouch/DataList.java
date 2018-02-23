package io.alegd.materialtouch;

import com.jfoenix.controls.JFXCheckBox;
import io.alegd.materialtouch.dataload.DataProvider;
import io.alegd.materialtouch.dataload.Exportable;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

/**
 * @author J. Alejandro Guerra Denis
 */
public class DataList<T> extends DataContainer<T> {

    private ListView<T> listView;

    /**
     * Class constructor.
     *
     * @param listView The ListView
     */
    private DataList(ListView<T> listView) {
        dataContainer = listView;

        this.listView = listView;
        this.card = (Pane) listView.getParent().getParent().lookup(".card");

        listView.setItems(viewHolders);
    }

    /**
     * Class constructor.
     *
     * @param listView     The ListView
     * @param dataProvider The class implementing {@link DataProvider} interface
     */
    public DataList(ListView<T> listView, DataProvider dataProvider) {
        this(listView);
        this.dataProvider = dataProvider;

        listView.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> dataProvider.onItemSelected(null, newVal));
    }

    /**
     * Class constructor.
     *
     * @param listView   The ListView
     * @param exportable The class implementing {@link Exportable} interface
     */
    public DataList(ListView<T> listView, Exportable exportable) {
        this(listView);
        this.exportable = exportable;
        this.dataProvider = exportable;

        listView.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> dataProvider.onItemSelected(null, newVal));
    }


    @Override
    protected Node createPage(int pageIndex) {
        int fromIndex = pageIndex * pagination.getRowsPerPage();
        int toIndex = Math.min(fromIndex + pagination.getRowsPerPage(), viewHolders.size());

        listView.getItems().setAll(
                FXCollections.observableArrayList(viewHolders.subList(fromIndex, toIndex)));

        return listView;
    }

    /**
     *
     */
    @Override
    public void checkContent() {
        if (card != null) {
            card.getChildren().clear();

            if (listView.getItems().isEmpty()) {
                card.getChildren().add(mEmptyState);
            } else { // This means is in empty state
                card.getChildren().add(wrapper);
            }
        }
    }


    @Override
    public void addSelectionBox() {
        JFXCheckBox checkBox = new JFXCheckBox();
        listView.setCellFactory(new SelectionValueFactory<>(this));
    }
}
