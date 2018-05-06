package io.alegd.materialtouch.pagination;

import com.jfoenix.controls.JFXComboBox;
import com.sun.javafx.scene.control.skin.PaginationSkin;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.layout.HBox;

import static java.lang.Math.min;

public class JFXPaginationSkin extends PaginationSkin {

    private HBox controlBox;

    private Button prev;

    private Button next;

    private Button first;

    private Button last;

    private JFXComboBox<String> rowsPerPage;

    private Label rowPerPageLabel = new Label("Rows per page:");

    private Label indexShowingLabel;

    private JFXPagination pagination;


    private void patchNavigation() {
        pagination = (JFXPagination) getSkinnable();
        Node control = pagination.lookup(".control-box");

        if (!(control instanceof HBox))
            return;

        controlBox = (HBox) control;
        prev = (Button) controlBox.getChildren().get(0);
        next = (Button) controlBox.getChildren().get(controlBox.getChildren().size() - 1);

        rowsPerPage = new JFXComboBox<>(
                FXCollections.observableArrayList("10", "25", "50", "100"));
        rowsPerPage.getSelectionModel().select(String.valueOf(pagination.getRowsPerPage()));
        rowsPerPage.getStyleClass().add("rows-per-page-combo-box");
        rowsPerPage.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    pagination.setRowsPerPage(Integer.parseInt(newValue));
                    pagination.setPageCount(pagination.getItemsCount() / Integer.parseInt(newValue) + 1);
                });

        first = new Button("A");
        first.setOnAction(e -> {
            pagination.setCurrentPageIndex(0);
        });
        first.disableProperty().bind(
                pagination.currentPageIndexProperty().isEqualTo(0));

        last = new Button("Z");
        last.setOnAction(e -> {
            pagination.setCurrentPageIndex(pagination.getPageCount());
        });
        last.disableProperty().bind(
                pagination.currentPageIndexProperty().isEqualTo(
                        pagination.getPageCount() - 1));

        String s = getShownItemsInfo();
        indexShowingLabel = new Label(s);
        indexShowingLabel.setPadding(new Insets(0, 32, 0, 32));
        pagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
            String ss = getShownItemsInfo();
            indexShowingLabel.setText(ss);
        });

        ListChangeListener childrenListener = c -> {
            while (c.next()) {
                // implementation detail: when nextButton is added, the setup is complete
                if (c.wasAdded() && !c.wasRemoved() // real addition
                        && c.getAddedSize() == 1 // single addition
                        && c.getAddedSubList().get(0) == next) {
                    addCustomNodes();
                }
            }
        };
        controlBox.getChildren().addListener(childrenListener);
        addCustomNodes();
    }


    private String getShownItemsInfo() {
        int upperLimit = min((pagination.getCurrentPageIndex() + 1) * pagination.getRowsPerPage(),
                pagination.getItemsCount());
        int lowerLimit;

        if (pagination.getCurrentPageIndex() == pagination.getPageCount() - 1)
            lowerLimit = (pagination.getPageCount() - 1) * pagination.getRowsPerPage() + 1;
        else
            lowerLimit = upperLimit - pagination.getRowsPerPage() + 1;

        return lowerLimit + "-" + upperLimit + " of " + pagination.getItemsCount();
    }


    protected void addCustomNodes() {
        // guarding against duplicate child exception
        // (some weird internals that I don't fully understand...)
        if (rowPerPageLabel.getParent() == controlBox) return;

        controlBox.getChildren().add(0, rowPerPageLabel);
        controlBox.getChildren().add(1, rowsPerPage);
        controlBox.getChildren().add(2, indexShowingLabel);
//        controlBox.getChildren().add(2, first);
//        controlBox.getChildren().add(last);
    }

    /**
     * @param pagination
     */
    public JFXPaginationSkin(Pagination pagination) {
        super(pagination);
        patchNavigation();
    }
}
