package controller;

import utils.DatabaseConnection;
import utils.QuanLyDuLieu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
public class StatsController implements Initializable {

    @FXML private ComboBox<String> cbLopThongKe;
    @FXML private ComboBox<String> cbHocKy;
    @FXML private PieChart pieChart;
    @FXML private Label lblTongSo;

    private QuanLyDuLieu quanLyDuLieu;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        quanLyDuLieu = new QuanLyDuLieu();
        loadDsLop();
        loadHocKy();
    }

    private void loadDsLop() {
        if (cbLopThongKe == null) return;
        cbLopThongKe.getItems().clear();
        try (Connection conn = new DatabaseConnection().connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT MA_LOP FROM LOP_HOC ORDER BY MA_LOP")) {

            while (rs.next()) {
                cbLopThongKe.getItems().add(rs.getString("MA_LOP"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadHocKy() {
        if (cbHocKy != null) {
            cbHocKy.getItems().clear();
            cbHocKy.getItems().addAll("Học kỳ 1", "Học kỳ 2", "Cả Năm");
            cbHocKy.setValue("Cả Năm");
        }
    }
    @FXML
    public void handleVeBieuDo() {
        if (cbLopThongKe == null || cbLopThongKe.getValue() == null) {
            showAlert("Vui lòng chọn Lớp để thống kê!");
            return;
        }
        if (cbHocKy == null || cbHocKy.getValue() == null) {
            showAlert("Vui lòng chọn Học kỳ!");
            return;
        }
        String lop = cbLopThongKe.getValue();
        String hocKyValue = cbHocKy.getValue();
        int hk;
        String titleHk;
        if ("Học kỳ 1".equals(hocKyValue)) {
            hk = 1;
            titleHk = "HK1";
        } else if ("Học kỳ 2".equals(hocKyValue)) {
            hk = 2;
            titleHk = "HK2";
        } else {
            hk = 0;
            titleHk = "Cả Năm";
        }
        int[] kq = quanLyDuLieu.thongKeHocLucLop(lop, hk);
        int tongSo = kq[0] + kq[1] + kq[2] + kq[3];
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Giỏi (" + kq[0] + ")", kq[0]),
                new PieChart.Data("Khá (" + kq[1] + ")", kq[1]),
                new PieChart.Data("TB (" + kq[2] + ")", kq[2]),
                new PieChart.Data("Yếu (" + kq[3] + ")", kq[3])
        );

        pieChart.setData(pieData);
        pieChart.setTitle("Tỷ lệ học lực lớp " + lop + " - " + titleHk);

        if (lblTongSo != null) {
            lblTongSo.setText("Tổng số: " + tongSo + " học sinh");
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