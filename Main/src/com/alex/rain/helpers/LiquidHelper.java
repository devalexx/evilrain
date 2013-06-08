package com.alex.rain.helpers;

import com.alex.rain.hashgrid.HashGrid;
import com.alex.rain.models.Drop;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Alexander Shubenkov
 * @since: 01.06.13
 */

public class LiquidHelper {
    public final float RADIUS = 35f;
    public final float IDEAL_RADIUS = 200f;
    public final float MULTIPLIER = IDEAL_RADIUS / RADIUS;
    public final float IDEAL_RADIUS_SQ = IDEAL_RADIUS * IDEAL_RADIUS;
    public final float VISCOSITY = 0.004f;
    public final float EPSILON = 0.001f;
    private final List<Drop> dropList;
    private Map<Drop, Integer> dropIndexMap;
    private HashGrid<Drop> grid;
    private Vector2[] _delta;
    private Vector2[] _scaledPositions;
    private Vector2[] _scaledVelocities;
    private Vector2 change = new Vector2();
    private Vector2 relativePosition = new Vector2();

    public LiquidHelper(List<Drop> dropList) {
        this.dropList = dropList;
        dropIndexMap = new HashMap<Drop, Integer>();
        grid = new HashGrid<Drop>(800, 480, 25);
    }

    private HashGrid<Drop> createHashGrid() {
        grid.updateAll();

        if(grid.size() != dropList.size())
            for(int i = grid.size(); i < dropList.size(); i++)
                grid.add(dropList.get(i));

        return grid;
    }

    private void prepareArrays() {
        if(_delta == null || _delta.length != dropList.size()) {
            _delta = new Vector2[dropList.size()];
            _scaledPositions = new Vector2[dropList.size()];
            _scaledVelocities = new Vector2[dropList.size()];
            dropIndexMap.clear();
        }
    }

    private void prepareSimulation() {
        for (int i = 0; i < dropList.size(); i++) {
            Drop particle = dropList.get(i);
            _scaledPositions[i] = particle.getPosition().cpy().mul(MULTIPLIER);
            _scaledVelocities[i] = particle.getLinearVelocity().cpy().mul(MULTIPLIER);
            _delta[i] = new Vector2();
            if(_delta.length != dropIndexMap.size()) {
                dropIndexMap.put(particle, i);
            }
        }
    }

    public void applyLiquidConstraint(float deltaT) {
        createHashGrid();

        prepareArrays();
        prepareSimulation();

        float p, pnear, pressure, presnear;
        for (int i = 0; i < dropList.size(); i++) {
            Drop particle = dropList.get(i);

            // Calculate pressure
            p = 0.0f;
            pnear = 0.0f;
            Object[] neighbors = grid.get(particle.getPosition()).toArray();
            float[] distances = new float[neighbors.length];
            for (int a = 0; a < neighbors.length; a++) {
                int i2 = dropIndexMap.get(neighbors[a]);
                relativePosition.set(_scaledPositions[i2]).sub(_scaledPositions[i]);
                float distanceSq = relativePosition.len2();

                //within idealRad check
                if (distanceSq < IDEAL_RADIUS_SQ) {
                    distances[a] = (float)Math.sqrt(distanceSq);
                    if (distances[a] < EPSILON)
                        distances[a] = IDEAL_RADIUS - 0.01f;
                    float oneminusq = 1.0f - (distances[a] / IDEAL_RADIUS);
                    p = (p + oneminusq * oneminusq);
                    pnear = (pnear + oneminusq * oneminusq * oneminusq);
                } else {
                    distances[a] = Float.MAX_VALUE;
                }
            }

            // Apply forces
            pressure = (p - 5f) / 2.0f; //normal pressure term
            presnear = pnear / 2.0f; //near particles term
            change.set(0, 0);
            for (int a = 0; a < neighbors.length; a++) {
                int i2 = dropIndexMap.get(neighbors[a]);
                relativePosition.set(_scaledPositions[i2]).sub(_scaledPositions[i]);

                if (distances[a] < IDEAL_RADIUS) {
                    float oneminusq = 1.0f - distances[a] / IDEAL_RADIUS;
                    float factor = oneminusq * (pressure + presnear * oneminusq) / (2.0F * distances[a]);
                    Vector2 d = relativePosition.mul(factor);
                    Vector2 relativeVelocity = _scaledVelocities[i2].tmp().sub(_scaledVelocities[i]);

                    factor = VISCOSITY * oneminusq * deltaT;
                    d.sub(relativeVelocity.mul(factor));
                    _delta[i2].add(d);
                    change.sub(d);
                }
            }
            _delta[i].add(change);
        }

        moveParticles(/*deltaT*/1/60f); // TODO: wrong?

    }

    private void moveParticles(float deltaT) {
        for (int i = 0; i < dropList.size(); i++) {
            Drop particle = dropList.get(i);

            particle.setPosition(particle.getPosition().tmp().add(_delta[i].div(MULTIPLIER)));
            particle.setLinearVelocity(particle.getLinearVelocity().tmp().add(_delta[i].div((MULTIPLIER * deltaT))));
        }
    }
}
