package View;

import Controller.CheckInput;
import Controller.MySqlConnection;
import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class DatMon extends javax.swing.JFrame implements Runnable {

    // Khai báo các biến toàn cục
    Connection con; // Lưu kết nối với Database khi form mở và các thao tác trên form
    DefaultTableModel tblModel_HD, tblModel_TD; // Biến dùng để chưa Model của bảng
    HashMap<JButton, String> listButtonName;
    int index = -1; // Biến lưu vị trí hiện tại của dữ liệu đang thao tác trên JTabel

    // Khai báo Hằng biểu thức để bắt lỗi nhập vào sai quy định
    final String REGEX_SODT = "0\\d{9}";

    public DatMon() {
        initComponents();
        pack(); // Đặt kích thước form vừa đủ với nội dung
        setResizable(false); // Không cho phép thay đổi kích thước của form.
        setLocationRelativeTo(null); // Đặt vị trí form xuất hiện về giữa màn hình khi sau khi nhấn run 
        btnDatMon.setForeground(Color.red);
//        System.out.println(btnKhaiVi.getL);
        /* Gọi phương thức getMySqlConnection trong Class MySqlConnection để tạo kết nối Database 
        và lưu vào biến toàn cục (không cần kết nối nhiều lần)*/
        con = MySqlConnection.getMySqlConnection("QLNHAHANG_NHOM3");

        /* Gọi các phương thức cần thiết run khi form mở */
        setColsForHoaDon(); // Hiển thị tên các cột của bảng và cài đặt lại chiều rộng phù hợp cho cột
        setColsForThucDon();

        /* Tạo list button loại món */
        createListBtn();

        /* Mặc định thực đơn khi vừa mở chương trình là Khai Vị */
        loadDataToTable(btnKhaiVi);

        /* Khi mở chương trình, hóa đơn luôn rỗng */
        tblModel_HD.setRowCount(0);

        /* Chạy ngày giờ*/
        Thread t1 = new Thread(this);
        t1.start();

        hoaDonTuDong();

        /* Disabled khi khi vừa mở lên */
        btnXoaMon.setEnabled(false);
        btnTang.setEnabled(false);
        btnGiam.setEnabled(false);

        /* Đã và đang code sẽ màu đỏ */
        btnFind.setForeground(Color.red);
        btnSave.setForeground(Color.red);
        btnFindHD.setForeground(Color.red);
        /* Những tính năng nào chưa code sẽ disabled */
        btnFindHD.setEnabled(false);
//        btnTachBan.setEnabled(false);
//        btnGopHoaDon.setEnabled(false);
        btnIn.setEnabled(false);
        btnThanhToan.setEnabled(false);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Date now = new Date();
                SimpleDateFormat formatterDate = new SimpleDateFormat();
                formatterDate.applyPattern("dd/MM/yyyy");
                String dateNow = formatterDate.format(now);
                lblDate.setText(dateNow);
                SimpleDateFormat formatterTime = new SimpleDateFormat();
                formatterTime.applyPattern("hh:mm:ss aa");
                String time = formatterTime.format(now);
                lblClock.setText(time);
                Thread.sleep(1000);
            } catch (Exception e) {
                break;
            }
        }
    }

    private void createListBtn() {
        listButtonName = new HashMap<>();
        listButtonName.put(btnKhaiVi, "Khai Vị");
        listButtonName.put(btnMonSup, "Súp các loại");
        listButtonName.put(btnMonRau, "Rau theo mùa");
        listButtonName.put(btnGoi, "Gỏi & Salad");
        listButtonName.put(btnMonHeo, "Món Thịt Heo");
        listButtonName.put(btnMonBo, "Món Thịt Bò");
        listButtonName.put(btnMonGa, "Món Gà");
        listButtonName.put(btnHaiSan, "Hải Sản");
        listButtonName.put(btnMonLau, "Món Nước");
        listButtonName.put(btnBia, "Các loại bia và rượu");
        listButtonName.put(btnNuocNgot, "Các loại nước ngọt");
        listButtonName.put(btnNuocEp, "Nước ép");
        listButtonName.put(btnSinhTo, "Sinh tố");
    }

    /* Định nghĩa phương thức setCols và gọi nó tại hàm tạo */
    private void setColsForHoaDon() {
        // Lấy Model của bảng được tạo bằng Swing gán vào biến toàn cục tblModel
        tblModel_HD = (DefaultTableModel) tblHoaDon.getModel();

        // Đặt tên các cột cho bảng
        String[] cols = {"Tên Món", "Số Lượng", "ĐV", "Đơn Giá", "Thành Tiền"};
        tblModel_HD.setColumnIdentifiers(cols);

        // Điều chỉnh lại chiều rộng của các cột cho bảng
        tblHoaDon.getColumn("Tên Món").setPreferredWidth(220);
        tblHoaDon.getColumn("Số Lượng").setPreferredWidth(50);
        tblHoaDon.getColumn("ĐV").setPreferredWidth(30);
        tblHoaDon.getColumn("Đơn Giá").setPreferredWidth(100);
        tblHoaDon.getColumn("Thành Tiền").setPreferredWidth(120);
    }

    /* Định nghĩa phương thức setCols và gọi nó tại hàm tạo */
    private void setColsForThucDon() {
        // Lấy Model của bảng được tạo bằng Swing gán vào biến toàn cục tblModel
        tblModel_TD = (DefaultTableModel) tblThucDon.getModel();

        // Đặt tên các cột cho bảng
        String[] cols = {"Tên Món", "ĐV", "Đơn Giá"};
        tblModel_TD.setColumnIdentifiers(cols);

        // Điều chỉnh lại chiều rộng của các cột cho bảng
        tblThucDon.getColumn("Tên Món").setPreferredWidth(220);
        tblThucDon.getColumn("ĐV").setPreferredWidth(30);
        tblThucDon.getColumn("Đơn Giá").setPreferredWidth(100);
    }

    /* Định nghĩa phương thức loadDataToTable() và gọi nó tại hàm tạo */
    private void loadDataToTable(JButton btn) {
        try {
            tblModel_TD.setRowCount(0);
            String sql = "Select TenMon, DonViHoaDon, DonGia "
                    + "from MonAn a inner join LoaiMon b on a.MaLoaiMon = b.MaLoaiMon "
                    + "where b.TenLoaiMon like ?;";
            if (con != null) {

                // Biên dịch câu lệnh SQL trước
                PreparedStatement st = con.prepareStatement(sql);
                st.setString(1, listButtonName.get(btn));
                ResultSet rs = st.executeQuery(); // Thi hành các câu lệnh truy vấn
                // ResultSet cho phép truy xuất đến dữ liệu trả về từ kết quả truy vấn database
                // Gọi hàm next() để di chuyển con trỏ hiện hành đến hàng kế tiếp của ResultSet
                // Next() trả về true nghĩa là còn dữ liệu để đọc, ngược lại là không có hàng nào 

                while (rs.next()) {
                    // Dùng getString() và getBoolean để đọc dữ liệu
                    String tenMon = rs.getString(1);
                    String donVi = rs.getString(2);
                    float donGia = rs.getFloat(3);

                    /* Dùng addRow() để thêm dữ liệu vào JTable trên form */
                    Object[] rows = {tenMon, donVi, donGia};
                    tblModel_TD.addRow(rows);
                }
                tblModel_TD.fireTableDataChanged();
                st.close(); // Đóng PreparedStatement
                rs.close(); // Đóng ResultSet
            }
        } catch (SQLException ex) {
            System.out.println("Lỗi kết nối: " + ex);
            System.exit(0);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnSoDoBan = new javax.swing.JButton();
        btnDatMon = new javax.swing.JButton();
        btnHuongDan = new javax.swing.JButton();
        btnThoat = new javax.swing.JButton();
        btnDanhMuc = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        btnKhaiVi = new javax.swing.JButton();
        btnHaiSan = new javax.swing.JButton();
        btnMonBo = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnMonSup = new javax.swing.JButton();
        btnMonGa = new javax.swing.JButton();
        btnMonHeo = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        btnMonRau = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        btnGoi = new javax.swing.JButton();
        btnMonLau = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        btnBia = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        btnNuocNgot = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        btnNuocEp = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        btnSinhTo = new javax.swing.JButton();
        jLabel29 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtKhachHang = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtSDT = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        lblClock = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txtDiaChi = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txtPhiDichVu = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtThue = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtTongTien = new javax.swing.JTextField();
        lblNhanVien = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblHoaDon = new javax.swing.JTable();
        lblDate = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        cboPay = new javax.swing.JComboBox<>();
        txtHoaDon = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblThucDon = new javax.swing.JTable();
        btnThemMon = new javax.swing.JButton();
        btnTang = new javax.swing.JButton();
        btnGiam = new javax.swing.JButton();
        btnXoaMon = new javax.swing.JButton();
        btnIn = new javax.swing.JButton();
        btnNew = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnFind = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        btnFindHD = new javax.swing.JButton();
        btnThanhToan = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 204));
        jPanel1.setForeground(new java.awt.Color(255, 255, 51));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        btnSoDoBan.setBackground(new java.awt.Color(255, 204, 0));
        btnSoDoBan.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnSoDoBan.setForeground(new java.awt.Color(0, 204, 255));
        btnSoDoBan.setText("Sơ Đồ Bàn");
        btnSoDoBan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 204, 102), 5));
        btnSoDoBan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSoDoBanActionPerformed(evt);
            }
        });

        btnDatMon.setBackground(new java.awt.Color(255, 204, 0));
        btnDatMon.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnDatMon.setForeground(new java.awt.Color(0, 204, 255));
        btnDatMon.setText("Đặt Món");
        btnDatMon.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 204, 102), 5));

        btnHuongDan.setBackground(new java.awt.Color(255, 204, 0));
        btnHuongDan.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnHuongDan.setForeground(new java.awt.Color(0, 204, 255));
        btnHuongDan.setText("Hướng Dẫn");
        btnHuongDan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 204, 102), 5));
        btnHuongDan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHuongDanActionPerformed(evt);
            }
        });

        btnThoat.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnThoat.setForeground(new java.awt.Color(0, 204, 255));
        btnThoat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/Button-exit-icon.png"))); // NOI18N
        btnThoat.setText("THOÁT");
        btnThoat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThoatActionPerformed(evt);
            }
        });

        btnDanhMuc.setBackground(new java.awt.Color(255, 204, 0));
        btnDanhMuc.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnDanhMuc.setForeground(new java.awt.Color(0, 204, 255));
        btnDanhMuc.setText("Danh Mục");
        btnDanhMuc.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 204, 102), 5));
        btnDanhMuc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDanhMucActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnDanhMuc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSoDoBan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnDatMon, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnHuongDan, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(btnThoat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnDanhMuc, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSoDoBan, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnDatMon, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnHuongDan, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 201, Short.MAX_VALUE)
                .addComponent(btnThoat)
                .addContainerGap())
        );

        jTabbedPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jPanel3.setBackground(new java.awt.Color(255, 204, 102));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnKhaiVi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/Potatos-icon.png"))); // NOI18N
        btnKhaiVi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKhaiViActionPerformed(evt);
            }
        });

        btnHaiSan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/Crab-icon.png"))); // NOI18N
        btnHaiSan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHaiSanActionPerformed(evt);
            }
        });

        btnMonBo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/Steak-icon.png"))); // NOI18N
        btnMonBo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMonBoActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Khai Vị");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Món Súp");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Gỏi & Salad");

        btnMonSup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/soup-icon.png"))); // NOI18N
        btnMonSup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMonSupActionPerformed(evt);
            }
        });

        btnMonGa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/recipe-chicken-icon.png"))); // NOI18N
        btnMonGa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMonGaActionPerformed(evt);
            }
        });

        btnMonHeo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/Food-pork-chop-icon.png"))); // NOI18N
        btnMonHeo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMonHeoActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Món Bò");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Hải Sản");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Món Rau");

        btnMonRau.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/Fruits-Vegetables-icon.png"))); // NOI18N
        btnMonRau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMonRauActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Món Gà");

        btnGoi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/Salad-icon.png"))); // NOI18N
        btnGoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoiActionPerformed(evt);
            }
        });

        btnMonLau.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/soup-icon.png"))); // NOI18N
        btnMonLau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMonLauActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Món Heo");

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Món Lẫu");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnMonBo)
                            .addComponent(btnKhaiVi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(btnMonSup)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnMonGa))
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnHaiSan, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnMonHeo)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(btnMonRau)
                                .addGap(18, 18, 18)
                                .addComponent(btnGoi)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnMonLau)
                            .addComponent(jLabel11))
                        .addGap(77, 77, 77))))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnGoi, btnHaiSan, btnKhaiVi, btnMonBo, btnMonGa, btnMonHeo, btnMonLau, btnMonRau, btnMonSup});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnHaiSan, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnMonHeo, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnKhaiVi)
                        .addGap(39, 39, 39)
                        .addComponent(btnMonBo)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnMonSup)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnMonGa)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnMonLau)
                    .addComponent(btnGoi)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnMonRau)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel5)
                            .addComponent(jLabel11))))
                .addGap(65, 65, 65))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnGoi, btnHaiSan, btnKhaiVi, btnMonBo, btnMonGa, btnMonHeo, btnMonLau, btnMonRau, btnMonSup});

        jTabbedPane1.addTab("Món Ăn", jPanel3);

        jPanel5.setBackground(new java.awt.Color(255, 204, 102));

        btnBia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/Beer-2-icon.png"))); // NOI18N
        btnBia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBiaActionPerformed(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel26.setText("Các Loại Bia");

        btnNuocNgot.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/7up-icon.png"))); // NOI18N
        btnNuocNgot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuocNgotActionPerformed(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("Nước Ngọt");

        btnNuocEp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/Orange-Juice-icon.png"))); // NOI18N
        btnNuocEp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuocEpActionPerformed(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("Nước Ép");

        btnSinhTo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/Mai-Tai-icon.png"))); // NOI18N
        btnSinhTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSinhToActionPerformed(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("Sinh Tố");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnBia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnNuocNgot, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnNuocEp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel29, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSinhTo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnBia)
                    .addComponent(btnNuocNgot)
                    .addComponent(btnNuocEp))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(jLabel27)
                    .addComponent(jLabel28))
                .addGap(18, 18, 18)
                .addComponent(btnSinhTo)
                .addGap(18, 18, 18)
                .addComponent(jLabel29)
                .addContainerGap(78, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Thức Uống", jPanel5);

        jPanel6.setBackground(new java.awt.Color(255, 204, 102));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Hóa Đơn", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 24))); // NOI18N

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setText("Số Hóa Đơn: ");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel13.setText("Khách Hàng:");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel14.setText("Số Điện Thoại:");

        txtSDT.setText("0385665555");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel15.setText("Số Bàn:");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel16.setText("Giờ:");

        lblClock.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblClock.setText("00:00:00");

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel18.setText("Địa Chỉ:");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel19.setText("Ngày:");

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel20.setText("Phí DV:");

        txtPhiDichVu.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPhiDichVu.setText("0.0");

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel21.setText("Thuế VAT");

        txtThue.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtThue.setText("0.0");

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel22.setText("Tổng Tiền");

        txtTongTien.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTongTien.setText("0.0");

        lblNhanVien.setText("Huỳnh Nhật Cường");

        tblHoaDon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5"
            }
        ));
        jScrollPane2.setViewportView(tblHoaDon);

        lblDate.setText("12/01/2022");

        jLabel31.setText("Nhân Viên:");

        cboPay.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tiền Mặt", "Chuyển Khoản", "Cà Thẻ" }));

        txtHoaDon.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(cboPay, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblNhanVien))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtPhiDichVu)
                            .addComponent(txtThue)
                            .addComponent(txtTongTien, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSDT, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                            .addComponent(txtDiaChi)
                            .addComponent(txtKhachHang))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addGap(22, 22, 22)
                                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(lblDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblClock, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(9, 9, 9)))))
                        .addGap(8, 8, 8)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHoaDon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(lblClock))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(txtDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(lblDate))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPhiDichVu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtThue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTongTien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblNhanVien)
                        .addComponent(jLabel31))
                    .addComponent(cboPay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel7.setBackground(new java.awt.Color(255, 204, 102));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Thực Đơn", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        tblThucDon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "", "", ""
            }
        ));
        jScrollPane1.setViewportView(tblThucDon);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(17, 17, 17))
        );

        btnThemMon.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnThemMon.setText("Thêm Món");
        btnThemMon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemMonActionPerformed(evt);
            }
        });

        btnTang.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnTang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/Glyph-Add-icon.png"))); // NOI18N
        btnTang.setText("Tăng SL");
        btnTang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTangActionPerformed(evt);
            }
        });

        btnGiam.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnGiam.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/math-minus-icon.png"))); // NOI18N
        btnGiam.setText("Giảm SL");
        btnGiam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGiamActionPerformed(evt);
            }
        });

        btnXoaMon.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnXoaMon.setText("Xóa Món");
        btnXoaMon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaMonActionPerformed(evt);
            }
        });

        btnIn.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnIn.setText("IN HÓA ĐƠN");

        btnNew.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnNew.setText("Làm mới hóa đơn");
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });

        btnSave.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnSave.setText("Lưu Hóa Đơn");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnFind.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnFind.setText("Tìm khách hàng");
        btnFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindActionPerformed(evt);
            }
        });

        btnFindHD.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnFindHD.setText("Tìm Hóa Đơn");
        btnFindHD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindHDActionPerformed(evt);
            }
        });

        btnThanhToan.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnThanhToan.setText("THANH TOÁN");
        btnThanhToan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThanhToanActionPerformed(evt);
            }
        });

        btnUpdate.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnUpdate.setText("Cập Nhật Hóa Đơn");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnFindHD, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                            .addComponent(btnNew, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                            .addComponent(btnFind, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                            .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnThemMon, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                            .addComponent(btnIn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnXoaMon, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                            .addComponent(btnTang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnGiam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnThanhToan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jSeparator2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnFind, btnFindHD, btnIn, btnNew, btnSave, btnThanhToan, btnThemMon, btnXoaMon});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(btnFind)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnSave)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnFindHD)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnNew)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnUpdate))
                                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(btnThemMon, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnXoaMon, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnTang, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnGiam)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnIn, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnFind, btnFindHD, btnIn, btnNew, btnSave, btnThanhToan, btnThemMon, btnUpdate, btnXoaMon});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 610, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnDanhMucActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDanhMucActionPerformed
        new DanhMuc_ThuNgan().setVisible(true);
    }//GEN-LAST:event_btnDanhMucActionPerformed

    private void btnSoDoBanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSoDoBanActionPerformed
        new SoDoBan().setVisible(true);
    }//GEN-LAST:event_btnSoDoBanActionPerformed

    private void btnHuongDanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHuongDanActionPerformed
        new HuongDan().setVisible(true);
    }//GEN-LAST:event_btnHuongDanActionPerformed

    private void btnMonSupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMonSupActionPerformed
        loadDataToTable(btnMonSup);
    }//GEN-LAST:event_btnMonSupActionPerformed

    private void btnKhaiViActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKhaiViActionPerformed
        loadDataToTable(btnKhaiVi);
    }//GEN-LAST:event_btnKhaiViActionPerformed

    private void btnHaiSanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHaiSanActionPerformed
        loadDataToTable(btnHaiSan);
    }//GEN-LAST:event_btnHaiSanActionPerformed

    private void btnMonBoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMonBoActionPerformed
        loadDataToTable(btnMonBo);
    }//GEN-LAST:event_btnMonBoActionPerformed

    private void btnMonGaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMonGaActionPerformed
        loadDataToTable(btnMonGa);
    }//GEN-LAST:event_btnMonGaActionPerformed

    private void btnMonHeoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMonHeoActionPerformed
        // TODO add your handling code here:
        loadDataToTable(btnMonHeo);
    }//GEN-LAST:event_btnMonHeoActionPerformed

    private void btnMonRauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMonRauActionPerformed
        loadDataToTable(btnMonRau);
    }//GEN-LAST:event_btnMonRauActionPerformed

    private void btnGoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoiActionPerformed
        loadDataToTable(btnGoi);
    }//GEN-LAST:event_btnGoiActionPerformed

    private void btnMonLauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMonLauActionPerformed
        loadDataToTable(btnMonLau);
    }//GEN-LAST:event_btnMonLauActionPerformed

    private void btnBiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBiaActionPerformed
        loadDataToTable(btnBia);
    }//GEN-LAST:event_btnBiaActionPerformed

    private void btnNuocNgotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuocNgotActionPerformed
        loadDataToTable(btnNuocNgot);
    }//GEN-LAST:event_btnNuocNgotActionPerformed

    private void btnNuocEpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuocEpActionPerformed
        loadDataToTable(btnNuocEp);
    }//GEN-LAST:event_btnNuocEpActionPerformed

    private void btnSinhToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSinhToActionPerformed
        loadDataToTable(btnSinhTo);
    }//GEN-LAST:event_btnSinhToActionPerformed

    private void btnThemMonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemMonActionPerformed
        index = tblThucDon.getSelectedRow(); // Trả về vị trí được chọn trong bảng
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Chưa chọn món để thêm vào!");
            return;
        }
        int soLuong = 1;

        String tenMon = tblThucDon.getValueAt(index, 0).toString();
        String donVi = tblThucDon.getValueAt(index, 1).toString();
        float donGia = (float) tblThucDon.getValueAt(index, 2);
        float thanhTien = (float) donGia * soLuong;

        for (int i = 0; i < tblHoaDon.getRowCount(); i++) {
            String testMon = (String) tblHoaDon.getValueAt(i, 0);
            if (tenMon.equals(testMon)) {
                btnTangActionPerformed(evt);
                return;
            }
        }

        /* Dùng addRow() để thêm dữ liệu vào JTable trên form */
        Object[] rows = {tenMon, soLuong, donVi, donGia, thanhTien};
        tblModel_HD.addRow(rows);
        int idx = findIndex(tenMon, tblModel_HD);
        tblHoaDon.setRowSelectionInterval(idx, idx);
        btnXoaMon.setEnabled(true);
        btnTang.setEnabled(true);
        btnGiam.setEnabled(true);
        btnThanhToan.setEnabled(true);
        tongHoaDon();
    }//GEN-LAST:event_btnThemMonActionPerformed

    private void clearJTextField() {
        // Set tất cả các TextField và table rỗng
        tblModel_HD.setRowCount(0);
        txtKhachHang.setText("");
        txtSDT.setText("");
        txtDiaChi.setText("");
//        rdoSoBan.setText("");
        txtPhiDichVu.setText("");
        txtThue.setText("");
        txtTongTien.setText("");
        txtSDT.requestFocus(); // Focus về nhập MaSV đầu tiên
    }

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        clearJTextField();
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        /* Kiểm tra rỗng, kiểm tra biểu thức chính quy, kiểm tra trùng lặp Mã Sinh Viên */
        if (checkBeforeSaving()) {
            return;
        }
        /* Lưu dữ liệu vào bảng “” lấy từ các JTextField 
        và đồng thời thêm dữ liệu đó vào JTable khi form mở */
        saveData();
        clearJTextField();
        hoaDonTuDong();
    }//GEN-LAST:event_btnSaveActionPerformed

    private boolean checkBeforeSaving() {
        // Lấy dữ liệu từ các JTextField lưu vào biến
        String hoTen = txtKhachHang.getText();
        String soDT = txtSDT.getText();
        String diaChi = txtDiaChi.getText();
        /* Nếu chưa nhập HỌ TÊN thì checkEmpty() trả về true, 
        câu lệnh bên trong if được thực thi và return khỏi phương thức */
 /* Họ Tên không cần kiểm tra biểu thức chính quy và dữ liệu trùng lặp */
        if (CheckInput.checkEmpty(hoTen, "Bạn chưa nhập HỌ TÊN cần thêm!")) {
            return true;
        }
        /* Nếu chưa nhập SỐ ĐIỆN THOẠI thì checkEmpty() trả về true, 
        câu lệnh bên trong if được thực thi và return khỏi phương thức */
        if (CheckInput.checkEmpty(soDT, "Bạn chưa nhập SỐ ĐIỆN THOẠI cần thêm!")) {
            return true;
        }
        /* Nếu SỐ ĐIỆN THOẠI không khớp với biểu thức quy định thì checkRegex() trả về true, 
        câu lệnh bên trong if được thực thi và return khỏi phương thức */
        if (CheckInput.checkRegex(soDT, REGEX_SODT, "SỐ ĐIỆN THOẠI bạn nhập không hợp lệ!")) {
            return true;
        }
        // Nếu không rơi vào trường hợp if nào hết, phương thức sẽ return false
        return false;
    }

    private void saveData() {
        // Tạo các biến chứa các dữ liệu được người dùng nhập vào các JTextField
        String hoTen = txtKhachHang.getText();
        String soDT = txtSDT.getText();
        String diaChi = txtDiaChi.getText();
        String maHoaDon = txtHoaDon.getText();

        String ngay = lblDate.getText();
        Date utilDate;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        try {
            utilDate = formatter.parse(ngay);
        } catch (ParseException ex) {
            Logger.getLogger(DatMon.class.getName()).log(Level.SEVERE, null, ex);
            utilDate = new java.util.Date();
        }
        java.sql.Date ngayLapHD = new java.sql.Date(utilDate.getTime());

        String hinhThucTT = (String) cboPay.getSelectedItem();

        // Lưu thông tin khách hàng vào bảng khách hàng
        // Cần thêm phần kiểm tra khách hàng đã có chưa, nếu có rồi thì không cần lưu, nếu chưa thì lưu mới vào bảng khách hàng
        try {
            /* Nếu SỐ ĐIỆN THOẠI đã có trong database khách hàng thì checkDuplicate() trả về true, 
        câu lệnh bên trong if được thực thi và return khỏi phương thức */
            if (CheckInput.checkDuplicate(soDT, con, "KhachHang", "soDT")) {
                JOptionPane.showConfirmDialog(this, "Khách hàng cũ!");
            } else {
                String sql = "Insert into KhachHang values (?,?,?)";
                Object args[] = {hoTen, soDT, diaChi};
                int rs = prepareStatement(sql, args).executeUpdate();
                if (!(rs > 0)) { // Nếu không có kết quả, sẽ hiện thông báo chưa được lưu
                    JOptionPane.showMessageDialog(this, "Hóa Đơn" + txtKhachHang.getText() + " chưa được lưu!");
                    return;
                }
                prepareStatement(sql, args).close();
            }

            // Lưu vào HoaDon
            String tenNV = lblNhanVien.getText();
            String sqlMaNV = "Select MaNV From NhanVien Where HoTenNV = ?";
            ResultSet rs = executeQuery(sqlMaNV, tenNV);
            String maNV = "";
            if (rs.next()) {
                maNV = rs.getString(1);
            }
            System.out.println("maNV = " + maNV);

            String sqlHD = "Insert into HoaDon (NgayLapHD, HinhThucTT, MaNV, MaKH) values (?,?,?,?)";
            Object argsHD[] = {ngayLapHD, hinhThucTT, maNV, soDT};
            int rsHD = prepareStatement(sqlHD, argsHD).executeUpdate();
            if (!(rsHD > 0)) {
                JOptionPane.showMessageDialog(this, "Hóa Đơn" + txtKhachHang.getText() + " chưa được lưu!");
                return;
            }

            // Lưu vào HoaDonChiTiet
            String sqlHDCT = "Insert into HoaDonCT (MaHoaDon, MaMon, SoLuong, DonViTinh) values ";
            int len = tblHoaDon.getRowCount();
            if (len == 0) {
                JOptionPane.showMessageDialog(this, "Hóa Đơn chưa có thông tin. Lưu thất bại!");
                return;
            }
            for (int i = 0; i < len; i++) {
                String tenMon = (String) tblHoaDon.getValueAt(i, 0);
                String sqlMaMon = "Select MaMon From MonAn Where TenMon = ?";

                ResultSet rsMaMon = executeQuery(sqlMaMon, tenMon);
                String maMon = "";
                if (rsMaMon.next()) {
                    maMon = rsMaMon.getString(1);
                }
                System.out.println("maMon = " + maMon);

                int soLuong = (int) tblHoaDon.getValueAt(i, 1);
                String donViTinh = (String) tblHoaDon.getValueAt(i, 2);
                sqlHDCT += String.format("('%s', '%s', %d, '%s')%c", maHoaDon, maMon, soLuong, donViTinh, (i == len - 1) ? ';' : ',');
            }
            int rsHDCT = prepareStatement(sqlHDCT).executeUpdate();

            if ((rsHDCT > 0)) {
                JOptionPane.showMessageDialog(this, "Hóa Đơn " + txtKhachHang.getText() + " đã được lưu!");
            }
            prepareStatement(sqlHD, argsHD).close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi:" + ex);
            System.exit(0);
        }
    }

    private void btnThoatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThoatActionPerformed
        exitForm();
    }//GEN-LAST:event_btnThoatActionPerformed

    private void exitForm() {

        // LÀM PHẦN KHÁCH HÀNG TRƯỚC - SẼ THAY DỔI THÀNH HÓA ĐƠN SAU
        int save = 0, thoat = 0;
        // Nếu txtMaKH không có dữ liệu, Đưa ra câu hỏi yêu cầu người dùng xác nhận có chắc chắn thoát hay không? 
        if (txtSDT.getText().equals("")) {
            thoat = (JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn thoát?", "Xác nhận", JOptionPane.YES_NO_OPTION));
            // Nếu người dụng chọn Yes, gọi phương thức exit(0) để thoát chương trình
            if (thoat == JOptionPane.YES_OPTION) {
                backToLogin();
            }
        } else {
            // Ngược lại, nếu txtMaKH có dữ liệu, hỏi người dùng có cần lưu lại không? 
            save = (JOptionPane.showConfirmDialog(this, "Bạn chưa lưu dữ liệu, bạn có muốn lưu lại và thoát?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION));

            /* Nếu người dùng chọn Yes thì gọi phương thức kiểm tra trùng lặp, nếu trùng lặp thì update, 
               nếu không thì lưu thông tin khách hàng mới và thoát chương trình */
            if (save == JOptionPane.YES_OPTION) {
                if (CheckInput.checkDuplicate(txtSDT.getText(), con, "KhachHang", "MaKH")) {
//                    this.update();
                } else {
                    this.saveData();
                }
                backToLogin();
            }
        }
    }

    private void backToLogin() {
        // Đóng QuanLySinhVien => Mở LogIn
//        Login_Form login = new Login_Form();
//        login.setVisible(true);
        this.dispose();
    }

    private void btnFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindActionPerformed
        tblHoaDon.clearSelection(); // Xóa focus vào bảng
        /* Cho người dùng nhập SDT cần tìm vào InputDialog và lưu vào biến soDT 
           Nếu không hợp lệ, yêu cầu nhập lại. */
        String soDT = "";
        do {
            soDT = JOptionPane.showInputDialog(this, "Nhập số điện thoại cần tìm tại đây:");
//            soDT = "0385665555";
            /* Nếu đã nhập SỐ ĐIỆN THOẠI thì kiểm tra quy tắc nhập */
            if (!CheckInput.checkEmpty(soDT, "Bạn chưa nhập SỐ ĐIỆN THOẠI cần tìm!")) {
                CheckInput.checkRegex(soDT, REGEX_SODT, "SỐ ĐIỆN THOẠI bạn nhập không hợp lệ!");
            }
            /* Nếu SỐ ĐIỆN THOẠI đã có trong database khách hàng thì checkDuplicate() trả về true, 
            câu lệnh bên trong if được thực thi và thoát khỏi vòng lặp */
            if (CheckInput.checkDuplicate(soDT, con, "KhachHang", "soDT")) {
                break;
            }
        } while (true);
        findCustomById(soDT);
    }//GEN-LAST:event_btnFindActionPerformed

    private void btnFindHDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindHDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFindHDActionPerformed

    private void btnXoaMonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaMonActionPerformed
        int idx = tblHoaDon.getSelectedRow();
        System.out.println("First: " + idx);
        if (idx >= 0) {
            tblModel_HD.removeRow(idx); // Xóa khỏi JTable trên form
            if (idx >= 0) {
                --idx;
            }
            if (idx >= 0) {
                System.out.println("kq" + idx);
                tblHoaDon.setRowSelectionInterval(idx, idx);
            } else {
                btnXoaMon.setEnabled(false);
                btnTang.setEnabled(false);
                btnGiam.setEnabled(false);
            }
        }
        tongHoaDon();
    }//GEN-LAST:event_btnXoaMonActionPerformed

    private void btnTangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTangActionPerformed
        // TODO add your handling code here:
        int i = tblHoaDon.getSelectedRow();
        int soLuong = (int) tblHoaDon.getValueAt(i, 1) + 1;
        tblHoaDon.setValueAt(soLuong, i, 1);

        float donGia = (float) tblHoaDon.getValueAt(i, 3);
        float thanhTien = (float) donGia * soLuong;
        tblHoaDon.setValueAt(thanhTien, i, 4);

        tongHoaDon();
    }//GEN-LAST:event_btnTangActionPerformed

    private void btnGiamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGiamActionPerformed
        // TODO add your handling code here:
        int i = tblHoaDon.getSelectedRow();
        int soLuong = (int) tblHoaDon.getValueAt(i, 1);
        if (soLuong > 1) {
            soLuong = soLuong - 1;
        }
        tblHoaDon.setValueAt(soLuong, i, 1);

        float donGia = (float) tblHoaDon.getValueAt(i, 3);
        float thanhTien = (float) donGia * soLuong;
        tblHoaDon.setValueAt(thanhTien, i, 4);

        tongHoaDon();
    }//GEN-LAST:event_btnGiamActionPerformed

    private void btnThanhToanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThanhToanActionPerformed
//        if (CheckInput.checkDuplicate(soDT, con, "KhachHang", "soDT")) {
//                btnSaveActionPerformed(evt);
//                return;
//            }
//        
        btnUpdateActionPerformed(evt);
    }//GEN-LAST:event_btnThanhToanActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(this, "Hóa Đơn " + txtKhachHang.getText() + " đã được update!");
    }//GEN-LAST:event_btnUpdateActionPerformed

    private boolean findCustomById(String soDT) {
//        tblHoaDon.clearSelection(); tblHoaDon.clearSelection();
        try {
            String sql = "Select * From KhachHang where SoDT = ?";
            // Biên dịch câu lệnh SQL trước
            PreparedStatement st = con.prepareStatement(sql);
            st.setString(1, soDT);
            // Thi hành các câu lệnh truy vấn
            ResultSet rs = st.executeQuery();
            // ResultSet cho phép truy xuất đến dữ liệu trả về từ kết quả truy vấn database
            // Gọi hàm next() để di chuyển con trỏ hiện hành đến hàng kế tiếp của ResultSet
            // Next() trả về true nghĩa là còn dữ liệu để đọc, ngược lại là không có hàng nào
            if (rs.next()) {
                // Dùng getString()để đọc dữ liệu
                txtKhachHang.setText(rs.getString(1));
                txtSDT.setText(rs.getString(2));
                txtDiaChi.setText(rs.getString(3));
            } else {
                JOptionPane.showMessageDialog(this, "Khách hàng này là KHÁCH HÀNG MỚI!",
                        "Lỗi", JOptionPane.WARNING_MESSAGE);
            }
            st.close();
            rs.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi:" + ex);
            System.exit(0);
        }
        return true;
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DatMon().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBia;
    private javax.swing.JButton btnDanhMuc;
    private javax.swing.JButton btnDatMon;
    private javax.swing.JButton btnFind;
    private javax.swing.JButton btnFindHD;
    private javax.swing.JButton btnGiam;
    private javax.swing.JButton btnGoi;
    private javax.swing.JButton btnHaiSan;
    private javax.swing.JButton btnHuongDan;
    private javax.swing.JButton btnIn;
    private javax.swing.JButton btnKhaiVi;
    private javax.swing.JButton btnMonBo;
    private javax.swing.JButton btnMonGa;
    private javax.swing.JButton btnMonHeo;
    private javax.swing.JButton btnMonLau;
    private javax.swing.JButton btnMonRau;
    private javax.swing.JButton btnMonSup;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnNuocEp;
    private javax.swing.JButton btnNuocNgot;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSinhTo;
    private javax.swing.JButton btnSoDoBan;
    private javax.swing.JButton btnTang;
    private javax.swing.JButton btnThanhToan;
    private javax.swing.JButton btnThemMon;
    private javax.swing.JButton btnThoat;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnXoaMon;
    private javax.swing.JComboBox<String> cboPay;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblClock;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblNhanVien;
    private javax.swing.JTable tblHoaDon;
    private javax.swing.JTable tblThucDon;
    private javax.swing.JTextField txtDiaChi;
    private javax.swing.JTextField txtHoaDon;
    private javax.swing.JTextField txtKhachHang;
    private javax.swing.JTextField txtPhiDichVu;
    private javax.swing.JTextField txtSDT;
    private javax.swing.JTextField txtThue;
    private javax.swing.JTextField txtTongTien;
    // End of variables declaration//GEN-END:variables

    public PreparedStatement prepareStatement(String sql, Object... args) throws SQLException {
        PreparedStatement st = con.prepareStatement(sql); // Biên dịch câu lệnh SQL trước
        for (int i = 0; i < args.length; i++) {
            st.setObject(i + 1, args[i]);
        }
//        System.out.println(st.toString());
        return st;
    }

    public ResultSet executeQuery(String sql, Object... args) {
        try {
            PreparedStatement stmt = prepareStatement(sql, args);
            return stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int findIndex(String tenMon, DefaultTableModel tblModel) {
        int idx = 0;
        Vector all = tblModel.getDataVector();
        while (idx < all.size()) {
            Vector sv = (Vector) all.elementAt(idx);
            if (sv.elementAt(0).toString().equals(tenMon)) {
                return idx;
            }
            idx++;
        }
        if (idx == all.size()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin cần tìm!");
            this.clearJTextField();
            return 0;
        }
        return 0;
    }

    private void tongHoaDon() {
        float sum = 0;
        for (int i = 0; i < tblHoaDon.getRowCount(); i++) {
            sum = sum + (float) tblHoaDon.getValueAt(i, 4);
        }
        txtPhiDichVu.setText(String.valueOf(sum * 0.05));
        txtThue.setText(String.valueOf(sum * 0.1));
        txtTongTien.setText(String.valueOf(sum * 0.05 + sum * 0.1 + sum));
    }

    private void hoaDonTuDong() {
        /* Số hóa đơn tự động */
        String sqlHD = "Select Max(MaHoaDon) From HoaDon";
        int maHoaDon;
        try {
            ResultSet rs = executeQuery(sqlHD);
            if (rs.next()) {
                maHoaDon = rs.getInt(1) + 1;
                txtHoaDon.setText(String.valueOf(maHoaDon));
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        txtHoaDon.setEnabled(false);
    }

    private boolean checkBeforeUpdating() {

        return false; // Nếu không rơi vào trường hợp if nào hết, phương thức sẽ return false
    }
}
