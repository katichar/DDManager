package cc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Jdbc {
    private Jdbc() {}
    private static synchronized Connection init() {
    	Connection conn = null ;
        try {
        	ConfigInfo ri = ConfigInfo.getInstance() ;
            // 加载驱动
            Class.forName(ri.getDbDriver());
            conn = DriverManager.getConnection(ri.getDbUrl(), ri.getDbUser(), ri.getDbPassword());
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
    // 返回Connection
    public  static Connection createConnection() {
    	Connection conn  = init();
        return conn;
    }

    // 返回Connection
    public  static void closeConnection(Connection conn) {
        if(conn !=null ){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			};
        }
    }
    
}
