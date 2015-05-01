import java.awt.image.BufferedImage;
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

public class Main {

	public static void main(String[] args) {
		Main main = new Main();
		String nazwaPliku = "example1.tga";
		byte[] wczytaneBajty = main.czestosliwoscBajtowJPG(nazwaPliku);
		for(int i=0; i< wczytaneBajty.length; i++){
			if(i==18){
				System.out.println("\n******************");
			}
			if(i%18 ==0){
				System.out.println("\n\n ");
			}
			System.out.print(" "+i+" ");
		}
		System.out.println("Ilosc wczytanych bajtow: "+ wczytaneBajty.length);
	}

	public byte[] czestosliwoscBajtowJPG(String nazwaPliku) {

		FileInputStream fis;
		byte[] buffer = null;
		try {
			fis = new FileInputStream(new File(nazwaPliku));

			buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buffer;
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

	public void openJpeg() {
		InputStream inputStream;
		try {
			inputStream = new FileInputStream("obrazek1.jpg");
			BufferedImage image = ImageIO.read(inputStream);
			ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg")
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
