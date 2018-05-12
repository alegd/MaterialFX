package io.alegd.materialtouch;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListView;
import io.alegd.materialtouch.dataload.Exportable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

/**
 * @author J. Alejandro Guerra Denis
 */
public class DataList<T> extends DataContainer<T> {

    private ListView<T> listView;

    private T lastItem;

    private JFXButton selectAllButton = new JFXButton();
    private ObservableList<Node> toolBarActions;


    public DataList() {
        listView = new JFXListView<>();
        listView.setItems(viewHolders);
        setCenter(listView);

        getStylesheets().addAll(
                this.getClass().getResource("/css/data-table.css").toExternalForm(),
                this.getClass().getResource("/css/fonts.css").toExternalForm());

        withEmptyState(null, null, true);

        listView.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> dataProvider.onItemSelected(null, newVal));
        setupSelectAllButton();

        toolBarActions = getContextualHeader().getRightItems();
        toolBarActions.addListener((ListChangeListener<? super Node>) observable -> {
            if (!toolBarActions.contains(selectAllButton)) {
                toolBarActions.add(0, selectAllButton);
            }
        });
    }


    private void setupSelectAllButton() {
        selectAllButton.setGraphic(Constant.getIcon("select_all", 21));
        selectAllButton.setOnMouseClicked(e -> {
            for (T item : listView.getItems()) {
                Selectable selectableItem = (Selectable) item;
                selectableItem.setSelected(true);
            }
        });
    }

    /**
     * Class constructor.
     *
     * @param exportable The class implementing {@link Exportable} interface
     */
    public DataList(Exportable exportable) {
        this();
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
        if (getParent() != null) {
            if (listView.getItems().isEmpty()) {
                if (!showHeaderWithNoData) {
                    setTop(null);
                    setCenter(mEmptyState);
                } else {
                    setCenter(mEmptyState);
                }
            } else {
                setCenter(listView);
            }
        } else {
            if (mEmptyState.getParent() != null) {
                setCenter(listView);
            }
        }
    }


    @Override
    public void addSelectionBoxes() {
        listView.getItems().addListener((ListChangeListener<? super T>) observable -> {
            observable.next();
            lastItem = observable.getList().get(observable.getTo() - 1);

            if (lastItem != null) {
                JFXCheckBox checkBox = new JFXCheckBox();
                checkBox.selectedProperty().bindBidirectional(((Selectable) lastItem).selectedProperty());
                checkBox.setOnAction(event -> {
                    if (((Selectable) lastItem).isSelected())
                        selectedItems.get().add((Selectable) lastItem);
                    else
                        selectedItems.get().remove(lastItem);
                });

                if (lastItem instanceof HBox)
                    ((HBox) lastItem).getChildren().add(0, checkBox);
                else if (lastItem instanceof Node) {
                    HBox wrapper = new HBox((Node) lastItem);
                    int indexToReplace = listView.getItems().indexOf(lastItem);
                    listView.getItems().set(indexToReplace, (T) wrapper);
                } else {
                    Label wrapper = new Label(String.valueOf(lastItem));
                    int indexToReplace = listView.getItems().indexOf(lastItem);
                    listView.getItems().set(indexToReplace, (T) wrapper);
                }
            }
        });
    }


    @Override
    public void removeSelectionBox() {

    }
}
