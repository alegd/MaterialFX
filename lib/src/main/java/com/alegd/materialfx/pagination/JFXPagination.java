package com.alegd.materialfx.pagination;

import javafx.scene.control.Pagination;
import javafx.scene.control.Skin;

public class JFXPagination extends Pagination {

    private int rowsPerPage = 10;

    private int itemsCount;


    public JFXPagination() {
        super();
    }


    public JFXPagination(int itemsCount) {
        super(1);
        this.itemsCount = itemsCount;
        setPageCount(itemsCount / rowsPerPage + 1);
    }


    public JFXPagination(int itemsCount, int pageIndex) {
        super(1, pageIndex);
        this.itemsCount = itemsCount;
        setPageCount(itemsCount / rowsPerPage + 1);
    }


    @Override
    protected Skin<?> createDefaultSkin() {
        return new JFXPaginationSkin(this);
    }


    public int getRowsPerPage() {
        return rowsPerPage;
    }


    public void setRowsPerPage(int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }


    public int getItemsCount() {
        return itemsCount;
    }


    public void setItemsCount(int itemsCount) {
        this.itemsCount = itemsCount;
    }
}
