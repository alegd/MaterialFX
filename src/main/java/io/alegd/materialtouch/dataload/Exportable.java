package io.alegd.materialtouch.dataload;

import java.util.Map;

/**
 * @author J. Alejandro Guerra Denis
 */
public interface Exportable extends DataProvider {

    /**
     * Build a map with parameters to be use in reports.
     *
     * @return The map with all parameter necessaries to generate a report.
     */
    Map<String, Object> getReportParams();

    /**
     * Print data displayed in data table.
     */
    void printData();

    /**
     * Generate and export a document containing the data shown in data table.
     */
    void exportData();
}
