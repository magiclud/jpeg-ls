package tgaTest;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.swing.JFrame;
import javax.swing.JScrollPane;


public class Main {

	public static void main(String[] args) {
		Main main = new Main();
		String nazwaPliku = "example1.tga";
		 TgaReader tga = new TgaReader();
		try {
			BufferedImage buffy =  tga.getImage(nazwaPliku);
			  ImagePanel panel = new ImagePanel(buffy);

	            JFrame frame = new JFrame("TGA test");
	            JScrollPane pane = new JScrollPane(panel);
	            frame.add(pane);
	            frame.setSize(900, 600);
	            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	            frame.setVisible(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

////		 byte[] wczytaneBajty = main.czestosliwoscBajtowJPG(nazwaPliku);
//		int[] wczyatneBajty = main.czestosliwoscBajtowJPG(nazwaPliku);
//		//byte[] wczytaneBajty = null;
////		try {
////			wczyatneBajty = main.extractBytes(nazwaPliku);
//
//			for (int i = 0; i < 18; i++) {
//				// if(i==18){
//				// System.out.println("\n******************");
//				// }
//				// if(i%18 ==0){
//				// System.out.println("\n\n ");
//				// }
//				System.out.print(" " + i + " ");
//			}
////		} catch (IOException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////
////		}
//		System.out
//				.println("\nIlosc wczytanych bajtow: " + wczyatneBajty.length);
	}

	public byte[] extractBytes(String ImageName) throws IOException {
		// open image
//		File imgPath = new File(ImageName);
//		BufferedImage bufferedImage = ImageIO.read(imgPath);
//
//		// get DataBufferBytes from Raster
//		WritableRaster raster = bufferedImage.getRaster();
//		DataBufferByte data = (DataBufferByte) raster.getDataBuffer();
//
//		return (data.getData());
		 byte[] imageInByte = null;
	        BufferedImage originalImage = ImageIO.read(new File(
	                "/home/aga/workspace/JPEG/example0.tga"));

	        // convert BufferedImage to byte array
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ImageIO.write(originalImage, "tga", baos);
	        baos.flush();
	        imageInByte = baos.toByteArray();
	        baos.close();
	        return imageInByte;
	}



	public int[] czestosliwoscBajtowJPG(String nazwaPliku) {
File file = new File(nazwaPliku);
		ByteArrayOutputStream byteArrayOutputStream = null;

		try {
			BufferedImage bufferedImage = ImageIO.read(file);
			byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "tga", byteArrayOutputStream);
			byteArrayOutputStream.flush();

		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (byteArrayOutputStream != null)
					byteArrayOutputStream.close();
			} catch (IOException e) {
			}

		}
		int[] wczytaneBajty = zamienUjemneNaDodatnie(byteArrayOutputStream
				.toByteArray());
		return wczytaneBajty;
	}
	private int[] zamienUjemneNaDodatnie(byte[] byteArray) {
		int[] wczytaneBajty = new int[byteArray.length];
		for (int i = 0; i < byteArray.length; i++) {
			if (byteArray[i] < 0) {
				wczytaneBajty[i] = 256 + byteArray[i];
			} else {
				wczytaneBajty[i] = byteArray[i];
			}

		}
		return wczytaneBajty;
	}

	public void openJpeg(String nazwaPliku) {
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(new File(nazwaPliku));
			BufferedImage image = ImageIO.read(inputStream);
			ImageWriter writer = ImageIO.getImageWritersByFormatName("tga")
					.next();
			ImageWriteParam param = writer.getDefaultWriteParam();
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(1);
			writer.setOutput(ImageIO.createImageOutputStream(new File(
					"obraz.JPG")));
			writer.write(null, new IIOImage(image, null, null), param);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
