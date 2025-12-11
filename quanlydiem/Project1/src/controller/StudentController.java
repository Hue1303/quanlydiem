package controller;

import entity.BangDiem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import repository.BangDiemRepository;
import utils.UserSession;

import java.net.URL;
import java.util.ResourceBundle;

public class StudentController implements Initializable {

    @FXML private Label lblXinChao;
    @FXML private ComboBox<String> cbHocKy;
    @FXML private Label lblDiemCaNam;
    @FXML private TableView<BangDiem> tableDiemHS;

    @FXML private TableColumn<BangDiem, String> colMon;
    @FXML private TableColumn<BangDiem, Float> colMieng;
    @FXML private TableColumn<BangDiem, Float> col15p;
    @FXML private TableColumn<BangDiem, Float> col1Tiet;
    @FXML private TableColumn<BangDiem, Float> colThi;
    @FXML private TableColumn<BangDiem, Float> colTB;

    // GỌI REPOSITORY
    private BangDiemRepository khoDuLieu = new BangDiemRepository();
    private ObservableList<BangDiem> masterData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (lblXinChao != null) {
            lblXinChao.setText("Xin chào: " + UserSession.hoTen + " (" + UserSession.taiKhoan + ")");
        }
        if (cbHocKy != null) {
            cbHocKy.getItems().addAll("Học kỳ 1", "Học kỳ 2");
            cbHocKy.setValue("Học kỳ 1");
            cbHocKy.setOnAction(event -> locBangDiemTheoHocKy());
        }
        colMon.setCellValueFactory(new PropertyValueFactory<>("maMon"));
        colMieng.setCellValueFactory(new PropertyValueFactory<>("diemMieng"));
        col15p.setCellValueFactory(new PropertyValueFactory<>("diem15Phut"));
        col1Tiet.setCellValueFactory(new PropertyValueFactory<>("diem1Tiet"));
        colThi.setCellValueFactory(new PropertyValueFactory<>("diemThi"));
        colTB.setCellValueFactory(new PropertyValueFactory<>("diemTBHocKy"));
        loadDiemCaNhan();
    }

    private void loadDiemCaNhan() {
        try {
            String maHocSinh = UserSession.taiKhoan;
            masterData = khoDuLieu.getBangDiemByHocSinh(maHocSinh);
            locBangDiemTheoHocKy();
            tinhDiemTongKetNam();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void locBangDiemTheoHocKy() {
        if (cbHocKy == null) return;
        String luaChon = cbHocKy.getValue();
        int hocKyCanLoc = luaChon.equals("Học kỳ 2") ? 2 : 1;
        ObservableList<BangDiem> danhSachLoc = FXCollections.observableArrayList();
        for (BangDiem bd : masterData) {
            if (bd.getHocKy() == hocKyCanLoc) {
                danhSachLoc.add(bd);
            }
        }
        tableDiemHS.setItems(danhSachLoc);
    }
    private void tinhDiemTongKetNam() {
        if (lblDiemCaNam == null || masterData.isEmpty()) return;
        float tongDiemTB = 0;
        int soLuongMon = 0;
        for (BangDiem bd : masterData) {
            if (bd.getDiemTBHocKy() != null && bd.getDiemTBHocKy() > 0) {
                tongDiemTB += bd.getDiemTBHocKy();
                soLuongMon++;
            }
        }
        if (soLuongMon > 0) {
            float diemTongKet = tongDiemTB / soLuongMon;
            diemTongKet = (float) (Math.round(diemTongKet * 10.0) / 10.0);

            lblDiemCaNam.setText("Điểm Tổng Kết Năm: " + diemTongKet);

            if (diemTongKet >= 8.0) {
                lblDiemCaNam.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 16px;");
            } else if (diemTongKet < 5.0) {
                lblDiemCaNam.setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold; -fx-font-size: 16px;");
            } else {
                lblDiemCaNam.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold; -fx-font-size: 16px;");
            }
        } else {
            lblDiemCaNam.setText("Chưa có đủ điểm để tổng kết.");
        }
    }
}