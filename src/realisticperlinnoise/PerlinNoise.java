package realisticperlinnoise;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import realisticperlinnoise.FastNoise.CellularDistanceFunction;
import realisticperlinnoise.FastNoise.CellularReturnType;
import realisticperlinnoise.FastNoise.FractalType;
import realisticperlinnoise.FastNoise.Interp;
import realisticperlinnoise.FastNoise.NoiseType;


/**
 *
 * @author fes77
 */
public class PerlinNoise implements MouseWheelListener, KeyListener{
    Pixel[][] grid;
    int frequencyBase = 1;
    JFrame frame;
    FastNoise fn = new FastNoise();
    private Color[] colorLevel;
    private Color[][] darkerColors;
    private Color[][] brighterColors;
    private final int nColorLevelPower = 8;
    private final int nColorLevel = (int)Math.pow(2, 8);
    
//    public static final int DEFAULT_WIDTH = 12480 / Pixel.DEFAULT_SIZE;
//    public static final int DEFAULT_HEIGHT = 7020 / Pixel.DEFAULT_SIZE;
    public static final int DEFAULT_WIDTH = 3840 / Pixel.DEFAULT_SIZE;
    public static final int DEFAULT_HEIGHT = 2160 / Pixel.DEFAULT_SIZE;
    private final int Trench = (int)(0.1f * nColorLevel); 
    private final int DeepWater = (int)(0.35f * nColorLevel); 
    private final int ShallowWater = (int)(0.4f * nColorLevel);   
    private final int Sand = (int)(0.5f * nColorLevel); 
    private final int Grass = (int)(0.7f * nColorLevel); 
    private final int Forest = (int)(0.8f * nColorLevel); 
    private final int Rock = (int)(0.9f * nColorLevel); 
    private final int Snow = (int)(1 * nColorLevel); 
    
    
    float shift = 30;
    float amplitude = 35;
    int shiftX = 0;
    int shiftY = 0;
    float zoom = 1;
    

    private float frequency32th = 0.03125f;
    private float frequency16th = 0.0625f;
    private float frequency8th = 0.125f;
    private float frequency4th = 0.25f;
    private float frequency2nd = 0.5f; 
    private float frequency = 1;
    private float frequency2 = 2;
    private float frequency4 = 4;
    private float frequency8 = 4;
    private float frequency16 = 16;
    private float frequency32 = 32;
    private float z = 1;
    
//    private int frequency4th = 1024;
//    private int frequency2nd = 512; 
//    private int frequency = 256;
//    private int frequency2 = 128;
//    private int frequency4 = 64;
//    private int frequency8 = 32;
    private static double FACTOR = Math.sqrt(0.73);
    
    public PerlinNoise(JFrame frame, int shiftX, int shiftY, int zoom) 
    {
        fn.SetNoiseType(NoiseType.CubicFractal);
        fn.SetInterp(Interp.Quintic);
        grid = new Pixel[DEFAULT_HEIGHT][DEFAULT_WIDTH + 1];
        for(int i = 0; i < DEFAULT_HEIGHT; i++)
        {
            for(int j = 0; j < DEFAULT_WIDTH + 1; j++)
            {
                grid[i][j] = new Pixel(Color.WHITE, 0);
            }
        }
        this.frame = frame;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
        this.zoom = zoom;
        colorInit();
    }
    
    public PerlinNoise() 
    {
        fn.SetNoiseType(NoiseType.CubicFractal);
        fn.SetInterp(Interp.Quintic);
        grid = new Pixel[DEFAULT_HEIGHT][DEFAULT_WIDTH + 1];
        for(int i = 0; i < DEFAULT_HEIGHT; i++)
        {
            for(int j = 0; j < DEFAULT_WIDTH + 1; j++)
            {
                double noise = fn.GetNoise(i * frequencyBase, j * frequencyBase);    
                grid[i][j] = new Pixel(Color.WHITE, 0);
            }
        }
        colorInit();
    }
    
    public PerlinNoise(JFrame frame) 
    {
        fn.SetNoiseType(NoiseType.CubicFractal);
        fn.SetInterp(Interp.Quintic);
        this.frame = frame;
        grid = new Pixel[DEFAULT_HEIGHT][DEFAULT_WIDTH + 1];
        for(int i = 0; i < DEFAULT_HEIGHT; i++)
        {
            for(int j = 0; j < DEFAULT_WIDTH + 1; j++)
            {
                grid[i][j] = new Pixel(Color.WHITE, 0);
            }
        }
        colorInit();
    }

    @Override
    public String toString() {
        return "PerlinNoise{" + "shift=" + shift + ", amplitude=" + amplitude + ", frequency32th=" + frequency32th + ", frequency16th=" + frequency16th + ", frequency8th=" + frequency8th + ", frequency4th=" + frequency4th + ", frequency2nd=" + frequency2nd + ", frequency=" + frequency + ", frequency2=" + frequency2 + ", frequency4=" + frequency4 + ", frequency8=" + frequency8 + '}';
    }
    
    
    private void colorInit()
    {
        colorLevel = new Color[nColorLevel];
        darkerColors = new Color[10][nColorLevel];
        brighterColors = new Color[10][nColorLevel];
        
        for(int i = 0; i < nColorLevel; i++)
        {
            Color color;

            if(i < Trench)
            {
                color = interpolateColor(GameColor.TrenchColor, GameColor.DeepColor, ((i - 0) << nColorLevelPower) / (Trench - 0));
            }
            else if(i < DeepWater)
            {
                color = interpolateColor(GameColor.DeepColor, GameColor.ShallowColor, ((i - Trench) << nColorLevelPower)/ (DeepWater - Trench));
            }
            else if(i < ShallowWater)
            {
                color = interpolateColor(GameColor.ShallowColor, GameColor.ShoreColor, ((i - DeepWater) << nColorLevelPower)/ (ShallowWater - DeepWater));
            }
            else if(i < ShallowWater+0.02f)
            {
                color = GameColor.CityColor;
            }
            else if(i < Sand)
            {
                color = interpolateColor(GameColor.SandColor, GameColor.CrayColor, ((i - ShallowWater) << nColorLevelPower)/ (Sand - ShallowWater));
            }
            else if(i < Forest)
            {
                color = interpolateColor(GameColor.GrassColor, GameColor.ForestColor, ((i - Sand) << nColorLevelPower)/ (Forest - Sand));
            }
            else if(i < Grass)
            {
                color = GameColor.ForestColor;
            }
            else if(i < Rock)
            {
                color = interpolateColor(GameColor.RockColor, GameColor.SnowColor, ((i - Grass) << nColorLevelPower)/ (Rock - Grass));
            }
            else
            {
                color = GameColor.SnowColor;
            }
            colorLevel[i] = color;
        }
        
        for(int i = 0; i < nColorLevel; i++)
        {
            for(int j = 0; j < 10; j++)
            {
                darkerColors[j][i] = new Color(colorLevel[i].getRed(), colorLevel[i].getGreen(), colorLevel[i].getBlue());
                for(int p = 0; p < j + 1; p++)
                    darkerColors[j][i] = new Color((int)(darkerColors[j][i].getRed()* FACTOR), (int)(darkerColors[j][i].getGreen()* FACTOR), (int)(darkerColors[j][i].getBlue() * FACTOR));
            }
        }
            
        
        for(int i = 0; i < nColorLevel; i++)
        {
            for(int j = 0; j < 10; j++)
            {
                brighterColors[j][i] = new Color(colorLevel[i].getRed(), colorLevel[i].getGreen(), colorLevel[i].getBlue());
                for(int p = 0; p < j + 1; p++)
                    brighterColors[j][i] = new Color((int)Math.min(brighterColors[j][i].getRed() / FACTOR, 255), (int)Math.min(brighterColors[j][i].getGreen() / FACTOR, 255), (int)Math.min(brighterColors[j][i].getBlue() / FACTOR, 255));
            }
        }
    }
    
    public void draw(Graphics2D g2d, int shiftX, int shiftY, float zoom)
    {
//        update(shiftY, shiftX, zoom);
//        for(int i = 0; i < DEFAULT_HEIGHT; i++)
//        {
//            for(int j = 0; j < DEFAULT_WIDTH; j++)
//            {
//                grid[i][j].draw(g2d);
//            }
//        }

//        Color[][] image = new Color[DEFAULT_WIDTH][DEFAULT_HEIGHT];
//        BufferedImage bufferedImage = new BufferedImage(image.length, image[0].length,
//                BufferedImage.TYPE_INT_RGB);
//
//        // Set each pixel of the BufferedImage to the color from the Color[][].
//        for (int i = 0; i < DEFAULT_HEIGHT; i++) {
//            for (int j = 0; j < DEFAULT_WIDTH; j++) {
//                bufferedImage.setRGB(j, i, grid[i][j].getColor().getRGB());
//            }
//        }
//        
//
//        g2d.drawImage(bufferedImage, 0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT, null);
        
        // Flexible but slow
        Color[][] image = new Color[DEFAULT_WIDTH][DEFAULT_HEIGHT];
        BufferedImage bufferedImage = new BufferedImage(image.length * Pixel.DEFAULT_SIZE, image[0].length * Pixel.DEFAULT_SIZE,
                BufferedImage.TYPE_INT_RGB);

        // Set each pixel of the BufferedImage to the color from the Color[][].
        for (int i = 0; i < DEFAULT_HEIGHT; i++) {
            for (int j = 0; j < DEFAULT_WIDTH; j++) {
                for(int t = 0; t < Pixel.DEFAULT_SIZE; t++)
                    for(int s = 0; s < Pixel.DEFAULT_SIZE; s++)
                        bufferedImage.setRGB(
                                j * Pixel.DEFAULT_SIZE + s, 
                                i * Pixel.DEFAULT_SIZE + t, 
                                grid[i][j].getColor().getRGB());
            }
        }

        g2d.drawImage(bufferedImage, 0, 0, DEFAULT_WIDTH * Pixel.DEFAULT_SIZE, DEFAULT_HEIGHT * Pixel.DEFAULT_SIZE, null);
    }
    
    public void update(int shiftX, int shiftY, float zoom)
    {
//        System.out.println(toString());
        update_Limited(shiftX, shiftY, zoom, 0, DEFAULT_WIDTH, 0, DEFAULT_HEIGHT);
    }
    
    public void update_Limited(int shiftX, int shiftY, float zoom, int width_start, int width_end, int height_start, int height_end)
    {
        float x;
        float y;
        int counter =0;
        for(int i = height_start; i < height_end; i++)
        {
            if((height_end - height_start) * counter / 100f < i)
                System.out.println(counter++);
            for(int j = width_start; j < width_end + 1; j++)
            {
                
                x = (j - (shiftX / Pixel.DEFAULT_SIZE)) / zoom;
                y = (i - (shiftY / Pixel.DEFAULT_SIZE)) / zoom;
                

                double  noise = fn.GetNoise(y * frequencyBase / 4, x * frequencyBase / 4)/frequency4th;
                        noise += fn.GetNoise(y * frequencyBase / 2, x * frequencyBase / 2)/frequency2nd;
                        noise += fn.GetNoise(y * frequencyBase, x * frequencyBase)/frequency;
                        noise += fn.GetNoise(y * frequencyBase *  2, x * frequencyBase * 2)/frequency2;
                        noise += fn.GetNoise(y * frequencyBase *  4, x * frequencyBase * 4)/frequency4;
                        noise += fn.GetNoise(y * frequencyBase *  8, x * frequencyBase * 8)/frequency8;
                        noise += fn.GetNoise(y * frequencyBase *  16, x * frequencyBase * 16)/frequency16;
                        noise += fn.GetNoise(y * frequencyBase *  32, x * frequencyBase * 32)/frequency32;
                        noise += fn.GetNoise(y * frequencyBase *  64, x * frequencyBase * 64)/frequency32/2;
                        noise += fn.GetNoise(y * frequencyBase *  128, x * frequencyBase * 128)/frequency32/4;
                        noise += fn.GetNoise(y * frequencyBase *  256, x * frequencyBase * 256)/frequency32/8;
                        noise += fn.GetNoise(y * frequencyBase *  512, x * frequencyBase * 512)/frequency32/16;

                
                noise = noise * amplitude / 100f  + shift/100f;
                noise = Math.min(0.99, Math.max(0, noise));
                
                grid[i][j].color = colorLevel[(int)(noise * 256)];
                grid[i][j].noise = noise * 256;
                

//                leftNoise = noise;
            }
            
        }
        for(int i = height_start; i < height_end; i++)
        {
            for(int j = width_start; j < width_end; j++)
            {
                // shadows
                double rightNoise = grid[i][j + 1].getNoise();
                double noise = grid[i][j].getNoise();
                if((rightNoise) - (noise) > 14f / zoom)
                    grid[i][j].color = brighterColors[9][(int)grid[i][j].noise];
                else if((rightNoise) - (noise) > 10f / zoom)
                    grid[i][j].color = brighterColors[8][(int)grid[i][j].noise];
                else if((rightNoise) - (noise) > 8f / zoom)
                    grid[i][j].color = brighterColors[7][(int)grid[i][j].noise];
                else if((rightNoise) - (noise) > 4.5f / zoom)
                    grid[i][j].color = brighterColors[6][(int)grid[i][j].noise];
                else if((rightNoise) - (noise) > 3.5f / zoom)
                    grid[i][j].color = brighterColors[5][(int)grid[i][j].noise];
                else if((rightNoise) - (noise) > 3f / zoom)
                    grid[i][j].color = brighterColors[4][(int)grid[i][j].noise];
                else if((rightNoise) - (noise) > 2f / zoom)
                    grid[i][j].color = brighterColors[3][(int)grid[i][j].noise];
                else if((rightNoise) - (noise) > 1.5f / zoom)
                    grid[i][j].color = brighterColors[2][(int)grid[i][j].noise];
                else if((rightNoise) - (noise) > 0.5f / zoom)
                    grid[i][j].color = brighterColors[1][(int)grid[i][j].noise];
                else if((rightNoise) - (noise) > 0.4f / zoom)
                    grid[i][j].color = brighterColors[0][(int)grid[i][j].noise];
                
                if((noise) - (rightNoise) > 7f / zoom)
                    grid[i][j].color = darkerColors[9][(int)grid[i][j].noise];
                else if((noise) - (rightNoise) > 5f / zoom)
                    grid[i][j].color = darkerColors[8][(int)grid[i][j].noise];
                else if((noise) - (rightNoise) > 3.5f / zoom)
                    grid[i][j].color = darkerColors[7][(int)grid[i][j].noise];
                else if((noise) - (rightNoise) > 2.5f / zoom)
                    grid[i][j].color = darkerColors[6][(int)grid[i][j].noise];
                else if((noise) - (rightNoise) > 1.5f / zoom)
                    grid[i][j].color = darkerColors[5][(int)grid[i][j].noise];
                else if((noise) - (rightNoise) > 1.2f / zoom)
                    grid[i][j].color = darkerColors[4][(int)grid[i][j].noise];
                else if((noise) - (rightNoise) > 1f / zoom)
                    grid[i][j].color = darkerColors[3][(int)grid[i][j].noise];
                else if((noise) - (rightNoise) > 0.8f / zoom)
                    grid[i][j].color = darkerColors[2][(int)grid[i][j].noise];
                else if((noise) - (rightNoise) > 0.5f / zoom)
                    grid[i][j].color = darkerColors[1][(int)grid[i][j].noise];
                else if((noise) - (rightNoise) > 0.3f / zoom)
                    grid[i][j].color = darkerColors[0][(int)grid[i][j].noise];
                
                
//                if((noise * 256) - (rightNoise * 256) > 0.5f / zoom)
//                {
//                    Color color = grid[i][j].getColor();
//                    grid[i][j] = new Pixel(new Color(color.getRed(), color.getGreen(), color.getBlue()).darker(), 0);
//                    if((noise * 256) - (rightNoise * 256) > 1f / zoom)
//                    {
//                        grid[i][j] = new Pixel(grid[i][j].getColor().darker(), 0);
//                        if((noise * 256) - (rightNoise * 256) > 1.5f / zoom)
//                        {
//                            grid[i][j] = new Pixel(grid[i][j].getColor().darker(), 0);
//                            if((noise * 256) - (rightNoise * 256) > 3.5f / zoom)
//                            {
//                                grid[i][j] = new Pixel(grid[i][j].getColor().darker(), 0);
//                            }
//                        }
//                    }
//                    
//                }
                

                
                
//                if((rightNoise * 256) - (noise * 256)  > 0.5f / zoom)
//                {
//                    Color color = grid[i][j].getColor();
//                    grid[i][j] = new Pixel(new Color(color.getRed(), color.getGreen(), color.getBlue()).brighter(), 0);
//                    if((rightNoise * 256) - (noise * 256)  > 2f / zoom)
//                    {
//                        grid[i][j] = new Pixel(grid[i][j].getColor().brighter(), 0);
//                        if((rightNoise * 256) - (noise * 256)  > 3.5f / zoom)
//                        {
//                            grid[i][j] = new Pixel(grid[i][j].getColor().brighter(), 0);
//                        }
//                    }
//                    
//                }
            }

        }
        
        
//        for(int i = height_start; i < height_end - 1; i++)
//        {
//            for(int j = width_start; j < width_end - 1; j++)
//            {
//                
//               if(grid[i][j].color != grid[i + 1][j].color || grid[i][j].color != grid[i][j + 1].color)
//               {
//                   grid[i][j] = new Pixel(Color.WHITE, new Vector2(j, i));
//               }
//            }
//            
//        }
       
    }
    
    private Color interpolateColor(Color startC, Color endC, int ratio)
    {
        try{
        float iRatio = nColorLevel - ratio;
        return new Color(
                (int)(startC.getRed() * iRatio + endC.getRed() * ratio) >> nColorLevelPower,
                (int)(startC.getGreen()* iRatio + endC.getGreen() * ratio) >> nColorLevelPower,
                (int)(startC.getBlue()* iRatio + endC.getBlue() * ratio) >> nColorLevelPower
        );
        }
        catch(Exception e)
        {
            System.out.println("Error : interpolateColor()" + ratio);
            return Color.WHITE;
        }
    }
    
    public void addShiftY(int shiftX, int shiftY, float zoom)
    {
//        shiftY >>= pixelSizeShift;
        shiftY /= Pixel.DEFAULT_SIZE;
        int diffY = shiftY - this.shiftY;
        
        if(diffY == 0)
        {
            
        }
//        else if(this.zoom != zoom)
//        {
//            // Only Y updates
//            System.out.println("Zoom update");
//            update_Limited(shiftX, shiftY, zoom, 0, DEFAULT_WIDTH, 0, DEFAULT_HEIGHT);
//            this.zoom = zoom;
//            setShiftY(shiftY);
//        }
        // Shifting Upwards
        else if(diffY < 0)
        {
            System.out.println("Shift Upwards");
            diffY *= -1;
            for(int i = 0; i < DEFAULT_HEIGHT - diffY; i++)
            {
                for(int j = 0; j < DEFAULT_WIDTH; j++)
                {
                    grid[i][j].copyColor(grid[i + diffY][j]);
                }
            }
            // Text Blue Draw
//            for(int i = DEFAULT_HEIGHT - diffY; i < DEFAULT_HEIGHT; i++)
//            {
//                for(int j = 0; j < DEFAULT_WIDTH; j++)
//                {
//                    grid[i][j].setColor(Color.BLUE);
//                }
//            }
            
//            update_Limited(shiftX, shiftY << pixelSizeShift, zoom, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT - diffY, DEFAULT_HEIGHT);
            update_Limited(shiftX, shiftY * Pixel.DEFAULT_SIZE, zoom, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT - diffY, DEFAULT_HEIGHT);

        }
        // Shifting Downwards
        else 
        {
            System.out.println("Shift Downwards");
            for(int i = DEFAULT_HEIGHT - 1; i >= diffY; i--)
            {
                for(int j = 0; j < DEFAULT_WIDTH; j++)
                {
                    grid[i][j].copyColor(grid[i - diffY][j]);
                }
            }
            
            // Text Blue Draw
//            for(int i = 0; i < diffY; i++)
//            {
//                for(int j = 0; j < DEFAULT_WIDTH; j++)
//                {
//                    grid[i][j].setColor(Color.BLUE);
//                }
//            }
            
//            update_Limited(shiftX, shiftY << pixelSizeShift, zoom, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT - diffY, DEFAULT_HEIGHT);
            update_Limited(shiftX, shiftY * Pixel.DEFAULT_SIZE, zoom, 0, DEFAULT_WIDTH, 0, diffY);
        }
        setShiftY(shiftY);
    }
    
    public void addShiftX(int shiftX, int shiftY, float zoom)
    {
        shiftX /= Pixel.DEFAULT_SIZE;
//        shiftX >>= pixelSizeShift;
        int diffX = shiftX - this.shiftX;
        
        if(diffX == 0)
        {
            // Only Y updates
        }
//        else if(this.zoom != zoom)
//        {
//            setShiftX(shiftX);
////            update_Limited(shiftX, shiftY, zoom, 0, DEFAULT_WIDTH, 0, DEFAULT_HEIGHT);
//        }
        // Shifting left
        else if(diffX < 0)
        {
            System.out.println("Left");
            diffX *= -1;
            for(int i = 0; i < DEFAULT_WIDTH - diffX; i++)
            {
                for(int j = 0; j < DEFAULT_HEIGHT; j++)
                {
                    grid[j][i].copyColor(grid[j][i + diffX]);
                }
            }
            // Text Blue Draw
//            for(int i = DEFAULT_WIDTH - diffX; i < DEFAULT_WIDTH; i++)
//            {
//                for(int j = 0; j < DEFAULT_HEIGHT; j++)
//                {
//                    grid[j][i].setColor(Color.BLUE);
//                }
//            }
//            update_Limited(shiftX << pixelSizeShift, shiftY, zoom, DEFAULT_WIDTH - diffX, DEFAULT_WIDTH, 0, DEFAULT_HEIGHT);
            update_Limited(shiftX * Pixel.DEFAULT_SIZE, shiftY, zoom, DEFAULT_WIDTH - diffX, DEFAULT_WIDTH, 0, DEFAULT_HEIGHT);
        }
        // Shifting rigfht
        else 
        {
            System.out.println("Right");
            for(int i = DEFAULT_WIDTH - 1; i >= diffX; i--)
            {
                for(int j = 0; j < DEFAULT_HEIGHT; j++)
                {
                    grid[j][i].copyColor(grid[j][i - diffX]);
                }
            }
            // Text Blue Draw
//            for(int i = 0; i < diffX; i++)
//            {
//                for(int j = 0; j < DEFAULT_HEIGHT; j++)
//                {
//                    grid[j][i].setColor(Color.BLUE);
//                }
//            }
//            update_Limited(shiftX << pixelSizeShift, shiftY, zoom, DEFAULT_WIDTH - diffX, DEFAULT_WIDTH, 0, DEFAULT_HEIGHT);
            update_Limited(shiftX * Pixel.DEFAULT_SIZE, shiftY, zoom, 0, diffX, 0, DEFAULT_HEIGHT);
        }
        setShiftX(shiftX);
    }
    
    public float getFrequency4th()
    {
        return frequency4th;
    }
    
    public void setShift(float shift) {
        this.shift = shift;
    }

    public void setAmplitude(float amplitude) {
        this.amplitude = amplitude;
    }
    
    public void setFrequency32th(float frequency32th) {
        this.frequency32th = frequency32th * 256;
    }
    
    public void setFrequency16th(float frequency16th) {
        this.frequency16th = frequency16th * 256;
    }
    
    public void setFrequency8th(float frequency8th) {
        this.frequency8th = frequency8th * 256;
    }
    
    public void setFrequency4th(float frequency4th) {
        this.frequency4th = frequency4th * 256;
        System.out.println("4th: " + getFrequency4th());
    }
    
    public void setFrequency2nd(float frequency2nd) {
        this.frequency2nd = frequency2nd * 256;
    }
    
    public void setFrequency(float frequency) {
        this.frequency = frequency * 256;
    }

    public void setFrequency2(float frequency2) {
        this.frequency2 = frequency2 * 256;
    }

    public void setFrequency4(float frequency4) {
        this.frequency4 = frequency4 * 256;
    }

    public void setFrequency8(float frequency8) {
        this.frequency8 = frequency8 * 256;
    }

    public void setFrequency16(float frequency16) {
        this.frequency16 = frequency16 * 256;
    }

    public void setFrequency32(float frequency32) {
        this.frequency32 = frequency32 * 256;
    }
    

    public void setZ(float z) {
        this.z = z;
        System.out.println("Z " + z);
    }
    
    public void setNoiseType(NoiseType noiseType)
    {
        fn.SetNoiseType(noiseType);
        System.out.println("Noise Type Changed " + noiseType);
    }
    
    public void setNoiseType(Interp interp)
    {
        fn.SetInterp(interp);
        System.out.println("Interpolation Changed " + interp);
    }
    
    public void setFractalType(FractalType fractalType)
    {
        fn.SetFractalType(fractalType);
        System.out.println("Fractal Type Changed " + fractalType);
    }
    
    public void setCellularDistanceFunction(CellularDistanceFunction cellularDistanceFunction)
    {
        fn.SetCellularDistanceFunction(cellularDistanceFunction);
        System.out.println("Cellular Distance Function Changed " + cellularDistanceFunction);
    }
    
    public void setCellularReturnType(CellularReturnType cellularReturnType)
    {
        fn.SetCellularReturnType(cellularReturnType);
        System.out.println("Cellular Return Type Changed " + cellularReturnType);
    }
    
    public void setBaseFrequency(int n)
    {
        frequencyBase = n;
    }
    
    public void addBaseFrequency(int n)
    {
        frequencyBase += n;
    }
    
    public void setShiftX(int n)
    {
        shiftX = n;
    }
    
    public void setShiftY(int n)
    {
        shiftY = n;
    }
    
    public void setZoom(float n)
    {
        zoom = n;
    }
    
    public int getShiftX()
    {
        return shiftX;
    }
    
    public int getShiftY()
    {
        return shiftY;
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        addBaseFrequency(e.getWheelRotation());
        frame.repaint();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true");
        
        
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(PerlinNoise.DEFAULT_HEIGHT * Pixel.DEFAULT_SIZE + 50, PerlinNoise.DEFAULT_WIDTH * Pixel.DEFAULT_SIZE));
        PerlinNoise p = new PerlinNoise(frame);
//        for(int i = 0; i < 4; i++)
//        {
//            for(int j = 0; j < 4; j++)
//            {
//                p.update(10240 * j, 5760 * i, 8);
//                p.saveImage("40K-" + i + "-" + j);
//            }
//        }
        
        p.update(0, 0, 4.8f);
        p.saveImage("13K2");
//        p.update(0, 10000/4, 4);
//        p.saveImage("10000p-2-1");
//        p.update(10000/4, 10000/4, 4);
//        p.saveImage("10000p-2-2");
        
        frame.addMouseWheelListener(p);
        frame.addKeyListener(p);
        JPanel panel = new JPanel()
        {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                p.update(p.getShiftX(), p.getShiftY(), 4);
                p.draw(g2d, p.getShiftX(), p.getShiftY(), 4);
            }
        };
//        panel.setBackground(Color.GRAY);
        panel.setLayout(new BorderLayout());
        frame.add(panel);
        frame.setVisible(true);

        
        JFrame controlFrame = new JFrame();
        controlFrame.setLayout(new GridLayout(1,1,1,1));
        controlFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        controlFrame.setSize(new Dimension(400, 600));
        
        JPanel controlePanel = new JPanel();
        controlePanel.setLayout(new GridLayout(1,2,1,1));
        
        JLabel shiftLabel = new JLabel("Shift");
        JLabel amplitudeLabel = new JLabel("Amplitude");
        JLabel shiftXLabel = new JLabel("Shift X");
        JLabel shiftYLabel = new JLabel("Shift Y");
        JLabel frequency32thLabel = new JLabel("Frequency 32th");
        JLabel frequency16thLabel = new JLabel("Frequency 16th");
        JLabel frequency8thLabel = new JLabel("Frequency 8th");
        JLabel frequency4thLabel = new JLabel("Frequency 4th");
        JLabel frequency2ndLabel = new JLabel("Frequency 2nd");
        JLabel frequencyLabel = new JLabel("Frequency 1");
        JLabel frequency2Label = new JLabel("Frequency 2");
        JLabel frequency4Label = new JLabel("Frequency 4");
        JLabel frequency8Label = new JLabel("Frequency 8");
        JLabel frequency16Label = new JLabel("Frequency 16");
        JLabel frequency32Label = new JLabel("Frequency 32");
        JLabel zLabel = new JLabel("z");
        
        JLabel shiftLabelNum = new JLabel("30");
        JLabel amplitudeLabelNum = new JLabel("35");
        JLabel shiftXLabelNum = new JLabel("0");
        JLabel shiftYLabelNum = new JLabel("0");
        JLabel frequency32thLabelNum = new JLabel("0.03");
        JLabel frequency16thLabelNum = new JLabel("0.06");
        JLabel frequency8thLabelNum = new JLabel("0.13");
        JLabel frequency4thLabelNum = new JLabel("0.25");
        JLabel frequency2ndLabelNum = new JLabel("0.5");
        JLabel frequencyLabelNum = new JLabel("1");
        JLabel frequency2LabelNum = new JLabel("2");
        JLabel frequency4LabelNum = new JLabel("4");
        JLabel frequency8LabelNum = new JLabel("8");
        JLabel frequency16LabelNum = new JLabel("16");
        JLabel frequency32LabelNum = new JLabel("32");
        JLabel zLabelNum = new JLabel("1");
        
        
        int sliderSize = 100;
        int tickSpacing = 50;
        float dividerMul = 8f;
        
        JSlider shiftSlider = new JSlider(JSlider.HORIZONTAL,
                                      1, sliderSize, sliderSize/3)
        {
            public void setValue(int value)
            {
                super.setValue(value);
                shiftLabelNum.setText("" + value);
                p.setShift(value);
                frame.repaint();
            }
        };
        
        shiftSlider.setMajorTickSpacing(tickSpacing);
        shiftSlider.setPaintTicks(true);
        shiftSlider.setPaintLabels(true);
        
        JSlider amplitudeSlider = new JSlider(JSlider.HORIZONTAL,
                                      1, sliderSize, (int)(sliderSize/2.5f))
        {
            public void setValue(int value)
            {
                super.setValue(value);
                amplitudeLabelNum.setText("" + value);
                p.setAmplitude(value);
                frame.repaint();
            }
        };
        
        amplitudeSlider.setMajorTickSpacing(tickSpacing);
        amplitudeSlider.setPaintTicks(true);
        amplitudeSlider.setPaintLabels(true);
        
        JSlider shiftXSlider = new JSlider(JSlider.HORIZONTAL,
                                      -sliderSize, sliderSize, 0)
        {
            public void setValue(int value)
            {
                super.setValue(value);
                shiftXLabelNum.setText("" + value);
                p.setShiftX(value);
                frame.repaint();
            }
        };
        
        shiftXSlider.setMajorTickSpacing(tickSpacing);
        shiftXSlider.setPaintTicks(true);
        shiftXSlider.setPaintLabels(true);
        
        JSlider shiftYSlider = new JSlider(JSlider.HORIZONTAL,
                                      -sliderSize, sliderSize, 0)
        {
            public void setValue(int value)
            {
                super.setValue(value);
                shiftYLabelNum.setText("" + value);
                p.setShiftY(value);
                frame.repaint();
            }
        };
        
        shiftYSlider.setMajorTickSpacing(tickSpacing);
        shiftYSlider.setPaintTicks(true);
        shiftYSlider.setPaintLabels(true);
        
        JSlider frequency32thSlider = new JSlider(JSlider.HORIZONTAL,
                                      1, sliderSize, (int)dividerMul)
        {
            public void setValue(int value)
            {
                super.setValue(value);
                frequency8thLabelNum.setText("" + value/32f);
                p.setFrequency32th(value/32f/256f/dividerMul);
                frame.repaint();
            }
        };
        
        frequency32thSlider.setMajorTickSpacing(tickSpacing);
        frequency32thSlider.setPaintTicks(true);
        frequency32thSlider.setPaintLabels(true);
        
        JSlider frequency16thSlider = new JSlider(JSlider.HORIZONTAL,
                                      1, sliderSize, (int)dividerMul)
        {
            public void setValue(int value)
            {
                super.setValue(value);
                frequency8thLabelNum.setText("" + value/16f);
                p.setFrequency16th(value/16f/256f/dividerMul);
                frame.repaint();
            }
        };
        
        frequency16thSlider.setMajorTickSpacing(tickSpacing);
        frequency16thSlider.setPaintTicks(true);
        frequency16thSlider.setPaintLabels(true);
        
        JSlider frequency8thSlider = new JSlider(JSlider.HORIZONTAL,
                                      1, sliderSize, (int)dividerMul)
        {
            public void setValue(int value)
            {
                System.out.println(value/8f);
                super.setValue(value);
                frequency8thLabelNum.setText("" + value/8f);
                p.setFrequency8th(value/8f/256f/dividerMul);
                frame.repaint();
            }
        };
        
        frequency8thSlider.setMajorTickSpacing(tickSpacing);
        frequency8thSlider.setPaintTicks(true);
        frequency8thSlider.setPaintLabels(true);
        
        JSlider frequency4thSlider = new JSlider(JSlider.HORIZONTAL,
                                      1, sliderSize, (int)dividerMul)
        {
            public void setValue(int value)
            {
                super.setValue(value);
                frequency4thLabelNum.setText("" + value/4f);
                p.setFrequency4th(value/4f/256f/dividerMul);
                frame.repaint();
            }
        };
        
        frequency4thSlider.setMajorTickSpacing(tickSpacing);
        frequency4thSlider.setPaintTicks(true);
        frequency4thSlider.setPaintLabels(true);
        
        JSlider frequency2ndSlider = new JSlider(JSlider.HORIZONTAL,
                                      1, sliderSize, (int)dividerMul)
        {
            public void setValue(int value)
            {
                super.setValue(value);
                frequency2ndLabelNum.setText("" + value/2f);
                p.setFrequency2nd(value/2f/256f/dividerMul);
                frame.repaint();
            }
        };
        
        frequency2ndSlider.setMajorTickSpacing(tickSpacing);
        frequency2ndSlider.setPaintTicks(true);
        frequency2ndSlider.setPaintLabels(true);
        
        JSlider frequencySlider = new JSlider(JSlider.HORIZONTAL,
                                      1, sliderSize, (int)dividerMul)
        {
            public void setValue(int value)
            {
                super.setValue(value);
                frequencyLabelNum.setText("" + value/1f);
                p.setFrequency(value/1f/256f/dividerMul);
                frame.repaint();
            }
        };
        
        frequencySlider.setMajorTickSpacing(tickSpacing);
        frequencySlider.setPaintTicks(true);
        frequencySlider.setPaintLabels(true);
        
        JSlider frequency2Slider = new JSlider(JSlider.HORIZONTAL,
                                      1, sliderSize, (int)dividerMul)
        {
            public void setValue(int value)
            {
                super.setValue(value);
                frequency2LabelNum.setText("" + value/0.5f);
                p.setFrequency2(value/0.5f/256f/dividerMul);
                frame.repaint();
            }
        };
        
        frequency2Slider.setMajorTickSpacing(tickSpacing);
        frequency2Slider.setPaintTicks(true);
        frequency2Slider.setPaintLabels(true);
                
        JSlider frequency4Slider = new JSlider(JSlider.HORIZONTAL,
                                      1, sliderSize, (int)dividerMul)
        {
            public void setValue(int value)
            {
                super.setValue(value);
                frequency4LabelNum.setText("" + value/0.25f);
                p.setFrequency4(value/0.25f/256f/dividerMul);
                frame.repaint();
            }
        };
        
        frequency4Slider.setMajorTickSpacing(tickSpacing);
        frequency4Slider.setPaintTicks(true);
        frequency4Slider.setPaintLabels(true);
        
        JSlider frequency8Slider = new JSlider(JSlider.HORIZONTAL,
                                      1, sliderSize, (int)dividerMul)
        {
            public void setValue(int value)
            {
                super.setValue(value);
                frequency8LabelNum.setText("" + value/0.125f);
                p.setFrequency8(value/0.125f/256f/dividerMul);
                frame.repaint();
            }
        };
        frequency8Slider.setMajorTickSpacing(tickSpacing);
        frequency8Slider.setPaintTicks(true);
        frequency8Slider.setPaintLabels(true);
        
        JSlider frequency16Slider = new JSlider(JSlider.HORIZONTAL,
                                      1, sliderSize, (int)dividerMul)
        {
            public void setValue(int value)
            {
                super.setValue(value);
                frequency16LabelNum.setText("" + value/0.0625f);
                p.setFrequency16(value/0.0625f/256f/dividerMul);
                frame.repaint();
            }
        };
        frequency16Slider.setMajorTickSpacing(tickSpacing);
        frequency16Slider.setPaintTicks(true);
        frequency16Slider.setPaintLabels(true);
        
        JSlider frequency32Slider = new JSlider(JSlider.HORIZONTAL,
                                      1, sliderSize, (int)dividerMul)
        {
            public void setValue(int value)
            {
                super.setValue(value);
                frequency32LabelNum.setText("" + value/0.03125f);
                p.setFrequency32(value/0.03125f/dividerMul);
                frame.repaint();
            }
        };
        frequency32Slider.setMajorTickSpacing(tickSpacing);
        frequency32Slider.setPaintTicks(true);
        frequency32Slider.setPaintLabels(true);
        
        JSlider zSlider = new JSlider(JSlider.HORIZONTAL,
                                      1, sliderSize, (int)dividerMul)
        {
            public void setValue(int value)
            {
                super.setValue(value);
                zLabelNum.setText("" + value);
                p.setZ(value);
                frame.repaint();
            }
        };
        
//        frequency4Slider.
        HorizontalPanel shiftPanel = new HorizontalPanel();
        shiftPanel.add(shiftLabel);
        shiftPanel.add(shiftSlider);
        shiftPanel.add(shiftLabelNum);
        HorizontalPanel amplitudePanel = new HorizontalPanel();
        amplitudePanel.add(amplitudeLabel);
        amplitudePanel.add(amplitudeSlider);
        amplitudePanel.add(amplitudeLabelNum);
        HorizontalPanel shiftXPanel = new HorizontalPanel();
        shiftXPanel.add(shiftXLabel);
        shiftXPanel.add(shiftXSlider);
        shiftXPanel.add(shiftXLabelNum);
        HorizontalPanel shiftYPanel = new HorizontalPanel();
        shiftYPanel.add(shiftYLabel);
        shiftYPanel.add(shiftYSlider);
        shiftYPanel.add(shiftYLabelNum);
        HorizontalPanel frequency32thPanel = new HorizontalPanel();
        frequency32thPanel.add(frequency32thLabel);
        frequency32thPanel.add(frequency32thSlider);
        frequency32thPanel.add(frequency32thLabelNum);
        HorizontalPanel frequency16thPanel = new HorizontalPanel();
        frequency16thPanel.add(frequency16thLabel);
        frequency16thPanel.add(frequency16thSlider);
        frequency16thPanel.add(frequency16thLabelNum);
        HorizontalPanel frequency8thPanel = new HorizontalPanel();
        frequency8thPanel.add(frequency8thLabel);
        frequency8thPanel.add(frequency8thSlider);
        frequency8thPanel.add(frequency8thLabelNum);
        HorizontalPanel frequency4thPanel = new HorizontalPanel();
        frequency4thPanel.add(frequency4thLabel);
        frequency4thPanel.add(frequency4thSlider);
        frequency4thPanel.add(frequency4thLabelNum);
        HorizontalPanel frequency2ndPanel = new HorizontalPanel();
        frequency2ndPanel.add(frequency2ndLabel);
        frequency2ndPanel.add(frequency2ndSlider);
        frequency2ndPanel.add(frequency2ndLabelNum);
        HorizontalPanel frequencyPanel = new HorizontalPanel();
        frequencyPanel.add(frequencyLabel);
        frequencyPanel.add(frequencySlider);
        frequencyPanel.add(frequencyLabelNum);
        HorizontalPanel frequency2Panel = new HorizontalPanel();
        frequency2Panel.add(frequency2Label);
        frequency2Panel.add(frequency2Slider);
        frequency2Panel.add(frequency2LabelNum);
        HorizontalPanel frequency4Panel = new HorizontalPanel();
        frequency4Panel.add(frequency4Label);
        frequency4Panel.add(frequency4Slider);
        frequency4Panel.add(frequency4LabelNum);
        HorizontalPanel frequency8Panel = new HorizontalPanel();
        frequency8Panel.add(frequency8Label);
        frequency8Panel.add(frequency8Slider);
        frequency8Panel.add(frequency8LabelNum);
        HorizontalPanel frequency16Panel = new HorizontalPanel();
        frequency16Panel.add(frequency16Label);
        frequency16Panel.add(frequency16Slider);
        frequency16Panel.add(frequency16LabelNum);
        HorizontalPanel frequency32Panel = new HorizontalPanel();
        frequency32Panel.add(frequency32Label);
        frequency32Panel.add(frequency32Slider);
        frequency32Panel.add(frequency32LabelNum);
        HorizontalPanel zPanel = new HorizontalPanel();
        zPanel.add(zLabel);
        zPanel.add(zSlider);
        zPanel.add(zLabelNum);
        
        VerticalPanel vpParam = new VerticalPanel();
        
        vpParam.add(shiftPanel);
        vpParam.add(amplitudePanel);
        vpParam.add(shiftXPanel);
        vpParam.add(shiftYPanel);
        vpParam.add(frequency32thPanel);
        vpParam.add(frequency16thPanel);
        vpParam.add(frequency8thPanel);
        vpParam.add(frequency4thPanel);
        vpParam.add(frequency2ndPanel);
        vpParam.add(frequencyPanel);
        vpParam.add(frequency2Panel);
        vpParam.add(frequency4Panel);
        vpParam.add(frequency8Panel);
        vpParam.add(frequency16Panel);
        vpParam.add(frequency32Panel);
        vpParam.add(zPanel);
        
        
        JLabel noiseTypeLabel = new JLabel("Noise Type");
        JComboBox noiseTypeCmb = new JComboBox(new NoiseType[]{NoiseType.Value, NoiseType.ValueFractal, NoiseType.Perlin, NoiseType.PerlinFractal, NoiseType.Simplex, NoiseType.SimplexFractal, NoiseType.Cellular, NoiseType.WhiteNoise, NoiseType.Cubic, NoiseType.CubicFractal})
                {
                    public void setSelectedItem(Object item)
                    {
                        super.setSelectedItem(item);
                        p.setNoiseType((NoiseType)item);
                        frame.repaint();
                    }
                };
        
        JLabel interpLabel = new JLabel("Interpolation");
        JComboBox interpCmb = new JComboBox(new Interp[]{Interp.Linear, Interp.Hermite, Interp.Quintic})
                {
                    public void setSelectedItem(Object item)
                    {
                        super.setSelectedItem(item);
                        p.setNoiseType((Interp)item);
                        frame.repaint();
                    }
                };
        
        JLabel fractalTypeLabel = new JLabel("FractalType");
        JComboBox fractalTypeCmb = new JComboBox(new FractalType[]{FractalType.FBM, FractalType.Billow, FractalType.RigidMulti})
                {
                    public void setSelectedItem(Object item)
                    {
                        super.setSelectedItem(item);
                        p.setFractalType((FractalType)item);
                        frame.repaint();
                    }
                };
        
        JLabel cellularDistanceFunctionLabel = new JLabel("CellularDistanceFunction");
        JComboBox cellularDistanceFunctionCmb = new JComboBox(new CellularDistanceFunction[]{CellularDistanceFunction.Euclidean, CellularDistanceFunction.Manhattan, CellularDistanceFunction.Natural})
                {
                    public void setSelectedItem(Object item)
                    {
                        super.setSelectedItem(item);
                        p.setCellularDistanceFunction((CellularDistanceFunction)item);
                        frame.repaint();
                    }
                };
        
        JLabel cellularReturnTypeLabel = new JLabel("CellularReturnType");
        JComboBox cellularReturnTypeCmb = new JComboBox(new CellularReturnType[]{CellularReturnType.CellValue, CellularReturnType.NoiseLookup, CellularReturnType.Distance, CellularReturnType.Distance2, CellularReturnType.Distance2Add, CellularReturnType.Distance2Sub, CellularReturnType.Distance2Mul, CellularReturnType.Distance2Div})
                {
                    public void setSelectedItem(Object item)
                    {
                        super.setSelectedItem(item);
                        p.setCellularReturnType((CellularReturnType)item);
                        frame.repaint();
                    }
                };
        
        
        
        HorizontalPanel noiseTypeLabelPanel = new HorizontalPanel();
        noiseTypeLabelPanel.add(noiseTypeLabel);
        noiseTypeLabelPanel.add(noiseTypeCmb);
        HorizontalPanel interpPanel = new HorizontalPanel();
        interpPanel.add(interpLabel);
        interpPanel.add(interpCmb);
        HorizontalPanel fractalTypePanel = new HorizontalPanel();
        fractalTypePanel.add(fractalTypeLabel);
        fractalTypePanel.add(fractalTypeCmb);
        HorizontalPanel cellularDistanceFunctionPanel = new HorizontalPanel();
        cellularDistanceFunctionPanel.add(cellularDistanceFunctionLabel);
        cellularDistanceFunctionPanel.add(cellularDistanceFunctionCmb);
        HorizontalPanel cellularReturnTypePanel = new HorizontalPanel();
        cellularReturnTypePanel.add(cellularReturnTypeLabel);
        cellularReturnTypePanel.add(cellularReturnTypeCmb);
        
        
        
        
        VerticalPanel vpEnum = new VerticalPanel();
        vpEnum.add(noiseTypeLabelPanel);
        vpEnum.add(interpPanel);
        vpEnum.add(fractalTypePanel);
        vpEnum.add(cellularDistanceFunctionPanel);
        vpEnum.add(cellularReturnTypePanel);
        
        
        
        controlePanel.add(vpParam);
        controlePanel.add(vpEnum);
        controlFrame.add(controlePanel);
        controlFrame.setVisible(true);
        
        System.out.println(p.getFrequency4th());
        
        
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyChar() == 'p')
        {
            saveImage("Image");
        }
    }
    
    public void saveImage(String name)
    {
        String imageFilename = name + ".png";//[302]
        Color[][] image = new Color[DEFAULT_WIDTH][DEFAULT_HEIGHT];
        for(int i = 0; i < DEFAULT_WIDTH; i++)
            for(int j = 0; j < DEFAULT_HEIGHT; j++)
                image[i][j] = grid[j][i].getColor();
        //[303]
        BufferedImage bufferedImage = new BufferedImage(image.length, image[0].length,
        BufferedImage.TYPE_INT_RGB);

        // Set each pixel of the BufferedImage to the color from the Color[][].
        for (int x = 0; x < image.length; x++) {
            for (int y = 0; y < image[x].length; y++) {
                bufferedImage.setRGB(x, y, image[x][y].getRGB());
            }
        }
        try {
            ImageIO.write(bufferedImage, "png", new File(imageFilename));//[304]
        } catch (IOException ex) {
            Logger.getLogger(PerlinNoise.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(name + " saved");
    }
}