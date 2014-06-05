/**
 * @author Marcos Lemus
 * */
package neurogame.level;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import neurogame.library.Library;
import neurogame.main.Drawable;

/**
 *
 * @author Marcos
 */
public class Chunks implements Drawable{
    private List<Path> pathList;
    
    private Path2D.Double top;
    private Path2D.Double bottom;
    
    private int PATH_LENGTH = 1000;
    private double center  = .0;
    
    public Chunks(){
        pathList = new ArrayList<>();
        pathList.add(new Path());
        
        
        for(int i = 0; i < PATH_LENGTH; i++){
            pathList.add(new Path(pathList.get(pathList.size()-1), .8));
        }
        
        top = new Path2D.Double();
        bottom = new Path2D.Double();
    }
    
    public void update(){
        
    }
    
    @Override
    public void draw(Graphics2D g){
        top.reset();
        bottom.reset();
        
        top.moveTo(0, 0);
        bottom.moveTo(0, 1);
        
        final double start = pathList.get(1).getX();
        
        pathList.forEach(p ->{
            top.lineTo(p.getX() - start, p.getTopY());
            bottom.lineTo(p.getX() - start, p.getBottomY());
        });

        top.lineTo(PATH_LENGTH * Path.STEP_SIZE, 0);
        top.closePath();
        
        bottom.lineTo(PATH_LENGTH * Path.STEP_SIZE, 1);
        bottom.closePath();
        
        AffineTransform oldTransform = g .getTransform();
        g.setTransform(AffineTransform.getScaleInstance(Library.U_VALUE, Library.U_VALUE));
        
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 2, 1);
        
        g.setColor(Color.ORANGE);
        g.fill(top);
        g.fill(bottom);
        
        g.setTransform(oldTransform);
        
        if(Path.isCentered()){
            center = .8 - Library.RANDOM.nextDouble()*.6;
            System.out.println(center);
        }
    }
}
