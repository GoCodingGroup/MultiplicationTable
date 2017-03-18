#version 120

//the position of the vertex as specified by our renderer
attribute vec3 Position;
//the color of the vertex as specified by renderer
varying vec4 colorv;

void main() {
    //pass along the position
    //gl_Position = vec4(0.2*Position, 1.0);
    gl_Position = vec4((0.4*Position[0]) - 0.5, 0.6*Position[1], 0.4*Position[2], 1.0);
    //pass along the color
    colorv = gl_Color;
}