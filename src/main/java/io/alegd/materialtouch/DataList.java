package io.alegd.materialtouch;

import com.jfoenix.controls.JFXToolbar;
import com.jfoenix.effects.JFXDepthManager;
import io.alegd.materialtouch.dataload.DataProvider;
import io.alegd.materialtouch.dataload.Exportable;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.BorderPane;
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

    /**
     * Set main toolbar or contextual toolbar depending on how many selected items
     * we have. Main toolbar is only showed when selected items count is 0.
     *
     * @param itemCount The amount of selected items.
     */
    public void setToolbar(int itemCount) {
        if (itemCount > 0) {
            if (itemCount > 1)
                mCToolbarTitle.setText(itemCount + " elementos seleccionados");
            else
                mCToolbarTitle.setText(itemCount + " elemento seleccionado");

            if (listView.getParent() instanceof BorderPane)
                ((BorderPane) listView.getParent()).setTop(contextualToolbar);
            else
                ((Pane) listView.getParent()).getChildren().add(0, contextualToolbar);
        } else {
            if (selectedItems != null)
                selectedItems.clear();

            if (listView.getParent() instanceof BorderPane)
                ((BorderPane) listView.getParent()).setTop(mainToolbar);
            else
                ((Pane) listView.getParent()).getChildren().add(0, mainToolbar);
        }
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
        listView.setCellFactory(CheckBoxListCell.forListView(new SelectionValueFactory<>(this)));

        contextualToolbar = new JFXToolbar();
        JFXDepthManager.setDepth(contextualToolbar, 0);
        contextualToolbar.getStyleClass().add("table-header");
        contextualToolbar.getStyleClass().add("alternate-table-header");
        mCToolbarTitle = new Label("contextual");
        contextualToolbar.setLeftItems(mCToolbarTitle);
    }
}
