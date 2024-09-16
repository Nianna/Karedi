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
    requires javasound.aac;
    requires org.fxmisc.flowless;
    requires wellbehavedfx;
    requires com.github.trilarion.sound;
    requires nianna.hyphenator;

    opens com.github.nianna.karedi to javafx.graphics;
    opens com.github.nianna.karedi.controller to javafx.fxml;
    opens com.github.nianna.karedi.dialog to javafx.fxml;
    opens com.github.nianna.karedi.display to javafx.fxml;
    opens com.github.nianna.karedi.control to javafx.fxml;
}