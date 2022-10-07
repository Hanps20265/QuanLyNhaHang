/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Helper.ConnectDatabase;
import Model.HoaDon;
import java.sql.ResultSet;
import java.util.List;

/**
 *
 * @author admin
 */
public class HoaDonDAO extends MainDao<HoaDon, String>{

   @Override
    public void insert(HoaDon entity) {
        String sql = "insert into HOADON VALUES(?,?,?,?,?)";
        ConnectDatabase.update(sql,entity.getMaHd(),entity.getNguoiLap(),entity.getNgayLap(),entity.getHinhThuctt(),entity.getMaKhachhang());
    }

    @Override
    public void update(HoaDon entity) {
        String sql = "update HOADON set  NguoiLapHD = ?, NgayLapHD= ?, HinhThucTT= ?, MaKH = ? where MaHoaDon = ?";
        ConnectDatabase.update(sql, entity.getNguoiLap(),entity.getNgayLap(),entity.getHinhThuctt(),entity.getMaKhachhang(),entity.getMaHd());
    }

    @Override
    public void delete(String id) {
        String sql="DELETE FROM Hoadon WHERE MaHD = ?";
        ConnectDatabase.update(sql, id);
    }

    @Override
    public HoaDon selectById(String id) {
        String sql="SELECT * FROM HoaDon WHERE MaHD=?";
        List<HoaDon> list = this.selectBySql(sql, id);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<HoaDon> selectAll() {
        String sql="SELECT * FROM HoaDon";
        return this.selectBySql(sql);
    }

    @Override
    protected List<HoaDon> selectBySql(String sql, Object... args) {
        try {
            ResultSet rs = null;
            rs = ConnectDatabase.query(sql, args);
            for
        } catch (Exception e) {
        } finally {
        }
    }
}
