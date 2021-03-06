import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class TgaMain {

	private int height;
	private int width;
	private static int[] imageBitMap;
	private Pixel[][] pixelsArray;
	private Pixel[][] pixArrAfterEquation;
	private int[] readedBytes;
	private ArrayList<Double> entropySolutions;
	

	public static void main(String[] args) {
		TgaMain tga = new TgaMain();
		String fileName = "example0.tga";
		int[] readedBytes = tga.readBytesFromTgaFile(fileName);
		tga.setImageBitMap(readedBytes);
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

	private int[] setImageBitMap(int[] readedBytes) {
		imageBitMap = new int[readedBytes.length - 18 - 26];
		int i = 0;
		for (int k = 18; k < readedBytes.length - 26; k++) {
			imageBitMap[i++] = readedBytes[k];
		}
		return imageBitMap;
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

	/**
	 * naugłówek =18 bit readedBytes[2] <- here is alwas 2(in bytes) why ?
	 * readedBytes[16] = 0x20, why?
	 */
	private BufferedImage decodeTgaFile(int[] readedBytes) {
		System.out.println(" nagówek - bajty: "); // printArray(header);
		System.out.println("\n");

		this.width = (readedBytes[13] << 8) + readedBytes[12];
		this.height = (readedBytes[15] << 8) + readedBytes[14];
		pixelsArray = new Pixel[width][height];
		pixArrAfterEquation = new Pixel[width][height];
		System.out.println("width=" + width + ", height = " + height);

		createPixelsArray();

		/********************************************************************/
		BufferedImage buffy = new BufferedImage(width, height,
				BufferedImage.TYPE_3BYTE_BGR);
		// buffy = readImage_ZeroEquation(buffy, readedBytes);
		entropySolutions = new ArrayList<Double>();
		for (int i = 0; i < 9; i++) {
			System.out.println("-------  nr: " + i + "   ----------");
			int nrEquation = i;
			buffy = getBufferedImageUsingEquation(buffy, nrEquation);
			countEntropy( );
			System.out.println("------- ------------------ ----------");
		}
		findTheBestEntropy();
		return buffy;
	}

	private void findTheBestEntropy() {
		double bestEntropyImage =1000;
		double  bestEntRed=1000, bestEntGreen=1000, bestEntBlue=1000;
		int nrPredykBestEntImg=1000;
		int nrPredBestEntRed=0, nrPredBestEntGreen=0, nrPredBestEntBlue=0;
		for(int i=0; i< entropySolutions.size();i=i+4){
			if(entropySolutions.get(i)<bestEntropyImage){
				bestEntropyImage = entropySolutions.get(i);
				nrPredykBestEntImg = i*2/8; //TODO ??!!
			}
			if(entropySolutions.get(i+1)< bestEntRed){
				 bestEntRed = entropySolutions.get(i+1);
				 nrPredBestEntRed = i*2/8;
			}
			if(entropySolutions.get(i+2)< bestEntGreen){
				 bestEntGreen = entropySolutions.get(i+2);
				 nrPredBestEntGreen = i*2/8;
			}
			if(entropySolutions.get(i+3)< bestEntBlue){
				 bestEntBlue = entropySolutions.get(i+3);
				 nrPredBestEntBlue = i*2/8;
			}
		}
		System.out.println("Najlepsza metoda dla całego obrazu: "+nrPredykBestEntImg+ ", wartosć entropii: "+ bestEntropyImage );
		System.out.println("Najlepsza metoda dla red: "+nrPredBestEntRed+ ", wartosć entropii: "+ bestEntRed );
		System.out.println("Najlepsza metoda dla green: "+nrPredBestEntGreen+ ", wartosć entropii: "+ bestEntGreen );
		System.out.println("Najlepsza metoda dla blue: "+nrPredBestEntBlue+ ", wartosć entropii: "+ bestEntBlue );
	}

	private void countEntropy() {// TODO

		int counter = 0;
		Map<Integer, Integer> byte_frequenc = new HashMap<Integer, Integer>();
		Map<Integer, Integer> redFreq = new HashMap<Integer, Integer>();
		Map<Integer, Integer> greenFreq = new HashMap<Integer, Integer>();
		Map<Integer, Integer> blueFreq = new HashMap<Integer, Integer>();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int dex = (j * width + i) * 3 + 18;
				if (dex < imageBitMap.length) {
					Pixel pixel = pixArrAfterEquation[i][j];
					addToArrayFrequency(byte_frequenc, pixel.getBlue());
					addToArrayFrequency(byte_frequenc, pixel.getRed());
					addToArrayFrequency(byte_frequenc, pixel.getGreen());
					addToArrayFrequency(blueFreq, pixel.getBlue());
					addToArrayFrequency(redFreq, pixel.getRed());
					addToArrayFrequency(greenFreq, pixel.getGreen());
					counter++;
				}
			}
		}
		double enropyWhole = entropiaDlaPojedynczychBajtow(counter * 3,
				byte_frequenc);
		double redEntropy = entropiaDlaPojedynczychBajtow(counter, redFreq);
		double blueEntropy = entropiaDlaPojedynczychBajtow(counter, blueFreq);
		double greenEntropy = entropiaDlaPojedynczychBajtow(counter, greenFreq);

		System.out.println("Entropia dla calego pliku: " + enropyWhole);
		System.out.println("Entropia czerwonych: " + redEntropy);
		System.out.println("Entropia niebieskich: " + blueEntropy);
		System.out.println("Entropia zielonych: " + greenEntropy);
		entropySolutions.add(enropyWhole);
		entropySolutions.add(redEntropy);
		entropySolutions.add(greenEntropy);
		entropySolutions.add(blueEntropy);
	}

	public double entropiaDlaPojedynczychBajtow(int length,
			Map<Integer, Integer> czestotliwoscSymbolu) {
		// System.out.println("liczba wystąpień wszystkich symboli: "
		// + wczytaneBajty.length);

		Set<Entry<Integer, Integer>> hashSet = czestotliwoscSymbolu.entrySet();

		double entropia = 0;
		for (Entry entry : hashSet) {
			double waga = (int) entry.getValue() / (double) length;
			entropia += waga * Math.log(1 / waga);
			// System.out.println("bajt = " + entry.getKey() + ", waga (pi)= "
			// + waga);
		}
		return entropia;
	}

	private void addToArrayFrequency(Map<Integer, Integer> byte_frequenc,
			int key) {
		if (!byte_frequenc.isEmpty() && byte_frequenc.containsKey(key)) {
			byte_frequenc.put(key, byte_frequenc.get(key) + 1);
		} else {
			byte_frequenc.put(key, 1);
		}
	}

	private void createPixelsArray() {
		Pixel pixel;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {

				int dex = (j * width + i) * 3 + 18;// 3->byte per pixel, 18->
													// offset
				if (dex < imageBitMap.length) {
					int b = readedBytes[dex];
					int g = readedBytes[dex + 1];
					int r = readedBytes[dex + 2];
					int y = height - j - 1;
					pixel = new Pixel(i, y, b, g, r);
					pixelsArray[i][j] = pixel;
				}
			}
		}
	}

	private BufferedImage getBufferedImageUsingEquation(BufferedImage buffy,
			int nrEquation) {
		BufferedImage bufImg = buffy;
		Pixel north = new Pixel();
		Pixel west = new Pixel();
		Pixel northWest = new Pixel();

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int dex = (j * width + i) * 3 + 18;
				if (dex < imageBitMap.length) {
					if (i == 0) {
						west.setBlue(0);
						west.setGreen(0);
						west.setRed(0);
					} else {
						west.setBlue(pixelsArray[i - 1][j].getBlue());
						west.setGreen(pixelsArray[i - 1][j].getGreen());
						west.setRed(pixelsArray[i - 1][j].getRed());
					}
					if (j == 0) {
						north.setBlue(0);
						north.setGreen(0);
						north.setRed(0);
					} else {
						north.setBlue(pixelsArray[i][j - 1].getBlue());
						north.setGreen(pixelsArray[i][j - 1].getGreen());
						north.setRed(pixelsArray[i][j - 1].getRed());
					}
					if (j == 0 || i == 0) {
						northWest.setBlue(0);
						northWest.setGreen(0);
						northWest.setRed(0);
					} else {
						northWest.setBlue(pixelsArray[i - 1][j - 1].getBlue());
						northWest
								.setGreen(pixelsArray[i - 1][j - 1].getGreen());
						northWest.setRed(pixelsArray[i - 1][j - 1].getRed());
					}
					bufImg = runEquation(i, j, north, west, northWest,
							nrEquation, buffy);
				}
			}
			// System.out.print(" \n");
		}
		return bufImg;
	}

	private BufferedImage runEquation(int i, int j, Pixel north, Pixel west,
			Pixel northWest, int nrEquation, BufferedImage buffy) {

		if (nrEquation == 0) {/* only read */
			Pixel pix = pixelsArray[i][j];
			buffy.setRGB(i, pix.getY(),
					pix.getBlue() + pix.getGreen() + pix.getRed());

			pixArrAfterEquation[i][j] = new Pixel(pix.getRed(), pix.getBlue(),
					pix.getGreen());
			return buffy;

		} else if (nrEquation == 1) {/* I[i-1,j] */
			Pixel pix = pixelsArray[i][j];

			int blue = pix.getBlue() - west.getBlue();
			int green = pix.getGreen() - west.getGreen();
			int red = pix.getRed() - west.getRed();
			buffy.setRGB(i, pix.getY(), blue + green + red);
			pixArrAfterEquation[i][j] = new Pixel(red, blue, green);

			return buffy;
		} else if (nrEquation == 2) {/* I[i,j-1] */
			Pixel pix = pixelsArray[i][j];

			int blue = pix.getBlue() - north.getBlue();
			int green = pix.getGreen() - north.getGreen();
			int red = pix.getRed() - north.getRed();
			buffy.setRGB(i, pix.getY(), blue + green + red);
			pixArrAfterEquation[i][j] = new Pixel(red, blue, green);

			return buffy;
		} else if (nrEquation == 3) {/* I[i-1,j-1] */
			Pixel pix = pixelsArray[i][j];
			int blue = pix.getBlue() - northWest.getBlue();
			int green = pix.getGreen() - northWest.getGreen();
			int red = pix.getRed() - northWest.getRed();
			buffy.setRGB(i, pix.getY(), blue + green + red);
			pixArrAfterEquation[i][j] = new Pixel(red, blue, green);
			return buffy;
		} else if (nrEquation == 4) {/* I[i,j-1]+ I[i-1,j]-I[i-1,j-1]+ */
			Pixel pix = pixelsArray[i][j];
			int blue = pix.getBlue()
					- (north.getBlue() + west.getBlue() - northWest.getBlue());
			int green = pix.getGreen()
					- (north.getGreen() + west.getGreen() - northWest
							.getGreen());
			int red = pix.getRed()
					- (north.getRed() + west.getRed() - northWest.getRed());
			buffy.setRGB(i, pix.getY(), blue + green + red);
			pixArrAfterEquation[i][j] = new Pixel(red, blue, green);
			return buffy;
		} else if (nrEquation == 5) {/* I[i,j-1]+(I[i-1,j]-I[i-1,j-1])/2 */
			Pixel pix = pixelsArray[i][j];
			int blue = pix.getBlue()
					- (north.getBlue() + (west.getBlue() - northWest.getBlue()) / 2);
			int green = pix.getGreen()
					- (north.getGreen() + (west.getGreen() - northWest
							.getGreen()) / 2);
			int red = pix.getRed()
					- (north.getRed() + (west.getRed() - northWest.getRed()) / 2);
			buffy.setRGB(i, pix.getY(), blue + green + red);
			pixArrAfterEquation[i][j] = new Pixel(red, blue, green);
			return buffy;
		} else if (nrEquation == 6) {/* I[i-1,j]+(I[i,j-1]-I[i-1,j-1])/2 */
			Pixel pix = pixelsArray[i][j];
			int blue = pix.getBlue()
					- (west.getBlue() + (north.getBlue() - northWest.getBlue()) / 2);
			int green = pix.getGreen()
					- (west.getGreen() + (north.getGreen() - northWest
							.getGreen()) / 2);
			int red = pix.getRed()
					- (west.getRed() + (north.getRed() - northWest.getRed()) / 2);
			buffy.setRGB(i, pix.getY(), blue + green + red);
			pixArrAfterEquation[i][j] = new Pixel(red, blue, green);
			return buffy;
		} else if (nrEquation == 7) {/* (I[i,j-1]+I[i-1,j])/2 */
			Pixel pix = pixelsArray[i][j];
			int blue = pix.getBlue() - ((north.getBlue() - west.getBlue()) / 2);
			int green = pix.getGreen()
					- ((north.getGreen() - west.getGreen()) / 2);
			int red = pix.getRed() - ((north.getRed() - west.getRed()) / 2);
			buffy.setRGB(i, pix.getY(), blue + green + red);
			pixArrAfterEquation[i][j] = new Pixel(red, blue, green);
			return buffy;
		} else if (nrEquation == 8) {
			/*
			 * if NW >= max(W,N) X' = max=(W,N) else if NW <- min(W,N) X'=
			 * min(W,N) elseX' =W+N-NW
			 */
			Pixel pix = pixelsArray[i][j];
			int blue;
			if (northWest.getBlue() >= Math
					.max(west.getBlue(), north.getBlue())) {
				blue = Math.max(west.getBlue(), north.getBlue());
			} else if (northWest.getBlue() <= Math.min(west.getBlue(),
					north.getBlue())) {
				blue = Math.min(west.getBlue(), north.getBlue());
			} else {
				blue = west.getBlue() + north.getBlue() - northWest.getBlue();
			}
			int red;
			if (northWest.getRed() >= Math.max(west.getRed(), north.getRed())) {
				red = Math.max(west.getRed(), north.getRed());
			} else if (northWest.getRed() <= Math.min(west.getRed(),
					north.getRed())) {
				red = Math.min(west.getRed(), north.getRed());
			} else {
				red = west.getRed() + north.getRed() - northWest.getRed();
			}
			int green;
			if (northWest.getGreen() >= Math.max(west.getGreen(),
					north.getGreen())) {
				green = Math.max(west.getGreen(), north.getGreen());
			} else if (northWest.getGreen() <= Math.min(west.getGreen(),
					north.getGreen())) {
				green = Math.min(west.getGreen(), north.getGreen());
			} else {
				green = west.getGreen() + north.getGreen()
						- northWest.getGreen();
			}
			buffy.setRGB(i, pix.getY(), blue + green + red);
			pixArrAfterEquation[i][j] = new Pixel(red, blue, green);
			return buffy;
		}
		return buffy;

	}

	private void printArray(int[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.print(" " + array[i] + "");
		}
		System.out.println(" ");
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int[] readedBytes = changeToUnsigned(byteArray);
		return readedBytes;
	}

	private int[] changeToUnsigned(byte[] byteArray) {
		this.readedBytes = new int[byteArray.length];
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
