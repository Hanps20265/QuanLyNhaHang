/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Helper.ConnectDatabase;
import Model.NhanVien;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author admin
 */
public class NhanVienDAO extends MainDao<NhanVien, String>{
    public void insert(NhanVien model){
        String sql="INSERT INTO NhanVien (MaNV, HotenNV, GioiTing, SDT, DiaChi, MaCV, MaCaTruc, Luong, MatKhau) VALUES (?, ?, ?, ?, ?, ? ,? ,? ,?)";
        ConnectDatabase.update(sql, 
                model.getMaNV(),model.getHoTen(),model.getGioiTinh(),model.getsDt(),model.getDiaChi(),
                model.getMaCv(),model.getMaCatruc(),model.getLuong(),model.getMatKhau());
    }
    
    public void update(NhanVien model){
        String sql="UPDATE NhanVien SET HotenNV=?, GioiTinh=?, SDT=?, Diachi=?, MaCV= ?, MaCaTruc= ?, Luong = ? , Matkhau = ? WHERE MaNV=?";
        ConnectDatabase.update(sql, 
                model.getHoTen(),model.getGioiTinh(),model.getsDt(),model.getDiaChi(),
                model.getMaCv(),model.getMaCatruc(),model.getLuong(),model.getMatKhau(),model.getMaNV());
    }
    
    public void delete(String MaNV){
        String sql="DELETE FROM NhanVien WHERE MaNV=?";
        ConnectDatabase.update(sql, MaNV);
    }
    
    public List<NhanVien> selectAll(){
        String sql="SELECT * FROM NhanVien";
        return this.selectBySql(sql);
    }
    
    public NhanVien selectById(String manv){
        String sql="SELECT * FROM NhanVien WHERE MaNV=?";
        List<NhanVien> list = this.selectBySql(sql, manv);
        return list.size() > 0 ? list.get(0) : null;
    }
    
    protected List<NhanVien> selectBySql(String sql, Object...args){
        List<NhanVien> list=new ArrayList<>();
        try {
            ResultSet rs = null;
            try {
                rs = ConnectDatabase.query(sql, args);
                while(rs.next()){
                    NhanVien entity=new NhanVien();
                    entity.setMaNV(rs.getString("MaNV"));
                    entity.setHoTen(rs.getString("HotenNV"));
                    entity.setGioiTinh(rs.getString("Gioitinh"));
                    entity.setsDt(rs.getString("SoDT"));
                    entity.setDiaChi(rs.getString("DiaChi"));
                    entity.setMaCv(rs.getString("MaCV"));
                    entity.setMaCatruc(rs.getString("MaCatruc"));
                    entity.setLuong(rs.getDouble("Luong"));
                    entity.setMatKhau(rs.getString("Matkhau"));
                    list.add(entity);
                }
            } 
            finally{
                rs.getStatement().getConnection().close();
            }
        } 
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        return list;
    }
}
