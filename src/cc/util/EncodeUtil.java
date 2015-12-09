package cc.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class EncodeUtil {
	private static EncodeUtil  inst  = new EncodeUtil();
	public static EncodeUtil getInstance(){
		return inst;
	}
	
	/**
	 * 加密
	 * @param pwd
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public String encodeString(String pwd) throws UnsupportedEncodingException{
		byte[] data = pwd.getBytes("GBK");
		return new sun.misc.BASE64Encoder().encode(data) ;
	}
	
	/**
	 * 解密
	 * @param pwd
	 * @return
	 * @throws IOException 
	 */
	public String decodeString(String pwd) throws IOException{
		byte[] data = new sun.misc.BASE64Decoder().decodeBuffer(pwd);
		return new String(data,"gbk");
	}
}
