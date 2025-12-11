package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {

            URL url = getClass().getResource("/view/Login.fxml");

            if (url == null) {
                System.out.println("LỖI: Không tìm thấy file Login.fxml!");
                System.out.println("Vui lòng kiểm tra lại thư mục /view/Login.fxml");
                return;
            }
            Parent root = FXMLLoader.load(url);
            primaryStage.setTitle("HỆ THỐNG QUẢN LÝ ĐIỂM HỌC SINH");
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);

            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}