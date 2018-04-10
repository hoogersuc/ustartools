package com.dpnet.utils;

import com.dpnet.utils.ImageIOHelper;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;



public class OCR {
	private static final String LANG_OPTION = "-l";
	private static final String EOL = System.getProperty("line.separator");
	private static String tessPath = new File("tesseract").getAbsolutePath();

	public synchronized static String recognizeText(File imageFile, String imageFormat,int ii)
			throws Exception {
		File tempImage = ImageIOHelper.createImage(imageFile, imageFormat);

		File outputFile = new File(imageFile.getParentFile(), "output"+ii);
		StringBuffer strB = new StringBuffer();

		List<String> cmd = new ArrayList<String>();
		cmd.add(tessPath + "\\tesseract");
		cmd.add("");
		cmd.add(outputFile.getName());
		cmd.add(LANG_OPTION);
		cmd.add("chi_sim");


		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(imageFile.getParentFile());

		cmd.set(1, tempImage.getName());
		pb.command(cmd);
		pb.redirectErrorStream(true);
		Process process = pb.start();

		int w = process.waitFor();

		// delete temp working files
		tempImage.delete();

		if (w == 0) {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(outputFile.getAbsolutePath() + ".txt"),
					"UTF-8"));

			String str;

			while ((str = in.readLine()) != null) {
				strB.append(str).append(EOL);
			}
			in.close();
		} else {
			String msg;
			switch (w) {
				case 1:
					msg = "Errors accessing files. There may be spaces in your image's filename.";
					break;
				case 29:
					msg = "Cannot recognize the image or its selected region.";
					break;
				case 31:
					msg = "Unsupported image format.";
					break;
				default:
					msg = "Errors occurred.";
			}
			tempImage.delete();
			throw new RuntimeException(msg);
		}

		new File(outputFile.getAbsolutePath() + ".txt").delete();
		return strB.toString();
	}

	public static void main(String[] args) throws Exception {


		File file = new File("C:/Users/hooge/Nox_share/Image/Screenshot_2018-03-24-00-50-25.png");

		BufferedImage bufImg = ImageIO.read(file);

		ImageFilter imgFliter = new ImageFilter(bufImg);
		BufferedImage greyimg = imgFliter.changeGrey();

		ImageIO.write(greyimg, "png", new File("C:/Users/hooge/Nox_share/Image/test0.png"));


		imgFliter = new ImageFilter(greyimg);
		greyimg = imgFliter.median();

		ImageIO.write(greyimg, "png", new File("C:/Users/hooge/Nox_share/Image/test1.png"));


		imgFliter = new ImageFilter(greyimg);
		greyimg = imgFliter.grayFilter();

		ImageIO.write(greyimg, "png", new File("C:/Users/hooge/Nox_share/Image/test2.png"));


		File imageFile = ImageIOHelper.createImage(0,greyimg);

		String txt = recognizeText(imageFile, "tiff", 0);

		System.out.println(txt);


		imgFliter = new ImageFilter(greyimg);
		greyimg = imgFliter.reverse();

		ImageIO.write(greyimg, "png", new File("C:/Users/hooge/Nox_share/Image/test3.png"));


		imageFile = ImageIOHelper.createImage(0,greyimg);

		txt = recognizeText(imageFile, "tiff", 0);

		System.out.println(txt);


	}
}
