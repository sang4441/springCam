#extension GL_OES_EGL_image_external : require

//necessary
precision mediump float;
uniform samplerExternalOES camTexture;

varying vec2 v_CamTexCoordinate;
varying vec2 v_CamTexCoordinateTwo;
varying vec2 v_TexCoordinate;


uniform float width;
uniform float height;

uniform float stretch_x_strenth;
uniform float stretch_x_point;

uniform float shoulder_stretch_strength;
uniform vec2 point_shoulder_left;
uniform vec2 point_shoulder_right;
uniform vec2 point_chest_left;
uniform vec2 point_chest_right;
uniform vec2 point_face;

uniform float radius_shoulder;
uniform float radius_chest;
uniform float radius_face;

uniform float strength_shoulder;
uniform float strength_chest;
uniform float strength_face;

const float radius = 100.0;
//const float angle = 0.2;
//const float center_x = 100.0;


//const vec2 center = vec2(150.0, 150.0);
//const float PI = 3.1415926535;

const float PI = 3.1415926535;
#define EPSILON 0.000011



//vec4 PostFX(vec2 uv)
//{
//  vec2 texSize = vec2(1600, 900);
//  vec2 tc = uv * texSize;
//  tc -= center;
//  float dist = length(tc);
//  if (dist < radius)
//  {
//    float percent = ((radius - dist) / radius);
//    float theta = percent * percent * angle * 8.0;
//    float s = sin(theta);
//    float c = cos(theta);
//  }
//  tc += center;
//  vec3 color = texture2D(camTexture, tc / texSize).rgb;
//  return vec4(color, 1.0);
//}

//vec4 Distort(vec2 p)
//{
////  vec2 texSize = vec2(1600, 900);
////  vec2 tc = p * texSize;
//////   tc -= center;
////    float dist = length(tc - center);
////    if (dist < radius)
////    {
////        float percent = 1.0 - ((radius - dist) / radius);
////        tc.x = tc.x + (100.0 * percent);
////        tc.y = tc.y + (100.0 * percent);
////    }
////    vec3 color = texture2D(camTexture, tc / texSize).rgb;
////    return vec4(color, 1.0);
//}

vec4 bodyShape(vec2 p) {
    float aperture = 178.0;
    float apertureHalf = 0.5 * aperture * (PI / 180.0);
    float maxFactor = sin(apertureHalf);

    vec2 texSize = vec2(height, width);
    vec2 tc = p * texSize;
    float distShoulderLeft = length(tc - point_shoulder_left);
    float distShoulderRight = length(tc - point_shoulder_right);
    float distFace = length(tc - point_face);
    float distChestLeft = length(tc - point_chest_left);
    float distChestRight = length(tc - point_chest_right);

    vec4 bgColor;


//    if (distShoulderLeft < radius_shoulder)
//    {
//        float percent = ((radius_shoulder - distShoulderLeft) / radius_shoulder);
////        tc.x = tc.x + (strength_shoulder * percent);
//        tc.y = tc.y + (strength_shoulder * percent);
//        bgColor = vec4(1.0, 1.0, 1.0, 1.0);
//    }
//    else if (distShoulderRight < radius_shoulder)
//    {
//        float percent = ((radius_shoulder - distShoulderRight) / radius_shoulder);
////        tc.x = tc.x + (-strength_shoulder * percent);
//        tc.y = tc.y + (-strength_shoulder * percent);
//        bgColor = vec4(1.0, 1.0, 1.0, 1.0);
//    }
//
    if (distFace < radius_face)
    {

//    float distance_to_center_x = tc.x -

    vec2 xy = 2.0 * p.xy - 1.0;
    float center_v_x = point_face.x / height;
    float center_v_y = point_face.y / width;
    float center_v_x_resize = 2.0 * center_v_x - 1.0;
    float center_v_y_resize = 2.0 * center_v_y - 1.0;

    xy.x = xy.x + center_v_x_resize;
    xy.y = xy.y + center_v_y_resize;


//    float center_v_x = (xy.x - point_face.x / height);
//    float center_v_y = (xy.y - point_face.y / width);

//    float theta  = atan(distance_y, distance_x);
//    float radius = length(xy);

//    vec2 distFaceSmall = vec2(distance_y, distance_x);


    float theta  = atan(center_v_y_resize, center_v_x_resize);
    float radius = length(vec2(center_v_y_resize, center_v_x_resize));

    radius = pow(radius, 1.1);

    xy.x = radius * cos(theta);
    xy.y = radius * sin(theta);

    xy = 0.5 * (xy + 1.0);
    xy.x = xy.x - point_face.x / height;
    xy.y = xy.y - point_face.y / width;
    tc = xy * texSize;


//barrel
//    float radius = length(xy);
//
//    float distance = length()
//
//    if (power > 1.1)
//    {
//        power = 1.1;
//    }
//
//    float power = 1.0;
//
//    radius = pow(radius, power);
//    float addon = pow(radius, power);
//
//    xy.x = xy.x + power * cos(theta);
//    xy.y = xy.y + power * sin(theta);




  //*********************
  //recent
//    float theta  = atan(distFace.y, distFace.x);
//
//    float power = 1.0 + strength_face * (1.0 -  (distFace / radius_face));
//
//    float distance_x = point_face.x - tc.x;
//    float distance_y = point_face.y - tc.y;
//
////    float power_x = 15.0 * (((abs(distance_x) / radius_face)));
////    float power_y = 15.0 * (((abs(distance_y) / radius_face)));
//    float power_x = 15.0 * (1.0 - (distFace / radius_face));
//    float power_y = 15.0 * (1.0 - (distFace / radius_face));
//
//
//    tc.x = tc.x + power_x;
//    tc.y = tc.y + power_x;
//
    //*********************



//             float percent_x = (length(tc.x - point_face.x));
//             float percent_y = (length(tc.y - point_face.y));
//             //x 부터 중앙까지 거리
//
//            float radius_face_half = radius_face / 2.0;
//             if (tc.x > point_face.x)
//             {
////                  float radius_x = sqrt(pow(radius_face, 2.0) - pow(tc.y, 2.0));
//                  //x 부터 표면까지 거리
//                  float dist_to_rad_half = abs(radius_face_half - length(tc.x - point_face.x));
//                  float percent = 1.0 - dist_to_rad_half / radius_face_half;
////                  float percent = (percent_x) / radius_face;
//                  tc.x = tc.x + 20.0 * percent;
//             }
//             else if (tc.x < point_face.x)
//             {
//                  float dist_to_rad_half = abs(radius_face_half - length(tc.x - point_face.x));
//                  float percent = 1.0 - dist_to_rad_half / radius_face_half;
//                  tc.x = tc.x - 20.0 * percent;
//             }
//             if (tc.y > point_face.y)
//             {
//                  float dist_to_rad_half = abs(radius_face_half - length(tc.y - point_face.y));
//                  float percent = 1.0 - dist_to_rad_half / radius_face_half;
//                  tc.y = tc.y + 20.0 * percent;
//             }
//             else if (tc.y < point_face.y)
//             {
//                 float dist_to_rad_half = abs(radius_face_half - length(tc.y - point_face.y));
//                 float percent = 1.0 - dist_to_rad_half / radius_face_half;
//                  tc.y = tc.y - 20.0 * percent;
//             }

             //**************************

//    float percent_x = (length(tc.x - point_chest_left.x));
//    float percent_y = (length(tc.y - point_chest_left.y));
//
////        float percent_x = (length(tc.x - point_chest_left.x)) / (radius_chest);
////        float percent_y = (length(tc.y - point_chest_left.y)) / (radius_chest);
//
//    if (tc.x > point_chest_left.x)
//    {
//        float radius_x = sqrt(pow(radius_chest, 2.0) - pow(tc.y, 2.0));
//        float percent = (radius_x - percent_x) / radius_chest;
//        tc.x = tc.x + 5.0 * percent ;
//    }
//    else if (tc.x < point_chest_left.x)
//    {
//        float radius_x = sqrt(pow(radius_chest, 2.0) - pow(tc.y, 2.0));
//        float percent = (radius_x - percent_x) / radius_chest;
//        tc.x = tc.x - 5.0 * percent;
//    }
//    if (tc.y > point_chest_left.y)
//    {
//        float percent = (sqrt(pow(radius_chest, 2.0) - pow(tc.x, 2.0)) - percent_y) / radius_chest;
//        tc.y = tc.y + 5.0 * percent ;
//    }
//    else if (tc.y < point_chest_left.y)
//    {
//        float percent = (sqrt(pow(radius_chest, 2.0) - pow(tc.x, 2.0)) - percent_y) / radius_chest;
//        tc.y = tc.y - 5.0 * percent ;
//    }

    //*********************



//
//    float d = length(xy * maxFactor);
//    float z = sqrt(1.0 - d * d);
//    float r = atan(d, z) / PI;
//    float phi = atan(xy.y, xy.x);
//
//    xy.x = r * cos(phi) + 0.5;
//    xy.y = r * sin(phi) + 0.5;
//
//


//            float z = sqrt(1.0 - distFace * distFace);
//            float r = atan(distFace, z) / PI;
//            float phi = atan(tc.y, tc.x);
//
//            tc.x = r * cos(phi);
//            tc.y = r * sin(phi);
//        }
//        else
//        {
//            uv = Vertex_UV.xy;
//        }



        bgColor = vec4(1.5, 1.0, 1.0, 1.0);
    }
//    else if (distChestLeft < radius_chest)
//    {
////        float percent_x = (length(tc.x - point_chest_left.x));
////        float percent_y = (length(tc.y - point_chest_left.y));
////
//////        float percent_x = (length(tc.x - point_chest_left.x)) / (radius_chest);
//////        float percent_y = (length(tc.y - point_chest_left.y)) / (radius_chest);
////
////        if (tc.x > point_chest_left.x)
////        {
////            float radius_x = sqrt(pow(radius_chest, 2.0) - pow(tc.y, 2.0));
////            float percent = (radius_x - percent_x) / radius_chest;
////            tc.x = tc.x + 5.0 * percent ;
////        }
////        else if (tc.x < point_chest_left.x)
////        {
////            float radius_x = sqrt(pow(radius_chest, 2.0) - pow(tc.y, 2.0));
////            float percent = (radius_x - percent_x) / radius_chest;
////            tc.x = tc.x - 5.0 * percent;
////        }
////        if (tc.y > point_chest_left.y)
////        {
////            float percent = (sqrt(pow(radius_chest, 2.0) - pow(tc.x, 2.0)) - percent_y) / radius_chest;
////            tc.y = tc.y + 5.0 * percent ;
////        }
////        else if (tc.y < point_chest_left.y)
////        {
////            float percent = (sqrt(pow(radius_chest, 2.0) - pow(tc.x, 2.0)) - percent_y) / radius_chest;
////            tc.y = tc.y - 5.0 * percent ;
////        }
//
//         vec2 xy = 2.0 * p.xy - 1.0;
//
//            float theta  = atan(xy.y, xy.x);
//            float radius = length(xy);
//
//            float power = 1.0 - 0.5 * (1.0 -  (distChestLeft / radius_chest));
//            if (power < 0.9)
//            {
//                power = 0.9;
//            }
//
////            radius = pow(radius, power);
//
//            radius = radius - 0.01;
//
//            xy.x = radius * cos(theta);
//            xy.y = radius * sin(theta);
//            xy = 0.5 * (xy + 1.0);
//
//            tc = xy * texSize;
//
//
//        bgColor = vec4(1.5, 1.0, 1.0, 1.0);
//    }
    else
    {
        bgColor = vec4(1.0, 1.0, 1.0, 1.0);
    }
    vec3 color = texture2D(camTexture, tc / texSize).rgb;
    return vec4(color, 1.0) * bgColor;


//    return texture2D(camTexture, p);
}
vec2 stretch(vec2 pos)
{
   float x = pos.x - stretch_x_strenth;
   return vec2(x, pos.y);
}

vec2 Distort(vec2 p)
{
    float theta  = atan(p.y, p.x);
    float radius = length(p);
    float power = 1.1;
    if (radius >= 0.6 && radius < 1.0)
    {

//        power = power + 0.1 * (radius - 0.7) / 0.3;
         float percent = (1.0 - (radius - 0.6) / 0.4    );
         power =  1.0 + 0.1 * percent;
         radius = radius * power;

//        radius = pow(radius, 0.9);
//        radius = pow(radius, 0.8);
    }
    else if (radius < 0.6)
    {
         radius = radius * power;
//    radius = radius ;
//        radius = pow(radius, 0.8);
//        radius = radius + (pow(0.7, 0.8) - 0.7);
//        radius = pow(0.7, 0.8);
    }

//    else
//    {
//        power = 0.9;
//    }

   ;

//    radius = pow(radius, 0.8);

    p.x = radius * cos(theta);
    p.y = radius * sin(theta);
    return p;
}

vec4 test(vec2 p) {

    vec2 texSize = vec2(height, width);
    vec2 tc = p * texSize;

    vec2 center = point_face / texSize;

    float distFace = length(tc - point_face);

//    vec2 xy = p / 100.0;

    vec4 bgColor;
    if (distFace < radius_face)
    {
        tc.x = (tc.x - point_face.x) / radius_face;
        tc.y = (tc.y - point_face.y) / radius_face;

        tc = Distort(tc);

        tc.x = (tc.x * radius_face) + point_face.x;
        tc.y = (tc.y * radius_face) + point_face.y;
//        bgColor = vec4(0.7, 1.0, 1.0, 1.0);

    }
//    else
//    {
////        bgColor = vec4(1.0, 1.0, 1.0, 1.0);
//    }

     vec4 c = texture2D(camTexture, tc / texSize);

    return c;
}

void main ()
{

//      vec2 xy = (2.0 * v_CamTexCoordinate.xy - 1.0) * 2.0;
      vec2 uv;
//      float d = length(v_CamTexCoordinate.xy);
//      if (d < 1.0)
//      {
//        vec2 result = Distort(xy);
//        result.x = result.x;
//        result.y = result.y;
//        uv = result;
//      }
//      else
//      {
//        uv = v_CamTexCoordinate.xy;
//      }

//      uv = test(v_CamTexCoordinate.xy);
//      vec4 c = texture2D(camTexture, uv);
//      gl_FragColor = c;

  if (v_CamTexCoordinate.x<(stretch_x_point))
  {
//    vec4 c = bodyShape(v_CamTexCoordinate.xy);
//    gl_FragColor = c;

//        uv = test(v_CamTexCoordinate.xy);
//      vec4 c = texture2D(camTexture, uv);
      gl_FragColor = test(v_CamTexCoordinate.xy);
  }
  else if (v_CamTexCoordinate.x>=(stretch_x_point))
  {
        vec4 bgColor;
        bgColor = vec4(0.5, 1.0, 1.0, 1.0);
        gl_FragColor = texture2D(camTexture, stretch(v_CamTexCoordinateTwo)) * bgColor;
  }
}
