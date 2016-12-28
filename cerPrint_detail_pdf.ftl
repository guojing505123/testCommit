<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>证书打印模板</title>

<style type="text/css">
/*<![CDATA[*/

body {
    font-family: SimSun;
}
table{
	font-size:13px;
	table-layout:fixed; 
	-fs-table-paginate:paginate;
}
.cer_col{padding-left:5px;}

/*]]>*/
</style>
</head>
<body>
<#list zyzsList as obj>
<table  width="98%" style="line-height:21px;margin:5px 10px ${obj.REMARK3} 10px;">
  <tr>
    <td width="19%">&nbsp;</td>
    <td width="29%">&nbsp;</td>
    <td width="5%">&nbsp;</td>
    <td class="cer_col" align="left" width="13%">执业证名称：</td>
    <td width="35%">${obj.ZYZSMC}</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td class="cer_col" align="left">执业证编号：</td>
    <td>${obj.ZYZSHM}</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td class="cer_col" align="left">业务范围：</td>
    <td>不得超出所属公司的经营范围。</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td class="cer_col" align="left">执业区域：</td>
    <td>湖南省</td>
  </tr>
  <tr style="line-height:12px;">
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td colspan="2">
    	<table>
    		<tr>
    			<td align="right">发证日期：</td>
    			<td align="left">
    				<#if obj.createTime?? >
				    	${obj.createTime?string("yyyy/MM/dd")}
				    </#if>
				</td>
			</tr>
    	</table>
    </td>
  </tr>
  <tr style="line-height:14px;">
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td colspan="2">
    	<table>
    		<tr>
    			<td align="right">有效开始日期：</td>
    			<td align="left">
    				<#if obj.KSRQ?? >
				    	${obj.KSRQ?string("yyyy/MM/dd")}
				    </#if>
				</td>
			</tr>
    	</table>
    </td>
  </tr>
  <tr style="line-height:14px;">
  	<td align="right">&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td colspan="2">
    	<table>
    		<tr>
    			<td align="right">有效截止日期：</td>
    			<td align="left">
    				<#if obj.DQRQ?? >
				    	${obj.DQRQ?string("yyyy/MM/dd")}
				    </#if>
				</td>
			</tr>
    	</table>
    </td>
  </tr>
  <tr style="line-height:14px;">
    <td align="right">姓名：</td>
    <td>${obj.XM}</td>
    <td>&nbsp;</td>
    <td colspan="2" rowspan="6" align="center"><img src="./${obj.imageUrl}" width="130" height="130" /></td>
  </tr>
  <tr>
    <td align="right">性别：</td>
    <td>${obj.XB}</td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td align="right">身份证件种类：</td>
    <td>${obj.SFZHZL}</td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td align="right">身份证件号码：</td>
    <td>${obj.ZJHM}</td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td align="right">营销人员编号：</td>
    <td>${obj.CYRYBH}</td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td align="center">&nbsp;所属公司：</td>
    <td style="height:44px;">${obj.GSMC}</td>
    <td>&nbsp;</td>
  </tr>
  <#--注释
  <tr style="font-size:11px;line-height:10px;">
  	 <th rowspan="2" align="right">证书查询网址：</th>
	 <td >http://zyz.yxybb.com/zyz<br/>http://iir.circ.gov.cn</td>
	 <td>&nbsp;</td>
	 <td colspan="2" align="left" class="cer_col">公司投诉电话：${obj.JDDH}<br/>湖南省保险中介行业协会监制&nbsp;联系电话：${obj.XHJDDH}</td>
  </tr>  
  -->
  <tr style="font-size:11px;line-height:14px;">
  	 <th rowspan="2" align="right">证书查询网址：</th>
	 <td rowspan="2">http://yxygl.hnbzx.cn/zyz<br/></td>
	 <td>&nbsp;</td>
	 <td class="cer_col" align="left" colspan="2">公司投诉电话：${obj.JDDH}</td>        
  </tr>              	
  <tr style="font-size:11px;line-height:12px;">
     <td>&nbsp;</td>
     <td colspan="2" align="left" class="cer_col">湖南省保险中介行业协会监制&nbsp;联系电话：0731-88393183</td>         
  </tr>  
</table>
</#list>
</body>
</html>
