package cc.util;

public class Consts {
	/**
	 * =======================================================
	 */
	//同时处理的任务 数。并发
	public static final int NUM_CONCURRENT = 1;
	//EXCEL一次性处理的数据
	public static final int RECORDS_PER_REQ = 10000;
	//一次处理的任务 数
	public static final int NUM_PER_REQ = 5;
	//多久去查询一下，是否有任务了
	public static final int TIME_INTERVAL_REQ = 20*1000;
	
	
	//RequestInfo
	public static final String REQ_QURERY = "SELECT t.task_id,o.order_name,t.order_id,t.date_scope,t.file,o.`language` FROM dmp.dmp_task t,dmp.dmp_order o WHERE o.order_id=t.order_id and t.status =1 ";
//	public static final String REQ_QURERY = "SELECT t.task_id,o.order_name,t.order_id,t.date_scope,t.file,o.`language` FROM dmp_task t,dmp_order o WHERE o.order_id=t.order_id and t.task_id =359 ";
	//DetailInfo  分页SQL
	public static final String GET_REQ_QURERY(){
		return REQ_QURERY+"  limit 0,"+NUM_PER_REQ;
	}
	
	//public static final String REQ_UPDATE = "update dmp.dmp_task  set status=0 where task_id='";
	public static final String GET_REQ_UPDATE(String taskId,String fileName){
		return "update dmp.dmp_task  set status=3 ,file='"+fileName+"' where task_id='"+taskId+"'";
	}
	
	public static final String DETAIL_QURERY_ID = 
			"select  his.id"
			+" from dmp.dmp_order o inner join dmp.dmp_order_items i on o.order_id = i.order_id "
			+"  inner join customsdict.dict_product p on i.hs =  p.code and p.`year`=i.`year`   "
			+" inner join customsdb.collect_history his on i.hs =  his.hs and i.`year`=his.`year` and i.`month`=his.`month` and i.direction=if(his.isimp=0,'e','i') ";
	public static final String DETAIL_QURERY_ID(String order,String scope){
			return DETAIL_QURERY_ID+" where o.order_id = '"+order+"' and concat(i.year,'-',i.month) in ("+scope+")  ORDER BY his.hs ";
	}
	
	
	//DetailInfo
	public static final String DETAIL_QURERY_CN = 
			"select concat(i.direction,'cn'),i.hs, p.desp_cn,concat(i.year,'',i.month),his.compid,comp.compName_cn,rea.areaName_cn,his.num,his.usd,his.usd/his.num,country.countryName_cn, "
			+" c.customName_cn,trade.tradeName_cn,tran.trsName_cn,country2.countryName_cn,p.unit1title_cn,comp.tel,comp.fax,comp.zipcode,comp.email,comp.contact_cn, "
			+" comptype.typeName_cn,comp.address_cn,rea2.areaName_cn,i.year "
			+" from dmp.dmp_order o inner join dmp.dmp_order_items i on o.order_id = i.order_id "
			+" inner join customsdict.dict_product p on i.hs =  p.code and p.`year`=i.`year`  "
			+" inner join customsdb.collect_history his on i.hs =  his.hs and i.`year`=his.`year` and i.`month`=his.`month` and i.direction=if(his.isimp=0,'e','i') "
			+" left join customsdict.dict_custom c on his.customid = c.`code`   "
			+" left join customsdict.dict_company comp on his.compid  = comp.code "
			+" left join customsdict.dict_area rea on  his.recarea = rea.code "
			+" left join customsdict.dict_area rea2 on  rea2.code = left(rea.code,2) "
			+" left join  customsdict.dict_country country on  his.salescountry = country.code  "
			+" left join  customsdict.dict_tradetype trade on  his.tradetype = trade.code  "
			+" left join  customsdict.dict_transport tran on  his.transporttype = tran.code  "
			+" left join  customsdict.dict_country country2 on  his.tradecountry = country2.code  "
			+" left join customsdict.dict_comptype comptype on  his.comptype = comptype.code   " ;
			//+" where o.order_id = 'xxx' and concat(i.year,'-',i.month)  in ('','') " ;
	//DetailInfo
	public static final String DETAIL_QURERY_EN = 
			"select concat(i.direction,'en') ,i.hs, p.desp_en,concat(i.year,'',i.month),his.compid,comp.compName_en,rea.areaName_en,his.num,his.usd,his.usd/his.num,country.countryName_en, "
			+" c.customName_en,trade.tradeName_en,tran.trsName_en,country2.countryName_en,p.unit1title_en,comp.tel,comp.fax,comp.zipcode,comp.email,comp.contact_en, "
			+" comptype.typeName_en,comp.address_en,rea2.areaName_en,i.year "
			+" from dmp.dmp_order o inner join dmp.dmp_order_items i on o.order_id = i.order_id "
			+" inner join customsdict.dict_product p on i.hs =  p.code and p.`year`=i.`year`  "
			+" inner join customsdb.collect_history his on i.hs =  his.hs and i.`year`=his.`year` and i.`month`=his.`month` and i.direction=if(his.isimp=0,'e','i') "
			+" left join customsdict.dict_custom c on his.customid = c.`code`  "
			+" left join customsdict.dict_company comp on his.compid  = comp.code "
			+" left join customsdict.dict_area rea on  his.recarea = rea.code "
			+" left join customsdict.dict_area rea2 on  rea2.code = left(rea.code,2) "
			+" left join  customsdict.dict_country country on  his.salescountry = country.code  "
			+" left join  customsdict.dict_tradetype trade on  his.tradetype = trade.code  "
			+" left join  customsdict.dict_transport tran on  his.transporttype = tran.code  "
			+" left join  customsdict.dict_country country2 on  his.tradecountry = country2.code  "
			+" left join customsdict.dict_comptype comptype on  his.comptype = comptype.code   " ;
			//+" where o.order_id = 'xxx' and concat(i.year,'-',i.month)  in ('','') " ;
	//DetailInfo  分页SQL
	public static final String GET_DETAIL_QURERY(String order,String language,String scope,String con){
		if (language.equals("cn")) {
			return DETAIL_QURERY_CN+" where o.order_id = '"+order+"' and concat(i.year,'-',i.month) in ("+scope+")  and his.id in "+con+" ORDER BY i.hs, i.`year`,i.`month`  ";
		}else if(language.equals("en")){
			return DETAIL_QURERY_EN+" where o.order_id = '"+order+"' and concat(i.year,'-',i.month) in ("+scope+")  and his.id in "+con+"  ORDER BY i.hs, i.`year`,i.`month`  ";
		}
		return DETAIL_QURERY_CN+" where o.order_id = '"+order+"' and concat(i.year,'-',i.month) in ("+scope+")   and his.id in "+con+" ORDER BY i.hs, i.`year`,i.`month`  ";
	}
	
	public static final String INDEX_QURERY="SELECT i.hs, i.year,i.`month`,i.direction,idx.totalNum "
			+ "from dmp.dmp_order o inner join dmp.dmp_order_items i on o.order_id = i.order_id "
			+ "INNER JOIN customsdb.collect_index idx on idx.hs = i.hs and idx.`year`=i.`year` and idx.`month`=i.`month` and i.direction=if(idx.isimport=0,'e','i') ";
			
	
	public static final String GET_INDEX_QUERY(String orderid,String language,String scope){
		return INDEX_QURERY + " WHERE o.order_id = '"+orderid+"' and concat(i.year,'-',i.month) in ("+scope+") ORDER BY i.hs, i.`year`,i.`month`";
	}
	
	/**
	 * =======================================================
	 */
	//数据密码是否加密
	public static final boolean ENCODE_DB_PWD = true;
	//配置信息文件
	public static final String CONFIG_FILE="/configInfo.xml" ;
	/**
	 * =======================================================
	 */
	//服务启动的端口
	public static final int LOGSERVER_PORT=1234;
	//远程连接服务的密码，目前简单处理一个字符
	public static final String LOGSERVER_PWD="-";

}
