package Helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class ConnectDatabase {
    private static String driver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static String dburl="jdbc:sqlserver://localhost;database=QLNHAHANG_NHOM3";
    private static String username="sa";
    private static String password="123456";
    
//    private static String dburl="jdbc:mysql://localhost:3306/QLNHAHANG_NHOM3";
//    private static String username="root";
//    private static String password="1234567908";
    
//    static{
//        try {            
//            Class.forName(driver);
//        } 
//        catch (ClassNotFoundException ex) {
//            throw new RuntimeException(ex);
//        }
//    }
    
    
    public static PreparedStatement getStmt(String sql, Object...args) throws SQLException{
        
        Connection connection = DriverManager.getConnection(dburl, username, password);
        PreparedStatement pstmt = null;
        if(sql.trim().startsWith("{")){
            pstmt = connection.prepareCall(sql);
            // Nếu câu lệnh SQL có tồn tại dấu { (lệnh starsWith trả về true) thì dùng prepareCall gọi PROC
        }
        else{
            pstmt = connection.prepareStatement(sql);
            // Biên dịch 1 lần câu lệnh SQL
        }
        
        for(int i=0; i < args.length; i++){
            pstmt.setObject(i+1, args[i]); 
        }
        
        return pstmt;
    }
    
    public static ResultSet query(String sql, Object...args) {
        try {
            PreparedStatement stmt = ConnectDatabase.getStmt(sql, args);
            ResultSet rs = stmt.executeQuery();
            return rs;
        } 
        catch (Exception e) {
            System.out.println("Lỗi 1: "+ e);
        }
        return null;
    }
    
    public static void update(String sql, Object...args) {
        try {
            PreparedStatement stmt = ConnectDatabase.getStmt(sql, args);
            try {
                stmt.executeUpdate();
            } 
            finally{
                stmt.getConnection().close();
            }
        } 
        catch (Exception e) {
            System.out.println("Lỗi: "+ e);
        }
    }
    
    public static Object value(String sql, Object...args) {
        try {
            ResultSet rs = ConnectDatabase.query(sql, args);
            if(rs.next()){
                return rs.getObject(0);
            }
            rs.getStatement().getConnection().close();
            
        } catch (Exception e) {
            System.out.println("Lỗi: "+ e);
        } 
        return null;
    }
}
