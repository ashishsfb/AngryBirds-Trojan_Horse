/*****************************************************************************
 ** ANGRYBIRDS AI AGENT FRAMEWORK
 ** Copyright (c) 2014, XiaoYu (Gary) Ge, Stephen Gould, Jochen Renz
 **  Sahan Abeyasinghe,Jim Keys,  Andrew Wang, Peng Zhang
 ** All rights reserved.
 **This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License. 
 **To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/3.0/ 
 *or send a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 *****************************************************************************/
        package ab.demo;

        import java.awt.Point;
        import java.awt.Rectangle;
        import java.awt.image.BufferedImage;
        import java.util.ArrayList;
        import java.util.LinkedHashMap;
        import java.util.List;
        import java.util.Map;
        import java.util.Random;

        import ab.demo.other.ActionRobot;
        import ab.demo.other.Shot;
        import ab.planner.TrajectoryPlanner;
        import ab.pridictor.memory;
        import ab.utils.StateUtil;
        import ab.vision.ABObject;
        import ab.vision.ABType;
        import ab.vision.GameStateExtractor.GameState;
        import ab.vision.Vision;

public class NaiveAgent implements Runnable {
    public List<memory> rem=new ArrayList<memory>();
    private ActionRobot aRobot;
    private Random randomGenerator;
    public int currentLevel = 1;
    public int previousLevel = 0;
    private int closeTargetCounter = 0;
    private boolean useHighTrajectory = false, tempTraj = false;
    public static int time_limit = 12;
    private Map<Integer,Integer> scores = new LinkedHashMap<Integer,Integer>();
    TrajectoryPlanner tp;
    private boolean firstShot;
    private Point prevTarget;
    private boolean levelLost = false;
    memory m = null;
    // a standalone implementation of the Naive Agent
    public NaiveAgent() {

        aRobot = new ActionRobot();
        tp = new TrajectoryPlanner();
        prevTarget = null;
        firstShot = true;
        randomGenerator = new Random();
        // --- go to the Poached Eggs episode level selection page ---
        ActionRobot.GoFromMainMenuToLevelSelection();

    }


    // run the client
    public void run() {
        int levelCounter = 1;
        aRobot.loadLevel(currentLevel);
        int count = 0;//for first level
        while (true) {
//            if(previousLevel != currentLevel){
//                System.out.println("##New level "+currentLevel+", to be precise starts, should store memory state now");
//                BufferedImage screenshot = ActionRobot.doScreenShot();
//                Vision v = new Vision(screenshot);
//                m = new memory(v);
//                m.id = currentLevel;
//                previousLevel++;
//            }
            if(currentLevel == 1 || rem.size() == 0){
                count++;
                if(count == 1) {
                    System.out.println("##New level " + currentLevel);
                    BufferedImage screenshot = ActionRobot.doScreenShot();
                    Vision v = new Vision(screenshot);
                    m = new memory(v);
                    m.id = currentLevel;
                }
            }
            GameState state = solve();
            if (state == GameState.WON /*|| levelCounter > 3*/) {
                levelLost = false;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int score = StateUtil.getScore(ActionRobot.proxy);
                if(!scores.containsKey(currentLevel))
                    scores.put(currentLevel, score);
                else
                {
                    if(scores.get(currentLevel) < score)
                        scores.put(currentLevel, score);
                }
                int totalScore = 0;
                //Printing all level scores
                for(Integer key: scores.keySet()){

                    totalScore += scores.get(key);
                    System.out.println(" Level " + key
                            + " Score: " + scores.get(key) + " ");
                }
                System.out.println("Total Score: " + totalScore);
                m.sucess = true;
                rem.add(m);
                //proceeding to next level
                aRobot.loadLevel(++currentLevel);

                System.out.println("I came back after load new level");
                BufferedImage screenshot = ActionRobot.doScreenShot();
                Vision v = new Vision(screenshot);
                m = new memory(v);
                m.id = currentLevel;

                //printing current level memory
                System.out.println("\n*************************");
                System.out.println("Current Level : "+(currentLevel-1));
                System.out.println("Memory : "+rem.get(rem.size()-1).id);
                System.out.println("Passed : "+rem.get(rem.size()-1).sucess);
                System.out.println("Rem size : "+rem.size());
                System.out.println("List of targets in order : ");
                for(int j = 0; j < rem.get(rem.size()-1).WhatIDid.size(); j++){
                    System.out.println((j+1)+". Type : "+rem.get(rem.size()-1).WhatIDid.get(j).type+"\tx : "+rem.get(rem.size()-1).WhatIDid.get(j).x+"\ty : "+rem.get(rem.size()-1).WhatIDid.get(j).y+"\tHeight : "+rem.get(rem.size()-1).WhatIDid.get(j).height+"\t");
                }
                System.out.println("*************************\n");

                System.out.println("\n##New level "+currentLevel);
                // make a new trajectory planner whenever a new level is entered
                tp = new TrajectoryPlanner();

                // first shot on this level, try high shot first
                firstShot = true;
            } else if (state == GameState.LOST) {
                levelLost = true;
                levelCounter++;

                if(levelCounter <= 3) {

                    System.out.println("Trying for "+ (levelCounter) +" time.");
                    m.sucess = false;
                    rem.add(m);
                    aRobot.restartLevel();
                    System.out.println("I came back after restart level");
                    BufferedImage screenshot = ActionRobot.doScreenShot();
                    Vision v = new Vision(screenshot);
                    m = new memory(v);
                    m.id = currentLevel;
//                    rem.add(m);

                    //printing current level memory
                    System.out.println("\n*************************");
                    System.out.println("Current Level : "+currentLevel);
                    System.out.println("Memory : "+rem.get(rem.size()-1).id);
                    System.out.println("Passed : "+rem.get(rem.size()-1).sucess);
                    System.out.println("Rem size : "+rem.size());
                    System.out.println("List of targets in order : ");
                    for(int j = 0; j < rem.get(rem.size()-1).WhatIDid.size(); j++){
                        System.out.println((j+1)+". Type : "+rem.get(rem.size()-1).WhatIDid.get(j).type+"\tx : "+rem.get(rem.size()-1).WhatIDid.get(j).x+"\ty : "+rem.get(rem.size()-1).WhatIDid.get(j).y+"\tHeight : "+rem.get(rem.size()-1).WhatIDid.get(j).height+"\t");
                    }
                    System.out.println("*************************\n");
                }
                else {
                    levelCounter = 1;
                    m.sucess = false;
                    rem.add(m);
                    levelLost=false;
                    aRobot.loadLevel(++currentLevel);

                    BufferedImage screenshot = ActionRobot.doScreenShot();
                    Vision v = new Vision(screenshot);
                    m = new memory(v);
                    m.id = currentLevel;

                    //printing current level memory
                    System.out.println("\n*************************");
                    System.out.println("Current Level : "+(currentLevel-1));
                    System.out.println("Memory : "+rem.get(rem.size()-1).id);
                    System.out.println("Passed : "+rem.get(rem.size()-1).sucess);
                    System.out.println("Rem size : "+rem.size());
                    System.out.println("List of targets in order : ");
                    for(int j = 0; j < rem.get(rem.size()-1).WhatIDid.size(); j++){
                        System.out.println((j+1)+". Type : "+rem.get(rem.size()-1).WhatIDid.get(j).type+"\tx : "+rem.get(rem.size()-1).WhatIDid.get(j).x+"\ty : "+rem.get(rem.size()-1).WhatIDid.get(j).y+"\tHeight : "+rem.get(rem.size()-1).WhatIDid.get(j).height+"\t");
                    }
                    System.out.println("*************************\n");
                    // make a new trajectory planner whenever a new level is entered
                    tp = new TrajectoryPlanner();

                    // first shot on this level, try high shot first
                    firstShot = true;
                }
            } else if (state == GameState.LEVEL_SELECTION) {
                System.out
                        .println("Unexpected level selection page, go to the last current level : "
                                + currentLevel);
                aRobot.loadLevel(currentLevel);
            } else if (state == GameState.MAIN_MENU) {
                System.out
                        .println("Unexpected main menu page, go to the last current level : "
                                + currentLevel);
                ActionRobot.GoFromMainMenuToLevelSelection();
                aRobot.loadLevel(currentLevel);
            } else if (state == GameState.EPISODE_MENU) {
                System.out
                        .println("Unexpected episode menu page, go to the last current level : "
                                + currentLevel);
                ActionRobot.GoFromMainMenuToLevelSelection();
                aRobot.loadLevel(currentLevel);
            }

        }

    }

    private double distance(Point p1, Point p2) {
        return Math
                .sqrt((double) ((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)
                        * (p1.y - p2.y)));
    }

    public GameState solve()
    {
        // capture Image
        BufferedImage screenshot = ActionRobot.doScreenShot();

        // process image
        Vision vision = new Vision(screenshot);
//        memory m=new memory(vision);

        // find the slingshot
        Rectangle sling = vision.findSlingshotMBR();

        // confirm the slingshot
        while (sling == null && aRobot.getState() == GameState.PLAYING) {
            System.out
                    .println("No slingshot detected. Please remove pop up or zoom out");
            ActionRobot.fullyZoomOut();
            screenshot = ActionRobot.doScreenShot();
            vision = new Vision(screenshot);
            sling = vision.findSlingshotMBR();
        }
        // get all the pigs
        List<ABObject> pigs = vision.findPigsMBR();

        GameState state = aRobot.getState();

        // if there is a sling, then play, otherwise just skip.
        if (sling != null) {
            if (!pigs.isEmpty()) {

                Point releasePoint = null;
                Shot shot = new Shot();
                int dx,dy;
                {
                    //-->Assignment 1: Pick a pig that is at largest height
                    // rather than picking'em randomly and select bigger angle
                    // random pick up a pig<--

                    //ABObject pig = pigs.get(randomGenerator.nextInt(pigs.size()));

                    //-->picking the pig with least y<--
//                    System.out.println("The pigs :-");
//                    int min = 0;
//                    int i = 0;
//                    for(i = 0; i < pigs.size(); i++){
//                        System.out.println("Pig "+(i+1)+" :"+pigs.get(i).x+", "+pigs.get(i).y);
//                        if(pigs.get(min).y > pigs.get(i).y){
//                            min = i;
//                        }
//                    }
//                    ABObject pig = pigs.get(min);
//
//                    -->Printing all the blocks in the blocklist for Assignment 2<--
//                    System.out.println("All Blocks here :");
//                    int count = 1;
//                    for(Block b : vision.getBlocks()){
//                        System.out.println("S.no - " + count++ + ": "+b.blockNumber+" "+b.blockMaterial+" "+b.blockShape);
//                    }


                    //Assignment 4: Selecting weakpoints{tnts and circular objects} one by one  and then pigs if not all dead
                   //Checking the memory for simmiler level
//                    memory bestmatch=null;
//                    int maxcount=0;
//                    for(int i=0;i<rem.size();++i)
//                    {
//                        memory tp=rem.get(i);
//                         int count=0;
//                           if(tp.NumberofPigs == m.NumberofPigs)
//                               count++;
//                            if(tp.NumberofWp==m.NumberofWp)
//                               count++;
//                            for(int j=0;j<m.pigs.size();++j)
//                            {
//                                for(int k=0;k<m.pigs.size();++k)
//                                {
//                                    if(m.pigs.get(k).getHeight() >= tp.pigs.get(j).getHeight()-10 && m.pigs.get(k).getHeight() <= tp.pigs.get(j).getHeight()+10)
//                                {
//                                        count++;
//                                        break;
//                                }
//                                }
//                             }
//                        if(count>maxcount)
//                        {     maxcount=count;
//                            bestmatch=tp;
//                        }
//
//                    }
//                    if(maxcount >= 2+ m.NumberofPigs)
//                    {
//                        if(bestmatch.sucess)
//                        {
//                            //follow what you did earlier
//                        }
//                        else
//                        {
//                            //do something else then what you did earlier
//                        }
//                    }

                    ABObject weakPt = null;
                    List<ABObject> weakPoints = vision.getWeakPoints();


                    //removing useless weak pts
                    if(!weakPoints.isEmpty()) {
                        for(int i = 0; i < weakPoints.size(); i++){
                            int counter = 0;
                            for(int j = 0; j < pigs.size(); j++){
                                if(weakPoints.get(i).x > pigs.get(j).x && weakPoints.get(i).y > pigs.get(j).y){
                                    counter++;
                                }
                            }
                            if(counter == pigs.size()){
                                weakPoints.remove(i);
                            }
                        }
                    }

                    Point _tpt = null;
                    if(!levelLost) {
                        if (!weakPoints.isEmpty()) {
                            int priorWeakPts[][];
                            priorWeakPts = givePriorityWeekPoints(weakPoints);

                            System.out.println("Weak Pts :-");
                            for (int i = 0; i < weakPoints.size(); i++) {
                                System.out.print((i + 1) + ": [" + weakPoints.get(i).x + ", " + weakPoints.get(i).y + "] " + weakPoints.get(i).type + "\t");
                            }
                            System.out.println("##");
//                            weakPt = weakPoints.get(0);
//                            weakPoints.remove(0);
                            int max = 0;
                            for(int i = 0; i < weakPoints.size(); i++){
                                if(max < priorWeakPts[i][1]){
                                    max = i;
                                }
                            }
                            System.out.println("**");
                            priorWeakPts[max][1] = 0;
                            weakPt = weakPoints.get(max);
                            weakPoints.remove(max);
                            _tpt = weakPt.getCenter();
                            m.whatidid(weakPt);
                        } else {
                            System.out.println("The pigs :-");
                            int min = 0;
                            int i = 0;
                            for (i = 0; i < pigs.size(); i++) {
                                System.out.println("Pig " + (i + 1) + " :" + pigs.get(i).x + ", " + pigs.get(i).y);
                                if (pigs.get(min).y > pigs.get(i).y) {
                                    min = i;
                                }
                            }
                            ABObject pig = pigs.get(min);
                            _tpt = pig.getCenter();
                            m.whatidid(pig);
                        }
                    }
                    else{
                        System.out.println("Lost d game, trying random weak pts and pigs");
                        List<ABObject> targetList  = new ArrayList<ABObject>();
                        targetList = rem.get(rem.size()-1).WhatIDid;
                        System.out.println("Rem List : "+rem.get(rem.size()-1).WhatIDid.size());
                        System.out.println("Target List : "+targetList.size());
                        int z=0,c=0;
                        while(z<targetList.size())
                        {
                            for(int y=0;y<weakPoints.size();y++)
                            {
                                    if(distance(targetList.get(z).getCenter(),weakPoints.get(y).getCenter())<10)
                                        c=1;
                                    if(distance(targetList.get(z).getCenter(),pigs.get(y).getCenter())<10)
                                        c=1;
                            }
                            if(c==0)
                                targetList.remove(z);

                            z++;
                        }
                        if(!targetList.isEmpty()){

                            int target = randomGenerator.nextInt(targetList.size());
                            _tpt = targetList.get(target).getCenter();
                            m.WhatIDid.add(targetList.get(target));
                            targetList.remove(target);

                        }
                        else
                        {
                            if (!weakPoints.isEmpty()) {
                                System.out.println("Weak Pts :-");
                                for (int i = 0; i < weakPoints.size(); i++) {
                                    System.out.print((i + 1) + ": [" + weakPoints.get(i).x + ", " + weakPoints.get(i).y + "] " + weakPoints.get(i).type + "\t");
                                }
                                weakPt = weakPoints.get(0);
                                weakPoints.remove(0);
                                _tpt = weakPt.getCenter();
                                m.whatidid(weakPt);
                            } else {
                                System.out.println("The pigs :-");
                                int min = 0;
                                int i = 0;
                                for (i = 0; i < pigs.size(); i++) {
                                    System.out.println("Pig " + (i + 1) + " :" + pigs.get(i).x + ", " + pigs.get(i).y);
                                    if (pigs.get(min).y > pigs.get(i).y) {
                                        min = i;
                                    }
                                }
                                ABObject pig = pigs.get(min);
                                _tpt = pig.getCenter();
                                m.whatidid(pig);
                            }
                        }

                    }

//                    Point _tpt = pig.getCenter();// if the target is very close to before, randomly choose a
                    // point near it

                    if (prevTarget != null && distance(prevTarget, _tpt) < 10) {
                        closeTargetCounter++;
                        double _angle = randomGenerator.nextDouble() * Math.PI * 2;
                        _tpt.x = _tpt.x + (int) (Math.cos(_angle) * 10);
                        _tpt.y = _tpt.y + (int) (Math.sin(_angle) * 10);
                        System.out.print("Randomly changing to " + _tpt);
                        if(closeTargetCounter == 2){
                            System.out.print(" and using higher trajectory.");
                            _tpt.x = _tpt.x - 10;
                            _tpt.y = _tpt.y + 10;
                            useHighTrajectory = true;
                            tempTraj = true;
                            closeTargetCounter = 0;
                        }
                        System.out.println();
                    }

                    prevTarget = new Point(_tpt.x, _tpt.y);

                    // estimate the trajectory
                    ArrayList<Point> pts = tp.estimateLaunchPoint(sling, _tpt);

                    //-->Commenting this for the 1st Assignment: to choosing higher trajectory<--
                    // do a high shot when entering a level to find an accurate velocity
//					if (firstShot && pts.size() > 1)
//					{
//						releasePoint = pts.get(1);
//					}
//					else if (pts.size() == 1)
//						releasePoint = pts.get(0);
//					else if (pts.size() == 2)
//					{
//						// randomly choose between the trajectories, with a 1 in
//						// 6 chance of choosing the high one
//						if (randomGenerator.nextInt(6) == 0)
//							releasePoint = pts.get(1);
//						else
//							releasePoint = pts.get(0);
//					}
//					else
//						if(pts.isEmpty())
//						{
//							System.out.println("No release point found for the target");
//							System.out.println("Try a shot with 45 degree");
//							releasePoint = tp.findReleasePoint(sling, Math.PI/4);
//						}

                    //-->Selecting the higher trajectory no matter what for Assignment 1<--
                    if(!useHighTrajectory) {
                        if (pts.isEmpty()) {
                            System.out.println("No release point found for the target");
                            System.out.println("Try a shot with 45 degree");
                            releasePoint = tp.findReleasePoint(sling, Math.PI / 4);
                        } else if (pts.size() == 2) {
                            //System.out.println("Selecting the higher route");
                            //releasePoint = pts.get(1);
                            System.out.println("Selecting the lower route");
                            releasePoint = pts.get(0);//selecting lower route instead
                        } else {
                            System.out.println("Selecting the only route available : " + pts.get(0).toString());
                            releasePoint = pts.get(0);
                        }
                    }
                    else{
                        if (pts.size() == 2) {
                            System.out.println("Selecting the higher route");
                            releasePoint = pts.get(1);
                        }
                        else {
                            if (pts.size() != 0)
                                releasePoint = pts.get(0);
                        }
                        useHighTrajectory = false;
                    }

                    // Get the reference point
                    Point refPoint = tp.getReferencePoint(sling);


                    //Calculate the tapping time according the bird type
                    if (releasePoint != null) {
                        double releaseAngle = tp.getReleaseAngle(sling,
                                releasePoint);
//                        System.out.println("Release Point: " + releasePoint);
//                        System.out.println("Release Angle: "
//                                + Math.toDegrees(releaseAngle));
                        System.out.println("Target pt : ["+_tpt.x+", "+_tpt.y+"]");
                        int tapInterval = 0;
                        switch (aRobot.getBirdTypeOnSling())
                        {

                            case RedBird:
                                tapInterval = 0; break;               // start of trajectory
                            case YellowBird:
                                refPoint.y = refPoint.y + 9;
                                System.out.println("Lowering the yellow brd");
                                if(tempTraj) {
                                    tapInterval = 80 + randomGenerator.nextInt(5);
                                    System.out.println("tapInterval : "+tapInterval);
                                    break; // 80-85% of the way
                                }
                                else {
                                    tapInterval = 78 + randomGenerator.nextInt(5);
                                    System.out.println("*tapInterval : "+tapInterval);
                                    break; // 78-83% of the way
                                }
                            case WhiteBird:
                                tapInterval =  70 + randomGenerator.nextInt(20);break; // 70-90% of the way
                            case BlackBird:
                                tapInterval =  70 + randomGenerator.nextInt(20);break; // 70-90% of the way
                            case BlueBird:
                                tapInterval =  65 + randomGenerator.nextInt(15);break; // 65-80% of the way
                            default:
                                tapInterval =  60;
                        }

                        m.tapIntervals.add(tapInterval);
                        int tapTime = tp.getTapTime(sling, releasePoint, _tpt, tapInterval);
                        dx = (int)releasePoint.getX() - refPoint.x;
                        dy = (int)releasePoint.getY() - refPoint.y;
                        shot = new Shot(refPoint.x, refPoint.y, dx, dy, 0, tapTime);
                    }
                    else
                    {
                        System.err.println("No Release Point Found");
                        return state;
                    }
                }

                // check whether the slingshot is changed. the change of the slingshot indicates a change in the scale.
                {
                    ActionRobot.fullyZoomOut();
                    screenshot = ActionRobot.doScreenShot();
                    vision = new Vision(screenshot);
                    Rectangle _sling = vision.findSlingshotMBR();
                    if(_sling != null)
                    {
                        double scale_diff = Math.pow((sling.width - _sling.width),2) +  Math.pow((sling.height - _sling.height),2);
                        if(scale_diff < 25)
                        {
                            if(dx < 0)
                            {
                                aRobot.cshoot(shot);
                                state = aRobot.getState();
                                if ( state == GameState.PLAYING )
                                {
                                    screenshot = ActionRobot.doScreenShot();
                                    vision = new Vision(screenshot);
                                    List<Point> traj = vision.findTrajPoints();
                                    tp.adjustTrajectory(traj, sling, releasePoint);
                                    firstShot = false;
                                }
                            }
                        }
                        else
                            System.out.println("Scale is changed, can not execute the shot, will re-segement the image");
                    }
                    else
                        System.out.println("no sling detected, can not execute the shot, will re-segement the image");
                }

            }
        else
             m.ifwin(true);
        }
        return state;
    }

    public int[][] givePriorityWeekPoints(List<ABObject> weakPoints)
    {
        System.out.println("Number of weak points" + weakPoints.size());
        List<ABObject> priorityList;
        int[][] target = new int[weakPoints.size()][2];
        int i = 0;
        for (ABObject obj : weakPoints)
        {
            if(obj.getType() == ABType.Pig|| obj.getType() == ABType.TNT)
            {
                target[i][0] = i;
                target[i][1] = obj.area*10;
                ++i;
            }
            else
            {
                target[i][0] = i;
                target[i][1] = obj.area;
                ++i;
            }
            int [][] obj1 =  new int[weakPoints.size()][2];
            i=0;
            int k=0;
            int count=0;
            for (i = 0;i<weakPoints.size();++i)
            {
                for (k  = 0;k<weakPoints.size();++k) {
                    System.out.println("#");
                    System.out.println("i"+i+""+target[i][0]);
                    System.out.println("k"+k+""+target[k][0]);
                    if(distance(weakPoints.get(target[i][0]).getCenter(), weakPoints.get(target[k][0]).getCenter())<20)
                    {
                        count++;
                    }
                }
                System.out.println("*");
//                target[i][1] = target[i][1]*count;
                count=0;

            }

        }
        return target;
    }

    public static void main(String args[]) {

        NaiveAgent na = new NaiveAgent();

        if (args.length > 0)
            na.currentLevel = Integer.parseInt(args[0]);
        na.run();

    }
}
