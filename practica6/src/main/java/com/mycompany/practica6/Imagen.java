package com.mycompany.practica6;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;



public class Imagen extends JPanel{
    private BufferedImage imagen = null;
    public String fichero = "";    
    public Mat mat;
    public Integer umbral;
    
    public Imagen(){
        nu.pattern.OpenCV.loadShared();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
              
    }
    
    
    public int abrirImagen(String fichero){
        
        //Abrimos la imagen 
        this.fichero = fichero;
        try {
            imagen = ImageIO.read(new File(fichero));
            
        } catch (IOException ex) {
            Logger.getLogger(Imagen.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        }        
        
        //Comprobamos que la imagen sea menor a 1024x768
        if(imagen.getWidth()<=1024 && imagen.getHeight()<=768){  
            
            mat = new Mat(imagen.getHeight(), imagen.getWidth(), CvType.CV_8UC3);
            byte[] data = ((DataBufferByte) imagen.getRaster().getDataBuffer()).getData();
            mat.put(0, 0, data);
            
            this.setSize(new Dimension(imagen.getWidth() ,imagen.getHeight()));
            Graphics g=getGraphics();
            paintComponent(g);            
        }else{
            JOptionPane.showMessageDialog(null, "La imagen debe tener una resolución inferior a 1024x768", "Error", JOptionPane.INFORMATION_MESSAGE);
        }      
        
        return 0;
    }
    
    public void getUmbral(Integer umbral){
        this.umbral = umbral;
        Mat umbralizada = umbralizar(mat,umbral);
        imagen = toBufferedImage(umbralizada);
        Graphics g=getGraphics();
        paintComponent(g);
    }
    
    private Mat umbralizar(Mat imagen_original, Integer umbral) {
        // crear dos imágenes en niveles de gris con el mismo
        // tamaño que la original
        Mat imagenGris = new Mat(imagen_original.rows(),
        imagen_original.cols(),
        CvType.CV_8U);
        
        Mat imagenUmbralizada = new Mat(imagen_original.rows(),
        imagen_original.cols(),
        CvType.CV_8U);
        
        // convierte a niveles de grises la imagen original
        Imgproc.cvtColor(imagen_original,
        imagenGris,
        Imgproc.COLOR_BGR2GRAY);
        // umbraliza la imagen:
        // - píxeles con nivel de gris > umbral se ponen a 1
        // - píxeles con nivel de gris <= umbra se ponen a 0
        Imgproc.threshold(imagenGris,
        imagenUmbralizada,
        umbral,
        255,
        Imgproc.THRESH_BINARY);
        // se devuelve la imagen umbralizada
        return imagenUmbralizada;
    }

    private BufferedImage toBufferedImage(Mat m) {
    if (!m.empty()) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }    
        return null;
    }
    
    public void guardarImagen(File fichero){
        try {
            ImageIO.write(imagen, "jpg", fichero);
        } catch (IOException ex) {
            Logger.getLogger(Imagen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
               
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(imagen, 0, 0, null);             
    }
}