package controller;

import utils.QuanLyDuLieu;
import utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class AccountController implements Initializable {

    @FXML private PasswordField txtPassOld;
    @FXML private PasswordField txtPassNew;
    @FXML private PasswordField txtPassConfirm;
    @FXML private Label lblThongTinTK;

    private QuanLyDuLieu quanLyDuLieu;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        quanLyDuLieu = new QuanLyDuLieu();

        // Hiển thị thông tin tài khoản
        if (lblThongTinTK != null) {
            lblThongTinTK.setText(
                    "Tài khoản: " + UserSession.taiKhoan + "\n" +
                            "Họ tên: " + UserSession.hoTen + "\n" +
                            "Vai trò: " + UserSession.vaiTro
            );
        }
    }

    @FXML
    public void handleChangePassword() {
        if (txtPassOld.getText().isEmpty() ||
                txtPassNew.getText().isEmpty() ||
                txtPassConfirm.getText().isEmpty()) {
            showAlert("Vui lòng điền đầy đủ thông tin!");
            return;
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu phải giống nhau
        if (!txtPassNew.getText().equals(txtPassConfirm.getText())) {
            showAlert("Mật khẩu mới và xác nhận mật khẩu không khớp!");
            return;
        }

        // Kiểm tra mật khẩu mới phải khác mật khẩu cũ
        if (txtPassOld.getText().equals(txtPassNew.getText())) {
            showAlert("Mật khẩu mới phải khác mật khẩu cũ!");
            return;
        }

        String kq = quanLyDuLieu.doiMatKhau(
                UserSession.taiKhoan,
                txtPassOld.getText(),
                txtPassNew.getText()
        );

        if ("OK".equals(kq)) {
            showAlert("Đổi mật khẩu thành công!");
            txtPassOld.clear();
            txtPassNew.clear();
            txtPassConfirm.clear();
        } else if ("SAI_MK".equals(kq)) {
            showAlert("Mật khẩu cũ không đúng!");
        } else {
            showAlert("Lỗi hệ thống!");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }
}