package ru.sourcecode;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    Button goButton;
    @FXML
    Button resetButton;
    @FXML
    TextField textField1;
    @FXML
    TextField textField2;
    @FXML
    ImageView background;
    @FXML
    ImageView car1;
    @FXML
    ImageView car2;
    @FXML
    ImageView banner;
    Point startPos;
    Point endPos;
    boolean isFatal1;
    boolean isFatal2;
    boolean play;

    public void goClicked() {
        if (car1.getTranslateX() > startPos.x || car2.getTranslateX() > startPos.x) resetClicked();
        if (play) return;
        play = true;
        isFatal1 = false;
        isFatal2 = false;

        Integer time1 = Integer.parseInt(textField1.getText());
        Integer time2 = Integer.parseInt(textField2.getText());
        if (time1 / time2 >= 2) {
            isFatal2 = true;
        } else if (time2 / time1 >= 2) {
            isFatal1 = true;
        }
        if (isFatal1) {
            startCar(car1, time1, isFatal1);
            startCar(car2, time2, isFatal2);
        } else if (isFatal2) {
            startCar(car2, time2, isFatal2);
            startCar(car1, time1, isFatal1);
        } else {
            startCar(car1, time1, false);
            startCar(car2, time2, false);
        }
        isFatal1 = false;
        isFatal2 = false;
        play = false;
    }

    private void startCar(ImageView car, int time, boolean isFatal) {
        Duration duration = Duration.millis(time * 1000);
        TranslateTransition transition = new TranslateTransition(duration, car);
        transition.setByX(endPos.x - car.getFitWidth() - 150);
        transition.setAutoReverse(false);
        transition.play();
        transition.setOnFinished(event->{
            if (isFatal) {
                playSound();
                showBanner();
            }
        });
    }

    public void resetClicked() {
        car1.setTranslateX(startPos.x);
        car2.setTranslateX(startPos.x);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startPos = new Point((int) car1.getLayoutX(), (int)car1.getLayoutY());
        endPos = new Point((int)background.getFitWidth(),(int)background.getFitHeight());
    }
    private void playSound() {
        new Thread(()->{
            try {
                FileInputStream fis = new FileInputStream(
                        System.getProperty("user.dir") + File.separator + "fatality.mp3");
                Player playMP3 = new Player(fis);
                playMP3.play();
            } catch (FileNotFoundException | JavaLayerException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showBanner() {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(banner);
        fadeTransition.setDuration(new Duration(300));
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.setCycleCount(1);
        fadeTransition.setAutoReverse(false);
        fadeTransition.play();
        fadeTransition.setOnFinished(event -> {
            new Thread(()->{
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                banner.setOpacity(0);
            }).start();
        });
    }

}
