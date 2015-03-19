/*******************************************************************************
 * Copyright 2013 See AUTHORS file.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE V3
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.alex.rain.helpers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import java.util.ArrayList;
import java.util.List;

public class EarClippingSeparatorHelper implements SeparatorHelper {
    /*
     * Triangulates a polygon using simple O(N^2) ear-clipping algorithm
     * Returns a Triangle array unless the polygon can't be triangulated,
     * in which case null is returned.  This should only happen if the
     * polygon self-intersects, though it will not _always_ return null
     * for a bad polygon - it is the caller's responsibility to check for
     * self-intersection, and if it doesn't, it should at least check
     * that the return value is non-null before using.  You're warned!
     */

    Triangle[] triangulatePolygon(float[] xv, float[] yv, int vNum){
        if (vNum < 3) return null;

        Triangle[] buffer = new Triangle[vNum];
        int bufferSize = 0;
        float[] xrem = new float[vNum];
        float[] yrem = new float[vNum];
        for (int i=0; i<vNum; ++i){
            xrem[i] = xv[i];
            yrem[i] = yv[i];
        }

        while (vNum > 3){
            //Find an ear
            int earIndex = -1;
            for (int i=0; i<vNum; ++i){
                if (isEar(i,xrem,yrem)) {
                    earIndex = i;
                    break;
                }
            }

            //If we still haven't found an ear, we're screwed.
            //The user did Something Bad, so return null.
            //This will probably crash their program, since
            //they won't bother to check the return value.
            //At this we shall laugh, heartily and with great gusto.
            if (earIndex == -1) return null;


            //Clip off the ear:
            //  - remove the ear tip from the list

            //Opt note: actually creates a new list, maybe
            //this should be done in-place instead.  A linked
            //list would be even better to avoid array-fu.
            --vNum;
            float[] newx = new float[vNum];
            float[] newy = new float[vNum];
            int currDest = 0;
            for (int i=0; i<vNum; ++i){
                if (currDest == earIndex) ++currDest;
                newx[i] = xrem[currDest];
                newy[i] = yrem[currDest];
                ++currDest;
            }

            //  - add the clipped triangle to the triangle list
            int under = (earIndex==0)?(xrem.length-1):(earIndex-1);
            int over = (earIndex==xrem.length-1)?0:(earIndex+1);

            Triangle toAdd = new Triangle(xrem[earIndex],yrem[earIndex],xrem[over],yrem[over],xrem[under],yrem[under]);
            buffer[bufferSize] = toAdd;
            ++bufferSize;

            //  - replace the old list with the new one
            xrem = newx;
            yrem = newy;
        }
        Triangle toAdd = new Triangle(xrem[1],yrem[1],xrem[2],yrem[2],xrem[0],yrem[0]);
        buffer[bufferSize] = toAdd;
        ++bufferSize;

        Triangle[] res = new Triangle[bufferSize];
        System.arraycopy(buffer, 0, res, 0, bufferSize);
        return res;
    }

    Polygon[] polygonizeTriangles(Triangle[] triangulated){
        Polygon[] polys;
        int polyIndex = 0;

        if (triangulated == null){
            return null;
        } else{
            polys = new Polygon[triangulated.length];
            boolean[] covered = new boolean[triangulated.length];
            for (int i=0; i<triangulated.length; i++){
                covered[i] = false;
            }

            boolean notDone = true;

            while(notDone){
                int currTri = -1;
                for (int i=0; i<triangulated.length; i++){
                    if (covered[i]) continue;
                    currTri = i;
                    break;
                }
                if (currTri == -1){
                    notDone = false;
                } else{
                    Polygon poly = new Polygon(triangulated[currTri]);
                    covered[currTri] = true;
                    for (int i=0; i<triangulated.length; i++){
                        if (covered[i]) continue;
                        Polygon newP = poly.add(triangulated[i]);
                        if (newP == null) continue;
                        if (newP.isConvex()){
                            poly = newP;
                            covered[i] = true;
                        }
                    }
                    polys[polyIndex] = poly;
                    polyIndex++;
                }
            }
        }
        Polygon[] ret = new Polygon[polyIndex];
        for (int i=0; i<polyIndex; i++){
            ret[i] = polys[i];
        }
        return ret;
    }

    //Checks if vertex i is the tip of an ear
    boolean isEar(int i, float[] xv, float[] yv){
        float dx0,dy0,dx1,dy1;
        dx0=dy0=dx1=dy1=0;
        if (i >= xv.length || i < 0 || xv.length < 3){
            return false;
        }
        int upper = i+1;
        int lower = i-1;
        if (i == 0){
            dx0 = xv[0] - xv[xv.length-1]; dy0 = yv[0] - yv[yv.length-1];
            dx1 = xv[1] - xv[0]; dy1 = yv[1] - yv[0];
            lower = xv.length-1;
        } else if (i == xv.length-1){
            dx0 = xv[i] - xv[i-1]; dy0 = yv[i] - yv[i-1];
            dx1 = xv[0] - xv[i]; dy1 = yv[0] - yv[i];
            upper = 0;
        } else{
            dx0 = xv[i] - xv[i-1]; dy0 = yv[i] - yv[i-1];
            dx1 = xv[i+1] - xv[i]; dy1 = yv[i+1] - yv[i];
        }
        float cross = dx0*dy1-dx1*dy0;
        if (cross > 0) return false;
        Triangle myTri = new Triangle(xv[i],yv[i],xv[upper],yv[upper],xv[lower],yv[lower]);
        for (int j=0; j<xv.length; ++j){
            if (j==i || j == lower || j == upper) continue;
            if (myTri.isInside(xv[j],yv[j])) return false;
        }
        return true;
    }

    class Triangle{

        public float[] x;
        public float[] y;

        public Triangle(float x1, float y1, float x2, float y2, float x3, float y3){
            this();
            float dx1 = x2-x1;
            float dx2 = x3-x1;
            float dy1 = y2-y1;
            float dy2 = y3-y1;
            float cross = dx1*dy2-dx2*dy1;
            boolean ccw = (cross>0);
            if (ccw){
                x[0] = x1; x[1] = x2; x[2] = x3;
                y[0] = y1; y[1] = y2; y[2] = y3;
            } else{
                x[0] = x1; x[1] = x3; x[2] = x2;
                y[0] = y1; y[1] = y3; y[2] = y2;
            }
        }

        public Triangle(){
            x = new float[3];
            y = new float[3];
        }

        public boolean isInside(float _x, float _y){
            float vx2 = _x-x[0]; float vy2 = _y-y[0];
            float vx1 = x[1]-x[0]; float vy1 = y[1]-y[0];
            float vx0 = x[2]-x[0]; float vy0 = y[2]-y[0];

            float dot00 = vx0*vx0+vy0*vy0;
            float dot01 = vx0*vx1+vy0*vy1;
            float dot02 = vx0*vx2+vy0*vy2;
            float dot11 = vx1*vx1+vy1*vy1;
            float dot12 = vx1*vx2+vy1*vy2;
            float invDenom = 1.0f / (dot00*dot11 - dot01*dot01);
            float u = (dot11*dot02 - dot01*dot12)*invDenom;
            float v = (dot00*dot12 - dot01*dot02)*invDenom;

            return ((u>0)&&(v>0)&&(u+v<1));
        }

    }

    class Polygon{

        public float[] x;
        public float[] y;
        public int nVertices;

        public Polygon(float[] _x, float[] _y){
            nVertices = _x.length;
            x = new float[nVertices];
            y = new float[nVertices];
            for (int i=0; i<nVertices; ++i){
                x[i] = _x[i];
                y[i] = _y[i];
            }
        }

        public Polygon(Triangle t){
            this(t.x,t.y);
        }

        public void set(Polygon p){
            nVertices = p.nVertices;
            x = new float[nVertices];
            y = new float[nVertices];
            for (int i=0; i<nVertices; ++i){
                x[i] = p.x[i];
                y[i] = p.y[i];
            }
        }

        /*
         * Assuming the polygon is simple, checks
         * if it is convex.
         */
        public boolean isConvex(){
            boolean isPositive = false;
            for (int i=0; i<nVertices; ++i){
                int lower = (i==0)?(nVertices-1):(i-1);
                int middle = i;
                int upper = (i==nVertices-1)?(0):(i+1);
                float dx0 = x[middle]-x[lower];
                float dy0 = y[middle]-y[lower];
                float dx1 = x[upper]-x[middle];
                float dy1 = y[upper]-y[middle];
                float cross = dx0*dy1-dx1*dy0;
                //Cross product should have same sign
                //for each vertex if poly is convex.
                boolean newIsP = (cross>0)?true:false;
                if (i==0){
                    isPositive = newIsP;
                } else if (isPositive != newIsP){
                    return false;
                }
            }
            return true;
        }

        /*
         * Tries to add a triangle to the polygon.
         * Returns null if it can't connect properly.
         * Assumes bitwise equality of join vertices.
         */
        public Polygon add(Triangle t){
            //First, find vertices that connect
            int firstP = -1;
            int firstT = -1;
            int secondP = -1;
            int secondT = -1;
            for (int i=0; i < nVertices; i++){
                if (t.x[0] == x[i] && t.y[0] == y[i]){
                    if (firstP == -1){
                        firstP = i; firstT = 0;
                    } else{
                        secondP = i; secondT = 0;
                    }
                } else if (t.x[1] == x[i] && t.y[1] == y[i]){
                    if (firstP == -1){
                        firstP = i; firstT = 1;
                    } else{
                        secondP = i; secondT = 1;
                    }
                } else if (t.x[2] == x[i] && t.y[2] == y[i]){
                    if (firstP == -1){
                        firstP = i; firstT = 2;
                    } else{
                        secondP = i; secondT = 2;
                    }
                }
            }
            //Fix ordering if first should be last vertex of poly
            if (firstP == 0 && secondP == nVertices-1){
                firstP = nVertices-1;
                secondP = 0;
            }

            //Didn't find it
            if (secondP == -1) return null;

            //Find tip index on triangle
            int tipT = 0;
            if (tipT == firstT || tipT == secondT) tipT = 1;
            if (tipT == firstT || tipT == secondT) tipT = 2;

            float[] newx = new float[nVertices+1];
            float[] newy = new float[nVertices+1];
            int currOut = 0;
            for (int i=0; i<nVertices; i++){
                newx[currOut] = x[i];
                newy[currOut] = y[i];
                if (i == firstP){
                    ++currOut;
                    newx[currOut] = t.x[tipT];
                    newy[currOut] = t.y[tipT];
                }
                ++currOut;
            }
            return new Polygon(newx,newy);
        }
    }


    public void separate(Body body, FixtureDef fixtureDef, List<Vector2> verticesVec, float scale) {
        PolygonShape polyShape;
        float[] x = new float[verticesVec.size()];
        float[] y = new float[verticesVec.size()];

        for(int i = 0; i < verticesVec.size(); i++) {
            x[i] = verticesVec.get(i).x;
            y[i] = -verticesVec.get(i).y;
        }

        Triangle[] triangulated = triangulatePolygon(x,y,verticesVec.size());

        if(triangulated == null)
            return;

        Polygon[] polygons = polygonizeTriangles(triangulated);

        if(polygons == null)
            return;

        for(Polygon p : polygons) {
            Vector2[] vertices = new Vector2[p.nVertices];
            for(int i = 0; i < p.nVertices; i++)
                vertices[i] = new Vector2(p.x[i], -p.y[i]);

            polyShape = new PolygonShape();
            polyShape.set(vertices);
            fixtureDef.shape = polyShape;
            body.createFixture(fixtureDef);
        }
    }

    public List<List<Vector2>> getSeparated(List<Vector2> verticesVec, float scale) {
        List<List<Vector2>> listOfList = new ArrayList<List<Vector2>>();

        float[] x = new float[verticesVec.size()];
        float[] y = new float[verticesVec.size()];

        for(int i = 0; i < verticesVec.size(); i++) {
            x[i] = verticesVec.get(i).x;
            y[i] = -verticesVec.get(i).y;
        }

        Triangle[] triangulated = triangulatePolygon(x,y,verticesVec.size());

        if(triangulated == null)
            return listOfList;

        Polygon[] polygons = polygonizeTriangles(triangulated);

        if(polygons == null)
            return listOfList;

        for(Polygon p : polygons) {
            List<Vector2> vertices = new ArrayList<Vector2>();
            for(int i = 0; i < p.nVertices; i++)
                vertices.add(new Vector2(p.x[i], -p.y[i]));
            listOfList.add(vertices);
        }

        return listOfList;
    }

    public int validate(List<Vector2> verticesVec) {
        float[] x = new float[verticesVec.size()];
        float[] y = new float[verticesVec.size()];

        for(int i = 0; i < verticesVec.size(); i++) {
            x[i] = verticesVec.get(i).x;
            y[i] = -verticesVec.get(i).y;
        }

        Triangle[] triangulated = triangulatePolygon(x,y,verticesVec.size());

        if(triangulated == null)
            return 1;

        Polygon[] polygons = polygonizeTriangles(triangulated);

        if(polygons == null)
            return 1;

        return 0;
    }
}
