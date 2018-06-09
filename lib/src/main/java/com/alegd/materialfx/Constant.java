package com.alegd.materialfx;

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

    private static final String ICONS_FILE = "MaterialIcons-Regular.svg";

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
            double mWidth = bounds.getWidth() / 1.5;
            double mHeight = bounds.getHeight() / 1.25;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
