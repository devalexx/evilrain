package com.alex.rain.helpers;

import com.alex.rain.models.Drop;

import java.util.*;

/**
 * @author: Alexander Shubenkov
 * @since: 01.06.13
 */

public class LiquidHelper {
    private final List<Drop> dropList;
    private int dropListSize;
    private final float fluidMinX = 0f;
    private final  float fluidMaxX = 800f;
    private final float fluidMinY = 0f;
    private final float fluidMaxY = 480f;
    private final int hashWidth = 30, hashHeight = 20;
    private final ArrayList<Integer>[][] hash = new ArrayList[hashWidth][hashHeight];
    private final float VISCOSITY = 0.004f;
    private final float RADIUS;
    private final float IDEAL_RADIUS;
    private final float IDEAL_RADIUS_SQ;
    private final float MULTIPLIER;
    private final float EPSILON = 0.001f;
    private final ArrayList<Integer> neighbors = new ArrayList<Integer>(100);

    private float[] xchange;
    private float[] ychange;
    private float[] xs;
    private float[] ys;
    private float[] vxs;
    private float[] vys;

    public LiquidHelper(ArrayList<Drop> dropList, boolean lightVersion) {
        this.dropList = dropList;
        dropListSize = dropList.size();
        RADIUS = lightVersion ? 40f : 30f;
        IDEAL_RADIUS = lightVersion ? 500f : 500f;
        MULTIPLIER = IDEAL_RADIUS / RADIUS;
        IDEAL_RADIUS_SQ = IDEAL_RADIUS * IDEAL_RADIUS;

        for (int i = 0; i < hashWidth; ++i) {
            for (int j = 0; j < hashHeight; ++j) {
                hash[i][j] = new ArrayList<Integer>();
            }
        }
        createRequiredData();
    }

    public final static float map(final float val, final float fromMin, final float fromMax,
                final float toMin, final float toMax) {
        final float mult = (val - fromMin) / (fromMax - fromMin);
        final float res = toMin + mult * (toMax - toMin);
        return res;
    }

    private int hashX(float x) {
        float f = map(x, fluidMinX, fluidMaxX, 0, hashWidth - .001f);
        return (int) f;
    }

    private int hashY(float y) {
        float f = map(y, fluidMinY, fluidMaxY, 0, hashHeight - .001f);
        return (int) f;
    }

    private void hashLocations() {
        for (int a = 0; a < hashWidth; a++) {
            for (int b = 0; b < hashHeight; b++) {
                hash[a][b].clear();
            }
        }

        for (int a = 0; a < dropListSize; a++) {
            int hcell = hashX(dropList.get(a).getPosition().x);
            int vcell = hashY(dropList.get(a).getPosition().y);
            if (hcell > -1 && hcell < hashWidth && vcell > -1 && vcell < hashHeight)
                hash[hcell][vcell].add(new Integer(a));
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
            neighbors.clear();
            int hcell = hashX(dropList.get(i).getPosition().x);
            int vcell = hashY(dropList.get(i).getPosition().y);
            for (int nx = -1; nx < 2; nx++) {
                for (int ny = -1; ny < 2; ny++) {
                    int xc = hcell + nx;
                    int yc = vcell + ny;
                    if (xc > -1 && xc < hashWidth && yc > -1 && yc < hashHeight && hash[xc][yc].size() > 0) {
                        for (int a = 0; a < hash[xc][yc].size(); a++) {
                            Integer ne = hash[xc][yc].get(a);
                            if (ne != null && ne != i)
                                neighbors.add(ne);
                        }
                    }
                }
            }

            // Particle pressure calculated by particle proximity
            // Pressures = 0 iff all particles within range are IDEAL_RADIUS distance away
            float[] vlen = new float[neighbors.size()];
            float p = 0.0f;
            float pnear = 0.0f;
            for (int a = 0; a < neighbors.size(); a++) {
                int j = neighbors.get(a);
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
                        p = (p + oneminusqSq);
                        pnear = (pnear + oneminusq * oneminusqSq);
                    } else {
                        vlen[a] = Float.MAX_VALUE;
                    }
                }
            }

            float pressure = (p - 4F) / 2.0F; // normal pressure term
            float presnear = pnear / 2.0F; // near particles term
            float changex = 0.0F;
            float changey = 0.0F;
            for (int a = 0; a < neighbors.size(); a++) {
                int j = neighbors.get(a);
                float vx = xs[j] - xs[i];
                float vy = ys[j] - ys[i];
                if (vx > -IDEAL_RADIUS && vx < IDEAL_RADIUS && vy > -IDEAL_RADIUS && vy < IDEAL_RADIUS) {
                    if (vlen[a] < IDEAL_RADIUS) {
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
                        changex -= dx;
                        changey -= dy;
                    }
                }
            }

            xchange[i] += changex;
            ychange[i] += changey;
        }

        for (int i = 0; i < dropListSize; ++i) {
            dropList.get(i).setPosition(dropList.get(i).getPosition().add(xchange[i] / MULTIPLIER, ychange[i] / MULTIPLIER));
            dropList.get(i).setLinearVelocity(dropList.get(i).getLinearVelocity().add(xchange[i] / (MULTIPLIER * deltaT), ychange[i] / (MULTIPLIER * deltaT)));
        }

    }
}
