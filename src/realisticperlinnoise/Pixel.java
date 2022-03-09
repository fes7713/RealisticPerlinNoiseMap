/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package realisticperlinnoise;

import java.awt.Color;

/**
 *
 * @author fes77
 */
public class Pixel {
    Color color;
    int size;
    double noise;

    public static int DEFAULT_SIZE = 1;
    
    public Pixel(Color color, double noise) {
        this.color = color;
        this.noise = noise;
        size = DEFAULT_SIZE;
    }

//    Color getColor()
//    {
//        return color;
//    }
    
    void setColor(Color color) {
        this.color = color;
    }
    
    void copyColor(Pixel p)
    {
        color = p.color;
    }
    
    public Color getColor()
    {
        return color;
    }
    public double getNoise()
    {
        return noise;
    }
}
