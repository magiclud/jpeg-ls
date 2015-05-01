import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TgaMain {

	public static void main(String[] args) {
		TgaMain tga = new TgaMain();
		String fileName = "example1.tga";
		int[] readedBytes = tga.readBytesFromTgaFile(fileName);
		// printBytes(readedBytes);
		decodeTgaFile(readedBytes);

	}

	/**
	 * readedBytes[2] <- here is ? readedBytes[2] <- here is ?
	 */
	private static void decodeTgaFile(int[] readedBytes) {
		System.out.println(" nagówek: ");
		for (int i = 0; i < 18; i++) {
			System.out.print(" " + readedBytes[i] + "");
		}
		System.out.println("\n");
		for (int i = 0; i < 18; i++) {
			int x = readedBytes[i] + (readedBytes[i] << 8);

			System.out.println("i:" + i + "   x=" + x);
		}
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
