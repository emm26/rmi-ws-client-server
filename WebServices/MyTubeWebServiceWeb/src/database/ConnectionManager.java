
package database;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import utils.Output;

import javax.naming.InitialContext;

public class ConnectionManager {
	
	Connection conn = null;

    protected void openConnection() {
        try {
        	InitialContext cxt = new InitialContext();
        	DataSource ds = (DataSource) cxt.lookup("java:/PostgresXADS");
        	conn = ds.getConnection();
        	conn.setAutoCommit(false);
        } catch (Exception e) {
            Output.printError("While openning connection: " + e.toString());
            //e.printStackTrace();
            System.exit(0);
        }
    }

    protected void closeConnection(){
        try {
            conn.close();
        } catch (SQLException e) {
        	Output.printError("While closing connection: " + e.toString());
            //e.printStackTrace();
            System.exit(0);
        }
    }
    
}



