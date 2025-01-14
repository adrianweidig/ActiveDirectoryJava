
module headless.entertainment.adj {
    requires javafx.controls;
    requires javafx.fxml;

    requires jpro.webapi;

    requires java.naming;

    requires com.dlsc.formsfx;
    requires org.controlsfx.controls;
    requires org.apache.commons.lang3;
    requires org.apache.poi.ooxml;


    opens headless.entertainment.adj to javafx.fxml, javafx.graphics;
    exports headless.entertainment.adj to javafx.fxml;

    opens headless.entertainment.adj.Controller to javafx.fxml;
    exports headless.entertainment.adj.Controller to javafx.fxml;

    opens headless.entertainment.adj.Model to javafx.fxml;
    exports headless.entertainment.adj.Model to javafx.fxml;

    opens headless.entertainment.adj.Client to javafx.fxml;
    exports headless.entertainment.adj.Client to javafx.fxml;

    opens headless.entertainment.adj.Controller.DialogControllers to javafx.fxml;
    exports headless.entertainment.adj.Controller.DialogControllers to javafx.fxml;

    opens headless.entertainment.adj.Enums to javafx.fxml;
    exports headless.entertainment.adj.Enums to javafx.fxml;

    //On compilation Error add to VM-Args:
    //--add-opens=javafx.controls/javafx.scene.control.skin=org.controlsfx.controls

}