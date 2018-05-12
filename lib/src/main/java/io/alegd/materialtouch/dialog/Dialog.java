package io.alegd.materialtouch.dialog;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import io.alegd.materialtouch.Constant;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;


/**
 * Common components to show emergent dialogs with Material Design in any class.
 * To correct us of this the class must implement IConfirmationAction.
 *
 * @author J. Alejandro Guerra Denis
 */
public class Dialog extends JFXDialog {

    private DialogActionListener actionListener;

    private JFXButton cancelButton;

    private JFXButton confirmButton;

    private JFXDialogLayout mDialogLayout;

    /**
     * Class constructor. Here initial configuration for the dialog is set, for
     * additional configurations or to overwrite default one, methods in this same
     * class ca be used.
     */
    private Dialog() {
        mDialogLayout = new JFXDialogLayout();
        setOverlayClose(false);

        cancelButton = new JFXButton("CANCELAR");
        cancelButton.getStyleClass().add("dialog-button");
        cancelButton.setOnMouseClicked(event -> handleCancel());

        confirmButton = new JFXButton("ACEPTAR");
        confirmButton.getStyleClass().add("dialog-button");
        confirmButton.setOnMouseClicked(event -> handleConfirm());

        mDialogLayout.setActions(cancelButton, confirmButton);
        setContent(mDialogLayout);

        setTransitionType(JFXDialog.DialogTransition.CENTER);
    }

    /**
     * Class constructor. Here initial configuration for the dialog is set, for
     * additional configurations or to overwrite default one, methods in this same
     * class ca be used.
     *
     * @param container The StackPane where the dialog is contained.
     */
    public Dialog(StackPane container) {
        this();
        setDialogContainer(container);
    }

    /**
     * Show dialog with content already passed as parameters.
     *
     * @param title             The title in dialog heading.
     * @param confirmActionText The text of the confirmation button in the dialog
     * @param content           The content in the body of the dialog.
     */
    public void show(String title, String confirmActionText, Node... content) {
        // In case actions were cleared, we just set it again
        mDialogLayout.setActions(cancelButton, confirmButton);
        // And then set everything else
        setDialogTitle(new Label(title));
        setDialogContent(content);
        setConfirmActionText(confirmActionText);
        show();
    }

    /**
     * Show dialog with content already passed as parameters, but no actions.
     *
     * @param title   The title in dialog heading.
     * @param content The content in the body of the dialog.
     */
    public void show(String title, Node... content) {
        mDialogLayout.getActions().clear();
        setDialogTitle(new Label(title));
        setDialogContent(content);
        mDialogLayout.getStyleClass().add("no-actions-dialog");
        show();
    }

    /**
     * Show dialog with content passed as parameters, but no heading or actions.
     *
     * @param content The content in the body of the dialog.
     */
    public void show(Node... content) {
        setDialogContent(content);
        mDialogLayout.getHeading().clear();
        mDialogLayout.getActions().clear();
        mDialogLayout.getStyleClass().add("no-actions-dialog");
        show();
    }

    /**
     * When user clicks confirm, the action define in the class that is implementing
     * {@link DialogActionListener} is executed, if the action was successfully executed,
     * we close the dialog, if not, the dialog stays open and developers should implement
     * how errors must be handled.
     */
    private void handleConfirm() {
        if (actionListener.onConfirm())
            close();
    }

    /**
     * When user clicks cancel we just close the open dialog.
     */
    private void handleCancel() {
        actionListener.onCancel();
        close();
    }

    /**
     * @param title The title of the dialog
     */
    public void setDialogTitle(Node title) {
        // If dialog doesn't close on overlay click we add a close button to it
        if (!isOverlayClose() && mDialogLayout.getActions().size() <= 0) {
            JFXButton closeButton = new JFXButton(null, Constant.getIcon("close", 14));
            closeButton.setOnMouseClicked(e -> close());
            setDialogTitle(title, closeButton);
        } else {
            mDialogLayout.setHeading(title);
        }
    }

    /**
     * @param title The title of the dialog
     */
    private void setDialogTitle(Node title, Node... rightItems) {
        BorderPane header = new BorderPane();
        header.setLeft(title);

        HBox rightItemsContainer = new HBox();
        rightItemsContainer.setPadding(new Insets(0, 0, 0, 24));
        rightItemsContainer.getChildren().addAll(rightItems);
        header.setRight(rightItemsContainer);

        mDialogLayout.setHeading(header);
    }


    public JFXDialogLayout getDialogLayout() {
        return mDialogLayout;
    }


    public void setDialogContent(Node... items) {
        mDialogLayout.setBody(items);
    }


    public void setConfirmActionText(String text) {
        confirmButton.setText(text.toUpperCase());
    }


    public void setActionListener(DialogActionListener actionListener) {
        this.actionListener = actionListener;
    }
}
