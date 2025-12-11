package controller;

import entity.BangDiem;
import utils.DatabaseConnection;
import utils.QuanLyDuLieu;
import utils.UserSession;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.util.List;
import java.util.ResourceBundle;

public class GradeManagerController implements Initializable {

    @FXML private ComboBox<String> cbMonHoc;
    @FXML private ComboBox<String> cbHocKy;
    @FXML private TableView<BangDiem> tableDiem;
    @FXML private TableColumn<BangDiem, String> colDiemMaHS;
    @FXML private TableColumn<BangDiem, String> colDiemTen;
    @FXML private TableColumn<BangDiem, Float> colDiemTB; // Điểm TB Học Kỳ

    @FXML private Label lblTenHSDiem;
    @FXML private TextField txtDiemMieng;
    @FXML private TextField txtDiem15p;
    @FXML private TextField txtDiem1Tiet;
    @FXML private TextField txtDiemThi;

    @FXML private ComboBox<String> cbLopHoc;

    // Các cột Điểm Chi Tiết
    @FXML private TableColumn<BangDiem, Float> colDiemMieng;
    @FXML private TableColumn<BangDiem, Float> colDiem15p;
    @FXML private TableColumn<BangDiem, Float> colDiem1Tiet;
    @FXML private TableColumn<BangDiem, Float> colDiemThi;

    // Cột Điểm TB Cả Năm
    @FXML private TableColumn<BangDiem, Float> colDiemTBNam;

    private QuanLyDuLieu quanLyDuLieu;
    private String currentMaHS = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        quanLyDuLieu = new QuanLyDuLieu();
        cauHinhBangDiem();
        loadLopHoc();
        loadMonHoc();
        loadHocKy();
        caiDatSuKien();
    }

    private void cauHinhBangDiem() {
        colDiemMaHS.setCellValueFactory(new PropertyValueFactory<>("maHS"));
        colDiemTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colDiemTB.setCellValueFactory(new PropertyValueFactory<>("diemTBHocKy"));
        colDiemMieng.setCellValueFactory(new PropertyValueFactory<>("diemMieng"));
        colDiem15p.setCellValueFactory(new PropertyValueFactory<>("diem15Phut"));
        colDiem1Tiet.setCellValueFactory(new PropertyValueFactory<>("diem1Tiet"));
        colDiemThi.setCellValueFactory(new PropertyValueFactory<>("diemThi"));
        colDiemTBNam.setCellValueFactory(new PropertyValueFactory<>("diemTBCaNam"));
    }

    private void loadLopHoc() {
        if (cbLopHoc == null) return;
        cbLopHoc.getItems().clear();
        try (Connection conn = new DatabaseConnection().connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT MA_LOP FROM LOP_HOC")) {

            while (rs.next()) {
                cbLopHoc.getItems().add(rs.getString("MA_LOP"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cbLopHoc.setValue(cbLopHoc.getItems().isEmpty() ? null : cbLopHoc.getItems().get(0)); // Chọn lớp đầu tiên
    }

    private void loadMonHoc() {
        if (cbMonHoc == null) return;
        cbMonHoc.getItems().clear();
        if ("GV_BO_MON".equals(UserSession.vaiTro) && UserSession.chuyenMon != null) {
            cbMonHoc.getItems().add(UserSession.chuyenMon);
            cbMonHoc.setValue(UserSession.chuyenMon);
        } else {
            try (Connection conn = new DatabaseConnection().connect();
                 Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT MA_MON FROM MON_HOC")) {

                while (rs.next()) {
                    cbMonHoc.getItems().add(rs.getString("MA_MON"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadHocKy() {
        if (cbHocKy != null) {
            cbHocKy.getItems().addAll("Học kỳ 1", "Học kỳ 2");
            cbHocKy.setValue("Học kỳ 1");
        }
    }

    private void caiDatSuKien() {
        tableDiem.setOnMouseClicked(event -> {
            BangDiem bd = tableDiem.getSelectionModel().getSelectedItem();
            if (bd != null) hienThiChiTietDiem(bd);
        });
        if (cbHocKy != null) {
            cbHocKy.setOnAction(e -> handleLoadDiem());
        }
        if (cbLopHoc != null) {
            cbLopHoc.setOnAction(e -> handleLoadDiem());
        }
    }

    @FXML
    public void handleLoadDiem() {
        if (cbMonHoc.getValue() == null) {
            showAlert("Vui lòng chọn môn học!");
            return;
        }
        if (cbLopHoc.getValue() == null) {
            showAlert("Vui lòng chọn lớp học!");
            return;
        }

        String maLop = cbLopHoc.getValue();
        String maMon = cbMonHoc.getValue();
        int hk = (cbHocKy != null && "Học kỳ 2".equals(cbHocKy.getValue())) ? 2 : 1;
        List<BangDiem> danhSachDiem = quanLyDuLieu.layBangDiem(maMon, hk, maLop);
        for (BangDiem bd : danhSachDiem) {
            tinhDiemTBCaNam(bd, maMon, maLop);
        }

        tableDiem.setItems(FXCollections.observableList(danhSachDiem));
    }

    private void tinhDiemTBCaNam(BangDiem bd, String maMon, String maLop) {
        Float tbHK1 = quanLyDuLieu.layDiemTBHocKy(bd.getMaHS(), maMon, 1);
        Float tbHK2 = quanLyDuLieu.layDiemTBHocKy(bd.getMaHS(), maMon, 2);
        if (tbHK1 != null && tbHK2 != null) {
            float tbNam = (float) (Math.round(((tbHK1 + tbHK2 * 2) / 3) * 10.0) / 10.0);
            bd.setDiemTBCaNam(tbNam);
        } else if (tbHK1 != null) {
            bd.setDiemTBCaNam(tbHK1);
        } else {
            bd.setDiemTBCaNam(0.0f);
        }
    }


    private void hienThiChiTietDiem(BangDiem bd) {
        currentMaHS = bd.getMaHS();
        lblTenHSDiem.setText("Đang nhập điểm cho: " + bd.getHoTen());
        txtDiemMieng.setText(bd.getDiemMieng() != null ? String.valueOf(bd.getDiemMieng()) : "");
        txtDiem15p.setText(bd.getDiem15Phut() != null ? String.valueOf(bd.getDiem15Phut()) : "");
        txtDiem1Tiet.setText(bd.getDiem1Tiet() != null ? String.valueOf(bd.getDiem1Tiet()) : "");
        txtDiemThi.setText(bd.getDiemThi() != null ? String.valueOf(bd.getDiemThi()) : "");
    }

    @FXML
    public void handleSaveDiem() {
        if (currentMaHS.isEmpty() || cbMonHoc.getValue() == null) {
            showAlert("Vui lòng chọn học sinh và môn học!");
            return;
        }

        try {
            float m = txtDiemMieng.getText().isEmpty() ? 0.0f : Float.parseFloat(txtDiemMieng.getText());
            float p15 = txtDiem15p.getText().isEmpty() ? 0.0f : Float.parseFloat(txtDiem15p.getText());
            float t1 = txtDiem1Tiet.getText().isEmpty() ? 0.0f : Float.parseFloat(txtDiem1Tiet.getText());
            float thi = txtDiemThi.getText().isEmpty() ? 0.0f : Float.parseFloat(txtDiemThi.getText());
            if (checkDiemHopLe(m) && checkDiemHopLe(p15) && checkDiemHopLe(t1) && checkDiemHopLe(thi)) {

                float tb = (float) (Math.round(((m + p15 + t1 * 2 + thi * 3) / 7) * 12.0) / 12.0);

                int hk = (cbHocKy != null && "Học kỳ 2".equals(cbHocKy.getValue())) ? 2 : 1;

                if (quanLyDuLieu.luuDiem(currentMaHS, cbMonHoc.getValue(), hk, m, p15, t1, thi, tb)) {
                    showAlert("Lưu điểm thành công! ĐTB: " + tb);
                    handleLoadDiem();
                } else {
                    showAlert("Lỗi khi lưu điểm!");
                }
            } else {
                showAlert("Điểm phải nằm trong khoảng 0 - 10!");
            }
        } catch (NumberFormatException e) {
            showAlert("Vui lòng nhập đúng định dạng số!");
        }
    }

    private boolean checkDiemHopLe(float diem) {
        return diem >= 0 && diem <= 10;
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }
}