package com.alegd.materialfx.dialog;

/**
 * @author J. Alejandro Guerra Denis
 */
public interface DialogActionListener {

    /**
     * Confirm the action of a dialog. The dialog should be closed after the action
     * is successfully confirmed or stay opened and show an error otherwise.
     *
     * @return True if the action was successfully confirmed
     */
    boolean onConfirm();

    boolean onCancel();
}
