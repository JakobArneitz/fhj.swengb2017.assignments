package at.fhj.swengb.apps.battleship;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.image.Image;

import java.awt.*;


public class AlertBox {

    // nice shooting sound for destroying the ship

    public static void shoot() {
//        Media cannonshoot = new Media("file:///C:/workspace/fhj.swengb2017.assignments/battleship/jfx/src/main/resources/at/fhj/swengb/apps/battleship/jfx/Cannon+2.mp3");
//        MediaPlayer cannonplay = new MediaPlayer(cannonshoot);
//        cannonplay.play();

    }

    //nice sploosh sound for missing the target

    public static void sploosh() {

//        Media sploosh = new Media("file:///C:/workspace/fhj.swengb2017.assignments/battleship/jfx/src/main/resources/at/fhj/swengb/apps/battleship/jfx/short_heavy_splash.mp3");
//        MediaPlayer splooshie = new MediaPlayer(sploosh);
//        splooshie.play();

    }

    public static void explode() {
//        Media exploded = new Media("file:///C:/workspace/fhj.swengb2017.assignments/battleship/jfx/src/main/resources/at/fhj/swengb/apps/battleship/jfx/Explosion+8.mp3");
//        MediaPlayer exploding = new MediaPlayer(exploded);
//        exploding.play();

    }

    public static void display(String title, String message) {


        Stage window = new Stage(StageStyle.UTILITY);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
        window.setFullScreen(true);
        Label label = new Label();
        label.setPrefSize(1000,300);
        label.setAlignment(Pos.CENTER);
        label.setText(message);
        label.setFont(Font.font("Times-New-Roman",20));
        Button closeButton = new Button("Ja");
        closeButton.setPrefSize(400,200);
        closeButton.setStyle("-fx-background-color: #add8e6");
        closeButton.setOnAction(e -> window.close());






        VBox layout = new VBox(10);
        layout.setId("pane");
        layout.getChildren().addAll(label,closeButton);
        layout.setBackground(new Background(new BackgroundFill(Color.rgb(17, 119, 255), CornerRadii.EMPTY, Insets.EMPTY)));
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
        shoot();
    }





}
