package com.dpnet.tools.wechat;

import com.dpnet.tools.qq.domain.model.AppBaitiao;
import com.dpnet.tools.qq.domain.model.Qqmember;
import com.dpnet.tools.qq.domain.model.Sinaweibo;
import com.dpnet.tools.qq.domain.repository.AppBaitiaoMapper;
import com.dpnet.tools.qq.domain.repository.QqmemberMapper;
import com.dpnet.tools.qq.domain.repository.SinaweiboMapper;
import com.dpnet.utils.ImageFilter;
import com.dpnet.utils.ImageIOHelper;
import com.dpnet.utils.OCR;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

@RestController
@EnableAutoConfiguration
@Log4j2
@MapperScan(basePackages="com.dpnet.tools.qq.domain.repository")
@RequestMapping("simulate")
public class SimulateOperController {

	@Autowired
	private AppBaitiaoMapper appBaitiaoMapper;

	@Autowired
	private QqmemberMapper qqmemberMapper;

	@Autowired
	private SinaweiboMapper sinaweiboMapper;

	@RequestMapping("near")
	@ResponseBody
	public String  near(@RequestParam("page")int page) throws FileNotFoundException, IOException, InterruptedException {




	Process process =Runtime.getRuntime().exec("ls");
	final BufferedReader read = new BufferedReader(new InputStreamReader(process.getInputStream()));

	new Thread(new Runnable() {

		@Override
		public void run() {

			String line = null;
			try {
				while((line = read.readLine()) != null){
					log.info(line);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}
	}).start();


	BufferedWriter write = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
	//exe(write,"D:");
	exe(write,"cd \"/Applications/Nox App Player.app/Contents/MacOS\"");
		exe(write,"nox_adb shell input tap 250 850");
		write.flush();
		Thread.sleep(5000);
		int times = 5;
		do{
		int y = 200;
		do{
			exe(write,"nox_adb shell input tap 250 "+y);
			y = y+130;
			Thread.sleep(3000);
			exe(write,"nox_adb shell screencap -p /mnt/shared/Image/screen1.png ");
			Thread.sleep(1000);
			int cally = getPixLocation("/Users/hooger/Library/Application Support/Nox App Player/Nox_share/Image/screen1.png");
			exe(write,"nox_adb shell input tap 250 "+cally+" ");
			Thread.sleep(3000);
			exe(write,"nox_adb shell input tap 850 100 ");
			Thread.sleep(3000);
			exe(write,"nox_adb shell input tap 40 100 ");
			Thread.sleep(3000);

		}while(y < 1390);


		exe(write,"nox_adb shell input swipe 250  1220  250 475");
		Thread.sleep(3000);
		times--;
		}while(times > 0);

		return "ok";
	}
	@RequestMapping("addBC")
	@ResponseBody
	public String  addBlackCardFriend(@RequestParam("start") int start, @RequestParam("limit") int limit) throws Exception{

//		ConnectionDB db = ConnectionDB.getTravelDB();

//		for(int start=545;start <= 545;start+=100){
			log.info(start);
			try{

				List rtList = appBaitiaoMapper.selectByRowBounds(new AppBaitiao(),new RowBounds(start,limit));
//			List<Map<String,Object>> rtList = db.excuteQuery("select t.phone from app_baitiao t limit "+start+",20");



			if(rtList==null || rtList.size() <= 0){
				return "empty";
			}
			List<String> codeList = new ArrayList<>();
			for(Object rt : rtList){
				AppBaitiao ab = new AppBaitiao();
				BeanUtils.copyProperties(rt, ab);
				codeList.add(ab.getPhone()+"");
			}
			addfriend(codeList);
			}finally {
				log.info(start);
			}
//		}
		return "ok";
	}

	@RequestMapping("addQQ")
	@ResponseBody
	public String addfriend(@RequestParam("start") int start, @RequestParam("limit") int limit) throws Exception{

//		ConnectionDB db = ConnectionDB.getTravelDB();

//		for(int start=0;;start+=100){
			log.info(start);
			try{
				List rtList = qqmemberMapper.selectByRowBounds(new Qqmember(), new RowBounds(start, limit));
//			List<Map<String,Object>> rtList = db.excuteQuery("select uin from qqmember limit "+start+",100");

			if(rtList==null || rtList.size() <= 0){
				return "empty";
			}
			List<String> codeList = new ArrayList<>();
			for(Object rt : rtList){
				Qqmember qm = new Qqmember();
				BeanUtils.copyProperties(rt, qm);
				codeList.add(qm.getUin()+"");
			}
			addfriend(codeList);
			}finally {
				log.info(start);
			}
			return "ok";
	}


	public static void addfriend(List<String> codeList) throws Exception{


		Process process =Runtime.getRuntime().exec("ls");
		final BufferedReader read = new BufferedReader(new InputStreamReader(process.getInputStream()));

		Thread printThread = new Thread(new Runnable() {

			@Override
			public void run() {

				String line = null;

					try {
						while((line = read.readLine()) != null){
							log.info(line);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw new RuntimeException();
					}



			}
		});
		printThread.start();


		BufferedWriter write = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
		//exe(write,"D:");
		exe(write,"cd \"/Applications/Nox App Player.app/Contents/MacOS\"");
			//exe(write,"nox_adb shell input tap 300 240");
			//write.flush();
			//Thread.sleep(2000);

			int xs = 1;
			int i=0;
			for(String code : codeList){

				log.info(++i);


				exe(write,"nox_adb shell input tap 300 240 ");
				Thread.sleep(3000);
				exe(write,"nox_adb shell input text "+code);
				exe(write,"nox_adb shell input tap 300 240 ");
				Thread.sleep(3000*xs);
				exe(write,"nox_adb shell screencap -p /mnt/shared/Image/screen2.png ");
				Thread.sleep(1000);
				String txt = readimgtext("/Users/hooger/Library/Application Support/Nox App Player/Nox_share/Image/screen2.png");
				if(txt.contains("操 作 过 于 频 繁")){
					throw new RuntimeException("操作过于频繁");
				}
				if(txt.contains("该 用 户 不 在")){

				}else if(txt.contains("添 加 到 通 讯 录")){
					int cally = getPixLocation("/Users/hooger/Library/Application Support/Nox App Player/Nox_share/Image/screen2.png");
					exe(write,"nox_adb shell input tap 250 "+cally+" ");
					Thread.sleep(3000*xs);
					exe(write,"nox_adb shell screencap -p /mnt/shared/Image/screen2.png ");
					Thread.sleep(1000);
					txt = readimgtext("/Users/hooger/Library/Application Support/Nox App Player/Nox_share/Image/screen2.png");
					if(txt.contains("你 需 要 发 送 验 证 申 请")){
						exe(write,"nox_adb shell input tap 850 100 ");
						Thread.sleep(3000*xs);
					}
					exe(write,"nox_adb shell input tap 40 100 ");
					Thread.sleep(2000);
				}


				exe(write,"nox_adb shell input tap 40 100 ");
				long sleepTime = new Random().nextInt(10) * 1000 * xs;
				log.info(String.format("休息[%s]秒", sleepTime/1000));
				Thread.sleep(sleepTime);


			}

			process.destroy();
			read.close();
			write.close();
			printThread.interrupt();

	}



	public static String readimgtext(String imgpath) throws Exception {


		File file = new File(imgpath);

		BufferedImage bufImg = ImageIO.read(file);

		 ImageFilter imgFliter = new ImageFilter(bufImg);
     	 BufferedImage greyimg = imgFliter.changeGrey();

//     	ImageIO.write(greyimg, "png", new File("/Users/hooger/Library/Application Support/Nox App Player/Nox_share/Image/test0.png"));


     	 imgFliter = new ImageFilter(greyimg);
     	greyimg = imgFliter.median();

//     	ImageIO.write(greyimg, "png", new File("/Users/hooger/Library/Application Support/Nox App Player/Nox_share/Image/test1.png"));


     	 imgFliter = new ImageFilter(greyimg);
     	greyimg = imgFliter.grayFilter();

//     	ImageIO.write(greyimg, "png", new File("/Users/hooger/Library/Application Support/Nox App Player/Nox_share/Image/test2.png"));


     	 File imageFile = ImageIOHelper.createImage(0,greyimg);

		String txt = OCR.recognizeText(imageFile, "tiff", 0);

		log.info(txt);


		 imgFliter = new ImageFilter(greyimg);
	     	greyimg = imgFliter.reverse();

	     	ImageIO.write(greyimg, "png", new File("/Users/hooger/Library/Application Support/Nox App Player/Nox_share/Image/test3.png"));


	     	 imageFile = ImageIOHelper.createImage(0,greyimg);

			txt += OCR.recognizeText(imageFile, "tiff", 0);

			log.info(txt);

			return txt;
	}



	private static int getPixLocation(String imgpath) throws FileNotFoundException, IOException{

		BufferedImage img = ImageIO.read(new FileInputStream(new File(imgpath)));
		for(int y = img.getHeight()-1; y>0 ; y--){
			int rgb = img.getRGB(250, y);
			int r = ((rgb&0xff0000)>>16);
			int g = ((rgb&0x00ff00)>>8);
			int b = ((rgb&0xff));
			if(r == 26 && 173 == g && 25==b){
				return y;
			}
		}
		return 0;

	}

	private static ByteArrayOutputStream download(String url) throws Exception {
		ByteArrayOutputStream byteout = new ByteArrayOutputStream();
		InputStream in = null;
		try{


			URLConnection conn =  new URL(url).openConnection();
			conn.setRequestProperty("Cookie", "sessionid=vc9mwbjw2bek2c73b0n5zmfp6z7wofwu; csrftoken=dAkxT6n6DcsjWDdQiFDEWDMSZpxv5uA0; Hm_lvt_4424053ee9f86dc524876ab488113772=1490771605,1490844116,1491468561,1491546870; Hm_lpvt_4424053ee9f86dc524876ab488113772=1491549215");
			in = conn.getInputStream();

			byte[]bs = new byte[1024];
			int len = -1;

			while((len=in.read(bs))!=-1){
				byteout.write(bs, 0, len);
			}

		}catch (Exception e) {

			throw new Exception("下载页面出错,URL=>["+url+"]");
		}finally{
			try{
				if(in!=null)
					in.close();
			}catch (Exception e) {

			}
		}
		return byteout;
	}

	private static void exe(BufferedWriter write, String line) throws IOException{
//		log.info(line);
		write.write(line+" \r\n");
		write.flush();
	}



	@RequestMapping("sendWechat")
	@ResponseBody
	public String sendCircle(int id) throws Exception{


		Process process =Runtime.getRuntime().exec("ls");
		final BufferedReader read = new BufferedReader(new InputStreamReader(process.getInputStream()));

		Thread printThread = new Thread(new Runnable() {

			@Override
			public void run() {

				String line = null;

					try {
						while((line = read.readLine()) != null){
							log.info(line);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw new RuntimeException();
					}



			}
		});
		printThread.start();


		BufferedWriter write = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
		//exe(write,"D:");
		exe(write,"cd \"/Applications/Nox App Player.app/Contents/MacOS\"");


		Sinaweibo weibo = new Sinaweibo();
		weibo.setId(id);

		weibo = sinaweiboMapper.selectOne(weibo);



			String txt = (String) weibo.getWeibotxt();


			String imgs = (String) weibo.getWeiboimg();
			String[]imgpaths = imgs.split(";");
			for(String imgpath : imgpaths){


				imgpath = imgpath.replace("square", "bmiddle");
				String imgname = imgpath.substring(imgpath.lastIndexOf("/")+1, imgpath.lastIndexOf("."));




				BufferedImage bufImag = ImageIO.read(new URL("http:"+imgpath));

				String imgfilePath = "C:/Users/hooge/Pictures/"+imgname+".png";
				File imgfile = new File(imgfilePath);
				ImageIO.write(bufImag, "png", imgfile);

				exe(write, "nox_adb push "+imgfilePath+" /sdcard/Pictures/Screenshots");
				Thread.sleep(1000);
				exe(write, "nox_adb shell am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file:///sdcard/Pictures/Screenshots/"+imgname+".png");
				imgfile.delete();

			}


			exe(write,"nox_adb shell input tap 250 950 ");
			Thread.sleep(1000);
			exe(write,"nox_adb shell input tap 250 770 ");
			Thread.sleep(2000);


			for(int i=0;i<imgpaths.length;i++){

				int x = 190+230*(i%4);
				int y = 180+225 * (i/4);

				exe(write,"nox_adb shell input tap "+x+" "+y);
			}
			exe(write,"nox_adb shell input tap 820 100 ");
			Thread.sleep(5000);
			exe(write,"nox_adb shell input tap 250 180 ");
			Thread.sleep(1000);
			txt = txt.trim().replaceAll("\\n", "\\n");
			exe(write, "nox_adb shell am broadcast -a ADB_INPUT_TEXT --es msg '"+txt+"'");
			Thread.sleep(2000);


			exe(write,"nox_adb shell input tap 820 100 ");

			long sleepTime = 20000;
			log.info(String.format("休息[%s]秒", sleepTime/1000));
			Thread.sleep(sleepTime);

for(String imgpath : imgpaths){


				String imgname = imgpath.substring(imgpath.lastIndexOf("/")+1, imgpath.lastIndexOf("."));

				exe(write, "nox_adb shell rm /sdcard/Pictures/Screenshots/"+imgname+".png");
				Thread.sleep(1000);
				exe(write, "nox_adb shell am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file:///sdcard/Pictures/Screenshots/"+imgname+".png");
				//imgfile.delete();

			}

		Thread.sleep(2000);
		printThread.interrupt();


		return "ok";
	}


	@RequestMapping("addMomo")
	@ResponseBody
	public String sendMomo(int id) throws Exception{


		Process process =Runtime.getRuntime().exec("ls");
		final BufferedReader read = new BufferedReader(new InputStreamReader(process.getInputStream()));

		Thread printThread = new Thread(new Runnable() {

			@Override
			public void run() {

				String line = null;

					try {
						while((line = read.readLine()) != null){
							log.info(line);
						}
					} catch (IOException e) {
						log.error("",e);
						throw new RuntimeException();
					}



			}
		});
		printThread.start();


		BufferedWriter write = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
		//exe(write,"D:");
		exe(write,"cd \"/Applications/Nox App Player.app/Contents/MacOS\"");

		Sinaweibo weibo = new Sinaweibo();
		weibo.setId(id);

		weibo = sinaweiboMapper.selectOne(weibo);



		String txt = (String) weibo.getWeibotxt();


		String imgs = (String) weibo.getWeiboimg();
			String[]imgpaths = imgs.split(";");
//			for(String imgpath : imgpaths){
			for(int i=0;i<6&&i<imgpaths.length; i++){
				String imgpath = imgpaths[i];

				imgpath = imgpath.replace("square", "bmiddle");
				String imgname = imgpath.substring(imgpath.lastIndexOf("/")+1, imgpath.lastIndexOf("."));




				BufferedImage bufImag = ImageIO.read(new URL("http:"+imgpath));

				String imgfilePath = "C:/Users/hooge/Pictures/"+imgname+".png";
				File imgfile = new File(imgfilePath);
				ImageIO.write(bufImag, "png", imgfile);

				exe(write, "nox_adb push "+imgfilePath+" /sdcard/Pictures/Screenshots");
				Thread.sleep(1000);
				exe(write, "nox_adb shell am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file:///sdcard/Pictures/Screenshots/"+imgname+".png");
				imgfile.delete();

			}


			exe(write,"nox_adb shell input tap 850 100 ");
			Thread.sleep(1000);
			exe(write,"nox_adb shell input tap 750 370 ");
			Thread.sleep(2000);


			for(int i=1;i<7&&i<imgpaths.length+1;i++){

				int x = 190+230*(i%4);
				int y = 180+225 * (i/4);

				exe(write,"nox_adb shell input tap "+x+" "+y);
			}
			exe(write,"nox_adb shell input tap 850 1350 ");
			Thread.sleep(5000);


			txt = txt.trim().replaceAll("\\n", "\\n");
			exe(write, "nox_adb shell am broadcast -a ADB_INPUT_TEXT --es msg '"+txt+"'");
			Thread.sleep(5000);


			exe(write,"nox_adb shell input tap 850 100 ");

			long sleepTime = 20000;
			log.info(String.format("休息[%s]秒", sleepTime/1000));
			Thread.sleep(sleepTime);

			for(int i=0;i<6&&i<imgpaths.length; i++){
				String imgpath = imgpaths[i];

				String imgname = imgpath.substring(imgpath.lastIndexOf("/")+1, imgpath.lastIndexOf("."));

				exe(write, "nox_adb shell rm /sdcard/Pictures/Screenshots/"+imgname+".png");
				Thread.sleep(1000);
				exe(write, "nox_adb shell am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file:///sdcard/Pictures/Screenshots/"+imgname+".png");
				//imgfile.delete();

			}

		Thread.sleep(2000);
		printThread.interrupt();

		return "ok";
	}


	public static void main(String[] args) throws Exception {
//		addfriend();
//		addBlackCardFriend();
//		sendCircle(1);
//		sendMomo(1);
	}

}
