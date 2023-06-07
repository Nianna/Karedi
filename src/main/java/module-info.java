module karedi {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires jlayer;
    requires java.logging;
    requires java.desktop;
    requires mp3agic;
    requires java.prefs;
    requires jfugue;
    requires org.fxmisc.richtext;
    requires org.fxmisc.flowless;
    requires wellbehavedfx;

    opens com.github.nianna.karedi to javafx.graphics;
    opens com.github.nianna.karedi.controller to javafx.fxml;
    opens com.github.nianna.karedi.display to javafx.fxml;
    opens com.github.nianna.karedi.control to javafx.fxml;
}