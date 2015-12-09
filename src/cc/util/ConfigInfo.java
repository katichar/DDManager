package cc.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.dmp.service.ImportData;

import cc.util.EncodeUtil;
/**
 * 数据库信息
 * @author chenzmb
 *
 */
public class ConfigInfo {
	//是否启动日志服务器
	private String logServer = "";
	//日志信息级别，只有在界面形式生效
	private String logLevel = ""; //0 显示正常信息，1 显示错误信息，2.显示sql以及错误信息
	//
	private String filepath ="./";
	
	//邮件信息
	private String mailHost ="";
	private String mailUser ="";
	private String mailPassword ="";
	private String mailrecv ="";//接收人
    // 数据信息
    private String dbUser = "";

    private String dbPassword = "";

    private String dbDriver = "";

    private String dbUrl = "";
    
    private ConfigInfo(){}
    // 单例
    private static ConfigInfo inst  = init();
    
    // 保存数据信息
    public void saveDbInfo(String user,String pwd,String url) {
    	try {
           
            if(Consts.ENCODE_DB_PWD){
				pwd = EncodeUtil.getInstance().encodeString(pwd);
            }
            
    		String content = 
    		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
    		+"<dataconfig> \r\n"		
    		+"\t<server> \r\n"
    				+"\t\t <logserver>"+getLogServer()+"</logserver> \r\n"
    				+"\t\t <loglevel>"+getLogLevel()+"</loglevel> \r\n"
    				+"\t\t <filepath>"+getFilepath()+"</filepath> \r\n"
    				+"\t\t <mailhost>"+getMailHost()+"</mailhost> \r\n"
    				+"\t\t <mailuser>"+getMailUser()+"</mailuser> \r\n"
    				+"\t\t <mailpassword>"+getMailPassword()+"</mailpassword> \r\n"
    				+"\t\t <mailrecv>"+getMailrecv()+"</mailrecv> \r\n"
    		+"\t</server>		 \r\n"
    		
    		+"\t<dbdata> \r\n"
    				+"\t\t <dbuser>"+user+"</dbuser> \r\n"
    				+"\t\t <dbpassword>"+pwd+"</dbpassword> \r\n"
    				+"\t\t <dbdriver> com.mysql.jdbc.Driver </dbdriver> \r\n"
    				+"\t\t <dburl> "+url.replaceAll("&", "&amp;")+ "</dburl> \r\n"
    		+"\t</dbdata> \r\n"
    		+"</dataconfig> \r\n" ;
    		
    		FileOutputStream fos = new FileOutputStream(Consts.CONFIG_FILE);
    		fos.write(content.getBytes());
    		fos.flush();
    		fos.close();
    		
    		setDbUser(user) ;
        	setDbPassword(pwd) ;
        	setDbUrl(url);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    	
	}
    

	// 解析数据信息
    private static ConfigInfo init() {
    	ConfigInfo ri = new ConfigInfo();
        try {
            SAXBuilder saxBuilder = new SAXBuilder();            
            Document doc = saxBuilder.build(new File(ImportData.path+Consts.CONFIG_FILE));
            Element rootElement = doc.getRootElement();
           
            Element dbData = rootElement.getChild("dbdata");
            ri.setDbUser(dbData.getChild("dbuser").getValue().trim());
            String dbpassword = dbData.getChild("dbpassword").getValue().trim() ;
            if(Consts.ENCODE_DB_PWD){
            	dbpassword = EncodeUtil.getInstance().decodeString(dbpassword) ;
            }
            ri.setDbPassword(dbpassword);
            ri.setDbUrl(dbData.getChild("dburl").getValue().trim());
            ri.setDbDriver(dbData.getChild("dbdriver").getValue().trim());
            
            
            dbData = rootElement.getChild("server");
            ri.setLogServer(dbData.getChild("logserver").getValue().trim());
            ri.setLogLevel(dbData.getChild("loglevel").getValue().trim());
            ri.setFilepath(dbData.getChild("filepath").getValue().trim());
            
            ri.setMailHost(dbData.getChild("mailhost").getValue().trim());
            ri.setMailUser(dbData.getChild("mailuser").getValue().trim());
            ri.setMailPassword(dbData.getChild("mailpassword").getValue().trim());
            
            ri.setMailrecv(dbData.getChild("mailrecv").getValue().trim());
        }
        catch (JDOMException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
        	e.printStackTrace();
        }
        return ri;
    }

    

    
    public String getMailrecv() {
		return mailrecv;
	}


	public void setMailrecv(String mailrecv) {
		this.mailrecv = mailrecv;
	}


	public String getFilepath() {
		return filepath;
	}


	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}


	public String getMailHost() {
		return mailHost;
	}


	public void setMailHost(String mailHost) {
		this.mailHost = mailHost;
	}


	public String getMailUser() {
		return mailUser;
	}


	public void setMailUser(String mailUser) {
		this.mailUser = mailUser;
	}


	public String getMailPassword() {
		return mailPassword;
	}


	public void setMailPassword(String mailPassword) {
		this.mailPassword = mailPassword;
	}

	
	public String getLogServer() {
		return logServer;
	}

	public void setLogServer(String logServer) {
		this.logServer = logServer;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public static ConfigInfo getInstance(){
    	return inst;
    }
	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public String getDbDriver() {
		return dbDriver;
	}

	public void setDbDriver(String dbDriver) {
		this.dbDriver = dbDriver;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

   
}
