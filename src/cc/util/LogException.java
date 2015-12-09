package cc.util;
public class LogException extends Throwable{
		private static final long serialVersionUID = 1L;
		 public LogException(Throwable e){
			 super(e);
		 }
		public String getMsg()
	    {
			StringBuffer sb = new StringBuffer( toString()) ;
			StackTraceElement[] sts = getStackTrace() ;
			for(StackTraceElement s:sts){
				sb.append(s.toString() ).append("\r\n");
			}
			return sb.toString() ;
	    }
	}