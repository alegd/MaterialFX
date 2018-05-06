package io.alegd.materialtouch.datatable;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXToolbar;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.jfoenix.effects.JFXDepthManager;
import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import io.alegd.materialtouch.DataContainer;
import io.alegd.materialtouch.Selectable;
import io.alegd.materialtouch.dataload.AsyncDataProvider;
import io.alegd.materialtouch.dataload.DataProvider;
import io.alegd.materialtouch.dataload.Exportable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

import java.util.function.Function;

/**
 * @param <T>
 * @author J. Alejandro Guerra Denis
 */
public class DataTable<T extends RecursiveTreeObject<T>> extends DataContainer<T> {

    private JFXTreeTableColumn<T, Boolean> selectionColumn;

    private JFXTreeTableColumn<T, String> actionColumn;

    private JFXCheckBox selectionHeader = new JFXCheckBox();

    private TreeTableView<T> treeTableView;

    /**
     * Class constructor.
     *
     * @param treeTableView The TreeTableView
     */
    private DataTable(TreeTableView<T> treeTableView) {
        dataContainer = treeTableView;
        parentPane = (Pane) dataContainer.getParent();

        this.treeTableView = treeTableView;

        this.treeTableView.setRoot(new RecursiveTreeItem<>(viewHolders, RecursiveTreeObject::getChildren));
        this.treeTableView.setShowRoot(false);

        if (parentPane != null)
            if (parentPane.getParent() != null)
                this.card = (Pane) parentPane.getParent().lookup(".card");

        setRowFactory();
        withEmptyState(null, null, true);
    }

    /**
     * Class constructor.
     *
     * @param treeTableView The TreeTableView
     * @param dataProvider  The class implementing {@link DataProvider} interface
     */
    public DataTable(TreeTableView<T> treeTableView, DataProvider dataProvider) {
        this(treeTableView);
        this.dataProvider = dataProvider;

        if (!(dataProvider instanceof AsyncDataProvider))
            wrapDataContainer();
    }

    /**
     * Class constructor.
     *
     * @param treeTableView The TreeTableView
     * @param dataProvider  The class implementing {@link DataProvider} interface
     * @param paginate      Whether to paginate or not
     */
    public DataTable(TreeTableView<T> treeTableView, DataProvider dataProvider, boolean paginate) {
        this(treeTableView, dataProvider);
        this.paginate = paginate;
    }

    /**
     * Class constructor.
     *
     * @param treeTableView The TreeTableView
     * @param exportable    The class implementing {@link Exportable} interface
     */
    public DataTable(TreeTableView<T> treeTableView, Exportable exportable) {
        this(treeTableView);
        this.exportable = exportable;
        this.dataProvider = exportable;

        if (!(dataProvider instanceof AsyncDataProvider))
            wrapDataContainer();
    }

    /**
     * Class constructor.
     *
     * @param treeTableView The TreeTableView
     * @param exportable    The class implementing {@link Exportable} interface
     */
    public DataTable(TreeTableView<T> treeTableView, Exportable exportable, boolean paginate) {
        this(treeTableView, exportable);
        this.paginate = paginate;
    }

    /**
     * Set click action for rows of a TreeTableView. On mouse click the onItemSelected
     * method is called, since that method is define through an interface, each class that
     * use this method is responsible for its own implementation.
     */
    private void setRowFactory() {
        this.treeTableView.setRowFactory(tv -> {
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

    /**
     * Setup column for item selection. Each cell in this column (including the column header)
     * show a CheckBox, users can click on those CheckBoxes to execute later any action they define
     * and add to {@link #contextualToolbar}.
     */
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

            contextualToolbar = new JFXToolbar();
            JFXDepthManager.setDepth(contextualToolbar, 0);
            contextualToolbar.getStyleClass().add("table-header");
            contextualToolbar.getStyleClass().add("alternate-table-header");
            mCToolbarTitle = new Label("contextual");
            contextualToolbar.setLeftItems(mCToolbarTitle);
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


    public void removeSelectionColumn() {
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
    @Override
    public void checkContent() {
        if (wrapper == null)
            wrapDataContainer();

        if (treeTableView.getParent() != null) {
            if (treeTableView.getRoot().getChildren().isEmpty()) {
                if (!showHeaderWithNoData) {
                    wrapper.setTop(null);
                    wrapper.setCenter(mEmptyState);
                    wrapper.setBottom(null);
                } else {
                    wrapper.setCenter(mEmptyState);
                }
            } else {
                wrapper.setCenter(treeTableView);
            }
        } else {
            if (mEmptyState.getParent() != null) {
                wrapper.setCenter(treeTableView);
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


    @Override
    public synchronized void loadData() {
        super.loadData();
        setupFirstColumn();
        setupLastColumn();
    }
}
