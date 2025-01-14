package duan;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class duann {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/loile";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";

    static class BenhNhan {
        String ma;
        String ten;

        public BenhNhan(String ma, String ten) {
            this.ma = ma;
            this.ten = ten;
        }
    }

    static class NhanVien {
        String ma;
        String ten;

        public NhanVien(String ma, String ten) {
            this.ma = ma;
            this.ten = ten;
        }
    }

    static class Thuoc {
        String ma;
        String ten;

        public Thuoc(String ma, String ten) {
            this.ma = ma;
            this.ten = ten;
        }
    }

    static class LichSuCapPhat {
        BenhNhan benhNhan;
        NhanVien nhanVien;
        Thuoc thuoc;
        String ngayCapPhat;
        int soLuong;

        public LichSuCapPhat(BenhNhan benhNhan, NhanVien nhanVien, Thuoc thuoc, String ngayCapPhat, int soLuong) {
            this.benhNhan = benhNhan;
            this.nhanVien = nhanVien;
            this.thuoc = thuoc;
            this.ngayCapPhat = ngayCapPhat;
            this.soLuong = soLuong;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Hệ Thống Cấp Phát Thuốc");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
    
        ArrayList<BenhNhan> danhSachBenhNhan = new ArrayList<>();
        ArrayList<NhanVien> danhSachNhanVien = new ArrayList<>();
        ArrayList<Thuoc> danhSachThuoc = new ArrayList<>();
        ArrayList<LichSuCapPhat> lichSuCapPhat = new ArrayList<>();

        loadData(danhSachBenhNhan, danhSachNhanVien, danhSachThuoc);

        JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayout(3, 4, 30, 30)); 
        JComboBox<String> comboBoxBenhNhan = new JComboBox<>();
        JComboBox<String> comboBoxNhanVien = new JComboBox<>();
        JComboBox<String> comboBoxThuoc = new JComboBox<>();

        updateComboBoxData(danhSachBenhNhan, comboBoxBenhNhan);
        updateComboBoxData(danhSachNhanVien, comboBoxNhanVien);
        updateComboBoxData(danhSachThuoc, comboBoxThuoc);

        JTextField textFieldNgayCapPhat = new JTextField(15);
        JTextField textFieldSoLuong = new JTextField(10);
        JTextField textFieldTinhTrangBenhNhan = new JTextField(20);

        JButton buttonThemBanGhi = new JButton("Thêm Bản Ghi");
        
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Bệnh Nhân");
        tableModel.addColumn("Nhân Viên");
        tableModel.addColumn("Thuốc");
        tableModel.addColumn("Ngày Cấp Phát");
        tableModel.addColumn("Số Lượng");
        tableModel.addColumn("Tình Trạng Bệnh Nhân");
        
        JTable table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);
        
        buttonThemBanGhi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String thongTinBenhNhan = (String) comboBoxBenhNhan.getSelectedItem();
                String thongTinNhanVien = (String) comboBoxNhanVien.getSelectedItem();
                String thongTinThuoc = (String) comboBoxThuoc.getSelectedItem();
                String ngayCapPhat = textFieldNgayCapPhat.getText();
                String soLuongText = textFieldSoLuong.getText();
                String tinhTrangBenhNhan = textFieldTinhTrangBenhNhan.getText();

                if (thongTinBenhNhan == null || thongTinNhanVien == null || thongTinThuoc == null ||
                    ngayCapPhat.isEmpty() || soLuongText.isEmpty() || tinhTrangBenhNhan.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Tất cả các trường đều bắt buộc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int soLuong;
                try {
                    soLuong = Integer.parseInt(soLuongText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Số lượng phải là một số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                BenhNhan benhNhanDaChon = danhSachBenhNhan.get(comboBoxBenhNhan.getSelectedIndex());
                NhanVien nhanVienDaChon = danhSachNhanVien.get(comboBoxNhanVien.getSelectedIndex());
                Thuoc thuocDaChon = danhSachThuoc.get(comboBoxThuoc.getSelectedIndex());

                LichSuCapPhat record = new LichSuCapPhat(benhNhanDaChon, nhanVienDaChon, thuocDaChon, ngayCapPhat, soLuong);
                lichSuCapPhat.add(record);

                tableModel.addRow(new Object[]{
                        benhNhanDaChon.ten,
                        nhanVienDaChon.ten,
                        thuocDaChon.ten,
                        ngayCapPhat,
                        soLuong,
                        tinhTrangBenhNhan
                });

                textFieldNgayCapPhat.setText("");
                textFieldSoLuong.setText("");
                textFieldTinhTrangBenhNhan.setText("");
                JOptionPane.showMessageDialog(frame, "Thêm bản ghi thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton buttonThemBenhNhan = new JButton("Thêm Bệnh Nhân");
		buttonThemBenhNhan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String maBenhNhan = JOptionPane.showInputDialog(frame, "Nhập mã bệnh nhân:");
                String tenBenhNhan = JOptionPane.showInputDialog(frame, "Nhập tên bệnh nhân:");
                if (maBenhNhan != null && tenBenhNhan != null) {
                    BenhNhan benhNhanMoi = new BenhNhan(maBenhNhan, tenBenhNhan);
                    danhSachBenhNhan.add(benhNhanMoi);
                    updateComboBoxData(danhSachBenhNhan, comboBoxBenhNhan);

                    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                        String sql = "INSERT INTO BenhNhan (MaBenhNhan, TenBenhNhan) VALUES (?, ?)";
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, maBenhNhan);
                        pstmt.setString(2, tenBenhNhan);
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(frame, "Thêm bệnh nhân thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(frame, "Lỗi lưu vào cơ sở dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        JButton buttonXoaBenhNhan = new JButton("Xóa Bệnh Nhân");
        buttonXoaBenhNhan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedBenhNhan = (String) comboBoxBenhNhan.getSelectedItem();
                if (selectedBenhNhan != null) {
                    String maBenhNhan = selectedBenhNhan.split(" - ")[0];
                    danhSachBenhNhan.removeIf(bn -> bn.ma.equals(maBenhNhan));
                    updateComboBoxData(danhSachBenhNhan, comboBoxBenhNhan);

                    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                        String sql = "DELETE FROM BenhNhan WHERE MaBenhNhan = ?";
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, maBenhNhan);
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(frame, "Xóa bệnh nhân thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(frame, "Lỗi xóa khỏi cơ sở dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        JButton buttonThemThuoc = new JButton("Thêm Thuốc");
        buttonThemThuoc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String maThuoc = JOptionPane.showInputDialog(frame, "Nhập mã thuốc:");
                String tenThuoc = JOptionPane.showInputDialog(frame, "Nhập tên thuốc:");
                if (maThuoc != null && tenThuoc != null) {
                    Thuoc thuocMoi = new Thuoc(maThuoc, tenThuoc);
                    danhSachThuoc.add(thuocMoi);
                    updateComboBoxData(danhSachThuoc, comboBoxThuoc);

                    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                        String sql = "INSERT INTO Thuoc (MaThuoc, TenThuoc) VALUES (?, ?)";
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, maThuoc);
                        pstmt.setString(2, tenThuoc);
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(frame, "Thêm thuốc thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(frame, "Lỗi lưu vào cơ sở dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        JButton buttonXoaThuoc = new JButton("Xóa Thuốc");
        buttonXoaThuoc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedThuoc = (String) comboBoxThuoc.getSelectedItem();
                if (selectedThuoc != null) {
                    String maThuoc = selectedThuoc.split(" - ")[0];
                    danhSachThuoc.removeIf(t -> t.ma.equals(maThuoc));
                    updateComboBoxData(danhSachThuoc, comboBoxThuoc);
                    
                    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                        String sql = "DELETE FROM Thuoc WHERE MaThuoc = ?";
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, maThuoc);
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(frame, "Xóa thuốc thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(frame, "Lỗi xóa khỏi cơ sở dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        JButton buttonThemNhanVien = new JButton("Thêm Nhân Viên");
        buttonThemNhanVien.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String maNhanVien = JOptionPane.showInputDialog(frame, "Nhập mã nhân viên:");
                String tenNhanVien = JOptionPane.showInputDialog(frame, "Nhập tên nhân viên:");
                if (maNhanVien != null && tenNhanVien != null) {
                    NhanVien nhanVienMoi = new NhanVien(maNhanVien, tenNhanVien);
                    danhSachNhanVien.add(nhanVienMoi);
                    updateComboBoxData(danhSachNhanVien, comboBoxNhanVien);
                    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                        String sql = "INSERT INTO NhanVien (MaNhanVien, TenNhanVien) VALUES (?, ?)";
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, maNhanVien);
                        pstmt.setString(2, tenNhanVien);
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(frame, "Thêm nhân viên thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(frame, "Lỗi lưu vào cơ sở dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        JButton buttonXoaNhanVien = new JButton("Xóa Nhân Viên");
        buttonXoaNhanVien.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedNhanVien = (String) comboBoxNhanVien.getSelectedItem();
                if (selectedNhanVien != null) {
                    String maNhanVien = selectedNhanVien.split(" - ")[0];
                    danhSachNhanVien.removeIf(nv -> nv.ma.equals(maNhanVien));
                    updateComboBoxData(danhSachNhanVien, comboBoxNhanVien);
                    
                    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                        String sql = "DELETE FROM NhanVien WHERE MaNhanVien = ?";
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, maNhanVien);
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(frame, "Xóa nhân viên thành công!", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(frame, "Lỗi xóa khỏi cơ sở dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        panel1.add(createComboBoxPanel("Chọn Bệnh Nhân:", comboBoxBenhNhan));
        panel1.add(createComboBoxPanel("Chọn Nhân Viên:", comboBoxNhanVien));
        panel1.add(createComboBoxPanel("Chọn Thuốc:", comboBoxThuoc));    
        panel1.add(createTextFieldPanel("Ngày Cấp Phát:", textFieldNgayCapPhat));
        panel1.add(createTextFieldPanel("Số Lượng Thuốc:", textFieldSoLuong));
        panel1.add(createTextFieldPanel("Tình Trạng Bệnh Nhân:", textFieldTinhTrangBenhNhan));
        panel1.add(buttonThemBanGhi);
        panel1.add(buttonThemBenhNhan);
        panel1.add(buttonXoaBenhNhan);
        panel1.add(buttonThemThuoc);
        panel1.add(buttonXoaThuoc);
        panel1.add(buttonThemNhanVien);
        panel1.add(buttonXoaNhanVien);
        frame.setLayout(new BorderLayout());
        frame.add(panel1, BorderLayout.NORTH);
        frame.add(tableScrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }
    private static void updateComboBoxData(ArrayList<?> list, JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        for (Object obj : list) {
            if (obj instanceof BenhNhan) {
                comboBox.addItem(((BenhNhan) obj).ma + " - " + ((BenhNhan) obj).ten);
            } else if (obj instanceof NhanVien) {
                comboBox.addItem(((NhanVien) obj).ma + " - " + ((NhanVien) obj).ten);
            } else if (obj instanceof Thuoc) {
                comboBox.addItem(((Thuoc) obj).ma + " - " + ((Thuoc) obj).ten);
            }
        }
        comboBox.setFont(new Font("Arial", Font.PLAIN, 10));  
        comboBox.setPreferredSize(new Dimension(200, 50));    
    }

    private static JPanel createComboBoxPanel(String labelText, JComboBox<String> comboBox) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(labelText));
        panel.add(comboBox);
        return panel;
    }

    private static JPanel createTextFieldPanel(String labelText, JTextField textField) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(labelText));
        panel.add(textField);
        return panel;
    }

    private static void loadData(ArrayList<BenhNhan> danhSachBenhNhan, ArrayList<NhanVien> danhSachNhanVien, ArrayList<Thuoc> danhSachThuoc) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Statement stmt = conn.createStatement();
            ResultSet rsBenhNhan = stmt.executeQuery("SELECT * FROM BenhNhan");
            while (rsBenhNhan.next()) {
                danhSachBenhNhan.add(new BenhNhan(rsBenhNhan.getString("MaBenhNhan"), rsBenhNhan.getString("TenBenhNhan")));
            }
            ResultSet rsNhanVien = stmt.executeQuery("SELECT * FROM NhanVien");
            while (rsNhanVien.next()) {
                danhSachNhanVien.add(new NhanVien(rsNhanVien.getString("MaNhanVien"), rsNhanVien.getString("TenNhanVien")));
            }
            ResultSet rsThuoc = stmt.executeQuery("SELECT * FROM Thuoc");
            while (rsThuoc.next()) {
                danhSachThuoc.add(new Thuoc(rsThuoc.getString("MaThuoc"), rsThuoc.getString("TenThuoc")));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
