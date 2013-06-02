package edu.kit.aifb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;

public class WebAppJdbcFactory implements JdbcFactory {
	static Log logger = LogFactory.getLog( WebAppJdbcFactory.class );
	
	DataSource dataSource;
	String dataSourceId;
	
	Connection connection;
	
	@Required
	public void setDataSourceId( String dataSourceId ) {
		this.dataSourceId = dataSourceId;
	}
	
	public Connection getConnection() throws SQLException {
		if( connection == null || connection.isClosed() ) {
			connection = createConnection();
		}
		return connection;
	}

	public Statement createStatement() throws SQLException {
		return getConnection().createStatement();
	}

	public PreparedStatement prepareStatement( String sql ) throws SQLException {
		return getConnection().prepareStatement( sql );
	}

	@Override
	protected void finalize() {
		try {
			if( connection != null && !connection.isClosed() ) {
				connection.close();
			}
		}
		catch( SQLException e ) {
			logger.error( e );
		}
	}

	public Connection createConnection() throws SQLException {
		try {
			if( dataSource == null ) {
				// Obtain our environment naming context
				Context initCtx = new InitialContext();
				Context envCtx = (Context) initCtx.lookup( "java:comp/env" );

				// Look up our data source
				dataSource = (DataSource)envCtx.lookup( dataSourceId );
			}
			return dataSource.getConnection();
		}
		catch( NamingException e ) {
			logger.error( e );
			throw new SQLException( "Data source could not be initialized." );
		}
	}
}
