#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif
varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float u_time;

float rand(vec2 co){
    float a = u_time;
    return fract(dot(co.xy, vec2(cos(co.x), sin(3.14)))+u_time*0.4);
}

void main() {
    vec4 c = v_color * texture2D(u_texture, v_texCoords);
    if(c.a > 0.26)
        c.a = 0.3;
    else if(c.a > 0.2) {
        c.a = 0.8;
    } else
        c.a = 0.0;
    gl_FragColor = c;

    if(1.0-rand(gl_FragCoord.xy) < 0.02 && c.a < 0.8) {
        gl_FragColor.b = max(1, c.a);
        gl_FragColor.a = 0.8;
    }
}