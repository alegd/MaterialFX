package io.alegd.materialtouch;

import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.svg.SVGGlyphLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.InputStream;

/**
 * @author Alejandro Guerra Denis on 9/3/2017.
 */
public class Constant {

    public static final String PRIMARY_STAGE = "stage";
    public static final String ROOT = "content-root";
    public static final String BUDGET_ROOT = "budget-root";
    public static final String CONTENT_MAIN_FLOW = "ContentMainFlow";
    public static final String CONTENT_MAIN_FLOW_HANDLER = "ContentMainFlowHandler";
    public static final String INNER_FLOW = "ContentInnerFlow";
    public static final String INNER_FLOW_HANDLER = "ContentInnerFlowHandler";
    public static final String BUDGET_INNER_FLOW = "BudgetInnerFlow";
    public static final String BUDGET_INNER_FLOW_HANDLER = "BudgetInnerFlowHandler";
    public static final String CONTENT_PANE = "ContentPane";
    public static final String DRAWER = "Drawer";
    public static final String SETTINGS_PATH = ".config";
    public static final String LAST_SELECTED_VIEW = "LastSelectedView";
    public static final String LAST_SELECTED_INDEX = "LastSelectedIndex";
    public static final String WIZARD  = "wizard";

    public static final String ICONS_FILE = "MaterialIcons-Regular.svg";

    public static final String BUDGET_DATA = "receipt-data";
    public static final String BUDGET_FORM_VIEW = "receipt-form";
    public static final String RECEIPT_DETAIL_VIEW = "receipt-detail";
    public static double mWidth = 960;
    public static double mHeight = 600;
    public static String[] months = {"enero", "febrero", "marzo", "abril", "mayo",
            "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"};


    /**
     * @param name the icon name according to Google Material Design Icons.
     * @return the icon.
     */
    public static SVGGlyph getIcon(String name, double size, Color color) {
        SVGGlyph icon = getIcon(name, size);
        icon.setFill(color);
        return icon;
    }

    /**
     * @param name the icon name according to Google Material Design Icons.
     * @return the icon.
     */
    public static SVGGlyph getIcon(String name, double size) {
        SVGGlyph icon = null;
        try {
            InputStream is = Constant.class.getResourceAsStream("/fonts/" + ICONS_FILE);
            SVGGlyphLoader.loadGlyphsFont(is, ICONS_FILE);
            icon = SVGGlyphLoader.getIcoMoonGlyph(ICONS_FILE + "." + name);
            icon.setSize(size);
            icon.setFill(Color.rgb(97, 97, 97));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return icon;
    }


    public static void centerOnScreen(Stage primaryStage) {
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);
    }

    public static void calculateScreenBounds() {
        try {
            Rectangle2D bounds = Screen.getScreens().get(0).getBounds();
            mWidth = bounds.getWidth() / 1.5;
            mHeight = bounds.getHeight() / 1.25;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
