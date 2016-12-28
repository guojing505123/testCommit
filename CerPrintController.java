package com.jiajie.controller.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javassist.expr.NewArray;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.jiajie.common.Config;
import com.jiajie.common.ConstantUtils;
import com.jiajie.common.HtmlToPdfUtils;
import com.jiajie.common.PropertiesUtil;
import com.jiajie.common.QRCodeUtils;
import com.jiajie.common.tools.DateUtil;
import com.jiajie.common.util.EntitySetUtil;
import com.jiajie.core.constants.Constants;
import com.jiajie.core.interfaces.MethodLog;
import com.jiajie.core.mapper.JsonMapper;
import com.jiajie.core.orm.Page;
import com.jiajie.core.orm.Search;
import com.jiajie.core.util.ParamMap;
import com.jiajie.entity.SysDictData;
import com.jiajie.entity.SysOrganization;
import com.jiajie.entity.SysUser;
import com.jiajie.entity.Zsdy;
import com.jiajie.entity.Zszx;
import com.jiajie.entity.Zyzs;
import com.jiajie.service.CommonService;
import com.jiajie.service.SysDictDataService;
import com.jiajie.service.SysDictTypeService;
import com.jiajie.service.SysOrganizationService;
import com.jiajie.service.SysUserService;
import com.jiajie.service.ZsdyService;
import com.jiajie.service.ZszxService;
import com.jiajie.service.ZyzsService;
import com.jiajie.shiro.ShiroUser;
import com.jiajie.vo.JsonResult;
import com.sun.star.awt.Size;

@Controller
public class CerPrintController {

	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory
			.getLogger(CerPrintController.class);

	@Autowired
	private ZyzsService zyzsService;

	@Autowired
	private ZsdyService zsdyService;
	
	@Autowired
	private ZszxService zszxService;
	
	@Autowired
	private SysUserService sysUserService;

	@Autowired
	private SysOrganizationService sysOrganizationService;
	
	@Autowired
	private SysDictTypeService sysDictTypeService;
	
	@Autowired
	private SysDictDataService sysDictDataService;
	
	@Autowired
	private CommonService commonService;

	private int nums = 0;

	@RequiresPermissions("cerPrint:view")
	@RequestMapping("/cerPrint")
	@MethodLog(remark = "查询")
	public String cerPrint_list(Page<Zyzs> page, Model model, @RequestParam(value="findSelect",required=false) String findSelect) {
		ParamMap paramMap = new ParamMap(
				((ServletRequestAttributes) RequestContextHolder
						.getRequestAttributes()).getRequest().getParameterMap());
		HttpSession session=((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getSession();
		if (null == page) {
			page = new Page<Zyzs>(Constants.DEFAULT_PAGENO,
					Constants.DEFAULT_PAGESIZE);
		}
		ShiroUser shiroUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal(); 
		if(shiroUser != null){
			paramMap.put("orgFcode", shiroUser.getOrgFocde());
		}
		//默认不加载数据，点击检索加载数据
		if(paramMap.getString("js")!=null){
			//这个不清楚用途，暂注销
//			System.out.println("sssss::::::::::::::::::::::::"+session.getAttribute("zjhms"));
//			if(session.getAttribute("zjhms")!=null ){
//				paramMap.put("params",session.getAttribute("zjhms"));
//				model.addAttribute("zjhms", session.getAttribute("zjhms"));
//			}
			if(paramMap.getString("params")==null){
				paramMap.put("params",paramMap.getString("zjhms"));
			}
			page = this.zyzsService.findByPage(page, paramMap);
		}
		if(page.getTotalItems() == -1){
			page.setTotalItems(0);
		}
		//获取从业人员类型
		model.addAttribute("obj",paramMap);
		model.addAttribute("findSelect", findSelect);
		return "admin/cerManage/cerPrint/list";
	}
	
	
	
	
	@RequiresPermissions("cerPrint:view")
	@RequestMapping("/cerPrintSearch")
	@ResponseBody
	@MethodLog(remark = "查询")
	public String cerPrintSearch(Page<Zyzs> page, Model model) {
		ParamMap paramMap = new ParamMap(
				((ServletRequestAttributes) RequestContextHolder
						.getRequestAttributes()).getRequest().getParameterMap());
		if (null == page) {
			page = new Page<Zyzs>(Constants.DEFAULT_PAGENO,
					Constants.DEFAULT_PAGESIZE);
		}
		//获取从业人员类型
		model.addAttribute("obj",paramMap);
		page = this.zyzsService.findByPage(page, paramMap);
		//page = sysOrganizationService.getZzjgmc(page);
		
		JsonResult json = new JsonResult();
		StringBuffer msg = new StringBuffer();
		try {
			json.setCode("1");
			json.setMsg(msg.append("成功").toString());
		} catch (Exception e) {
			json.setCode("0");
			json.setMsg(msg.append("失败").toString());
		}
		
		return JsonMapper.nonEmptyMapper().toJson(json);
	}

	@RequiresPermissions("cerPrint:add")
	@RequestMapping(value = "/cerPrintCheck")
	public String cerPrintCheck(HttpServletRequest request, Model model) {
		ParamMap paramMap = new ParamMap(((ServletRequestAttributes) RequestContextHolder
						.getRequestAttributes()).getRequest().getParameterMap());
		ShiroUser shiroUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal(); 
		if(shiroUser != null){
			paramMap.put("orgFcode", shiroUser.getOrgFocde());
		}
		HttpSession session=((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getSession();
		session.setAttribute("params", paramMap);
		return "admin/cerManage/cerPrint/cerPrintCheck";
	}
	
	@RequiresPermissions(value={"cerPrint:add","cerPrint:update"},logical=Logical.OR)
	@RequestMapping(value = "/genPdfCerPrint")
	@MethodLog(remark = "生成打印证书")
	public String genPdfCerPrint(HttpServletRequest request,HttpServletResponse response, Model model) {
		//是否打印营销员编号(1:打印)
		String saleNo = request.getParameter("saleNo").trim();
		//是否打印分支机构(1:打印)
		String areaName = request.getParameter("areaName").trim();
		HttpSession session=((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getSession();
		ParamMap paramMap = (ParamMap) session.getAttribute("params");
		ShiroUser shiroUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal(); 
		if(shiroUser != null)
			paramMap.put("orgFcode", shiroUser.getOrgFocde());
		paramMap.put("num1", request.getParameter("num1"));
		paramMap.put("num2", request.getParameter("num2"));
		StringBuilder appendIds = new StringBuilder();
		try {
			String imageDir = PropertiesUtil.getStringByKey("imageURL");
			//二维码生成图片路径
			String imagePath = request.getSession().getServletContext().getRealPath("/")+"/" + imageDir;
			//PDF模板路径
			String from = Config._WEBINFURL + "pdf/template/cerPrint_detail1_pdf.ftl";
			if("1".equals(saleNo)){
				from = Config._WEBINFURL + "pdf/template/cerPrint_detail_pdf.ftl";
			}
			System.out.println("pdf模板路径="+from);
			//PDF存放的路径
			String fileName=ConstantUtils.EXCEL_FILE_NAME+"_"+EntitySetUtil.generateFcode();
			String to = request.getSession().getServletContext().getRealPath("/")+"/" +"upload" + File.separator + fileName+".pdf";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Map<String,Object> param = new HashMap<String,Object>();
			//获取打印数据
			List<Map> zyzsList = zyzsService.getPrintData(paramMap);
			List pdfList = new ArrayList();
			int listSize=zyzsList.size();
			System.out.println("size:::::::::::::::::::::::::"+listSize);
			for(int i = 0; i < listSize; i++){
				Map map=zyzsList.get(i);
				Zyzs zyzs = new Zyzs();
				//批量生成二维码
				String imageUrl = imagePath + File.separator + map.get("zjhm") + ".jpg";
				String QR_filePath = QRCodeUtils.genQRCodeImage(imageUrl, map.get("zjhm")==null?"":map.get("zjhm").toString());
				zyzs.setXM(map.get("xm")==null?"":map.get("xm").toString());//姓名
				zyzs.setXB(commonService.getZdname(map.get("xb")==null?"":map.get("xb").toString()));//性别
				zyzs.setSFZHZL(commonService.getZdname(map.get("sfzhzl")==null?"":map.get("sfzhzl").toString()));//证件类型
				zyzs.setZJHM(map.get("zjhm")==null?"":map.get("zjhm").toString());//身份证件号
				zyzs.setCYRYBH(map.get("cyrybh")==null?"":map.get("cyrybh").toString());//营销人员编号
				String comName="";
				//是否打印分支机构
				if("1".equals(areaName)){
					comName=commonService.getOrgname(map.get("fzjgid")==null?"":map.get("fzjgid").toString());//分支机构名称
				}else{
					comName=commonService.getOrgFname(map.get("fzjgid")==null?"":map.get("fzjgid").toString());//公司名称
				}
				if(comName.length()<13){
					comName += "\r\n";
				}
				zyzs.setGSMC(comName);
				zyzs.setZYZSHM(map.get("zyzshm")==null?"":map.get("zyzshm").toString());//执业证编号
				zyzs.setZYZSMC(commonService.getZdname(map.get("zyzsmc").toString()));//获取执业证名称
				zyzs.setJDDH(map.get("jddh")==null?"":map.get("jddh").toString());//公司投诉电话
				zyzs.setXHJDDH(map.get("xhjddh")==null?"":map.get("xhjddh").toString());//联系电话
				zyzs.setCreateTime(new Date());//发证日期
				if(map.get("ksrq")!=null && !"".equals(map.get("ksrq"))){
					zyzs.setKSRQ(sdf.parse(map.get("ksrq")==null?"":map.get("ksrq").toString()));//有效开始日期
				}
				if(map.get("dqrq")!=null && !"".equals(map.get("dqrq"))){
					zyzs.setDQRQ(sdf.parse(map.get("dqrq")==null?"":map.get("dqrq").toString()));
				}
				if(i==listSize){
					appendIds.append(map.get("fcode"));
				}else{
					appendIds.append(map.get("fcode")).append(",");
				}
				//二维码路径
				zyzs.setImageUrl(imageDir + File.separator + map.get("zjhm") + ".jpg");
				if(i==listSize-1){
					zyzs.setREMARK3("400px");
				}else{
					//zyzs.setREMARK3("590px");
					zyzs.setREMARK3("656px");
				}
				pdfList.add(zyzs);
			}
			System.out.println("sizeddd:::::::::::::::::::::::::"+pdfList.size());
			param.put("zyzsList",pdfList);
			String basePath = "file://" + Config.ROOT_PATH;
			String turl=request.getSession().getServletContext().getRealPath("/")+"/" +"static";
			HtmlToPdfUtils.html2PdfWithFreemarker(from, to, param,basePath,turl);
			model.addAttribute("appendIds", appendIds);
			model.addAttribute("fileName", fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 return "admin/cerManage/cerPrint/genCerPrint";
	}
	
	
	@RequiresPermissions("cerPrint:view")
	@RequestMapping(value = "/cerPrintValid")
	@ResponseBody
	public String cerPrintValid(@RequestParam("ids") String ids,HttpServletRequest request, Model model) {
		String imageDir = PropertiesUtil.getStringByKey("imageURL");
		//二维码生成图片路径
		String imagePath = request.getSession().getServletContext().getRealPath("/")+File.separator+ imageDir;
		JsonResult json = new JsonResult();
		Search search = Search.build(true);
		String[] idsList = ids.split(",");
		search.addFilterIn("fcode", idsList);
		List<Zyzs> zyzsList = zyzsService.findAllZyzs(search);
		//0-表示没有打印，1-表示已打印
		Integer flag = 1;
		try {
			for(Zyzs zyzsBean : zyzsList){
				String imageUrl = imagePath + File.separator + zyzsBean.getZJHM() + ".jpg";
				//判断是否存在，如果存在返回true
				boolean hasExist = com.jiajie.common.FileUtils.isExistFile(imageUrl);
				if(!hasExist){
					flag = 0;
					break;
				}
			}
    		json.setCode("1");
    		json.setParams(flag.toString());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			json.setCode("0");
			json.setParams(flag.toString());
		}
    	return JsonMapper.nonEmptyMapper().toJson(json);
	}


	@RequiresPermissions("cerPrint:view")
	@RequestMapping(value = "/down_cerPrint")
	@ResponseBody
	public String downCerPrint(@RequestParam("fileName") String fileName,HttpServletRequest request,
			HttpServletResponse response, Model model) {
		try {
			StringBuffer msg = new StringBuffer();
			File file = new File(request.getSession().getServletContext().getRealPath("/")+"/" +"upload" + File.separator + fileName+".pdf");
			// 读到流中
			InputStream inStream = new FileInputStream(file);// 文件的存放路径
			// 设置输出的格式
			response.reset();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-msdownload");
			response.addHeader("Content-Disposition", "attachment; filename=\""
					+ new String(fileName.concat(".pdf")
							.getBytes("UTF-8"), "ISO8859-1") + "\"");
			response.setContentLength((int) file.length());
			// 循环取出流中的数据
			byte[] b = new byte[1024];
			int len;
			while ((len = inStream.read(b)) > 0) {
				response.getOutputStream().write(b, 0, len);
			}
			inStream.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return JsonMapper.nonEmptyMapper().toJson("成功");
	}

	@RequiresPermissions(value={"cerPrint:add","cerPrint:update"},logical=Logical.OR)
	@RequestMapping(value = "/cerPrint_reason")
	@MethodLog(remark = "打印证书原因")
	public String cerPrintReason(@RequestParam("ids") String ids, Zsdy entity,
			HttpServletRequest request, Model model) {
		//该标识用来区分："证书打印"和"批量修改打印状态"按钮。
		//0-证书打印 1-批量修改打印状态
		String flag = request.getParameter("flag").trim();
//		if("1".equals(flag)){
//			flag = "1";
//		}else{
//			flag = "0";
//		}
		// 证书打印ID
		model.addAttribute("ids", ids);
		//该标识用来区分："证书打印"和"批量修改打印状态"按钮。
		model.addAttribute("flag", flag);
		return "admin/cerManage/cerPrint/cerPrintReason";
	}
	

	@RequiresPermissions(value={"cerPrint:add","cerPrint:update"},logical=Logical.OR)
	@RequestMapping(value = "/savePrint")
	@ResponseBody
	@MethodLog(remark = "保存打印证书原因")
	public String cerPrintSaveReason(Zsdy entity, Model model) {
		
		Subject sub = SecurityUtils.getSubject();
    	ShiroUser shiroUser = (ShiroUser) sub.getPrincipal();
		JsonResult json = new JsonResult();
		StringBuffer msg = new StringBuffer();
		String[] idsList = entity.getIds().split(",");
		String printReason = entity.getDYYY();
		try {
			Zyzs zyzs = null;
			for (String fcode : idsList) {
				//该标识用来区分："证书打印"和"批量修改打印状态"按钮。
				//0-证书打印 1-批量修改打印状态
//				if(StringUtils.isNotBlank(entity.getFlag())){
					zyzs = this.zyzsService.getZyzsByFcode(fcode);
//					if("1".equals(entity.getFlag())){
						//功能："批量修改打印状态"
						Search search = Search.build(true);
						search.addFilterIn("zyzs.fcode", fcode);
						List<Zsdy> zsdyList = zsdyService.findAllZsdy(search);
						zyzs.setDYZT("YDY");
						if(zsdyList.size()>0){
							Zsdy zsdyBean = new Zsdy();
							// 打印原因
							zsdyBean.setDYYY(printReason);
							// 证书外键
//							Zyzs zyzs = new Zyzs();
//							zyzs.setFcode(fcode);
							zsdyBean.setZyzs(zyzs);
							zsdyBean.setLastModifier(shiroUser.getFcode());
							//批量修改
							zsdyService.updateZsdy(zsdyBean);
							//更新证书打印日期
							updatePrintDate(zyzs,fcode);
						}else{
							//新增
							saveCerPrint(shiroUser,zyzs,printReason);
							//更新证书打印日期
							updatePrintDate(zyzs,fcode);
						}
//					}else{
//						//功能："证书打印"
//						saveCerPrint(shiroUser,zyzs,printReason);
//					}
//				}
			}

			json.setCode("1");
			json.setMsg(msg.append("成功").toString());
			json.setReloadwin("top.contentFrame");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			json.setCode("0");
			json.setMsg(msg.append("失败").toString());
		}
		// redirectAttributes.addFlashAttribute("msg", msg);
		return JsonMapper.nonEmptyMapper().toJson(json);
	}
	
	/**
	 * 保存"证书打印"和"批量修改打印状态"方法
	 */
	private void saveCerPrint(ShiroUser shiroUser,Zyzs zyzs, String printReason){
		Date date = new Date();
		String fcode = shiroUser.getFcode();
		Zsdy zsdyBean = new Zsdy();
		zsdyBean.setCreator(fcode);
		zsdyBean.setCreateTime(date);
		zsdyBean.setLastModifier(fcode);
		zsdyBean.setLastModifiedTime(date);
		zsdyBean.setSysFlag("0");
		// 打印原因
		zsdyBean.setDYYY(printReason);
		zsdyBean.setFcode(EntitySetUtil.generateFcode());
//		// 证书外键
		zsdyBean.setZyzs(zyzs);
		zsdyService.saveOrUpdateZsdy(zsdyBean);
		
		
	}
	
	/**
	 * 更新证书打印日期
	 * @param zyzs
	 * @param fcode
	 */
	public void updatePrintDate(Zyzs zyzs,String fcode){
		/*String printDate = zsdyService.getMaxZsdyDate(fcode);
		if(null != printDate && StringUtils.isNotEmpty(printDate)){
			
		}*/
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			zyzs.setPrintDate(new Date());
			//更新打印日期
			zyzsService.updateZyzs(zyzs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequiresPermissions("cerPrint:view")
	@RequestMapping(value = "/batchSearch")
	@MethodLog(remark = "批量查询")
	public String batchSearch(Zsdy entity, HttpServletRequest request,
			Model model) {
		// 证书打印ID
		return "admin/cerManage/cerPrint/batchSearch";
	}
	
	
	/**
     * 证书详情
     * @param model
     * @param gsdm
     * @return
     */
	@RequiresPermissions("cerPrint:view")
    @RequestMapping("/showDetail")
	public String showDetail(Model model, @RequestParam("fcode") String fcode,HttpServletRequest request){
		if(StringUtils.isNotEmpty(fcode)){
			Zyzs zyzsBean = zyzsService.getZyzsByFcode(fcode);
			String imagePath = PropertiesUtil.getStringByKey("imageURL");
			//二维码生成图片路径
			imagePath = imagePath + File.separator + zyzsBean.getZJHM() + ".jpg";
			zyzsBean.setImageUrl(imagePath);
			Search search = Search.build(true);
			//获取执业证名称
			search.addFilterIn("dicCode", zyzsBean.getZYZSMC());
			SysDictData sysDictData = sysDictDataService.getUniquSysDictDataBySearch(search);
			String zgzsmc = "";
			if(null != sysDictData){
				zgzsmc = sysDictData.getDicName();
			}
			zyzsBean.setCerName(zgzsmc);
			
			//获取区域名称
			search.clear();
			search.addFilterIn("dicCode", zyzsBean.getQY_S());
			SysDictData sysDict = sysDictDataService.getUniquSysDictDataBySearch(search);
			
			String cerQY_S = "";
			if(null != sysDict){
				cerQY_S = sysDict.getDicName();
			}
			zyzsBean.setCerQY_S(cerQY_S);
			search.clear();
			search.addFilterIn("dicCode", zyzsBean.getSFZHZL());
			SysDictData sfzjzlsysDict = sysDictDataService.getUniquSysDictDataBySearch(search);
			
//			search.clear();
//			search.addFilterIn("fcode", zyzsBean.getGSMC());
			String comName = "";
			SysOrganization sysOrganization = sysOrganizationService.getSysOrganizationById(zyzsBean.getFZJGID());
			if(null != sysOrganization){
				if(StringUtils.isNotBlank(sysOrganization.getOrgname())){
					comName = sysOrganization.getOrgname();
				}
			}
			
			model.addAttribute("comName", comName);
			model.addAttribute("obj", zyzsBean);
			model.addAttribute("sfzjzl", sfzjzlsysDict.getDicName());
			search.clear();
			search.addFilterIn("zyzs.fcode", fcode);
			List<Zsdy> zsdyList = zsdyService.findAllZsdy(search);
			List<Zsdy> zsdyVoList = new ArrayList<Zsdy>();
			for(Zsdy entity : zsdyList){
				SysUser sysUser = sysUserService.getSysUserByFCode(entity.getCreator());
				if(null != sysUser && StringUtils.isNotEmpty(sysUser.getUsername())){
					entity.setUsername(sysUser.getUsername());
				}
				zsdyVoList.add(entity);
			}
//			ZsdyVo zsdyVo = new ZsdyVo();
//			zsdyVo.setFCODE(id);
//			List<ZsdyVo> zsdyVoList = zsdyService.findAllZsdyVo(zsdyVo);
			model.addAttribute("result", zsdyVoList);
		}
		return "admin/cerManage/cerPrint/showDetail";
	}
	
	@RequiresPermissions("cerPrint:view")
	@ResponseBody
	@RequestMapping(value={"/ex_upload"},  method = RequestMethod.POST)
	public String ex_upload(MultipartFile dataFile, Model model, HttpServletRequest request, HttpServletResponse response){
		Subject sub = SecurityUtils.getSubject();
    	ShiroUser shiroUser = (ShiroUser) sub.getPrincipal();
    	JsonResult json = new JsonResult();
    	String value = "";
    	
    	String path = request.getSession().getServletContext().getRealPath("/upload");
    	
    	String fileName = ConstantUtils.BATCH_SEARCH_TEMPLATE_NAME+".xls";

    	try {
			FileUtils.copyInputStreamToFile(dataFile.getInputStream(), new File(path, fileName));
			HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(path + File.separator + fileName));
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				HSSFSheet sheet = workbook.getSheetAt(i);
				if(sheet.getRow(0) != null){
					int cells = sheet.getRow(0).getPhysicalNumberOfCells();
					for (int j = 0; j < cells; j++) {
						HSSFCell cell = sheet.getRow(0).getCell((short) j);
						if (cell != null) {
							switch (cell.getCellType()) {
							case HSSFCell.CELL_TYPE_FORMULA:
								break;
							case HSSFCell.CELL_TYPE_NUMERIC:
								value += cell.getNumericCellValue() + ",";
								break;
							case HSSFCell.CELL_TYPE_STRING:
								value += cell.getStringCellValue().trim() + ",";
								break;
							default:
								value += "0";
								break;
								
							}
						}
					}
				}
			}
			String[] val = value.split(",");
			if (!val[0].equals("证件号码")) {
				FileUtils.deleteQuietly(new File(path + File.separator + fileName));
				json.setCode("0");
				json.setMsg("批量查询失败,上传的模板格式不正确,确认之后请重新上传!!!");
				return JsonMapper.nonEmptyMapper().toJson(json);
			}
			Workbook book = Workbook.getWorkbook(new File(path + File.separator + fileName));
			Sheet sheet = book.getSheet(0);
			Cell[] sfzhmCells = sheet.getColumn(0);
			
			if(sfzhmCells != null && sfzhmCells.length > 1){
				for (int i = 1; i < sfzhmCells.length; i++) {
					if(sfzhmCells[i] != null && sfzhmCells[i].getContents() != null && sfzhmCells[i].getContents().trim() != null 
							&& sfzhmCells[i].getContents().trim().length() > 18){
						book.close();
						json.setCode("0");
						json.setMsg("批量查询失败,上传的模板中第1列第" + (i+1) + "行身份证号码长度大于18位,请确认之后,重新录入!!!");
						return JsonMapper.nonEmptyMapper().toJson(json);
					}
					if(sfzhmCells[i] != null && sfzhmCells[i].getContents() != null && sfzhmCells[i].getContents().trim() != null 
							&& sfzhmCells[i].getContents().trim().length() < 18 && sfzhmCells[i].getContents().trim().length() >0){
						book.close();
						json.setCode("0");
						json.setMsg("批量查询失败,上传的模板中第1列第" + (i+1) + "行身份证号码长度小于18位,请确认之后,重新录入!!!");
						return JsonMapper.nonEmptyMapper().toJson(json);
					}
				}
			}
			
			StringBuilder sfzhmStr = new StringBuilder();
			if(sfzhmCells.length > 1){
				for (int i = 1; i < sfzhmCells.length; i++) {
					String sfzh = "";
					if(sfzhmCells[i] != null && sfzhmCells[i].getContents() != null){
						sfzh = sfzhmCells[i].getContents().trim();
						if(i==sfzhmCells.length-1){
							sfzhmStr.append(sfzh);
						}else{
							sfzhmStr.append(sfzh).append(",");
						}
					}
				}
				
				FileUtils.deleteQuietly(new File(path + File.separator + fileName));
//				Search search = Search.build(true);
//				String[] idsList = sfzhmStr.toString().split(",");
//				search.addFilterIn("ZJHM", idsList);
//				List<Zyzs> zyzsList = zyzsService.findAllZyzs(search);
//				model.addAttribute("sfzhmStr", sfzhmStr);
				json.setCode("1");
				json.setMsg("批量查询成功!!!");
				//设置匹配成功的所有身份证
				HttpSession session=((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getSession();
				session.setAttribute("zjhms", sfzhmStr.toString());
				json.setParams(sfzhmStr.toString());
				return JsonMapper.nonEmptyMapper().toJson(json);
			}else{
				FileUtils.deleteQuietly(new File(path + File.separator + fileName));
				json.setCode("0");
				json.setMsg("批量查询失败,上传的模板没有添加需要匹配的数据,请添加之后,重新录入!!!");
				return JsonMapper.nonEmptyMapper().toJson(json);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FileUtils.deleteQuietly(new File(path + File.separator + fileName));
			json.setCode("0");
			json.setMsg("批量查询失败!!!");
			return JsonMapper.nonEmptyMapper().toJson(json);
		}
	}
}


