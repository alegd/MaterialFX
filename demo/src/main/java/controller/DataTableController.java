package controller;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import io.alegd.materialtouch.Selectable;
import io.alegd.materialtouch.dataload.DataProvider;
import io.alegd.materialtouch.datatable.DataTable;
import io.datafx.controller.ViewController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import javax.annotation.PostConstruct;

@ViewController("/view/datatable.fxml")
public class DataTableController implements DataProvider {

    @FXML
    private DataTable<PersonViewHolder> dataTable;

    @FXML
    private JFXTreeTableColumn<PersonViewHolder, String> nameColumn;

    @FXML
    private JFXTreeTableColumn<PersonViewHolder, String> ageColumn;

    @FXML
    private JFXTreeTableColumn<PersonViewHolder, String> addressColumn;

    private Person[] persons = new Person[]{
            new Person("Ale", 25, ""),
            new Person("Adriana", 54, "")
    };

    @PostConstruct
    public void init() {
        dataTable.setSelectableItems(true);
        dataTable.setDataProvider(this);
        dataTable.loadData();
    }

    public void setupDataContainer() {
        dataTable.setupCellValueFactory(nameColumn, PersonViewHolder::nameProperty);
        dataTable.setupCellValueFactory(ageColumn, PersonViewHolder::ageProperty);
        dataTable.setupCellValueFactory(addressColumn, PersonViewHolder::addressProperty);
    }

    public void onItemSelected(MouseEvent event, Object item) {

    }

    public void loadData() {
        for (Person person : persons) {
            dataTable.add(new PersonViewHolder(person));
        }

        dataTable.prefHeightProperty().setValue(persons.length * 48 + 120);
    }


    public class PersonViewHolder extends RecursiveTreeObject<PersonViewHolder> implements Selectable {

        private StringProperty name;

        private StringProperty age;

        private StringProperty address;

        private BooleanProperty selected;


        public PersonViewHolder(Person person) {
            name = new SimpleStringProperty(person.getName());
            age = new SimpleStringProperty(String.valueOf(person.getAge()));
            address = new SimpleStringProperty(person.getAddress());

            selected = new SimpleBooleanProperty();
        }


        public String getName() {
            return name.get();
        }

        public StringProperty nameProperty() {
            return name;
        }

        public String getAge() {
            return age.get();
        }

        public StringProperty ageProperty() {
            return age;
        }

        public String getAddress() {
            return address.get();
        }

        public StringProperty addressProperty() {
            return address;
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
