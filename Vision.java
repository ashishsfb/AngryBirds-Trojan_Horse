/*****************************************************************************
 ** ANGRYBIRDS AI AGENT FRAMEWORK
 ** Copyright (c) 2014,XiaoYu (Gary) Ge, Stephen Gould, Jochen Renz
 **  Sahan Abeyasinghe, Jim Keys,   Andrew Wang, Peng Zhang
 ** All rights reserved.
 **This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License. 
 **To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/3.0/ 
 *or send a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 *****************************************************************************/
        package ab.vision;

        import java.awt.Point;
        import java.awt.Rectangle;
        import java.awt.image.BufferedImage;
        import java.util.ArrayList;
        import java.util.List;

public class Vision {
    private BufferedImage image;
    private VisionMBR visionMBR = null;
    private VisionRealShape visionRealShape = null;

    public Vision(BufferedImage image)
    {
        this.image = image;
    }

    public List<ABObject> findBirdsMBR()
    {
        if (visionMBR == null)
        {
            visionMBR = new VisionMBR(image);
        }
        return visionMBR.findBirds();

    }
    /**
     * @return a list of MBRs of the blocks in the screenshot. Blocks: Stone, Wood, Ice
     * */
    public List<ABObject> findBlocksMBR()
    {
        if (visionMBR == null)
        {
            visionMBR = new VisionMBR(image);
        }
        return visionMBR.findBlocks();
    }

    public List<ABObject> findTNTs()
    {
        if(visionMBR == null)
        {
            visionMBR = new VisionMBR(image);
        }
        return visionMBR.findTNTs();
    }
    public List<ABObject> findPigsMBR()
    {
        if (visionMBR == null)
        {
            visionMBR = new VisionMBR(image);
        }
        return visionMBR.findPigs();
    }
    public List<ABObject> findPigsRealShape()
    {
        if(visionRealShape == null)
        {
            visionRealShape = new VisionRealShape(image);
        }

        return visionRealShape.findPigs();
    }
    public List<ABObject> findBirdsRealShape()
    {
        if(visionRealShape == null)
        {
            visionRealShape = new VisionRealShape(image);
        }

        return visionRealShape.findBirds();
    }

    public List<ABObject> findHills()
    {
        if(visionRealShape == null)
        {
            visionRealShape = new VisionRealShape(image);
        }

        return visionRealShape.findHills();
    }


    public Rectangle findSlingshotMBR()
    {
        if (visionMBR == null)
        {
            visionMBR = new VisionMBR(image);
        }
        return visionMBR.findSlingshotMBR();
    }
    public List<Point> findTrajPoints()
    {
        if (visionMBR == null)
        {
            visionMBR = new VisionMBR(image);
        }
        return visionMBR.findTrajPoints();
    }
    /**
     * @return a list of real shapes (represented by Body.java) of the blocks in the screenshot. Blocks: Stone, Wood, Ice
     * */
    public List<ABObject> findBlocksRealShape()
    {
        if(visionRealShape == null)
        {
            visionRealShape = new VisionRealShape(image);
        }
        List<ABObject> allBlocks = visionRealShape.findObjects();

        return allBlocks;
    }
    public VisionMBR getMBRVision()
    {
        if(visionMBR == null)
            visionMBR = new VisionMBR(image);
        return visionMBR;
    }

    //Creating this method for the assignment 2 : for getting all blocks i.e. ice, wood or stone
    //and their shapes in a single list
    public List<Block> getBlocks() {
        List<Block> blocks = new ArrayList<Block>();

        List<ABObject> l = this.findBlocksRealShape();
        Block b;

        for(ABObject o : l){
            if(o.type == ABType.Ice || o.type == ABType.Wood || o.type == ABType.Stone){
                b = new Block(o.id, o.shape, o.getType());
                blocks.add(b);
            }
        }

        return blocks;
    }

    //Assignment 4: getting a basic list of all weak points
    public List<ABObject> getWeakPoints() {
        List<ABObject> weakPoints = new ArrayList<ABObject>();
        List<ABObject> blocks = this.findBlocksRealShape();

        //adding circular objects to weak points list
        for(ABObject o : blocks){
            if(o.shape == ABShape.Circle && o.type != ABType.Unknown){
                weakPoints.add(o);
            }
        }

        //adding all tnts to weak points list
        weakPoints.addAll(findTNTs());
        return weakPoints;
    }

    public List<ABObject> belowpigs() {
        List<ABObject> belowpigs = new ArrayList<ABObject>();
        List<ABObject> blocks = this.findBlocksRealShape();
        List<ABObject> pigs = this.findPigsMBR();
        //adding circular objects to weak points list
        ABObject ad = new ABObject();
        for(ABObject p : pigs)
        {
            if(p.getCenterY() < 500 )
            {
                ad=null;
                int y= Integer.MAX_VALUE;
                for(ABObject o : blocks)
                {

                    if(o.getCenterX() < p.getCenterX() + 5  && o.getCenterX() > p.getCenterX() - 5 && o.type!=ABType.Stone)
                    {
                        if(o.getCenterY() <= p.getCenterY() + 10 && o.getCenterY() > p.getCenterY() )
                            ad=o;
                    }
                }
                if(ad != null)
                    belowpigs.add(ad);
            }
        }//adding all tnts to weak points list
//        weakPoints.addAll(findTNTs());
        return belowpigs;
    }
//    public List<WeakPoint> setPriority(List<WeakPoint> abObjs){
//        List<WeakPoint> wpts = null;
//        for(ABObject o : abObjs){
//            if(o.type == ABType.TNT){
//
//            }
//        }
//        return wpts;
//    }
    public int currentstate()
    {
        int k;
        List<ABObject> l =findPigsMBR() ;

        return l.size();
    }

    public int currentweekstate()
    {
        int k;
        List<ABObject> l =getWeakPoints() ;

        return l.size();
    }


}
