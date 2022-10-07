package Model;

public class ThucDon {
    private String maMon;
    private String maLoaiMon;
    private String tenMon;
    private float gia;
    private String donViHoaDon;

    public ThucDon() {
    }

    public ThucDon(String maMon, String maLoaiMon, String tenMon, float gia, String donViHoaDon) {
        this.maMon = maMon;
        this.maLoaiMon = maLoaiMon;
        this.tenMon = tenMon;
        this.gia = gia;
        this.donViHoaDon = donViHoaDon;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public float getGia() {
        return gia;
    }

    public void setGia(float gia) {
        this.gia = gia;
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        this.maMon = maMon;
    }

    public String getMaLoaiMon() {
        return maLoaiMon;
    }

    public void setMaLoaiMon(String maLoaiMon) {
        this.maLoaiMon = maLoaiMon;
    }

    public String getDonViHoaDon() {
        return donViHoaDon;
    }

    public void setDonViHoaDon(String donViHoaDon) {
        this.donViHoaDon = donViHoaDon;
    }
    
    
}
