import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;


public class TgaMain {

	public static void main(String[] args) {
		TgaMain tga = new TgaMain();
		String fileName = "example3.tga";
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

	}

	/**
	 * naugłówek =18 bit
	 * readedBytes[2] <- here is alwas 2(in bytes) why ?
	 * readedBytes[16] = 0x20, why?
	 */
	private BufferedImage decodeTgaFile(int[] readedBytes) {
		System.out.println(" nagówek - bajty: ");
		for (int i = 0; i < 18; i++) {
			System.out.print(" " + readedBytes[i] + "");
		}
		System.out.println("\n");
		
		int x1 = (readedBytes[9] << 8)+ readedBytes[8]; 
		int y1 = (readedBytes[11] << 8)+ readedBytes[10];
		int width = (readedBytes[13] << 8)+ readedBytes[12]; 
		int height = (readedBytes[15] << 8)+ readedBytes[14]; 
		System.out.println("x1 = "+ x1+",  x2="+y1+",  width="+ width+ ",  height = "+ height);
		
		BufferedImage buffy = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		int offset = 18;
		int byte_per_pixel = readedBytes[16]/8;
		System.out.println("byte_per_pixel: "+ byte_per_pixel);
		
		for(int i=0; i<width; i++){
		System.out.print(i+":   ");
			for(int j=0; j<height; j++){
				
				int dex = (j*width + i)*byte_per_pixel+ offset;
				System.out.println("----dex: "+ dex+ ", "+ dex+1+ ", "+ dex+2+ ", "+ dex+3);
				int b = readedBytes[dex];
				int g = readedBytes[dex+1];
				int r = readedBytes[dex+2];
				System.out.println("---*-*--- "+readedBytes[dex]+" "+ readedBytes[dex+1]+" "+readedBytes[dex+2] );
				
				int y = height-j -1;
				System.out.println("-y- "+y);
				buffy.setRGB(i, y, (r<<16)+ (g<<8) + b);
			}
			System.out.print(" \n");
		
		}
		
		
//		int rangePixels = width*height;
//		int[] pixels = new int[rangePixels];
//		int index=0;
//		int nr = 18;
//		while(rangePixels>0){
//			int pixel = readedBytes[nr];
//			System.out.println("Pixel: "+ pixel);
//			if(pixel<128){//if((pixel&0x80)==0)
//				System.out.println("pixe: "+ pixel + " :::: ");
//				for(int i=0; i<=nr; i++){
//					int r = readedBytes[++nr];
//					int g = readedBytes[++nr];
//					int b = readedBytes[++nr];
//					pixels[index++]=0xff000000 | (b<<16)| (g<<8)| r;
//				}
//				
//			}else{
//				System.out.println("jestem w else");
//			}
//			nr++;
//			rangePixels=rangePixels-nr+1;
//		}
		return buffy;
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
