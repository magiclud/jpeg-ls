import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class TgaMain {

	private int height;
	private int width;

	public static void main(String[] args) {
		TgaMain tga = new TgaMain();
		String fileName = "example0.tga";
		int[] readedBytes = tga.readBytesFromTgaFile(fileName);
		// printBytes(readedBytes);

		BufferedImage buffy = tga.decodeTgaFile(readedBytes);
		ImagePanel panel = new ImagePanel(buffy);

		JFrame frame = new JFrame("TGA test");
		JScrollPane pane = new JScrollPane(panel);
		frame.add(pane);
		frame.setSize(900, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		/************************ ENTROPY *********************/
		/******************Ktora entropia jest poprawnie liczona? *****************/
		int[] imageBitMap = getImageBitMap(readedBytes); // get bytes without
															// head and foot
		Map<Integer, Integer> frequencyBytes = tga
				.countFrequencyReadedBytes(imageBitMap);
		Map<Integer, Integer> frequencyBytes2 = tga.countFrequencyReadedBytes2(imageBitMap);
		 double imageEntropy = tga.imageEntropy(imageBitMap, frequencyBytes);
		 double imageEntropy2 = tga.imageEntropy(imageBitMap, frequencyBytes2);
		 System.out.println("Entropia: " + imageEntropy+ "  :   "+ imageEntropy2 );
		/************************************************ */

	}

	private static int[] getImageBitMap(int[] readedBytes) {
		int[] imageBitMap = new int[readedBytes.length - 18 - 26];
		int i = 0;
		for (int k = 18; k < readedBytes.length - 26; k++) {
			imageBitMap[i++] = readedBytes[k];
		}
		return imageBitMap;
	}

	private double imageEntropy(int[] readedBytes,
			Map<Integer, Integer> frequencyBytes) {
		System.out.println("liczba wystąpień wszystkich symboli: "
				+ readedBytes.length);

		// Print the content of the hashMap
		Set<Entry<Integer, Integer>> hashSet = frequencyBytes.entrySet();
		for (int i = 0; i < readedBytes.length; i++) {
		}
		double entropia = 0;
		for (Entry entry : hashSet) {
			double waga = (int) entry.getValue() / (double) readedBytes.length;
			entropia += waga * Math.log(1 / waga);
			// System.out.println("bajt = " + entry.getKey() + ", waga (pi)= "
			// + waga);
		}
		return entropia;
	}

	public Map<Integer, Integer> countFrequencyReadedBytes(int[] readedBytes) {
		// Key - byte, Value - liczba wystąpień
		Map<Integer, Integer> byte_frequency = new HashMap<Integer, Integer>();
		// omijamy naglowek i stopke
		for (int i = 18; i < readedBytes.length - 26; i++) {
			// System.out.println("Readed byte: " + readedByte);
			if (!byte_frequency.isEmpty()
					&& byte_frequency.containsKey(readedBytes[i])) {
				byte_frequency.put(readedBytes[i],
						byte_frequency.get(readedBytes[i]) + 1);
			} else {
				byte_frequency.put(readedBytes[i], 1);
			}

		}
		return byte_frequency;
	}

	public Map<Integer, Integer> countFrequencyReadedBytes2(int[] readedBytes) {
		// Key - byte, Value - liczba wystąpień
		Map<Integer, Integer> byte_frequency = new HashMap<Integer, Integer>();
		// omijamy naglowek i stopke
		// for (int i = 18; i < readedBytes.length - 26; i++) {
		// System.out.println("Readed byte: " + readedByte);
		
		for (int i = 0; i < width; i++) {

			for (int j = 0; j < height; j++) {
				int dex = (j * width + i) * 3 + 18;
				if (dex >= readedBytes.length) {
					break;
				}

				int bytePix = readedBytes[dex];
//				int g = readedBytes[dex + 1];
//				int r = readedBytes[dex + 2];
				int y = height - j - 1;
//				int rgbValue = (r << 16) + (g << 8) + b;
//				int red = r << 16;
//				int green = (g << 8);
				 if (!byte_frequency.isEmpty()
				 && byte_frequency.containsKey(bytePix)) {
				 byte_frequency.put(bytePix,
				 byte_frequency.get(bytePix) + 1);
				 } else {
				 byte_frequency.put(bytePix, 1);
				 }
			}
		//	System.out.println("");
		}
		return byte_frequency;
	}

	/**
	 * naugłówek =18 bit readedBytes[2] <- here is alwas 2(in bytes) why ?
	 * readedBytes[16] = 0x20, why?
	 */
	private BufferedImage decodeTgaFile(int[] readedBytes) {
		System.out.println(" nagówek - bajty: ");
		for (int i = 0; i < 18; i++) {
			System.out.print(" " + readedBytes[i] + "");
		}
		System.out.println("\n");

		int x1 = (readedBytes[9] << 8) + readedBytes[8];
		int y1 = (readedBytes[11] << 8) + readedBytes[10];
		this.width = (readedBytes[13] << 8) + readedBytes[12];
		this.height = (readedBytes[15] << 8) + readedBytes[14];
		System.out.println("x1 = " + x1 + ",  x2=" + y1 + ",  width=" + width
				+ ",  height = " + height);

		/********************************************************************/
		BufferedImage buffy = new BufferedImage(width, height,
				BufferedImage.TYPE_3BYTE_BGR);
		buffy = readImage_ZeroEquation(buffy, readedBytes);

		/******************************************************************************************** I ***/
		// buffy = readImage_FirstEquation(buffy, readedBytes);

		/****************************************************************************** wzor II ******/
		// buffy = readImage_SecondEquation(buffy, readedBytes);

		return buffy;
	}

	private BufferedImage readImage_ZeroEquation(BufferedImage buffy,
			int[] readedBytes) {
		BufferedImage buffImg = buffy;
		int offset = 18;
		int byte_per_pixel = readedBytes[16] / 8;

		for (int i = 0; i < buffy.getWidth(); i++) {

			for (int j = 0; j < buffy.getHeight(); j++) {

				int dex = (j * buffy.getWidth() + i) * byte_per_pixel + offset;
				int b = readedBytes[dex];
				int g = readedBytes[dex + 1];
				int r = readedBytes[dex + 2];
				int y = buffy.getHeight() - j - 1;
				buffImg.setRGB(i, y, (r << 16) + (g << 8) + b);
			}
		}
		return buffImg;
	}

	private BufferedImage readImage_FirstEquation(BufferedImage buffy,
			int[] readedBytes) {
		BufferedImage buf = buffy;
		int offset = 18;
		int byte_per_pixel = readedBytes[16] / 8;// = 3, bo 24/8
		System.out.println("readedBytes[16]: " + readedBytes[16]);
		System.out.println("byte_per_pixel: " + byte_per_pixel);
		for (int i = 0; i < buffy.getWidth(); i++) {
			// System.out.print(i+":   ");
			for (int j = 0; j < buffy.getHeight(); j = j + 2) {

				int dex = (j * buffy.getWidth() + i) * byte_per_pixel + offset;
				// System.out.print(" "+ dex);
				int b = readedBytes[dex];
				int g = readedBytes[dex + 1];
				int r = readedBytes[dex + 2];
				// System.out.print("["+b+", "+ g+", "+r+"]" );
				// System.out.println("---*-*--- "+readedBytes[dex]+" "+
				// readedBytes[dex+1]+" "+readedBytes[dex+2] );

				int y = buffy.getHeight() - j - 1;
				// System.out.println("-y- "+y);
				// buffy.setRGB(i, y, (r<<16)+ (g<<8) + b);

				if (j == 0) {
					j = 1;
				}
				int dex2 = ((j - 1) * buffy.getWidth() + i) * byte_per_pixel
						+ offset;
				int b2 = readedBytes[dex2];
				int g2 = readedBytes[dex2 + 1];
				int r2 = readedBytes[dex2 + 2];
				int b2_div = b - b2;
				int g2_div = g - g2;
				int r2_div = r - r2;
				// System.out.print("("+b2+", "+ g2+", "+r2+") " );
				buf.setRGB(i, y, (r2_div << 16) + (g2_div << 8) + b2_div);
				// System.out.print("["+dex+"-"+dex2+"] ");
			}
			// System.out.print(" \n");
		}
		return buf;
	}

	private static void printBytes(byte[] readedBytes) {
		for (int i = 0; i < readedBytes.length; i++) {
			if (i % 18 == 0) {
				System.out.print(" \n ");
			}
			System.out.print(" " + readedBytes[i] + " ");
		}
		System.out.print(" \n Dlugosc pliku: " + readedBytes.length);
		System.out.print(" \n ");
	}

	private int[] readBytesFromTgaFile(String fileName) {
		File file = new File(fileName);
		byte[] byteArray = new byte[(int) file.length()];
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			bis.read(byteArray);
			bis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int[] readedBytes = changeToUnsigned(byteArray);
		return readedBytes;
	}

	private int[] changeToUnsigned(byte[] byteArray) {
		int[] readedBytes = new int[byteArray.length];
		for (int i = 0; i < byteArray.length; i++) {
			if (byteArray[i] < 0) {
				readedBytes[i] = 256 + byteArray[i];
			} else {
				readedBytes[i] = byteArray[i];
			}

		}
		return readedBytes;
	}

}
