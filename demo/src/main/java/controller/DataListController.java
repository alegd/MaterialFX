package controller;

import com.jfoenix.controls.JFXButton;
import io.alegd.materialtouch.Constant;
import io.alegd.materialtouch.DataList;
import io.alegd.materialtouch.Selectable;
import io.alegd.materialtouch.dataload.DataProvider;
import io.datafx.controller.ViewController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import javax.annotation.PostConstruct;

import static io.alegd.materialtouch.Constant.getIcon;

@ViewController("/view/data_list.fxml")
public class DataListController implements DataProvider {

    @FXML
    private DataList<PizzaViewHolder> dataList;

    private String[] names = new String[]{
            "Jamon", "Napolitana", "Reynier", "Claudia", "Rafael", "Grettel", "Adriana", "Jose Pablo", "Carlos", "Pepperoni"
    };

    private Pizza[] pizzas = new Pizza[11];

    private JFXButton deleteButton;


    @PostConstruct
    public void init() {
        fillPersons();
        setupDeleteButton();

        dataList.setDataProvider(this);
        dataList.setSelectableItems(true);
        dataList.showInPages(true);
        dataList.getContextualHeader().setRightItems(deleteButton);
        dataList.loadData();
    }


    private void fillPersons() {
        int randomIndex;

        for (int i = 0; i < pizzas.length; i++) {
            randomIndex = (int) (Math.random() * 10);
            pizzas[i] = new Pizza(names[randomIndex]);
        }
    }


    private void setupDeleteButton() {
        deleteButton = new JFXButton(null, Constant.getIcon("delete", 18));
        deleteButton.setPrefSize(24, 24);
        deleteButton.setOnMouseClicked(e-> System.out.println("PUFF!!!"));
    }


    public void setupDataContainer() {
    }


    public void onItemSelected(MouseEvent event, Object item) {
        System.out.println("Item clicked: " + ((PizzaViewHolder) item).getName());

    }

    public void loadData() {
        for (Pizza pizza : pizzas) {
            dataList.add(new PizzaViewHolder(pizza));
        }

        dataList.prefHeightProperty().setValue(pizzas.length * 48 + 120);
    }


    public class PizzaViewHolder extends HBox implements Selectable {

        private Label name;

        private JFXButton button;

        private BooleanProperty selected;

        /**
         * @return
         */
        public PizzaViewHolder(Pizza pizza) {
            name = new Label(pizza.getName());
            Pane spanPane = new Pane();
            HBox.setHgrow(spanPane, Priority.ALWAYS);
            setStyle("-fx-padding: 0 8px;");
            button = new JFXButton(null, getIcon("share", 18));
            button.getGraphic().setRotate(90);

            this.getChildren().addAll(name, spanPane, button);
            this.setSpacing(16);
            this.setAlignment(Pos.CENTER);
            this.getStyleClass().addAll("list-view-primary-text");

            selected = new SimpleBooleanProperty();
        }


        public Label getName() {
            return name;
        }

        public JFXButton getButton() {
            return button;
        }


        @Override
        public BooleanProperty selectedProperty() {
            return selected;
        }

        @Override
        public boolean isSelected() {
            return selected.get();
        }

        @Override
        public void setSelected(boolean selected) {
            this.selected.set(selected);
        }
    }
}
