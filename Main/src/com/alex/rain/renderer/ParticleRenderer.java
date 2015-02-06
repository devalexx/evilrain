/*******************************************************************************
 * Copyright 2015 See AUTHORS file.
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
package com.alex.rain.renderer;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import finnstr.libgdx.liquidfun.ParticleSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;

public class ParticleRenderer {

    protected ShaderProgram shader;
    protected Mesh mesh;

    public ParticleRenderer(Color color, int maxParticleNumber) {
        shader = createShader(color);
        setMaxParticleNumber(maxParticleNumber);
    }

    public void setMaxParticleNumber(int pCount) {
        if(mesh != null) mesh.dispose();
        mesh = new Mesh(false, pCount, pCount,
                new VertexAttribute(VertexAttributes.Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE));
    }

    public int getMaxParticleNumber() {
        return mesh.getMaxVertices();
    }

    public void render (ParticleSystem pSystem, float pRadiusScale, Matrix4 pProjMatrix) {
        if(pSystem == null)
            return;

        Gdx.gl20.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl20.glEnable(GL20.GL_VERTEX_PROGRAM_POINT_SIZE);
        Gdx.gl20.glEnable(0x8861); //GL11.GL_POINT_SPRITE_OES

        shader.begin();
        shader.setUniformf("particlesize", pSystem.getParticleRadius());
        shader.setUniformf("scale", pRadiusScale*2);
        shader.setUniformMatrix("u_projTrans", pProjMatrix);

        mesh.setVertices(pSystem.getParticlePositionAndColorBufferArray(true));
        mesh.render(shader, GL20.GL_POINTS, 0, pSystem.getParticleCount());
        shader.end();
        Gdx.gl20.glDisable(0x8861);
    }

    public void dispose() {
        shader.dispose();
        mesh.dispose();
    }

    static final public ShaderProgram createShader(Color pColor) {
        String prefix = "";
        if(Gdx.app.getType() == Application.ApplicationType.Desktop)
            prefix +="#version 120\n";
        else
            prefix +="#version 100\n";

        final String vertexShader =
                "attribute vec4 a_color;" +
                        "varying vec4 v_color;" +
                        "attribute vec4 a_position;\n" //
                        + "\n" //
                        + "uniform float particlesize;\n" //
                        + "uniform float scale;\n"
                        + "uniform mat4 u_projTrans;\n" //
                        + "\n" //
                        + "void main()\n" //
                        + "{\n" //
                        + "   gl_Position =  u_projTrans * vec4(a_position.xy, 0.0, 1.0);\n" //
                        + "   gl_PointSize = scale * particlesize;\n"
                        + "   v_color = a_color;"
                        + "}\n";
        final String fragmentShader = "#ifdef GL_ES\n" //
                + "#define LOWP lowp\n" //
                + "precision mediump float;\n" //
                + "#else\n" //
                + "#define LOWP \n" //
                + "#endif\n" +
                "varying LOWP vec4 v_color;" //
                + "void main()\n"//
                + "{\n" //
                + " float len = length(vec2(gl_PointCoord.x - 0.5, gl_PointCoord.y - 0.5));\n" //
                + " if(len <= 0.52) {\n" //
                + " 	gl_FragColor = v_color * vec4(1, 1, 1, 1 - len * 1.92);\n" //
                + " } else {\n" //
                + " 	gl_FragColor = vec4(0, 0, 0, 0);\n" //
                + " }\n"
                + "}";

        ShaderProgram shader = new ShaderProgram(prefix + vertexShader,
                prefix + fragmentShader);
        if (shader.isCompiled() == false) {
            Gdx.app.log("ERROR", shader.getLog());
        }

        return shader;
    }
}
