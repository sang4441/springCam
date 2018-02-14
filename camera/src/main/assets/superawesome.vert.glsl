//position
attribute vec4 position;

//camera transform and texture
uniform mat4 camTextureTransform;
attribute vec4 camTexCoordinate;
attribute vec4 camTexCoordinateTwo;

//tex coords
varying vec2 v_CamTexCoordinate;
varying vec2 v_CamTexCoordinateTwo;




void main()
{
    //camera texcoord needs to be manipulated by the transform given back from the system
    v_CamTexCoordinate = (camTextureTransform * camTexCoordinate).xy;
    v_CamTexCoordinateTwo =  (camTextureTransform * camTexCoordinateTwo).xy;
    gl_Position = position;
//    gl_Position = Distort(position);
}