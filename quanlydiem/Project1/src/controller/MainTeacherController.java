package controller;

import utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/*
 * Controller chính cho màn hình giáo viên
 * Chỉ lo việc hiển thị tên giáo viên và quản lý các Tab
 */
public class MainTeacherController implements Initializable {

    @FXML private Label lblTaiKhoanGV;
    @FXML private Tab tabQuanLyHS;
    @FXML private Tab tabNhapDiem;
    @FXML private Tab tabThongKe;
    @FXML private Tab tabTaiKhoan;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (lblTaiKhoanGV != null) {
            lblTaiKhoanGV.setText("Giáo viên: " + UserSession.hoTen);
        }

        // Phân quyền: Nếu là GV bộ môn thì không cho truy cập tab Quản lý học sinh
        if (UserSession.vaiTro != null && UserSession.vaiTro.equals("GV_BO_MON")) {
            if (tabQuanLyHS != null) {
                tabQuanLyHS.setDisable(true);
            }
        }
    }
}