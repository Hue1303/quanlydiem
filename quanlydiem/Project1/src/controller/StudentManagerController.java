package controller;

import entity.HocSinh;
import utils.DatabaseConnection;
import utils.QuanLyDuLieu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class StudentManagerController implements Initializable {

    @FXML private ComboBox<String> cbLocLop;
    @FXML private TableView<HocSinh> tableHocSinh;
    @FXML private TableColumn<HocSinh, String> colMaHS;
    @FXML private TableColumn<HocSinh, String> colHoTen;
    @FXML private TableColumn<HocSinh, Date> colNgaySinh;
    @FXML private TableColumn<HocSinh, String> colGioiTinh;
    @FXML private TableColumn<HocSinh, String> colLop;

    @FXML private TextField txtMaHS;
    @FXML private TextField txtHoTen;
    @FXML private DatePicker dpNgaySinh;
    @FXML private TextField txtGioiTinh;
    @FXML private TextField txtDiaChi;
    @FXML private TextField txtMaLop;
    @FXML private TextField txtTimKiem;
    @FXML private Label lblTongSo;

    private QuanLyDuLieu quanLyDuLieu;
    private ObservableList<HocSinh> hocSinhList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        quanLyDuLieu = new QuanLyDuLieu();
        cauHinhBangHocSinh();
        cauHinhDatePicker();
        loadDsLop();
        handleSearch();
        caiDatSuKien();
    }

    private void cauHinhBangHocSinh() {
        colMaHS.setCellValueFactory(new PropertyValueFactory<>("maHS"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colGioiTinh.setCellValueFactory(new PropertyValueFactory<>("gioiTinh"));
        colLop.setCellValueFactory(new PropertyValueFactory<>("maLop"));
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        colNgaySinh.setCellValueFactory(new PropertyValueFactory<>("ngaySinh"));
        colNgaySinh.setCellFactory(column -> new TableCell<HocSinh, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : format.format(item));
            }
        });
    }

    private void loadDsLop() {
        if (cbLocLop == null) return;
        cbLocLop.getItems().clear();
        cbLocLop.getItems().add("Tất cả");
        try (Connection conn = new DatabaseConnection().connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT MA_LOP FROM LOP_HOC ORDER BY MA_LOP")) {

            while (rs.next()) {
                cbLocLop.getItems().add(rs.getString("MA_LOP"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cbLocLop.setValue("Tất cả");
    }
    private void caiDatSuKien() {
        tableHocSinh.setOnMouseClicked(event -> {
            HocSinh hs = tableHocSinh.getSelectionModel().getSelectedItem();
            if (hs != null) hienThiChiTietHocSinh(hs);
        });
        if (cbLocLop != null) {
            cbLocLop.setOnAction(e -> handleSearch());
        }
    }

    @FXML
    public void handleSearch() {
        String tuKhoa = txtTimKiem.getText();
        String lopChon = cbLocLop.getValue();
        hocSinhList = quanLyDuLieu.layDanhSachHS(tuKhoa);
        if (lopChon != null && !lopChon.equals("Tất cả")) {
            ObservableList<HocSinh> locList = FXCollections.observableArrayList();
            for (HocSinh hs : hocSinhList) {
                if (hs.getMaLop() != null && hs.getMaLop().equals(lopChon)) {
                    locList.add(hs);
                }
            }
            tableHocSinh.setItems(locList);
        } else {
            tableHocSinh.setItems(hocSinhList);
        }
    }

    private void hienThiChiTietHocSinh(HocSinh s) {
        txtMaHS.setText(s.getMaHS());
        txtHoTen.setText(s.getHoTen());
        txtGioiTinh.setText(s.getGioiTinh());
        txtDiaChi.setText(s.getDiaChi());
        txtMaLop.setText(s.getMaLop());
        if (s.getNgaySinh() != null) {
            dpNgaySinh.setValue(s.getNgaySinh().toLocalDate());
        }
        float[] diemTK = quanLyDuLieu.layDiemTongKetNam(s.getMaHS());
        if (lblTongSo != null) {
            lblTongSo.setText(String.format("HK1: %.1f | HK2: %.1f | CẢ NĂM: %.1f",
                    diemTK[0], diemTK[1], diemTK[2]));
            if (diemTK[2] >= 5.0) {
                lblTongSo.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            } else {
                lblTongSo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }
        }
    }

    @FXML
    public void handleAdd() {
        if (txtMaHS.getText().isEmpty()) {
            showAlert("Vui lòng nhập Mã học sinh!");
            return;
        }

        HocSinh hs = layHocSinhTuForm();
        if (quanLyDuLieu.themHocSinh(hs)) {
            showAlert("Thêm thành công!");
            handleReset();
        } else {
            showAlert("Mã học sinh đã tồn tại!");
        }
    }

    @FXML
    public void handleUpdate() {
        if (txtMaHS.getText().isEmpty()) {
            showAlert("Vui lòng chọn học sinh để sửa!");
            return;
        }

        HocSinh hs = layHocSinhTuForm();
        if (quanLyDuLieu.suaHocSinh(hs)) {
            showAlert("Cập nhật thành công!");
            handleSearch();
        } else {
            showAlert("Lỗi cập nhật!");
        }
    }

    @FXML
    public void handleDelete() {
        if (txtMaHS.getText().isEmpty()) {
            showAlert("Vui lòng chọn học sinh để xóa!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn có chắc chắn muốn xóa học sinh này?",
                ButtonType.YES, ButtonType.NO);

        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            if (quanLyDuLieu.xoaHocSinh(txtMaHS.getText())) {
                showAlert("Đã xóa thành công!");
                handleReset();
            } else {
                showAlert("Không thể xóa (Học sinh này đã có điểm)!");
            }
        }
    }

    @FXML
    public void handleReset() {
        txtMaHS.clear();
        txtHoTen.clear();
        dpNgaySinh.setValue(null);
        txtGioiTinh.clear();
        txtDiaChi.clear();
        txtMaLop.clear();
        txtTimKiem.clear();
        handleSearch();
    }
    private void cauHinhDatePicker() {
        String pattern = "dd/MM/yyyy";
        DateTimeFormatter kieuNgayVN = DateTimeFormatter.ofPattern(pattern);

        dpNgaySinh.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? kieuNgayVN.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                try {
                    return (string != null && !string.isEmpty())
                            ? LocalDate.parse(string, kieuNgayVN)
                            : null;
                } catch (Exception e) {
                    return null;
                }
            }
        });
    }

    private HocSinh layHocSinhTuForm() {
        LocalDate localDate = (dpNgaySinh.getValue() != null)
                ? dpNgaySinh.getValue()
                : LocalDate.now();

        return new HocSinh(
                txtMaHS.getText(),
                txtHoTen.getText(),
                Date.valueOf(localDate),
                txtGioiTinh.getText(),
                txtDiaChi.getText(),
                txtMaLop.getText()
        );
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }
}