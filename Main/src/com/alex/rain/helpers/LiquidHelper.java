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

import com.alex.rain.models.Drop;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.*;

/**
 * Based on www.jbox2d.org liquid demo
 */

public class LiquidHelper {
    private final List<Drop> dropList;
    private int dropListSize;
    private final float fluidMinX = 0f;
    private final  float fluidMaxX = 800f;
    private final float fluidMinY = 0f;
    private final float fluidMaxY = 480f;
    private final int hashWidthCount, hashHeightCount;
    private final float hashColWidth, hashColHeight;
    private final int [][][] hash;
    private final int [][] hashSize;
    private final int MAX_NUMBER = 400;
    private final float VISCOSITY = 0.004f;
    private final float RADIUS;
    private final float IDEAL_RADIUS;
    private final float IDEAL_RADIUS_SQ;
    private final float MULTIPLIER;
    private final float EPSILON = 0.001f;
    private final int[] neighbors = new int[MAX_NUMBER];
    private final float[] vlen = new float[MAX_NUMBER];
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    private float[] xchange;
    private float[] ychange;
    private float[] xs;
    private float[] ys;
    private float[] vxs;
    private float[] vys;

    float cellWidth, cellHeight;
    public LiquidHelper(ArrayList<Drop> dropList, boolean lightVersion) {
        this.dropList = dropList;
        dropListSize = dropList.size();
        RADIUS = lightVersion ? 40f : 30f;
        IDEAL_RADIUS = lightVersion ? 400f : 300f;
        hashWidthCount = lightVersion ? 48 : 38;
        hashHeightCount = lightVersion ? 34 : 24;

        float hashWidthCountE = hashWidthCount - 0.001f;
        float hashHeightCountE = hashHeightCount - 0.001f;
        hashColWidth = hashWidthCountE / fluidMaxX;
        hashColHeight = hashHeightCountE / fluidMaxY;

        MULTIPLIER = IDEAL_RADIUS / RADIUS;
        IDEAL_RADIUS_SQ = IDEAL_RADIUS * IDEAL_RADIUS;

        cellWidth = fluidMaxX / hashWidthCount;
        cellHeight = fluidMaxY / hashHeightCount;
        hash = new int[hashWidthCount][hashHeightCount][MAX_NUMBER];
        hashSize = new int[hashWidthCount][hashHeightCount];

        createRequiredData();
    }

    private int hashX(float x) {
        return (int)(x * hashColWidth);
    }

    private int hashY(float y) {
        return (int)(y * hashColHeight);
    }

    private void hashLocations() {
        for (int a = 0; a < hashWidthCount; a++) {
            for (int b = 0; b < hashHeightCount; b++) {
                hashSize[a][b] = 0;
            }
        }

        for (int a = 0; a < dropListSize; a++) {
            int hcell = hashX(dropList.get(a).getPosition().x);
            int vcell = hashY(dropList.get(a).getPosition().y);
            if (hcell > -1 && hcell < hashWidthCount && vcell > -1 && vcell < hashHeightCount)
                hash[hcell][vcell][hashSize[hcell][vcell]++] = a;
        }
    }

    public void createRequiredData() {
        xchange = new float[dropListSize];
        ychange = new float[dropListSize];
        xs = new float[dropListSize];
        ys = new float[dropListSize];
        vxs = new float[dropListSize];
        vys = new float[dropListSize];
    }

    public void applyLiquidConstraint(final float deltaT) {
        if(dropListSize != dropList.size()) {
            dropListSize = dropList.size();
            createRequiredData();
        }

        hashLocations();
        Arrays.fill(xchange, 0.0f);
        Arrays.fill(ychange, 0.0f);

        for (int i = 0; i < dropListSize; ++i) {
            xs[i] = MULTIPLIER * dropList.get(i).getPosition().x;
            ys[i] = MULTIPLIER * dropList.get(i).getPosition().y;
            vxs[i] = MULTIPLIER * dropList.get(i).getLinearVelocity().x;
            vys[i] = MULTIPLIER * dropList.get(i).getLinearVelocity().y;
        }

        for (int i = 0; i < dropListSize; i++) {
            // Populate the neighbor list from the 9 proximate cells
            int neighborsSize = 0;
            int hcell = hashX(dropList.get(i).getPosition().x);
            int vcell = hashY(dropList.get(i).getPosition().y);
            for (int nx = -1; nx < 2; nx++) {
                int xc = hcell + nx;
                for (int ny = -1; ny < 2; ny++) {
                    int yc = vcell + ny;
                    if (xc > -1 && xc < hashWidthCount && yc > -1 && yc < hashHeightCount && hashSize[xc][yc] > 0) {
                        for (int a = 0; a < hashSize[xc][yc]; a++) {
                            int ne = hash[xc][yc][a];
                            if (ne != i)
                                neighbors[neighborsSize++] = ne;
                        }
                    }
                }
            }

            // Particle pressure calculated by particle proximity
            // Pressures = 0 iff all particles within range are IDEAL_RADIUS distance away
            float p = 0.0f;
            float pnear = 0.0f;
            for (int a = 0; a < neighborsSize; a++) {
                int j = neighbors[a];
                float vx = xs[j] - xs[i];
                float vy = ys[j] - ys[i];

                // early exit check
                if (vx > -IDEAL_RADIUS && vx < IDEAL_RADIUS && vy > -IDEAL_RADIUS && vy < IDEAL_RADIUS) {
                    float vlensqr = (vx * vx + vy * vy);
                    // within IDEAL_RADIUS check
                    if (vlensqr < IDEAL_RADIUS_SQ) {
                        vlen[a] = (float) Math.sqrt(vlensqr);
                        if (vlen[a] < EPSILON)
                            vlen[a] = IDEAL_RADIUS - .01f;
                        float oneminusq = 1.0f - (vlen[a] / IDEAL_RADIUS);
                        float oneminusqSq = oneminusq * oneminusq;
                        p += oneminusqSq;
                        pnear += oneminusq * oneminusqSq;
                    } else {
                        vlen[a] = Float.MAX_VALUE;
                    }
                }
            }

            float pressure = (p - 4F) / 2.0F; // normal pressure term
            float presnear = pnear / 2.0F; // near particles term
            for (int a = 0; a < neighborsSize; a++) {
                if (vlen[a] == Float.MAX_VALUE)
                    continue;

                int j = neighbors[a];
                float vx = xs[j] - xs[i];
                float vy = ys[j] - ys[i];
                if (vx > -IDEAL_RADIUS && vx < IDEAL_RADIUS && vy > -IDEAL_RADIUS && vy < IDEAL_RADIUS) {
                    float oneminusq = 1.0f - (vlen[a] / IDEAL_RADIUS);
                    float factor = oneminusq * (pressure + presnear * oneminusq) / (2.0F * vlen[a]);
                    float dx = vx * factor;
                    float dy = vy * factor;
                    float relvx = vxs[j] - vxs[i];
                    float relvy = vys[j] - vys[i];
                    factor = VISCOSITY * oneminusq * deltaT;
                    dx -= relvx * factor;
                    dy -= relvy * factor;

                    xchange[j] += dx;
                    ychange[j] += dy;
                    xchange[i] -= dx;
                    ychange[i] -= dy;
                }
            }
        }

        for (int i = 0; i < dropListSize; i++) {
            // todo: is it correct?
            //dropList.get(i).setPosition(dropList.get(i).getPosition().add(xchange[i] / MULTIPLIER, ychange[i] / MULTIPLIER));
            dropList.get(i).setLinearVelocity(dropList.get(i).getLinearVelocity().add(xchange[i] / (MULTIPLIER * deltaT), ychange[i] / (MULTIPLIER * deltaT)));
        }

    }

    public void drawDebug() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 1, 0, 1);
        for(int i = 0; i < hashWidthCount; i++)
            shapeRenderer.line(i * 800 / hashWidthCount, 0, i * 800 / hashWidthCount, 480);
        for(int j = 0; j < hashHeightCount; j++)
            shapeRenderer.line(0, j * 480 / hashHeightCount, 800, j * 480 / hashHeightCount);
        shapeRenderer.end();
    }
}
