/*
Copyright 2010-2013 Michael Shick

This file is part of 'Lock Pattern Generator'.

'Lock Pattern Generator' is free software: you can redistribute it and/or
modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or (at your option)
any later version.

'Lock Pattern Generator' is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details.

You should have received a copy of the GNU General Public License along with
'Lock Pattern Generator'.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.example.haotian.haotianalp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PatternGenerator
{
    protected int mGridLength;
    protected int mMinNodes;
    protected int mMaxNodes;
    protected Random mRng;
    protected List<Point> mAllNodes;

    public PatternGenerator()
    {
        mRng = new Random();
        setGridLength(3); //Set to a 3x3 grid
        setMinNodes(Defaults.PATTERN_MIN);
        setMaxNodes(Defaults.PATTERN_MAX);
    }

    public List<Point> getPattern()
    {

//        int patternLength = mRng.nextInt(mMaxNodes - mMinNodes) + mMinNodes; //Generate random pattern length between min and max
        List<Point> pattern = new ArrayList<Point>(); // List contains the lock pattern
//        List<Point> availablePoints = new ArrayList<Point>(mAllNodes); // Points that are not part of the lock pattern yet
//        List<Point> candidatePoints; // Points that could possibly be the next node in the lock pattern
//        ArrayList<Point> toRemove; // Points that can not be the next node in the lock pattern
//
//        int nextNodeNumber = mRng.nextInt(9); //Generates random # [0,9)
//        Point currentNode = availablePoints.get(nextNodeNumber); // Selects randomly chosen node to be next in the lock pattern
//        availablePoints.remove(currentNode); //Remove start node from allNodes list
//        pattern.add(currentNode); //Add start node to pattern list
//        candidatePoints = new ArrayList<Point>(availablePoints); //Start with all remaining nodes as possible candidates for the next step of the pattern
//
//        int dX, dY, gcd, possibleUnusedX, possibleUnusedY; //declare variables used in loop
//
//
//        while (pattern.size() != patternLength) { //Loop until desired pattern size is reached
//            toRemove = new ArrayList<Point>();
//            for (Point candidate : candidatePoints) { //Go through each candidate
//                dX = candidate.x - currentNode.x; //Calculate deltaX and deltaY values for candidate
//                dY = candidate.y - currentNode.y;
//                gcd = computeGcd(dX, dY); //Check if candidate is adjacent or not via gcd
//                for (int j = 1; j < gcd; j++) { //If node isn't adjacent (i.e. gcd > 1), then check for unused nodes in path
//                    possibleUnusedX = currentNode.x + dX/gcd*j; //Calculate the X and Y position of unused node that lays in path between current pattern node and candidate node
//                    possibleUnusedY = currentNode.y + dY/gcd*j;
//                    for (Point find : candidatePoints) { //Check if the node in the calculated X,Y position is in the candidate list
//                        if (find.x == possibleUnusedX && find.y == possibleUnusedY) {
//                            toRemove.add(candidate); //If there is an unused node between the current pattern node and the candidate node, the candidate is not valid
//                        }
//                    }
//                }
//            }
//
//            //Remove points from the candidate list that are not valid choices
//            for (Point remove : toRemove) {
//                candidatePoints.remove(remove);
//            }
//
//            nextNodeNumber = mRng.nextInt(candidatePoints.size()); //Randomly select next node in pattern from candidate list
//            currentNode = candidatePoints.get(nextNodeNumber);
//            availablePoints.remove(currentNode); //Remove next node from allNodes list
//            pattern.add(currentNode); //Add next node to pattern list
//            candidatePoints = new ArrayList<Point>(availablePoints); //Reset candidates list to all nodes that are not in the pattern
//        }
        Point first = new Point(1,0);
        Point second = new Point(1,1);
        Point third = new Point(0,2);
        Point fourth = new Point(0,1);
        //Point fifth = new Point(1,0);
        //Point sixth = new Point(0,0);

        pattern.add(first);
        pattern.add(second);
        pattern.add(third);
        pattern.add(fourth);
        //pattern.add(fifth);
        //pattern.add(sixth);

        return pattern;
    }

    //
    // Accessors / Mutators
    //

    public void setGridLength(int length)
    {
        // build the prototype set to copy from later
        List<Point> allNodes = new ArrayList<Point>();
        for(int y = 0; y < length; y++)
        {
            for(int x = 0; x < length; x++)
            {
                allNodes.add(new Point(x,y));
            }
        }
        mAllNodes = allNodes;

        mGridLength = length;
    }
    public int getGridLength()
    {
        return mGridLength;
    }

    public void setMinNodes(int nodes)
    {
        mMinNodes = nodes;
    }
    public int getMinNodes()
    {
        return mMinNodes;
    }

    public void setMaxNodes(int nodes)
    {
        mMaxNodes = nodes;
    }
    public int getMaxNodes()
    {
        return mMaxNodes;
    }

    //
    // Helper methods
    //

    public static int computeGcd(int a, int b)
    /* Implementation taken from
     * http://en.literateprograms.org/Euclidean_algorithm_(Java)
     * Accessed on 12/28/10
     */
    {
        if(b > a)
        {
            int temp = a;
            a = b;
            b = temp;
        }

        while(b != 0)
        {
            int m = a % b;
            a = b;
            b = m;
        }

        return Math.abs(a);
    }
}
