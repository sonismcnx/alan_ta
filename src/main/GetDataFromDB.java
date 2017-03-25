package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import main.Queries;

/**
 * Servlet implementation class GetDataFromDB
 */
@WebServlet("/GetDataFromDB")
public class GetDataFromDB extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String dbName = "alan_ta";
	private static String dbUser = "alan";
	private static String dbPass = "beng24a92ec2";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	String format = request.getParameter("fmt");
    	String srv = request.getParameter("srv");
    	String strQLimit = request.getParameter("qLimit");
    	String outputStr = new String();
//    	Enumeration<String> parnames = request.getParameterNames();
//    	while(parnames.hasMoreElements()) {
    		System.out.println(request.getParameter("qLimit"));
//    	}
    	
    	int qLimit = 0;
    	if(strQLimit.equals("b")) {qLimit = 1000;}
    	else if(strQLimit.equals("c")) {qLimit = 10000;}
    	else {qLimit = 100;}
    	System.out.println(qLimit);
//    	try{
//    		qLimit = Integer.valueOf(strQLimit).intValue();
//    	} catch(NumberFormatException nfe) {
//    		qLimit = 100;
//    	}
    	
    	// set the intended content type
    	// response.setContentType("application/json");
    	// or
    	// response.setContentType("text/html; charset=UTF-8");
    	PrintWriter out = response.getWriter();
    	try {
	    	if(format.equals("xml")) {
	    		switch(srv) {
	    			case "dir" :
	    				outputStr = fetchDirDataXML();
	    				break;
	    			case "sql" :
	    				outputStr = fetchDBDataXML(qLimit);
	    				break;
	    			default :
	    				response.sendError(404);
	    				break;//return;
	    		}
	    		out.println("<?xml version=\"1.0\" encoding=\"UTF-16\" ?>");
	    		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
	    		
//	    		response.setContentType("text/xml;charset=UTF-16");
//	    		response.setCharacterEncoding("UTF-8");
	    		out.println(outputStr);
	    	} else if (format.equals("json")) {
	    		switch(srv) {
	    			case "dir" :
	    				outputStr = fetchDirDataJSON();
	    				break;
	    			case "sql" :
	    				outputStr = fetchDBDataJSON(qLimit);
	    				break;
	    			default :
	    				response.sendError(404);
	    				break;//return;
	    		}
	    		response.setContentType("application/json");
	    		out.println(outputStr);
	    	} else {
	    		response.sendError(404);
	    	}
    	} catch(NamingException e) {
    		response.sendError(504);
    	} catch (NullPointerException e) {
    		response.sendError(404);
    	} catch (SQLException e) {
    		response.sendError(504);
    	} catch(ClassNotFoundException e) {
    		response.sendError(500);
    	}
    		
    }
    
    private String fetchDBDataJSON(int qLimit) throws ClassNotFoundException, SQLException {
    	String jsonRoot = new String();
    	String jsonData = new String();
    	boolean lastCol;
    	
    	// Hey man!!!
    	// Dont Forget to add the mysql-connector-java-x.x.x-bin.jar to the Deployment Assembly!
    	// it's on the Project Property window ;o
    	// you wrote this comment after you were having a headache because you forgot to do that! :p
    	// the runtime processor will give you ClassNotFoundException for that! dayyum! X|
    	String driver = "com.mysql.jdbc.Driver";
    	Class.forName(driver);
    	
    	String url = "jdbc:mysql://localhost:3306/" + dbName;
    	Connection conn = (Connection) DriverManager.getConnection(url, dbUser, dbPass);
    	String query = new String();
    	if(qLimit==100) {
    		query = Queries.getRooms(100);
    	} else if(qLimit==1000) {
    		query = Queries.getCustomers(1000);
    	} else if(qLimit==10000) {
    		query = Queries.getPayments(10000);
    	} else {
    		query = Queries.getRooms(100);
    	}
		System.out.println(query);
    	
    	Statement stmt = (Statement) conn.createStatement();
    	ResultSet rs = stmt.executeQuery(query);
    	
    	int colCount = rs.getMetaData().getColumnCount();
    	String escColData = new String();
    	
    	jsonData = jsonData.concat("\"entries\":[");
    	
    	while(rs.next()) {

    		lastCol = false;
    		
    		jsonData = jsonData.concat("{");
    		
    		for(int i = 1; i <= colCount; i++) {
    			
    			lastCol = ( i != colCount ) ? false : true;

				escColData = StringEscapeUtils.escapeHtml4(rs.getString(i));
				jsonData = jsonData + "\""+ rs.getMetaData().getColumnName(i) + "\":" + "\"" + escColData + "\"";
				
				if(!lastCol) {
					jsonData = jsonData.concat(",");
				} else {
					jsonData = jsonData.concat("}");
				}
				
    		}
			
			jsonData = rs.isLast() ? jsonData.concat("]") : jsonData.concat(",");
    	}
    	conn.close(); // close the SQL connection

    	jsonRoot = "{" + jsonData + "}";
    	return jsonRoot; 
    }
    
    private String fetchDBDataXML(int qLimit) throws ClassNotFoundException, SQLException {
    	String xmlData = new String();
    	
//    	create connection to database
    	String driver = "com.mysql.jdbc.Driver";
    	Class.forName(driver);
    	
    	String url = "jdbc:mysql://localhost:3306/" + dbName;
    	Connection conn = (Connection) DriverManager.getConnection(url, dbUser, dbPass);
    	
    	String query = new String();
    	
    	if(qLimit==100) {
    		query = Queries.getRooms(100);
    	} else if(qLimit==1000) {
    		query = Queries.getCustomers(1000);
    	} else if(qLimit==10000) {
    		query = Queries.getPayments(10000);
    	} else {
    		query = Queries.getRooms(100);
    	}
		System.out.println(query);
    	
    	Statement stmt = (Statement) conn.createStatement();
    	ResultSet rs = stmt.executeQuery(query);
    	
    	int colCount = rs.getMetaData().getColumnCount();
    	
    	String colName = new String();
    	String escColData = new String();
    	
    	xmlData = "<root><entries>";
    	
//    	put data into order
    	while(rs.next()) {
    		
    		xmlData = xmlData + "<entry>";
    		
    		for(int i = 1; i <= colCount; i++) {
				
    			colName = rs.getMetaData().getColumnName(i);
    			escColData = StringEscapeUtils.escapeHtml4(rs.getString(i));
    			
				xmlData = xmlData + "<" + colName + ">" + 
										escColData + 
									"</" + colName + ">";
				
    		}
    		
    		xmlData = xmlData + "</entry>";
			
    	}
    	conn.close(); // close the SQL connection
    	
    	xmlData = xmlData + "</entries></root>";
    	    	
    	return xmlData;
    }
    
    private SearchControls getSimpleSearchControls() {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setTimeLimit(30000);
        //String[] attrIDs = {"objectGUID"};
        //searchControls.setReturningAttributes(attrIDs);
        return searchControls;
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
    
    private String fetchDirDataJSON() throws NamingException {
    	String jsonRoot = new String();
    	String jsonData = new String();
    	Long timeThen = 0L;
		Long timeLater = 0L;
		boolean firstAttr, lastAttr;
		
    	Hashtable<String, String> env = new Hashtable<String, String>();
    	env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
    	env.put(Context.PROVIDER_URL, "ldap://localhost:1389/dc=kosago,dc=net");

    	LdapContext ctx = new InitialLdapContext(env, null);
    	@SuppressWarnings("rawtypes")
		NamingEnumeration namingEnum = ctx.search("ou=people", "(objectclass=person)", getSimpleSearchControls());
    		    		   		
    	String attrID = new String();
    	String attrValue = new String();
    		
    	timeThen = new Date().getTime();
    	jsonData = jsonData.concat("[");
    		
    	while (namingEnum.hasMore()) {
    		SearchResult sr = (SearchResult) namingEnum.next();
    		Attributes attrs = sr.getAttributes();

    		firstAttr = true;
    		lastAttr = false;
    			
    		for(@SuppressWarnings("rawtypes")
			NamingEnumeration entryAttrs = attrs.getIDs(); entryAttrs.hasMore();) {
    			attrID = entryAttrs.next().toString();
    			attrValue = attrs.get(attrID).get().toString();
    				
				lastAttr = entryAttrs.hasMore() ? false : true;
				
				if(firstAttr) { 
					jsonData = jsonData.concat("{");
					firstAttr = false;
				}
				
				jsonData = jsonData + "\""+ attrID + "\" : " + "\"" + attrValue + "\"";
				
				if(!lastAttr) {
					jsonData = jsonData.concat(",");
				} else {
					jsonData = jsonData.concat("}");
				}
				
			}
			
			jsonData = namingEnum.hasMore() ? jsonData.concat(",") : jsonData;
			
		}
		
		jsonData = jsonData.concat("]");
		timeLater = new Date().getTime();
		
		jsonRoot = "{" + 
						"\"time\" : { " +
									"\"start\" : \"" + timeThen + "\"," +
									"\"end\" : \"" + timeLater + "\"," +
									"\"diff\" : \"" + (timeLater-timeThen) + "\"" +
								"}," +
						"\"data\" : " + jsonData +
					"}";
    	
    	return jsonRoot;
    }
    
    private String fetchDirDataXML() throws NamingException {
    	
    	String xmlRoot = new String();
    	String xmlData;
    	Long timeThen = 0L;
    	Long timeLater = 0L;
    	    	
    	Hashtable<String, String> env = new Hashtable<String, String>();
    	env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
    	env.put(Context.PROVIDER_URL, "ldap://localhost:1389/dc=kosago,dc=net");
    		
		LdapContext ctx = new InitialLdapContext(env, null);
		@SuppressWarnings("rawtypes")
		NamingEnumeration namingEnum = ctx.search("ou=people", "(objectclass=person)", getSimpleSearchControls());
		
		String attrID = new String();
		String attrValue = new String();
		
		timeThen = new Date().getTime();
		xmlData = "<data>";
		
		while(namingEnum.hasMore()) {
			SearchResult sr = (SearchResult) namingEnum.next();
			Attributes attrs = sr.getAttributes();
			
			xmlData = xmlData.concat("<entry>");

			for(@SuppressWarnings("rawtypes")
			NamingEnumeration entryAttrs = attrs.getIDs(); entryAttrs.hasMore();) {
				attrID = entryAttrs.next().toString();
				attrValue = attrs.get(attrID).get().toString();
				xmlData = xmlData + "<" + attrID + ">" + attrValue + "</" + attrID + ">";
			}
			xmlData = xmlData.concat("</entry>");
			
		}
		
		xmlData = xmlData.concat("</data>");
		
		timeLater = new Date().getTime();
		
		xmlRoot = "<root>" + 
					"<time>" + 
						"<begin>" + timeThen + "</begin>" + 
						"<end>" + timeLater + "</end>" + 
						"<diff>" + (timeLater-timeThen) + "</diff>" + 
					"</time>" +
					xmlData + 
				"</root>";
		
    	return xmlRoot;
    }

}
