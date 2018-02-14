#extension GL_OES_EGL_image_external : require

precision mediump float;
uniform samplerExternalOES camTexture;

varying vec2 v_CamTexCoordinate;
varying vec2 v_CamTexCoordinateTwo;
varying vec2 v_TexCoordinate;

uniform float width;
uniform float height;
uniform float stretch_x_strenth;
uniform float stretch_x_point;
uniform vec2 point_face;
uniform float radius_face;
uniform float strength_face;

const float radius = 100.0;

vec2 stretch(vec2 pos)
{
   float x = pos.x - stretch_x_strenth;
   return vec2(x, pos.y);
}

vec2 Distort(vec2 p)
{
    float theta  = atan(p.y, p.x);
    float radius = length(p);
    float power = strength_face;

    if (radius >= 0.6 && radius < 1.0)
    {
         float percent = (1.0 - (radius - 0.6) / 0.4);
         power =  1.0 + (power - 1.0) * percent;
         radius = radius * power;
         p.x = radius * cos(theta);
         p.y = radius * sin(theta);
    }
    else if (radius < 0.6)
    {
         radius = radius * power;
         p.x = radius * cos(theta);
         p.y = radius * sin(theta);
    }

    return p;
}

vec4 faceResize(vec2 p) {

    vec2 texSize = vec2(height, width);
    vec2 tc = p * texSize;
    vec2 center = point_face / texSize;
    float distFace = length(tc - point_face);
    vec4 c;
    vec4 bgColor;
    if (distFace < radius_face && point_face.x > 0.0 && point_face.y > 0.0)
    {
        tc.x = (tc.x - point_face.x) / radius_face;
        tc.y = (tc.y - point_face.y) / radius_face;

        tc = Distort(tc);

        tc.x = (tc.x * radius_face) + point_face.x;
        tc.y = (tc.y * radius_face) + point_face.y;
        bgColor = vec4(0.7, 0.5, 1.0, 1.0);
        c = texture2D(camTexture, tc / texSize);
    }
    else
    {
        bgColor = vec4(1.0, 1.0, 1.0, 1.0);
        c = texture2D(camTexture, p);
    }
    return c;
}

void main ()
{

  vec2 uv;

  if (v_CamTexCoordinate.x<(stretch_x_point))
  {
      gl_FragColor = faceResize(v_CamTexCoordinate.xy);
  }
  else if (v_CamTexCoordinate.x>=(stretch_x_point))
  {
        vec4 bgColor;
        bgColor = vec4(1.0, 1.0, 1.0, 1.0);
        gl_FragColor = texture2D(camTexture, stretch(v_CamTexCoordinateTwo)) * bgColor;
  }
}
