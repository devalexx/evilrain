attribute vec4 a_color;
varying vec4 v_color;
attribute vec4 a_position;

uniform float particlesize;
uniform float scale;
uniform mat4 u_projTrans;

void main() {
   gl_Position = u_projTrans * vec4(a_position.xy, 0.0, 1.0);
   gl_PointSize = scale * particlesize;
   v_color = a_color;
}