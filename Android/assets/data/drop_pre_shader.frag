#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif

varying LOWP vec4 v_color;

void main() {
    vec2 coord = vec2(gl_PointCoord.x - 0.5, gl_PointCoord.y - 0.5);
    float len = length(coord);

    if(len <= 0.5) {
        gl_FragColor = v_color * vec4(1, 1, 1, 1.0 - len * 2.0);
    } else {
        gl_FragColor = vec4(0, 0, 0, 0);
    }
}