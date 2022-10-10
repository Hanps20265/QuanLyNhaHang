/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author admin
 */
public class HoaDon {
    private String maHd;
    private String nguoiLap;
    private String ngayLap;
    private String hinhThuctt;
    private String maKhachhang;
    public String getMaHd() {
        return maHd;
    }

    public void setMaHd(String maHd) {
        this.maHd = maHd;
    }

    public String getNguoiLap() {
        return nguoiLap;
    }

    public void setNguoiLap(String nguoiLap) {
        this.nguoiLap = nguoiLap;
    }

    public String getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(String ngayLap) {
        this.ngayLap = ngayLap;
    }

    public String getHinhThuctt() {
        return hinhThuctt;
    }

    public void setHinhThuctt(String hinhThuctt) {
        this.hinhThuctt = hinhThuctt;
    }

    public String getMaKhachhang() {
        return maKhachhang;
    }

    public void setMaKhachhang(String maKhachhang) {
        this.maKhachhang = maKhachhang;
    }
    public List<String> getEntity()
    {
        List<String> list = new ArrayList<>();
        list.add("MaHoaDon");
        list.add("NgayLapHD");
        list.add("NguoiLapHD");
        list.add("HinhThucTT");
        list.add("MaKH");
        return list;
    }
}
