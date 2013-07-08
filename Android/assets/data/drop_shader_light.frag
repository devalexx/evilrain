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
    if(c.b > 0.5)
        gl_FragColor = vec4(0,0,0.5,1);
    else if(c.b > 0.4 && c.b < 0.5)
        gl_FragColor = vec4(0,0,0.6,1);
    else if(c.b > 0.3 && c.b < 0.4)
        gl_FragColor = vec4(0,0,0.7,1);
    else if(c.b > 0.2 && c.b < 0.3)
        gl_FragColor = vec4(0,0,0.8,1);
    else if(c.b > 0.1 && c.b < 0.2)
        gl_FragColor = vec4(0,0,0.3,1);
    else
        gl_FragColor = vec4(0,0,0,0);

}