varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

void main() {
    vec4 c = texture2D(u_texture, v_texCoords);
    if(c.b >= 0.8)
        gl_FragColor = vec4(0,0,0.9,0.5);
    else
        gl_FragColor = vec4(0,0,0,0);
}