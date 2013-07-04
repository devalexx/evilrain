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

// rain: fract(dot(co.xy, vec2(cos(co.x), sin(3.14)))+u_time);
float rand(vec2 co){
    float a = u_time;
    return fract(dot(co.xy, vec2(cos(3.0), sin(co.y)))+u_time/5.0+sin(co.x));
}

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

    if(1.0-rand(gl_FragCoord.xy) < 0.1 && c.b > 0.2)
        gl_FragColor += vec4(0.05,0.05,0.1,0);
}