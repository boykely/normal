package java_openCV;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.ws.Holder;

import org.ejml.data.DenseMatrix64F;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Main implements ChangeListener
{	public static enum GradientType
	{
		R,G,B,RGB,Grey
	}
	public static int lastX=0;
	public static int lastY=0;
	public Mat source;
	public Mat target;
	public JLabel targetLabel;
	public byte valueR=60;
	public byte valueG=60;
	public byte valueB=60;
	public static void main(String[]args)
	{
		//CREER TESTBORDS NEW
		//calculsBoards();
		//assambleTiles();
		//getNormalMap();
		getNormalMapNew();
		//addImage();
	}
	public static void addImage()
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		try
		{
			
			String dir="C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\optimize7\\";//"C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\";
			Mat image=Imgcodecs.imread(dir+"spec.jpg");
			Mat image2=Imgcodecs.imread(dir+"diffuse.jpg");
			Mat res=new Mat(image.rows(),image.cols(),CvType.CV_8UC3);
			for(int i=0;i<image.rows();i++)
			{
				for(int j=0;j<image.cols();j++)
				{
					byte[] i1=new byte[3];
					byte[] i2=new byte[3];
					image.get(i, j, i1);
					image2.get(i, j,i2);
					int r=byteColorCVtoIntJava(i1[2])+byteColorCVtoIntJava(i2[2]);r=r>256?255:r;
					int g=byteColorCVtoIntJava(i1[1])+byteColorCVtoIntJava(i2[1]);g=g>256?255:g;
					int b=byteColorCVtoIntJava(i1[0])+byteColorCVtoIntJava(i2[0]);b=b>256?255:b;
					res.put(i, j, new byte[]{
						(byte)b,
						(byte)g,
						(byte)r
					});
				}
			}
			Core.multiply(image, image2, res);
			saveImage(convertCVtoJava(res),dir+"total.jpg");
			System.out.println("fin");
		}
		catch(Exception e)
		{
			System.err.println("Erreur general:"+e.getMessage());
		}
	}
	public static void getNormalMapNew()
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		//source => Mesostructure from specularity
				try 
				{
					String dir="C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\Final\\source_tile\\";
					Mat m=Imgcodecs.imread(dir+"new_step2.jpg");
					Mat mX=new Mat();
					Mat mY=new Mat();
					Imgproc.Sobel(m, mX, m.depth(), 1, 0);
					Imgproc.Sobel(m, mY, m.depth(), 0, 1);
					Mat normal=new Mat(m.rows(),m.cols(),CvType.CV_8UC3);
					byte[] p=new byte[3];
					byte[] q=new byte[3];
					for(int i=0;i<m.rows();i++)
					{
						for(int j=0;j<m.cols();j++)
						{
							mX.get(i, j,p);
							mY.get(i, j,q);
							int mXndg=(byteColorCVtoIntJava(p[0])+byteColorCVtoIntJava(p[1])+byteColorCVtoIntJava(p[2]))/3;
							int mYndg=(byteColorCVtoIntJava(q[0])+byteColorCVtoIntJava(q[1])+byteColorCVtoIntJava(q[2]))/3;
							normal.put(i, j, new byte[]{
								(byte)255,
								(byte)mYndg,
								(byte)mXndg
							});
						}
					}
					saveImage(cvToJava(normal), dir+"normal20o.jpg");
					System.out.println("end");
				}
				catch(Exception e)
				{
					System.out.println("erreur catché");
				}
	}
	public static void getNormalMap()
	{
		//http://www.gamedev.net/topic/586973-how-to-generate-normal-map-from-texture/
		//http://stackoverflow.com/questions/2368728/can-normal-maps-be-generated-from-a-texture
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		try
		{
			
			String dir="C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\Final\\source_tile\\";//"C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\";
			Mat image=Imgcodecs.imread(dir+"renderDiff 20e.jpg");
			Mat gray=new Mat();
			Imgproc.cvtColor(image, gray,Imgproc.COLOR_BGR2GRAY );
			System.out.println(gray);
			Mat X=new Mat();//suivant la ligne
			Mat Y=new Mat();//suivant la colonne
			Imgproc.Sobel(gray, X, image.depth(), 1, 0);
			Imgproc.Sobel(gray, Y, image.depth(), 0, 1);
			System.out.println("start");
			for(int i=0;i<image.rows();i++)
			{
				for(int j=0;j<image.cols();j++)
				{
					byte[] pX=new byte[3];
					byte[] pY=new byte[3];
					X.get(i, j,pX);
					Y.get(i, j,pY);
					double[] x=new double[]{
							1,
							0,
							byteColorCVtoIntJava(pX[0])
					};
					double[] y=new double[]{
							0,
							1,
							byteColorCVtoIntJava(pY[0])
					};
					double[] n=new double[]{
							x[1]*y[2]-x[2]*y[1],
							x[2]*y[0]-x[0]*y[2],
							x[0]*y[1]-x[1]*y[0]
							
							//byteColorCVtoIntJava(pX[0]),
							//byteColorCVtoIntJava(pY[0]),
							//1/2
					};
					double somme=Math.sqrt(n[0]*n[0]+n[1]*n[1]+n[2]*n[2]);
					//System.out.println(somme);
					double[] normal=new double[3];
					if(somme!=0)
					{
						normal=new double[]{
								n[0]/somme,
								n[1]/somme,
								n[2]/somme
						};
					}
					//System.out.println(normal[0]+"//"+normal[1]+"//"+normal[2]);
					/*double r=((normal[0]+1)/2)*255;r=r>256?255:r;
					double g=((normal[1]+1)/2)*255;g=g>256?255:g;
					double b=((normal[2]+1)/2)*255;b=b>256?255:b;*/
					/*double r=(normal[0]+1)*(255/2);r=r>256?255:r;
					double g=(normal[1]+1)*(255/2);g=g>256?255:g;
					double b=(normal[2]+1)*(255/2);b=b>256?255:b;*/
					double r=((normal[0]+1)/2)*255;r=r>256?255:r;
					double g=((normal[1]+1)/2)*255;g=g>256?255:g;
					double b=((normal[2]+1)/2)*255;b=b>256?255:b;
					image.put(i, j, new byte[]{(byte)b,(byte)g,(byte)r});
				}
			}
			System.out.println("save");
			Mat blur=new Mat();
			Imgproc.GaussianBlur(image, blur, new Size(15,15), 4);
			Mat dest=new Mat();
			//Core.subtract(blur, image, dest);//ressemble l'anisotropie
			Core.add(blur, image, dest);
			Imgproc.GaussianBlur(dest, dest, new Size(9,9), 2);
			saveImage(convertCVtoJava(dest), dir+"normal.jpg");
			System.out.println("fin");
		}
		catch(Exception e)
		{
			System.err.println("Erreur general:"+e.getMessage());
		}
	}
	public static void getNormalMapFromParam()
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		try
		{
			
			String dir="C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\optimize5\\";//"C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\";
			String path="source_tile_relit_";
			//BufferedImage[][] tiles=new BufferedImage[12][16];
			int tileWNumber=17;
			int tileHNumber=12;
			int rows=tileHNumber*192;
			int cols=tileWNumber*192;
			int brdfEstimation=tileWNumber*tileHNumber;
			
			
			int originS=0;
			int originT=0;
			Mat image=new Mat(rows,cols,CvType.CV_8UC3);
			System.out.println("Start!!");
			for(int i=0;i<tileHNumber;i++)
			{
				for(int j=0;j<tileWNumber;j++)
				{
					FileReader fileB=new FileReader("C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\optimize5\\testB.txt");
					FileReader fileG=new FileReader("C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\optimize5\\testG.txt");
					FileReader fileR=new FileReader("C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\optimize5\\testR.txt");
					BufferedReader readerB=new BufferedReader(fileB);
					BufferedReader readerG=new BufferedReader(fileG);
					BufferedReader readerR=new BufferedReader(fileR);
					String lb=readerB.readLine();
					String lg=readerG.readLine();
					String lr=readerR.readLine();
					for(int s=0;s<192;s++)
					{
						for(int t=0;t<192;t++)
						{
							String[] brdfParamB=lb.split("//");
							String[] brdfParamG=lg.split("//");
							String[] brdfParamR=lr.split("//");
							originS=s+192*i;
							originT=t+192*j;
							double Bx=Double.parseDouble(brdfParamB[5]);double By=Double.parseDouble(brdfParamB[6]);
							double Gx=Double.parseDouble(brdfParamG[5]);double Gy=Double.parseDouble(brdfParamG[6]);
							double Rx=Double.parseDouble(brdfParamR[5]);double Ry=Double.parseDouble(brdfParamR[6]);
							double B=Math.atan(Bx/By)*180/Math.PI;
							double G=Math.atan(Gx/Gy)*180/Math.PI;
							double R=Math.atan(Rx/Ry)*180/Math.PI;
							//System.out.println(B+"//"+G+"//"+R);
							byte[] p=new byte[]{(byte)B,(byte)G,(byte)R};
							image.put(originS, originT, p);
							lb=readerB.readLine();
							lg=readerG.readLine();
							lr=readerR.readLine();
						}
					}
				}
			}
			System.out.println("save");
			saveImage(convertCVtoJava(image), dir+"normal.jpg");
			System.out.println("fin");
		}
		catch(Exception e)
		{
			System.err.println("Erreur general:"+e.getMessage());
		}
	}
	public static void assambleTiles()
	{
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		try
		{
			String dir="C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\TT\\";//"C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\branche_step1\\bords\\";//"C:\\Users\\ralambomahay1\\Downloads\\stage\\twoshot_data_input\\book_leather_red\\";//"C:\\Users\\ralambomahay1\\Downloads\\stage\\twoshot_data_input\\book_leather_red\\test step2_1204\\";//;//;
			String path="source_tile_";
			//BufferedImage[][] tiles=new BufferedImage[12][16];
			int tileWNumber=17;
			int tileHNumber=12;
			int brdfEstimation=tileWNumber*tileHNumber;
			BufferedImage[][] tiles=new BufferedImage[tileHNumber][tileWNumber];
			Mat[][] tilesCV=new Mat[tileHNumber][tileWNumber];
				
			Color color;
			int c,b,g,r;			
			
			List<double[]> blueData=new ArrayList<>();
			List<double[]> greenData=new ArrayList<>();
			List<double[]> redData=new ArrayList<>();	
			HashMap<Integer, int[]> p=new HashMap<>();
			double[][] posData=new double[192*192][];//it is used only for test			
			List<DenseMatrix64F> posDataMatrix=new ArrayList<>();//it contains 192*192 DenseMatrix64F. Each item refers to position array in world coordinates
			List<DenseMatrix64F> paramBList=new ArrayList<>();//it contains all parameters for each pixel
			List<DenseMatrix64F> paramGList=new ArrayList<>();//it contains all parameters for each pixel
			List<DenseMatrix64F> paramRList=new ArrayList<>();//it contains all parameters for each pixel
			
			int colonne=192;
			int ligne=192;
			int pI=0;
			List<DenseMatrix64F> blueMData=new ArrayList<>();			
			List<DenseMatrix64F> greenMData=new ArrayList<>();
			List<DenseMatrix64F> redMData=new ArrayList<>();
			List<DenseMatrix64F> blueMDataAdjusted=new ArrayList<>();
			List<DenseMatrix64F> greenMDataAdjusted=new ArrayList<>();
			List<DenseMatrix64F> redMDataAdjusted=new ArrayList<>();
			
			System.out.println("start intialisation des données...");
			Mat tempMat;
			BufferedImage tempBuffer;
			for(int fileI=0;fileI<tileHNumber;fileI++)
			{							
				for(int fileJ=0;fileJ<tileWNumber;fileJ++)
				{									
					tempBuffer=ImageIO.read(new File(dir+path+fileI+"_"+fileJ+".jpg"));
					
					//on va lisser chaque tiles avant d'appliquer le svbrdf optimisation
					tempMat=convertTileToCV(tempBuffer);
					//Imgproc.GaussianBlur(tempMat, tempMat, new Size(3,3), 4);
					//tilesCV[fileI][fileJ]=tempMat.clone();
					//tempBuffer=convertCVtoJava(tempMat);
					tiles[fileI][fileJ]=tempBuffer;
				}						
			}
			System.out.println("Fin bluring files");
			
			for(int i=0;i<192;i++)
			{
				for(int j=0;j<192;j++)
				{
					double[] blue=new double[brdfEstimation];
					double[] green=new double[brdfEstimation];
					double[] red=new double[brdfEstimation];
					double[] temp=new double[brdfEstimation];
					DenseMatrix64F tempPos=new DenseMatrix64F(brdfEstimation,1);
					DenseMatrix64F tempB=new DenseMatrix64F(brdfEstimation,1);
					DenseMatrix64F tempG=new DenseMatrix64F(brdfEstimation,1);
					DenseMatrix64F tempR=new DenseMatrix64F(brdfEstimation,1);
					int iter=0;
					for(int tileI=0;tileI<tileHNumber;tileI++)
					{						
						for(int tileJ=0;tileJ<tileWNumber;tileJ++)
						{
							c=tiles[tileI][tileJ].getRGB(j, i);
							color=new Color(c);
							b=color.getBlue();
							g=color.getGreen();
							r=color.getRed();
							blue[iter]=b;
							green[iter]=g;
							red[iter]=r;
							//stocke position
							String pp=((tileI*ligne)+i)+"."+((tileJ*colonne)+j+"1");
							temp[iter]=Double.parseDouble(pp);
							iter++;			
						}
					}
					posData[pI]=temp;
					tempPos.data=temp;
					posDataMatrix.add(tempPos);
					pI++;
					blueData.add(blue);
					greenData.add(green);
					redData.add(red);
					tempB.data=blue;					
					tempG.data=green;
					tempR.data=red;
					blueMData.add(tempB);
					greenMData.add(tempG);
					redMData.add(tempR);					
				}
			}
			
			
			 //On peut faire la reconstruction dans cette partie ou optimisation
			BufferedImage image=new BufferedImage(tileWNumber*192,tileHNumber*192,BufferedImage.TYPE_3BYTE_BGR);
			int[] position;
			DenseMatrix64F temp;
			DenseMatrix64F tempR;
			DenseMatrix64F tempG;
			DenseMatrix64F tempB;
			
			for(int i=0;i<192*192;i++)
			{
				temp=posDataMatrix.get(i);				
				tempR=redMData.get(i);				
				tempG=greenMData.get(i);
				tempB=blueMData.get(i);
				for(int j=0;j<brdfEstimation;j++)
				{
					position=decodePosition(temp.get(j,0));
					r=(tempR.get(j, 0)>256)?255:( tempR.get(j,0)<0?0: (int)tempR.get(j,0));
					g=(tempG.get(j, 0)>256)?255:( tempG.get(j,0)<0?0: (int)tempG.get(j,0));
					b=(tempB.get(j, 0)>256)?255:( tempB.get(j,0)<0?0: (int)tempB.get(j,0));
					color=new Color(r,g,b);					
					image.setRGB(position[1],position[0], color.getRGB());
				}
			}
			System.out.println("Debut enregistremen");
			saveImage(image, "C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\TF20n.jpg");			
			
			System.out.println("bord traité");
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	public static void calculsBoards()
	{
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		try
		{
			String dir="C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\TT\\";//"C:\\Users\\ralambomahay1\\Downloads\\stage\\twoshot_data_input\\book_leather_red\\";//"C:\\Users\\ralambomahay1\\Downloads\\stage\\twoshot_data_input\\book_leather_red\\test step2_1204\\";//;//;
			String path="source_tile_relit_";
			//BufferedImage[][] tiles=new BufferedImage[12][16];
			int tileWNumber=17;
			int tileHNumber=12;
			int brdfEstimation=tileWNumber*tileHNumber;
			BufferedImage[][] tiles=new BufferedImage[tileHNumber][tileWNumber];
			Mat[][] tilesCV=new Mat[tileHNumber][tileWNumber];
				
			Color color;
			int c,b,g,r;			
			
			List<double[]> blueData=new ArrayList<>();
			List<double[]> greenData=new ArrayList<>();
			List<double[]> redData=new ArrayList<>();	
			HashMap<Integer, int[]> p=new HashMap<>();
			double[][] posData=new double[192*192][];//it is used only for test			
			List<DenseMatrix64F> posDataMatrix=new ArrayList<>();//it contains 192*192 DenseMatrix64F. Each item refers to position array in world coordinates
			List<DenseMatrix64F> paramBList=new ArrayList<>();//it contains all parameters for each pixel
			List<DenseMatrix64F> paramGList=new ArrayList<>();//it contains all parameters for each pixel
			List<DenseMatrix64F> paramRList=new ArrayList<>();//it contains all parameters for each pixel
			
			int colonne=192;
			int ligne=192;
			int pI=0;
			List<DenseMatrix64F> blueMData=new ArrayList<>();			
			List<DenseMatrix64F> greenMData=new ArrayList<>();
			List<DenseMatrix64F> redMData=new ArrayList<>();
			List<DenseMatrix64F> blueMDataAdjusted=new ArrayList<>();
			List<DenseMatrix64F> greenMDataAdjusted=new ArrayList<>();
			List<DenseMatrix64F> redMDataAdjusted=new ArrayList<>();
			
			System.out.println("start intialisation des données...");
			Mat tempMat;
			BufferedImage tempBuffer;
			for(int fileI=0;fileI<tileHNumber;fileI++)
			{							
				for(int fileJ=0;fileJ<tileWNumber;fileJ++)
				{									
					tempBuffer=ImageIO.read(new File(dir+path+fileI+"_"+fileJ+".jpg"));
					
					//on va lisser chaque tiles avant d'appliquer le svbrdf optimisation
					tempMat=convertTileToCV(tempBuffer);
					Imgproc.GaussianBlur(tempMat, tempMat, new Size(3,3), 4);
					tilesCV[fileI][fileJ]=tempMat.clone();
					tempBuffer=convertCVtoJava(tempMat);
					tiles[fileI][fileJ]=tempBuffer;
				}						
			}
			System.out.println("Fin bluring files");
			
			for(int i=0;i<192;i++)
			{
				for(int j=0;j<192;j++)
				{
					double[] blue=new double[brdfEstimation];
					double[] green=new double[brdfEstimation];
					double[] red=new double[brdfEstimation];
					double[] temp=new double[brdfEstimation];
					DenseMatrix64F tempPos=new DenseMatrix64F(brdfEstimation,1);
					DenseMatrix64F tempB=new DenseMatrix64F(brdfEstimation,1);
					DenseMatrix64F tempG=new DenseMatrix64F(brdfEstimation,1);
					DenseMatrix64F tempR=new DenseMatrix64F(brdfEstimation,1);
					int iter=0;
					for(int tileI=0;tileI<tileHNumber;tileI++)
					{						
						for(int tileJ=0;tileJ<tileWNumber;tileJ++)
						{
							c=tiles[tileI][tileJ].getRGB(j, i);
							color=new Color(c);
							b=color.getBlue();
							g=color.getGreen();
							r=color.getRed();
							blue[iter]=b;
							green[iter]=g;
							red[iter]=r;
							//stocke position
							String pp=((tileI*ligne)+i)+"."+((tileJ*colonne)+j+"1");
							temp[iter]=Double.parseDouble(pp);
							iter++;			
						}
					}
					posData[pI]=temp;
					tempPos.data=temp;
					posDataMatrix.add(tempPos);
					pI++;
					blueData.add(blue);
					greenData.add(green);
					redData.add(red);
					tempB.data=blue;					
					tempG.data=green;
					tempR.data=red;
					blueMData.add(tempB);
					greenMData.add(tempG);
					redMData.add(tempR);					
				}
			}
			
			
			 //On peut faire la reconstruction dans cette partie ou optimisation
			BufferedImage image=new BufferedImage(tileWNumber*192,tileHNumber*192,BufferedImage.TYPE_3BYTE_BGR);
			int[] position;
			DenseMatrix64F temp;
			DenseMatrix64F tempR;
			DenseMatrix64F tempG;
			DenseMatrix64F tempB;
			
			for(int i=0;i<192*192;i++)
			{
				temp=posDataMatrix.get(i);				
				tempR=redMData.get(i);				
				tempG=greenMData.get(i);
				tempB=blueMData.get(i);
				for(int j=0;j<brdfEstimation;j++)
				{
					position=decodePosition(temp.get(j,0));
					r=(tempR.get(j, 0)>256)?255:( tempR.get(j,0)<0?0: (int)tempR.get(j,0));
					g=(tempG.get(j, 0)>256)?255:( tempG.get(j,0)<0?0: (int)tempG.get(j,0));
					b=(tempB.get(j, 0)>256)?255:( tempB.get(j,0)<0?0: (int)tempB.get(j,0));
					color=new Color(r,g,b);					
					image.setRGB(position[1],position[0], color.getRGB());
				}
			}
			System.out.println("Debut enregistremen");
			saveImage(image, "C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\branche_step1\\relitTilesSmooth.jpg");			
			/*
			 * On va filtrer les bords de chaque tiles
			 * Appliquer le filtre localement
			 */
			int originS=0;
			int originT=0;
			
			Mat origin=Imgcodecs.imread("C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\branche_step1\\relitTilesSmooth.jpg");
			Mat result=new Mat(origin.rows(),origin.cols(),CvType.CV_8UC3);
			int len=5;
			int size=len*len;
			int[][] noyau=kernel(len);
			int[] ligneVar=new int[size];
			int[] colVar=new int[size];
			for(int i=0;i<tileHNumber;i++)
			{
				for(int j=0;j<tileWNumber;j++)
				{
					Mat tempTile=tilesCV[i][j].clone();
					byte[] pixel=new byte[3];
					byte[] pf=new byte[3];					
					for(int s=0;s<192;s++)
					{
						for(int t=0;t<192;t++)
						{
							originT=t+192*j;
							originS=s+192*i;
							origin.get(originS, originT,pixel);
							result.put(originS,originT,pixel);
							
							if(i==0)
							{
								//VERTICAL overlap region
								if(t==191)
								{
									//bord right
									double red=0,green=0,blue=0;
									for(int z=0;z<noyau.length;z++)
									{
										int[] coord=noyau[z];
										ligneVar[z]=originS+coord[0];
										colVar[z]=originT+coord[1];
										//on test chaque coordonnées maintenant
										if(coord[0]<=0)
										{
											//on est dans la partie <0 du noyau
											if(ligneVar[z]<=0)
											{
												ligneVar[z]=originS;
												colVar[z]=originT;
											}
										}
										else
										{
											if(ligneVar[z]>=origin.rows())
											{
												ligneVar[z]=originS;
												colVar[z]=originT;
											}
										}
										if(coord[1]<=0)
										{
											if(colVar[z]<=0)
											{
												ligneVar[z]=originS;
												colVar[z]=originT;
											}
										}
										else
										{
											if(colVar[z]>=origin.cols())
											{
												ligneVar[z]=originS;
												colVar[z]=originT;
											}
										}
										origin.get(ligneVar[z], colVar[z],pf);
										//result.put(ligneVar[z], colVar[z],pixel);
										red+=byteColorCVtoIntJava(pf[2]);
										green+=byteColorCVtoIntJava(pf[1]);
										blue+=byteColorCVtoIntJava(pf[0]);
										
									}
									red/=size;red=red<256?red:255;
									green/=size;green=green<256?green:255;
									blue/=size;blue=blue<256?blue:255;
									
									//on repasse la boucle pour pixeliser les voisins
									for(int z=0;z<noyau.length;z++)
									{
										int[] coord=noyau[z];
										ligneVar[z]=originS+coord[0];
										colVar[z]=originT+coord[1];
										//on test chaque coordonnées maintenant
										if(coord[0]<=0)
										{
											//on est dans la partie <0 du noyau
											if(ligneVar[z]<=0)
											{
												ligneVar[z]=originS;
												colVar[z]=originT;
											}
										}
										else
										{
											if(ligneVar[z]>=origin.rows())
											{
												ligneVar[z]=originS;
												colVar[z]=originT;
											}
										}
										if(coord[1]<=0)
										{
											if(colVar[z]<=0)
											{
												ligneVar[z]=originS;
												colVar[z]=originT;
											}
										}
										else
										{
											if(colVar[z]>=origin.cols())
											{
												ligneVar[z]=originS;
												colVar[z]=originT;
											}
										}
										result.put(ligneVar[z], colVar[z],new byte[]{(byte)blue,(byte)green,(byte)red});
									}
									//fin boucle
									 
									 
								}
								if(j==0)
								{
									//HORIZONTAL OVERLAP REGION
									if(s==191)
									{
										//bord bottom
										if(originS!=(11*192+s))
										{
											double red=0,green=0,blue=0;
											for(int z=0;z<noyau.length;z++)
											{
												int[] coord=noyau[z];
												ligneVar[z]=originS+coord[0];
												colVar[z]=originT+coord[1];
												//on test chaque coordonnées maintenant
												if(coord[0]<=0)
												{
													//on est dans la partie <0 du noyau
													if(ligneVar[z]<=0)
													{
														ligneVar[z]=originS;
														colVar[z]=originT;
													}
												}
												else
												{
													if(ligneVar[z]>=origin.rows())
													{
														ligneVar[z]=originS;
														colVar[z]=originT;
													}
												}
												if(coord[1]<=0)
												{
													if(colVar[z]<=0)
													{
														ligneVar[z]=originS;
														colVar[z]=originT;
													}
												}
												else
												{
													if(colVar[z]>=origin.cols())
													{
														ligneVar[z]=originS;
														colVar[z]=originT;
													}
												}
												origin.get(ligneVar[z], colVar[z],pf);
												//result.put(ligneVar[z], colVar[z],pixel);
												red+=byteColorCVtoIntJava(pf[2]);
												green+=byteColorCVtoIntJava(pf[1]);
												blue+=byteColorCVtoIntJava(pf[0]);
												
											}
											red/=size;red=red<256?red:255;
											green/=size;green=green<256?green:255;
											blue/=size;blue=blue<256?blue:255;
											
											//on repasse la boucle pour pixeliser les voisins
											for(int z=0;z<noyau.length;z++)
											{
												int[] coord=noyau[z];
												ligneVar[z]=originS+coord[0];
												colVar[z]=originT+coord[1];
												//on test chaque coordonnées maintenant
												if(coord[0]<=0)
												{
													//on est dans la partie <0 du noyau
													if(ligneVar[z]<=0)
													{
														ligneVar[z]=originS;
														colVar[z]=originT;
													}
												}
												else
												{
													if(ligneVar[z]>=origin.rows())
													{
														ligneVar[z]=originS;
														colVar[z]=originT;
													}
												}
												if(coord[1]<=0)
												{
													if(colVar[z]<=0)
													{
														ligneVar[z]=originS;
														colVar[z]=originT;
													}
												}
												else
												{
													if(colVar[z]>=origin.cols())
													{
														ligneVar[z]=originS;
														colVar[z]=originT;
													}
												}
												result.put(ligneVar[z], colVar[z],new byte[]{(byte)blue,(byte)green,(byte)red});
											}
											//fin boucle
											 
											 
										}
									}
								}
							}							
							else if(j==0)
							{
								//HORIZONTAL OVERLAP REGION
								if(s==191)
								{
									//bord bottom
									if(originS!=(11*192+s))
									{
										double red=0,green=0,blue=0;
										for(int z=0;z<noyau.length;z++)
										{
											int[] coord=noyau[z];
											ligneVar[z]=originS+coord[0];
											colVar[z]=originT+coord[1];
											//on test chaque coordonnées maintenant
											if(coord[0]<=0)
											{
												//on est dans la partie <0 du noyau
												if(ligneVar[z]<=0)
												{
													ligneVar[z]=originS;
													colVar[z]=originT;
												}
											}
											else
											{
												if(ligneVar[z]>=origin.rows())
												{
													ligneVar[z]=originS;
													colVar[z]=originT;
												}
											}
											if(coord[1]<=0)
											{
												if(colVar[z]<=0)
												{
													ligneVar[z]=originS;
													colVar[z]=originT;
												}
											}
											else
											{
												if(colVar[z]>=origin.cols())
												{
													ligneVar[z]=originS;
													colVar[z]=originT;
												}
											}
											origin.get(ligneVar[z], colVar[z],pf);
											//result.put(ligneVar[z], colVar[z],pixel);
											red+=byteColorCVtoIntJava(pf[2]);
											green+=byteColorCVtoIntJava(pf[1]);
											blue+=byteColorCVtoIntJava(pf[0]);
											
										}
										red/=size;red=red<256?red:255;
										green/=size;green=green<256?green:255;
										blue/=size;blue=blue<256?blue:255;
										
										//on repasse la boucle pour pixeliser les voisins
										for(int z=0;z<noyau.length;z++)
										{
											int[] coord=noyau[z];
											ligneVar[z]=originS+coord[0];
											colVar[z]=originT+coord[1];
											//on test chaque coordonnées maintenant
											if(coord[0]<=0)
											{
												//on est dans la partie <0 du noyau
												if(ligneVar[z]<=0)
												{
													ligneVar[z]=originS;
													colVar[z]=originT;
												}
											}
											else
											{
												if(ligneVar[z]>=origin.rows())
												{
													ligneVar[z]=originS;
													colVar[z]=originT;
												}
											}
											if(coord[1]<=0)
											{
												if(colVar[z]<=0)
												{
													ligneVar[z]=originS;
													colVar[z]=originT;
												}
											}
											else
											{
												if(colVar[z]>=origin.cols())
												{
													ligneVar[z]=originS;
													colVar[z]=originT;
												}
											}
											result.put(ligneVar[z], colVar[z],new byte[]{(byte)blue,(byte)green,(byte)red});
										}
										//fin boucle
										 
										 
									}
								}
							}
							else
							{
								//L-SHAPE OVERLAP REGION
								if(s==0)
								{
									//bord top
									if(originS!=0)
									{
										double red=0,green=0,blue=0;
										for(int z=0;z<noyau.length;z++)
										{
											int[] coord=noyau[z];
											ligneVar[z]=originS+coord[0];
											colVar[z]=originT+coord[1];
											//on test chaque coordonnées maintenant
											if(coord[0]<=0)
											{
												//on est dans la partie <0 du noyau
												if(ligneVar[z]<=0)
												{
													ligneVar[z]=originS;
													colVar[z]=originT;
												}
											}
											else
											{
												if(ligneVar[z]>=origin.rows())
												{
													ligneVar[z]=originS;
													colVar[z]=originT;
												}
											}
											if(coord[1]<=0)
											{
												if(colVar[z]<=0)
												{
													ligneVar[z]=originS;
													colVar[z]=originT;
												}
											}
											else
											{
												if(colVar[z]>=origin.cols())
												{
													ligneVar[z]=originS;
													colVar[z]=originT;
												}
											}
											origin.get(ligneVar[z], colVar[z],pf);
											//result.put(ligneVar[z], colVar[z],pixel);
											red+=byteColorCVtoIntJava(pf[2]);
											green+=byteColorCVtoIntJava(pf[1]);
											blue+=byteColorCVtoIntJava(pf[0]);
											
										}
										red/=size;red=red<256?red:255;
										green/=size;green=green<256?green:255;
										blue/=size;blue=blue<256?blue:255;
										
										//on repasse la boucle pour pixeliser les voisins par la moyenne
										for(int z=0;z<noyau.length;z++)
										{
											int[] coord=noyau[z];
											ligneVar[z]=originS+coord[0];
											colVar[z]=originT+coord[1];
											//on test chaque coordonnées maintenant
											if(coord[0]<=0)
											{
												//on est dans la partie <0 du noyau
												if(ligneVar[z]<=0)
												{
													ligneVar[z]=originS;
													colVar[z]=originT;
												}
											}
											else
											{
												if(ligneVar[z]>=origin.rows())
												{
													ligneVar[z]=originS;
													colVar[z]=originT;
												}
											}
											if(coord[1]<=0)
											{
												if(colVar[z]<=0)
												{
													ligneVar[z]=originS;
													colVar[z]=originT;
												}
											}
											else
											{
												if(colVar[z]>=origin.cols())
												{
													ligneVar[z]=originS;
													colVar[z]=originT;
												}
											}
											result.put(ligneVar[z], colVar[z],new byte[]{(byte)blue,(byte)green,(byte)red});
										}
										//fin boucle
										 
									}								
								}
								
								if(t==0)
								{
									//bord left
									if(originT!=0)
									{
										double red=0,green=0,blue=0;
										for(int z=0;z<noyau.length;z++)
										{
											int[] coord=noyau[z];
											ligneVar[z]=originS+coord[0];
											colVar[z]=originT+coord[1];
											//on test chaque coordonnées maintenant
											if(coord[0]<=0)
											{
												//on est dans la partie <0 du noyau
												if(ligneVar[z]<=0)
												{
													ligneVar[z]=originS;
													colVar[z]=originT;
												}
											}
											else
											{
												if(ligneVar[z]>=origin.rows())
												{
													ligneVar[z]=originS;
													colVar[z]=originT;
												}
											}
											if(coord[1]<=0)
											{
												if(colVar[z]<=0)
												{
													ligneVar[z]=originS;
													colVar[z]=originT;
												}
											}
											else
											{
												if(colVar[z]>=origin.cols())
												{
													ligneVar[z]=originS;
													colVar[z]=originT;
												}
											}
											origin.get(ligneVar[z], colVar[z],pf);
											//result.put(ligneVar[z], colVar[z],pixel);
											red+=byteColorCVtoIntJava(pf[2]);
											green+=byteColorCVtoIntJava(pf[1]);
											blue+=byteColorCVtoIntJava(pf[0]);
											
										}
										red/=size;red=red<256?red:255;
										green/=size;green=green<256?green:255;
										blue/=size;blue=blue<256?blue:255;
										
										//on repasse la boucle pour pixeliser les voisins
										for(int z=0;z<noyau.length;z++)
										{
											int[] coord=noyau[z];
											ligneVar[z]=originS+coord[0];
											colVar[z]=originT+coord[1];
											//on test chaque coordonnées maintenant
											if(coord[0]<=0)
											{
												//on est dans la partie <0 du noyau
												if(ligneVar[z]<=0)
												{
													ligneVar[z]=originS;
													colVar[z]=originT;
												}
											}
											else
											{
												if(ligneVar[z]>=origin.rows())
												{
													ligneVar[z]=originS;
													colVar[z]=originT;
												}
											}
											if(coord[1]<=0)
											{
												if(colVar[z]<=0)
												{
													ligneVar[z]=originS;
													colVar[z]=originT;
												}
											}
											else
											{
												if(colVar[z]>=origin.cols())
												{
													ligneVar[z]=originS;
													colVar[z]=originT;
												}
											}
											result.put(ligneVar[z], colVar[z],new byte[]{(byte)blue,(byte)green,(byte)red});
										}
										//fin boucle
										 
										 
									}
								}
							}
							tilesCV[i][j].put(s, t, pixel);
						}
					}
					//saveImage(convertCVtoJava(tilesCV[i][j]), "C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\branche_step1\\bords\\bord_"+i+"_"+j+".jpg");
				}
			}
			saveImage(convertCVtoJava(result), "C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\branche_step1\\testBords.jpg");
			System.out.println("bord traité");
			/*
			 * Reconstruire
			 */
			
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	public static int[] decodePosition(double pos)
	{
		int[] p=new int[3];
		String[] pattern=Double.toString(pos).split("\\.");
		p[0]=Integer.parseInt(pattern[0]);
		p[1]=Integer.parseInt(pattern[1].substring(0, pattern[1].length()-1));
		p[2]=0;
		return p;
	}
	public static int byteColorCVtoIntJava(byte b)
	{		
		int i=(b+128)+128;		
		return b>=0?(int)b:i;
	}
	public static void saveImage(BufferedImage image,String path)
	{
		try
		{			
			//save image		
			ImageIO.write(image, "jpg", new File(path));			
		}
		catch(IOException e)
		{
			System.err.println("Erreur lors de l'enregistrement:"+e.getMessage());
		}		
	}
	public static void SimpleSeuillage(Mat image,Mat resultat,byte[] seuils)
	{
		int rows=image.rows();
		int cols=image.cols();
		byte[] color=new byte[3];
		byte[] currentColor=new byte[3];
		for(int i=0;i<rows;i++)
		{
			for(int j=0;j<cols;j++)
			{
				image.get(i,j,currentColor);
				//System.out.println(currentColor[0]+"/"+currentColor[1]+"/"+currentColor[2]);
				color[0]=currentColor[0]>=seuils[0]?seuils[0]:currentColor[0];
				color[1]=currentColor[1]>=seuils[1]?seuils[1]:currentColor[1];
				color[2]=currentColor[2]>=seuils[2]?seuils[2]:currentColor[2];
				resultat.put(i, j, color);
			}
		}
	}
	public static void Gradient(Mat imageCV3,Mat imageCV4,GradientType type)
	{
		double[] color=new double[3];
		double[] greyCol=new double[3];
		byte[] pixelN=new byte[3];
		byte[] pixelS=new byte[3];
		byte[] pixelE=new byte[3];
		byte[] pixelW=new byte[3];
		
		int dx=0;
		int dy=0;
		int dB=0;
		for(int i=0;i<imageCV3.rows();i++)
		{
			for(int j=0;j<imageCV3.cols();j++)
			{					
				//
				imageCV3.get(i-1<0?i:(i-1), j,pixelN);
				imageCV3.get(i+1<imageCV3.rows()?i+1:i, j,pixelS);
				imageCV3.get(i, j+1<imageCV3.cols()?j+1:j,pixelE);
				imageCV3.get(i, j-1<0?j:j-1,pixelW);
				/*if(i-1<0)pixelN=new byte[]{0,0,0};
				else imageCV3.get(i-1, j,pixelN);
				if(i+1<imageCV3.rows())pixelS=new byte[]{0,0,0};
				else imageCV3.get(i+1, j,pixelS);
				if(j+1<imageCV3.cols())pixelE=new byte[]{0,0,0};
				else imageCV3.get(i, j+1,pixelE);
				if(j-1<0)pixelW=new byte[]{0,0,0};
				else imageCV3.get(i, j-1,pixelW);*/
				if(type==GradientType.Grey)
				{
					double[] N=new double[]{(pixelN[0]+pixelN[1]+pixelN[2])/3,(pixelN[0]+pixelN[1]+pixelN[2])/3,(pixelN[0]+pixelN[1]+pixelN[2])/3};
					double[] S=new double[]{(pixelS[0]+pixelS[1]+pixelS[2])/3,(pixelS[0]+pixelS[1]+pixelS[2])/3,(pixelS[0]+pixelS[1]+pixelS[2])/3};
					double[] E=new double[]{(pixelE[0]+pixelE[1]+pixelE[2])/3,(pixelE[0]+pixelE[1]+pixelE[2])/3,(pixelE[0]+pixelE[1]+pixelE[2])/3};
					double[] W=new double[]{(pixelW[0]+pixelW[1]+pixelW[2])/3,(pixelW[0]+pixelW[1]+pixelW[2])/3,(pixelW[0]+pixelW[1]+pixelW[2])/3};
					double ddB=Math.sqrt((E[0]-W[0])*(E[0]-W[0])+(N[0]-S[0])*(N[0]-S[0]));
					if(ddB>255)ddB=255;
					greyCol[0]=ddB;
					greyCol[1]=ddB;
					greyCol[2]=ddB;
					imageCV4.put(i, j, greyCol);					
				}
				else
				{
					dx=pixelE[0]-pixelW[0];
					dy=pixelN[0]-pixelS[0];
					
					//module dx et dy
					dB=(int)Math.sqrt(dx*dx+dy*dy);				
					int dG=(int)Math.sqrt((pixelE[1]-pixelW[1])*(pixelE[1]-pixelW[1])+((pixelN[1]-pixelS[1])*(pixelN[1]-pixelS[1])));
					int dR=(int)Math.sqrt((pixelE[2]-pixelW[2])*(pixelE[2]-pixelW[2])+((pixelN[2]-pixelS[2])*(pixelN[2]-pixelS[2])));
					//choix entre derivé selon dx ou dy
					//dx=pixelE[0]-pixelW[0];
					/*dy=pixelN[0]-pixelS[0];
					dB=(int)Math.sqrt(dy*dy);				
					int dG=(int)Math.sqrt((pixelN[1]-pixelS[1])*(pixelN[1]-pixelS[1]));
					int dR=(int)Math.sqrt((pixelN[2]-pixelS[2])*(pixelN[2]-pixelS[2]));*/
					/*
					//gradient direction
					dB=(int)((Math.atan(((double)dy)/dx)*180)/Math.PI);
					int dG=(int)((Math.atan(((double)((pixelN[1]-pixelS[1])*(pixelN[1]-pixelS[1])))/(pixelE[1]-pixelW[1])*(pixelE[1]-pixelW[1]))*180/Math.PI));
					int dR=(int)((Math.atan(((double)((pixelN[2]-pixelS[2])*(pixelN[2]-pixelS[2])))/(pixelE[2]-pixelW[2])*(pixelE[2]-pixelW[2]))*180/Math.PI));*/
					if(dB>255)dB=255;
					if(dG>255)dG=255;
					if(dR>255)dR=255;
					if(type==GradientType.B)
					{
						dG=0;dR=0;
					}
					else if(type==GradientType.R)
					{
						dB=0;dG=0;
					}
					else if(type==GradientType.G)
					{
						dB=0;
						dR=0;
					}					
					color[0]=dB;
					color[1]=dG;
					color[2]=dR;
					imageCV4.put(i, j, color);
				}
								
			}
		}
	}
	public static void Normal(Mat image,Mat resultat,GradientType type)
	{
		int rows=image.rows();
		int cols=image.cols();
		double[] color=new double[3];
		double[] greyCol=new double[3];
		byte[] pixelNE=new byte[3];
		byte[] pixelSW=new byte[3];
		byte[] pixelNW=new byte[3];
		byte[] pixelSE=new byte[3];
		double dB,dR,dG;
		for(int i=0;i<rows;i++)
		{
			for(int j=0;j<cols;j++)
			{
				//
				if(i-1>=0 && j+1<=cols)image.get(i-1, j+1,pixelNE);
				else image.get(i, j,pixelNE);
				if(i-1>=0 && j-1>=0)image.get(i-1, j-1,pixelNW);
				else image.get(i, j,pixelNW);
				if(i+1>rows && j+1>cols)image.get(i, j,pixelSE);
				else image.get(i+1, j+1,pixelSE);
				if(i+1>rows && j-1<0)image.get(i, j,pixelSW);
				else image.get(i+1, j-1,pixelSW);				
				if(type==GradientType.Grey)
				{
					double[] NE=new double[]{(pixelNE[0]+pixelNE[1]+pixelNE[2])/3,(pixelNE[0]+pixelNE[1]+pixelNE[2])/3,(pixelNE[0]+pixelNE[1]+pixelNE[2])/3};
					double[] SE=new double[]{(pixelSE[0]+pixelSE[1]+pixelSE[2])/3,(pixelSE[0]+pixelSE[1]+pixelSE[2])/3,(pixelSE[0]+pixelSE[1]+pixelSE[2])/3};
					double[] NW=new double[]{(pixelNW[0]+pixelNW[1]+pixelNW[2])/3,(pixelNW[0]+pixelNW[1]+pixelNW[2])/3,(pixelNW[0]+pixelNW[1]+pixelNW[2])/3};
					double[] SW=new double[]{(pixelSW[0]+pixelSW[1]+pixelSW[2])/3,(pixelSW[0]+pixelSW[1]+pixelSW[2])/3,(pixelSW[0]+pixelSW[1]+pixelSW[2])/3};
					double ddB=Math.abs(Math.atan2(SE[0]-NW[0], SW[0]-NE[0])*255);
					if(ddB>255)ddB=255;
					greyCol[0]=ddB;
					greyCol[1]=ddB;
					greyCol[2]=ddB;
					resultat.put(i, j, greyCol);					
				}
				else
				{									
					dB=Math.abs(Math.atan2(pixelSE[0]-pixelNW[0], pixelSW[0]-pixelNE[0])*255) ;
					dR=Math.abs(Math.atan2(pixelSE[2]-pixelNW[2], pixelSW[2]-pixelNE[2])*255);//
					dG=Math.abs(Math.atan2(pixelSE[1]-pixelNW[1], pixelSW[1]-pixelNE[1])*255);//
					if(type==GradientType.B)
					{
						dG=0;dR=0;
					}
					else if(type==GradientType.R)
					{
						dB=0;dG=0;
					}
					else if(type==GradientType.G)
					{
						dB=0;
						dR=0;
					}					
					color[0]=dB;
					color[1]=dG;
					color[2]=dR;
					resultat.put(i, j, color);
				}
			}
		}
		
	}
	public static BufferedImage cvToJava(Mat m)
	{
		int bufferSize=m.cols()*m.rows()*m.channels();
		BufferedImage image=new BufferedImage(m.cols(),m.rows(), BufferedImage.TYPE_3BYTE_BGR);
		byte[] data=new byte[bufferSize];
		byte[] dataDest=((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		m.get(0, 0, data);
		System.arraycopy(data, 0, dataDest, 0, bufferSize);
		return image;
	}
	public static Mat javaToCv(BufferedImage image)
	{
		Mat m=new Mat(image.getHeight(),image.getWidth(),CvType.CV_8UC3);
		int bufferSize=image.getHeight()*image.getWidth()*3;
		byte[] data=((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		byte[] dataDest=new byte[image.getHeight()*image.getWidth()*3];
		System.arraycopy(data, 0, dataDest, 0, bufferSize);
		m.put(0, 0, dataDest);
		return m;
	}
	@Override
	public void stateChanged(ChangeEvent e) 
	{
		// TODO Auto-generated method stub
		JSlider slider=(JSlider)e.getSource();
		byte value=(byte)slider.getValue();		
		if(slider.getName()=="R")
		{
			valueR=value;
			
		}
		else if(slider.getName()=="G")
		{
			//System.err.println(value);
			valueG=value;			
		}
		else
		{
			//System.err.println(value);
			valueB=value;			
		}
		SimpleSeuillage(source, target, new byte[]{valueB,valueG,valueR});
		targetLabel.setIcon(new ImageIcon(cvToJava(target)));
	}
	public static void Substract(Mat source1,Mat source2,Mat dest)
	{
		if(source1.rows()!=source2.rows() && source1.cols()!=source2.cols())
		{
			System.err.println("Taille des images non compatible");
			return;
		}
		byte[] color1=new byte[3];
		byte[] color2=new byte[3];
		byte[] color=new byte[3];
		for(int i=0;i<source1.rows();i++)
		{
			for(int j=0;j<source1.cols();j++)
			{
				source1.get(i, j,color1);
				source2.get(i, j,color2);
				color[0]=(byte)(color1[0]-color2[0]);
				color[1]=(byte)(color1[1]-color2[1]);
				color[2]=(byte)(color1[2]-color2[2]);
				dest.put(i, j, color);
			}
		}
	}
	public static BufferedImage convertCVtoJava(Mat mat)
	{
		BufferedImage image=new BufferedImage(mat.cols(), mat.rows(),BufferedImage.TYPE_3BYTE_BGR);
		byte[] data=new byte[mat.rows()*mat.cols()*mat.channels()];
		mat.get(0, 0,data);
		byte[] dest=((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		System.arraycopy(data, 0, dest, 0, mat.rows()*mat.cols()*mat.channels());		
		return image;
	}
	public static Mat convertTileToCV(BufferedImage im)
	{
		Mat m=new Mat(im.getHeight(), im.getWidth(), CvType.CV_8UC3);
		byte[] data=((DataBufferByte)im.getRaster().getDataBuffer()).getData();
		m.put(0, 0, data);
		return m;
	}
	public static int[][] kernel(int n)
	{
		if(n%2==0)return null;
		int[][] k=new int[n*n][];
		int demi=(int)Math.round((double)n/2);
		int z=0;
		for(int i=-(demi-1);i<demi;i++)
		{
			for(int j=-(demi-1);j<demi;j++)
			{
				k[z]=new int[]{i,j};
				z++;
			}
		}
		return k;
	}
}
