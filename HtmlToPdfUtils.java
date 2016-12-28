package com.jiajie.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.jiajie.entity.Zyzs;
import com.lowagie.text.pdf.BaseFont;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class HtmlToPdfUtils {
	private static String defaultEncoding = "UTF-8";
	
	private static void addFonts(ITextFontResolver fontResolver,String turl) throws Exception {
		// 微软宋体
		fontResolver.addFont(turl+"/simsun.ttc",
				BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//		// 仿宋_GB2312
//		fontResolver.addFont("SIMFANG.TTF",
//				BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//		// 黑体
//		fontResolver.addFont("SIMHEI.TTF",
//				BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//		// 楷体
//		fontResolver.addFont("SIMKAI.TTF",
//				BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//		// 宋体&新宋体
//		fontResolver.addFont("SIMSUN.TTC",
//				BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
	}

	private static void addFont(ITextRenderer renderer,String turl) throws Exception {
		ITextFontResolver fontResolver = renderer.getFontResolver();
		addFonts(fontResolver,turl);
	}

	private ITextRenderer getITextRendererWithFrom(String from, String basePath) {
		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocument(from);
		setBasePath(renderer, basePath);
		return renderer;
	}

	private static ITextRenderer getITextRendererWithContent(String content,
			String basePath) {
		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocumentFromString(content);
		setBasePath(renderer, basePath);
		return renderer;
	}

	private static void setBasePath(ITextRenderer renderer, String basePath) {
		if (!"".equals(basePath) && !basePath.endsWith("/")) {
			basePath += "/";
		}
		renderer.getSharedContext().setBaseURL(basePath);
	}

	public void html2PdfWithFrom(String from, String to, String basePath,String turl)
			throws Exception {
		OutputStream os = new FileOutputStream(to);
		ITextRenderer renderer = getITextRendererWithFrom(from, basePath);
		addFont(renderer,turl);
		renderer.layout();
		renderer.createPDF(os);
		os.close();
	}

	public static void html2PdfWithContent(String content, String to, String basePath,String turl)
			throws Exception {
		OutputStream os = new FileOutputStream(to);
		Tidy tidy = new Tidy();
		tidy.setXHTML(true);
		ByteArrayInputStream bais = new ByteArrayInputStream(content.getBytes());
		InputStreamReader isr = new InputStreamReader(bais);
		BufferedReader br = new BufferedReader(isr);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(baos);
		BufferedWriter bw = new BufferedWriter(osw);
		tidy.parse(br, bw);
		ITextRenderer renderer = getITextRendererWithContent(baos.toString(),
				basePath);
		addFont(renderer,turl);
		renderer.layout();
		renderer.createPDF(os);
		bw.close();
		osw.close();
		baos.close();
		br.close();
		isr.close();
		bais.close();
		os.close();
	}

//	public static void html2PdfWithFreemarker(String from, String to, Map param,
//			String basePath) throws Exception {
//		Configuration cfg = new Configuration();
//		cfg.setClassicCompatible(true);
//		File templateFile = new File(from);
//		String templateDirPath = templateFile.getParent();
//		cfg.setDirectoryForTemplateLoading(new File(templateDirPath));
//		String templateName = templateFile.getName();
//		Template t = cfg.getTemplate(templateName);
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		OutputStreamWriter osw = new OutputStreamWriter(baos);
//		PrintWriter out = new PrintWriter(new BufferedWriter(osw));
//		t.process(param, out);
//		out.flush();
//		String content = baos.toString();
//		System.out.println(content.length());
//		html2PdfWithContent(content, to, basePath);
//		out.close();
//		osw.close();
//		baos.close();
//	}
	
	public static void html2PdfWithFreemarker(String from, String to, Map<String,Object> param,
			String basePath,String turl) throws Exception {
		Configuration cfg = new Configuration();
		cfg.setClassicCompatible(true);
		File templateFile = new File(from);
		String templateDirPath = templateFile.getParent();
		cfg.setDirectoryForTemplateLoading(new File(templateDirPath));
		String templateName = templateFile.getName();
		//解决freemarker模板读取后出现乱码的问题  
		cfg.setDefaultEncoding(defaultEncoding);
		Template t = cfg.getTemplate(templateName);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(baos);
		PrintWriter out = new PrintWriter(new BufferedWriter(osw));
		t.process(param, out);
		out.flush();
		String content = baos.toString();
		html2PdfWithContent(content, to, basePath,turl);
		out.close();
		osw.close();
		baos.close();
	}
	
	public static String getStandardHtml(String oldString) {
		String html = "";
		html = oldString.replaceAll("<o:p[^>]*>", "");
		html = html.replaceAll("</o:p[^>]*>", "");
		html = html.replaceAll("<font[^>]*>", "<font>");
		html = html.replaceAll("<FONT[^>]*>", "<font>");
		html = html.replaceAll("<span[^>]*>", "<span>");
		html = html.replaceAll("<SPAN[^>]*>", "<span>");
		html = html.replaceAll("<p[^>]*>", "<p>");
		html = html.replaceAll("<P[^>]*>", "<p>");
		html = html.replaceAll("<SPAN\\slang=EN-US>", "");
		html = html.replaceAll("<span\\slang=EN-US>", "");
		html = html.replaceAll("src=\"/web/0/1/image/", "src=\"web/0/1/image/");
		return html;
	}

	public void string2File(String content, File distFile) {
		if (!distFile.getParentFile().exists())
			distFile.getParentFile().mkdirs();
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new StringReader(content));
			bw = new BufferedWriter(new FileWriter(distFile));
			char buf[] = new char[1024];
			int len;
			while ((len = br.read(buf)) != -1) {
				bw.write(buf, 0, len);
			}
			bw.flush();
			br.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
//	public static void mstzsdy(String url, String to) {
//		try{
//		HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
//		imageGenerator.loadUrl(url);
//		FileUtils fileUtils = new FileUtils();
//		fileUtils.createDirByString(to);
//		imageGenerator.saveAsImage(to);
//		}catch(Exception e){
//		e.printStackTrace();
//		}
//		}

	public static void main(String[] args) {
//		Pdf_Png(1,"D:/cqh_05659x_43.pdf","d:/test.jpg");
		//mstzsdy("http://tests.hunbys.com:8080/newJyscStudentAction.do?method=ck_chq&zid=0000002981742389423498378","d:/test.png");
//		File file=new File("d:/test.html");
//		System.out.println(file.getName());
	}
}
