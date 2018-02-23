package io.alegd.materialtouch;

import javafx.beans.property.BooleanProperty;

/**
 * @author J. Alejandro Guerra Denis
 */
public interface Selectable {

    BooleanProperty selectedProperty();

    boolean isSelected();

    void setSelected(boolean selected);
}
