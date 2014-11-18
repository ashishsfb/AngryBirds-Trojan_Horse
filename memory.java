package ab.pridictor;
import java.util.*;
import ab.vision.ABObject;
import ab.vision.Vision;

/**
 * Created by anup on 17/11/14.
 */
public class memory {
    public int id;
//    public int NumberofPigs;
//    public int NumberofWp;
    public boolean sucess;

   // public List<ABObject> ObjectsAroundPigs = null;
   public List<ABObject> Wkpts = new ArrayList<ABObject>();
    public List<ABObject> pigs = new ArrayList<ABObject>();
    public List<ABObject> WhatIDid = new ArrayList<ABObject>();
    public memory(Vision v)
    {
//        this.NumberofPigs = v.currentstate();
//        this.NumberofWp = v.currentweekstate();
        this.pigs=v.findPigsMBR();
        this.Wkpts=v.getWeakPoints();
    }
    public void whatidid(ABObject a)
    {
        this.WhatIDid.add(a);
    }
    public void ifwin(boolean b)
    {
        this.sucess = b;
    }
}
