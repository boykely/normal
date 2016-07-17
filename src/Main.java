import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.swing.*;
import javax.tools.JavaFileObject;
import javax.xml.crypto.Data;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.ejml.alg.dense.mult.CVectorVectorMult;
import org.ejml.data.DenseMatrix32F;
import org.ejml.data.DenseMatrix64F;
import org.ejml.simple.SimpleMatrix;
import org.opencv.core.*;
import org.opencv.imgproc.*;
import org.opencv.ml.Boost;

public class Main 
{
	public static int gl=0;
	public static void main(String[] args)
	{	
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);		
		JFrame frame=new JFrame("Titre");
		frame.setSize(new Dimension(1400, 450));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);		
		FlowLayout layout=new FlowLayout();
		frame.setLayout(layout);
		try
		{
			BufferedImage guide=ImageIO.read(new File("C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\TT\\testBords.jpg"));
			int width=guide.getWidth();
			int height=guide.getHeight();
			Mat imageGuide=new Mat(guide.getHeight(),guide.getWidth(),CvType.CV_8UC3);
			System.err.println(imageGuide);
			byte[] dataGuide=((DataBufferByte)guide.getRaster().getDataBuffer()).getData();
			
			imageGuide.put(0, 0, dataGuide);
			imageGuide.get(0, 0,dataGuide);
			
			JLabel labelGuide=new JLabel(new ImageIcon(guide));	
			
			JScrollPane scroll=new JScrollPane(labelGuide);
			
			scroll.setPreferredSize(new Dimension(500, 450));
			
			frame.add(scroll);
			
			Mat imageResult=new Mat(height,width,CvType.CV_8UC3);
			Imgproc.GaussianBlur(imageGuide, imageResult, new Size(3,3), 4);
			//
			System.out.println("--------------FIN TEXTURE MATCHING--------");
			System.out.println(imageResult);
			BufferedImage resultImage=new BufferedImage(imageResult.cols(),imageResult.rows(), BufferedImage.TYPE_3BYTE_BGR);
			//resultImage=new BufferedImage(imageResult.cols(),imageResult.rows(), BufferedImage.TYPE_3BYTE_BGR);
			byte[] dataResult=((DataBufferByte)resultImage.getRaster().getDataBuffer()).getData();		
			System.out.println("data result affiche");	
			imageResult.get(0, 0,dataResult);	
			saveImage(resultImage, "C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\SampleTransportReflectance\\TT\\testBordsNew.jpg");
		}
		catch(Exception e)
		{
			System.out.println("Erreur"+e.getMessage());
		}
		JTextArea text=new JTextArea();
		frame.add(text);
		frame.setVisible(true);
		System.out.println("end");
		
	}
	public static void mainM(String[] args)
	{	
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);		
		JFrame frame=new JFrame("Titre");
		frame.setSize(new Dimension(1400, 450));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);		
		FlowLayout layout=new FlowLayout();
		frame.setLayout(layout);
		try
		{
			//BufferedImage guide=ImageIO.read(new File("C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\flash.png"));
			BufferedImage guide=ImageIO.read(new File("C:\\Users\\ralambomahay1\\Downloads\\stage\\ImageTest\\test-3-tiles-Gaussian\\main.jpg"));
			int width=guide.getWidth();
			int height=guide.getHeight();
			//BufferedImage guide=ImageIO.read(new File("C:\\Users\\ralambomahay1\\Downloads\\tsi.jpg"));
			BufferedImage source=ImageIO.read(new File("C:\\Users\\ralambomahay1\\Downloads\\stage\\ImageTest\\test-3-tiles-Gaussian\\eye.jpg"));
			Mat imageGuide=new Mat(guide.getHeight(),guide.getWidth(),CvType.CV_8UC3);
			System.err.println(imageGuide);
			Mat imageSource=new Mat(source.getHeight(),source.getWidth(),CvType.CV_8UC3);
			byte[] dataGuide=((DataBufferByte)guide.getRaster().getDataBuffer()).getData();
			imageGuide.put(0, 0, dataGuide);
			byte[] dataSource=((DataBufferByte)source.getRaster().getDataBuffer()).getData();
			imageSource.put(0, 0, dataSource);
			
			//avant de matcher les 2 images on egalise leur histogramme respectifs
			//EqualHistogram(imageGuide, imageGuide);
			//EqualHistogram(imageSource, imageSource);
			//mise à jour des données de l'affichage
			imageGuide.get(0, 0,dataGuide);
			imageSource.get(0,0,dataSource);
			
			JLabel labelGuide=new JLabel(new ImageIcon(guide));	
			JLabel labelSource=new JLabel(new ImageIcon(source));
			JLabel labelResult=new JLabel();
			
			JScrollPane scroll=new JScrollPane(labelGuide);
			JScrollPane scroll_=new JScrollPane(labelSource);
			JScrollPane scroll__=new JScrollPane(labelResult);
			
			scroll.setPreferredSize(new Dimension(500, 450));
			scroll_.setPreferredSize(new Dimension(500, 450));
			scroll__.setPreferredSize(new Dimension(500, 450));
			
			frame.add(scroll);
			frame.add(scroll_);
			frame.add(scroll__);
			
			//crée adjascence pour la création de A
			int[][] pixelIndex=new int[imageSource.rows()*imageSource.cols()][];
			int id=0;
			for(int i=0;i<imageSource.rows();i++)
			{
				for(int j=0;j<imageSource.cols();j++)
				{
					pixelIndex[id]=new int[]{i,j};
					id++;
				}
			}
			System.out.println(id);
			id=0;
			//
			//calcule de A et B
			Mat AA=new Mat(imageSource.rows(),imageSource.cols(),CvType.CV_32FC1);
			DenseMatrix64F matBB=new DenseMatrix64F(imageSource.rows()*imageSource.cols(), 1);
			DenseMatrix64F matBG=new DenseMatrix64F(imageSource.rows()*imageSource.cols(), 1);
			DenseMatrix64F matBR=new DenseMatrix64F(imageSource.rows()*imageSource.cols(), 1);
			DenseMatrix64F matA=new  DenseMatrix64F(1500, 1500);
			SimpleMatrix matXB;
			SimpleMatrix matXG;
			SimpleMatrix matXR;
			for(int i=0;i<1500;i++)
			{
				for(int j=0;j<1500;j++)
				{
					if(i==j)
					{
						AA.put(i, j, new float[]{-4});
						matA.set(i, j, -4);
					}
					else 
					{
						int[] p=pixelIndex[i];
						int[] pp=pixelIndex[j];
						if(p[0]-pp[0]==0/*même ligne*/ && Math.abs(p[1]-pp[1])==1 /*adjascent*/)
						{
							AA.put(i, j, new float[]{1});matA.set(i, j, 1);
						}
						else if(p[1]-pp[1]==0/*même colonne*/ && Math.abs(p[0]-pp[0])==1 /*adjascent*/)
						{
							AA.put(i, j, new float[]{1});matA.set(i, j, 1);
						}
						else
						{
							AA.put(i, j, new float[]{0});
							matA.set(i, j, 0);
						}
					}
				}
			}
			System.out.println("Mat A="+matA.numRows);
			//
			//
			Mat A=sobel(imageGuide);
			Mat B=sobel(imageSource);
			BufferedImage resultImage=new BufferedImage(guide.getWidth(),guide.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			Mat imageResult=imageGuide.clone();
			int ia=imageResult.rows()/3;
			int ib=ia+imageSource.rows();
			int ja=imageResult.cols()/2;
			int jb=ja+imageSource.cols();
			int x=0;
			int y=0;
			for(int i=0;i<imageResult.rows();i++)
			{
				for(int j=0;j<imageResult.cols();j++)
				{
					byte[] mainP=new byte[3];
					byte[] eyeP=new byte[3];
					if(i>=ia && i<ib && j>=ja && j<jb)
					{
						//copie de eye dans main
						B.get(i-ia, j-ja,eyeP);
						//imageResult.put(i, j, eyeP);
						x++;
						//on va calculer matB ici
						byte[] guidePixel=new byte[3];
						imageGuide.get(i, j,guidePixel);
						byte[] border=new byte[3];
						if(i==ia && j>=ja && j<jb)
						{
							imageGuide.get(i-1, j,guidePixel);
							border[0]=(byte)(byteColorCVtoIntJava(guidePixel[0]));border[1]=(byte)(byteColorCVtoIntJava(guidePixel[1]));border[2]=(byte)(byteColorCVtoIntJava(guidePixel[2]));
							if(j==ja)
							{		
								imageGuide.get(i, j-1,guidePixel);
								border[0]+=(byte)(byteColorCVtoIntJava(guidePixel[0]));border[1]+=(byte)(byteColorCVtoIntJava(guidePixel[1]));border[2]+=(byte)(byteColorCVtoIntJava(guidePixel[2]));
							}
							if(j==jb-1)
							{
								imageGuide.get(i, j+1,guidePixel);
								border[0]+=(byte)(byteColorCVtoIntJava(guidePixel[0]));border[1]+=(byte)(byteColorCVtoIntJava(guidePixel[1]));border[2]+=(byte)(byteColorCVtoIntJava(guidePixel[2]));
							}
							matBB.set(id,0,byteColorCVtoIntJava(border[0]));
							matBG.set(id,0,byteColorCVtoIntJava(border[1]));
							matBR.set(id,0,byteColorCVtoIntJava(border[2]));
						}
						else if(i>ia && i<ib && j==ja)
						{
							imageGuide.get(i, j-1,guidePixel);
							border[0]+=(byte)(byteColorCVtoIntJava(guidePixel[0]));border[1]+=(byte)(byteColorCVtoIntJava(guidePixel[1]));border[2]+=(byte)(byteColorCVtoIntJava(guidePixel[2]));
							if(i==ib-1)
							{
								imageGuide.get(i+1, j,guidePixel);
								border[0]+=(byte)(byteColorCVtoIntJava(guidePixel[0]));border[1]+=(byte)(byteColorCVtoIntJava(guidePixel[1]));border[2]+=(byte)(byteColorCVtoIntJava(guidePixel[2]));
							}
							matBB.set(id,0,byteColorCVtoIntJava(border[0]));
							matBG.set(id,0,byteColorCVtoIntJava(border[1]));
							matBR.set(id,0,byteColorCVtoIntJava(border[2]));
							//imageResult.put(i, j, new byte[]{0,127,0});
						}
						else if(i>ia && i<ib && j==jb )
						{
							imageGuide.get(i, j+1,guidePixel);
							border[0]+=(byte)(byteColorCVtoIntJava(guidePixel[0]));border[1]+=(byte)(byteColorCVtoIntJava(guidePixel[1]));border[2]+=(byte)(byteColorCVtoIntJava(guidePixel[2]));
							if(i==ib-1)
							{
								imageGuide.get(i+1, j,guidePixel);
								border[0]+=(byte)(byteColorCVtoIntJava(guidePixel[0]));border[1]+=(byte)(byteColorCVtoIntJava(guidePixel[1]));border[2]+=(byte)(byteColorCVtoIntJava(guidePixel[2]));
							}
							matBB.set(id,0,byteColorCVtoIntJava(border[0]));
							matBG.set(id,0,byteColorCVtoIntJava(border[1]));
							matBR.set(id,0,byteColorCVtoIntJava(border[2]));
						}
						else if(i==ib-1 && j>ja && j<jb-1)
						{
							imageGuide.get(i+1, j,guidePixel);
							border[0]+=(byte)(byteColorCVtoIntJava(guidePixel[0]));border[1]+=(byte)(byteColorCVtoIntJava(guidePixel[1]));border[2]+=(byte)(byteColorCVtoIntJava(guidePixel[2]));
							matBB.set(id,0,byteColorCVtoIntJava(border[0]));
							matBG.set(id,0,byteColorCVtoIntJava(border[1]));
							matBR.set(id,0,byteColorCVtoIntJava(border[2]));
						}
						else
						{
							matBB.set(id, 0, 0);
							matBG.set(id, 0, 0);
							matBR.set(id, 0, 0);
						}
						int b=byteColorCVtoIntJava(eyeP[0]);
						int g=byteColorCVtoIntJava(eyeP[1]);
						int r=byteColorCVtoIntJava(eyeP[2]);
						double t=matBB.get(id, 0);
						t+=b;
						matBB.set(id, 0, t);
						t=matBG.get(id, 0);
						t+=g;
						matBG.set(id, 0, t);
						t=matBR.get(id, 0);
						t+=r;
						matBR.set(id, 0, t);
						id++;
					}
					
				}
			}
			System.out.println(id);
			SimpleMatrix sparse=new SimpleMatrix(matA);
			matXB=sparse.solve(new SimpleMatrix(matBB));
			matXG=sparse.solve(new SimpleMatrix(matBG));
			matXR=sparse.solve(new SimpleMatrix(matBR));
			id=0;
			double alpha=0.8;
			double teta=0.75;
			for(int i=0;i<imageResult.rows();i++)
			{
				for(int j=0;j<imageResult.cols();j++)
				{
					byte[] mainP=new byte[3];
					byte[] eyeP=new byte[3];
					byte[] grad=new byte[3];
					if(i>=ia && i<ib && j>=ja && j<jb)
					{
						imageResult.get(i, j,mainP);
						imageSource.get(i-ia, j-ja,eyeP);
						B.get(i-ia, j-ja,grad);
						int eb=byteColorCVtoIntJava(eyeP[0]);
						int eg=byteColorCVtoIntJava(eyeP[1]);
						int er=byteColorCVtoIntJava(eyeP[2]);
						int mb=byteColorCVtoIntJava(mainP[0]);
						int mg=byteColorCVtoIntJava(mainP[1]);
						int mr=byteColorCVtoIntJava(mainP[2]);
						
						//copie de eye dans main = poisson
						double b=matXB.get(id, 0)>256?255:matBB.get(id, 0);
						double g=matXG.get(id, 0)>256?255:matBG.get(id, 0);
						double r=matXR.get(id, 0)>256?255:matBR.get(id, 0);
						
						b=alpha*mb+(1-alpha)*b;
						g=alpha*mg+(1-alpha)*g;
						r=alpha*mr+(1-alpha)*r;
						
						/*b=teta*b+(1-teta)*eb;
						g=teta*g+(1-teta)*eg;
						r=teta*r+(1-teta)*er;*/
						byte[] p=new byte[]{(byte)b,(byte)g,(byte)r};
						imageResult.put(i, j, p);
						id++;
					}
				}
			}
			Mat newM=imageResult.submat(ia, ib, ja, jb);
			Imgproc.GaussianBlur(newM, newM, new Size(3,3), 1.5);
			for(int i=0;i<imageResult.rows();i++)
			{
				for(int j=0;j<imageResult.cols();j++)
				{
					byte[] mainP=new byte[3];
					byte[] eyeP=new byte[3];
					byte[] grad=new byte[3];
					if(i>=ia && i<ib && j>=ja && j<jb)
					{
						newM.get(i-ia, j-ja,eyeP);
						imageResult.put(i, j, eyeP);
					}
				}
			}
			/*for(int i=0;i<imageSource.rows();i++)
			{
				for(int j=0;j<imageSource.cols();j++)
				{
					byte[] targetP=new byte[3];
					byte[] sourceP=new byte[3];
					imageSource.get(i, j,sourceP);
					//ici on suppose on est en dehors du masque ==0
					imageResult.put(i, j, sourceP);					
					if(i<imageGuide.rows() && j<imageGuide.cols())
					{
						byte[] finalP=new byte[3];
						byte[] tempP=new byte[3];
						imageGuide.get(i, j,targetP);
						//ici on suppose on dans le masque ==1
						if(i-1>=0)imageGuide.get(i-1, j,tempP);
						else imageGuide.get(i, j,tempP);
						int b=(4*byteColorCVtoIntJava(targetP[0])-byteColorCVtoIntJava(tempP[0]))+byteColorCVtoIntJava(tempP[0]);
						int g=(4*byteColorCVtoIntJava(targetP[1])-byteColorCVtoIntJava(tempP[1]))+byteColorCVtoIntJava(tempP[1]);
						int r=(4*byteColorCVtoIntJava(targetP[2])-byteColorCVtoIntJava(tempP[2]))+byteColorCVtoIntJava(tempP[2]);
						/*finalP[0]+=(byte)(Math.sqrt(b));
						finalP[1]+=(byte)(Math.sqrt(g));
						finalP[2]+=(byte)(Math.sqrt(r));*//*
						finalP[0]+=b>256?(byte)255:(byte)b;
						finalP[1]+=g>256?(byte)255:(byte)g;
						finalP[2]+=r>256?(byte)255:(byte)r;
						if(i+1<imageGuide.rows())imageGuide.get(i+1, j,tempP);
						else imageGuide.get(i, j,tempP);
						b=(4*byteColorCVtoIntJava(targetP[0])-byteColorCVtoIntJava(tempP[0]))+byteColorCVtoIntJava(tempP[0]);
						g=(4*byteColorCVtoIntJava(targetP[1])-byteColorCVtoIntJava(tempP[1]))+byteColorCVtoIntJava(tempP[1]);
						r=(4*byteColorCVtoIntJava(targetP[2])-byteColorCVtoIntJava(tempP[2]))+byteColorCVtoIntJava(tempP[2]);
						finalP[0]+=b>256?(byte)255:(byte)b;
						finalP[1]+=g>256?(byte)255:(byte)g;
						finalP[2]+=r>256?(byte)255:(byte)r;
						if(j-1>=0)imageGuide.get(i, j-1,tempP);
						else imageGuide.get(i, j,tempP);
						b=(4*byteColorCVtoIntJava(targetP[0])-byteColorCVtoIntJava(tempP[0]))+byteColorCVtoIntJava(tempP[0]);
						g=(4*byteColorCVtoIntJava(targetP[1])-byteColorCVtoIntJava(tempP[1]))+byteColorCVtoIntJava(tempP[1]);
						r=(4*byteColorCVtoIntJava(targetP[2])-byteColorCVtoIntJava(tempP[2]))+byteColorCVtoIntJava(tempP[2]);
						finalP[0]+=b>256?(byte)255:(byte)b;
						finalP[1]+=g>256?(byte)255:(byte)g;
						finalP[2]+=r>256?(byte)255:(byte)r;
						if(j+1<imageGuide.cols())imageGuide.get(i, j+1,tempP);
						else imageGuide.get(i, j,tempP);
						b=(4*byteColorCVtoIntJava(targetP[0])-byteColorCVtoIntJava(tempP[0]))+byteColorCVtoIntJava(tempP[0]);
						g=(4*byteColorCVtoIntJava(targetP[1])-byteColorCVtoIntJava(tempP[1]))+byteColorCVtoIntJava(tempP[1]);
						r=(4*byteColorCVtoIntJava(targetP[2])-byteColorCVtoIntJava(tempP[2]))+byteColorCVtoIntJava(tempP[2]);
						finalP[0]+=b>256?(byte)255:(byte)b;
						finalP[1]+=g>256?(byte)255:(byte)g;
						finalP[2]+=r>256?(byte)255:(byte)r;
						//
						
						//Pixel voisins 
						
						imageResult.put(i, j, finalP);
					}
				}
			}*/
			//System.arraycopy(dataSource, 0, dataResult, 0, imageGuide.channels()*imageGuide.cols()*imageGuide.rows());
			//MatchingHistogram(imageGuide, imageSource, imageResult);
			//EqualHistogram(imageGuide, imageResult);
			
			//imageResult=TextureMatching(imageGuide, imageSource, imageResult,6);
			/*
			Color av=AverageColor();
			System.out.println(av.getRed()+" - "+av.getGreen()+" - "+av.getBlue());
			byte[] p=new byte[]{(byte)av.getBlue(),(byte)av.getGreen(),(byte)av.getRed()};
			byte[] pixelS=new byte[3];
			byte[] pixelResult=new byte[3];
			int[] cam=new int[]{0,imageSource.rows(),5};
			int[] lum=new int[]{imageSource.cols(),imageSource.rows(),10};
			int[] pos=new int[3];
			double[] E=new double[3];
			double[] L=new double[3];
			double D2;			
			double[] H=new double[3];
			double[] N=new double[3];
			SimpleMatrix R;
			double[] Hn=new double[3];
			double[] Hnp=new double[3];
			SimpleMatrix M;
			double[] HnpW=new double[3];
			byte[] alb_d=new byte[3];
			byte[] alb_s=new byte[3];
			byte[] specv=new byte[3];
			double alpha=1;//lobe speculaire => plus grand plus retrecie et concentré
			double intensity=0.3;//varie 0 à 1
			System.out.println("Start");
			for(int i=0;i<imageResult.rows();i++)
			{
				for(int j=0;j<imageResult.cols();j++)
				{
					imageSource.get(i, j, alb_d);
					imageSource.get(i, j, alb_s);
					imageSource.get(i, j, specv);
					pos[0]=j;pos[1]=i;pos[2]=0;
					//E=normalize(cam-pos)
					E=normalize(XY(pos,cam));
					L=calculeL(pos,lum);
					double[] le=addXY(L,E);
					H=normalize(le);
					N=normalize(new int[]{j,i,2});
					D2=dot(L,L);
					R=new SimpleMatrix(new double[][]{
						{0,0,N[0]},
						{0,0,N[1]},
						{-N[0],-N[1],0}
					});
					Hn=calculeHn(H,N,R);	
					Hnp=div(Hn,Hn[2]);
					M=new SimpleMatrix(new double[][]{
						{byteColorCVtoIntJava(specv[2]),byteColorCVtoIntJava(specv[0])},
						{byteColorCVtoIntJava(specv[0]),byteColorCVtoIntJava(specv[1])},
					});
					HnpW=calculeHnpW(M,Hnp[0],Hnp[1]);
					double spec=Math.exp(-Math.pow(dot(new double[]{HnpW[0],HnpW[1]},new double[]{Hnp[0],Hnp[1]}), alpha*0.5));
					double cosine=Math.max(0, dot(N,L));
					double F0=0.04;
					double fres=F0+(1-F0)*Math.pow(1.0-Math.max(0, dot(H,E)), 5.0);
					spec=spec*fres/F0;
					//sqrt is necessary to "rough gamma" => I don't understand it but if we omit the sqrt the colors are not correct
					pixelResult[0]=(byte)Math.sqrt(((spec*byteColorCVtoIntJava(alb_s[0])+byteColorCVtoIntJava(alb_d[0]))*cosine/D2*intensity*255));
					pixelResult[1]=(byte)Math.sqrt(((spec*byteColorCVtoIntJava(alb_s[1])+byteColorCVtoIntJava(alb_d[1]))*cosine/D2*intensity*255));
					pixelResult[2]=(byte)Math.sqrt(((spec*byteColorCVtoIntJava(alb_s[2])+byteColorCVtoIntJava(alb_d[2]))*cosine/D2*intensity*255));
					//
					imageResult.put(i, j, pixelResult);
				}
			}*/
			System.out.println("--------------FIN TEXTURE MATCHING--------");
			System.out.println(imageResult);
			//resultImage=new BufferedImage(imageResult.cols(),imageResult.rows(), BufferedImage.TYPE_3BYTE_BGR);
			byte[] dataResult=((DataBufferByte)resultImage.getRaster().getDataBuffer()).getData();		
			float[] data=new float[526*526*3];
			System.out.println("data result affiche");	
			imageResult.get(0, 0,dataResult);	
			labelResult.setIcon(new ImageIcon(resultImage));
			saveImage(resultImage, "C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\valCopyDirect.jpg");
		}
		catch(Exception e)
		{
			System.out.println("Erreur"+e.getMessage());
		}
		JTextArea text=new JTextArea();
		frame.add(text);
		frame.setVisible(true);
		System.out.println((byte)254);
		
	}	
	public static Mat convertTileToCV(BufferedImage im)
	{
		Mat m=new Mat(im.getHeight(), im.getWidth(), CvType.CV_8UC3);
		byte[] data=((DataBufferByte)im.getRaster().getDataBuffer()).getData();
		m.put(0, 0, data);
		return m;
	}	
	public static int byteColorCVtoIntJava(byte b)
	{		
		int i=(b+128)+128;		
		return b>=0?(int)b:i;
	}
	public static void Histogramme(Mat m,int[] hist)
	{		
		//for Grey level images
		byte[] pixel=new byte[3];
		for(int i=0;i<m.rows();i++)
		{
			for(int j=0;j<m.cols();j++)
			{					
				m.get(i, j,pixel);				
				hist[byteColorCVtoIntJava(pixel[0])]+=1;				
			}
		}
	}
	public static void HistogrammeRGB(Mat m,int[] R,int[] G,int[] B)
	{
		byte[] col=new byte[3];
		for(int i=0;i<m.rows();i++)
		{
			for(int j=0;j<m.cols();j++)
			{					
				m.get(i, j,col);
				R[byteColorCVtoIntJava(col[2])]+=1;
				G[byteColorCVtoIntJava(col[1])]+=1;
				B[byteColorCVtoIntJava(col[0])]+=1;				
			}
		}
	}
	public static void HistogrammeHSV(BufferedImage image,float[] H,float[]S)
	{
		float[] hsb;
		int c;
		Color col;
		for(int i=0;i<image.getHeight();i++)
		{
			for(int j=0;j<image.getWidth();j++)
			{
				c=image.getRGB(j, i);
				col=new Color(c);
				hsb=Color.RGBtoHSB(col.getRed(), col.getGreen(), col.getBlue(), null);
				//System.out.println("R:"+col.getRed()+"G:"+col.getGreen()+"B:"+col.getBlue()+"<>H:"+hsb[0]+"S:"+hsb[1]+"V(B):"+hsb[2]);
				
			}
		}
	}
	public static void HistogrammeCumuleRGB(Mat m,int[] R,int[] G,int[] B,int[] RC,int[] GC,int[] BC,int N)
	{
		//System.out.println("Le nombre total des pixel:"+N);
		int valueR=0;int valueG=0;int valueB=0;
		for(int i=0;i<256;i++)
		{
			valueR+=R[i];RC[i]=R[i]==0?0:valueR;
			valueG+=G[i];GC[i]=G[i]==0?0:valueG;
			valueB+=B[i];BC[i]=B[i]==0?0:valueB;
			
		}
	}
	public static void InverseHistogrammeRGB(int[] RC,int[] GC,int[] BC,Hashtable<Integer, Integer> InvHistoCumulR,Hashtable<Integer, Integer> InvHistoCumulG,Hashtable<Integer, Integer> InvHistoCumulB )
	{
		for(int i=0;i<256;i++)
		{				
			InvHistoCumulR.put(RC[i], i);
			InvHistoCumulG.put(GC[i], i);
			InvHistoCumulB.put(BC[i], i);			
		}
	}
	public static void HistogrammeCumule(Mat m,int[] hist,int[] histoCumul)
	{
		int value=0;		
		for (int i=0;i<256;i++)
		{							    
		    value+=hist[i];
		    histoCumul[i]=value;		    
		}
	}
	public static void InverseHistogrammeCumule(int[] histoCumul,Hashtable<Integer, Integer> InvHistoCumul)
	{
		for(int i=0;i<histoCumul.length;i++)
		{			
			InvHistoCumul.put(histoCumul[i], i);
		}
	}
	public static void MatchingHistogram(Mat imRef,Mat imTarget,Mat result)
	{
		//imRef => image de référence
		//imTarget => image à changer d'histogramme comme l'imRef
		int[] RRef=new int[256];int[] RCRef=new int[256];Hashtable<Integer, Integer> InvHistoCumulRRef=new Hashtable<>();
		int[] GRef=new int[256];int[] GCRef=new int[256];Hashtable<Integer, Integer> InvHistoCumulGRef=new Hashtable<>();
		int[] BRef=new int[256];int[] BCRef=new int[256];Hashtable<Integer, Integer> InvHistoCumulBRef=new Hashtable<>();
		int[] RTar=new int[256];int[] RCTar=new int[256];Hashtable<Integer, Integer> InvHistoCumulRTar=new Hashtable<>();
		int[] GTar=new int[256];int[] GCTar=new int[256];Hashtable<Integer, Integer> InvHistoCumulGTar=new Hashtable<>();
		int[] BTar=new int[256];int[] BCTar=new int[256];Hashtable<Integer, Integer> InvHistoCumulBTar=new Hashtable<>();
		int N=imRef.cols()*imRef.rows();
		HistogrammeRGB(imRef, RRef, GRef, BRef);
		HistogrammeCumuleRGB(imRef, RRef, GRef, BRef,RCRef,GCRef,BCRef,N);
		InverseHistogrammeRGB(RCRef, GCRef, BCRef, InvHistoCumulRRef, InvHistoCumulGRef, InvHistoCumulBRef);
		
		HistogrammeRGB(imTarget, RTar, GTar, BTar);
		HistogrammeCumuleRGB(imTarget, RTar, GTar, BTar,RCTar,GCTar,BCTar,N);
		InverseHistogrammeRGB(RCTar, GCTar, BCTar, InvHistoCumulRTar, InvHistoCumulGTar, InvHistoCumulBTar);		
		byte[] pixel=new byte[3];
		byte[] pixelTarget=new byte[3];
		//on va essayer de calculer le gradient du ref 
		//Imgproc.Sobel(imTarget, imTarget, imTarget.depth(), 1, 1);
		for(int i=0;i<imRef.rows();i++)
		{
			for(int j=0;j<imRef.cols();j++)
			{
				imRef.get(i, j,pixel);
				imTarget.get(i, j,pixelTarget);
				byte blue=pixelTarget[0];byte green=pixelTarget[1];byte red=pixelTarget[2];				
				int r=minimum(RCTar[byteColorCVtoIntJava(red)],RCRef,InvHistoCumulRRef);
				int g=minimum(GCTar[byteColorCVtoIntJava(green)],GCRef,InvHistoCumulGRef);
				int b=minimum(BCTar[byteColorCVtoIntJava(blue)],BCRef,InvHistoCumulBRef);		
				pixel[0]=b>256?(byte)255:(byte)b;
				pixel[1]=b>256?(byte)255:(byte)g;
				pixel[2]=b>256?(byte)255:(byte)r;				
				result.put(i, j, pixel);
			}
		}
	}
	public static Mat TextureMatching(Mat imRef,Mat imTar,Mat result,int n)
	{
		List<Mat> pyramidRef=new ArrayList<>();
		List<Mat> pyramidTar=new ArrayList<>();
		List<Mat> gaussRef=new ArrayList<>();
		List<Mat> gaussTar=new ArrayList<>();
		Mat tempRef=imRef.clone();
		Mat tempTar=imTar.clone();
		createLaplacianPyramid(tempRef, n, pyramidRef,gaussRef);
		//createLaplacianPyramid(tempTar, n, pyramidTar,gaussTar);	
		MatchingHistogram(imRef, imTar, imTar);
		int i=0;		
		createLaplacianPyramid(tempTar, n, pyramidTar,gaussTar);
		while(i<n)
		{
			//result=new Mat();
			MatchingHistogram(pyramidRef.get(i),pyramidTar.get(i), pyramidTar.get(i));
			//result=pyramidTar.get(i).clone();
			i++;
		}
		System.out.println("CollapsePyramid:"+gaussTar.size()+"/"+pyramidTar.size());
		result=collapsePyramid(pyramidTar,gaussTar);
		MatchingHistogram(imRef, result, result);
		
		return result;
	}
	public static void EqualHistogram(Mat im,Mat result)
	{
		int[] RRef=new int[256];int[] RCRef=new int[256];
		int[] GRef=new int[256];int[] GCRef=new int[256];
		int[] BRef=new int[256];int[] BCRef=new int[256];
		int N=im.cols()*im.rows();
		HistogrammeRGB(im, RRef, GRef, BRef);
		HistogrammeCumuleRGB(im, RRef, GRef, BRef,RCRef,GCRef,BCRef,N);
		byte[] pixel=new byte[3];
		//System.out.println(im);System.out.println(result);
		for(int i=0;i<im.rows();i++)
		{
			for(int j=0;j<im.cols();j++)
			{
				im.get(i, j,pixel);
				double r=RCRef[byteColorCVtoIntJava(pixel[2])]*255/N;
				double g=GCRef[byteColorCVtoIntJava(pixel[1])]*255/N;
				double b=BCRef[byteColorCVtoIntJava(pixel[0])]*255/N;
				result.put(i, j, new double[]{b,g,r});
			}
		}
	}
	public static void LaplacianPyramid(Mat src,Mat dest,List<Mat> gauss)
	{		
		Mat temp=src.clone();		
		Imgproc.pyrDown(temp, dest);
		Imgproc.pyrUp(dest, dest,temp.size());
		gauss.add(dest.clone());//we will use it to collapse-pyramid		
		//temp=dest.clone();		
		Core.subtract(src, dest, dest);
		//Core.add(dest, temp, dest);//Si on veut retrouver l'image originale Gi 		
	}
	public static Mat collapsePyramid(List<Mat> pyramid,List<Mat>gauss)
	{
		/*Si Core.add dans LaplacianPyramid
		 * alors i>0
		 * on ne fait que imgproc.pyrUp
		 * Sinon i>=0 et Core.add
		 * NB:Si les 2 images (Ref et Tar sont très différents => il vaut mieux utiliser Imgproc.pyrUp sinon Core.add
		 */
		int i=pyramid.size()-1;
		Mat temp=new Mat();		
		while(i>=0)
		{
			//Imgproc.pyrUp(pyramid.get(i), temp,pyramid.get(i-1).size());			
			Core.add(pyramid.get(i), gauss.get(i), temp);
			//
			i--;
		}
		System.out.println("valeur de i="+i);
		System.out.println(temp);
		return temp;
	}
	public static void createLaplacianPyramid(Mat dest,int n,List<Mat>pyramid,List<Mat>gauss)
	{
		int i=0;
		Mat temp=dest.clone();
		while(i<n)
		{			
			LaplacianPyramid(temp, dest,gauss);
			pyramid.add(dest.clone());			
			Imgproc.pyrDown(temp, temp);			
			i++;
		}
	}
	public static double convertDouble(double x)
	{		
		return ((int)(x*100000000))/100000000.;
	}
	public static int minimum(int value,int[] cumul,Hashtable<Integer, Integer>inv_cumul)
	{
		int[] temp=new int[256];
		Hashtable<Integer, Integer>tempHash=new Hashtable<>();
		for(int i=0;i<256;i++)
		{
			temp[i]=Math.abs(value-cumul[i]);
			tempHash.put(temp[i], cumul[i]);
		}
		//trions temp pour avoir la minimal
		int a=0;
		for(int i=0;i<255;i++)
		{
			for(int j=i+1;j<256;j++)
			{
				if(temp[i]>temp[j])
				{
					a=temp[j];
					temp[j]=temp[i];
					temp[i]=a;
				}
			}
		}
		//
		return inv_cumul.get(tempHash.get(temp[0]));
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
	public static Color AverageColor()
	{
		Color c=null;
		int temp;
		int tempR=0;
		int tempG=0;
		int tempB=0;
		try 
		{
			BufferedImage image=ImageIO.read(new File("C:\\Users\\ralambomahay1\\Downloads\\stage\\twoshot_data_input\\book_leather_red\\flash.png"));
			//BufferedImage image=ImageIO.read(new File("C:\\Users\\ralambomahay1\\Downloads\\Java_workspace\\newGit\\Data\\book_black_input_flash.jpg"));
			for(int i=0;i<image.getHeight();i++)
			{
				for(int j=0;j<image.getWidth();j++)
				{
					temp=image.getRGB(j, i);
					c=new Color(temp);
					tempR+=c.getRed();
					tempG+=c.getGreen();
					tempB+=c.getBlue();
				}
			}
			tempR/=image.getHeight()*image.getWidth();
			tempG/=image.getHeight()*image.getWidth();
			tempB/=image.getHeight()*image.getWidth();
			c=new Color(tempR,tempG,tempB);
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			System.out.println("AverageColor erreur:"+e.getMessage());
		}
		return c;
	}
	public static int[] XY(int[] x,int[] y)
	{
		return new int[]{y[0]-x[0],y[1]-x[1],y[2]-x[2]};
	}
	public static double[] normalize(int[] x)
	{
		double norm=Math.sqrt(x[0]*x[0]+x[1]*x[1]+x[2]*x[2]);
		return new double[]{x[0]/norm,x[1]/norm,x[2]/norm};
	}
	public static double[] normalize(double[] x)
	{
		double norm=Math.sqrt(x[0]*x[0]+x[1]*x[1]+x[2]*x[2]);
		return new double[]{x[0]/norm,x[1]/norm,x[2]/norm};
	}
	public static double dot(double[] x,double[] y)
	{
		if(x.length==2)return x[0]*y[0]+x[1]*y[1];
		return x[0]*y[0]+x[1]*y[1]+x[2]*y[2];
	}
	public static double dot(int[] x,int[] y)
	{
		return x[0]*y[0]+x[1]*y[1]+x[2]*y[2];
	}
	public static double[] calculeL(int[]x,int[]y)
	{
		int[] xy=XY(x,y);
		return normalize(xy);
	}
	public static double[] addXY(double[]x,double[] y)
	{
		return new double[]{x[0]+y[0],x[1]+y[1],x[2]+y[2]};
	}
	public static double[] calculeHn(double[] h,double[] n,SimpleMatrix r)
	{		
		SimpleMatrix ha=new SimpleMatrix(new double[][]{{h[0]},{h[1]},{h[2]}});
		SimpleMatrix first=ha.plus(r.mult(ha)).plus(1);
		SimpleMatrix second=r.mult(r.mult(ha)).mult(new SimpleMatrix(new double[][]{{n[2]+1}}));
		return first.elementDiv(second).getMatrix().data;
		/*SimpleMatrix ha=new SimpleMatrix(new double[][]{{1},{0},{1}});
		SimpleMatrix ra=new SimpleMatrix(new double[][]{{0,0,1},{0,0,-1},{-1,1,0}});
		SimpleMatrix first=ha.plus(ra.mult(ha)).plus(1);
		SimpleMatrix second=ra.mult(ra.mult(ha)).mult(new SimpleMatrix(new double[][]{{3}}));
		first.print();
		second.print();*/
	}
	public static double[] div(double[] xy,double e)
	{
		return new double[]{xy[0]/e,xy[1]/e,xy[2]/e};
	}
	public static double[] calculeHnpW(SimpleMatrix m,double x,double y)
	{
		SimpleMatrix xy=new SimpleMatrix(new double[][]{
			{x},
			{y}
		});
		SimpleMatrix temp=m.mult(xy);
		double[] res=temp.getMatrix().data;
		return new double[]{res[0],res[1],1};
	}
	public static Mat sobel(Mat image)
	{
		Mat result=new Mat();
		Mat x=new Mat();Mat xx=new Mat();
		Mat y=new Mat();Mat yy=new Mat();
		Imgproc.Sobel(image, x, -1, 1, 0);
		Imgproc.Sobel(image, y, -1, 0, 1);
		x.convertTo(xx, CvType.CV_32FC3);
		y.convertTo(yy, CvType.CV_32FC3);
		Core.magnitude(xx, yy, result);
		result.convertTo(result, CvType.CV_8UC3);
		return result;
	}
	public static Mat zoomIn(Mat image,int factor)
	{
		int ligne=image.rows()/factor;
		int col=image.cols()/factor;
		Mat result=new Mat(ligne,col,CvType.CV_8UC3);
		byte[] newP=new byte[3];
		byte[] P=new byte[3];
		int i_=0;
		int j_=0;
		int r=0,g=0,b=0;
		for(int i=0;i<image.rows();)
		{
			for(int j=0;j<image.cols();)
			{
				for(int k=0;k<factor;k++)
				{
					for(int z=0;z<factor;z++)
					{
						image.get(i+k, j+z,P);
						b+=byteColorCVtoIntJava(P[0]);
						g+=byteColorCVtoIntJava(P[1]);
						r+=byteColorCVtoIntJava(P[2]);
					}
				}
				b/=factor*factor;b=b>256?255:b;
				g/=factor*factor;g=g>256?255:g;
				r/=factor*factor;r=r>256?255:r;
				/*image.get(i, j,newP);
				if(Math.abs(b-byteColorCVtoIntJava(newP[0]))>2 && Math.abs(g-byteColorCVtoIntJava(newP[1]))>2 && Math.abs(r-byteColorCVtoIntJava(newP[2]))>2)
				{
					b=byteColorCVtoIntJava(newP[0]);
					g=byteColorCVtoIntJava(newP[1]);
					r=byteColorCVtoIntJava(newP[2]);
				}*/
				result.put(i_, j_, new byte[]{(byte)b,(byte)g,(byte)r});
				j+=factor;
				j_++;
			}
			i+=factor;
			i_++;
			j_=0;
		}
		return result;
	}
}
