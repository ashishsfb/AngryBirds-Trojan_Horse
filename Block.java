package ab.vision;

import java.awt.*;

/**
 * Created by ashish on 28-Sep-14.
 */
public class Block {
    public int blockNumber;
    public ABShape blockShape;
    public ABType blockMaterial;

    public Block(int number, ABShape shape, ABType material){
        this.blockNumber = number;
        this.blockShape = shape;
        this.blockMaterial = material;
    }
}
