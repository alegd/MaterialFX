package com.alegd.materialfx;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListView;
import com.alegd.materialfx.dataload.Exportable;
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

    private JFXListView<T> listView;

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

        addItemSelectionListener();
        setupSelectAllButton();

        toolBarActions = getContextualHeader().getRightItems();
        toolBarActions.addListener((ListChangeListener<? super Node>) observable -> {
            if (!toolBarActions.contains(selectAllButton)) {
                toolBarActions.add(0, selectAllButton);
            }
        });
    }


    private void addItemSelectionListener() {
        listView.getItems().addListener((ListChangeListener<? super T>) observable -> {
            observable.next(); // Mandatory call before any action with observable
            T addedItem = observable.getAddedSubList().get(0);

            if (addedItem instanceof String) {
                Label itemWrapper = new Label((String) addedItem);
                itemWrapper.setOnMouseClicked(e -> dataProvider.onItemSelected(e, itemWrapper));
            } else if (addedItem instanceof Node) {
                ((Node) addedItem).setOnMouseClicked(e -> dataProvider.onItemSelected(e, addedItem));
            }
        });
    }


    private void setupSelectAllButton() {
        selectAllButton.setGraphic(Constant.getIcon("select_all", 20));
        selectAllButton.setOnMouseClicked(e -> {
            onSelectAll();
        });
    }


    private void onSelectAll() {
        boolean allSelectedFlag = (selectedItems.size() == listView.getItems().size());
        selectedItems.clear();

        if (!allSelectedFlag) {
            for (T item : listView.getItems()) {
                Selectable selectableItem = (Selectable) item;
                selectableItem.setSelected(true);
                selectedItems.get().add(selectableItem);
            }
        } else {
            for (T item : listView.getItems()) {
                Selectable selectableItem = (Selectable) item;
                selectableItem.setSelected(false);
            }

            selectedItems.clear();
        }
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
            observable.next(); // Mandatory call before any action with observable
            T addedItem = observable.getAddedSubList().get(0);

            if (addedItem != null) {
                JFXCheckBox checkBox = createSelectionBox(addedItem);

                if (addedItem instanceof HBox)
                    ((HBox) addedItem).getChildren().add(0, checkBox);
                else if (addedItem instanceof Node) {
                    HBox wrapper = new HBox((Node) addedItem);
                    int indexToReplace = listView.getItems().indexOf(addedItem);
                    listView.getItems().set(indexToReplace, (T) wrapper);
                } else {
                    Label wrapper = new Label(String.valueOf(addedItem));
                    int indexToReplace = listView.getItems().indexOf(addedItem);
                    listView.getItems().set(indexToReplace, (T) wrapper);
                }
            }
        });
    }


    private JFXCheckBox createSelectionBox(T item) {
        JFXCheckBox checkBox = new JFXCheckBox();
        checkBox.selectedProperty().bindBidirectional(((Selectable) item).selectedProperty());
        checkBox.setOnAction(event -> {
            if (((Selectable) item).isSelected())
                selectedItems.get().add((Selectable) item);
            else
                selectedItems.get().remove(item);
        });

        return checkBox;
    }


    @Override
    public void removeSelectionBox() {

    }


    public ListView<T> getListView() {
        return listView;
    }
}
