#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif
varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

void main() {
    vec4 c = v_color * texture2D(u_texture, v_texCoords);
    if(c.a > 0.27)
        c.a = 0.3;
    else if(c.a > 0.2) {
        c.a = 0.8;
    } else
        c.a = 0.0;
    gl_FragColor = c;
}