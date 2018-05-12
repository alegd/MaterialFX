package io.alegd.materialtouch.datatable;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import io.alegd.materialtouch.DataContainer;
import io.alegd.materialtouch.Selectable;
import io.alegd.materialtouch.dataload.Exportable;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.util.function.Function;

/**
 * @param <T>
 * @author J. Alejandro Guerra Denis
 */
public class DataTable<T extends RecursiveTreeObject<T>> extends DataContainer<T> {

    private JFXTreeTableView<T> treeTableView;

    private JFXTreeTableColumn<T, Boolean> selectionColumn;

    private JFXTreeTableColumn<T, String> actionColumn;

    private JFXCheckBox selectionHeader = new JFXCheckBox();

    private boolean showHeaderWithNoData;

    /**
     * Class constructor.
     */
    public DataTable() {
        treeTableView = new JFXTreeTableView<>();

        getStylesheets().addAll(
                this.getClass().getResource("/css/data-table.css").toExternalForm(),
                this.getClass().getResource("/css/fonts.css").toExternalForm());

        setCenter(treeTableView);

        treeTableView.setRoot(new RecursiveTreeItem<>(viewHolders, RecursiveTreeObject::getChildren));
        treeTableView.setShowRoot(false);
        setRowFactory();

        withEmptyState(null, null, true);

        Platform.runLater(() -> {
            setupFirstColumn();
            setupLastColumn();
            if (havePages())
                paginate();
        });
    }

    /**
     * Class constructor.
     *
     * @param paginate Whether to showPages or not
     */
    public DataTable(boolean paginate) {
        this();
        this.showPages = paginate;
    }

    /**
     * Class constructor.
     *
     * @param exportable The class implementing {@link Exportable} interface
     */
    public DataTable(Exportable exportable) {
        this();
        this.exportable = exportable;
        this.dataProvider = exportable;
    }

    /**
     * Class constructor.
     *
     * @param exportable The class implementing {@link Exportable} interface
     */
    public DataTable(Exportable exportable, boolean paginate) {
        this(exportable);
        this.showPages = paginate;
    }

    /**
     * Add an empty state to the data table. Empty states occur when an item’s content can’t
     * be shown for any reason.
     *
     * @param title    The title for the empty state card
     * @param subtitle The subtitle for the empty state card, usually a suggestion to get
     *                 rid of the emptiness.
     */
    public synchronized void withEmptyState(String title, String subtitle, boolean keepHeader) {
        showHeaderWithNoData = keepHeader;
        withEmptyState(title, subtitle);
    }

    /**
     * Set click action for rows of a  On mouse click the onItemSelected
     * method is called, since that method is define through an interface, each class that
     * use this method is responsible for its own implementation.
     */
    private void setRowFactory() {
        treeTableView.setRowFactory(tv -> {
            TreeTableRow<T> row = new TreeTableRow<>();
            row.setOnMouseClicked(event -> this.dataProvider.onItemSelected(event, row));
            return row;
        });
    }

    /**
     * Setup how to show values for cells in a column passed as parameter.
     *
     * @param column The column to be set
     * @param mapper The function that is going to give value to a cell
     * @param <S>    The data type of the value in a cell
     */
    public <S> void setupCellValueFactory(JFXTreeTableColumn<T, S> column,
                                          Function<T, ObservableValue<S>> mapper) {
        column.setCellValueFactory((TreeTableColumn.CellDataFeatures<T, S> param) -> {
            if (column.validateValue(param)) {
                return mapper.apply(param.getValue().getValue());
            } else {
                return column.getComputedValue(param);
            }
        });
    }


    @Override
    public void addSelectionBox() {
        selectionColumn = new JFXTreeTableColumn<>();
        selectionColumn.minWidthProperty().setValue(64);
        selectionColumn.setGraphic(selectionHeader);
        selectionColumn.setCellValueFactory(new SelectionColumnValueFactory<>(this));
        selectionColumn.getStyleClass().add("selection-column");

        selectionHeader.setOnAction(event -> onSelectAll());

        if (treeTableView.getColumns().indexOf(selectionColumn) == -1) {
            TreeTableColumn<T, ?> firstColumn = treeTableView.getColumns().get(0);
            firstColumn.getStyleClass().add("after-checkbox-column");
            firstColumn.getStyleClass().remove("first-column");
            treeTableView.getColumns().add(0, selectionColumn);
        }
    }

    /**
     * Setup column to display individual actions for each row.
     */
    public void addActionColumn(
            Callback<TreeTableColumn<T, String>, TreeTableCell<T, String>> value) {
        actionColumn = new JFXTreeTableColumn<>();
        actionColumn.getStyleClass().add("action-column");
        actionColumn.setCellFactory(value);
        actionColumn.setCellValueFactory(param -> new SimpleStringProperty(""));
        actionColumn.setPrefWidth(48);

        if (treeTableView.getColumns().indexOf(actionColumn) == -1)
            treeTableView.getColumns().add(actionColumn);
    }


    public boolean removeColumn(TreeTableColumn column) {
        return treeTableView.getColumns().remove(column);
    }

    @Override
    public void removeSelectionBox() {
        if (treeTableView.getColumns().indexOf(selectionColumn) != -1) {
            treeTableView.getColumns().remove(selectionColumn);
            selectionColumn = null;

            TreeTableColumn<T, ?> firstColumn = treeTableView.getColumns().get(0);
            firstColumn.getStyleClass().remove("after-checkbox-column");
            firstColumn.getStyleClass().add("first-column");
            firstColumn.prefWidthProperty().setValue(calculateWidthFromHeader(firstColumn, true));
        }
    }


    public boolean removeActionColumn() {
        return treeTableView.getColumns().remove(actionColumn);
    }


    @Override
    protected Node createPage(int pageIndex) {
        int fromIndex = pageIndex * pagination.getRowsPerPage();
        int toIndex = Math.min(fromIndex + pagination.getRowsPerPage(), viewHolders.size());

        treeTableView.setRoot(new RecursiveTreeItem<>(
                FXCollections.observableArrayList(viewHolders.subList(fromIndex, toIndex)),
                RecursiveTreeObject::getChildren));

        return treeTableView;
    }

    /**
     *
     */
    public void checkContent() {
        if (getParent() != null) {
            if (treeTableView.getRoot().getChildren().isEmpty()) {
                if (!showHeaderWithNoData) {
                    setTop(null);
                    setCenter(mEmptyState);
                } else {
                    setCenter(mEmptyState);
                }
            } else {
                setCenter(treeTableView);
            }
        } else {
            if (mEmptyState.getParent() != null) {
                setCenter(treeTableView);
            }
        }
    }


    public JFXCheckBox getSelectionHeader() {
        return selectionHeader;
    }

    public JFXTreeTableColumn<T, Boolean> getSelectionColumn() {
        return selectionColumn;
    }

    public JFXTreeTableColumn<T, String> getActionColumn() {
        return actionColumn;
    }


    private void setupFirstColumn() {
        TreeTableColumn<T, ?> firstColumn = treeTableView.getColumns().get(0);
        if (!firstColumn.equals(selectionColumn)) {
            firstColumn.getStyleClass().add("first-column");
            if (firstColumn.getStyleClass().contains("numeric-column"))
                firstColumn.prefWidthProperty().setValue(calculateWidthFromHeader(firstColumn, true));
        }
    }


    private void setupLastColumn() {
        if (treeTableView.getColumns().size() > 1) {
            TreeTableColumn<T, ?> lastColumn = treeTableView.getColumns().get(treeTableView.getColumns().size() - 1);
            lastColumn.getStyleClass().add("last-column");
        }
    }


    public Number calculateWidthFromHeader(TreeTableColumn column, boolean isFirstColumn) {
        FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
        Label label = new Label(column.getText());
        Number result = fontLoader.computeStringWidth(label.getText(), label.getFont()) + 2;

        if (isFirstColumn)
            return result.intValue() + 25; // 25 more in width due to the padding applied

        return result;
    }


    private void onSelectAll() {
        boolean allSelectedFlag = (selectedItems.size() == treeTableView.getRoot().getChildren().size());
        selectedItems.clear();

        if (!allSelectedFlag) {
            for (TreeItem<T> item : treeTableView.getRoot().getChildren()) {
                Selectable selectableItem = (Selectable) item.getValue();
                selectableItem.setSelected(true);
                selectedItems.get().add(selectableItem);
            }
        } else {
            for (TreeItem<T> item : treeTableView.getRoot().getChildren()) {
                Selectable selectableItem = (Selectable) item.getValue();
                selectableItem.setSelected(false);
            }

            selectedItems.clear();
        }
    }


    public synchronized void loadData() {
        dataProvider.setupDataContainer();
        viewHolders.clear();
        dataProvider.loadData();

        if (mEmptyState != null)
            checkContent();
    }


    public boolean add(T item) {
        return viewHolders.add(item);
    }


    public ObservableList<T> getData() {
        return viewHolders;
    }


    public ObservableList<Selectable> getSelectedItems() {
        return selectedItems.get();
    }

    public void setShowHeaderWithNoData(boolean showHeader) {
        this.showHeaderWithNoData = showHeader;
    }

    public ObservableList<TreeTableColumn<T, ?>> getColumns() {
        return treeTableView.getColumns();
    }

    public <S> void setColumns(JFXTreeTableColumn<T, S>... columns) {
        treeTableView.getColumns().addAll(columns);
    }
}
