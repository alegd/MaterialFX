package io.alegd.materialtouch;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToolbar;
import io.alegd.materialtouch.dataload.DataProvider;
import io.alegd.materialtouch.dataload.Exportable;
import io.alegd.materialtouch.pagination.JFXPagination;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author J. Alejandro Guerra Denis
 */
public abstract class DataContainer<T> {

    JFXToolbar mainToolbar;

    protected JFXToolbar contextualToolbar;

    protected Label mCToolbarTitle;

    protected Exportable exportable;

    protected DataProvider dataProvider;

    protected Control dataContainer;

    protected Pane card;

    protected VBox mEmptyState;

    protected boolean showHeaderWithNoData;

    protected ObservableList<T> viewHolders;

    protected ListProperty<Selectable> selectedItems;

    protected boolean paginate;

    protected JFXPagination pagination;

    protected Pane parentPane;

    protected BorderPane wrapper;


    protected DataContainer() {
        if (viewHolders == null)
            viewHolders = FXCollections.observableArrayList();

        if (selectedItems == null)
            selectedItems = new SimpleListProperty<>(FXCollections.observableArrayList());

        selectedItems.addListener((observable, oldValue, newValue) -> {
            if (newValue.size() > 0) {
                if (newValue.size() > 1)
                    mCToolbarTitle.setText(newValue.size() + " elementos seleccionados");
                else
                    mCToolbarTitle.setText("1 elemento seleccionado");

                ((BorderPane) dataContainer.getParent()).setTop(contextualToolbar);
            } else {
                ((BorderPane) dataContainer.getParent()).setTop(mainToolbar);
            }
        });
    }

    /**
     *
     */
    public synchronized void loadData() {
        dataProvider.setupDataContainer();
        viewHolders.clear();
        dataProvider.loadData();

        if (mEmptyState != null)
            checkContent();
    }

    /**
     *
     */
    public synchronized void wrapDataContainer() {
        if (parentPane == null) {
            if (dataContainer.getParent() != null)
                parentPane = (Pane) dataContainer.getParent();
        }
        // if parentPane is a BorderPane we got ourselves already a wrapper
        if (!(parentPane instanceof BorderPane)) {
            if (wrapper == null) {
                wrapper = new BorderPane(dataContainer);
                if (parentPane != null)
                    parentPane.getChildren().add(wrapper);
            }
        } else {
            wrapper = (BorderPane) parentPane;
        }

        if (viewHolders.size() > 10 && paginate) {
            pagination = new JFXPagination(getData().size(), 0);
            pagination.setPageFactory(this::createPage);
            wrapper.setCenter(pagination);
        }

        if (parentPane instanceof VBox)
            VBox.setVgrow(wrapper, Priority.ALWAYS);

    }

    /**
     * Add a header (toolbar) to data table. Usually a data table header has a title at the
     * most left side and one or more actions (normally {@link Button}) at the most right side.
     *
     * @param mainTitle The title in the header for the data table
     * @param actions   The actions in the header for the data table
     */
    public synchronized void withHeader(String mainTitle, Node... actions) {
        mainToolbar = new JFXToolbar();
        mainToolbar.getStyleClass().add("table-header");
        mainToolbar.setLeftItems(new Label(mainTitle));

        if (exportable != null) {
            JFXButton printButton = new JFXButton(null,
                    Constant.getIcon("print", 20, Color.GRAY));
            printButton.setTooltip(new Tooltip("Imprimir datos"));
            printButton.setOnMouseClicked(event -> exportable.printData());

            JFXButton exportButton = new JFXButton(null,
                    Constant.getIcon("file_download", 18, Color.GRAY));
            exportButton.setTooltip(new Tooltip("Exportar datos"));
            exportButton.setOnMouseClicked(event -> exportable.exportData());

            List<Node> moreActions = new ArrayList<>();
            moreActions.add(printButton);
            moreActions.add(exportButton);
            moreActions.addAll(Arrays.asList(actions));
            mainToolbar.setRightItems((Node[]) moreActions.toArray(new Node[actions.length + 1]));
        } else {
            mainToolbar.setRightItems(actions);
        }

        if (parentPane instanceof BorderPane)
            ((BorderPane) parentPane).setTop(mainToolbar);
        else {
            wrapDataContainer();
            wrapper.setTop(mainToolbar);
        }
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
     * Add an empty state to the data table. Empty states occur when an item’s content can’t
     * be shown for any reason.
     *
     * @param title    The title for the empty state card
     * @param subtitle The subtitle for the empty state card, usually a suggestion to get
     *                 rid of the emptiness.
     */
    public synchronized void withEmptyState(String title, String subtitle) {
        Label titleLabel = new Label("Aún no existen datos");
        titleLabel.getStyleClass().add("main-text");

        Label subtitleLabel = new Label("Haz click en el signo + para añadir nuevos datos");
        subtitleLabel.getStyleClass().add("suggestion-text");

        if (title != null)
            if (!title.equalsIgnoreCase(""))
                titleLabel.setText(title);

        if (subtitle != null)
            if (!subtitle.equalsIgnoreCase(""))
                subtitleLabel.setText(subtitle);

        mEmptyState = new VBox(8, titleLabel, subtitleLabel);
        mEmptyState.getStyleClass().add("empty-state");
        mEmptyState.setAlignment(Pos.CENTER);
    }


    protected abstract Node createPage(int pageIndex);


    public abstract void checkContent();


    public abstract void addSelectionBox();


    public synchronized boolean add(T item) {
        return viewHolders.add(item);
    }


    public ObservableList<T> getData() {
        return viewHolders;
    }


    public void setViewHolders(ObservableList<T> viewHolders) {
        this.viewHolders = viewHolders;
    }


    public ObservableList<Selectable> getSelectedItems() {
        return selectedItems.get();
    }


    public JFXToolbar getMainToolbar() {
        return mainToolbar;
    }


    public JFXToolbar getContextualToolbar() {
        return contextualToolbar;
    }


    public BorderPane getWrapper() {
        return wrapper;
    }


    public void setShowHeaderWithNoData(boolean showHeader) {
        this.showHeaderWithNoData = showHeader;
    }
}
