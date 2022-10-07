package Dao;

import Dao.MainDao;
import Dao.MainDao;
import Model.ThucDon;
import Helper.ConnectDatabase;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;


public class ThucDonDAO extends MainDao<ThucDon, String>{

    @Override
    public void insert(ThucDon model) {
        String sql="INSERT INTO MonAn (MaMon, TenMon, DonGia, DonViHoaDon, MaLoaiMon) VALUES (?, ?, ?, ?, ?)";
        ConnectDatabase.update(sql, model.getTenMon(), model.getGia());
    }

    @Override
    public void update(ThucDon model) {
        String sql="UPDATE MonAn SET MaMon = ? ,TenMon = ?,DonGia = ?, DonViTinh = ?, MaLoaiMon = ? WHERE MaMon=?";
        ConnectDatabase.update(sql, 
                model.getTenMon(),model.getGia());
    }

    @Override
    public void delete(String maMon) {
        String sql="DELETE FROM MonAn WHERE MaMon=?";
        ConnectDatabase.update(sql, maMon);
    }

    @Override
    public ThucDon selectById(String TenMon) {
        String sql="SELECT * FROM MonAn WHERE TenMon=? ";
        List<ThucDon> list = this.selectBySql(sql, TenMon);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<ThucDon> selectAll() {
        String sql="select * from MonAn";
        return this.selectBySql(sql);
    }

    @Override
    protected List<ThucDon> selectBySql(String sql, Object... args) {
       List<ThucDon> list=new ArrayList<>();
        try {
            ResultSet rs = null;
            try {
                rs = ConnectDatabase.query(sql, args);
                while(rs.next()){
                    ThucDon entity = new ThucDon();
                    entity.setMaMon(rs.getString("MaMon"));
                    entity.setTenMon(rs.getString("TenMon"));
                    entity.setGia(rs.getFloat("DonGia"));
                    entity.setDonViHoaDon(rs.getString("DonViHoaDon"));
                    entity.setMaLoaiMon(rs.getString("MaLoaiMon"));
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
    
    public List<ThucDon> selectThitHeo(String maLoaiMon) {
        String sql="SELECT * FROM MonAn WHERE MaLoaiMon=? ";
        return this.selectBySql(sql, maLoaiMon);
    }
}
