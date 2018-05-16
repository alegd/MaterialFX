import com.jfoenix.controls.JFXDecorator;
import controller.DataListController;
import controller.DataTableController;
import io.alegd.materialtouch.datatable.DataTable;
import io.datafx.controller.ViewConfiguration;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{

    public void start(Stage primaryStage) throws Exception {
        ViewFlowContext context = new ViewFlowContext();
        ViewConfiguration viewConfiguration = new ViewConfiguration();
        // Spanish by default
        // Create and register in context the main flow
        Flow mainFlow = new Flow(DataTableController.class);
        // Create and register in context the main flow handler
        FlowHandler mainFlowHandler = new FlowHandler(mainFlow, context, viewConfiguration);

        DefaultFlowContainer container = new DefaultFlowContainer();
        mainFlowHandler.start(container);

        JFXDecorator decorator = new JFXDecorator(primaryStage, container.getView(), false, false, true);
        decorator.setCustomMaximize(true);

        Scene scene = new Scene(decorator, 600, 450);
        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.add(Main.class.getResource("/css/demo.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
