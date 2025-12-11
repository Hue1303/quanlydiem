package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;

import utils.DatabaseConnection;
import utils.UserSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField txtUser;
    @FXML private PasswordField txtPass;
    @FXML private Button btnLogin;
    @FXML private Label lblError;

    public void handleLogin() {
        String user = txtUser.getText().trim();
        String pass = txtPass.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            lblError.setText("Vui lòng nhập tài khoản và mật khẩu!");
            return;
        }

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.connect();

        if (connectDB == null) {
            lblError.setText("Lỗi kết nối CSDL! Kiểm tra XAMPP/MySQL.");
            return;
        }

        try {
            // 1. KIỂM TRA GIÁO VIÊN
            String sqlGV = "SELECT * FROM GIAO_VIEN WHERE TEN_DANG_NHAP = ? AND MAT_KHAU = ?";
            PreparedStatement statementGV = connectDB.prepareStatement(sqlGV);
            statementGV.setString(1, user);
            statementGV.setString(2, pass);
            ResultSet resultGV = statementGV.executeQuery();

            if (resultGV.next()) {
                String hoTen = resultGV.getString("HO_TEN");
                UserSession.taiKhoan = user;
                UserSession.hoTen = hoTen;

                UserSession.vaiTro = resultGV.getString("VAI_TRO");
                UserSession.chuyenMon = resultGV.getString("CHUYEN_MON");

                lblError.setText("Đăng nhập thành công! Xin chào: " + hoTen);
                moManHinh("/view/TeacherView.fxml", "Quản Lý Giáo Viên");
                return;
            }

            // 2. KIỂM TRA HỌC SINH
            String sqlHS = "SELECT * FROM HOC_SINH WHERE MA_HOC_SINH = ? AND NGAY_SINH = ?";
            PreparedStatement statementHS = connectDB.prepareStatement(sqlHS);
            statementHS.setString(1, user);
            statementHS.setString(2, pass);
            ResultSet resultHS = statementHS.executeQuery();

            if (resultHS.next()) {
                String tenHS = resultHS.getString("HO_TEN");

                UserSession.taiKhoan = user;
                UserSession.hoTen = tenHS;
                lblError.setText("Xin chào: " + tenHS);
                moManHinh("/view/StudentView.fxml", "Cổng Thông Tin Học Sinh");
            } else {
                lblError.setText("Sai tài khoản hoặc mật khẩu!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            lblError.setText("Có lỗi xảy ra trong code!");
        } finally {
            // Đảm bảo đóng kết nối CSDL
            try {
                if (connectDB != null) connectDB.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void moManHinh(String fxmlPath, String title) {
        try {
            // 1. Đóng cửa sổ Login cũ
            if (btnLogin != null && btnLogin.getScene() != null) {
                btnLogin.getScene().getWindow().hide();
            } else {
                return;
            }

            // 2. Mở cửa sổ mới
            URL url = getClass().getResource(fxmlPath);

            if (url == null) {
                System.err.println("LỖI TẢI GIAO DIỆN: KHÔNG TÌM THẤY file FXML tại đường dẫn: " + fxmlPath);
                return;
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("LỖI KHI TẢI FXML VÀ KHỞI TẠO CONTROLLER: " + fxmlPath);
        }
    }
}